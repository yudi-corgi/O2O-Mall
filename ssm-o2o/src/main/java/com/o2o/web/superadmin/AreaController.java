package com.o2o.web.superadmin;

import com.o2o.entity.Area;
import com.o2o.service.AreaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/superadmin")
public class AreaController {

    //获取logger对象
    Logger logger = LoggerFactory.getLogger(AreaController.class);


    @Autowired
    private AreaService areaService;

    @RequestMapping(value = "/listarea",method = RequestMethod.GET)
    @ResponseBody   //加此注解是将返回数据以json格式输出   @Controller + @ResponseBody == @RestController
    private Map<String,Object> listArea(){

        //输出日志信息
        logger.info("--- start ---");
        Long startTime = System.currentTimeMillis();

        Map<String,Object> modelMap = new HashMap<String,Object>();
        List<Area> areaList = new ArrayList<Area>();

        try{
            areaList = areaService.getAreaList();
            modelMap.put("rows",areaList);//封装所有area对象信息
            modelMap.put("total",areaList.size());//封装area信息条数
        }catch (Exception e){
            e.printStackTrace();
            //捕获异常时封装错误信息
            modelMap.put("success",false);
            modelMap.put("errMsg",e.toString());
        }
        logger.error("test error"); //测试error级别日志信息输出
        Long endTime = System.currentTimeMillis();
        //占位符方式输出
        logger.debug("costTime:[{}ms]",endTime-startTime);//测试debug级别日志信息输出
        logger.info("--- end ---");

        return modelMap;
    }

}
