package com.o2o.util;

public class PathUtil {


    /**
     * System.getProperty可以获取系统的属性信息
     */

    private  static String seperator = System.getProperty("file.separator");

    /**
     * 根据操作系统的不同，选择不同的项目图片根路径
     * @return
     */
    public static String getImgBasePath(){
        //获取当前的操作系统
        String os = System.getProperty("os.name");
        //获取图片路径
        String basePath = "";
        if(os.toLowerCase().startsWith("win")){ //window系统下
            basePath = "E:/Java视频资源/o2oDevImage/";
        }else{  //linux 、IOS等其它系统
            basePath = "/home/xiangze/image/";
        }
        //win系统下文件路径以 \ 划分，此处转为 /
        basePath = basePath.replace(seperator,"/");
        return  basePath;
    }

    /**
     *  获取存放店家图片的相对路径
     */
    public static String getShopImagePath(long shopId){

        String ImagePath = "o2oShopImage/" + shopId + "/";
        return ImagePath.replace(seperator,"/");
    }
}
