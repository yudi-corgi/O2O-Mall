package com.o2o.service.impl;

import com.o2o.dao.ShopDao;
import com.o2o.dto.ImageHolder;
import com.o2o.dto.ShopExecution;
import com.o2o.entity.Shop;
import com.o2o.enums.ShopStateEnum;
import com.o2o.exceptions.ShopOperationException;
import com.o2o.service.ShopService;
import com.o2o.util.ImageUtil;
import com.o2o.util.PageCalculator;
import com.o2o.util.PathUtil;
import jdk.internal.util.xml.impl.Input;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

@Service
public class ShopServiceImpl implements ShopService {

    @Autowired
    private ShopDao shopDao;


    @Override
    public ShopExecution getShopList(Shop shopCondition, int pageIndex, int pageSize) {
        //通过页数获取sql查询的起始rowIndex
        int rowIndex = new PageCalculator().calculatorRowIndex(pageIndex,pageSize);
        //获取店铺列表和总数
        List<Shop> shopList = shopDao.queryShopList(shopCondition,rowIndex,pageSize);
        int count = shopDao.queryShopCount(shopCondition);
        //存值返回
        ShopExecution se = new ShopExecution();
        if(shopList != null){
            se.setShopList(shopList);
            se.setCount(count);
        }else{
            se.setState(ShopStateEnum.INNER_ERROR.getState());
        }

        return se;
    }

    @Override
    public Shop getByShopId(long shopId) {
        return shopDao.queryByShopId(shopId);
    }

    @Override
    public ShopExecution modifyShop(Shop shop, ImageHolder thumbnail)
            throws ShopOperationException {
        //判断传递参数是否为空
        if(shop == null || shop.getShopId() == null){
            return new ShopExecution(ShopStateEnum.NULL_SHOP);
        }else{
            try{
                //1.判断是否需要处理图片
                if(thumbnail.getImage() != null && thumbnail.getImageName() != null && !"".equals(thumbnail.getImageName())){
                    Shop tempShop = shopDao.queryByShopId(shop.getShopId());
                    //1-1.删除旧图片
                    if(tempShop.getShopImg() != null) {
                        ImageUtil.deleteFileOrPath(tempShop.getShopImg());
                    }
                }
                //调用方法生成新图片并设值到Shop对象中
                addShopImg(shop,thumbnail);
                //2.更新店铺信息
                shop.setLastEditTime(new Date());
                int effectedNum = shopDao.updateShop(shop);
                if(effectedNum <= 0){
                    return new ShopExecution(ShopStateEnum.INNER_ERROR);
                }else{
                    shop = shopDao.queryByShopId(shop.getShopId());
                    return new ShopExecution(ShopStateEnum.SUCCESS,shop);
                }
            }catch(Exception e){
                throw new ShopOperationException("modifyShop error:"+e.getMessage());
            }
        }
    }

    @Override
    @Transactional //事务管理
    public ShopExecution addShop(Shop shop, ImageHolder thumbnail) {
        //当店铺信息为空时返回状态信息
        if(shop == null){
            return new ShopExecution(ShopStateEnum.NULL_SHOP);
        }

        //执行添加店铺
        try{
            //1.初始化值
            shop.setEnableStatus(0);//审核中
            shop.setCreateTime(new Date());
            shop.setLastEditTime(new Date());
            //2.并插入数据库
            int effectedNum = shopDao.insertShop(shop);
            if(effectedNum <= 0){
                //使用运行时异常，错误时会事务终止并回滚，若直接抛出Exception则之前执行的操作不会回滚
                throw new ShopOperationException("店铺创建失败");
            }else{
                //3.店铺创建成功后，判断图片文件是否为空，不为空则添加图片
                if(thumbnail.getImage() != null){
                    System.out.println("shopImg0不为空");
                    //System.out.println(shopImgInputStream.getAbsolutePath());
                    try {
                        //存储图片
                        addShopImg(shop, thumbnail);
                    }catch (Exception e){
                        e.printStackTrace();
                        //throw new ShopOperationException("addShopImg123 error:"+e.getMessage());
                    }
                    //4.更新店铺的图片地址
                    /**
                     * 解析：addShopImg添加图片后此处直接可直接更新，是因为java中若传入函数的参数为引用类型
                     * 则传递的是引用类型的内存地址，所以当值改变后，参数所属的类也会将其改变
                     * 若是基本数据类型，则传递的是参数值的拷贝，原本的类里的参数不会受到影响
                     */
                    effectedNum = shopDao.updateShop(shop);
                    if (effectedNum <= 0){
                        throw new ShopOperationException("更新店铺图片地址失败");
                    }
                }
            }

        }catch (Exception e){
            throw new ShopOperationException("addShop error:"+e.getMessage());
        }
        return new ShopExecution(ShopStateEnum.CHECK,shop);
    }

    //处理图片 并创建存储图片路径
    private void addShopImg(Shop shop,ImageHolder thumbnail) {
        //获取图片的相对路径
        String dest = PathUtil.getShopImagePath(shop.getShopId());
        System.out.println(dest);
        //获取图片的绝对路径
        String shopImgAddr = ImageUtil.generateThumbnail(thumbnail,dest);
        //设置shop的图片路径属性
        shop.setShopImg(shopImgAddr);
    }
}
