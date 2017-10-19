package mybatisPro.mybatisService;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ekuter.mvc.constants.Constants;
import ekuter.mvc.exception.BusinessException;
import ekuter.mvc.util.ManMadeStationDataUtil;
import mybatisPro.dataBase.SqlSessionHander;
import mybatisPro.myBatisDao.ManMadeStationMapper;
import mybatisPro.mybatisEntity.RainfallEntity;
import mybatisPro.mybatisEntity.ReportStatusEntity;
import mybatisPro.mybatisEntity.ReservoirWaterLevelEntity;
import mybatisPro.mybatisEntity.RiverWaterLevelEntity;
import mybatisPro.mybatisService.impl.ManMadeStaionService;
import mybatisPro.mybatisService.impl.StationService;
import mybatisPro.mybatisService.impl.WaterLevelService;

/**
 * 人工报讯业务层接口实现类
 * @author si.yu
 * @date 2017/08/22
 * @version 1.0
 * */
@Service
public class ManMadeStationServiceImpl implements ManMadeStaionService{

	private StationService stationService=new StationServiceImpl();
	
	private WaterLevelService waterLevelService = new WaterLevelServiceImpl();
	
	private static final Logger logger = LoggerFactory.getLogger(ManMadeStationServiceImpl.class);
	/**
	 * 人工上报数据(雨量数据插入)
	 * @param rainfallEntity
	 * @return
	 * */
	@Override
	public boolean ManMadeDataReport(List<ManMadeStationDataUtil> manMadeStationList,Date report_date) {
		try {
			
			return SqlSessionHander.SqlExecute(sqlsession ->{
				
				ManMadeStationMapper madeStationMapper = sqlsession.getMapper(ManMadeStationMapper.class);
			
				//插入数据记数
				int count = 0;
				//遍历人工站上报数据列表
				for(ManMadeStationDataUtil stationDataUtil : manMadeStationList){
					
					RainfallEntity rainfallEntity = new RainfallEntity();
					RiverWaterLevelEntity riverLevelEntity = new RiverWaterLevelEntity();
					ReservoirWaterLevelEntity resLevelEntity = new ReservoirWaterLevelEntity();
					
					Long sta_id = stationDataUtil.getSta_id();
					//根据测站ID查询测站类型
					Map<String,Object> entity = stationService.getStationInfoById(sta_id);
					if(null != entity){
						Long type = (Long) entity.get("station_type_id");
						if(null != type){
							
							//根据测站类型插入数据表数据
							if(Constants.RAINFALL_STATION.longValue() == type.longValue()){//雨量站
								//获取数据ID
								Long rainfall_id = stationDataUtil.getRainfall_id();
								//往雨量表中插入雨量数据
								Float rainfall = stationDataUtil.getRainfall();
								
								rainfallEntity.setCreatetime(new Date());
								//判断是新增数据还是修改数据
								if(null == rainfall_id){
									rainfallEntity.setSta_id(sta_id);
									rainfallEntity.setTime(report_date);
									if(null != rainfall){
										rainfallEntity.setTime_rainfall(rainfall);
										//调用新增雨量数据接口新增数据
										int insertFlag = madeStationMapper.insertIntoRainFallData(rainfallEntity);
										if(0 != insertFlag){
											count++;
											continue;
										}else{
											return false;
										}
									}else{
										continue;
									}
									
								}else{
									//调用雨量修改接口根据数据ID修改数据
									rainfallEntity.setId(rainfall_id);
									if(null == rainfall) {
										rainfallEntity.setTime_rainfall(0.0f);
									}else{
										rainfallEntity.setTime_rainfall(rainfall);
									}
									int updateFlag = madeStationMapper.updateRainFallData(rainfallEntity);
									if(0 != updateFlag){
										count++;
										continue;
									}else{
										return false;
									}
								}
								
							}else if(Constants.WATER_LEVEL_STATION.longValue() == type.longValue()){//水库水位站
								
								//获取水库水位数据ID
								Long res_id = stationDataUtil.getRes_id();
								//往水库水位表中插入水位数据
								Float resWaterLevel = stationDataUtil.getResvoirWaterLevel();
								
								resLevelEntity.setCreatetime(new Date());
								//根据水位查询计算出库容值
								if(null == resWaterLevel){
									
									resLevelEntity.setStorageCapacity(null);
								}else if(resWaterLevel<44.00){
									resLevelEntity.setStorageCapacity(0.0f);
								}else if(resWaterLevel > 85.00){
									resLevelEntity.setStorageCapacity(null);
								}else{
									Float storageData = 
											waterLevelService.getStorageCapacityByWaterLevel(resWaterLevel);
									if(null != storageData){
										resLevelEntity.setStorageCapacity(storageData);
									}else{
										logger.info(Constants.EXCEPTION_MAP.get("EX_WATERLEVEL_5")); 
										throw new BusinessException(Constants.EXCEPTION_MAP.get("EX_WATERLEVEL_5"));
									}
								}
								
								if(null == res_id){
									resLevelEntity.setSta_id(sta_id);
									resLevelEntity.setTime(report_date);
									if(null != resWaterLevel){
										resLevelEntity.setWaterLevel(resWaterLevel);
										int insertFlag = madeStationMapper.insertResvoirData(resLevelEntity);
										if(0 != insertFlag){
											count++;
											continue;
										}else{
											return false;
										}
									}else{
										continue;
									}
									
								}else{
									//调用水库水位修改接口根据数据ID修改数据
									resLevelEntity.setId(res_id);
									if(null == resWaterLevel){ 
										resLevelEntity.setWaterLevel(0.0f);
									}else{
										resLevelEntity.setWaterLevel(resWaterLevel);
									}
									int updateFlag = madeStationMapper.updateResvoirData(resLevelEntity);
									if(0 != updateFlag){
										count++;
										continue;
									}else{
										return false;
									}
								}
								
							}else if(Constants.SOIL_MOISTURE_STATION.longValue() == type.longValue()){//河道水位站
								//获取河道水位数据ID
								Long river_id = stationDataUtil.getRiver_id();
								
								//往河道水位表中插入水位数据
								Float riverWaterLevel = stationDataUtil.getRiverWaterLevel();
								
								riverLevelEntity.setCreatetime(new Date());
								
								if(null == river_id){
									riverLevelEntity.setSta_id(sta_id);
									riverLevelEntity.setTime(report_date);
									
									if(null != riverWaterLevel){
										riverLevelEntity.setWaterLevel(riverWaterLevel);
										int insertFlag = madeStationMapper.insertRiverData(riverLevelEntity);
										if(0 != insertFlag){
											count++;
											continue;
										}else{
											return false;
										}
									}else{
										continue;
									}
								}else{
									riverLevelEntity.setId(river_id);
									if(null == riverWaterLevel){ 
										riverLevelEntity.setWaterLevel(0.0f); 
									}else{
										riverLevelEntity.setWaterLevel(riverWaterLevel);
									}
									int updateFlag = madeStationMapper.updateRiverData(riverLevelEntity);
									if(0 != updateFlag){
										count++;
										continue;
									}else{
										return false;
									}
								}
								
							}else if(Constants.WATER_RAINFALL_STATION.longValue() == type.longValue()){//水库水文站

								//往雨量表和水库水位表中插入水位数据
								Long rainfall_id = stationDataUtil.getRainfall_id();//雨量数据ID
								
								//往雨量表中插入雨量数据
								Float rainfall = stationDataUtil.getRainfall();
								rainfallEntity.setTime_rainfall(rainfall);
								rainfallEntity.setCreatetime(new Date());
								
								Long res_id = stationDataUtil.getRes_id();//水库水位数据ID
								//往水库水位表中插入水位数据
								Float resWaterLevel = stationDataUtil.getResvoirWaterLevel();
								resLevelEntity.setWaterLevel(resWaterLevel);
								resLevelEntity.setCreatetime(new Date());
								
								//根据水位查询计算出库容值
								if(null == resWaterLevel){
									resLevelEntity.setStorageCapacity(null);
								}else if(resWaterLevel<44.00){
									resLevelEntity.setStorageCapacity(0.0f);
								}else if(resWaterLevel > 85.00){
									resLevelEntity.setStorageCapacity(null);
								}else{
									Float storageData = 
											waterLevelService.getStorageCapacityByWaterLevel(resWaterLevel);
									if(null != storageData){
										resLevelEntity.setStorageCapacity(storageData);
									}else{
										logger.info(Constants.EXCEPTION_MAP.get("EX_WATERLEVEL_5")); 
										throw new BusinessException(Constants.EXCEPTION_MAP.get("EX_WATERLEVEL_5"));
									}
								}
								
								
								if(null == rainfall_id && null == res_id){
									//调用新增雨量数据接口新增数据
									rainfallEntity.setSta_id(sta_id);
									rainfallEntity.setTime(report_date);
									
									int nowCount = 0;
									if(null != rainfall){
										int	insertRainfallFlag = madeStationMapper.insertIntoRainFallData(rainfallEntity);
										if(0 != insertRainfallFlag){
											nowCount++;
										}else{
											return false;
										}
									}
									
									//调用水库水位数据新增接口插入数据
									resLevelEntity.setSta_id(sta_id);
									resLevelEntity.setTime(report_date);
									
									if(null != resWaterLevel){
										int	insertResviorFlag = madeStationMapper.insertResvoirData(resLevelEntity);
										if(0 != insertResviorFlag){
											nowCount++;
										}else{
											return false;
										}
									}
									if(0 != nowCount){
										count++;
										continue;
									}else{
										return false;
									}
								}else if(null != rainfall_id && null != res_id){
									//调用修改雨量数据接口修改数据
									rainfallEntity.setId(rainfall_id);
									if(null == rainfall){ rainfallEntity.setTime_rainfall(0.0f); }
									int updateRainfallFlag = madeStationMapper.updateRainFallData(rainfallEntity);
									
									//调用水库水位数据修改接口修改数据
									resLevelEntity.setId(res_id);
									if(null == resWaterLevel){ resLevelEntity.setWaterLevel(0.0f); }
									int updateResviorFlag = madeStationMapper.updateResvoirData(resLevelEntity);
									
									if(0 != updateRainfallFlag && 0 != updateResviorFlag){
										count++;
										continue;
									}else{
										return false;
									}
								}else if(null == rainfall_id && null != res_id){
									//调用新增雨量数据接口新增数据
									rainfallEntity.setSta_id(sta_id);
									rainfallEntity.setTime(report_date);
								
									int nowCount = 0;
									if(null != rainfall){
										int insertRainfallFlag = madeStationMapper.insertIntoRainFallData(rainfallEntity);
										if(0 != insertRainfallFlag){
											nowCount++;
										}else{
											return false;
										}
									}
									 
									//调用水库水位数据修改接口修改数据
									resLevelEntity.setId(res_id);
									if(null == resWaterLevel) resLevelEntity.setWaterLevel(0.0f);
									int updateResviorFlag = madeStationMapper.updateResvoirData(resLevelEntity);
									if(0 != updateResviorFlag){
										nowCount++;
									}else{
										return false;
									}
									if(0 != nowCount){
										count++;
										continue;
									}else{
										return false;
									}
								}else if(null != rainfall_id && null == res_id){
									//调用修改雨量数据接口修改数据
									int nowCount = 0;
									rainfallEntity.setId(rainfall_id);
									if(null == rainfall){ rainfallEntity.setTime_rainfall(0.0f); }
									int updateRainfallFlag = madeStationMapper.updateRainFallData(rainfallEntity);
									if(0 != updateRainfallFlag){
										nowCount++;
									}else{
										return false;
									}
									
									//调用水库水位数据新增接口插入数据
									resLevelEntity.setSta_id(sta_id);
									resLevelEntity.setTime(report_date);
									
									if(null != resWaterLevel){
										int insertResviorFlag = madeStationMapper.insertResvoirData(resLevelEntity);
										if(0 != insertResviorFlag){
											nowCount++;
										}else{
											return false;
										}
									}
									
									
									if(0 != nowCount){
										count++;
										continue;
									}else{
										return false;
									}
								}
								
							}else if(Constants.SOIL_RAINFALL_STATION.longValue() == type.longValue()){//河道水文站
								//往雨量表和河道水位表中插入水位数据
								Long rainfall_id = stationDataUtil.getRainfall_id();//雨量数据ID
								//往雨量表中插入雨量数据
								Float rainfall = stationDataUtil.getRainfall();
								rainfallEntity.setTime_rainfall(rainfall);
								rainfallEntity.setCreatetime(new Date());
								
								Long river_id = stationDataUtil.getRiver_id();//河道水位数据ID
								//往河道水位表中插入水位数据
								Float riverWaterLevel = stationDataUtil.getRiverWaterLevel();
								riverLevelEntity.setWaterLevel(riverWaterLevel);
								riverLevelEntity.setCreatetime(new Date());
								
								if(null == rainfall_id && null == river_id){
									//调用新增雨量数据接口新增数据
									rainfallEntity.setSta_id(sta_id);
									rainfallEntity.setTime(report_date);

									int nowCount = 0;
									if(null != rainfall){
										int insertRainfallFlag = madeStationMapper.insertIntoRainFallData(rainfallEntity);
										if(0 != insertRainfallFlag){
											nowCount++;
										}else{
											return false;
										}
									}
									
									//调用水库水位数据新增接口插入数据
									riverLevelEntity.setSta_id(sta_id);
									riverLevelEntity.setTime(report_date);
									
									if(null != riverWaterLevel)	{
										int insertRiverFlag = madeStationMapper.insertRiverData(riverLevelEntity);
										if(0 != insertRiverFlag){
											nowCount++;
										}else{
											return false;
										}
									}	
									
									
									if(0 != nowCount){
										count++;
										continue;
									}else{
										return false;
									}
								}else if(null != rainfall_id && null != river_id){
									//调用修改雨量数据接口修改数据
									rainfallEntity.setId(rainfall_id);
									if(null == rainfall) rainfallEntity.setTime_rainfall(0.0f);
									int updateRainfallFlag = madeStationMapper.updateRainFallData(rainfallEntity);
									
									//调用河道水位数据修改接口修改数据
									riverLevelEntity.setId(river_id);
									if(null == riverWaterLevel) riverLevelEntity.setWaterLevel(0.0f);
									int updateRiverFlag = madeStationMapper.updateRiverData(riverLevelEntity);
									
									if(0 != updateRainfallFlag && 0 != updateRiverFlag){
										count++;
										continue;
									}else{
										return false;
									}
								}else if(null == rainfall_id && null != river_id){
									//调用新增雨量数据接口新增数据
									rainfallEntity.setSta_id(sta_id);
									rainfallEntity.setTime(report_date);
									
									int nowCount = 0;
									if(null != rainfall){
										int insertRainfallFlag = madeStationMapper.insertIntoRainFallData(rainfallEntity);
										if(0 != insertRainfallFlag){
											nowCount++;
										}else{
											return false;
										}
									}
									
									
									//调用河道水位数据修改接口修改数据
									riverLevelEntity.setId(river_id);
									if(null == riverWaterLevel) riverLevelEntity.setWaterLevel(0.0f);
									int updateRiverFlag = madeStationMapper.updateRiverData(riverLevelEntity);
									if(0 != updateRiverFlag){
										nowCount++;
									}else{
										return false;
									}
									
									if(0 != nowCount){
										count++;
										continue;
									}else{
										return false;
									}
								}else if(null != rainfall_id && null == river_id){
									//调用修改雨量数据接口修改数据
									rainfallEntity.setId(rainfall_id);
									int nowCount = 0;
									if(null == rainfall) rainfallEntity.setTime_rainfall(0.0f);
									int updateRainfallFlag = madeStationMapper.updateRainFallData(rainfallEntity);
									if(0 != updateRainfallFlag){
										nowCount++;
									}else{
										return false;
									}
									
									//调用水库水位数据新增接口插入数据
									riverLevelEntity.setSta_id(sta_id);
									riverLevelEntity.setTime(report_date);
									
									if(null != riverWaterLevel){
										int insertRiverFlag = madeStationMapper.insertRiverData(riverLevelEntity);
										if(0 != insertRiverFlag){
											nowCount++;
										}else{
											return false;
										}
									}
									
									
									if(0 != nowCount){
										count++;
										continue;
									}else{
										return false;
									}
								}
							}
						}else{
							logger.info(Constants.EXCEPTION_MAP.get("DATAERROR"));
							throw new BusinessException(Constants.EXCEPTION_MAP.get("DATAERROR"));
						}
					}else{
						logger.info(Constants.EXCEPTION_MAP.get("STATIONINFODETAIL"));
						throw new BusinessException(Constants.EXCEPTION_MAP.get("STATIONINFODETAIL"));
					}
				}
				//判断插入数据条数不为0时提交事务
				if(count != 0){
					sqlsession.commit();
					return true;
				}else{
					return false;
				}
			});
			
		} catch (Exception e) {
			logger.info(e.getMessage());
			throw new BusinessException(Constants.EXCEPTION_MAP.get("REPORTFAIL"));
		}
	}
	
	
	/**
	 * 根据上报数据的日期查询是否已有上报装填
	 * @param reportTime
	 * @return
	 * */
	@Override
	public ReportStatusEntity findReportStatus(String reportTime) {
		
		try {
			return SqlSessionHander.SqlExecute(sqlsession ->{
				ManMadeStationMapper madeStationMapper = sqlsession.getMapper(ManMadeStationMapper.class);
				ReportStatusEntity statusEntity = 
						madeStationMapper.findReportStatus(reportTime);
				return statusEntity;
			});
		} catch (Exception e) {
			logger.info(e.getMessage());
			throw new BusinessException(Constants.EXCEPTION_MAP.get("REPORTSTATUS_EXE"));
		}
	}


