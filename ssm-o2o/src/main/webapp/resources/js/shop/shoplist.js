$(function () {
    getlist();
    //获取店铺列表
    function getlist(e){
        $.ajax({
            url:"/shopadmin/getshoplist",
            type:"get",
            dataType:"json",
            success:function (data) {
                if(data.success){
                    handleList(data.shopList);
                    handleUser(data.user);
                }
            }
        });
    }
    //为html页面标签配置文本信息
    function handleUser(data) {
        $('#user-name').text(data.name);
    }
    function handleList(data) {
        var html="";
        data.map(function (item,index) {
            html += '<div class="row row-shop"><div class="col-40">'
                 + item.shopName + '</div><div class="col-40">'
                 + shopStatus(item.enableStatus)
                 + '</div><div class="col-20">'
                 + goShop(item.enableStatus,item.shopId) + '</div></div>';
        });
        $('.shop-wrap').html(html);
    }
    //将shop状态Id换成文本信息
    function shopStatus(status){
        if(status == 0){
            return '审核中';
        }else if(status == -1){
            return '店铺非法';
        }else if(status == 1){
            return '审核通过';
        }
    }
    //进入店铺管理页面并传递shopId
    function goShop(status,shopId) {
        if(status == 1){
            return '<a href="/shopadmin/shopmanage?shopId='+shopId+'">进入</a>';
        }else {
            return '';
        }
    }
});