// "use strict";
var thisDate = new Date();
var todayNumber = thisDate.getDate();
var tomonthNumber = thisDate.getMonth()+1;
var toyearNumber = thisDate.getFullYear();
var monthNumber;

//补0操作
function getzf(num){
    if(parseInt(num) < 10){
        num = '0'+num;
    }
    return num;
}

// 初始化日期条  timeArr:[年,月,日]; isData:是否加载数据; first: 是否第一次打开(isData为true有效)
function initTimeBar(timeArr,isData,first){
	var yyyymm = $("#monthsPicker").val();  // yyyy-mm
	var allMonthDay = (timeArr===undefined)?getLastDay():getLastDay(timeArr[0],timeArr[1]);  //获取月份的天数
	var beginTime = yyyymm+"-01";  //开始时间
	var endTime = yyyymm+"-"+((timeArr===undefined)?todayNumber:allMonthDay);  //结束时间

	$.ajax({
		url: getListOfReportStatusUrl,
		type: 'get',
		dataType: 'json',
		data: {
			begin_time: beginTime,
			end_time: endTime
		},
		success: function(result){
			if (result.status === 0) {
				var timebarData = result.data;

				var todayStartTime = new Date();  //今天start点
				todayStartTime.setHours(0,0,0,0);
				var todayEndTime = new Date(todayStartTime.getTime()+24*60*60*1000);  //今天end点
				var days = [];  //定义日期条数据;
				var weekDay = ['日','一','二','三','四','五','六'];

				for(var i=1;i<=allMonthDay;i++){
					var compareTimeArr = $("#monthsPicker").val().split("-");
					var compareTime = new Date(compareTimeArr[0],compareTimeArr[1]-1,i,8,0,0);  //循环点时间
					var compareDate = $("#monthsPicker").val()+((i<10)?"-0":"-")+i;  //循环日期,和后台数据对比

					var item = {};
					item.dateDayNumber = i;  //日期数
					item.weekDayNumber = compareTime.getDay();  //星期(数)
					item.weekDayTip = weekDay[item.weekDayNumber];  //星期(汉字)
					item.curr = (timeArr&&(timeArr[2]!=todayNumber)&&timeArr[2]==i)?true:false;  //选中项
					if (compareTime < todayStartTime) {  //以前
						item.beforeToday = true;
						item.isToday = false;

					}else if(compareTime > todayEndTime){  //以后
						item.beforeToday = false;
						item.isToday = false;
					}else {  //今天
						item.beforeToday = false;
						item.isToday = true;
					}
					//状态:已报9,迟报10,漏报11,未报12;
					if (compareTime < todayStartTime) {
						$.each(timebarData,function(index,val){
							if (val.reportTime === compareDate) {
								item.reportStatus = val.reportStatus;  // 已报 or 迟报
							}
						});
						if (!item.reportStatus) {
							item.reportStatus = 10;  // 漏报
						}
					}else {
						item.reportStatus = 11;  //未报
					}
					days.push(item);
				}
                console.log(days);

				var daysProperty = {
					weekDayTip: {
						class: function(){
                            var cla1 = ((!this.beforeToday)&&(!this.isToday))?"afterToday":"";
							var cla2 = ((this.weekDayNumber==6)||(this.weekDayNumber==0))?"weekend":"";
                            return cla1+" "+cla2;
						}
					},
					reportStatus: {
						class: function(e){
							var result = e.element.className;
							switch(this.reportStatus){
								case 8:
									result = result+" hasReport";  //已报
									break;
								case 9:
									result = result+" lateReport";  //迟报
									break;
								case 10:
									result = result+" leakReport";  //漏报
									break;
								default:
									result = result;  //未报
							}
							if (this.curr) {
								result = result + " curr";
							}else if (this.isToday) {
								result = result + " active end curr";
							}
							return result;
						}
					}
				};
				$("ul.clearfix li").removeClass("curr");  // 先清除"curr"
				$('#slider-index ul').render(days,daysProperty);
				if ($(".curr").length===2) {  // 有今天 + 点击
					$(".end").removeClass("curr");
				}else if ($(".curr").length===1) {  // 有今天 or 点击

				}else {  // 以前
					$("ul.clearfix li:first").addClass("curr");
				}

				$("ul.clearfix").width($("ul.clearfix li").length*50);

				if (isData) {  // 判断是否加载数据
					getAndBackupTable(first);
				}

				resetTimeBarPosition();
				setDateBar();
				setLeftAndRightMonthStatus();
				setLeftAndRightStatus();
			}else {
				console.log("不能获取时间条状态信息");
			}
		},
		error: function(err){
			console.log(err);
		}
	});
}
// 设置表格左上角的日期
function setDateBar(){
	var dateBar = $("#monthsPicker").val().split("-");
	var titleDay = $("ul .curr span").text();
	titleDay = (titleDay<10)?(0+titleDay):titleDay;
	$('#titleYear').text(dateBar[0]);
	$('#titleMonth').text(dateBar[1]);
	$('#titleDay').text(titleDay);
}

