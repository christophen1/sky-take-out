package com.sky.service.impl;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.*;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.DishFlavorService;
import com.sky.service.DishService;
import com.sky.service.SetmealDishService;
import com.sky.service.SetmealService;
import com.sky.utils.RedisUtils;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private DishService dishService;


    @Override
    @Transactional
    @CacheEvict(value = "dishCache",key = "#dishDTO.categoryId")
    public void saveWithFlavors(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        if (dish.getStatus()==null){
            dish.setStatus(StatusConstant.ENABLE);
        }
        save(dish);
        //save自动包装dish对象
        Long dishId = dish.getId();;
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()){
            flavors.forEach(flavor -> flavor.setDishId(dishId));
            dishFlavorService.saveBatch(flavors);
        }
    }

    /**
     * @description: 菜品扉页查询
     * @param:  * @param dishPageQueryDTO 参考{@link DishPageQueryDTO}
     * @return: com.sky.result.PageResult
     */
    @Override
    public PageResult<DishVO> page(DishPageQueryDTO dishPageQueryDTO) {

        Page<DishVO> page = new Page<>(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        page = dishMapper.pageQuery(page,dishPageQueryDTO);
        return new PageResult<>(page.getTotal(),page.getRecords());

    }

    /**
     * @description: 批量删除菜品
     * @param:  * @param ids 参考{@link List< Long>}
     * @return: void
     */
    @Override
    @Transactional
    @CacheEvict(value = "dishCache",allEntries = true)
    public void delete(List<Long> ids) {
        //业务规则：
        //可以一次删除一个菜品，也可以批量删除菜品

        for (Long id : ids) {
            Dish dish = getById(id);
            //起售中的菜品不能删除
            if(Objects.equals(dish.getStatus(), StatusConstant.ENABLE)) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //被套餐关联的菜品不能删除
        boolean exists = setmealDishService.lambdaQuery().in(SetmealDish::getDishId, ids).exists();
        if(exists) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //删除菜品
        removeBatchByIds(ids);
        //删除菜品后，关联的口味数据也需要删除掉
        dishFlavorService.lambdaUpdate().in(DishFlavor::getDishId,ids).remove();
    }

    /**
     * @description: 根据id查询菜品和对应的口味数据
     * @param:  * @param id
     * @return: com.sky.vo.DishVO
     */
    @Override
    public DishVO getByIdWithFlavor(Long id) {
        Dish dish = getById(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavorService.lambdaQuery().eq(DishFlavor::getDishId,dish.getId()).list());
        return dishVO;
    }

    /**
     * @description: 根据分类id查询菜品
     * @param:  * @param categoryId
     * @return: java.util.List<com.sky.entity.Dish>
     */
    @Override
    public List<Dish> listByCategoryId(Long categoryId) {
        return lambdaQuery().in(Dish::getCategoryId,categoryId).list();
    }


    /**
     * @description: 起售或停售菜品
     * @param:  * @param status 参考{@link Integer}
     * @param id 参考{@link Long}
     * @return: void
     */
    @Override
    @CacheEvict(value = "dishCache", allEntries = true)
    public void startOrStop(Integer status, Long id) {
        lambdaUpdate().eq(Dish::getId,id).set(Dish::getStatus,status).update();

    }

    /**
     * @description: 更新菜品
     * @param:  * @param dishDTO 参考{@link DishDTO}
     * @return: void
     */
    @Override
    @CacheEvict(value = "dishCache",allEntries = true)
    public void updateWithFlavors(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        updateById(dish);
        //先删再插
        dishFlavorService.lambdaUpdate().eq(DishFlavor::getDishId,dish.getId()).remove();
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()){
            flavors.forEach(flavor -> flavor.setDishId(dish.getId()));
            dishFlavorService.saveBatch(flavors);
        }
    }

    @Autowired
    private RedisUtils redisUtils;

    /**
     * @description: 根据分类id查询菜品
     * @param:  * @param categoryId
     * @return: java.util.List<com.sky.vo.DishVO>
     */
    @Override
    @Cacheable(cacheNames = "dishCache",key = "#categoryId")
    public List<DishVO> listByCategoryIdToVO(Long categoryId) {
//        String key = "dish:" + categoryId;
//        if(redisUtils.hasKey(key)){
//            redisUtils.expire(key,3L, TimeUnit.MINUTES);
//            return redisUtils.get(key,List.class);
//        }
        List<Dish> dishList = dishService.lambdaQuery()
                .eq(categoryId != null,Dish::getCategoryId, categoryId)
                .eq(Dish::getStatus, StatusConstant.ENABLE)
                .orderByDesc(Dish::getUpdateTime)
                .list();
        if (dishList.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> ids = dishList.stream()
                .map(Dish::getId)
                .collect(Collectors.toList());

        List<DishFlavor> flavorList = dishFlavorService.lambdaQuery()
                .in(DishFlavor::getDishId,ids).list();

// 按 dishId 分组
        Map<Long, List<DishFlavor>> map =
                flavorList.stream().collect(Collectors.groupingBy(DishFlavor::getDishId));

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish dish : dishList) {
            DishVO vo = new DishVO();
            BeanUtils.copyProperties(dish, vo);

            vo.setFlavors(map.getOrDefault(dish.getId(), new ArrayList<>()));

            dishVOList.add(vo);
        }
//        redisUtils.set(key,dishVOList,3L,TimeUnit.MINUTES);
        return dishVOList;
    }

}
