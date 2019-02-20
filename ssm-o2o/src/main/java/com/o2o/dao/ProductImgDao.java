package com.o2o.dao;

import com.o2o.entity.ProductImg;

import java.util.List;

public interface ProductImgDao {


    /**
     * 添加商品时批量插入详情图
     * @param productImgList
     * @return
     */
    int batchInsertProductImg(List<ProductImg> productImgList);

    /**
     * 删除指定商品下的所有详情图
     * @param productId
     * @return
     */
    int deleteProductImgByProductId(long productId);

    /**
     * 通过商品Id获取商品详情图列表
     * @param productId
     * @return
     */
    List<ProductImg> queryProductImgList(long productId);
}
