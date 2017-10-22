package ekuter.mvc.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ekuter.mvc.constants.Constants;
import ekuter.mvc.exception.BusinessException;
import ekuter.mvc.util.DateUtil;
import ekuter.mvc.util.ManMadeDataListUtil;
import ekuter.mvc.util.ManMadeStationDataUtil;
import ekuter.mvc.util.ResultMessageUtil;
import mybatisPro.mybatisEntity.ReportStatusEntity;
import mybatisPro.mybatisService.impl.ManMadeStaionService;
import mybatisPro.mybatisService.impl.StationService;
import mybatisPro.mybatisService.impl.WaterLevelService;

/**
 * 人工站数据上报管理
 * @author si.yu
 * @date 2017/08/16
 * @version 1.0
 * */
@RestController
@RequestMapping("manMadeStation")
public class ManMadeStationController {

	private static final Logger logger = LoggerFactory.getLogger(ManMadeStationController.class);
	
	@Autowired
	private StationService stationService;
	
	@Autowired
	private WaterLevelService waterLevelService;
	
	@Autowired
	private ManMadeStaionService manMadeService;
	
	
	/**
	 * 人工报讯页面数据列表查询
	 * @param time
	 * @return
	 * */
	@RequestMapping("getManMadeDataList")
	public ResultMessageUtil getManMadeDataList(String time){
		
		ResultMessageUtil resultMsg = new ResultMessageUtil();
		
		if(null != time){
			//查询人工站列表IDs
			List<Long> manMadeStaIds = stationService.getTypeOfStationIds(7);
			
			if(null != manMadeStaIds){
				
				DateUtil dateUtil = new DateUtil();
				
				List<Map<String, Object>> manMadeDataList = new ArrayList<Map<String, Object>>();
				
				//遍历测站ID
				for (Long staId : manMadeStaIds) {
					
					Map<String, Object> manMadeData = new HashMap<String,Object>();
					//查询该站的雨量数据
					Map<String, Object> rainFall = 
							stationService.getRainfallStationList(time +" "+"08:00:00",dateUtil.getFutureDates(time), staId);
					if(null != rainFall){
						manMadeData.put("rainFall", rainFall.get("rainfall"));
						manMadeData.put("rainfall_id", rainFall.get("rainfall_id"));
					}
					//查询该站的水库水位数据
					Map<String, Object> res_waterLever = 
							waterLevelService.getWaterLevelInfo(time +" "+"08:00:00",dateUtil.getFutureDates(time), staId);
					if(null != res_waterLever){
						manMadeData.put("resvoir_waterLevel", res_waterLever.get("waterLevel"));
						manMadeData.put("res_id", res_waterLever.get("res_id"));
					}
					//查询该站的河道水位数据
					Map<String, Object> river_waterLevel = 
							waterLevelService.getSoilWaterLevelInfo(time +" "+"08:00:00",dateUtil.getFutureDates(time), staId);
					if(null != river_waterLevel){
						manMadeData.put("river_waterLevel", river_waterLevel.get("waterLevel"));
						manMadeData.put("river_id", river_waterLevel.get("river_id"));
					}
					//查询测站信息
					Map<String, Object> staInfo = 
							stationService.getStationInfoById(staId);
					if(null != staInfo){
						manMadeData.put("sta_id", staInfo.get("id"));
						manMadeData.put("station_name", staInfo.get("station_name"));
						manMadeData.put("contract", staInfo.get("contract"));
						manMadeData.put("telephone", staInfo.get("telephone"));
						manMadeData.put("sta_type", staInfo.get("station_type_id"));
					}
						
					manMadeDataList.add(manMadeData);
				}
				
				resultMsg.setData(manMadeDataList);
				resultMsg.setStatus(0);
				return resultMsg;
			}else{
				logger.info(Constants.EXCEPTION_MAP.get("MANMADEIDS"));
				throw new BusinessException(Constants.EXCEPTION_MAP.get("MANMADEIDS"));
			}
		}else{
			logger.info(Constants.EXCEPTION_MAP.get("PARAMERROR"));
			throw new BusinessException(Constants.EXCEPTION_MAP.get("PARAMERROR"));
		}
 	}
	
