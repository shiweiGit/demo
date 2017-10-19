package mybatisPro.myBatisDao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

/**
 * 
 *
 * @date 2017年8月11日
 *
 * @version 1.0
 */
public interface RainfallStatisticsMapper {

	/**
	 * 雨量统计（小时）
	 */
	public void statisticsByHour(@Param("now_time") String now_time);

	/**
	 * 雨量统计（天）
	 */
	public void statisticsByDays(@Param("now_time") String now_time);

	/**
	 * 雨量统计（月）
	 */
	public void statisticsByMonths(@Param("now_time") String now_time);

	/**
	 * 校验雨量信息
	 * 
	 * @param now_time
	 * @param type
	 * @return
	 */
	public int checkRainInfo(@Param("now_time") String now_time, @Param("type") String type);

	/**
	 * 删除雨量信息
	 * 
	 * @param now_time
	 * @return
	 */
	public int deleteRainInfo(@Param("now_time") String now_time, @Param("type") String type);

	/**
	 * 年雨量统计（查询）
	 * 
	 * @return
	 */
	public Map<String, Object> statisticsByYear(@Param("year") String year, @Param("type") Integer type,
			@Param("sta_id") Long sta_id);

	/**
	 * 天雨量统计（查询）
	 * 
	 * @param days
	 * @param type
	 * @param hour_list
	 * @return
	 */
	public Map<String, Object> statisticsByDay(@Param("start_time") String start_time,
			@Param("end_time") String end_time, @Param("hour_list") List<String> hour_list,
			@Param("sta_id") Long sta_id);

	/**
	 * 月雨量统计（查询）
	 * 
	 * @param months
	 * @param type
	 * @param month_list
	 * @return
	 */
	public Map<String, Object> statisticsByMonth(@Param("months") String months, @Param("type") Integer type,
			@Param("month_list") List<String> month_list, @Param("sta_id") Long sta_id);

	/**
	 * 查询
	 * 
	 * @param hour
	 * @param day
	 * @param sta_id
	 * @return
	 */
	public Long getHourInfo(@Param("time") String time, @Param("sta_id") Long sta_id);

	/**
	 * 获取所有雨量站ID
	 * 
	 * @return
	 */
	public List<Long> getAllStaId(@Param("type") Integer type);

	/**
	 * 插入小时历史雨量表
	 * 
	 * @param time_rainfall
	 * @param time
	 * @param sta_id
	 * @return
	 */
	public int insertHourInfo(@Param("time_rainfall") Float time_rainfall, @Param("time") String time,
			@Param("sta_id") Long sta_id);

	/**
	 * 更新小时历史雨量表
	 * 
	 * @param id
	 * @param time_rainfall
	 * @return
	 */
	public int updateHourInfo(@Param("id") Long id, @Param("time_rainfall") Float time_rainfall);

	public int updateHourInfos(@Param("id") Long id, @Param("time_rainfall") Float time_rainfall);

}