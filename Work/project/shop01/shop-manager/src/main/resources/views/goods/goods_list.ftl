<!-- 设置项目根路径全局变量 -->
<#assign ctx=request.contextPath/>
<script type="text/javascript">
    var ctx = "${ctx}";
</script>
<html>
<head>
    <meta charset="UTF-8">
    <title>ego管理后台</title>
    <!-- Tell the browser to be responsive to screen width -->
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <!-- Bootstrap 3.3.4 -->
    <link href="${ctx}/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css">
    <!-- FontAwesome 4.3.0 -->
    <link href="${ctx}/bootstrap/css/font-awesome.min.css" rel="stylesheet" type="text/css">
    <!-- Ionicons 2.0.0 --
    <link href="https://code.ionicframework.com/ionicons/2.0.1/css/ionicons.min.css" rel="stylesheet" type="text/css" />
    <!-- Theme style -->
    <link href="${ctx}/dist/css/AdminLTE.min.css" rel="stylesheet" type="text/css">
    <!-- AdminLTE Skins. Choose a skin from the css/skins 
    	folder instead of downloading all of them to reduce the load. -->
    <link href="${ctx}/dist/css/skins/_all-skins.min.css" rel="stylesheet" type="text/css">
    <!-- iCheck -->
    <link href="${ctx}/plugins/iCheck/flat/blue.css" rel="stylesheet" type="text/css">
    <!-- jQuery 2.1.4 -->
    <script src="${ctx}/plugins/jQuery/jQuery-2.1.4.min.js"></script>
    <script src="${ctx}/js/global.js"></script>
    <script src="${ctx}/js/myFormValidate.js"></script>
    <script src="${ctx}/bootstrap/js/bootstrap.min.js" type="text/javascript"></script>
    <script src="${ctx}/js/layer/layer-min.js"></script>
    <link rel="stylesheet" href="${ctx}/js/layer/skin/layer.css" id="layui_layer_skinlayercss" style="">
    <!-- 弹窗js 参考文档 http://layer.layui.com/-->
    <script src="${ctx}/js/myAjax.js"></script>
    <script src="${ctx}/js/doT.min.js"></script>
</head>
<body style="background-color:#ecf0f5;">


