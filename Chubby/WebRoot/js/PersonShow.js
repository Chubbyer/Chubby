function showChart1(){
	var myChart1 = echarts.init(document.getElementById('myChart1'));
	myChart1.setOption({
	    title: {
	        text: '异步数据加载示例'
	    },
	    tooltip: {},
	    legend: {
	        data:['销量']
	    },
	    xAxis: {
	        data: []
	    },
	    yAxis: {},
	    series: [{
	        name: '销量',
	        type: 'bar',
	        data: []
	    }]
	});
	function fetchData(cb) {
		// 通过 setTimeout 模拟异步加载
		setTimeout(function() {
			cb({
				categories : [ "衬衫", "羊毛衫", "雪纺衫", "裤子", "高跟鞋", "袜子" ],
				data : [ 5, 20, 36, 10, 10, 20 ]
			});
		}, 3000);
	}
	// 初始 option
	option = {
		title : {
			text : '异步数据加载示例'
		},
		tooltip : {},
		legend : {
			data : [ '销量' ]
		},
		xAxis : {
			data : []
		},
		yAxis : {},
		series : [ {
			name : '销量',
			type : 'bar',
			data : []
		} ]
	};

	myChart1.showLoading();

	fetchData(function(data) {
		myChart1.hideLoading();
		myChart1.setOption({
			xAxis : {
				data : data.categories
			},
			series : [ {
				// 根据名字对应到相应的系列
				name : '销量',
				data : data.data
			} ]
		});
	});
}
function showChart2(){
	var myData = [ {
		value : 235,
		name : '视频广告'
	}, {
		value : 274,
		name : '联盟广告'
	}, {
		value : 310,
		name : '邮件营销'
	}, {
		value : 335,
		name : '直接访问'
	}, {
		value : 400,
		name : '搜索引擎'
	} ];
	var myChart = echarts.init(document.getElementById('main'));
	myChart.setOption({
		title : {
			text : '一天中PC使用时间分布'
		},
		series : [ {
			name : '访问来源',
			type : 'pie',
			radius : '55%',
			roseType : 'angle',
			data : myData
		} ]
	})
}
function showChart3(){
	var myChart3 = echarts.init(document.getElementById('myChart3'));
	var dataAll = [	[
	                   [10.0, 8.04],
	                   [8.0, 6.95],
	                   [13.0, 7.58],
	                   [9.0, 8.81],
	                   [11.0, 8.33],
	                   [14.0, 9.96],
	                   [6.0, 7.24],
	                   [4.0, 4.26],
	                   [12.0, 10.84],
	                   [7.0, 4.82],
	                   [5.0, 5.68]
	               ],
	               [
	                   [10.0, 9.14],
	                   [8.0, 8.14],
	                   [13.0, 8.74],
	                   [9.0, 8.77],
	                   [11.0, 9.26],
	                   [14.0, 8.10],
	                   [6.0, 6.13],
	                   [4.0, 3.10],
	                   [12.0, 9.13],
	                   [7.0, 7.26],
	                   [5.0, 4.74]
	               ],
	               [
	                   [10.0, 7.46],
	                   [8.0, 6.77],
	                   [13.0, 12.74],
	                   [9.0, 7.11],
	                   [11.0, 7.81],
	                   [14.0, 8.84],
	                   [6.0, 6.08],
	                   [4.0, 5.39],
	                   [12.0, 8.15],
	                   [7.0, 6.42],
	                   [5.0, 5.73]
	               ],
	               [
	                   [8.0, 6.58],
	                   [8.0, 5.76],
	                   [8.0, 7.71],
	                   [8.0, 8.84],
	                   [8.0, 8.47],
	                   [8.0, 7.04],
	                   [8.0, 5.25],
	                   [19.0, 12.50],
	                   [8.0, 5.56],
	                   [8.0, 7.91],
	                   [8.0, 6.89]
	               ]
	           ];

	           var markLineOpt = {
	               animation: false,
	               label: {
	                   normal: {
	                       formatter: 'y = 0.5 * x + 3',
	                       textStyle: {
	                           align: 'right'
	                       }
	                   }
	               },
	               lineStyle: {
	                   normal: {
	                       type: 'solid'
	                   }
	               },
	               tooltip: {
	                   formatter: 'y = 0.5 * x + 3'
	               },
	               data: [[{
	                   coord: [0, 3],
	                   symbol: 'none'
	               }, {
	                   coord: [20, 13],
	                   symbol: 'none'
	               }]]
	           };

	           var option = {
	               title: {
	                   text: 'Anscombe\'s quartet',
	                   x: 'center',
	                   y: 0
	               },
	               grid: [
	                   {x: '7%', y: '7%', width: '90%', height: '80%'},

	               ],
	               tooltip: {
	                   formatter: 'Group {a}: ({c})'
	               },
	               xAxis: [
	                   {gridIndex: 0, min: 0, max: 20},

	               ],
	               yAxis: [
	                   {gridIndex: 0, min: 0, max: 15},

	               ],
	               dataZoom: [
	                          {
	                              type: 'slider',
	                              start: 1,
	                              end: 35
	                          }
	                      ],
	               series: [
	                   {
	                       name: 'I',
	                       type: 'scatter',
	                       xAxisIndex: 0,
	                       yAxisIndex: 0,
	                       data: dataAll[0],
	                       markLine: markLineOpt
	                   },
	                   {
	                       name: 'II',
	                       type: 'scatter',
	                       xAxisIndex: 0,
	                       yAxisIndex: 0,
	                       data: dataAll[1],
	                       markLine: markLineOpt
	                   },
	                   {
	                       name: 'III',
	                       type: 'scatter',
	                       xAxisIndex: 0,
	                       yAxisIndex: 0,
	                       data: dataAll[2],
	                       markLine: markLineOpt
	                   },
	                   {
	                       name: 'IV',
	                       type: 'scatter',
	                       xAxisIndex: 0,
	                       yAxisIndex: 0,
	                       data: dataAll[3],
	                       markLine: markLineOpt
	                   }
	               ]
	           };
	           myChart3.setOption(option);
}