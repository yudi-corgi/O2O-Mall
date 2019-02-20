package com.o2o.util;


import com.o2o.dto.ImageHolder;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Position;
import net.coobird.thumbnailator.geometry.Positions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class ImageUtil {

    //全局变量
    //通过执行的线程获取当前项目的绝对路径(classPath)
    private static String basePath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
    //时间格式
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    //random对象
    private static final Random r = new Random();
    //获取日志对象
    private static Logger logger = LoggerFactory.getLogger(ImageUtil.class);

    /**
     * 将CommonsMultipartFile对象转换为File对象
     * @return
     */
    public static File transferCommonsMultipartFileToFile(CommonsMultipartFile cFile){
        File newFile = new File(cFile.getOriginalFilename());
        try {
            //查看源码可得
            //该方法提供将文件流转换为java.IO.File,并写入到newFile中
            cFile.transferTo(newFile);
        } catch (IOException e) {
            logger.error(e.toString());
            e.printStackTrace();
        }
        return newFile;
    }


    /**
     * 处理缩略图(店铺门面照及商品缩略图),并返回新生成图片的相对路径
     * @param thumbnail 用户传送的图片(CommonsMultipartFile该类为spring自带的图片文件处理类)
     * @param targetAddr 图片的存放位置
     * @return
     */
    public static String generateThumbnail(ImageHolder thumbnail, String targetAddr){
        //编码格式，防止乱码
        try {
            basePath= URLDecoder.decode(basePath,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //防止用户传输的图片重名，后端自动生成新的名称
        String realFileName = getRandomFileName();
        //获取图片的后缀名
        String extension = getFileExtension(thumbnail.getImageName());
        //若保存的目录不存在 则创建
        makeDirPath(targetAddr);

        //获取到相对路径
        String reletiveAddr = targetAddr + realFileName + extension;
        //输出日志信息
        logger.debug("current reletiveAddr is :" +reletiveAddr);
        //全路径: 根路径+相对路径
        File dest = new File(PathUtil.getImgBasePath() + reletiveAddr);
        logger.debug("current complete addr is :"+PathUtil.getImgBasePath() + reletiveAddr);

        try{//此处.of()是直接传入文件对象
            Thumbnails.of(thumbnail.getImage()).size(200,200)
                    .watermark(Positions.BOTTOM_RIGHT, ImageIO.read(new File(basePath + "/watermark.jpg")),0.25f)
                    .outputQuality(0.8f).toFile(dest);
        }catch (IOException e){
            logger.error(e.toString());
            e.printStackTrace();
        }
        //处理完图片后返回地址是为了保存于数据库
        return reletiveAddr;
    }

    /**
     * 处理商品详情图，并返回新生成图片的相对路径
     * @param thumbnail
     * @param targetAddr
     * @return
     */
    public static String generateNormalImg(ImageHolder thumbnail, String targetAddr) {
        //编码格式，防止乱码
        try {
            basePath= URLDecoder.decode(basePath,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //获取不重复的随机名
        String realFileName = getRandomFileName();
        //获取文件扩展名
        String extension = getFileExtension(thumbnail.getImageName());
        //如果路径不存在则创建
        makeDirPath(targetAddr);
        //获取文件存储的相对路径（带文件名）
        String relativeAddr = targetAddr + realFileName + extension;
        //获取文件要保存到的目标路径
        File dest = new File(PathUtil.getImgBasePath()+relativeAddr);
        logger.debug("current complete addr is :"+PathUtil.getImgBasePath()+relativeAddr);
        //调用Thumbnail生成带有水印的图片
        try{
            Thumbnails.of(thumbnail.getImage()).size(337,640)
                    .watermark(Positions.BOTTOM_RIGHT,ImageIO.read(new File(basePath + "/watermark.jpg")),0.25f)
                    .outputQuality(0.9f).toFile(dest);
        }catch (IOException e){
            logger.error(e.toString());
            throw new RuntimeException("创建详情图片失败:"+e.toString());
        }
        return relativeAddr;
    }


    /**
     * 编辑店铺图片信息时传入新图片则删除旧图片
     * storePath若是文件路径则删除文件，若是目录则删除目录下所有文件
     * @param storePath
     */
    public static void deleteFileOrPath(String storePath){
        //获取文件绝对路径
        File fileOrPath = new File(PathUtil.getImgBasePath() + storePath) ;
        //判断文件是否存在
        if(fileOrPath.exists()){
            //判断是否为目录
            if(fileOrPath.isDirectory()){
                //若为目录将目录下所有文件存到file数组，遍历并删除
                File files[] = fileOrPath.listFiles();
                for(int i=0;i<files.length;i++){
                    files[i].delete();
                }
            }
            //是文件则直接删除
            fileOrPath.delete();
        }
    }

    /**
     * 创建目标路径所涉及到的目录 ,PathUtil.getImgBasePath()获取到的目录路径若不存在则需要创建
     * @param targetAddr
     */
    private static void makeDirPath(String targetAddr) {
        //先获取图片全路径
        String realFileParentPath = PathUtil.getImgBasePath() + targetAddr;
        File dirPath = new File(realFileParentPath);
        //若不存在则创建目录
        if(!dirPath.exists()){
            dirPath.mkdirs();
        }

    }

    /**
     * 获取传输的图片后缀名
     * @param fileName
     * @return
     */
    private static String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));

    }

    /**
     * 生成随机文件名，当前年月日时分秒 + 随机五位数
     * @return
     */
    private static String getRandomFileName() {
        //生成随机数
        int rannum = r.nextInt(89999) + 10000;
        String nowTimeStr = sdf.format(new Date());
        return nowTimeStr + rannum;
    }


    //main 测试方法
    public static void main(String[] args) throws IOException {
        /*
        basePath= URLDecoder.decode(basePath,"utf-8");
        System.out.println(basePath);
        Thumbnails.of(new File("E:/Java视频资源/o2oDemoImage/xiaohuangren.jpg")) //图片的位置
        .size(1024,768)  //图片大小
        //参数一：水印的在图片上的位置 参数二：图片输入流读取水印(传入水印路径) 参数三：设置水印透明度
        .watermark(Positions.BOTTOM_RIGHT, ImageIO.read(new File(basePath + "watermark.jpg")),0.25f)
        .outputQuality(0.8f)    //设置图片压缩到原来的比例是多少
        .toFile("E:/Java视频资源/o2oDemoImage/xiaohuangrenNew.jpg");//新生成的图片位置及名称
        */
        System.out.println( Math.floor(Math.random() * 100));
    }
}