<div class="wrapper">
    <div class="breadcrumbs" id="breadcrumbs">
        <ol class="breadcrumb">
            <li><a href="javascript:void();"><i class="fa fa-home"></i>&nbsp;&nbsp;后台首页</a></li>

            <li><a href="javascript:void();">商品管理</a></li>
            <li><a href="javascript:void();">商品列表</a></li>
        </ol>
    </div>

    <style>#search-form > .form-group {
            margin-left: 10px;
        }</style>
    <!-- Main content -->
    <section class="content">
        <div class="container-fluid">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title"><i class="fa fa-list"></i> 商品列表</h3>
                </div>
                <div class="panel-body">
                    <div class="navbar navbar-default">
                        <form action="" id="search-form2" class="navbar-form form-inline" method="post"
                              onsubmit="return false">
                            <div class="form-group">
                                <select name="catId" id="catId" class="form-control"
                                        onchange="ajax_get_table(1)">
                                    <option value="">所有分类</option>
                                    <#list gcList as gc>
                                        <option value="${gc.id}">${gc.name}</option>
                                    </#list>
                                </select>
                            </div>
                            <div class="form-group">
                                <select name="brandId" id="brandId"
                                        class="form-control" onchange="ajax_get_table(1)">
                                    <option value="">所有品牌</option>
                                    <#list brList as br>
                                        <option value="${br.id}">${br.name}</option>
                                    </#list>
                                </select>
                            </div>

                            <div class="form-group">
                                <select name="isOnSale" id="isOnSale"
                                        class="form-control" onchange="ajax_get_table(1)">
                                    <option value="">全部</option>
                                    <option value="1">上架</option>
                                    <option value="0">下架</option>
                                </select>
                            </div>
                            <div class="form-group">
                                <select name="flag" id="flag"
                                        class="form-control" onchange="ajax_get_table(1)">
                                    <option value="">全部</option>
                                    <option value="1">新品</option>
                                    <option value="2">推荐</option>
                                </select>
                            </div>
                            <br>
                            <br>
                            <div class="form-group">
                                <label class="control-label" for="input-order-id">关键词</label>
                                <div class="input-group">
                                    <input name="keywords" value="" placeholder="搜索词" id="keywords"
                                           class="form-control" type="text">
                                </div>
                            </div>
                            <!--排序规则-->
                            <input name="orderby1" value="goods_id" type="hidden">
                            <input name="orderby2" value="desc" type="hidden">
                            <button type="submit" onclick="ajax_get_table(1)"
                                    id="button-filter search-order" class="btn btn-primary"><i class="fa fa-search"></i>
                                筛选
                            </button>
                            <button type="button" onclick="location.href='${ctx}/goods/add/page'"
                                    class="btn btn-primary pull-right"><i class="fa fa-plus"></i>添加新商品
                            </button>
                            <input name="__hash__"
                                   value="934c3c704c4bed5cb4da6cec6353613a_2e4eb53d2afc41d11040df3ef57fa1ca"
                                   type="hidden"></form>
                    </div>
                    <div id="ajax_return">
                        <form method="post" enctype="multipart/form-data" target="_blank" id="form-order">
                            <div class="table-responsive">
                                <table class="table table-bordered table-hover">
                                    <thead>
                                    <tr>
                                        <td style="width: 1px;" class="text-center">
                                        </td>
                                        <td class="text-right">
                                            <a href="javascript:sort('goods_id');">ID</a>
                                        </td>
                                        <td class="text-left">
                                            <a href="javascript:sort('goods_name');">商品名称</a>
                                        </td>
                                        <td class="text-left">
                                            <a href="javascript:sort('goods_sn');">货号</a>
                                        </td>
                                        <td class="text-left">
                                            <a href="javascript:sort('cat_id');">分类</a>
                                        </td>
                                        <td class="text-left">
                                            <a href="javascript:sort('shop_price');">价格</a>
                                        </td>
                                        <td class="text-left">
                                            <a href="javascript:void(0);">库存</a>
                                        </td>
                                        <td class="text-center">
                                            <a href="javascript:sort('sort');">排序</a>
                                        </td>
                                        <td class="text-center" style="width:135px;">操作</td>
                                    </tr>
                                    </thead>
                                    <tbody id="goodsContent">
                                    </tbody>
                                </table>
                            </div>
                        </form>



                        <div class="row">
                            <div class="col-sm-3 text-left"></div>
                            <div class="col-sm-9 text-right">
                                <div class="dataTables_paginate paging_simple_numbers">
                                    <ul class="pagination" id="pageContent">

                                    </ul>
                                </div>
                            </div>
                        </div>
                        <script>
                            // 点击分页触发的事件
                            $(".pagination  a").click(function () {
                                cur_page = $(this).data('p');
                                ajax_get_table('search-form2', cur_page);
                            });

                            /*
                             * 清除静态页面缓存
                             */
                            function ClearGoodsHtml(goods_id) {
                                $.ajax({
                                    type: 'GET',
                                    url: "/index/Admin/System/ClearGoodsHtml",
                                    data: {goods_id: goods_id},
                                    dataType: 'json',
                                    success: function (data) {
                                        layer.alert(data.msg, {icon: 2});
                                    }
                                });
                            }

                            /*
                             * 清除商品缩列图缓存
                             */
                            function ClearGoodsThumb(goods_id) {
                                $.ajax({
                                    type: 'GET',
                                    url: "/index/Admin/System/ClearGoodsThumb",
                                    data: {goods_id: goods_id},
                                    dataType: 'json',
                                    success: function (data) {
                                        layer.alert(data.msg, {icon: 2});
                                    }
                                });
                            }
                        </script>
                    </div>
                </div>
            </div>
        </div>
        <!-- /.row -->
    </section>
    <!-- /.content -->
</div>
<!-- /.content-wrapper -->


