package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;

public interface DishService extends IService<Dish> {
    /**
     * @description: 新增菜品
     * @param:  * @param dishDTO 参考{@link DishDTO}
     * @return: void
     */
    void saveWithFlavors(DishDTO dishDTO);
    /**
     * @description: 菜品分页查询
     * @param:  * @param dishPageQueryDTO 参考{@link DishPageQueryDTO}
     * @return: com.sky.result.PageResult<com.sky.entity.Dish>
     */
    PageResult page(DishPageQueryDTO dishPageQueryDTO);
}
