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

<title>Chubby</title>
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

<link rel="stylesheet" type="text/css" href="css/IndexPageCSS.css">
<script src="js/IndexPageJS.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		actionBack();
		$("#search").keydown(function(e) {
			if (e.keyCode == 13) {
				//alert($("#search").val());
				//$("form").submit();
				//var url = "PersonOverview?serach=" + $("#search").val();
				//$.get(url);
				//window.open('baidu.com');
			}
		});
	});
</script>

</head>
<body>
	<div class="container-fluid">
		<div class="row title">
			<h1>Chubby</h1>
			<br>
			<form method="post" action="Person">
				<input name="search" type="search" placeholder="试着输入你的学号或姓名">
			</form>
			<div class="row"><span style="color:#fff">${requestScope.errorInfo}</span></div>
			<div class="row"><h3>MAY.02 - 2017</h3></div>
			<br>
			<div class="row hotTopic">
				<div class="row">
					<a rel="license" href="UseRanking.jsp" target="_blank">
						<button type="button" class="btn btntopic btn-primary">使用时间排行榜</button>
					</a> <a rel="license" href="Distribut.jsp" target="_blank">
						<button type="button" class="btn btntopic btn-success">开关机分布</button>
					</a>
					<button type="button" class="btn btntopic btn-info">LeungJain</button>
					<button type="button" class="btn btntopic btn-warning">（警告）Warning</button>
				</div>
				<div class="row">
					<br>
				</div>
				<div class="row">
					<button type="button" class="btn btntopic btn-default">Default</button>
					<button type="button" class="btn btntopic btn-info">Info</button>
					<button type="button" class="btn btntopic btn-primary">Primary</button>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
