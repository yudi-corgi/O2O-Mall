package com.o2o.util;

import com.google.code.kaptcha.Constants;

import javax.servlet.http.HttpServletRequest;

/*
    验证码工具类
 */
public class CodeUtil {

    //判断前台输入验证码是否正确
    public static boolean checkVerifyCode(HttpServletRequest request){
        //从session中获取验证码图片中的字符
        String verifyCodeExpected = (String)request.getSession().getAttribute(
                Constants.KAPTCHA_SESSION_KEY);
        /*
            此处若前台有上传文件流且后台未处理，则request获取不到前台的所有值(指缩略图未被springmvc的视图解析器处理)
            即存在未处理的数据则request则获取不到前台全部值
        */
        String verifyCodeActual = HttpServletRequestUtil.getString(request,"verifyCodeActual");
        if(verifyCodeActual == null || !verifyCodeActual.equals(verifyCodeExpected)){
            return false;
        }
        return true;
    }
}
