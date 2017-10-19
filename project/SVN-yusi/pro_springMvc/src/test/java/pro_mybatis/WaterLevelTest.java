package pro_mybatis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import ekuter.mvc.constants.Constants;
import mybatisPro.mybatisEntity.ReservoirWaterLevelEntity;
import mybatisPro.mybatisService.StationServiceImpl;
import mybatisPro.mybatisService.WaterLevelServiceImpl;
import mybatisPro.mybatisService.impl.StationService;
import mybatisPro.mybatisService.impl.WaterLevelService;

/**
 * 水位数据查询测试类
 * @author si.yu
 * @date 2017/08/08
 * @version 1.0
 * */
public class WaterLevelTest {

	private WaterLevelService wService;
	private StationService stationService;
	
	@Before
	public void setUp(){
		wService = new WaterLevelServiceImpl();
		stationService = new StationServiceImpl();
	}
	
	@Test
	public void getWaterLevelInfo(){
		List<Long> idListAll = new ArrayList<Long>();
		
		List<Long> idList1 = 
				stationService.getStationIdByTypeId(Constants.WATER_LEVEL_STATION);
		List<Long> idList2 = 
				stationService.getStationIdByTypeId(Constants.WATER_RAINFALL_STATION);
		
		idListAll.addAll(idList1);
		idListAll.addAll(idList2);
		
		for(Long staId : idListAll){
			Map<String, Object> info = 
					wService.getWaterLevelInfo("2017-07-01 08:00:00", "2017-07-03 08:00:00", staId);
			System.out.println(staId);
			System.out.println(info);
		}
	}
	
	@Test
	public void getStorageCapation(){
		
		Float storage = wService.getStorageCapacityByWaterLevel(78.65f); 
		System.out.println(storage);
	}
	
	@Test
	public void getResWaterLevelInfoList(){
		List<ReservoirWaterLevelEntity> reservoirWaterLevelEntities = 
				wService.getResWaterLevelInfoList("2017-07-01 08:00:00", "2017-10-03 08:00:00", 246l);
		
		System.out.println(reservoirWaterLevelEntities);
	}
}