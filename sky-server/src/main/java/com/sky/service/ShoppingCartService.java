package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService extends IService<ShoppingCart> {
    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    void add(ShoppingCartDTO shoppingCartDTO);

    /**
     * 查看用户购物车
     * @return
     */
    List<ShoppingCart> listByUserId();

    /**
     * 清空购物车
     */
    void clean();

    /**
     * 减购物车
     * @param shoppingCartDTO
     */
    void sub(ShoppingCartDTO shoppingCartDTO);
}
