package com.o2o.dao;

import com.o2o.BaseTest;
import com.o2o.entity.Product;
import com.o2o.entity.ProductCategory;
import com.o2o.entity.ProductImg;
import com.o2o.entity.Shop;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProductDaoTest extends BaseTest {

    @Autowired
    private ProductDao productDao;
    @Autowired
    private ProductImgDao productImgDao;

    @Test
    public void testAInsertProduct() throws Exception{
        Shop shop1 = new Shop();
        shop1.setShopId(1L);
        ProductCategory pc1 = new ProductCategory();
        pc1.setProductCategoryId(2L);
        //初始化三个商品实例并添加到shopId为1的店铺
        //同时商品类别ID为1
        Product product1 = new Product();
        product1.setProductName("测试1");
        product1.setProductDesc("测试Desc1");
        product1.setImgAddr("test1");
        product1.setPriority(1);
        product1.setEnableStatus(1);
        product1.setCreateTime(new Date());
        product1.setLastEditTime(new Date());
        product1.setShop(shop1);
        product1.setProductCategory(pc1);
        Product product2 = new Product();
        product2.setProductName("测试2");
        product2.setProductDesc("测试Desc2");
        product2.setImgAddr("test2");
        product2.setPriority(2);
        product2.setEnableStatus(0);
        product2.setCreateTime(new Date());
        product2.setLastEditTime(new Date());
        product2.setShop(shop1);
        product2.setProductCategory(pc1);
        Product product3 = new Product();
        product3.setProductName("测试3");
        product3.setProductDesc("测试Desc3");
        product3.setImgAddr("test3");
        product3.setPriority(3);
        product3.setEnableStatus(1);
        product3.setCreateTime(new Date());
        product3.setLastEditTime(new Date());
        product3.setShop(shop1);
        product3.setProductCategory(pc1);
        //判断是否成功
        int effectedNum = productDao.insertProduct(product1);
        assertEquals(1,effectedNum);
        effectedNum = productDao.insertProduct(product2);
        assertEquals(1,effectedNum);
        effectedNum = productDao.insertProduct(product3);
        assertEquals(1,effectedNum);
    }

    @Test
    public void testBQueryProductList() throws Exception{

        Product productCondition = new Product();
        //分页查询，预期返回三条结果
        List<Product> productList = productDao.queryProductList(productCondition,0,3);
        assertEquals(3,productList.size());
        //查询商品总数
        int count = productDao.queryProductCount(productCondition);
        assertEquals(3,count);

        //使用商品名称模糊查询，预期返回两条结果
        productCondition.setProductName("测试");
        productList = productDao.queryProductList(productCondition,0,3);
        //System.out.println(productList.get(0).getProductName());
       // System.out.println(productList.get(0).getPriority());
        //System.out.println(productList.get(0).getEnableStatus());
        assertEquals(2,productList.size());
        count = productDao.queryProductCount(productCondition);
        assertEquals(2,count);
    }

    @Test
    public void testCQueryProductById() throws Exception{
        long productId = 1;
        // productId为1的商品添加两个商品详情图片记录
        ProductImg productImg1 = new ProductImg();
        productImg1.setImgAddr("图片1");
        productImg1.setImgDesc("测试图片1");
        productImg1.setPriority(1);
        productImg1.setCreateTime(new Date());
        productImg1.setProductId(1L);
        ProductImg productImg2 = new ProductImg();
        productImg2.setImgAddr("图片2");
        productImg2.setImgDesc("测试图片2");
        productImg2.setPriority(2);
        productImg2.setCreateTime(new Date());
        productImg2.setProductId(1L);
        List<ProductImg> productImgList = new ArrayList<ProductImg>();
        productImgList.add(productImg1);
        productImgList.add(productImg2);
        int effectedNum = productImgDao.batchInsertProductImg(productImgList);
        assertEquals(2,effectedNum);
        //查询Id为1的商品信息并校验返回的详情图列表size是否为2
        Product product = productDao.queryProductById(productId);
        assertEquals(2,product.getProductImgList().size());
        //删除新增的详情图
        effectedNum = productImgDao.deleteProductImgByProductId(productId);
        assertEquals(2,effectedNum);
    }

    @Test
    public void testDUpdateProduct() throws Exception{
        Product product = new Product();
        ProductCategory pc = new ProductCategory();
        Shop shop = new Shop();
        shop.setShopId(1L);
        product.setShop(shop);
        pc.setProductCategoryId(3L);
        product.setProductCategory(pc);
        product.setProductId(1L);
        product.setProductName("第一个产品");
        //修改商品名称
        int effectedNum = productDao.updateProduct(product);
        assertEquals(1,effectedNum);
    }

    @Test
    public void testEUpdateProductCategoryToNull(){
        //将商品的类别Id置为空
        int effectedNum = productDao.updateProductCategoryIdToNull(3L);
        assertEquals(1,effectedNum);
    }

    @Test
    public void testEDeleteShopAuthMap() throws Exception{
        int effectedNum = productDao.deleteProduct(9, 1);
        assertEquals(1, effectedNum);
    }
}
