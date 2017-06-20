function avg_Expect(points){
	var sum=0,avg,std_dvt;
	var ret=[];
	var array=[];
	array=points;
	for(var i=0;i<array.length;i++)
		sum+=array[i];
	avg=Math.round(sum*10/array.length)/10.0;
	for(var i=0;i<array.length;i++)
		sum+=(avg-array[i])*(avg-array[i]);
	std_dvt=Math.round(Math.sqrt(sum)*10)/10.0;
	ret.push(avg);
	ret.push(std_dvt);
	return ret;
}
function showChart3() {
	var myChart3 = echarts.init(document.getElementById('myChart3'));
	var option = {
		title : {
			text : '开关机时点分布',
			subtext : '数据来自于分布式PC日志处理系统Chubby'
		},
		legend : {
			data : [ '开机', '关机' ]
		},
		toolbox : {
			show : true,
			feature : {
				saveAsImage : {}
			}
		},
		tooltip : {
			trigger : 'axis',
			axisPointer : {
				show : true,
				type : 'cross',
				lineStyle : {
					type : 'dashed',
					width : 1
				}
			}
		},
		xAxis : [ {
			type : 'time',
			name : '日期'
		}, ],
		yAxis : {
			type : 'value',
			axisLabel : {
				formatter : '{value} :00'
			}
		},
		dataZoom : [ {
			type : 'slider',
			start : 60,
			end : 100
		} ],
		series : [ {
			name : '开机',
			type : 'scatter',
			data : [],
			itemStyle : {
				normal : {
					color : 'rgba(120, 187, 32, 0.8)'
				}
			}
		}, {
			name : '关机',
			type : 'scatter',
			data : [],
			itemStyle : {
				normal : {
					color : 'rgba(213, 130, 62, 0.8)'
				}
			}
		} ]
	};
	myChart3.setOption(option);
	myChart3.showLoading();
	$.get("PersonOverview?oType=301_3").done(function(rpdata) {
		var JSONObject = eval("(" + rpdata + ")");
						var openPoints = [];
						var open = [];
						var closePoints = [];
						openPoints = JSONObject.openPoints;
						closePoints = JSONObject.closePoints;
						if (openPoints.length == 0 && closePoints.length == 0) {
							myChart3.hideLoading();
							$("#myChart3").html("<img src=\"images/404.jpg\">");
						} else {
							myChart3.hideLoading();
							// 填入数据
							myChart3.setOption({
										series : [
												{
													name : '开机',
													type : 'scatter',
													tooltip : {
														trigger : 'axis',
														formatter : function(
																params) {
															var date = new Date(
																	params.value[0]);
															return params.seriesName
																	+ ' （'
																	+ date
																			.getFullYear()
																	+ '-'
																	+ (date
																			.getMonth() + 1)
																	+ '-'
																	+ date
																			.getDate()
																	+ ' '
																	+ '）<br/>'
																	+ params.value[1];
														},
														axisPointer : {
															type : 'cross',
															lineStyle : {
																type : 'dashed',
																width : 10
															}
														}
													},
													// symbolSize :
													// function(value) {
													// return
													// Math.round(value[2] /
													// 10);
													// },
													data : (function() {
														var open = [];
														var point = [];
														for (i = 0; i < openPoints.length; i++) {
															open.push([
																			new Date(
																					openPoints[i][0]),
																			openPoints[i][1] ])
														}
														return open;
													})()
												},
												{
													name : '关机',
													type : 'scatter',
													tooltip : {
														trigger : 'axis',
														formatter : function(
																params) {
															var date = new Date(
																	params.value[0]);
															return params.seriesName
																	+ ' （'
																	+ date
																			.getFullYear()
																	+ '-'
																	+ (date
																			.getMonth() + 1)
																	+ '-'
																	+ date
																			.getDate()
																	+ ' '
																	+ '）<br/>'
																	+ params.value[1];
														},
														axisPointer : {
															type : 'cross',
															lineStyle : {
																type : 'dashed',
																width : 10
															}
														}
													},
													data : (function() {
														var close = [];
														var point = [];
														for (i = 0; i < closePoints.length; i++) {
															close
																	.push([
																			new Date(
																					closePoints[i][0]),
																			closePoints[i][1] ])
														}
														return close;
													})()
												} ]
									});
							var showInfo='';
							//$("#commentary3").html("AAAA");
							var relInfo=[];
							var opens=[];
							for(var i=0;i<openPoints.length;i++){
								opens.push(openPoints[i][1]);
								}
							//alert(opens);
							relInfo=avg_Expect(opens);
							//alert(relInfo);
							if(relInfo[1]<=3)//标准差小于1
								showInfo+="您开机的时点主要集中在"+relInfo[0]+",这说明您的作息时间比较规范。";
							else
								showInfo+="没有发现您通常的开机时间，这也许跟您的上课时间有关系。";
							var closes=[];
							for(var i=0;i<closePoints.length;i++){
								if(closePoints[i][1]<6)
									closes.push(closePoints[i][1]+24);
								else
									closes.push(closePoints[i][1]);
								}
							
							relInfo=avg_Expect(closes);
							if(relInfo[1]<=3){//标准差小于1
								showInfo+="您关机机的时点主要集中在"+relInfo[0]+",这说明您通常在这个时间休息。";
								if(relInfo[0]>=24)
									showInfo+="Chubby提醒您早睡早起，有助于身体健康。";
							}
							else
								showInfo+="没有发现您通常的关机时间，这很可能说明您对电脑依赖度较低。";
							$("#commentary3").html(showInfo);
						}
					});
}

