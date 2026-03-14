package com.sky.service.impl;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Employee;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.DishFlavorService;
import com.sky.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;


    @Override
    @Transactional
    public void saveWithFlavors(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        if (dish.getStatus()==null){
            dish.setStatus(StatusConstant.ENABLE);
        }
        save(dish);

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
    public PageResult<Dish> page(DishPageQueryDTO dishPageQueryDTO) {
        Page<Dish> page = Page.of(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        page.addOrder(new OrderItem(dishPageQueryDTO.getSortBy(), dishPageQueryDTO.getIsAsc()));
        lambdaQuery().eq(dishPageQueryDTO.getCategoryId() != null, Dish::getCategoryId,dishPageQueryDTO.getCategoryId())
                .like(dishPageQueryDTO.getName() != null, Dish::getName,dishPageQueryDTO.getName())
                .eq(dishPageQueryDTO.getStatus() != null, Dish::getStatus,dishPageQueryDTO.getStatus())
                .page(page);
        return new PageResult<>(page.getTotal(),page.getRecords());
//        //分页条件
//        Page<Category> page = new Page<>(categoryPageQueryDTO.getPage(),categoryPageQueryDTO.getPageSize());
//        //排序条件
//        page.addOrder(OrderItem.asc("sort"));
//        page.addOrder(new OrderItem(categoryPageQueryDTO.getSortBy(), categoryPageQueryDTO.getIsAsc()));
//        //根据类型查询
//        lambdaQuery()
//                .eq(categoryPageQueryDTO.getType() != null,Category::getType,categoryPageQueryDTO.getType())
//                .like(StringUtils.isNotBlank(categoryPageQueryDTO.getName()),Category::getName,categoryPageQueryDTO.getName())
//                .page(page);
//        return new PageResult<>(page.getTotal(),page.getRecords());
    }
}
