package com.o2o.dao;

//*商品分类dao

import com.o2o.entity.ProductCategory;
import com.o2o.entity.Shop;
import com.o2o.entity.ShopCategory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductCategoryDao {

    /**
     * 通过shopId获取店铺下的所有商品分类
     * @param shopId 店铺Id
     * @return list
     */
    List<ProductCategory> queryProductCategory(Long shopId);

    /**
     * 批量添加商品类别
     * @param productCategoryList
     * @return  影响的行数
     */
    int batchInsertProductCategoryList(List<ProductCategory> productCategoryList);

    /**
     * 删除商品类别
     * @param productCategoryId
     * @param shopId
     * @return effectedNum
     */
    int deleteProductCategory(@Param("productCategoryId")long productCategoryId,@Param("shopId") long shopId);
}
