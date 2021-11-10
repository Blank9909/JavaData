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
    <link href="${ctx}//bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
    <!-- FontAwesome 4.3.0 -->
    <link href="${ctx}//bootstrap/css/font-awesome.min.css" rel="stylesheet" type="text/css"/>
    <!-- Ionicons 2.0.0 -->
    <link href="https://code.ionicframework.com/ionicons/2.0.1/css/ionicons.min.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}//dist/css/AdminLTE.min.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}//dist/css/skins/_all-skins.min.css" rel="stylesheet" type="text/css"/>
    <!--iCheck -->
    <link href="${ctx}//plugins/iCheck/flat/blue.css" rel="stylesheet" type="text/css"/>
    <link rel="stylesheet" href="${ctx}/css/fileinput.css"></link>
    <!-- jQuery 2.1.4 -->
    <script src="${ctx}//plugins/jQuery/jQuery-2.1.4.min.js"></script>
    <script src="${ctx}//js/global.js"></script>
    <script src="${ctx}//js/myFormValidate.js"></script>
    <script src="${ctx}//bootstrap/js/bootstrap.min.js" type="text/javascript"></script>
    <script src="${ctx}//js/layer/layer-min.js"></script><!-- 弹窗js 参考文档 http://layer.layui.com/-->
    <script src="${ctx}//js/myAjax.js"></script>

    <script type="text/javascript" src="${ctx}/js/fileinput.js"></script>
    <!-- 对中文的支持 -->
    <script type="text/javascript" src="${ctx}/js/fileinput_locale_zh.js"></script>
<body style="background-color:#ecf0f5;">


<div class="wrapper">
    <div class="breadcrumbs" id="breadcrumbs">
        <ol class="breadcrumb">
            <li><a href=""><i class="fa fa-home"></i>&nbsp;&nbsp;后台首页</a></li>

            <li><a href="">商品管理</a></li>
            <li><a href="">添加修改分类</a></li>
        </ol>
    </div>

    <section class="content">
        <div class="row">
            <div class="col-sm-12">
                <div class="box">
                    <div class="box-header">
                        <h3 class="box-title">增加分类</h3>
                        <div class="pull-right">
                            <a href="javascript:history.go(-1)" data-toggle="tooltip" title="" class="btn btn-default"
                               data-original-title="返回"><i class="fa fa-reply"></i></a>
                            <a href="javascript:;" class="btn btn-default"
                               data-url="http://www.ego.cn/Doc/Index/article/id/1006/developer/user.html"
                               onclick="get_help(this)"><i class="fa fa-question-circle"></i> 帮助</a>
                        </div>
                    </div>

                    <!-- /.box-header -->
                    <form method="post" class="form-horizontal" id="category_form">
                        <input name="level" id="level" type="hidden" value="1"/>
                        <input name="parentId" id="parentId" value="0" type="hidden"/>
                        <div class="box-body">
                            <div class="form-group">
                                <label class="col-sm-2 control-label">分类名称</label>
                                <div class="col-sm-6">
                                    <input type="text" placeholder="名称" class="form-control large" name="name" value="">
                                    <span class="help-inline" style="color:#F00; display:none;" id="err_name"></span>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="control-label col-sm-2">手机分类名称</label>
                                <div class="col-sm-6">
                                    <input type="text" placeholder="手机分类名称" class="form-control large" name="mobileName"
                                           value="">
                                    <span class="help-inline" style="color:#F00; display:none;"
                                          id="err_mobile_name"></span>
                                </div>
                            </div>
                            <div class="form-group">
                                <label0 class="control-label col-sm-2">上级分类</label0>

                                <div class="col-sm-3">
                                    <select name="parent_id_1" id="parent_id_1"
                                            onchange="getCategory(this.value,'parent_id_2','0');"
                                            class="small form-control">
                                        <option value="0">请选择分类</option>
                                        <#if gcList??>
                                            <#list gcList as gc>
                                                <option value="${gc.id}">${gc.name}</option>
                                            </#list>
                                        </#if>
                                    </select>
                                </div>
                                <div class="col-sm-3">
                                    <select name="parent_id_2" id="parent_id_2"
                                            class="small form-control" onchange="setParentIdAndLevel(this.value)">
                                        <option value=''>请选择分类</option>
                                    </select>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="control-label col-sm-2">导航显示</label>

                                <div class="col-sm-10">
                                    <label>
                                        <input checked="checked" type="radio" name="is_show" value="1"> 是
                                        <input type="radio" name="isShow" value="0"> 否

                                    </label>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="control-label col-sm-2">分类分组:</label>

                                <div class="col-sm-1">
                                    <select name="catGroup" id="cat_group" class="form-control">
                                        <option value="0">0</option>
                                        <option value='1'>1</option>
                                        "
                                        <option value='2'>2</option>
                                        "
                                        <option value='3'>3</option>
                                        "
                                        <option value='4'>4</option>
                                        "
                                        <option value='5'>5</option>
                                        "
                                        <option value='6'>6</option>
                                        "
                                        <option value='7'>7</option>
                                        "
                                        <option value='8'>8</option>
                                        "
                                        <option value='9'>9</option>
                                        "
                                        <option value='10'>10</option>
                                        "
                                        <option value='11'>11</option>
                                        "
                                        <option value='12'>12</option>
                                        "
                                        <option value='13'>13</option>
                                        "
                                        <option value='14'>14</option>
                                        "
                                        <option value='15'>15</option>
                                        "
                                        <option value='16'>16</option>
                                        "
                                        <option value='17'>17</option>
                                        "
                                        <option value='18'>18</option>
                                        "
                                        <option value='19'>19</option>
                                        "
                                        <option value='20'>20</option>
                                        "
                                    </select>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="control-label col-sm-2">分类展示图片</label>

                                <div class="col-sm-10" style="width:900px;height: 400px">

                                    <form enctype="multipart/form-data">
                                        <input id="file-goods-category" class="file" name="file" type="file"
                                               data-min-file-count="1">
                                    </form>
                                    <input type="hidden" value="" name="image" id="image" class="form-control large"
                                           readonly="readonly" style="width:500px;display:initial;"/>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="control-label col-sm-2">显示排序</label>
                                <div class="col-sm-1">
                                    <input type="text" placeholder="50" class="form-control large" name="sortOrder"
                                           value=""/>
                                    <span class="help-inline" style="color:#F00; display:none;"
                                          id="err_sort_order"></span>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="control-label col-sm-2">分佣比例</label>
                                <div class="col-sm-1">
                                    <input type="text" placeholder="50" class="form-control large" name="commissionRate"
                                           id="commission_rate" value="0"
                                           onpaste="this.value=this.value.replace(/[^\d.]/g,'')"
                                           onkeyup="this.value=this.value.replace(/[^\d.]/g,'')"/>
                                </div>
                                <div class="col-sm-1" style="margin-top: 6px;margin-left: -20px;">
                                    <span>%</span>
                                </div>
                            </div>
                        </div>
                        <div class="box-footer">
                            <input type="hidden" name="id" value="">
                            <button type="reset" class="btn btn-primary pull-left"><i class="icon-ok"></i>重填</button>
                            <button type="button" onclick="ajaxSubmitForm();" class="btn btn-primary pull-right"><i
                                        class="icon-ok"></i>提交
                            </button>
                        </div>
                        <input type="hidden" name="__hash__"
                               value="3089b9badfca8307d7d520487d125ae4_6385f66dff50b4c04db3ec79b8a9d245"/></form>
                </div>
            </div>
        </div>
    </section>