	/**
	 * 插入上报状态数据
	 * @Param statusEntity
	 * @return
	 * */
	@Override
	public Boolean insertReportStatus(ReportStatusEntity statusEntity) {
		try {
			return SqlSessionHander.SqlExecute(sqlsession ->{
				ManMadeStationMapper madeStationMapper = sqlsession.getMapper(ManMadeStationMapper.class);
				int insertFlag = 
						madeStationMapper.insertReportStatus(statusEntity);
				sqlsession.commit();
				if(0 == insertFlag){
					return false;
				}else{
					return true;
				}
				
			});
		} catch (Exception e) {
			logger.info(e.getMessage());
			throw new BusinessException(Constants.EXCEPTION_MAP.get("REPORTSTATUS_EXE2"));
		}
	}


	/**
	 * 查询时间段内的所有上报数据状态
	 * @param begin_time
	 * @param end_time
	 * @return
	 * */
	@Override
	public List<ReportStatusEntity> getListOfReportStatusData(String begin_time, String end_time) {
		try {
			return SqlSessionHander.SqlExecute(sqlsession ->{
				ManMadeStationMapper madeStationMapper = sqlsession.getMapper(ManMadeStationMapper.class);
				List<ReportStatusEntity> statusEntities = 
						madeStationMapper.getListOfReportStatusData(begin_time, end_time);
				return statusEntities;
				
			});
		} catch (Exception e) {
			logger.info(e.getMessage());
			throw new BusinessException(Constants.EXCEPTION_MAP.get("REPORTSTATUS_EXE3"));
		}
	}
}