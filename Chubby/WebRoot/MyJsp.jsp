<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
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

<title>My JSP 'MyJsp.jsp' starting page</title>

<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">
<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
<!-- 确保在移动设备可用 -->
<meta name="viewport" content="width=device-width, initial-scale=1">
<!-- 新 Bootstrap 核心 CSS 文件 -->
<link rel="stylesheet" href="./css/bootstrap.min.css">

<!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
<script src="./jQuery/jquery-3.1.1.min.js"></script>

<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
<script src="./js/bootstrap.min.js"></script>
<link rel="stylesheet" type="text/css" href="css/PersonCSS.css">
<script src="js/echarts.common.min.js"></script>
<script src="js/PersonShow.js"></script>
<script type="text/javascript">
	$(document).ready(
			function() {
			var open=[];
				var openPoints = [ [ "2017/02/20", 15.6 ],
						[ "2017/02/21", 9.8 ] ];
				var point = [];
				
				for (i = 0; i < openPoints.length; i++) {
					//point.push(new Date(openPoints[i][0]));
					//point.push(openPoints[i][1]);
					open.push([new Date(openPoints[i][0]),openPoints[i][1]])
				}
				alert(openPoints.length);
				alert(open);
			});
</script>
</head>

<body>
	This is my JSP page.
	<br>
</body>
</html>
