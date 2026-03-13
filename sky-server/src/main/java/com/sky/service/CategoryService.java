package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import org.springframework.stereotype.Service;

import java.util.List;


public interface CategoryService extends IService<Category> {
    /**
     * @description: 新增分类
     * @param:  * @param categoryDTO 参考{@link CategoryDTO}
     * @return: void
     */
    void addCategory(CategoryDTO categoryDTO);

    /**
     * @description: 修改分类
     * @param:  * @param categoryDTO 参考{@link CategoryDTO}
     * @return: void
     */
    void updateCategory(CategoryDTO categoryDTO);

    /**
     * @description: 分类分页查询
     * @param:  * @param categoryPageQueryDTO 参考{@link CategoryPageQueryDTO}
     * @return: com.sky.result.Result<com.sky.result.PageResult>
     */
    PageResult< Category> page(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * @description: 启售、停售分类
     * @param:  * @param status 参考{@link Integer}
     * @param id 参考{@link Long}
     * @return: void
     */
    void startOrStop(Integer status, Long id);


    /**
     * @description: 根据类型查询分类
     * @param:  * @param type 参考{@link Integer}
     * @return: com.sky.result.Result<com.sky.dto.CategoryDTO>
     */
    List<Category> listByType(Integer type);
}
