package mybatisPro.myBatisDao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import mybatisPro.mybatisEntity.ReservoirWaterLevelEntity;
import mybatisPro.mybatisEntity.RiverWaterLevelEntity;

/**
 * 水位数据查询管理数据层接口
 * @author si.yu
 * @date 2017/08/08
 * @version 1.0
 * */
public interface WaterLevelMapper {

	/**
	 * 根据时间段查询水库水位测站数据
	 * @param begin_time
	 * @param end_time
	 * @param sta_id
	 * */
	public Map<String, Object> getWaterLevelInfo(@Param("beginTime")String begin_time
			,@Param("endTime")String end_time,@Param("staId")Long sta_id);
	
	/**
	 * 根据时间段查询河道水位测站数据 
	 * @param begin_time
	 * @param end_time
	 * @param sta_id
	 **/
	public Map<String, Object> getSoilWaterLevelInfo(@Param("beginTime")String begin_time
			,@Param("endTime")String end_time,@Param("staId")Long sta_id);
	
	/**
	 * 查询时间段内水库水位数据列表
	 * @param begin_time
	 * @param end_time
	 * @param sta_id
	 * @return
	 * */
	public List<ReservoirWaterLevelEntity> getResWaterLevelInfoList(@Param("beginTime")String begin_time
			,@Param("endTime")String end_time,@Param("staId")Long sta_id);
	
	/**
	 * 查询时间段内河道水位数据列表
	 * @param begin_time
	 * @param end_time
	 * @param sta_id
	 * @return
	 * */
	public List<RiverWaterLevelEntity> getRiverWaterLevelInfoList(@Param("beginTime")String begin_time
			,@Param("endTime")String end_time,@Param("staId")Long sta_id );
	
	/**
	 * 根据水库水位数据查询对应库容数据
	 * @param waterLevel
	 * @return
	 * */
	public Float getStorageCapacityByWaterLevel(@Param("waterLevel")Float waterLevel);
}