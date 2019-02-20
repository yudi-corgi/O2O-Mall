$(function() {
    var loading = false; //判断当前店铺列表是否加载
    //分页允许返回的最大条数，超过则禁止访问后台
    var maxItems = 999;
    //一页显示最大条数
    var pageSize = 3;
    //页码
    var pageNum = 1;
    //获取店铺列表url
    var listUrl = '/frontend/listshops';
    //获取店铺类别及区域类别url
    var searchDivUrl = '/frontend/listshopspageinfo';
    //尝试从url获取parentId
    var parentId = getQueryString('parentId');
    var areaId = '';
    var shopCategoryId = '';
    var shopName = '';
    //渲染出店铺类别列表及区域列表以供搜索
    getSearchDivData();
    //预先加载10条店铺信息
    addItems(pageSize, pageNum);

    //获取店铺类别及区域信息
    function getSearchDivData() {
        //若有parentId,则取出一级类别下的二级类别
        var url = searchDivUrl + '?' + 'parentId=' + parentId;
        $.getJSON(url,function(data) {
                    if (data.success) {
                        //从后台获取店铺类别列表
                        var shopCategoryList = data.shopCategoryList;
                        var html = '';
                        html += '<a href="#" class="button" data-category-id=""> 全部类别  </a>';
                        //遍历列表，拼接出a标签
                        shopCategoryList.map(function(item, index) {
                                html += '<a href="#" class="button" data-category-id='
                                    + item.shopCategoryId
                                    + '>'
                                    + item.shopCategoryName
                                    + '</a>';
                            });
                        $('#shoplist-search-div').html(html);
                        //获取区域列表信息，拼接option标签
                        var selectOptions = '<option value="">全部街道</option>';
                        var areaList = data.areaList;
                        areaList.map(function(item, index) {
                            selectOptions += '<option value="'
                                + item.areaId + '">'
                                + item.areaName + '</option>';
                        });
                        $('#area-search').html(selectOptions);
                    }
                });
    }

    //获取分页展示的店铺列表信息
    function addItems(pageSize, pageIndex) {
        // 拼接查询的Url，空值则默认去掉该条件
        var url = listUrl + '?' + 'pageIndex=' + pageIndex + '&pageSize='
            + pageSize + '&parentId=' + parentId + '&areaId=' + areaId
            + '&shopCategoryId=' + shopCategoryId + '&shopName=' + shopName;
        //设定加载标识，若还在后台获取数据，则不能再次访问后台，避免多次重复加载
        loading = true;
        $.getJSON(url, function(data) {
            if (data.success) {
                //获取当前查询条件下店铺的总数
                maxItems = data.count;
                var html = '';
                data.shopList.map(function(item, index) {
                    html += '' + '<div class="card" data-shop-id="'
                        + item.shopId + '">' + '<div class="card-header">'
                        + item.shopName + '</div>'
                        + '<div class="card-content">'
                        + '<div class="list-block media-list">' + '<ul>'
                        + '<li class="item-content">'
                        + '<div class="item-media">' + '<img src="'
                        + item.shopImg + '" width="44">' + '</div>'
                        + '<div class="item-inner">'
                        + '<div class="item-subtitle">' + item.shopDesc
                        + '</div>' + '</div>' + '</li>' + '</ul>'
                        + '</div>' + '</div>' + '<div class="card-footer">'
                        + '<p class="color-gray">'
                        + new Date(item.lastEditTime).Format("yyyy-MM-dd")
                        + '更新</p>' + '<span>点击查看</span>' + '</div>'
                        + '</div>';
                });
                $('.list-div').append(html);
                //获取当前页面店铺列表总数
                var total = $('.list-div .card').length;
                if (total >= maxItems) {
                    /*
                        // 加载完毕，则注销无限加载事件，以防不必要的加载
                        $.detachInfiniteScroll($('.infinite-scroll'));
                    */
                    // 删除加载提示符
                    $('.infinite-scroll-preloader').hide();
                }else {
                    $('.infinite-scroll-preloader').show();
                }
                //若total<maxItems，则页码+1，加载新的店铺列表
                pageNum += 1;
                //标识加载结束，可以再次加载
                loading = false;
                //刷新页面，显示新加载的店铺
                $.refreshScroller();
            }
        });
    }

    //下滑屏幕自动进行分页搜索(默认是距离底部50像素更新)
    $(document).on('infinite', '.infinite-scroll-bottom', function() {
        if (loading)
            return;
        addItems(pageSize, pageNum);
    });

    //点击店铺的卡片进入详情页
    $('.shop-list').on('click', '.card', function(e) {
        var shopId = e.currentTarget.dataset.shopId;
        window.location.href = '/frontend/shopdetail?shopId=' + shopId;
    });

    //选择新的店铺类别后，重置页码，情况原先的店铺列表，按照新的类别去查询
    $('#shoplist-search-div').on('click','.button',
        function(e) {
            if (parentId) {// 如果传递过来的是一个父类下的子类
                shopCategoryId = e.target.dataset.categoryId;
                //若之前已选定了别的category，则移出其选定效果，改成新选定的(选中效果为蓝色)
                if ($(e.target).hasClass('button-fill')) {
                    $(e.target).removeClass('button-fill');
                    shopCategoryId = '';
                } else {
                    $(e.target).addClass('button-fill').siblings()
                        .removeClass('button-fill');
                }
                //查询条件改变，清空原先的店铺列表再查询
                $('.list-div').empty();
                pageNum = 1;
                addItems(pageSize, pageNum);
            } else {// 如果传递过来的父类为空，则按照父类查询(一级类别)
                parentId = e.target.dataset.categoryId;
                if ($(e.target).hasClass('button-fill')) {
                    $(e.target).removeClass('button-fill');
                    parentId = '';
                } else {
                    $(e.target).addClass('button-fill').siblings()
                        .removeClass('button-fill');
                }
                $('.list-div').empty();
                pageNum = 1;
                addItems(pageSize, pageNum);
                parentId = '';
            }

        });

    //需要查询的店铺名字发送变化后，重置页码，清空原先店铺列表，按照新名字查询
    $('#search').on('change', function(e) {
        shopName = e.target.value;
        $('.list-div').empty();
        pageNum = 1;
        addItems(pageSize, pageNum);
    });

    //区域信息发生变化后的处理，同上操作
    $('#area-search').on('change', function() {
        areaId = $('#area-search').val();
        $('.list-div').empty();
        pageNum = 1;
        addItems(pageSize, pageNum);
    });

    //点击"我"打开右侧栏
    $('#me').click(function() {
        $.openPanel('#panel-left-demo');
    });

    //初始化页面
    $.init();
});
