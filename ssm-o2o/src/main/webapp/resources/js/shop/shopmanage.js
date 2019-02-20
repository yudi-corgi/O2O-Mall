$(function () {

    var shopId = getQueryString("shopId");
    var shopInfoUrl = '/shopadmin/getshopmanagementinfo?shopId='+shopId;
    $.getJSON(shopInfoUrl,function (data) {
        //如果Id不存在则跳转会shoplist页面
       if(data.redirect){
           window.location.href = data.url;
       }else{
           //此处判断后端是否通过session获取的shopId
           if(data.shopId != undefined && data.shopId != null){
                shopId = data.shopId;
           }
           $('#shopInfo').attr('href','/shopadmin/shopoperation?shopId='+shopId);
       }
    });
});