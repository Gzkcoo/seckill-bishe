package com.seckill.controller;

import com.seckill.controller.viewobject.ProductVO;
import com.seckill.error.BusinessException;
import com.seckill.error.EmBusinessError;
import com.seckill.response.CommonReturnType;
import com.seckill.service.ProductService;
import com.seckill.service.model.ProductModel;
import com.seckill.validator.ValidationResult;
import com.seckill.validator.ValidatorImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Api("银行产品相关api")
@Controller("product")
@RequestMapping("/product")
@CrossOrigin
public class ProductController extends BaseController{

    @Autowired
    private ProductService productService;

    @Autowired
    private ValidatorImpl validator;

    //创建商品
    @ApiOperation("增加商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productName", value = "产品名称", required = true,
                    dataType = "string"),
            @ApiImplicitParam(name = "deadline", value = "产品期限（天）", required = false,
                    dataType = "int"),
            @ApiImplicitParam(name = "rate", value = "年化利率（%）", required = true,
                    dataType = "Double"),
            @ApiImplicitParam(name = "startAmount", value = "起存金额（元）", required = true,
                    dataType = "Double"),
            @ApiImplicitParam(name = "increAmount", value = "递增金额（元）", required = false,
                    dataType = "Double"),
            @ApiImplicitParam(name = "personLimit", value = "单人限额（元）", required = false,
                    dataType = "Double"),
            @ApiImplicitParam(name = "dayLimit", value = "单日限额（元）", required = false,
                    dataType = "Double"),
            @ApiImplicitParam(name = "risk", value = "风险等级", required = false,
                    dataType = "string"),
            @ApiImplicitParam(name = "valueDate", value = "起息日(精确到天)", required = false,
                    dataType = "Date"),
            @ApiImplicitParam(name = "method", value = "结息方式", required = false,
                    dataType = "string"),
            @ApiImplicitParam(name = "endDate", value = "到期日(精确到天)", required = false,
                    dataType = "Date"),
            @ApiImplicitParam(name = "sales", value = "产品销量", required = false,
                    dataType = "int"),
            @ApiImplicitParam(name = "description", value = "产品描述", required = true,
                    dataType = "string"),
            @ApiImplicitParam(name = "flag", value = "0为普通商品，1为秒杀商品", required = true,
                    dataType = "Byte"),
            @ApiImplicitParam(name = "stock", value = "库存", required = true,
                    dataType = "int")
    })
    @RequestMapping(value = "/create",method = {RequestMethod.POST},consumes={CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType createProduct(@RequestParam(name = "productName")String productName,
                                       @RequestParam(name = "deadline",required = false)Integer deadline,
                                       @RequestParam(name = "rate") Double rate,
                                       @RequestParam(name = "startAmount")Double startAmount,
                                       @RequestParam(name = "increAmount",required = false)Double increAmount,
                                       @RequestParam(name = "personLimit",required = false)Double personLimit,
                                       @RequestParam(name = "dayLimit",required = false)Double dayLimit,
                                       @RequestParam(name = "risk",required = false)String risk,
                                       @RequestParam(name = "valueDate",required = false)Date valueDate,
                                       @RequestParam(name = "method",required = false)String method,
                                       @RequestParam(name = "endDate",required = false) Date endDate,
                                       @RequestParam(name = "sales",required = false)Integer sales,
                                       @RequestParam(name = "description")String description,
                                       @RequestParam(name = "flag")Byte flag,
                                       @RequestParam(name = "stock")int stock) throws BusinessException {
        //封装service请求用来创建商品
        ProductModel productModel = new ProductModel();
        productModel.setProductName(productName);
        productModel.setRate(rate);
        productModel.setStartAmount(startAmount);
        productModel.setDescription(description);
        productModel.setFlag(flag);
        productModel.setStock(stock);
        productModel.setDeadline(deadline);
        productModel.setIncreAmount(increAmount);
        productModel.setPersonLimit(personLimit);
        productModel.setDayLimit(dayLimit);
        productModel.setRisk(risk);
        productModel.setValueDate(valueDate);
        productModel.setMethod(method);
        productModel.setEndDate(endDate);
        productModel.setSales(sales);


        ProductModel productModelForReturn = productService.createProduct(productModel);
        ProductVO productVO = convertVOFromModel(productModelForReturn);

        return CommonReturnType.create(productVO);
    }


    //商品列表页面浏览
    @ApiOperation("获取商品列表")
    @RequestMapping(value = "/list",method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType listItem(){
        List<ProductModel> productModelList = productService.listProduct();

        //使用stream apiJ将list内的itemModel转化为ITEMVO;
        List<ProductVO> productVOList =  productModelList.stream().map(productModel -> {
            ProductVO productVO = this.convertVOFromModel(productModel);
            return productVO;
        }).collect(Collectors.toList());
        return CommonReturnType.create(productVOList);
    }


    //商品详情页浏览
    @ApiOperation("获取商品详情页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "商品id", required = true,
                    dataType = "int")
    })
    @RequestMapping(value = "/get",method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType getProduct(@RequestParam(name = "id")Integer id) {

        ProductModel productModel = productService.getProductById(id);
        ProductVO productVO = this.convertVOFromModel(productModel);
        return CommonReturnType.create(productVO);

    }

    private ProductVO convertVOFromModel(ProductModel productModel){
        if (productModel == null){
            return null;
        }
        ProductVO productVO = new ProductVO();
        BeanUtils.copyProperties(productModel,productVO);
        return productVO;
    }


}
