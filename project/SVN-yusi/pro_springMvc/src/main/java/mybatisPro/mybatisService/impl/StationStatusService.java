package mybatisPro.mybatisService.impl;

import java.util.List;

import mybatisPro.mybatisEntity.StationStatusEntity;


/**
 * 测站状态统计业务层接口
 * @author si.yu
 * @date 2017/08/31
 * @version 1.0
 * */
public interface StationStatusService {

	
	/**
	 * 测站状态查询（mouth）
	 * @param begin_time
	 * @param end_time
	 * @return 
	 * */
	public List<StationStatusEntity> getStationStatusList(Long sta_id,String begin_time,String end_time);
}
