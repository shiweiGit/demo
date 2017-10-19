package pro_mybatis;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import ekuter.mvc.constants.Constants;
import mybatisPro.mybatisEntity.AreaEntity;
import mybatisPro.mybatisEntity.StationInfoEntityUtil;
import mybatisPro.mybatisService.AreaServiceImpl;
import mybatisPro.mybatisService.impl.AreaService;

/**
 * 区域管理测试类
 * @author si.yu
 * @date 2017/7/19
 * @version 1.0
 * */
public class AreaTest {
	
	private AreaService aService;
	
	@Before
	public void setUp(){
		aService = new AreaServiceImpl();
	}
	
	@Test
	public void getAreaList(){
		 
		List<AreaEntity> aEntities = aService.findListOfArea();
		
		System.out.println(aEntities);
	}
	
	@Test
	public void getAreaInfoDeatail(){
		AreaEntity aEntity = aService.getAreaInfoDetail(1l);
		System.out.println(aEntity);
	}
	
	@Test
	public void createArea(){
		
		AreaEntity aEntity = new AreaEntity();
		aEntity.setArea_name("123");
		aEntity.setComment("123");
		aEntity.setAlgorithmType(0);
		boolean flag = 
				aService.createArea(aEntity);
		System.out.println(flag);
	}
	
	@Test
	public void updateArea(){
		
		AreaEntity aEntity = new AreaEntity();
		aEntity.setArea_id(4l);
		aEntity.setAlgorithmType(0);
		aEntity.setComment("水库的全库区域");
		boolean flag = 
				aService.updateAreaInfo(aEntity);
		System.out.println(flag);   
	}
	
	@Test
	public void findAreaStationsInfo(){
		List<Map<String,Object>> map = 
				aService.findAreaStationsInfo(Constants.RAINFALL_STATION,"2017-07-01 07:00:00","2017-07-01 09:00:00");
		
		System.out.println(map);
	}
	
	@Test
	public void getAreaStationsInfo(){
		StationInfoEntityUtil map = 
				aService.getAreaStationsInfoByAreaId(12l,"2017-07-01 07:00:00","2017-07-01 09:00:00");
		
		System.out.println(map);
	}
	
	@Test
	public void findInfoByName(){
		boolean flag = aService.findAreaInfoByName("上游");
		System.out.println(flag);
	}
	
	@Test
	public void getStationParam(){
		
		List<Map<String, Object>> staParam = aService.getStationsParamByAreaId(1l, 2l);
		System.out.println(staParam);
		
	}
}