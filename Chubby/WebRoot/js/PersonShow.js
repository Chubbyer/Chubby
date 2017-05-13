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
	});
}
function showChart2() {
	var myData = [ {
		value : 235,
		name : '上午（12:00以前）'
	}, {
		value : 274,
		name : '下午（12:00-19:00）'
	}, {
		value : 310,
		name : '晚上（19:00以后）'
	} ];
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
		myChart2.hideLoading();
		myChart2.setOption({
			series : [ {
				data : myData
			} ]
		})
	});
}
function showChart3() {
	var myChart3 = echarts.init(document.getElementById('myChart3'));
	var dataAll = [
			[ [ "2017/04/12", 8.4 ], [ "2017/04/13", 6.9 ],
					[ "2017/04/14", 7.8 ], [ "2017/04/15", 8.1 ],
					[ "2017/04/16", 8.3 ], [ "2017/04/17", 9.6 ],
					[ "2017/04/18", 7.4 ], [ "2017/04/19", 4.6 ],
					[ "2017/04/20", 10.4 ], [ "2017/04/21", 4.2 ],
					[ "2017/04/22", 5.8 ] ],
			[ [ "2017/04/12", 9.4 ], [ "2017/04/13", 8.9 ],
					[ "2017/04/14", 9.8 ], [ "2017/04/15", 10.1 ],
					[ "2017/04/16", 11.3 ], [ "2017/04/17", 12.6 ],
					[ "2017/04/18", 8.4 ], [ "2017/04/19", 6.6 ],
					[ "2017/04/20", 11.4 ], [ "2017/04/21", 9.2 ],
					[ "2017/04/22", 7.8 ] ] ];

	var markLineOpt = {
		animation : false,
		label : {
			normal : {
				formatter : 'y = 0.5 * x + 3',
				textStyle : {
					align : 'right'
				}
			}
		},
		lineStyle : {
			normal : {
				type : 'solid'
			}
		},
		tooltip : {
			formatter : 'y = 0.5 * x + 3'
		},
		data : [ [ {
			coord : [ 0, 3 ],
			symbol : 'none'
		}, {
			coord : [ 20, 13 ],
			symbol : 'none'
		} ] ]
	};

	var option = {
		title : {
			text : '开关机时点分布',
			x : 'center',
			y : 0
		},
		grid : [ {
			x : '7%',
			y : '7%',
			width : '90%',
			height : '78%'
		},

		],
		tooltip : {
			formatter : 'Group {a}: ({c})'
		},
		xAxis : [
				{
					gridIndex : 0,
					type : 'category',
					// min : 0,
					// max : 20
					data : [ "2017/04/12", "2017/04/13", "2017/04/14",
							"2017/04/15", "2017/04/16", "2017/04/17",
							"2017/04/18", "2017/04/19", "2017/04/20",
							"2017/04/21", "2017/04/22", "2017/04/23" ]
				},

		],
		yAxis : [ {
			gridIndex : 0,
		// min : 0,
		// max : 15
		},

		],
		dataZoom : [ {
			type : 'slider',
			start : 60,
			end : 100
		} ],
		series : [ {
			name : 'I',
			type : 'scatter',
			xAxisIndex : 0,
			yAxisIndex : 0,
			// data : dataAll[0],
			data : [ 8.3, 7.5, 10.3, 10, 10, 20, 7, 12, 17, 11, 6, 13 ],
			markLine : markLineOpt
		}, {
			name : 'II',
			type : 'scatter',
			xAxisIndex : 0,
			yAxisIndex : 0,
			// data : dataAll[1],
			data : [ 9.3, 9.5, 12.3, 11, 13, 12, 15, 16, 18, 13, 8, 17 ],
			markLine : markLineOpt
		} ]
	};
	myChart3.setOption(option);
}