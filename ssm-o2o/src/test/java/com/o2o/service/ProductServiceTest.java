package com.o2o.service;

import com.o2o.BaseTest;
import com.o2o.dao.ProductDao;
import com.o2o.dto.ImageHolder;
import com.o2o.dto.ProductExecution;
import com.o2o.entity.Product;
import com.o2o.entity.ProductCategory;
import com.o2o.entity.Shop;
import com.o2o.enums.ProductStateEnum;
import com.o2o.exceptions.ShopOperationException;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ProductServiceTest extends BaseTest {

    @Autowired
    private ProductService productService;

    @Test
    public void testAddProduct() throws ShopOperationException, FileNotFoundException {
        //创建shopId为1 且productCategoryId为1的商品实例并给其成员变量赋值
        Product product = new Product();
        Shop shop = new Shop();
        shop.setShopId(1L);
        ProductCategory pc = new ProductCategory();
        pc.setProductCategoryId(2L);
        product.setShop(shop);
        product.setProductCategory(pc);
        product.setProductName("测试商品1");
        product.setProductDesc("测试商品1");
        product.setPriority(20);
        product.setCreateTime(new Date());
        product.setEnableStatus(ProductStateEnum.SUCCESS.getState());
        //创建缩略图文件流
        File thumbnailFile = new File("E:/Java视频资源/o2oDemoImage/xiaohuangren.jpg");
        InputStream is = new FileInputStream(thumbnailFile);
        ImageHolder thumbnail = new ImageHolder(thumbnailFile.getName(),is);
        //创建两个商品详情图文件流 并将他们添加到详情图片列表中
        File productImg1 = new File("E:/Java视频资源/o2oDemoImage/xiaohuangren.jpg");
        InputStream is1 = new FileInputStream(productImg1);
        File productImg2 = new File("E:/Java视频资源/o2oDemoImage/timg.jpg");
        InputStream is2 = new FileInputStream(productImg2);
        List<ImageHolder> imageHolderList = new ArrayList<ImageHolder>();
        imageHolderList.add(new ImageHolder(productImg1.getName(),is1));
        imageHolderList.add(new ImageHolder(productImg2.getName(),is2));
        //添加商品并验证
        ProductExecution pe = productService.addProduct(product,thumbnail,imageHolderList);
        assertEquals(ProductStateEnum.SUCCESS.getState(),pe.getState());
    }

    @Test
    public void testModifyProduct() throws ShopOperationException,FileNotFoundException{
        //创建商店Id和商品分类ID为1的商品实例并赋值
        Product product = new Product();
        Shop shop = new Shop();
        shop.setShopId(1L);
        ProductCategory pc = new ProductCategory();
        pc.setProductCategoryId(2L);
        product.setShop(shop);
        product.setProductCategory(pc);
        product.setProductId(1L);
        product.setProductName("正式商品2");
        product.setProductDesc("正式商品");
        //创建缩略图文件流
        File file  = new File("E:/Java视频资源/o2oDemoImage/xiaohuangren.jpg");
        InputStream is= new FileInputStream(file);
        ImageHolder thumbnail = new ImageHolder(file.getName(),is);
        //创建两个详情图
        File productImg1 = new File("E:/Java视频资源/o2oDemoImage/xiaohuangren.jpg");
        InputStream is1 = new FileInputStream(productImg1);
        File productImg2 = new File("E:/Java视频资源/o2oDemoImage/timg.jpg");
        InputStream is2 = new FileInputStream(productImg2);
        List<ImageHolder> imageHolderList = new ArrayList<ImageHolder>();
        imageHolderList.add(new ImageHolder(productImg1.getName(),is1));
        imageHolderList.add(new ImageHolder(productImg2.getName(),is2));
        //添加商品验证
        ProductExecution pe = productService.modifyProduct(product,thumbnail,imageHolderList);
        assertEquals(ProductStateEnum.SUCCESS.getState(),pe.getState());
    }
}
