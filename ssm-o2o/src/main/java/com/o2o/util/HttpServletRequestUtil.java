package com.o2o.util;

import javax.servlet.http.HttpServletRequest;

public class HttpServletRequestUtil {

    //Key转换为Int
    public static int getInt(HttpServletRequest request,String key){
        try{
            return Integer.decode(request.getParameter(key));
        }catch (Exception e){
            return -1;
        }
    }
    //Key转换为Long
    public static long getLong(HttpServletRequest request,String key){
        try{
            return Long.valueOf(request.getParameter(key));
        }catch (Exception e){
            return -1;
        }
    }
    //Key转换为Double
    public static Double getDouble(HttpServletRequest request,String key){
        try{
            return Double.valueOf(request.getParameter(key));
        }catch (Exception e){
            return -1d;
        }
    }
    //Key转换为布尔值
    public static Boolean getBoolean(HttpServletRequest request,String key){
        try{
            return Boolean.valueOf(request.getParameter(key));
        }catch (Exception e){
            return false;
        }
    }
    //获取String类型的key
    public static String getString(HttpServletRequest request,String key){
        try{
            String result = request.getParameter(key);
            if(result != null){
                //不为空则去掉左右两边的空格
                result = result.trim();
            }
            if("".equals(result)){
                result = null;
            }
            return result;
        }catch (Exception e){
            return null;
        }
    }

}
