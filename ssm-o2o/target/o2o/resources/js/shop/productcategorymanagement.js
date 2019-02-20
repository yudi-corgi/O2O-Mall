$(function () {

    var listUrl = '/shopadmin/getproductcategorylist';
    var addUrl = '/shopadmin/addproductcategorys';
    var deleteUrl = '/shopadmin/removeproductcategory';
    getList();
    //获取商品类别列表
    function getList() {
        $.getJSON(listUrl,function (data) {
           if(data.success){
                var dataList = data.data;
                $('.category-wrap').html('');
                var tempHtml = '';
                dataList.map(function (item,index) {
                   tempHtml += '<div class="row row-product-category now">'
                            + '<div class="col-33 product-category-name">'
                            + item.productCategoryName
                            + '</div>'
                            + '<div class="col-33">'
                            + item.priority
                            + '</div>'
                            + '<div class="col-33"><a href="#" class="button delete" data-id="'
                            + item.productCategoryId
                            + '">删除</a></div></div>';
                });
                $('.category-wrap').append(tempHtml);
           }
        });
    }
    //新增按钮添加点击事件
    $('#new').click(function () {
        //新增类别的控件类选择器设置为temp以跟原先就存在的类别作区别
        var tempHtml = '<div class="row row-product-category temp">'
                     + '<div class="col-33"><input class="category-input category" type="text" placeholder="分类名"></div>'
                     + '<div class="col-33"><input class="category-input priority" type="number" placeholder="优先级"></div>'
                     + '<div class="col-33"><a href="#" class="button delete">删除</a> </div>'
                     + '</div>';
        $('.category-wrap').append(tempHtml);
    });
    //提交按钮将数据提交到后台
    $('#submit').click(function () {
        //获取使用了.temp类选择器的控件
        var tempArr = $('.temp');
        //定义数组
        var productCategoryList = [];
        //此处为jq对象遍历，参数顺序与上方map相反
        tempArr.map(function (index,item) {
            //定义json对象
            var tempObj = {};
            // $(item)
            //$(tempArr[index]).find('.category').val();
            tempObj.productCategoryName = $(item).find('.category').val();
            tempObj.priority = $(item).find('.priority').val();
            if (tempObj.productCategoryName && tempObj.priority) {
                //将json对象保存在数组中
                productCategoryList.push(tempObj);
            }
        });
        //异步提交数据到后台
        $.ajax({
            url:addUrl,
            type:'POST',
            data:JSON.stringify(productCategoryList),   //传递的数据，后端接收的是一个json对象的字符串
            contentType:'application/json',         //发送信息至服务器时内容编码类型
            success:function (data) {
                if(data.success){
                    $.toast('提交成功');
                    //若提交成功则刷新类别列表
                    getList();
                }else{
                    $.toast('提交失败');
                }

            }
        });
    });

    //添加点击事件给新增(但未添加到数据库)的商品类别删除按钮
    $('.category-wrap').on('click','.row-product-category.temp .delete',function (e) {
        //控制台输出删除的元素标签
        console.log($(this).parent().parent());
        $(this).parent().parent().remove();
    });
    //添加点击事件给商品类别的删除按钮
    $('.category-wrap').on('click','.row-product-category.now .delete',function (e) {
        //e.target获取的是<div>  e.currentTarget获取的是delete(事件监听者)
        var target = e.currentTarget;
        $.confirm("确定删除吗?",function () {
           $.ajax({
               url:deleteUrl,
               type:'POST',
               data:{productCategoryId:target.dataset.id}, //传递后台的数据
               dataType:'json',                            //服务器返回数据的格式
               success:function (data) {
                   if(data.success){
                       $.toast('删除成功!');
                       //刷新列表
                       getList();
                   }else{
                       $.toast('删除失败!');
                   }
               }

           });
        });
    });

});