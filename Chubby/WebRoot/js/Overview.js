function showRanking() {
	var myChart = echarts.init(document.getElementById("ranking"));
	// 显示标题，图例和空的坐标轴
	myChart.setOption({
		title : {
			text : '日平均使用时间',
			subtext : '数据来自于分布式PC日志处理系统Chubby'
		},
		tooltip : {
			trigger : 'axis'
		},
		legend : {
			data : [ '使用时间' ]
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
			boundaryGap : [ 0, 0.01 ]
		} ],
		yAxis : [ {
			type : 'category',
			data : [ '梁健', '伍守增', '邬飞', '周宇', '张三', '李四' ]
		} ],
		series : [ {
			name : '2011年',
			type : 'bar',
			data : [ 18203, 23489, 29034, 104970, 131744, 630230 ],
			itemStyle : {
				normal : {
					color : 'rgba(0, 148, 219, 1)'
				}
			}
		}, ]
	});
	// myChart.showLoading();
	// // 异步加载数据
	// $.get("Overview?oType=302").done(function(rpdata) {
	// // alert(data);
	// var JSONObject = eval("(" + rpdata + ")");
	// var hours = [];
	// hours = JSONObject.hours;
	// var names = [];
	// names = JSONObject.names;
	// myChart.hideLoading();
	// // 填入数据
	// myChart.setOption({
	// xAxis : {
	// data : hours
	// },
	// series : [ {
	// // 根据名字对应到相应的系列
	// name : '使用时间',
	// data : names
	// } ]
	// });
	// });
}
function shouDistribut() {
	var myChart = echarts.init(document.getElementById('distribut'));
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
					color : 'rgba(35, 82, 108, 0.3)'
				}
			}
		}, {
			name : '关机',
			type : 'scatter',
			data : [],
			itemStyle : {
				normal : {
					color : 'rgba(22, 11, 125, 0.3)'
				}
			}
		} ]
	};
	myChart.setOption(option);
	myChart.showLoading();
	$.get("Overview?oType=303").done(
			function(rpdata) {
				var JSONObject = eval("(" + rpdata + ")");
				var openPoints = [];
				var open = [];
				var closePoints = [];
				openPoints = JSONObject.openPoints;
				closePoints = JSONObject.closePoints;
				myChart.hideLoading();
				// 填入数据
				myChart.setOption({
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