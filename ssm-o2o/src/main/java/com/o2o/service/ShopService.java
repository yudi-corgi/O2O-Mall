package com.o2o.service;

import com.o2o.dto.ImageHolder;
import com.o2o.dto.ShopExecution;
import com.o2o.entity.Shop;
import com.o2o.exceptions.ShopOperationException;
import jdk.internal.util.xml.impl.Input;

import java.io.InputStream;

public interface ShopService {

    //根据shopCondition分页返回相应店铺列表
    ShopExecution getShopList(Shop shopCondition,int pageIndex,int pageSize);
    //获取店铺信息
    Shop getByShopId(long shopId);
    //编辑店铺信息（包括图片处理）
    ShopExecution modifyShop(Shop shop, ImageHolder thumbnail)
            throws ShopOperationException;
    //注册店铺
    ShopExecution addShop(Shop shop, ImageHolder thumbnail)
            throws ShopOperationException;
}
