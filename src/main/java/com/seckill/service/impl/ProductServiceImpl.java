package com.seckill.service.impl;

import com.seckill.dao.ProductDOMapper;
import com.seckill.dao.StockDOMapper;
import com.seckill.dao.StockLogDOMapper;
import com.seckill.dataobject.ProductDO;
import com.seckill.dataobject.StockDO;
import com.seckill.dataobject.StockLogDO;
import com.seckill.error.BusinessException;
import com.seckill.error.EmBusinessError;
import com.seckill.mq.MqProducer;
import com.seckill.service.ProductService;
import com.seckill.service.model.ProductModel;
import com.seckill.validator.ValidationResult;
import com.seckill.validator.ValidatorImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.websocket.SendResult;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ValidatorImpl validator;

    @Autowired
    private ProductDOMapper productDOMapper;  //BigDecimal

    @Autowired
    private StockDOMapper stockDOMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MqProducer mqProducer;

    @Autowired
    private StockLogDOMapper stockLogDOMapper;

    @Override
    @Transactional
    public ProductModel createProduct(ProductModel productModel) throws BusinessException {
        //校验入参
//        ValidationResult result = validator.validate(productModel);
//        if (result.isHasErrors()){
//            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,result.getErrMsg());
//        }

        //model->dataobject
        ProductDO productDO = this.convertProductDoFromProductModel(productModel);

        //写入数据库
        productDOMapper.insertSelective(productDO);
        productModel.setId(productDO.getId());
        StockDO stockDO = this.convertStockDOFromProductModel(productModel);
        stockDOMapper.insertSelective(stockDO);

        //返回创建完成对象

        return this.getProductById(productModel.getId());
    }

    private StockDO convertStockDOFromProductModel(ProductModel productModel){
        if (productModel == null){
            return null;
        }
        StockDO stockDO = new StockDO();
        stockDO.setStock(productModel.getStock());
        stockDO.setProductId(productModel.getId());
        return stockDO;
    }

    private ProductDO convertProductDoFromProductModel(ProductModel productModel){
        if (productModel == null){
            return null;
        }
        ProductDO productDO = new ProductDO();
        BeanUtils.copyProperties(productModel,productDO);
        return productDO;
    }

    @Override
    public List<ProductModel> listProduct() {
        List<ProductDO> list = productDOMapper.listProduct();
        List<ProductModel> productModelList = list.stream().map(productDO -> {
            StockDO stockDO = stockDOMapper.selectByProductId(productDO.getId());
            ProductModel productModel = this.convertModelFromDataObject(productDO,stockDO);
            return productModel;
        }).collect(Collectors.toList());
        return productModelList;
    }

    @Override
    public ProductModel getProductById(Integer id) {
        ProductDO productDO = productDOMapper.selectByPrimaryKey(id);
        if (productDO == null){
            return null;
        }
        //获得库存数量
        StockDO stockDO = stockDOMapper.selectByProductId(productDO.getId());

        //DO-->Model
        ProductModel productModel = this.convertModelFromDataObject(productDO,stockDO);
        return productModel;

    }

    @Override
    @Transactional
    public boolean decreaseStock(Integer productId, Integer amount) throws BusinessException {
        //int affectedRow = stockDOMapper.decreaseStock(productId,amount);

        long result = redisTemplate.opsForValue().increment("seckill_product_stock_"+productId,amount.intValue() * -1);
        if (result>0){
            return true;
        }else if (result == 0){
            //打上库存售罄标识
            redisTemplate.opsForValue().set("seckill_product_stock_invalid_"+productId,"true");
            return true;
        }else {
            increaseStock(productId,amount);
            return false;
        }
    }

    @Override
    public boolean increaseStock(Integer productId, Integer amount) throws BusinessException {
        redisTemplate.opsForValue().increment("seckill_product_stock_"+productId,amount.intValue());
        return true;
    }

    @Override
    public boolean asyncDecreaseStock(Integer productId, Integer amount) throws BusinessException {
        Boolean mqResult = mqProducer.asyncReduceStock(productId,amount);
        return mqResult;
    }

    @Override
    @Transactional
    public void increaseSales(Integer productId, Integer amount) throws BusinessException {
        productDOMapper.increaseSales(productId,amount);
    }

    @Override
    @Transactional
    public String initStockLog(Integer productId, Integer amount) {
        StockLogDO stockLogDO = new StockLogDO();
        stockLogDO.setProductId(productId);
        stockLogDO.setAmount(amount);
        stockLogDO.setId(UUID.randomUUID().toString().replace("-",""));
        stockLogDO.setStatus(1);
        stockLogDOMapper.insertSelective(stockLogDO);
        return stockLogDO.getId();
    }

    private ProductModel convertModelFromDataObject(ProductDO productDO,StockDO stockDO){
        ProductModel productModel = new ProductModel();
        BeanUtils.copyProperties(productDO,productModel);
        productModel.setStock(stockDO.getStock());
        return productModel;
    }
}
