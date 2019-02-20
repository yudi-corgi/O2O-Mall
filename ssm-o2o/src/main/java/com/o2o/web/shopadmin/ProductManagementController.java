package com.o2o.web.shopadmin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.o2o.dto.ImageHolder;
import com.o2o.dto.ProductExecution;
import com.o2o.entity.Product;
import com.o2o.entity.ProductCategory;
import com.o2o.entity.Shop;
import com.o2o.enums.ProductStateEnum;
import com.o2o.exceptions.ProductOperationException;
import com.o2o.service.ProductCategoryService;
import com.o2o.service.ProductService;
import com.o2o.util.CodeUtil;
import com.o2o.util.HttpServletRequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/shopadmin")
public class ProductManagementController {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductCategoryService productCategoryService;

    //支持上传商品详情图片的最大数量
    private static final int IMAGE_MAX_COUNT = 6;

    @RequestMapping(value = "/addproduct",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> addProduct(HttpServletRequest request){
        Map<String,Object> modelMap = new HashMap<String, Object>();
        //验证码校验
        if(!CodeUtil.checkVerifyCode(request)){
            modelMap.put("success",false);
            modelMap.put("errMsg","验证码错误");
            return modelMap;
        }
        //接收前端参数的变量的初始化，包括商品，缩略图，详情图列表实体类
        MultipartHttpServletRequest multipartRequest = null; //获取图片流
        ImageHolder thumbnail = null; //保存缩略图
        List<ImageHolder> imageHolderList = new ArrayList<ImageHolder>();//保存详情图列表
        //从会话中获取上文路径来获取上传的内容
        CommonsMultipartResolver multipartResolver = new
                CommonsMultipartResolver(request.getSession().getServletContext());
        try{
            //若请求中存在文件流，则取出相关文件（包括缩略图和详情图）
            if(multipartResolver.isMultipart(request)){
                thumbnail = HandleImage((MultipartHttpServletRequest) request, imageHolderList);
            }else {
                modelMap.put("success",false);
                modelMap.put("errMsg","上传图片不能为空");
                return modelMap;
            }
        }catch (Exception e){
            modelMap.put("success",false);
            modelMap.put("errMsg",e.toString());
            return modelMap;
        }
        //生成product实例
        ObjectMapper mapper = new ObjectMapper();////实例化ObjectMapper，进行 POJO 转化 Json 或 Json 转换 POJO
        Product product = null;
        //获取商品信息的数据(json)
        String productStr = HttpServletRequestUtil.getString(request,"productStr");
        try {
            //尝试获取前端传过来的表单string流并将其转换为Product实体类
            product = mapper.readValue(productStr,Product.class);
        }catch (Exception e){
            modelMap.put("success",false);
            modelMap.put("errMsg",e.toString());
            return modelMap;
        }
        //若Product信息，缩略图以及商品详情图列表非空，则开始进行数据库添加操作
        if(product != null && thumbnail != null && imageHolderList.size() > 0){
            try{
                //从session中获取当前店铺的Id并赋值给product，减少对前端数据的依赖
                Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
                product.setShop(currentShop);
                //执行添加操作
                ProductExecution pe = productService.addProduct(product,thumbnail,imageHolderList);
                if(pe.getState() == ProductStateEnum.SUCCESS.getState()){
                    modelMap.put("success",true);
                }else {
                    modelMap.put("success",false);
                    modelMap.put("errMsg",pe.getStateInfo());
                }
            }catch (ProductOperationException e){
                modelMap.put("success",false);
                modelMap.put("errMsg",e.toString());
                return modelMap;
            }
        }else {
            modelMap.put("success",false);
            modelMap.put("errMsg","请输入商品信息");
        }
        return modelMap;
    }

    //设置商品缩略以及商品详情图
    private ImageHolder HandleImage(MultipartHttpServletRequest request, List<ImageHolder> imageHolderList) throws IOException {
        MultipartHttpServletRequest multipartRequest;
        ImageHolder thumbnail;
        multipartRequest = request;
        //取出缩略图并构建ImageHolder对象,CommonsMultipartFile是有因为spring解析的是该类文件流
        if(multipartRequest.getFile("thumbnail") == null){
            return null;
        }
        CommonsMultipartFile thumbnailFile = (CommonsMultipartFile) multipartRequest.getFile("thumbnail");
        thumbnail = new ImageHolder(thumbnailFile.getOriginalFilename(),thumbnailFile.getInputStream());
        //取出详情图列表并构建List<ImageHolder>列表对象，最多支持六张图片上传
        for(int i=0;i<IMAGE_MAX_COUNT;i++){
            if( multipartRequest.getFile("productImg"+i) == null){
                return null;
            }
            CommonsMultipartFile productImgFile = (CommonsMultipartFile) multipartRequest.getFile("productImg"+i);
            if(productImgFile != null){
                //若取出的第i个详情图片文件流不为空，则加入详情图片列表
                ImageHolder productImg = new ImageHolder(productImgFile.getOriginalFilename(),productImgFile.getInputStream());
                imageHolderList.add(productImg);
            }else {
                //若取出的第i个详情图片文件流为空，则终止循环
                break;
            }
        }
        return thumbnail;
    }

    @RequestMapping(value = "/getproductbyid",method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> getProductById(@RequestParam("productId")long productId){
        Map<String,Object> modelMap = new HashMap<String, Object>();
        //非空判断
        if(productId > -1){
            //获取商品信息
            Product product = productService.getPruductById(productId);
            //获取该店铺下的商品类别列表
            List<ProductCategory> productCategoryList =
                    productCategoryService.getProductCategoryList(product.getShop().getShopId());
            modelMap.put("product",product);
            modelMap.put("productCategoryList",productCategoryList);
            modelMap.put("success",true);
        }else {
            modelMap.put("success",false);
            modelMap.put("errMsg","empty productId");
        }
        return modelMap;
    }

    @RequestMapping(value = "/modifyproduct",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> modifyProduct(HttpServletRequest request){
        Map<String,Object> modelMap = new HashMap<String, Object>();
        //判断是商品编辑 还是商品上下架操作
        //前者需验证码，后者不需要
        boolean statusChange = HttpServletRequestUtil.getBoolean(request,"statusChange");
        //验证码判断
        if(!statusChange && !CodeUtil.checkVerifyCode(request)){
            modelMap.put("success",false);
            modelMap.put("errMsg","输入了错误的验证码");
            return modelMap;
        }
        //编辑操作
        //接收前端参数的变量的初始化，包括商品，缩略图，详情图列表
        ObjectMapper mapper = new ObjectMapper();
        Product product = null ;
        ImageHolder thumbnail = null ;
        List<ImageHolder> productImageHolderList = new ArrayList<ImageHolder>();
        //从sesssion中获取上下文中的文件流
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver
                (request.getSession().getServletContext());
        //若存在文件流则取出(缩略图，详情图)
        try {
            if(multipartResolver.isMultipart(request)){
                thumbnail = HandleImage((MultipartHttpServletRequest) request, productImageHolderList);
            }
        }catch (Exception e){
            modelMap.put("success",false);
            modelMap.put("errMsg",e.toString());
            return modelMap;
        }
        try {
            String productStr = HttpServletRequestUtil.getString(request,"productStr");
            //通过databind 将json转换为实体类 Product
            product = mapper.readValue(productStr,Product.class);
        }catch (Exception e){
            modelMap.put("success",false);
            modelMap.put("errMsg",e.toString());
            return modelMap;
        }
        //非空判断
        if(product != null ){
            try{
                //从session中获取当前店铺的Id并赋值给product，减少对前端数据的依赖
                Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
                product.setShop(currentShop);
                //执行添加操作
                ProductExecution pe = productService.modifyProduct(product,thumbnail,productImageHolderList);
                if(pe.getState() == ProductStateEnum.SUCCESS.getState()){
                    modelMap.put("success",true);
                }else {
                    modelMap.put("success",false);
                    modelMap.put("errMsg",pe.getStateInfo());
                }
            }catch (ProductOperationException e){
                modelMap.put("success",false);
                modelMap.put("errMsg",e.toString());
                return modelMap;
            }
        }else {
            modelMap.put("success",false);
            modelMap.put("errMsg","请输入商品信息");
        }
        return modelMap;
    }

    @RequestMapping(value = "/getproductlistbyshop",method = RequestMethod.GET)
    @ResponseBody
    private Map<String,Object> getProductListByShop(HttpServletRequest request){
        Map<String,Object> modelMap = new HashMap<String, Object>();
        //获取从前台传过来的页码
        int pageIndex = HttpServletRequestUtil.getInt(request,"pageIndex");
        //获取前台每页要求返回的数据条数
        int pageSize = HttpServletRequestUtil.getInt(request,"pageSize");
        //从session获取当前店铺信息,主要是shopId
        Shop currentShop = (Shop)request.getSession().getAttribute("currentShop");
        //空值判断
        if((pageIndex>-1)&&(pageSize>-1)&&(currentShop!=null)&&(currentShop.getShopId()!=null)){
            //获取传入的需要检索的条件，包括是否需要从某个商品类别以及模糊查找商品名去筛选某个店铺下的商品列表
            //筛选的条件可以进行排列组合
            long productCategoryId = HttpServletRequestUtil.getLong(request,"productCategoryId");
            String productName = HttpServletRequestUtil.getString(request,"productName");
            Product productCondition = compactProductCondition(currentShop.getShopId(),productCategoryId,productName);
            //传入查询条件以及分页信息进行查询，返回相应的商品列表以及总数
            ProductExecution pe = productService.getProductList(productCondition,pageIndex,pageSize);
            modelMap.put("productList",pe.getProductList());
            modelMap.put("count",pe.getCount());
            modelMap.put("success",true);
        }else{
            modelMap.put("success",false);
            modelMap.put("errMsg","empty pageSize or pageIndex or shopId");
        }
        return modelMap;
    }

         /**
     * 封装商品的检索条件
     * @param shopId    当前店铺id(必需)
     * @param productCategoryId  查询商品的类比
     * @param productName   模糊查询商品名称
     * @return
     */
    private Product compactProductCondition(long shopId, long productCategoryId,String productName){
        Product productCondition = new Product();
        Shop shop = new Shop();
        shop.setShopId(shopId);
        productCondition.setShop(shop);
        //若有指定类别的要求则添加进对象
        if(productCategoryId!=-1L){
            ProductCategory productCategory = new ProductCategory();
            productCategory.setProductCategoryId(productCategoryId);
            productCondition.setProductCategory(productCategory);
        }
        //若有商品名，模糊查询的要求则添加
        if(productName != null){
            productCondition.setProductName(productName);
        }
        return productCondition;
    }
}
