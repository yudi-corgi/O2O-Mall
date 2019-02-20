package com.o2o.dao;

import com.o2o.entity.Area;

import java.util.List;

public interface AreaDao {


    /**
     * 列出区域列表
     * @return areaList
     */
    List<Area> queryArea();

}
