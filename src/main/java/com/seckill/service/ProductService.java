package com.seckill.service;

import com.seckill.dataobject.ProductDO;
import com.seckill.error.BusinessException;
import com.seckill.service.model.ProductModel;

import java.util.List;

public interface ProductService {

    //创建商品
    ProductModel createProduct(ProductModel productModel) throws BusinessException;

    //商品列表浏览
    List<ProductModel> listProduct();

    //商品详情浏览
    ProductModel getProductById(Integer id);

    //落单减库存
    boolean decreaseStock(Integer productId,Integer amount) throws BusinessException;

    //库存回补
    boolean increaseStock(Integer productId,Integer amount) throws BusinessException;

    //异步减库存
    boolean asyncDecreaseStock(Integer productId,Integer amount) throws BusinessException;

    //用户销量增加
    void increaseSales(Integer productId,Integer amount) throws BusinessException;

    //初始化库存流水
    String initStockLog(Integer productId,Integer amount);



}
