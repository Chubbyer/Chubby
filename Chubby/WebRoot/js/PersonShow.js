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
		}, {
			name : '关机',
			type : 'scatter',
			data : [],
		} ]
	};
	myChart3.setOption(option);
	myChart3.showLoading();
	$.get("PersonOverview?oType=301_3").done(
			function(rpdata) {
				var JSONObject = eval("(" + rpdata + ")");
				var openPoints = [];
				var open = [];
				var closePoints = [];
				openPoints = JSONObject.openPoints;
				closePoints = JSONObject.closePoints;
				myChart3.hideLoading();
				// 填入数据
				myChart3.setOption({
					series : [
							{
								name : '开机',
								type : 'scatter',
								tooltip : {
									trigger : 'axis',
									formatter : function(params) {
										var date = new Date(params.value[0]);
										return params.seriesName + ' （'
												+ date.getFullYear() + '-'
												+ (date.getMonth() + 1) + '-'
												+ date.getDate() + ' '
												+ '）<br/>' + params.value[1];
									},
									axisPointer : {
										type : 'cross',
										lineStyle : {
											type : 'dashed',
											width : 10
										}
									}
								},
								// symbolSize : function(value) {
								// return Math.round(value[2] / 10);
								// },
								data : (function() {
									var open = [];
									var point = [];
									for (i = 0; i < openPoints.length; i++) {
										open.push([ new Date(openPoints[i][0]),
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
									formatter : function(params) {
										var date = new Date(params.value[0]);
										return params.seriesName + ' （'
												+ date.getFullYear() + '-'
												+ (date.getMonth() + 1) + '-'
												+ date.getDate() + ' '
												+ '）<br/>' + params.value[1];
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
										close.push([
												new Date(closePoints[i][0]),
												closePoints[i][1] ])
									}
									return close;
								})()
							} ]
				});
			});
}

function showChart2() {
	var myChart2 = echarts.init(document.getElementById('myChart2'));
	myChart2.setOption({
		title : {
			text : '一天中PC使用时间分布'
		},
		series : [ {
			name : '访问来源',
			type : 'pie',
			radius : '55%',
			roseType : 'angle',
			data : []
		} ]
	})
	myChart2.showLoading();
	$.get("PersonOverview?oType=301_2").done(function(rpdata) {
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
		myChart2.hideLoading();
		myChart2.setOption({
			series : [ {
				data : myData
			} ]
		})
		showChart3();
	});
}

function showChart1() {
	var myChart1 = echarts.init(document.getElementById("myChart1"));
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
	myChart1.showLoading();
	// 异步加载数据
	$.get("PersonOverview?oType=301_1").done(function(rpdata) {
		// alert(data);
		var JSONObject = eval("(" + rpdata + ")");
		var days = [];
		days = JSONObject.days;
		var points = [];
		points = JSONObject.points;
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
		showChart2();
	});
}

