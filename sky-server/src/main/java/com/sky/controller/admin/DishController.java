package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/admin/dish")
public class DishController {
    @Autowired
     private DishService dishService;
    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @PostMapping
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品：{}",dishDTO);
        dishService.saveWithFlavors(dishDTO);
        return Result.success();
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult<DishVO>> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("分页查询：{}",dishPageQueryDTO);
        PageResult<DishVO> pageResult = dishService.page(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 批量删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids){
        log.info("批量删除：{}",ids);
        dishService.delete(ids);
        return Result.success();
    }

    /**
     * 根据id查询菜品和对应的口味数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("查询菜品：{}",id);
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }


    /**
     * 根据分类id查询菜品
     * @param CategoryId
     * @return
     */
    @GetMapping("/list")
    public Result<List<Dish>> list(Long CategoryId){
        log.info("查询菜品：{}",CategoryId);
        List<Dish> list = dishService.listByCategoryId(CategoryId);
        return Result.success(list);
    }
    @PutMapping
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改菜品：{}",dishDTO);
        dishService.updateWithFlavors(dishDTO);
        return Result.success();
    }
    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable Integer status,Long id){
        log.info("起售、停售菜品：{}",id);
        dishService.startOrStop(status,id);
        return Result.success();
    }

}
