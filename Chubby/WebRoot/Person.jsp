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
	$(document).ready(function() {
		showChart1();
		webInfo();
		//showChart2();
		//showChart3();
	});
</script>
</head>
<body>
	<div class="container-fluid">
		<div class="row">
			<div class="col-md-12" id="personheard">
				<div class="col-md-1 col-md-offset-2"
					style="margin-top:50px;color: #9cd3aa;">
					<h1 style="font-weight: 600;font-size: 50px;line-height: 1;">Chubby</h1>
				</div>
				<div class="col-md-6"
					style="margin-top:50px;margin-left:100px;color:#e0e0e0">
					<blockquote>
						<p id="heardtitle">
							欢迎您${requestScope.useName}，我们已根据您的PC日志，得出了这份报告,它可能<br>有助于你了解自己的使用习惯
						</p>
						<footer>Info from <cite title="Source Title">Chubbyer
							Team</cite></footer>
					</blockquote>
				</div>
			</div>
		</div>
		<div class="row">
			<br>
			<div class="row">
				<div class="col-md-8 col-md-offset-2">
					<dl class="descreption">
						<dt>
							<span class="glyphicon glyphicon-th-large" aria-hidden="true"></span>&nbsp每一天累计使用的时间
						</dt>
						<dd>
							<br>
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;这是根据您每天的开关机时间计算得出的，并且预计了您未来一周可能使用的时间。您还可以根据榜单右上角的按钮查看数据视图、折线图，并且可以保存为图片，
							有任何疑问您还可以联系<a rel="license" href="http://www.cqjtu.edu.cn"
								target="_blank">Chubbyer</a>团队。
						</dd>
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
						<p id="commentary1"></p>
					</center>
				</div>
			</div>
			<div class="row">
				<div class="col-md-6 col-md-offset-3">
					<br>
					<button class="btn" type="button" id="forecast"
						style=" width:100%;background-color: #6a646a;color: #fff;">预测未来两周的使用时间</button>
				</div>
			</div>
			<div class="row" id="forecastChartDiv" style="display:none;">
				<br>
				<div class="row">
					<center>
						<div class="col-md-6 col-md-offset-3" id="forecastChart"
							style="height:400px; width:650px;"></div>
					</center>
				</div>
				<div class="row">
					<div class="col-md-6 col-md-offset-3">
						<center>
							<p>这里预测了你未来两周PC的使用时间</p>
						</center>
					</div>
				</div>
			</div>
			<div class="row">
				<div class="col-md-6 col-md-offset-3">
					<br>
					<button class="btn" type="button" id="webInfoBtn"
						style=" width:100%;background-color: #6a646a;color: #fff;">查看上网信息</button>
				</div>
			</div>
			<div class="row" id="webInfoDiv" style="display:none;">
				<br>
				<div class="row">
					<center>
						<div class="col-md-6 col-md-offset-3" id="webOnline"
							style="height:400px; width:650px;"></div>
					</center>
				</div>
				<div class="row">
					<div class="col-md-6 col-md-offset-3">
						<center>
							<p>根据您所有的上网记录，通过统计分析得出以上信息提供参考</p>
						</center>
					</div>
				</div>
				<div class="row">
					<center>
						<div class="col-md-6 col-md-offset-3" id="webBrowser"
							style="height:400px; width:650px;"></div>
					</center>
				</div>
				<div class="row">
					<div class="col-md-6 col-md-offset-3">
						<center>
							<p>通常您喜欢使用这些浏览器上网浏览网页</p>
						</center>
					</div>
				</div>
				<div class="row">
					<center>
						<div class="col-md-7 col-md-offset-3" id="webNode1"
							style="height:400px; width:700px;"></div>
					</center>
				</div>
				<div class="row">
					<div class="col-md-6 col-md-offset-3">
						<center>
							<p>通常您比较喜欢浏览这些类型的网页</p>
						</center>
					</div>
				</div>
				<div class="row">
					<center>
						<div class="col-md-7 col-md-offset-3" id="webNode2"
							style="height:400px; width:700px;"></div>
					</center>
				</div>
				<div class="row">
					<div class="col-md-6 col-md-offset-3">
						<center>
							<p>通常您比较喜欢浏览这些网站</p>
						</center>
					</div>
				</div>
			</div>
		</div>
		<div class="row">
			<br>
			<div class="row">
				<div class="col-md-8 col-md-offset-2">
					<dl class="descreption">
						<dt>
							<span class="glyphicon glyphicon-th-large" aria-hidden="true"></span>&nbsp;使用时间在一天中的分布
						</dt>
						<dd>
							<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;这包括你在上午（6:00-12:00），下午（12:00-19:00），晚上（19:00以后）使用时间的分布
						</dd>
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
						<p id="commentary2"></p>
					</center>
				</div>
			</div>
		</div>
		<div class="row">
			<br>
			<div class="row">
				<div class="col-md-8 col-md-offset-2">
					<dl class="descreption">
						<dt>
							<span class="glyphicon glyphicon-th-large" aria-hidden="true"></span>&nbsp;开关机时间的分布
						</dt>
						<dd>
							<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在这里你可以看到具体的开关机的时点
						</dd>
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
						<p id="commentary3"></p>
					</center>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-md-6 col-md-offset-3">
				<br> <a href=""><button class="btn" type="button"
						id="forecast"
						style=" width:100%;background-color: #6a646a;color: #fff;">
						回到首页</button></a>
			</div>
		</div>
	</div>
	<div>
		<footer class="bs-docs-footer">
		<div class="container">
			<br>
			<ul class="bs-docs-footer-links">
				<li><a href="https://github.com/Chubbyer/Chubby">GitHub 仓库</a></li>
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
					rel="license" href="https://" target="_blank">https://</a>
				浏览。本系统所涉及的数据未经日志文件提供者本人授权不得私自在互联网上传播，非法转载致使文件提供者相关权益受到伤害，本项目组摡不负责。
			</p>

		</div>
		</footer>
	</div>
</body>
</html>
