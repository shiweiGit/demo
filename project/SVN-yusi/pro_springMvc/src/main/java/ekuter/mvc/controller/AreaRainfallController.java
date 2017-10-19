package ekuter.mvc.controller;

import java.util.ArrayList;
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
import ekuter.mvc.util.ParamListUtil;
import ekuter.mvc.util.ResultMessageUtil;
import mybatisPro.mybatisEntity.AreaEntity;
import mybatisPro.mybatisEntity.AverageRainfallEntityUtil;
import mybatisPro.mybatisEntity.StationInfoEntityUtil;
import mybatisPro.mybatisEntity.StationParamEntity;
import mybatisPro.mybatisService.impl.AreaService;
import mybatisPro.mybatisService.impl.StationParamService;
import mybatisPro.mybatisService.impl.StationService;

/**
 * 区域降雨量管理
 * @author si.yu
 * @date 2017/7/24
 * @version 1.0
 * */
@RestController
@RequestMapping("areaRainfall")
public class AreaRainfallController {
	
	private static final Logger logger=LoggerFactory.getLogger(AreaRainfallController.class);
	
	@Autowired 
	private AreaService areaService;
	
	@Autowired
	private StationParamService paramService;
	
	@Autowired
	private StationService stationService;
	
	
	/**
	 * 区域平均降雨量计算
	 * @param begin_time 开始时间
	 * @param end_time 结束时间
	 * @return
	 * */
	@RequestMapping("areaAverageRainfall")
	public ResultMessageUtil getAreaAverageRainfall(String begin_time,String end_time){
		
		ResultMessageUtil resultMsg = new ResultMessageUtil();
		
		if(null != begin_time || null != end_time){
			//查询区域列表信息
			List<AreaEntity> areaEntities = areaService.findListOfArea();
			
			if(null != areaEntities){
				//创建区域平均降雨量信息队列
				List<AverageRainfallEntityUtil> averageRainfallList = new ArrayList<AverageRainfallEntityUtil>();
				//遍历区域信息列表
				for(AreaEntity areaEntity : areaEntities){
					
					//实例化工具类对象
					AverageRainfallEntityUtil averageUtil = new AverageRainfallEntityUtil();
					//循环获取区域ID
					Long area_id = areaEntity.getArea_id();
					//获取区域名称
					String area_name = areaEntity.getArea_name();
					//获取区域算法类型
					Integer alogorithmType = areaEntity.getAlgorithmType();
					
					
					if(null != area_id){
						
						//查询该区域内测站信息
						List<StationInfoEntityUtil> stationRainfallList = new ArrayList<StationInfoEntityUtil>();
						
						List<Long> staIds = new ArrayList<Long>();
						//该区域内所有雨量站id列表
						List<Long> staIds_rain = 
								areaService.getStaIdsByAreaId(Constants.RAINFALL_STATION, area_id);
						//该区域内所有水库水文站id列表
						List<Long> staIds_water = 
								areaService.getStaIdsByAreaId(Constants.WATER_RAINFALL_STATION, area_id);
						//该区域内所有河道水文站id列表
						List<Long> staIds_soil = 
								areaService.getStaIdsByAreaId(Constants.SOIL_RAINFALL_STATION, area_id);
						
						if(null != staIds_rain || null != staIds_water || null != staIds_soil){
							staIds.addAll(staIds_rain);
							staIds.addAll(staIds_water);
							staIds.addAll(staIds_soil);
							
							if(!staIds.isEmpty()){
								
								for(Long staId : staIds){
									
									StationInfoEntityUtil stationRainfall = 
											areaService.getAreaStationsInfoByAreaId(staId, begin_time, end_time);
									
									if(null != stationRainfall){
										stationRainfallList.add(stationRainfall);
									}else{
										//当该站在当前时间段内没有雨量统计数据，雨量返回为0（方便平均雨量计算）
										StationInfoEntityUtil stationInfo = new StationInfoEntityUtil();
										Map<String, Object> sEntity = stationService.getStationInfoById(staId);
										if(null != sEntity){
											stationInfo.setId((Long) sEntity.get("id"));
											stationInfo.setStation_name((String) sEntity.get("station_name"));
											stationInfo.setRainfall(0.0f);
											stationRainfallList.add(stationInfo);
										}else{
											logger.info(Constants.EXCEPTION_MAP.get("STATIONINFODETAIL"));
											throw new BusinessException(Constants.EXCEPTION_MAP.get("STATIONINFODETAIL"));
										}
									}
								}
							}
						}
						
						//绑定区域信息
						averageUtil.setArea_id(area_id);//区域ID
						averageUtil.setArea_name(area_name);//区域名称
						averageUtil.setAlgorithmType(alogorithmType);//区域平均雨量算法类型
					
						if(!stationRainfallList.isEmpty()){
							//分析该区域内最大雨量站和最大雨量
							averageUtil = maxRainfallStationAnalysis(averageUtil,stationRainfallList);
							//计算区域平均降雨量
							averageUtil = averageRainfallAnalysis(alogorithmType,averageUtil,stationRainfallList);
							
							averageUtil.setStation_info_list(stationRainfallList);
							
							//将单个平均雨量信息放入区域平均降雨量信息队列
							averageRainfallList.add(averageUtil);
						}else{
							averageUtil.setMessage("该区域未部署检测雨量站");
							averageRainfallList.add(averageUtil);
						}
					}else{
						logger.info(Constants.EXCEPTION_MAP.get("PARAMERROR"));
						throw new BusinessException(Constants.EXCEPTION_MAP.get("PARAMERROR"));
					}
				}
				resultMsg.setData(averageRainfallList);
				return resultMsg;
			}else{
				logger.info(Constants.EXCEPTION_MAP.get("GETLISTAREA"));
				throw new BusinessException(Constants.EXCEPTION_MAP.get("GETLISTAREA"));
			}
		}else{
			logger.info(Constants.EXCEPTION_MAP.get("PARAMERROR"));
			throw new BusinessException(Constants.EXCEPTION_MAP.get("PARAMERROR"));
		}
	}
 

