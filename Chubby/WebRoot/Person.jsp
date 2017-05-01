<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<base href="<%=basePath%>">

<title>My JSP 'Person.jsp' starting page</title>

<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">
<!-- 确保在移动设备可用 -->
<meta name="viewport" content="width=device-width, initial-scale=1">
<!-- 新 Bootstrap 核心 CSS 文件 -->
<link rel="stylesheet" href="./css/bootstrap.min.css">

<!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
<script src="./jQuery/jquery-3.1.1.min.js"></script>

<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
<script src="./js/bootstrap.min.js"></script>
<!--  <link rel="stylesheet" type="text/css" href="css/PersonCSS.css">-->
<script src="js/echarts.common.min.js"></script>
<script src="js/PersonShow.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		// 基于准备好的dom，初始化echarts实例
		showChart1();
		showChart2();
		showChart3();
		//var url = "PersonOverview?flag=yrue";
		//$.get(url, function(rpdata, status) {
		//$(".loader").hide();
		//var JSONObject = eval("(" + rpdata + ")");
		//$("#cc").html(rpdata);		
		//});		
	});
</script>
</head>
<body>
	<div class="container-fluid">
		<!--  <div class="loader">
			<span></span> <span></span> <span></span> <span></span> <span></span>
			<span></span> <span></span> <span></span> <span></span> <span></span>
			<span></span> <span></span> <span></span> <span></span> <span></span>
		</div>-->
		<div class="row">
			<div id="cc"></div>
			<div id="myChart1" style="width: 600px;height:400px;">vSBB</div>
		</div>
		<div class="row">
			<div id="main" style="width: 600px;height:400px;"></div>
		</div>
		<div class="row">
			<div id="myChart3" style="width: 600px;height:400px;"></div>
		</div>
	</div>
</body>
</html>
