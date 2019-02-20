$(function() {
    //定义访问后台，获取头条列表以及一级类别列表的URL
    var url = '/frontend/listmainpageinfo';
    //访问后台，获取数据
    $.getJSON(url, function (data) {
        if (data.success) {
            //获取后台传递的头条列表
            var headLineList = data.headLineList;
            var swiperHtml = '';
            //遍历头条列表，进行html标签拼接
            headLineList.map(function (item, index) {
                swiperHtml += '<div class="swiper-slide img-wrap">'
                    + '<a href="'+ item.lineLink +'" external>'
                    + '<img class="banner-img" src="'+ item.lineImg +'" alt="'+ item.lineName +'">'
                    + '</a></div>';
            });
            $('.swiper-wrapper').html(swiperHtml);
            //设定轮播图轮换时间为3秒
            $(".swiper-container").swiper({
                autoplay: 3000,
                //用户对轮播图进行操作时，是否自动停止autoplay
                autoplayDisableOnInteraction: false
            });
            //获取一级类别列表
            var shopCategoryList = data.shopCategoryList;
            var categoryHtml = '';
            //遍历拼接html标签，宽度(col-50)
            shopCategoryList.map(function (item, index) {
                categoryHtml += ''
                    +  '<div class="col-50 shop-classify" data-category='+ item.shopCategoryId +'>'
                    +      '<div class="word">'
                    +          '<p class="shop-title">'+ item.shopCategoryName +'</p>'
                    +          '<p class="shop-desc">'+ item.shopCategoryDesc +'</p>'
                    +      '</div>'
                    +      '<div class="shop-classify-img-warp">'
                    +          '<img class="shop-img" src="'+ item.shopCategoryImg +'">'
                    +      '</div>'
                    +  '</div>';
            });
            $('.row').html(categoryHtml);
        }
    });
    //点击:我: 显示侧边栏
    $('#me').click(function () {
        $.openPanel('#panel-left-demo');
    });

    //点击一级类别后进入二级类别页面(包括商铺列表)
    $('.row').on('click', '.shop-classify', function (e) {
        var shopCategoryId = e.currentTarget.dataset.category;
        var newUrl = '/frontend/shoplist?parentId=' + shopCategoryId;
        window.location.href = newUrl;
    });

});