	/**
	 * 人工报讯上报数据
	 * @param list
	 * @return
	 * */
	@RequestMapping("manMadeReportData")
	@ResponseBody
	public ResultMessageUtil manMadeReportData(@RequestBody ManMadeDataListUtil manMadeData){
		
		ResultMessageUtil resultMsg = new ResultMessageUtil();
		if(null != manMadeData){
			
			List<ManMadeStationDataUtil> manMadeStationList = manMadeData.getManMadeData();
			
			if(null != manMadeStationList){
				
				String reportTime = manMadeData.getReportTime();//获取上报的日期
				SimpleDateFormat formatter1 = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
				Date report_date = null;
				try {
					report_date = formatter1.parse(reportTime+" "+"08:00:00");
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(report_date);
				boolean reportFlag = manMadeService.ManMadeDataReport(manMadeStationList,report_date);
				if(reportFlag){
					
					ReportStatusEntity statusEntity = 
							manMadeService.findReportStatus(reportTime);
					if(null == statusEntity){
						Date date = new Date();
						SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd");
						String dateNow = formatter.format(date);
						ReportStatusEntity statusEntityInsert = new ReportStatusEntity();
						statusEntityInsert.setReportTime(reportTime);
						if(reportTime.equals(dateNow)){
							statusEntityInsert.setReportStatus(8);
						}else{
							statusEntityInsert.setReportStatus(9);
						}
						boolean insertFlag = manMadeService.insertReportStatus(statusEntityInsert); 
						if(insertFlag){
							resultMsg.setDataSub("插入上报状态成功！");
						}else{
							resultMsg.setDataSub("插入上报状态失败！");
						}
					}
					
					resultMsg.setStatus(0);
					resultMsg.setMsg("Report Success!");
					return resultMsg;
				}else{
					logger.info(Constants.EXCEPTION_MAP.get("REPORTFAIL"));
					throw new BusinessException(Constants.EXCEPTION_MAP.get("REPORTFAIL"));
				}
			}else{
				logger.info(Constants.EXCEPTION_MAP.get("PARAMERROR"));
				throw new BusinessException(Constants.EXCEPTION_MAP.get("PARAMERROR"));
			}

		}else{
			logger.info(Constants.EXCEPTION_MAP.get("PARAMERROR"));
			throw new BusinessException(Constants.EXCEPTION_MAP.get("PARAMERROR"));
		}
	}
	
	/**
	 * 查询时间段内的所有上报数据状态
	 * @param begin_time
	 * @param end_time
	 * @return
	 * */
	@RequestMapping("getListOfReportStatus")
	public ResultMessageUtil getListOfReportStatus(String begin_time,String end_time){
		
		ResultMessageUtil resultMsg = new ResultMessageUtil();
		
		if(null != begin_time || null != end_time){
			
			List<ReportStatusEntity> statusEntities = 
					manMadeService.getListOfReportStatusData(begin_time+" "+"00:00:00", end_time+" "+"23:59:59");
			if(null != statusEntities){
				
				resultMsg.setData(statusEntities);
				resultMsg.setMsg("search success!");
				resultMsg.setStatus(0);
				return resultMsg;
			}else{
				logger.info(Constants.EXCEPTION_MAP.get("REPORTSTATUS_EXE3"));
				throw new BusinessException(Constants.EXCEPTION_MAP.get("REPORTSTATUS_EXE3"));
			}
		}else{
			logger.info(Constants.EXCEPTION_MAP.get("PARAMERROR"));
			throw new BusinessException(Constants.EXCEPTION_MAP.get("PARAMERROR"));
		}
	}
}
