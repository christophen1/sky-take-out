package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.service.SetmealDishService;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal>
        implements SetmealService {
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private  DishMapper dishMapper;
    @Autowired
    private SetmealDishService setmealDishService;


    /**
     * 新增套餐
     */
    @Transactional
    @Override
    public void saveWithDish(SetmealDTO dto) {

        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(dto,setmeal);

        //1 保存套餐
        this.save(setmeal);

        Long setmealId = setmeal.getId();

        //2 保存套餐菜品关系
        List<SetmealDish> dishes = dto.getSetmealDishes();
        dishes.forEach(item -> item.setSetmealId(setmealId));

        setmealDishService.saveBatch(dishes);

    }

    /**
     * 分页查询
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO dto) {

        Page<Setmeal> page = new Page<>(dto.getPage(),dto.getPageSize());

        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();

        wrapper.like(dto.getName()!=null,Setmeal::getName,dto.getName())
                .eq(dto.getStatus()!=null,Setmeal::getStatus,dto.getStatus())
                .eq(dto.getCategoryId()!=null,Setmeal::getCategoryId,dto.getCategoryId())
                .orderByDesc(Setmeal::getCreateTime);

        this.page(page,wrapper);

        return new PageResult(page.getTotal(),page.getRecords());
    }

    /**
     * 删除套餐
     */
    @Transactional
    @Override
    public void deleteBatch(List<Long> ids) {

        for(Long id : ids){

            Setmeal setmeal = this.getById(id);

            if(Objects.equals(setmeal.getStatus(), StatusConstant.ENABLE)){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }

            //删除套餐
            this.removeById(id);

            //删除关联关系
            LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SetmealDish::getSetmealId,id);

            setmealDishMapper.delete(wrapper);
        }
    }

    /**
     * 根据id查询套餐
     */
    @Override
    public SetmealVO getByIdWithDish(Long id) {

        Setmeal setmeal = this.getById(id);

        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SetmealDish::getSetmealId,id);

        List<SetmealDish> dishes = setmealDishMapper.selectList(wrapper);

        SetmealVO vo = new SetmealVO();
        BeanUtils.copyProperties(setmeal,vo);

        vo.setSetmealDishes(dishes);

        return vo;
    }

    /**
     * 修改套餐
     */
    @Transactional
    @Override
    public void update(SetmealDTO dto) {

        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(dto,setmeal);

        //1 更新套餐
        this.updateById(setmeal);

        Long setmealId = dto.getId();

        //2 删除原关联关系
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SetmealDish::getSetmealId,setmealId);

        setmealDishMapper.delete(wrapper);

        //3 新增关系
        List<SetmealDish> dishes = dto.getSetmealDishes();
        dishes.forEach(item -> item.setSetmealId(setmealId));

        setmealDishService.saveBatch(dishes);
    }

    /**
     * 起售停售
     */
    @Override
    public void startOrStop(Integer status, Long id) {

        if(status == StatusConstant.ENABLE){

            List<Dish> dishList = dishMapper.getBySetmealId(id);

            for(Dish dish : dishList){
                if(dish.getStatus() == StatusConstant.DISABLE){
                    throw new SetmealEnableFailedException(
                            MessageConstant.SETMEAL_ENABLE_FAILED);
                }
            }
        }

        Setmeal setmeal = new Setmeal();
        setmeal.setId(id);
        setmeal.setStatus(status);

        this.updateById(setmeal);
    }
}