function showChart2() {
	var myChart2 = echarts.init(document.getElementById('myChart2'));
	myChart2.setOption({
		title : {
			text : '一天中PC使用时间分布',
			subtext : '数据来自于分布式PC日志处理系统Chubby'
		},
		tooltip : {
			trigger : 'item',
			formatter : "{a} <br/>{b} : {c} ({d}%)"
		},
		series : [ {
			name : '使用分布',
			type : 'pie',
			radius : '55%',
			roseType : 'angle',
			data : []
		} ]
	})
	myChart2.showLoading();
	$.get("PersonOverview?oType=301_2").done(
			function(rpdata) {
				//alert(rpdata);
				var JSONObject = eval("(" + rpdata + ")");
				var morning,afternoon,evening;
				morning=JSONObject.morning;
				afternoon=JSONObject.afternoon;
				evening=JSONObject.evening;
				var myData = [ {
					value : morning,
					name : '上午（6:00-12:00）'
				}, {
					value : afternoon,
					name : '下午（12:00-19:00）'
				}, {
					value : evening,
					name : '晚上（19:00以后）'
				} ];
				if (JSONObject.evening == 0 && JSONObject.afternoon==0
						&& JSONObject.morning==0) {
					myChart2.hideLoading();
					$("#myChart2").html("<img src=\"images/404.jpg\">");
					showChart3();
				} else {
					myChart2.hideLoading();
					myChart2.setOption({
						series : [ {
							data : myData
						} ]
					})
					
					var showInfo='';
					if(morning>afternoon&&morning>evening)
						showInfo+="您使用电脑的时间主要集中在上午，这可能跟您的课程时间有很大的关系，Chubby提醒您合理安排时间，" +
								"长时间使用电脑对眼睛有很大的伤害，我们需要一个美丽的世界，更需要一双美丽的眼睛。";
					if(afternoon>morning&&afternoon>evening)
						showInfo+="您使用电脑的时间主要集中在下午，这可能跟您的课程时间有很大的关系，Chubby提醒您合理安排时间，" +
								"长时间使用电脑对眼睛有很大的伤害，我们需要一个美丽的世界，更需要一双美丽的眼睛。";
					if(evening>morning&&evening>afternoon)
						showInfo+="您使用电脑的时间主要集中在晚上，这可能跟您的课程时间有很大的关系，Chubby提醒您合理安排时间，" +
								"长时间使用电脑对眼睛有很大的伤害，我们需要一个美丽的世界，更需要一双美丽的眼睛。";	
					
					$("#commentary2").html(showInfo);
					showChart3();
				}
			});
}

