package com.o2o.dao;

import com.o2o.BaseTest;
import com.o2o.entity.ProductCategory;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

//测试方法执行顺序按照方法名执行（顺序等同字符串排序）
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProductCategoryDaoTest extends BaseTest {

    @Autowired
    private ProductCategoryDao productCategoryDao;

    @Test
    public void testBQueryProductCategoryById(){
        Long shopId = 1L;
        List<ProductCategory> productCategoryList = productCategoryDao.queryProductCategory(shopId);
        System.out.println("该店铺自定义类别数为:"+productCategoryList.size());
    }

    @Test
    public void testABatchInsertProductCategory(){
        ProductCategory productCategory = new ProductCategory();
        productCategory.setProductCategoryName("商品类别1");
        productCategory.setCreateTime(new Date());
        productCategory.setPriority(1);
        productCategory.setShopId(1L);
        ProductCategory productCategory1 = new ProductCategory();
        productCategory1.setProductCategoryName("商品类别2");
        productCategory1.setCreateTime(new Date());
        productCategory1.setPriority(2);
        productCategory1.setShopId(1L);
        List<ProductCategory> productCategoryList = new ArrayList<ProductCategory>();
        productCategoryList.add(productCategory);
        productCategoryList.add(productCategory1);
        int effectedNum = productCategoryDao.batchInsertProductCategoryList(productCategoryList);
        assertEquals(2,effectedNum);
    }

    @Test
    public void testCDeleteProductCategory() throws Exception{
        long shopId= 1L;
        List<ProductCategory> productCategoryList = productCategoryDao.queryProductCategory(shopId);
        for (ProductCategory pc:productCategoryList) {
            if("商品类别1".equals(pc.getProductCategoryName()) || "商品类别2".equals(pc.getProductCategoryName())){
                int effectedNum = productCategoryDao.deleteProductCategory(pc.getProductCategoryId(),pc.getShopId());
                assertEquals(1,effectedNum);
            }

        }

    }
}
