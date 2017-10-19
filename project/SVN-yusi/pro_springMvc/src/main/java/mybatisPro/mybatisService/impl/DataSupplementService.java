package mybatisPro.mybatisService.impl;

import java.util.List;
import java.util.Map;

/**
 * 自动站数据增补业务层接口
 * @author si.yu
 * @date 2017/09/01
 * @version 1.0
 * */
public interface DataSupplementService {

	/**
	 * 查询雨量测站数据
	 * @param sta_id
	 * @param begin_time
	 * @param end_time
	 * @return
	 * */
	public List<Map<String, Object>> getRainfallStationData(Long sta_id,String begin_time,String end_time);
	
	/**
	 * 查询水库水位站测站数据
	 * @param sta_id
	 * @param begin_time
	 * @param end_time
	 * @return
	 * */
	public List<Map<String, Object>> getResvoirStationData(Long sta_id,String begin_time,String end_time);
	
	/**
	 * 查询河道水位站测站数据
	 * @param sta_id
	 * @param begin_time
	 * @param end_time
	 * @return
	 * */
	public List<Map<String, Object>> getRiverStationData(Long sta_id,String begin_time,String end_time);
	

	/**
	 * 自动站增补数据
	 * @param typeFlag
	 * @param sta_id
	 * @param time
	 * @param monitorData
	 * @return
	 * */
	public boolean insertDataSupplement(String typeFlag,Long sta_id,String time,Float monitorData);
	
	/**
	 * 自动站修改数据
	 * @param typeFlag
	 * @param sta_id
	 * @param time
	 * @param monitorData
	 * @return
	 * */
	public boolean updateDataSupplement(String typeFlag,Long sta_id,Long data_id,String time,Float monitorData);
}