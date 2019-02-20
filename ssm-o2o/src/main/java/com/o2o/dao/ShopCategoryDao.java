package com.o2o.dao;

import com.o2o.entity.ShopCategory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShopCategoryDao {

    /**
     * 查询所有商铺类别
     */
    //@Param 分spring和mybatis，二者在xml中使用时不同，spring用索引表示参数，mybatis直接用参数名
    List<ShopCategory> queryShopCategory(@Param("shopCategoryCondition")ShopCategory shopCategoryCondition);

}
