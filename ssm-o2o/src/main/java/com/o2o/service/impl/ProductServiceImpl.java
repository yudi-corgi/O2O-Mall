package com.o2o.service.impl;

import com.o2o.dao.ProductDao;
import com.o2o.dao.ProductImgDao;
import com.o2o.dto.ImageHolder;
import com.o2o.dto.ProductExecution;
import com.o2o.entity.Product;
import com.o2o.entity.ProductImg;
import com.o2o.enums.ProductStateEnum;
import com.o2o.exceptions.ProductOperationException;
import com.o2o.service.ProductService;
import com.o2o.util.ImageUtil;
import com.o2o.util.PageCalculator;
import com.o2o.util.PathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductDao productDao;
    @Autowired
    private ProductImgDao productImgDao;

    //获取商品列表
    @Override
    public ProductExecution getProductList(Product productCondition, int pageIndex, int pageSize) {
        //页码转换成数据库的行码，并调用dao层取回指定页码的商品列表
        int rowIndex = new PageCalculator().calculatorRowIndex(pageIndex,pageSize);
        List<Product> productList = productDao.queryProductList(productCondition,rowIndex,pageSize);
        //基于同样的查询条件返回该查询条件下的商品总数
        int count = productDao.queryProductCount(productCondition);
        ProductExecution pe = new ProductExecution();
        pe.setProductList(productList);
        pe.setCount(count);
        return pe;
    }

    //1.处理缩略图，获取缩略图相对路径并赋值给Product数据库保存
    //2.往tb_product写入商品信息，并返回productId
    //3.结合productId批量处理商品详情图
    //4.将商品详情图列表批量插入tb_product_img中
    @Override
    @Transactional
    public ProductExecution addProduct(Product product, ImageHolder thumbnail,
                                       List<ImageHolder> productImgList) throws ProductOperationException {
        //空值判断
        if(product != null && product.getShop()!=null && product.getShop().getShopId()!= null){
            //为商品设置默认值
            product.setCreateTime(new Date());
            product.setLastEditTime(new Date());
            //默认为上架的状态
            product.setEnableStatus(1);
            //若商品缩略图不为空则添加
            if(thumbnail != null){
                addThumbnail(product,thumbnail);
            }
            try{
                //创建商品信息
                int effectedNum = productDao.insertProduct(product);
                if(effectedNum <= 0){
                    throw new ProductOperationException("创建商品失败");
                }
            }catch (Exception e){
                throw new ProductOperationException("创建商品失败:"+e.getMessage());
            }
            //若商品详情图不为空
            if(productImgList != null && productImgList.size() > 0){
                addProductImgList(product,productImgList);
            }
            return new ProductExecution(ProductStateEnum.SUCCESS,product);
        }else {
            //传参为空则返回空值错误信息
            return new ProductExecution(ProductStateEnum.EMPTY);
        }
    }

    @Override
    public Product getPruductById(Long productId) throws ProductOperationException {
        return productDao.queryProductById(productId);
    }

    @Override
    @Transactional
    //1.若缩略图有参数，则处理缩略图，若原先存有图片则先删除再添加
    //2.详情图同上操作
    //3.将tb_product_img表里该商品存在的图片全部删除
    //4.更新商品信息
    public ProductExecution modifyProduct(Product product, ImageHolder thumbnail, List<ImageHolder> productImageHolder) throws ProductOperationException {
        //空值判断
        if(product != null && product.getShop() != null && product.getShop().getShopId() != null){
            //给商品设置默认属性
            product.setLastEditTime(new Date());
            //若商品缩略图不为空且原本已存在缩略图，则删除再添加
            if(thumbnail != null){
                //先获取原信息
                Product tempProduct = productDao.queryProductById(product.getProductId());
                if(tempProduct.getImgAddr() != null){
                    ImageUtil.deleteFileOrPath(tempProduct.getImgAddr());
                }
                addThumbnail(product,thumbnail);
            }
            //若有新详情图，则删除原先图片再添加
            if(productImageHolder != null && productImageHolder.size()>0){
                deleteProductImgList(product.getProductId());
                addProductImgList(product,productImageHolder);
            }
            //更新商品信息
            try{
                int effectedNum = productDao.updateProduct(product);
                if (effectedNum <= 0){
                    throw new ProductOperationException("更新商品信息失败");
                }
                return new ProductExecution(ProductStateEnum.SUCCESS,product);
            }catch (ProductOperationException e){
                throw new ProductOperationException("更新商品信息失败："+e.toString());
            }
        }else {
            return new ProductExecution(ProductStateEnum.EMPTY);
        }
    }

    //删除商品详情图片
    private void deleteProductImgList(Long productId) {
        //获取原图片
        List<ProductImg> productImgList = productImgDao.queryProductImgList(productId);
        //获取到后删除文件夹里的图片
        for (ProductImg p :productImgList) {
            ImageUtil.deleteFileOrPath(p.getImgAddr());
        }
        //删除数据库信息
        productImgDao.deleteProductImgByProductId(productId);
    }

    /**
     * 批量添加商品详情图
     * @param product
     * @param productImgList
     */
    private void addProductImgList(Product product, List<ImageHolder> productImgList) {
        //获取商店图片存储相对路径
        String dest = PathUtil.getShopImagePath(product.getShop().getShopId());
        List<ProductImg> productImgList1 = new ArrayList<ProductImg>();
        //遍历图片一次去处理，并添加进productImg实体类里
        for (ImageHolder imageholder:productImgList) {
            //ImageUtil.generateThumbnail保存图片 并返回图片相对路径
            String imgAddr = ImageUtil.generateNormalImg(imageholder,dest);
            ProductImg productImg = new ProductImg();
            productImg.setImgAddr(imgAddr);
            productImg.setProductId(product.getProductId());
            productImg.setCreateTime(new Date());
            productImgList1.add(productImg);
        }
        //如若确实有图片添加，就执行批量插入操作
        if(productImgList1.size()>0){
           try{
               int effectedNum = productImgDao.batchInsertProductImg(productImgList1);
               if(effectedNum<=0){
                   throw new ProductOperationException("创建商品详情图片失败");
               }
           }catch (Exception e){
               throw new ProductOperationException("创建商品详情图片失败："+e.toString());
           }
        }
    }

    /**
     * 添加商品缩略图
     * @param product
     * @param thumbnail
     */
    private void addThumbnail(Product product, ImageHolder thumbnail) {
        //获取商店图片存储相对路径
        String dest = PathUtil.getShopImagePath(product.getShop().getShopId());
        //传入数据处理图片后，返回图片相对路径
        String thumbnailAddr = ImageUtil.generateThumbnail(thumbnail,dest);
        product.setImgAddr(thumbnailAddr);
    }


}
