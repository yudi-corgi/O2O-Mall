$(function() {
    //商品列表是否加载的标识符
    var loading = false;
    //分页允许返回的最大条数，如果超出则进制返回后台
    var maxItems = 20;
    var pageSize = 3;
    //查询商品列表的url
    var listUrl = '/frontend/listproductsbyshop';
    //页码
    var pageNum = 1;
    var shopId = getQueryString('shopId');
    var productCategoryId = '';
    var productName = '';
    //访问店铺信息和商品信息的url
    var searchDivUrl = '/frontend/listshopdetailpageinfo?shopId='
        + shopId;
    //渲染出店铺信息以及商品类别信息列表以供搜索
    getSearchDivData();
    //预先加载商品列表信息
    addItems(pageSize, pageNum);

    //获取本店铺信息以及商品类别信息列表
    function getSearchDivData() {
        var url = searchDivUrl;
        $.getJSON(url,function(data) {
            if (data.success) {
                var shop = data.shop;
                //渲染店铺信息
                $('#shop-cover-pic').attr('src', shop.shopImg);
                $('#shop-update-time').html(new Date(shop.lastEditTime).Format("yyyy-MM-dd"));
                $('#shop-name').html(shop.shopName);
                $('#shop-desc').html(shop.shopDesc);
                $('#shop-addr').html(shop.shopAddr);
                $('#shop-phone').html(shop.phone);
                //获取商品类别列表，并拼接a标签
                var productCategoryList = data.productCategoryList;
                var html = '';
                productCategoryList.map(function(item, index) {
                    html += '<a href="#" class="button" data-product-search-id='
                                    + item.productCategoryId
                        + '>'
                        + item.productCategoryName
                        + '</a>';
                });
                $('#shopdetail-button-div').html(html);
            }
        });
    }

    //获取分页展示的商品信息列表
    function addItems(pageSize, pageIndex) {
        // 为url拼接查询条件,空值则默认去掉该条件，有则按照该条件查询
        var url = listUrl + '?' + 'pageIndex=' + pageIndex + '&pageSize='
            + pageSize + '&productCategoryId=' + productCategoryId
            + '&productName=' + productName + '&shopId=' + shopId;
        //加载标识符，若为true说明正在加载，避免出现重复加载
        loading = true;
        $.getJSON(url, function(data) {
            if (data.success) {
                //获取此次查询条件下的总商品条数
                maxItems = data.count;
                var html = '';
                //遍历并拼接商品信息列表
                data.productList.map(function(item, index) {
                    html += '' + '<div class="card" data-product-id='
                        + item.productId + '>'
                        + '<div class="card-header">' + item.productName
                        + '</div>' + '<div class="card-content">'
                        + '<div class="list-block media-list">' + '<ul>'
                        + '<li class="item-content">'
                        + '<div class="item-media">' + '<img src="'
                        + item.imgAddr + '" width="44">' + '</div>'
                        + '<div class="item-inner">'
                        + '<div class="item-subtitle">' + item.productDesc
                        + '</div>' + '</div>' + '</li>' + '</ul>'
                        + '</div>' + '</div>' + '<div class="card-footer">'
                        + '<p class="color-gray">'
                        + new Date(item.lastEditTime).Format("yyyy-MM-dd")
                        + '更新</p>' + '<span>点击查看</span>' + '</div>'
                        + '</div>';
                });
                $('.list-div').append(html);
                //获取当前列表的显示条数
                var total = $('.list-div .card').length;
                //若true则说明已显示全部商品信息
                if (total >= maxItems) {
                    /*
                        // 加载完毕，则注销无限加载事件，以防不必要的加载
                        $.detachInfiniteScroll($('.infinite-scroll'));
                    */
                    // 隐藏加载提示符
                    $('.infinite-scroll-preloader').hide();
                }else {
                    $('.infinite-scroll-preloader').show();
                }
                //否则页码+1，可以再次加载内容
                pageNum += 1;
                loading = false;
                //刷新页面，显示加载的新商品
                $.refreshScroller();
            }
        });
    }

    //下滑自动分页搜索(默认距离底部50像素刷新)
    $(document).on('infinite', '.infinite-scroll-bottom', function() {
        if (loading)
            return;
        addItems(pageSize, pageNum);
    });

    //选择新的商品类别后，重置页码，清空商品列表，按新类别查询商品信息
    $('#shopdetail-button-div').on('click','.button',
        function(e) {
            productCategoryId = e.target.dataset.productSearchId;
            if (productCategoryId) {
                if ($(e.target).hasClass('button-fill')) {
                    $(e.target).removeClass('button-fill');
                    productCategoryId = '';
                } else {
                    $(e.target).addClass('button-fill').siblings()
                        .removeClass('button-fill');
                }
                //商品列表清空并重新查询
                $('.list-div').empty();
                pageNum = 1;
                addItems(pageSize, pageNum);
            }
        });

    //点击商品卡片进入详情页面
    $('.list-div').on('click','.card',
            function(e) {
                var productId = e.currentTarget.dataset.productId;
                window.location.href = '/frontend/productdetail?productId='+ productId;
            });

    //搜索栏输入名称按名称查询商品信息，数据处理同选择商品类别
    $('#search').on('change', function(e) {
        productName = e.target.value;
        $('.list-div').empty();
        pageNum = 1;
        addItems(pageSize, pageNum);
    });

    //打开右侧栏
    $('#me').click(function() {
        $.openPanel('#panel-left-demo');
    });
    $.init();
});