//切换月份
function clickLeftOrRightMonth(){
	if ($(this)[0].className.indexOf("prevButton")!==-1) {
		var arr = datePrev();
	}else {
		var arr = dateNext();
	}
	arr = arr.split('-');

	setLeftAndRightMonthStatus();
	initTimeBar(arr,true);
}

//设置月份左右按钮状态
function setLeftAndRightMonthStatus(){
	var arr = $('#monthsPicker').val().split("-");

	$(".nextButton").removeClass("active");
	if (Number(arr[0]) < Number(toyearNumber)) {
		$(".nextButton").addClass("active");
	}else if (Number(arr[0]) === Number(toyearNumber)) {
		if (Number(arr[1]) < Number(tomonthNumber)) {
			$(".nextButton").addClass("active");
		}
	}
}

// 日期点击事件
function clickLis(){
	// 点击"未报"状态, 不响应
    // console.log(this);
    // console.log(this.classList);
    // console.log(this.classList[1]);
	if (!$(this).hasClass("active")&&!$(this).hasClass("leakReport")&&!$(this).hasClass("lateReport")&&!$(this).hasClass("hasReport")) {
		return false;
	}
	// 设置时间今天头(开始)
	var thisDateMin = new Date();
	thisDateMin.setHours(0,0,0,0);
	// 设置时间今天尾(结束)
	var thisDateMax = new Date(thisDateMin.getTime()+24*60*60*1000);
	// 获取li上的时间戳
	var liDate = new Date($(this).data("date"));

	// 比较
	if (liDate < thisDateMin) {  //以前
		$(this).addClass('curr').siblings().removeClass('curr');

		getAndBackupTable(false);
	}else if(liDate > thisDateMax){  //以后
		$('.lis').removeClass('curr');
	}else {  //今天
		$(this).addClass('end curr').siblings().removeClass('end curr');
		getAndBackupTable(false);
	}
	setDateBar();
}

//左右按钮点击事件
function clickLeftOrRight(){
	var left;
	if ($(this).hasClass("leftBtn")) {
		left = $(".dateBox").position().left + 50;
	}else if ($(this).hasClass("rightBtn active")) {
		left = $(".dateBox").position().left - 50;
	}
	$(".dateBox").css("left", left);

	setLeftAndRightStatus();
}

//设置左右按钮状态
function setLeftAndRightStatus(){
	var $firstLeft = $(".container-top li:first")[0].getBoundingClientRect().left;
	var $lastRight = $(".container-top li:last")[0].getBoundingClientRect().right;
	var $box = $(".showBox")[0].getBoundingClientRect();

	$('.leftBtn,.rightBtn').addClass('hide');
	if ($firstLeft < $box.left) {
		$('.leftBtn').removeClass('hide');
	}
	if ($lastRight > $box.right) {
		$('.rightBtn').removeClass('hide');
	}
	if ($lastRight > $box.right) {
		$('.rightBtn').removeClass('hide');
	}
}

