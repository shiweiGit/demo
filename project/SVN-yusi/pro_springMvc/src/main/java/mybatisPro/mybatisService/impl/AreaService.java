package mybatisPro.mybatisService.impl;

import java.util.List;
import java.util.Map;
import mybatisPro.mybatisEntity.AreaEntity;
import mybatisPro.mybatisEntity.StationInfoEntityUtil;

/**
 * 区域管理业务层接口
 * @author si.yu
 * @date 2017/7/19
 * @version 1.0
 * */
public interface AreaService {

	/**
	 * 查询区域列表
	 * @return
	 * */
	public List<AreaEntity> findListOfArea();
	
	/**
	 * 查询区域详情信息
	 * @param area_id
	 * @return
	 * */
	public AreaEntity getAreaInfoDetail(Long area_id);
	
	/**
	 * 修改区域信息
	 * @param areaEntity
	 * @return
	 * */
	public boolean updateAreaInfo(AreaEntity areaEntity);
	
	/**
	 * 创建区域
	 * @param areaEntity
	 * @return
	 * */
	public boolean createArea(AreaEntity areaEntity);
	
	/**
	 * 查询区域对应的测站
	 * @param sta_type_id 
	 * @return
	 * */
	public List<Map<String, Object>> findAreaStationsInfo(Long sta_type_id,String begin_time,String end_time);
	
	/**
	 * 根据区域ID查询区域测站信息列表
	 * @param sta_type_id 
	 * @param begin_time  
	 * @param end_time 
	 * @param area_id 
	 * @return
	 * */
	public StationInfoEntityUtil getAreaStationsInfoByAreaId(Long sta_id
			,String begin_time,String end_time);
	
	/**
	 * 根据区域名称查询区域信息
	 * @param area_name
	 * @return
	 * */
	public boolean findAreaInfoByName(String area_name);
	
	/**
	 * 根据区域ID查询测站系数列表
	 * @param area_id
	 * @return
	 * */
	public List<Map<String, Object>> getStationsParamByAreaId(Long sta_type_id,Long area_id);
	
	/**
	 * 根据区域ID和测站类型查询测站id 列表
	 * @param area_id
	 * @param type_id
	 * @return
	 * */
	public List<Long> getStaIdsByAreaId(Long sta_type_id,Long area_id);
}
