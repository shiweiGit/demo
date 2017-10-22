package mybatisPro.mybatisService;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ekuter.mvc.constants.Constants;
import ekuter.mvc.exception.BusinessException;
import mybatisPro.dataBase.SqlSessionHander;
import mybatisPro.myBatisDao.WaterLevelMapper;
import mybatisPro.mybatisEntity.ReservoirWaterLevelEntity;
import mybatisPro.mybatisEntity.RiverWaterLevelEntity;
import mybatisPro.mybatisService.impl.WaterLevelService;

/**
 * 水位数据查询业务层实现
 * @author si.yu
 * @date 2017/08/08
 * @version 1.0
 * */
@Service
public class WaterLevelServiceImpl implements WaterLevelService{

	private static final Logger logger = LoggerFactory.getLogger(WaterLevelServiceImpl.class);
	
	/**
	 * 根据时间段查询水位测站数据
	 * @param begin_time
	 * @param end_time
	 * @param sta_id
	 * */
	@Override
	public Map<String, Object> getWaterLevelInfo(String begin_time, String end_time, Long sta_id) {
		try {
			return SqlSessionHander.SqlExecute(sqlsession->{
				WaterLevelMapper waterLevelMapper = sqlsession.getMapper(WaterLevelMapper.class);
				Map<String,Object> waterLevelInfo = 
						waterLevelMapper.getWaterLevelInfo(begin_time, end_time, sta_id);
				return waterLevelInfo;
			});
		} catch (Exception e) {
			logger.info(e.getMessage());
			throw new BusinessException(Constants.EXCEPTION_MAP.get("EX_WATERLEVEL_1"));
		}
	}

	
	/**
	 *根据时间段查询河道水位测站数据 
	 * @param begin_time
	 * @param end_time
	 * @param sta_id
	 **/
	@Override
	public Map<String, Object> getSoilWaterLevelInfo(String begin_time, String end_time, Long sta_id) {
		try {
			return SqlSessionHander.SqlExecute(sqlsession->{
				WaterLevelMapper waterLevelMapper = sqlsession.getMapper(WaterLevelMapper.class);
				Map<String,Object> soilWaterLevelInfo = 
						waterLevelMapper.getSoilWaterLevelInfo(begin_time, end_time, sta_id);
				return soilWaterLevelInfo;
			});
		} catch (Exception e) {
			logger.info(e.getMessage());
			throw new BusinessException(Constants.EXCEPTION_MAP.get("EX_WATERLEVEL_2"));
		}
	}


	
	/**
	 * 查询时间段内水库水位数据列表
	 * @param begin_time
	 * @param end_time
	 * @param sta_id
	 * @return
	 * */
	@Override
	public List<ReservoirWaterLevelEntity> getResWaterLevelInfoList(String begin_time, String end_time, Long sta_id) {
		try {
			return SqlSessionHander.SqlExecute(sqlsession->{
				WaterLevelMapper waterLevelMapper = sqlsession.getMapper(WaterLevelMapper.class);
				List<ReservoirWaterLevelEntity> rEntities = 
						waterLevelMapper.getResWaterLevelInfoList(begin_time, end_time, sta_id);
				return rEntities;
			});
		} catch (Exception e) {
			logger.info(e.getMessage());
			throw new BusinessException(Constants.EXCEPTION_MAP.get("EX_WATERLEVEL_3"));
		}
	}


	/**
	 * 查询时间段内河道水位数据列表
	 * @param begin_time
	 * @param end_time
	 * @param sta_id
	 * @return
	 * */
	@Override
	public List<RiverWaterLevelEntity> getRiverWaterLevelInfoList(String begin_time, String end_time, Long sta_id) {
		try {
			return SqlSessionHander.SqlExecute(sqlsession->{
				WaterLevelMapper waterLevelMapper = sqlsession.getMapper(WaterLevelMapper.class);
				List<RiverWaterLevelEntity> riverWaterLevelEntities = 
						waterLevelMapper.getRiverWaterLevelInfoList(begin_time, end_time, sta_id);
				return riverWaterLevelEntities;
			});
		} catch (Exception e) {
			logger.info(e.getMessage());
			throw new BusinessException(Constants.EXCEPTION_MAP.get("EX_WATERLEVEL_4"));
		}
	}


	/**
	 * 根据水库水位数据查询对应库容数据
	 * @param waterLevel
	 * @return
	 * */
	@Override
	public Float getStorageCapacityByWaterLevel(Float waterLevel) {
		try {
			return SqlSessionHander.SqlExecute(sqlsession->{
				WaterLevelMapper waterLevelMapper = sqlsession.getMapper(WaterLevelMapper.class);
				Float storageCapacity = waterLevelMapper.getStorageCapacityByWaterLevel(waterLevel);
				if(null != storageCapacity){
					return storageCapacity;
				}else{
					throw new BusinessException(Constants.EXCEPTION_MAP.get("EX_WATERLEVEL_5"));
				}
			});
		} catch (Exception e) {
			logger.info(e.getMessage()); 
			throw new BusinessException(Constants.EXCEPTION_MAP.get("EX_WATERLEVEL_5"));
		}
	}

	
}