//检测并重置时间轴位置,防止按钮溢出
function resetTimeBarPosition(){
	var li = $("ul.clearfix .curr")[0].getBoundingClientRect();
	var showBox = $(".showBox")[0].getBoundingClientRect();
	var ul = $("ul.clearfix")[0].getBoundingClientRect();
	var difference = li.right - showBox.right;

	if (ul.right < showBox.right) {
		var a = $("ul.clearfix").position().left + (showBox.right - ul.right);
		$("ul.clearfix").css("left",a);
	}else if (ul.left > showBox.left) {
		var a = $("ul.clearfix").position().left + (showBox.left - ul.left);
		$("ul.clearfix").css("left",a);
	}else if(difference > 0){
		var newOffset = Math.abs($("ul.clearfix").position().left)+difference;
		$("ul.clearfix").css("left",-newOffset);
	}
}

// 读取表格数据(first:是否第一次打开)
function getAndBackupTable(first){
	var sendTime = $("#monthsPicker").val()+"-"+$(".dateBox .curr").find("span").text();  //发送的日期
	$.ajax({
		url: getManMadeDataListUrl,
		type: 'get',
		dataType: 'json',
		data: {
			time: sendTime
		},
		success: function(result){
			console.log("读取:");
			console.log(result);
			if (result.status === 0) {
				var data = result.data;
				data.forEach(function(val,i){  // 添加序号
					val.xvhao = i+1;
				});

				var dataProperty = {  //ID + 禁用类frozen
					station_name: {
						"data-stationid": function(){
							return this.sta_id;
						}
					},
					rainFall: {
						class: function(){
							if (this.sta_type===2||this.sta_type===3) {
								return "frozen";
							}else{
								return "";
							}
						},
						"data-rainfallid": function(){
							return this.rainfall_id;
						}
					},
					river_waterLevel: {
						class: function(){
							if (this.sta_type===1||this.sta_type===2||this.sta_type===4) {
								return "frozen";
							}else {
								return "";
							}
						},
						"data-riverid": function(){
							return this.river_id;
						}
					},
					resvoir_waterLevel: {
						class: function(){
							if (this.sta_type===1||this.sta_type===3||this.sta_type===5) {
								return "frozen";
							}else {
								return "";
							}
						},
						"data-resid": function(){
							return this.res_id;
						}
					}
				};
				$(".container-bottom-content-dataBind").render(data,dataProperty);

				saveLocalTable(first);  //在本地保存数据 + 可编辑状态
				if (first) {  //第一次打开: 变为编辑状态
					$("button.edit").addClass("hide").next().removeClass("hide");  //变为"保存键"+"取消键"
					$(".container-bottom-content").addClass("active");  //表格是"填写"状态
				}
			}
		}
	});
}

