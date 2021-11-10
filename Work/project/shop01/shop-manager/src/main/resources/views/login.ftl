<!-- 设置项目根路径全局变量 -->
<#assign ctx=request.contextPath/>
<script type="text/javascript">
    var ctx="${ctx}";
</script>
<html>
  <head>
    <meta charset="UTF-8">
    <title>ego | 后台登陆</title>
    <!-- Tell the browser to be responsive to screen width -->
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
    <!-- Bootstrap 3.3.4 -->
    <link href="${ctx}/bootstrap/css/bootstrap.css" rel="stylesheet" type="text/css" />
    <!-- Theme style -->
    <link href="${ctx}/dist/css/AdminLTE.css" rel="stylesheet" type="text/css" />
    <!-- iCheck -->
    <link href="${ctx}/plugins/iCheck/square/blue.css" rel="stylesheet" type="text/css" />
    <style>#imgVerify{width: 120px;margin: 0 auto; text-align: center;display: block;}	</style>
    <script>    
    function detectBrowser()
    {
	    var browser = navigator.appName
	    if(navigator.userAgent.indexOf("MSIE")>0){ 
		    var b_version = navigator.appVersion
			var version = b_version.split(";");
			var trim_Version = version[1].replace(/[ ]/g,"");
		    if ((browser=="Netscape"||browser=="Microsoft Internet Explorer"))
		    {
		    	if(trim_Version == 'MSIE8.0' || trim_Version == 'MSIE7.0' || trim_Version == 'MSIE6.0'){
		    		alert('请使用IE9.0版本以上进行访问');
		    		return;
		    	}
		    }
	    }
   }
    detectBrowser();
   </script>
  <body class="login-page">
    <div class="login-box">
      <div class="login-logo">
        <a href="#"><b>ego</b></a>
      </div>
      <div class="login-box-body">
        <p class="login-box-msg">管理后台</p>
          <div class="form-group has-feedback">
            <input type="text" name="username" id="username" class="form-control" placeholder="账号" />
            <span class="glyphicon glyphicon-envelope form-control-feedback"></span>
          </div>
          <div class="form-group has-feedback">
            <input type="password" name="password" class="form-control" id="password" placeholder="密码" />
            <span class="glyphicon glyphicon-lock form-control-feedback"></span>
          </div>
          <div class="form-group has-feedback">
          	<opinioncontrol realtime="true" opinion_name="vertify_code" default="true">
                <div class="row" style="padding-right: 65px;">
                    <div class="col-xs-8">
                        <input style="width: 135px" type="text" name="vertify" id="vertify" class="form-control" placeholder="验证码"/>
                    </div>
                    <div class="col-xs-4">
                        <img id="imgVerify" style="cursor:pointer;" src="${ctx}/image/getKaptchaImage"
														alt="点击更换" title="点击更换"/>
                    </div>
                </div>
            </opinioncontrol>
          </div>

          <div class="form-group">
          	 <button type="button" class="btn btn-primary btn-block btn-flat" onclick="checkLogin()">立即登陆 </button>  
          </div>
      </div>
      
	    <div class="margin text-center">
	        <div class="copyright">
	            2014-2016 &copy; <a href="http://www.ego.cn">ego v1.3.3</a>
	            <br/>
	            <a href="http://www.ego.cn">北京506网络有限公司</a>出品
	        </div>
	    </div>
    </div><!-- /.login-box -->
    <!-- jQuery 2.1.4 -->
    <script src="${ctx}/plugins/jQuery/jQuery-2.1.4.min.js" type="text/javascript"></script>
    <!-- Bootstrap 3.3.2 JS -->
    <script src="${ctx}/bootstrap/js/bootstrap.js" type="text/javascript"></script>
    <!-- iCheck -->
    <script src="${ctx}/plugins/iCheck/icheck.js" type="text/javascript"></script>
	<script src="${ctx}/js/layer/layer.js"></script><!-- 弹窗js 参考文档 http://layer.layui.com/-->
    <script>
	   function checkLogin(){
	     var userName= $("#username").val();
	     var password=$("#password").val();
	     var imageCode = $("#vertify").val();
	     $.ajax({
           type:"post",
           url:ctx+"/user/doLogin",
           data:{
             userName:userName,
             password:password,
               imageCode:imageCode
           },
           dataType:"json",
           success:function (data){
              if(data.code==200){
                  window.location.href=ctx+"/index";
              }else{
                alert(data.message);
              }
           }
         })
       }
    </script>
  </body>
</html>