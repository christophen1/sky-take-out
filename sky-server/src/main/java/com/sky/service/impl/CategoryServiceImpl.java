package com.sky.service.impl;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.mapper.CategoryMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import com.sky.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * @description: 新增分类
     * @param:  * @param categoryDTO 参考{@link CategoryDTO}
     * @return: void
     */
    @Override
    public void addCategory(CategoryDTO categoryDTO) {
        Category category = Category.builder()
                .type(categoryDTO.getType())
                .name(categoryDTO.getName())
                .sort(categoryDTO.getSort())
                .id(categoryDTO.getId())
                .build();
        category.setStatus(StatusConstant.ENABLE);
//        category.setCreateTime(LocalDateTime.now());
//        category.setUpdateTime(LocalDateTime.now());
//        category.setCreateUser(BaseContext.getCurrentId());
//        category.setUpdateUser(BaseContext.getCurrentId());
        save(category);
    }

    /**
     * @description: 修改分类
     * @param:  * @param categoryDTO 参考{@link CategoryDTO}
     * @return: void
     */
    @Override
    public void updateCategory(CategoryDTO categoryDTO) {
        Category category = Category.builder()
                .type(categoryDTO.getType())
                .name(categoryDTO.getName())
                .sort(categoryDTO.getSort())
                .id(categoryDTO.getId())
                .build();
//        category.setUpdateTime(LocalDateTime.now());
//        category.setUpdateUser(BaseContext.getCurrentId());
        updateById(category);
    }


    /**
     * @description: 分类分页查询
     * @param:  * @param categoryPageQueryDTO 参考{@link CategoryPageQueryDTO}
     * @return: com.sky.result.Result<com.sky.result.PageResult>
     */
    @Override
    public PageResult<Category> page(CategoryPageQueryDTO categoryPageQueryDTO) {
        //分页条件
        Page< Category> page = new Page<>(categoryPageQueryDTO.getPage(),categoryPageQueryDTO.getPageSize());
        //排序条件
        page.addOrder(OrderItem.asc("sort"));
        page.addOrder(new OrderItem(categoryPageQueryDTO.getSortBy(), categoryPageQueryDTO.getIsAsc()));
        //根据类型查询
        lambdaQuery()
                .eq(categoryPageQueryDTO.getType() != null,Category::getType,categoryPageQueryDTO.getType())
                .like(StringUtils.isNotBlank(categoryPageQueryDTO.getName()),Category::getName,categoryPageQueryDTO.getName())
                .page(page);
        return new PageResult<>(page.getTotal(),page.getRecords());

    }

    /**
     * @description: 启用或停售分类
     * @param:  * @param status 参考{@link Integer}
     * @param id 参考{@link Long}
     * @return: void
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        Category category = Category.builder()
                .id(id)
                .status(status)
                .build();
        updateById(category);
    }

    /**
     * @description: 根据类型查询分类
     * @param:  * @param type 参考{@link Integer}
     * @return: com.sky.result.Result<com.sky.entity.Category>
     */
    @Override
    public List<Category> listByType(Integer type) {

        List<Category> list = lambdaQuery().eq(type != null,Category::getType,type).list();
        return list;
    }


}