	/**
	 * 计算区域平均降雨量
	 * @param alogorithmType 算法类型
	 * @param averageUtil
	 * @param stationRainfallList
	 * @return averageUtil
	 * */
	private AverageRainfallEntityUtil averageRainfallAnalysis(Integer alogorithmType,AverageRainfallEntityUtil averageUtil,
			List<StationInfoEntityUtil> stationRainfallList) {
		
		Float rainfall = 0.0f;
		int count = 0;
		//计算区域平均雨量（0:算术平均算法；1：系数平均算法）
		if(0 == alogorithmType){//0:算术平均算法
			
			for(StationInfoEntityUtil stationUtil : stationRainfallList){
				//累加该区域所有站的降水量
				rainfall += stationUtil.getRainfall();
				//记录该区域测站的个数
				count++;
			}
			//按算术平均法计算该区域的平均降雨量
			Float averageRainfall = rainfall/count;
			averageUtil.setAverage_rainfall(averageRainfall);
			
		}else if(1 == alogorithmType){//1：系数平均算法
			
		    List<Map<String, Object>> paramInfoList = new ArrayList<Map<String, Object>>();
			
			for(StationInfoEntityUtil stationUtil : stationRainfallList){
				Map<String, Object> paramInfo = new HashMap<String,Object>();
				//获取测站ID
				Long sta_id = stationUtil.getId();
				if(null != sta_id){
					//根据测站ID查询该站对应的系数
					Float sta_param = paramService.getStationParamByStaId(sta_id);
					
					if(null != sta_param){
						//获取该站时间段内降雨量
						Float sta_rainfall = stationUtil.getRainfall();
						//降雨量乘以与该站对应的系数
						Float param_rainfall = sta_rainfall*sta_param;
						//累加该区域所有站的降水量
						rainfall += param_rainfall;
						//记录该区域测站的个数
						count++;
						//将该测站对应系数存放入map中
						paramInfo.put("param_staId", sta_id);
						paramInfo.put("param_staName", stationUtil.getStation_name());
						paramInfo.put("param_value", sta_param);
						
						paramInfoList.add(paramInfo);
					}else{
						logger.info(Constants.EXCEPTION_MAP.get("STATIONPARAM"));
						throw new BusinessException(Constants.EXCEPTION_MAP.get("STATIONPARAM"));
					}
					
				}else{
					logger.info(Constants.EXCEPTION_MAP.get("PARAMERROR"));
					throw new BusinessException(Constants.EXCEPTION_MAP.get("PARAMERROR"));
				}
			}
			//按系数平均法计算该区域的平均降雨量
			Float averageRainfall = rainfall/count;
			averageUtil.setAverage_rainfall(averageRainfall);
			averageUtil.setParam_list(paramInfoList);
		}else{
			logger.info(Constants.EXCEPTION_MAP.get("PARAMERROR"));
			throw new BusinessException(Constants.EXCEPTION_MAP.get("PARAMERROR"));
		}
		return averageUtil;
	}


