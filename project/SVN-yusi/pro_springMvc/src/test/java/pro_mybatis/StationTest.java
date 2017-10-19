package pro_mybatis;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import mybatisPro.mybatisEntity.ParamCodeEntity;
import mybatisPro.mybatisEntity.StationEntity;
import mybatisPro.mybatisService.StationServiceImpl;
import mybatisPro.mybatisService.impl.StationService;

/**
 * 测站信息管理测试类
 * @author si.yu
 * @date 2017/7/17
 * @version 1.0
 * */
public class StationTest {

	private StationService stationService;
	
	@Before
	public void setUp(){
		
		stationService = new StationServiceImpl();
	}
	
	@Test
	public void createStation(){
		
		StationEntity stationEntity = new StationEntity();
		
		stationEntity.setSys_code("#001");
		stationEntity.setStcd("O-7301");
		stationEntity.setStation_name("大兴水库段莘河水位站");
		stationEntity.setLongitude(2231.744558F);
		stationEntity.setLatitude(1111.238956F);
		stationEntity.setStation_location("大兴水库");
		stationEntity.setStation_type_id(1l);
		stationEntity.setArea_id(1l);
		stationEntity.setComments("承德水库最重要的水位检测站");
		
//		boolean flag = 
//				stationService.createStation(stationEntity);
//		
//		System.out.println(flag);
		
	}
	
	@Test
	public void getStationList(){
		
		List<Map<String, Object>> stationEntities = 
				stationService.getStationList(6);
		
		System.out.println(stationEntities);
	}
	
	@Test
	public void getStationInfoById(){
		
		Map<String, Object> stationEntity = 
				stationService.getStationInfoById(152L);
		
		System.out.println(stationEntity);
	}
	
	@Test
	public void updateStationInfo(){
		
		StationEntity stationEntity = new StationEntity();
		stationEntity.setId(12l);
		stationEntity.setX(5564.123345F);
		stationEntity.setY(7848.45321F);
		
		boolean updateFlag = 
				stationService.updateStationInfo(stationEntity);
		
		System.out.println(updateFlag);
	}
	
	
	@Test
	public void findStaInfobyName(){
		boolean findflag = 
				stationService.findStationInfoByStaName("雨量站");
		System.out.println(findflag);
	}
	
	@Test
	public void findStaRainfallList(){
		
		Map<String, Object> infoList = 
				stationService.getRainfallStationList("2017-07-01 07:00:00","2017-07-01 09:00:00", 12l);
		
		System.out.println(infoList);
	}
	
	@Test
	public void findStaInfoBySysCode(){
		StationEntity stationEntity = 
				stationService.findStationInfoBySysCode("#001");
		System.out.println(stationEntity);
	}
	
	@Test
	public void getParamCodeByStaId(){
		List<ParamCodeEntity> stationEntity = 
				stationService.getParamCodeListByStaId(157L);
		System.out.println(stationEntity);
	}
}