<!-- 编写商品模板 -->
<script type="template" id="goodsTemplate">
    {{ for(var i = 0; i < it.length; i++){ }}
    <tr>
        <td class="text-center">
            <input name="shipping_code[]" value="flat.flat" type="hidden">
        </td>
        <td class="text-right">{{=it[i].goodsId}}</td>
        <td class="text-left">{{=it[i].goodsName}}</td>
        <td class="text-left">{{=it[i].goodsSn}}</td>
        <td class="text-left">{{=it[i].catId}}</td>
        <td class="text-left">{{=it[i].shopPrice}}</td>
        <td class="text-left">
            <input onkeyup="this.value=this.value.replace(/[^\d.]/g,'')"
                   onpaste="this.value=this.value.replace(/[^\d.]/g,'')"
                   onchange="ajaxUpdateField(this);" name="store_count" size="4"
                   data-table="goods" data-id="143" value="{{=it[i].storeCount}}" type="text">
        </td>
        <td class="text-center">
            <input onkeyup="this.value=this.value.replace(/[^\d]/g,'')"
                   onpaste="this.value=this.value.replace(/[^\d]/g,'')"
                   onchange="updateSort('goods','goods_id','143','sort',this)" size="4"
                   value="{{=it[i].sort}}" type="text">
        </td>
        <td class="text-center">
            <a target="_blank" href="/index/Home/Goods/goodsInfo/id/143"
               class="btn btn-info" title="查看详情"><i class="fa fa-eye"></i></a>
            <a href="商品列表-添加新商品.html" class="btn btn-primary" title="编辑"><i
                        class="fa fa-pencil"></i></a>
            <a href="javascript:void(0);" onclick="del('143')" class="btn btn-danger"
               title="删除"><i class="fa fa-trash-o"></i></a>
        </td>
    </tr>
    {{ } }}
</script>


<!-- 编写分页模板 -->
<script type="template" id="pageTemplate">
    {{ if(it.hasPreviousPage){ }}
    <li class="paginate_button prev">
        <a href="javascript:ajax_get_table('{{=it.prePage}}');">上一页</a>
    </li>
    {{ } }}

    {{ for(var i = 1; i <= it.navigatepageNums.length; i++){ }}
    <li class="paginate_button
        {{ if(it.navigatepageNums[i-1] == it.pageNum){ }}
        active
        {{ } }}
        ">
        <a href="javascript:ajax_get_table('{{=it.navigatepageNums[i-1]}}');">{{=it.navigatepageNums[i-1]}}</a>
    </li>
    {{ } }}

    {{ if(it.hasNextPage){ }}
    <li class="paginate_button next">
        <a href="javascript:ajax_get_table('{{=it.nextPage}}');">下一页</a>
    </li>
    {{ } }}
</script>


<script type="text/javascript">

    $(function (){
        // ajax 加载商品列表
        ajax_get_table(1);
    })

    //ajax抓取页面 page为当前第几页
    function ajax_get_table(page) {
        $.ajax({
            url: "${ctx}/goods/queryGoodsForListPage",
            type: "POST",
            data: {
                pageNum: page,
                pageSize: 10,
                catId:$("#catId").val(),
                brandId:$("#brandId").val(),
                isOnSale:$("#isOnSale").val(),
                flag:$("#flag").val(),
                keywords:$("#keywords").val()
            },
            dataType: "JSON",
            success: function (result) {
                if (200 == result.code) {
                    if (result.pageInfo.list.length > 0) {
                        //获取商品列表模板
                        var goodsTemp = doT.template($("#goodsTemplate").text());
                        //填充数据
                        $("#goodsContent").html(goodsTemp(result.pageInfo.list));

                        //获取分页模板
                        var pageTemp = doT.template($("#pageTemplate").text());
                        //填充数据
                        $("#pageContent").html(pageTemp(result.pageInfo));
                    } else {
                        layer.msg("该分类或品牌暂无商品信息！");
                    }
                } else {
                    layer.msg("该分类或品牌暂无商品信息！");
                }
            },
            error: function (result) {
                console.log(result)
            }
        });
    }



</script>

</body>
</html>