function showChart1() {
	var myChart1 = echarts.init(document.getElementById("myChart1"));
	// 显示标题，图例和空的坐标轴
	myChart1.setOption({
		title : {
			text : '每天PC使用时间',
			subtext : '数据来自于分布式PC日志处理系统Chubby'
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
	myChart1.showLoading();
	// 异步加载数据
	$.get("PersonOverview?oType=301_1").done(
			function(rpdata) {
				// alert(rpdata);
				var JSONObject = eval("(" + rpdata + ")");
				var days = [];
				days = JSONObject.days;
				var points = [];
				points = JSONObject.points;
				if (points.length == 0&&days.length==0) {
					myChart1.hideLoading();
					$("#myChart1").html("<img src=\"images/404.jpg\">");
					showChart2();
				} else {
					myChart1.hideLoading();
					// 填入数据
					myChart1.setOption({
						xAxis : {
							data : days
						},
						series : [ {
							// 根据名字对应到相应的系列
							name : '使用时间',
							data : points
						} ]
					});
					var useSum = useHours(points);
					$("#commentary1").html(
							points.length+"天共计使用" + useSum + "小时，如果你是博尔特，用这些时间你可以奔跑" + Math.round(useSum
									* 36*10)/10.0 + "KM，相当于绕赤道"
									+ Math.round(useSum * 36 * 10 / 40075) / 10
									+ "圈")
					$("#forecast").click(function() {
						forecastChart(days[days.length - 1], points);
						$("#forecastChartDiv").fadeIn(2000);
					})
					showChart2();
				}
			});
}
function useHours(points) {
	var sum = 0;
	for (var i = 0; i < points.length; i++)
		sum += points[i];
	return sum;
}
function diffQuotient(data, k) {
	var points = [];
	points = data;
	var quotient = [];
	for (var i = 0; i < points.length - 1; i++) {
		quotient.push((points[i + 1] - points[i]) / k);
	}
	return quotient;
}
function result(x, y0, quotientTable) {
	var sum = y0;
	for (var i = 0; i < quotientTable.length; i++) {
		var temp = 1;
		for (var j = 0; j < i + 1; j++) {
			temp = temp * (x - j);
		}
		sum += quotientTable[i] * temp;
		// alert(quotientTable[i]+"*"+temp);
	}
	return sum;
}
function forecast(data) {
	var points = [];
	points = data;
	var mon = [];
	var tues = [];
	var wed = [];
	var thurs = [];
	var fir = [];
	var satur = [];
	var sun = [];
	for (var i = 0; i < points.length;) {
		mon.push(points[i++]);// 星期一
		tues.push(points[i++]);// 星期二
		wed.push(points[i++]);
		thurs.push(points[i++]);
		fir.push(points[i++]);
		satur.push(points[i++]);
		sun.push(points[i++]);
	}
	var table = [];
	table.push(mon);// 星期一
	table.push(tues);// 星期二
	table.push(wed);
	table.push(thurs);
	table.push(fir);
	table.push(satur);
	table.push(sun);
	var quotientTables = [];// 总差商表
	for (var i = 0; i < table.length; i++) {
		var quotient = table[i];
		var quotientTable = [];
		// alert(table[i]);
		for (var j = 0; j < table[i].length - 1; j++) {
			quotient = diffQuotient(quotient, j + 1);
			quotientTable.push(quotient[0]);// 某天的差商表
		}
		quotientTables.push(quotientTable);
		// alert(quotientTable);
		// var k=result(1.5,table[i][0],quotientTable);
		// alert(k);
	}
	var forecast = [];
	for (var i = 0; i < 2; i++) {// 两周
		for (var j = 0; j < table.length; j++) {
			var days = table[j];
			var r = Math.ceil(Math.random() * days.length);
			var useHours = result(r, days[0], quotientTables[j]);
			useHours = Math.round(useHours * 10) / 10;
			if (useHours > 24)
				forecast.push(24);
			else if (useHours < 0)
				forecast.push(0);
			else
				forecast.push(useHours);
		}
	}
	return forecast;
}
function forecastChart(day, points) {
	var myChart = echarts.init(document.getElementById("forecastChart"));
	myChart.setOption({
		title : {
			text : '未来两周预计使用时间',
			subtext : '数据来自于分布式PC日志处理系统Chubby'
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
		// dataZoom : [ {
		// type : 'slider',
		// start : 60,
		// end : 100
		// } ],
		series : [ {
			name : '使用时间',
			type : 'bar',
			data : []
		} ]
	});
	myChart.showLoading();
	var someDay = new Date(+new Date(day) + 24 * 3600 * 1000);
	var days = [];
	for (var i = 0; i < 14; i++) {
		var day = [ someDay.getFullYear(), someDay.getMonth() + 1,
				someDay.getDate() ].join('-');
		days.push(day);
		someDay = new Date(+someDay + 24 * 3600 * 1000);
	}
	// 异步加载数据
	var point = [];
	point = forecast(points);
	myChart.hideLoading();
	// 填入数据
	myChart.setOption({
		xAxis : {
			data : days
		},
		series : [ {
			// 根据名字对应到相应的系列
			name : '使用时间',
			data : point
		} ]
	});
}

function sortArray(points){
	var array=[],temp;
	array=points;
	for(var i=0;i<array.length;i++){
		for(var j=i+1;j<array.length;j++){
			if(array[i]<array[j]){
				temp=array[i];
				array[i]=array[j];
				array[j]=temp;
			}
		}
	}
	return array;
}
function webOnline(){
	var myChart = echarts.init(document.getElementById("webOnline"));
	// 显示标题，图例和空的坐标轴
	myChart.setOption({
		title : {
			text : '每天上网时间',
			subtext : '数据来自于分布式PC日志处理系统Chubby'
		},
		tooltip : {
			trigger : 'axis',
			axisPointer : {
				type : 'cross'
			}
		},
		legend : {
			data : [ '上网时间' ]
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
	myChart.showLoading();
	// 异步加载数据
	$.get("WebRecord?oType=401_1").done(
			function(rpdata) {
				// alert(rpdata);
				var JSONObject = eval("(" + rpdata + ")");
				var days = [];
				days = JSONObject.days;
				var points = [];
				points = JSONObject.points;
				if (points.length == 0||days.length==0) {
					myChart.hideLoading();
					$("#webOnline").html("<img src=\"images/404.jpg\">");
				} else {
					myChart.hideLoading();
					// 填入数据
					myChart.setOption({
						xAxis : {
							data : days
						},
						series : [ {
							// 根据名字对应到相应的系列
							name : '上网时间',
							data : points
						} ]
					});
				}
			});
}
function webBrowser(){
	var myChart = echarts.init(document.getElementById('webBrowser'));
	myChart.setOption({
		title : {
			text : '浏览器使用偏好',
			subtext : '数据来自于分布式PC日志处理系统Chubby'
		},
		tooltip : {
			trigger : 'item',
			formatter : "{a} <br/>{b} : {c} ({d}%)"
		},
		series : [ {
			name : '使用分布',
			type : 'pie',
			radius : '55%',
			//roseType : 'angle',
			data : []
		} ]
	})
	myChart.showLoading();
	$.get("WebRecord?oType=401_2").done(
			function(rpdata) {
				//alert(rpdata);
				var JSONObject = eval("(" + rpdata + ")");
				var browserName=[];
				var visit_count=[];
				browserName=JSONObject.browserName;
				visit_count=JSONObject.visit_count;
				var myData = [];
				for(var i=0;i<browserName.length;i++){
					myData.push({value:visit_count[i],name:browserName[i]});
				}				
				if (browserName.length == 0 ||visit_count.length==0) {
					myChart.hideLoading();
					$("#webBrowser").html("<img src=\"images/404.jpg\">");
				} else {
					myChart.hideLoading();
					myChart.setOption({
						series : [ {
							data : myData
						} ]
					})
				}
			});
}
function webNode1(){
	var myChart = echarts.init(document.getElementById("webNode1"));
	// 显示标题，图例和空的坐标轴
	myChart.setOption({
		title : {
			text : '经常性访问网页类型',
			subtext : '数据来自于分布式PC日志处理系统Chubby'
		},
		tooltip : {
			trigger : 'axis'
		},
		legend : {
			data : [ '访问次数' ]
		},
		toolbox : {
			show : true,
			feature : {
				mark : {
					show : true
				},
				dataView : {
					show : true,
					readOnly : false
				},
				magicType : {
					show : true,
					type : [ 'line', 'bar' ]
				},
				restore : {
					show : true
				},
				saveAsImage : {
					show : true
				}
			}
		},
		calculable : true,
		xAxis : [ {
			type : 'value',
			boundaryGap : [ 0, 0.01 ],
			axisLabel : {
				formatter : '{value} '
			}
		} ],
		yAxis : [ {
			type : 'category',
			data : []
		} ],
		series : [ {
			name : '访问次数',
			type : 'bar',
			data : [],
			itemStyle : {
				normal : {
				// color : 'rgba(0, 148, 219, 1)'
				}
			}
		}, ]
	});
	myChart.showLoading();
	// 异步加载数据
	$.get("WebRecord?oType=401_3").done(function(rpdata) {
		// alert(rpdata);
		var JSONObject = eval("(" + rpdata + ")");
		var Sites = [],s=[];
		s = JSONObject.Sites;
		for(var i=s.length-1;i>=0;i--)
			Sites.push(s[i]);
		var Counts = [],c=[];
		c = JSONObject.Counts;
		for(var i=c.length-1;i>=0;i--)
			Counts.push(c[i]);
		if (Sites.length == 0 || Counts.length == 0) {
			myChart.hideLoading();
			$("#webNode1").html("<img src=\"images/404.jpg\">");
		} else {
			myChart.hideLoading();
			// 填入数据
			myChart.setOption({
				yAxis : {
					data : Sites
				},
				series : [ {
					// 根据名字对应到相应的系列
					name : '访问次数',
					data : Counts
				} ]
			});
		}
	});
}
function webNode2(){
	var myChart = echarts.init(document.getElementById("webNode2"));
	// 显示标题，图例和空的坐标轴
	myChart.setOption({
		title : {
			text : '经常性访问的网页',
			subtext : '数据来自于分布式PC日志处理系统Chubby'
		},
		tooltip : {
			trigger : 'axis'
		},
		legend : {
			data : [ '访问次数' ]
		},
		toolbox : {
			show : true,
			feature : {
				mark : {
					show : true
				},
				dataView : {
					show : true,
					readOnly : false
				},
				magicType : {
					show : true,
					type : [ 'line', 'bar' ]
				},
				restore : {
					show : true
				},
				saveAsImage : {
					show : true
				}
			}
		},
		calculable : true,
		xAxis : [ {
			type : 'value',
			boundaryGap : [ 0, 0.01 ],
			axisLabel : {
				formatter : '{value} '
			}
		} ],
		yAxis : [ {
			type : 'category',
			data : [ '梁健', '伍守增', '邬飞', '周宇' ]
		} ],
		series : [ {
			name : '访问次数',
			type : 'bar',
			data : [ 3.2, 3.8, 4.0, 4.1 ],
			itemStyle : {
				normal : {
				// color : 'rgba(0, 148, 219, 1)'
				}
			}
		}, ]
	});
	myChart.showLoading();
	// 异步加载数据
	$.get("WebRecord?oType=401_4").done(function(rpdata) {
		// alert(data);
		var JSONObject = eval("(" + rpdata + ")");
		var Sites = [],s=[];
		s = JSONObject.Sites;
		for(var i=s.length-1;i>=0;i--)
			Sites.push(s[i]);
		var Counts = [],c=[];
		c = JSONObject.Counts;
		for(var i=c.length-1;i>=0;i--)
			Counts.push(c[i]);
		if (Sites.length == 0 && Counts.length == 0) {
			myChart.hideLoading();
			$("#webNode2").html("<img src=\"images/404.jpg\">");
		} else {
			myChart.hideLoading();
			// 填入数据
			myChart.setOption({
				yAxis : {
					data : Sites
				},
				series : [ {
					// 根据名字对应到相应的系列
					name : '访问次数',
					data : Counts
				} ]
			});
		}
	});
}


function webInfo(){
	$("#webInfoBtn").click(function() {
		//forecastChart(days[days.length - 1], points);
		$.get("WebRecord?oType=test").done(function(rpdata) {
			//alert(rpdata);
			var JSONObject = eval("(" + rpdata + ")");
			var rel=JSONObject.status;
			if(rel==2){
				//alert("<center><h3>没有找到与您相关的上网记录</h3></center>");
				$("#webInfoDiv").fadeIn(1000);
				$("#webInfoDiv").html("<center><h5>没有找到与您相关的上网记录</h4></center>");
			}
			else{
				$("#webInfoDiv").fadeIn(2000);
				webOnline();
				webBrowser();
				webNode1();
				webNode2();
			}
		})
	})
}