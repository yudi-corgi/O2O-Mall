package com.o2o.service;

import com.o2o.dto.ImageHolder;
import com.o2o.dto.ProductExecution;
import com.o2o.entity.Product;
import com.o2o.exceptions.ProductOperationException;
import jdk.internal.util.xml.impl.Input;

import java.io.InputStream;
import java.util.List;

public interface ProductService {

    /**
     * 查询商品列表并分页,可输入条件有：商品名(模糊)，商品状态，店铺Id，商品类别
     * @param productCondition
     * @param pageIndex 第几页的数据
     * @param pageSize
     * @return
     */
    ProductExecution getProductList(Product productCondition, int pageIndex, int pageSize);

    /**
     * 添加商品信息和图片处理
     * @param product
     * @param thumbnail 商品缩略图
     * @param productImgList 商品详情图集合
     * @return
     * @throws ProductOperationException
     */
    ProductExecution addProduct(Product product, ImageHolder thumbnail,
                                List<ImageHolder> productImgList) throws ProductOperationException;


    /**
     * 通过商品Id获取商品信息
     * @param productId
     * @return
     */
    Product getPruductById(Long productId) throws ProductOperationException;

    /**
     * 修改商品信息及图片处理
     * @param product
     * @param thumbnail
     * @param i
     * @return
     */
    ProductExecution modifyProduct(Product product,ImageHolder thumbnail,List<ImageHolder> productImageHolder)
        throws ProductOperationException;

}
