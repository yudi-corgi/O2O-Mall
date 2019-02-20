/**
 *
 */
//表示加载js就执行function中的代码
$(function () {
    var shopId = getQueryString("shopId");
    //若shopId不为空，则为编辑店铺信息；为空则为注册店铺
    var isEdit = shopId?true:false;
    //该url获取商铺类别和区域信息
    var initUrl = '/shopadmin/getshopinitinfo';
    //该url访问执行注册店铺的controller
    var registerShopUrl = '/shopadmin/registershop';
    //获取店铺信息
    var shopInfoUrl = '/shopadmin/getshopbyid?shopId=' + shopId;
    //编辑店铺url
    var editShopUrl = '/shopadmin/modifyshop';
   // alert(initUrl);
    //调用方法,判断是编辑还是注册
    if(!isEdit){
        getShopInitInfo();
    }else {
        getShopInfo(shopId);
    }

    /*
        根据shopId获取店铺信息并显示，再获取修改的数据提交后台处理更新
     */
    function getShopInfo(shopId) {
        $.getJSON(shopInfoUrl,function (data) {
            if(data.success){
                var shop = data.shop;
                $('#shop-name').val(shop.shopName);
                $('#shop-addr').val(shop.shopAddr);
                $('#shop-phone').val(shop.phone);
                $('#shop-desc').val(shop.shopDesc);
                var shopCategory = '<option data-id="' + shop.shopCategory.shopCategoryId + '" selected>' +
                    shop.shopCategory.shopCategoryName +'</option>';
                var tempAreaHtml = '';
                data.areaList.map(function (item,index) {
                    tempAreaHtml += '<option data-id="'+ item.areaId +'">'
                    + item.areaName + '</option>';
                });

                $('#shop-category').html(shopCategory);
                $('#shop-category').attr('disabled','disabled');
                $('#area').html(tempAreaHtml);

                $('#area option[data-id="' + shop.area.areaId + '"]').attr('selected','selected');

            }
        });
    }

    /*
         获取店铺分类和区域列表，获取表单数据并提交
    */
    function getShopInitInfo() {
        //将后台获取的数据填充到html上
        //以json形式返回结果，参数1：访问的地址  参数2：回调函数(访问参数1地址后获取的数据保存在参数2data)
        $.getJSON(initUrl, function (data) {
            //alert(data.shopCategoryList.get(0).shopCategoryName);
            if (data.success) {
                var tempHtml = '';  //添加店铺分类的标签
                var tempAreaHtml = '';  //添加区域的标签
                //此处shopCategoryList/areaList是访问后台后返回的数据列表
                //map采用Jquery方式遍历
                data.shopCategoryList.map(function (item, index) {
                    tempHtml += '<option data-id="' + item.shopCategoryId + '">'
                        + item.shopCategoryName + '</option>';
                });
                data.areaList.map(function (item, index) {
                    tempAreaHtml += '<option data-id = "' + item.areaId + '">'
                        + item.areaName + '</option>';
                });
                //将写好的html标签字符串通过id赋给相对应Html标签
                $('#shop-category').html(tempHtml);
                $('#area').html(tempAreaHtml);
            }
        });
    }
        //通过提交按钮获取表单信息
        $('#submit').click(function () {
            //获取数据 创建json对象(controller层通过jackson-databind转换成POJO)
            var shop = {};
            if(isEdit){
                shop.shopId = shopId;
            }
            shop.shopName = $('#shop-name').val();
            shop.shopAddr = $('#shop-addr').val();
            shop.phone = $('#shop-phone').val();
            shop.shopDesc = $('#shop-desc').val();
            //shopCategory是对象，不是单一变量
            shop.shopCategory = {
                //find()找到shop-category下的子标签,not是删除某一元素，选中的返回false不删除，其余的为true删除
                //data('id')则是从被选中的元素(option)返回附加的数据，即返回选中的option的id
                shopCategoryId: $('#shop-category').find('option').not(function () {
                    return !this.selected;
                }).data('id')
            };
            shop.area = {
                areaId: $('#area').find('option').not(function () {
                    return !this.selected;
                }).data('id')
            };
            //$('#')[0]是将 JQ对象换为JS对象,由于上传文件可能多个,files[0]表示取第一个
            var shopImg = $('#shop-img')[0].files[0];
            //该对象以键值对保存数据，可以异步上传二进制文件，详细百度
            var formData = new FormData();
            formData.append("shopImg", shopImg);
            //JSON.stringify()将shop对象解析为字符串形式保存，JSON.parse()则将字符串解析为json对象
            formData.append("shopStr", JSON.stringify(shop));
            var verifyCodeActual = $('#j_kaptcha').val();
            if(!verifyCodeActual){
                $.toast("请输入验证码!");
                return;
            }
            formData.append("verifyCodeActual",verifyCodeActual);
            /*
                通过AJAX提交数据到后台
             */
            $.ajax({
                url: (isEdit?editShopUrl:registerShopUrl),    //提交地址
                type: 'POST',            //提交方式
                data: formData,          //提交数据
                contentType: false,      //发送信息至服务器时内容编码类型，默认：application/x-www-form-urlencoded
                processData: false,      /* 发送的数据将被转换为对象（从技术角度来讲并非字符串）以配合默认contentType，
                                          不希望数据转换则为false */
                cache: false,            //不会从浏览器缓存中加载请求信息
                success: function (data) {
                    //此处data是后台返回的数据,即控制层的modelMap(如果后台成功获取上面信息)
                    if (data.success) {
                        $.toast('提交成功!');
                    } else {
                        $.toast('提交失败!' + data.errMsg);
                    }
                    $('#kaptcha_img').click();
                }
            });
        });


})