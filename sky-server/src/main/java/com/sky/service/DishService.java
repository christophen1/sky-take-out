package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

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

    /**
     * @description: 批量删除菜品
     * @param:  * @param ids
     * @return: void
     */
    void delete(List<Long> ids);

    /**
     * @description: 根据id查询菜品和对应的口味数据
     * @param:  * @param id
     * @return: com.sky.vo.DishVO
     */
    DishVO getByIdWithFlavor(Long id);

    /**
     * @description: 根据分类id查询菜品
     * @param:  * @param categoryId
     * @return: java.util.List<com.sky.entity.Dish>
     */
    List<Dish> listByCategoryId(Long categoryId);


    /**
     * @description: 修改菜品
     * @param:  * @param dishDTO 参考{@link DishDTO}
     * @return: void
     */
    void startOrStop(Integer status, Long id);

    /**
     * @description: 修改菜品
     * @param:  * @param dishDTO 参考{@link DishDTO}
     * @return: void
     */
    void updateWithFlavors(DishDTO dishDTO);
}