// 往后台保存表格数据
function saveTable(){
	var param = {};  //发送的数据
	var thisDate = $(".curr .dateDay span").text();  //日期
	param.reportTime = $("#monthsPicker").val()+"-"+((thisDate<10)?"0"+thisDate:thisDate);  //时间
	param.manMadeData = [];  //数据

	var $itemUL = $(".container-bottom-content-dataBind ul");
	for(var i=0;i<$itemUL.length;i++){
		// 某一行的全部input(3个)
		var itemInputs = $itemUL[i].querySelectorAll("li input");
		// 判断某一行是否有更改
		if ((itemInputs[0].getAttribute("data-resetvalue")!=itemInputs[0].value)||(itemInputs[1].getAttribute("data-resetvalue")!=itemInputs[1].value)||(itemInputs[2].getAttribute("data-resetvalue")!=itemInputs[2].value)) {
			//判断是否更改为空值
			if ((itemInputs[0].value!="—"&&itemInputs[0].value=="")||(itemInputs[1].value!="—"&&itemInputs[1].value=="")||(itemInputs[2].value!="—"&&itemInputs[2].value=="")) {
				errorBox($(itemInputs).parents("ul").find("[data-bind=station_name]").text()+" 的数据不完整");
				console.log("不传数据");
				return false;
			}else {
				var item = {};
			    item.sta_id = parseInt($itemUL[i].querySelector("[data-bind=station_name]").getAttribute("data-stationid"),10);
			    var stationname = $itemUL[i].querySelector("[data-bind=rainFall]");  //雨量
			    item.rainfall_id = (stationname.getAttribute("data-rainfallid"))?(stationname.getAttribute("data-rainfallid")):null;
			    item.rainfall = (stationname.value=="—"||stationname.value=="")?null:(parseFloat(stationname.value));
			    var riverwaterlevel = $itemUL[i].querySelector("[data-bind=river_waterLevel]");  //河道水位
			    item.river_id = (riverwaterlevel.getAttribute("data-riverid"))?(riverwaterlevel.getAttribute("data-riverid")):null;
			    item.riverWaterLevel = (riverwaterlevel.value=="—"||riverwaterlevel.value=="")?null:(parseFloat(riverwaterlevel.value));
			    var resvoirwaterlevel = $itemUL[i].querySelector("[data-bind=resvoir_waterLevel]");  //水库水位
			    item.res_id = (resvoirwaterlevel.getAttribute("data-resid"))?(resvoirwaterlevel.getAttribute("data-resid")):null;
			    item.resvoirWaterLevel = (resvoirwaterlevel.value=="—"||resvoirwaterlevel.value=="")?null:(parseFloat(resvoirwaterlevel.value));

			    param.manMadeData.push(item);  //插入单条数据
			}
		}
	}
	if (param.manMadeData.length === 0) {  //没更改内容, 就不用传了
		quitTable();
		console.log("不传数据");
		return false;
	}
		console.log("保存");
		console.log(param);
	$.ajax({
		url: manMadeReportDataUrl,
		type: 'post',
		traditional: true,
		contentType : 'application/json;charset=utf-8',
		dataType: 'json',
		data: JSON.stringify(param),
		success: function(result){
			console.log(result);
			if (result.status === 0) {
				var timeArr = $('#monthsPicker').val().split('-');
				timeArr.push($("li.curr span").text());  //传入的日期
				initTimeBar(timeArr,true,false); //更新时间条: timeArr的时间/加载数据/不是第一次
			}
		},
		error: function(err){	}
	});
}
// 在本地保存表格数据(是否可编辑:true ; false(默认))
function saveLocalTable(isEditable){
	var $inputs = $(".container-bottom-content").find("input");
	$("button.edit").removeClass("hide").next().addClass("hide");  //变为"编辑键"
	$(".container-bottom-content").removeClass("active");  //表格是"显示"状态
	$.each($inputs,function(i,input){
		var $input = $(input);
		if ($input.hasClass("frozen")) {
			$input.attr("type","text").val("—");  //有"forzon"的项补充"-";
		}else{
			$input.attr("type","number");
		}
		$input.attr("disabled","disabled").attr("data-resetvalue",$input.val());
		if (isEditable&&(!$input.hasClass("frozen"))) {  // 是否可以编辑
			$input.removeAttr("disabled");
		}
	});
}
// 取消编辑表格
function quitTable(){
	var $inputs = $(".container-bottom-content").removeClass("active").find("input");
	$.each($inputs,function(i,input){
		var value = input.getAttribute("data-resetvalue");
		$(input).attr("disabled","disabled").val(value);
	});
	$(".container-bottom .quit").parents(".operation").addClass("hide").prev().removeClass("hide");
}

$(function(){
	// 初始化
	$('#monthsPicker').val(DateToday('months'));
	initTimeBar(undefined,true,true);  //初始化时间条:现在时刻/加载数据/第一次打开

	$(window).resize(function(){
		setLeftAndRightStatus();
		resetTimeBarPosition();
	});
	// [回到今天]
	$('#gotoToday').on("click",function(){
		$('#monthsPicker').val(DateToday("months"));
		initTimeBar(undefined,true,false);  //初始化时间条:现在时刻/加载数据/不是第一次打开
	 });
	 //切换月份
 	$(".pickerBox").on("click",".prevButton.active,.nextButton.active",clickLeftOrRightMonth);
	//切换日期(直接点击)
	$(".clearfix").on("click","li",clickLis);
	//切换日期(左右键)
	$(".container-top").on("click",".leftBtn.active,.rightBtn.active",clickLeftOrRight);

	//编辑表格
	$(".container-bottom .edit").on("click",function(){
		$(".container-bottom .edit").addClass("hide").next().removeClass("hide");
		$(".container-bottom-content").addClass("active").find("input").not(".frozen").removeAttr("disabled");
	});
	// 保存表格
	$(".container-bottom .save").on("click",saveTable);
	// 取消编辑表格
	$(".container-bottom .quit").on("click",quitTable);

});
