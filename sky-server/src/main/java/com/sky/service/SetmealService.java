package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    /**
     * 新增套餐 + 菜品关系
     */
    void saveWithDish(SetmealDTO setmealDTO);

    /**
     * 分页查询
     */
    PageResult pageQuery(SetmealPageQueryDTO dto);

    /**
     * 删除套餐
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据id查询套餐
     */
    SetmealVO getByIdWithDish(Long id);

    /**
     * 修改套餐
     */
    void update(SetmealDTO setmealDTO);

    /**
     * 起售停售
     */
    void startOrStop(Integer status,Long id);
}