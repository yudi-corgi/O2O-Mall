package com.o2o.util;

public class PageCalculator {

    //返回数据的起始行数以便分页显示数据
    public int calculatorRowIndex(int pageIndex,int pageSize){
        return (pageIndex>0)?(pageIndex-1)*pageSize:0;
    }
}
