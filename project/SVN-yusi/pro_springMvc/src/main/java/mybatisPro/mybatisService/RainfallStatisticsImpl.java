package mybatisPro.mybatisService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ekuter.mvc.constants.Constants;
import ekuter.mvc.exception.BusinessException;
import ekuter.mvc.util.DateUtil;
import mybatisPro.dataBase.SqlSessionHander;
import mybatisPro.myBatisDao.RainfallStatisticsMapper;
import mybatisPro.mybatisService.impl.RainfallStatisticsService;

@Service
public class RainfallStatisticsImpl implements RainfallStatisticsService {

	private static final Logger logger = LoggerFactory.getLogger(RainfallStatisticsImpl.class);

	/**
	 * 雨量年统计
	 */
	@Override
	public List<Map<String, Object>> statisticsByYear(String year, Integer type) {
		try {
			return SqlSessionHander.SqlExecute(sqlsession -> {
				RainfallStatisticsMapper rainMapper = sqlsession.getMapper(RainfallStatisticsMapper.class);
				List<Long> all_sta_id = rainMapper.getAllStaId(type);
				List<Map<String, Object>> year_rain_list = new ArrayList<Map<String, Object>>();
				if (null != all_sta_id && !all_sta_id.isEmpty()) {
					all_sta_id.forEach(sta_id -> {
						Map<String, Object> rain_info = rainMapper.statisticsByYear(year, type, sta_id);
						if (null == rain_info.get("total")) {
							rain_info.put("total", 0);
						}
						for (String key : DateUtil.year_info) {
							if (null == rain_info.get(key)) {
								rain_info.put(key, "—");
							}
						}
						year_rain_list.add(rain_info);
					});
				}
				return year_rain_list;
			});
		} catch (Exception e) {
			logger.info(e.getMessage());
			throw new BusinessException(Constants.EXCEPTION_MAP.get("EX_AREA_1"));
		}
	}

	/**
	 * 雨量天统计
	 */
	@Override
	public List<Map<String, Object>> statisticsByDay(String days) {
		try {
			return SqlSessionHander.SqlExecute(sqlsession -> {
				DateUtil dateUtile = new DateUtil();
				List<String> hour_list = Arrays.asList(dateUtile.hour_info);
				String end_time = dateUtile.getFutureDates(days);
				String start_time = days + " " + "08:00:00";
				RainfallStatisticsMapper rainMapper = sqlsession.getMapper(RainfallStatisticsMapper.class);
				List<Long> all_sta_id = rainMapper.getAllStaId(6);
				List<Map<String, Object>> day_rain_list = new ArrayList<Map<String, Object>>();
				if (null != all_sta_id && !all_sta_id.isEmpty()) {
					all_sta_id.forEach(sta_id -> {
						Map<String, Object> rain_info = rainMapper.statisticsByDay(start_time, end_time, hour_list,
								sta_id);
						if (null == rain_info.get("total")) {
							rain_info.put("total", 0);
						}
						for (String key : hour_list) {
							if (null == rain_info.get(key)) {
								rain_info.put(key, "—");
							}
						}
						day_rain_list.add(rain_info);
					});
				}
				return day_rain_list;
			});
		} catch (Exception e) {
			logger.info(e.getMessage());
			throw new BusinessException(Constants.EXCEPTION_MAP.get("EX_AREA_1"));
		}
	}

	/**
	 * 雨量月统计
	 */
	@Override
	public List<Map<String, Object>> statisticsByMonth(String month, Integer type) {
		try {
			return SqlSessionHander.SqlExecute(sqlsession -> {
				DateUtil dateUtile = new DateUtil();
				String[] months_info = dateUtile.checkMonthsInfo(month);
				if (0 == months_info.length) {
					return null;
				}
				List<String> month_list = Arrays.asList(months_info);
				RainfallStatisticsMapper rainMapper = sqlsession.getMapper(RainfallStatisticsMapper.class);
				List<Long> all_sta_id = rainMapper.getAllStaId(type);
				List<Map<String, Object>> months_rain_list = new ArrayList<Map<String, Object>>();
				if (null != all_sta_id && !all_sta_id.isEmpty()) {
					all_sta_id.forEach(sta_id -> {
						Map<String, Object> rain_info = rainMapper.statisticsByMonth(month, type, month_list, sta_id);
						if (null == rain_info.get("total")) {
							rain_info.put("total", 0);
						}
						for (String key : months_info) {
							if (null == rain_info.get(key)) {
								rain_info.put(key, "—");
							}
						}
						months_rain_list.add(rain_info);
					});
				}
				return months_rain_list;
			});
		} catch (Exception e) {
			logger.info(e.getMessage());
			throw new BusinessException(Constants.EXCEPTION_MAP.get("EX_AREA_1"));
		}
	}

}
