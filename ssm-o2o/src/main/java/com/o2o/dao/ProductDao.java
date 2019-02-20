package com.o2o.dao;

import com.o2o.entity.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductDao {

    /**
     * 插入商品
     * @param product
     * @return
     */
    int insertProduct(Product product);

    /**
     * 查询商品列表并分页,可输入条件有：商品名(模糊)，商品状态，店铺Id，商品类别
     * @param productCondition 查询条件
     * @param rowIndex 查询起始行数
     * @param pageSize 每页显示几条记录
     * @return
     */
    List<Product> queryProductList(@Param("productCondition")Product productCondition,
                                   @Param("rowIndex")int rowIndex,@Param("pageSize")int pageSize);

    /**
     * 查询对应商品的总数
     * @param productCondition
     * @return
     */
    int queryProductCount(@Param("productCondition")Product productCondition);

    /**
     * 通过商品Id获取商品信息（包括详情图片）
     * @param productId
     * @return
     */
    Product queryProductById(long productId);

    /**
     * 更新商品信息(编辑商品时调用)
     * @param product
     * @return
     */
    int updateProduct(Product product);

    /**
     * 删除商品类别时将商品的类别id置为空
     * @param productCategoryId
     * @return
     */
    int updateProductCategoryIdToNull(long productCategoryId);

    /**
     * 删除商品
     * @param productId
     * @param shopId
     * @return
     */
    int deleteProduct(@Param("productId") long productId,
                      @Param("shopId") long shopId);
}
