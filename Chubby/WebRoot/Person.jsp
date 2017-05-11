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

<title>PC使用报告</title>

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
<link rel="stylesheet" type="text/css" href="css/PersonCSS.css">
<script src="js/echarts.common.min.js"></script>
<script src="js/PersonShow.js"></script>
<script type="text/javascript">
	$(document).ready(
			function() {
				// 基于准备好的dom，初始化echarts实例
				//alert("adad");
				//var myChart1 = echarts.init(document.getElementById('myChart1'));
				var myChart1 = echarts
						.init(document.getElementById("myChart1"));
				// 显示标题，图例和空的坐标轴
				myChart1.setOption({
					title : {
						text : '异步数据加载示例'
					},
					tooltip : {
						trigger : 'axis',
						axisPointer : {
							type : 'cross'
						}
					},
					legend : {
						data : [ '使用时间' ]
					},
					toolbox : {
						show : true,
						feature : {
							magicType : {
								type : [ 'line', 'bar' ]
							},
							saveAsImage : {}
						}
					},
					xAxis : {
						type : 'category',
						data : [],
						name : '日期'
					},
					yAxis : {
						type : 'value',
						axisLabel : {
							formatter : '{value} H'
						}
					},
					dataZoom : [ {
						type : 'slider',
						start : 60,
						end : 100
					} ],
					series : [ {
						name : '使用时间',
						type : 'bar',
						data : []
					} ]
				});

				// 异步加载数据
				$.get("PersonOverview?oType=301").done(
						function(data) {
							//alert(data);
							// 填入数据
							myChart1.setOption({
								xAxis : {
									data : [ "2017/04/12", "2017/04/13",
											"2017/04/14", "2017/04/15",
											"2017/04/16", "2017/04/17",
											"2017/04/18", "2017/04/19",
											"2017/04/20", "2017/04/21",
											"2017/04/22", "2017/04/23" ]
								},
								series : [ {
									// 根据名字对应到相应的系列
									name : '使用时间',
									data : [ 5, 20, 18, 10, 10, 20, 7, 12, 17,
											11, 6, 13 ]
								} ]
							});
						});
				//ajaxData();
				//showChart1();
				showChart2();
				showChart3();
			});
</script>
</head>
<body>
	<div class="container-fluid">
		<div class="row">
			<div class="col-md-12" id="personheard">
				<div class="col-md-6 col-md-offset-2"
					style="margin-top:50px;color:#e0e0e0">
					<blockquote>
						<p id="heardtitle">
							欢迎您Leung，我们已根据您的PC日志，得出了这份报告,它可能<br>有助于你了解自己的使用习惯
						</p>
						<footer>Chubby Team <cite title="Source Title">Source
							Title</cite></footer>
					</blockquote>
				</div>
			</div>
		</div>
		<div class="row">
			<br>
			<div class="row">
				<div class="col-md-6 col-md-offset-2">
					<dl class="descreption">
						<dt>
							<span class="glyphicon glyphicon-th-large" aria-hidden="true"></span>&nbsp每一天累计使用的时间
						</dt>
						<dd>这是根据您每天的开关机时间计算得出的，并且预计了您未来一周可能使用的时间</dd>
					</dl>
				</div>
			</div>
			<div class="row">
				<div class="col-md-6 col-md-offset-3" id="myChart1"
					style="height:400px;"></div>
			</div>
			<div class="row">
				<div class="col-md-6 col-md-offset-3">
					<center>
						<p>共计使用500小时，如果你是博尔特，用这些时间你可以奔跑5000KM，相当于绕赤道3圈</p>
					</center>
				</div>
			</div>
		</div>
		<div class="row">
			<br>
			<div class="row">
				<div class="col-md-6 col-md-offset-2">
					<dl class="descreption">
						<dt>
							<span class="glyphicon glyphicon-th-large" aria-hidden="true"></span>&nbsp使用时间在一天中的分布
						</dt>
						<dd>这包括你在上午（12:00以前），下午（12:00-17:00），晚上（17:00以后）使用时间的分布</dd>
					</dl>
				</div>
			</div>
			<div class="row">
				<div class="col-md-6 col-md-offset-3" id="myChart2"
					style="height:400px;"></div>
			</div>
			<div class="row">
				<div class="col-md-6 col-md-offset-3">
					<center>
						<p>共计使用500小时，如果你是博尔特，用这些时间你可以奔跑5000KM，相当于绕赤道3圈</p>
					</center>
				</div>
			</div>
		</div>
		<div class="row">
			<br>
			<div class="row">
				<div class="col-md-6 col-md-offset-2">
					<dl class="descreption">
						<dt>
							<span class="glyphicon glyphicon-th-large" aria-hidden="true"></span>&nbsp开关机时间的分布
						</dt>
						<dd>在这里你可以看到具体的开关机的时点</dd>
					</dl>
				</div>
			</div>
			<div class="row">
				<div class="col-md-6 col-md-offset-3" id="myChart3"
					style="height:400px;"></div>
			</div>
			<div class="row">
				<div class="col-md-6 col-md-offset-3">
					<center>
						<p>共计使用500小时，如果你是博尔特，用这些时间你可以奔跑5000KM，相当于绕赤道3圈</p>
					</center>
				</div>
			</div>
		</div>
	</div>
	<div>
		<footer class="bs-docs-footer">
		<div class="container">
			<br>
			<ul class="bs-docs-footer-links">
				<li><a href="https://github.com/twbs/bootstrap">GitHub 仓库</a></li>
				<li><a href="../about/">关于</a></li>
			</ul>

			<p>
				Designed and built with all the love in the world by <a href=""
					target="_blank">@LeungJain</a> and <a href="" target="_blank">@WuFei</a>,<a
					href="" target="_blank">@WuShouzheng</a>,<a href="" target="_blank">@ZhouYu</a>.
				Maintained by the <a href="">core team</a> with the help of <a
					href="">our Teacher Mr.Lou</a>.
			</p>

			<p>
				感谢 <a rel="license" href="http://www.cqjtu.edu.cn" target="_blank">重庆交通大学</a>信息科学与工程学院计科4班所有同学提供的数据，本项目的所有实验数据可以在<a
					rel="license" href="https://" target="_blank">https://</a> 浏览。
			</p>

		</div>
		</footer>
	</div>
</body>
</html>