	/**
	 * 分析该区域内最大雨量站和最大雨量
	 * @param averageUtil
	 * @param stationRainfallList
	 * @return averageUtil
	 * */
	private AverageRainfallEntityUtil maxRainfallStationAnalysis(AverageRainfallEntityUtil averageUtil,
			List<StationInfoEntityUtil> stationRainfallList) {
		
		Long maxStationId = null;
		Float maxRainfall = 0.0f;
		String maxStationName = null;
		//遍历测站雨量信息
		for(StationInfoEntityUtil stationUtil : stationRainfallList){
			//获取该站的降雨量
			Float rainfall = stationUtil.getRainfall();
			
			if(rainfall > maxRainfall){
				maxRainfall = rainfall;
				maxStationId = stationUtil.getId();
				maxStationName = stationUtil.getStation_name();
			}
		}
		averageUtil.setMax_Rainfall(maxRainfall);
		averageUtil.setMaxStation(maxStationName);
		averageUtil.setMaxStation_id(maxStationId);
		
		return averageUtil;
		
	}
	
	/**
	 * 区域算法管理
	 * @param area_id
	 * @param analysis_type
	 * @param list<StationParamEntity> paramList
	 * */
	@RequestMapping("analysisManager")
	@ResponseBody
	public ResultMessageUtil analysisManager(@RequestBody ParamListUtil param){
		
		String error = null;
		ResultMessageUtil resultMsg = new ResultMessageUtil();
		
		if(null != param){
			
			Long area_id = param.getArea_id();
			Integer analysis_type = param.getAnalysis_type();
			List<StationParamEntity> paramList = param.getParamList();
			
			if(null != area_id){
				//修改为算术算法
				if(0==analysis_type){
					//判断库中是否有该区域对应站的算法系数
					boolean haveParamFlag = paramService.findParamByAreaId(area_id);
					if(haveParamFlag){//有数据，判定为从系数算法改为算术算法，需要删除该区域的站对应的系数
						//删除该区域对应站系数 
						boolean delFlag = paramService.deleteParamByAreaId(area_id);
						if(delFlag){
							//根据区域ID修改区域算法类型
							resultMsg = updateAreaInfo(area_id,analysis_type);
							return resultMsg;
						}else{
							logger.info(Constants.EXCEPTION_MAP.get("PARAMLISTDEL"));
							throw new BusinessException(Constants.EXCEPTION_MAP.get("PARAMLISTDEL"));
						}
					}else{//无数据，判定为算术算法改为算术算法（实质无修改）
						//根据区域ID修改区域算法类型
						resultMsg = updateAreaInfo(area_id,analysis_type);
						return resultMsg;
					}
					
				//修改为系数算法
				}else if(1==analysis_type){
					//检测系数值只和是否为1
					error = checkParam(paramList);
					
					if(null != error){
						resultMsg.setStatus(1);
						resultMsg.setMsg(error);
						return resultMsg;
					}else{
						
						AreaEntity areaEntity = new AreaEntity();
						areaEntity.setArea_id(area_id);
						areaEntity.setAlgorithmType(analysis_type);
						//根据区域ID修改区域算法类型
						boolean updateArea = areaService.updateAreaInfo(areaEntity);
						
						if(updateArea){
							//判断库中是否有该区域对应站的算法系数
							boolean haveParamFlag = paramService.findParamByAreaId(area_id);
							//有数据，判定为系数修改
							if(haveParamFlag){
								//根据测站ID更新系数信息
								boolean updateFlag = paramService.updateParam(paramList);
								if(updateFlag){
									resultMsg.setStatus(0);
									resultMsg.setMsg("update success!");
									return resultMsg;
								}else{
									logger.info(Constants.EXCEPTION_MAP.get("PARAMLISTUPDATE"));
									throw new BusinessException(Constants.EXCEPTION_MAP.get("PARAMLISTUPDATE"));
								}
							}else{//无数据，判定为算术算法改为系数算法
								//根据区域ID和测站ID添加系数数据
								boolean addFlag = paramService.addParam(paramList);
								if(addFlag){
									resultMsg.setStatus(0);
									resultMsg.setMsg("update success!");
									return resultMsg;
								}else{
									logger.info(Constants.EXCEPTION_MAP.get("PARAMLISTADD"));
									throw new BusinessException(Constants.EXCEPTION_MAP.get("PARAMLISTADD"));
								}
							}
						}else{
							logger.info(Constants.EXCEPTION_MAP.get("UPDATEAREAINFO"));
							throw new BusinessException(Constants.EXCEPTION_MAP.get("UPDATEAREAINFO"));
						}
					}
				}else{
					logger.info(Constants.EXCEPTION_MAP.get("PARAMERROR"));
					throw new BusinessException(Constants.EXCEPTION_MAP.get("PARAMERROR"));
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
	 * 检测系数合法性（一个区域内的站系数相加和为1）
	 * @param paramList
	 * @return
	 * */
	private String checkParam(List<StationParamEntity> paramList) {
		String error = null;
		Float paramVal = 0.0f;
		
		for(StationParamEntity paramEntity : paramList){
			Float param = paramEntity.getParam();
			paramVal += param;
		}
		if(paramVal - 1 != 0){
			error = "系数不合法";
		}
		return error;
	}


	/**
	 * 区域平均降雨修改为算法平均算法后修改区域信息
	 * @param area_id
	 * @param analysis_type
	 * @return
	 * */
	private ResultMessageUtil updateAreaInfo(Long area_id, Integer analysis_type) {
		
		ResultMessageUtil resultMsg = new ResultMessageUtil();
		
		AreaEntity areaEntity = new AreaEntity();
		areaEntity.setArea_id(area_id);
		areaEntity.setAlgorithmType(analysis_type);
		//根据区域ID修改区域算法类型
		boolean updateArea = areaService.updateAreaInfo(areaEntity);
		if(updateArea){
			resultMsg.setStatus(0);
			resultMsg.setMsg("update success！");
			return resultMsg;
		}else{
			logger.info(Constants.EXCEPTION_MAP.get("UPDATEAREAINFO"));
			throw new BusinessException(Constants.EXCEPTION_MAP.get("UPDATEAREAINFO"));
		}
	}
	
	/**
	 * 根据区域ID查询测站系数列表
	 * @param area_id
	 * @return
	 * */
	@RequestMapping("getAreaStationParamList")
	public ResultMessageUtil getStationsParamByAreaId(Long area_id){
		
		ResultMessageUtil resultMsg = new ResultMessageUtil();
		
		if(null != area_id){
			List<Map<String, Object>> stationsParamList= new ArrayList<Map<String, Object>>();
			//该区域内所有雨量站
			List<Map<String, Object>> rainSta_paramList =
					areaService.getStationsParamByAreaId(Constants.RAINFALL_STATION, area_id);
			//该区域内所有水库水文站
			List<Map<String, Object>> waterSta_paramList =
					areaService.getStationsParamByAreaId(Constants.WATER_RAINFALL_STATION, area_id);
			//该区域内所有河道水文站
			List<Map<String, Object>> soilSta_paramList =
					areaService.getStationsParamByAreaId(Constants.SOIL_RAINFALL_STATION, area_id);
			
			if(null != rainSta_paramList || null != waterSta_paramList || null != soilSta_paramList){
				
				stationsParamList.addAll(rainSta_paramList);
				stationsParamList.addAll(waterSta_paramList);
				stationsParamList.addAll(soilSta_paramList);
				
				if(!stationsParamList.isEmpty()){
					resultMsg.setData(stationsParamList);
					resultMsg.setStatus(0);
					return resultMsg;
				}else{
					resultMsg.setData(stationsParamList);
					resultMsg.setMsg("该区域未设置雨量检测站");
					return resultMsg;
				}
				
			}else{
				logger.info(Constants.EXCEPTION_MAP.get("EX_AREA_7"));
				throw new BusinessException(Constants.EXCEPTION_MAP.get("EX_AREA_7"));
			}
		}else{
			logger.info(Constants.EXCEPTION_MAP.get("PARAMERROR"));
			throw new BusinessException(Constants.EXCEPTION_MAP.get("PARAMERROR"));
		}
	}
}