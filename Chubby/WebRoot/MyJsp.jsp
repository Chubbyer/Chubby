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
				var someDay = new Date(+new Date("2017-05-20") + 24 * 3600 * 1000);
				alert(someDay.getMonth());
				var days = [];
				for (var i = 0; i < 14; i++) {
					var day = [ someDay.getFullYear(), someDay.getMonth()+1,
							someDay.getDate() ].join('-');
							//alert(day);
					days.push(day);
					someDay = new Date(+someDay + 24 * 3600 * 1000);
				}
				alert(days)
				function diffQuotient(data, k) {
					var points = [];
					points = data;
					var quotient = [];
					for (var i = 0; i < points.length - 1; i++) {
						quotient.push((points[i + 1] - points[i]) / k);
					}
					return quotient;
				}
				function result(r, y0, quotientTable) {
					var sum = y0;
					for (var i = 0; i < quotientTable.length; i++) {
						var temp = 1;
						for (var j = 0; j < i + 1; j++) {
							//r = r - j;
							temp = temp * (r - j);
						}
						sum += quotientTable[i] * temp;
						//alert(quotientTable[i]+"*"+temp);
					}
					return sum;
				}
				var points = [ 9, 4, 1, 0, 1, 4, 9 ];
				var table = [];
				var mon = [];
				var tues = [];
				for (var i = 0; i < points.length;) {
					mon.push(points[i++]);//星期一
					//tues.push(points[i++]);//星期二
					//table[2].push(points[i++]);
					//table[3].push(points[i++]);
					//table[4].push(points[i++]);
					//table[5].push(points[i++]);
					//table[6].push(points[i++]);
				}
				table.push(mon);
				//table.push(tues);
				var quotientTables = [];//总差商表
				for (var i = 0; i < table.length; i++) {
					var quotient = table[i];
					var quotientTable = [];
					//alert(table[i]);
					for (var j = 0; j < table[i].length - 1; j++) {
						quotient = diffQuotient(quotient, j + 1);
						quotientTable.push(quotient[0]);//某天的差商表
					}
					quotientTables.push(quotientTable);
					//alert(quotientTable);
					var k = result(-2, table[i][0], quotientTable);
					//alert(k);
				}

			});
</script>
</head>

<body>
	This is my JSP page.
	<br>
</body>
</html>
