package com.sky.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.DishService;
import com.sky.service.SetmealService;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {

        //判断当前商品是否在购物车中
        //拿用户id
        Long userId = BaseContext.getCurrentId();
        List<ShoppingCart> shoppingCarts = lambdaQuery()
                .eq(shoppingCartDTO.getDishId() != null, ShoppingCart::getDishId, shoppingCartDTO.getDishId())
                .eq(shoppingCartDTO.getSetmealId() != null, ShoppingCart::getSetmealId, shoppingCartDTO.getSetmealId())
                .eq(ShoppingCart::getUserId, userId)
                .list();

        if(shoppingCarts != null && !shoppingCarts.isEmpty()){
            // 加一个判断口味是否一样，如果不一样就新增一个
            for(ShoppingCart cart : shoppingCarts){
                //口味双判空和一样都成立
                if(cart.getDishId() != null  && Objects.equals(cart.getDishFlavor(), shoppingCartDTO.getDishFlavor())){
                    //一样就加一
                    cart.setNumber(cart.getNumber()+1);
                    updateById(cart);
                    return;
                }
                if(cart.getSetmealId() != null ){
                    cart.setNumber(cart.getNumber()+1);
                    updateById(cart);
                    return;
                }

            }
        }
        //TODO这里发现一个设计缺陷，一个菜不同口味如大份小份价格应该是不一样的，但是现在都是一样的
        //没有口味一样的或不在，添加新的
        ShoppingCart cart = ShoppingCart.builder()
                .userId(userId)
                .dishId(shoppingCartDTO.getDishId())
                .setmealId(shoppingCartDTO.getSetmealId())
                .dishFlavor(shoppingCartDTO.getDishFlavor())
                .number(1)
                .build();
        //判断是菜品还是套餐
        if(shoppingCartDTO.getDishId() != null){
            //插入图片,金额，名称，id不用加
            Dish dish = dishService.getById(cart.getDishId());
            cart.setImage(dish.getImage());
            cart.setAmount(dish.getPrice());
            cart.setName(dish.getName());
        }else{
            Setmeal setmeal = setmealService.getById(cart.getSetmealId());
            cart.setImage(setmeal.getImage());
            cart.setAmount(setmeal.getPrice());
            cart.setName(setmeal.getName());
        }
        save(cart);

    }

    /**
     * 获取当前用户的购物车列表
     * @return
     */
    @Override
    public List<ShoppingCart> listByUserId() {
        Long userId = BaseContext.getCurrentId();
        return lambdaQuery()
                .eq(ShoppingCart::getUserId, userId)
                .list();
    }

    /**
     * 清空购物车
     */
    @Override
    public void clean() {
        Long userId = BaseContext.getCurrentId();
        lambdaUpdate()
                .eq(ShoppingCart::getUserId, userId)
                .remove();
    }

    /**
     * 减购物车
     * @param shoppingCartDTO
     */
    @Override
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        Long userId = BaseContext.getCurrentId();
        List<ShoppingCart> shoppingCarts = lambdaQuery()
                .eq(shoppingCartDTO.getDishId() != null, ShoppingCart::getDishId, shoppingCartDTO.getDishId())
                .eq(shoppingCartDTO.getSetmealId() != null, ShoppingCart::getSetmealId, shoppingCartDTO.getSetmealId())
                .eq(ShoppingCart::getUserId, userId)
                .list();
        for(ShoppingCart cart : shoppingCarts){
            //口味判空和一样都成立
            if(cart.getDishId() != null  && Objects.equals(cart.getDishFlavor(), shoppingCartDTO.getDishFlavor())){
                //一样就减一
                if(cart.getNumber() == 1){
                    removeById(cart);
                }else{
                    cart.setNumber(cart.getNumber()-1);
                    updateById(cart);
                }
                return;
            }
            if(cart.getSetmealId() != null ){
                if(cart.getNumber() == 1) {
                    removeById(cart);
                }else {
                    cart.setNumber(cart.getNumber()-1);
                    updateById(cart);
                }
            }
        }
    }

}
