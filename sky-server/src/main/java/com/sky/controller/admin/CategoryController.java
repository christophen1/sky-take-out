package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    /**
     * @description: 新增分类
     * @param:  * @param categoryDTO 参考{@link CategoryDTO}
     * @return: com.sky.result.Result
     */
    @PostMapping
    @ApiOperation("新增分类")
    public Result addCategory(@RequestBody CategoryDTO categoryDTO){
        log.info("新增分类：{}",categoryDTO);
        categoryService.addCategory(categoryDTO);
        return Result.success();
    }
    /**
     * @description: 修改分类
     * @param:  * @param categoryDTO 参考{@link CategoryDTO}
     * @return: com.sky.result.Result
     */
    @PutMapping
    @ApiOperation("修改分类")
    public Result updateCategory(@RequestBody CategoryDTO categoryDTO){
        log.info("修改分类：{}",categoryDTO);
        categoryService.updateCategory(categoryDTO);
        return Result.success();
    }
    /**
     * @description: 分类分页查询
     * @param:  * @param categoryPageQueryDTO 参考{@link CategoryPageQueryDTO}
     * @return: com.sky.result.Result<com.sky.result.PageResult>
     */
    @GetMapping("/page")
    @ApiOperation("分类分页查询")
    public Result<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO){
        log.info("分页查询：{}",categoryPageQueryDTO);
        PageResult<Category> pageResult = categoryService.page(categoryPageQueryDTO);
        return Result.success(pageResult);
    }
    /**
     * @description: 起售、停售分类
     * @param:  * @param status 参考{@link Integer}
     * @param id 参考{@link Long}
     * @return: com.sky.result.Result
     */
    @PostMapping("/status/{status}")
    @ApiOperation("起售、停售分类")
    public Result startOrStop(@PathVariable Integer status,Long id){
        log.info("起售、停售分类：{}",id);
        categoryService.startOrStop(status,id);
        return Result.success();
    }

    /**
     * @description: 删除分类
     * @param:  * @param id 参考{@link Long}
     * @return: com.sky.result.Result
     */
    @DeleteMapping
    @ApiOperation("根据id删除分类")
    public Result deleteCategory(Long id){
        log.info("删除分类：{}",id);
        categoryService.removeById(id);
        return Result.success();
    }

    /**
     * @description: 根据类型查询分类
     * @param:  * @param type 参考{@link Integer}
     * @return: com.sky.result.Result<com.sky.dto.CategoryDTO>
     */
    @GetMapping("/list")
    @ApiOperation("根据类型查询分类")
    public Result<List<Category>> list(Integer type){
        log.info("根据类型查询分类：{}",type);
        List<Category> list= categoryService.listByType(type);
        return Result.success(list);
    }

}
