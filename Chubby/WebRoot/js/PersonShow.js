function showChart3() {
	var myChart3 = echarts.init(document.getElementById('myChart3'));
	var option = {
		title : {
			text : '开关机时点分布',
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
	$
			.get("PersonOverview?oType=301_3")
			.done(
					function(rpdata) {
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
							myChart3
									.setOption({
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
															open
																	.push([
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
						}
					});
}

function showChart2() {
	var myChart2 = echarts.init(document.getElementById('myChart2'));
	myChart2.setOption({
		title : {
			text : '一天中PC使用时间分布'
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
				var myData = [ {
					value : JSONObject.morning,
					name : '上午（6:00-12:00）'
				}, {
					value : JSONObject.afternoon,
					name : '下午（12:00-19:00）'
				}, {
					value : JSONObject.evening,
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
					showChart3();
				}
			});
}

function showChart1() {
	var myChart1 = echarts.init(document.getElementById("myChart1"));
	// 显示标题，图例和空的坐标轴
	myChart1.setOption({
		title : {
			text : '每天PC使用时间'
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
							"共计使用" + useSum + "小时，如果你是博尔特，用这些时间你可以奔跑" + useSum
									* 36 + "KM，相当于绕赤道"
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
			text : '未来两周预计使用时间'
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
