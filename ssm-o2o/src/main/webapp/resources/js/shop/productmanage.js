$(function () {


    // http://localhost:8080/resources/js/shop/productmanage.js

    //alert(2);

    //通过该Url获取某店铺的商品列表
    var listUrl = '/shopadmin/getproductlistbyshop?pageIndex=1&pageSize=9999';
    //商品下架url
    var deleteUrl = '/shopadmin/modifyproduct';

    getList();
    //获取店铺下的商品列表
    function getList() {
        //从后台获取某店铺的商品列表
        $.getJSON(listUrl,function (data) {
            if(data.success){
                var productList = data.productList;
                var tempHtml = '';
                //遍历每条商品信息，拼接成一行显示，列信息包括:
                //商品名称，优先级，上架/下架(含productId)，编辑按钮(含productId)
                //预览(含productId)
                productList.map(function (item,index) {
                    var textOp = "下架";
                    var contraryStatus = 0;
                    if(item.enableStatus == 0){
                        //若状态值为0.表示为下架商品，点击操作变为上架
                        textOp = "上架";
                        contraryStatus = 1;
                    }else{
                        contraryStatus = 0;
                    }
                    //拼接商品的行信息
                    tempHtml += '<div class="row row-product">'
                             + '<div class="col-33">'+ item.productName + '</div>'
                             + '<div class="col-33">'+ item.priority + '</div>'
                             + '<div class="col-33">'
                             + '<a href="#" class="edit" data-id="'+ item.productId
                             + '" data-status="'+ item.enableStatus+ '">编辑</a>'
                             + '<a href="#" class="status" data-id="'+ item.productId
                             + '" data-status="'+ contraryStatus+ '">'+ textOp+ '</a>'
                             + '<a href="#" class="preview" data-id="'+ item.productId
                             + '" data-status="' + item.enableStatus+ '">预览</a>'
                             + '</div>'
                             + '</div>';
                });
                $('.product-wrap').html(tempHtml);
            }
        })
    }

    //将class为product-wrap里面的a标签绑定点击事件
    $('.product-wrap').on('click','a',function (e) {
        //获取点击事件的监听者
        var target = $(e.currentTarget);
        if(target.hasClass('edit')){
            //若果class存在edit，则为编辑操作，带productId
            window.location.href='/shopadmin/productoperation?productId='+e.currentTarget.dataset.id;
        }else if(target.hasClass('status')){
            //上/下架操作，携带productId
            changeItemStatus(e.currentTarget.dataset.id,e.currentTarget.dataset.status);
        }else if(target.hasClass('preview')){
            //预览操作，跳转到前台展示系统显示商品详情页
            window.location.href = '/frontend/productdetail?productId='+e.currentTarget.dataset.id;
        }
    });
    
    //改变商品上下架状态函数
    function changeItemStatus(id,enableStatus) {
        //定义product JSON对象，并封装productId和状态 （上下架）
        var product = {};
        product.productId = id;
        product.enableStatus = enableStatus;
        $.confirm('确定吗?',function () {
           //上下架相关商品
           $.ajax({
               url : deleteUrl,
               type : 'POST',
               data : {
                   productStr : JSON.stringify(product),
                   statusChange : true
               },
               dataType : 'json',
               success : function(data) {
                   if (data.success) {
                       $.toast('操作成功！');
                       getList();
                   } else {
                       $.toast('操作失败！');
                   }
               }
           });
        });
    }
    
});