</div>
<script>
    function getCategory(parentId, next) {
        var html = "<option value='0'>请选择商品分类</option>";

        if (parentId == 0) {
            // level  一级分类
            $("#level").val(1);
            $("#parentId").val(0);
            $("#" + next).html(html);
            return;
        }

        $.ajax({
            type: "post",
            url: ctx + "/goodsCategory/queryGoodsCategoriesByParentId",
            data: {
                parentId: parentId
            },
            dataType: "json",
            success: function (data) {
                if (null != data && data.length > 0) {
                    for (var i = 0; i < data.length; i++) {
                        html = html + "<option value='" + data[i].id + "'>" + data[i].name + "</option>";
                    }
                }
                $("#" + next).html(html);
                // 设置分类级别为2级分类
                $("#level").val(2);
                $("#parentId").val(parentId);
            }
        })
    }


    /**
     * 判断level 以及 上级id
     * @param parentId
     */
    function setParentIdAndLevel(parentId) {
        if (parentId == 0) {
            $("#parentId").val($("#parent_id_1").val());
            $("#level").val(2);
            return;
        }
        $("#parentId").val(parentId);
        $("#level").val(3);
    }


    function ajaxSubmitForm() {
        $.ajax({
            type: "post",
            url: ctx + "/goodsCategory/save",
            data: $("#category_form").serialize(),
            dataType: "json",
            success: function (data) {
                if (data.code == 200) {
                    window.location.href = ctx + "/goodsCategory/index";
                } else {
                    alert(data.msg);
                }
            }
        })
    }


    /////////////////////////////////////
    /**
     * 文件上传插件配置
     */
    /////////////////////////////////////
    /**
     * 初始设置
     *    language指定语言
     *    uploadUrl指定文件上传的后台地址
     *    allowedPreviewTypes允许上传文件的类型
     */
    $('#file-goods-category').fileinput({
        language: 'zh',
        uploadUrl: '${ctx}/fileUpload/save',
        allowedPreviewTypes: ['image', 'html', 'text', 'video', 'audio', 'flash']
    });
    /**
     * 上传文件失败后 调用方法（回调函数）
     */
    $('#file-goods-category').on('fileuploaderror', function (event, data, previewId, index) {
        var form = data.form,
            files = data.files, e
        xtra = data.extra,
            response = data.response,
            reader = data.reader;
        console.log(data);
        console.log('File upload error');
    });
    /**
     * 文件错误 比如文件类型错误 调用方法（回调函数）
     */
    $('#file-goods-category').on('fileerror', function (event, data) {
        console.log(data.id);
        console.log(data.index);
        console.log(data.file);
        console.log(data.reader);
        console.log(data.files);
    });
    /**
     * 文件上传成功后 调用方法（回调函数）
     */
    $('#file-goods-category').on('fileuploaded', function (event, data, previewId, index) {
        var form = data.form,
            files = data.files,
            extra = data.extra,
            response = data.response,
            reader = data.reader;
        // 服务器文件地址
        // alert(data.response.fileUrl);
        // 将服务器文件地址设置至隐藏域
        $("#image").val(data.response.fileUrl);
        console.log('File uploaded triggered');
    });


</script>

</body>
</html>