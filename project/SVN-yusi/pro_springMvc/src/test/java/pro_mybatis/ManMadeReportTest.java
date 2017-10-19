package pro_mybatis;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ekuter.mvc.util.ManMadeStationDataUtil;
import mybatisPro.mybatisEntity.ReportStatusEntity;
import mybatisPro.mybatisService.ManMadeStationServiceImpl;
import mybatisPro.mybatisService.impl.ManMadeStaionService;

/**
 * 人工报讯测试类
 * */
public class ManMadeReportTest {

	private ManMadeStaionService manMadeStaionService;
	
	@Before
	public void setUp(){
		manMadeStaionService = new ManMadeStationServiceImpl();
	}
	
	@Test
	public void reportData(){
		
		List<ManMadeStationDataUtil> manMadeStationList = new ArrayList<ManMadeStationDataUtil>();
		
		ManMadeStationDataUtil man1 = new ManMadeStationDataUtil();
		ManMadeStationDataUtil man2 = new ManMadeStationDataUtil();
		ManMadeStationDataUtil man3 = new ManMadeStationDataUtil();
		ManMadeStationDataUtil man4 = new ManMadeStationDataUtil();
		ManMadeStationDataUtil man5 = new ManMadeStationDataUtil();
		
		man1.setRainfall(2.0f);
		man1.setSta_id(113l);
		man1.setRainfall_id(100l);
		
		man2.setResvoirWaterLevel(12.6f);
		man2.setSta_id(114l);
		man2.setRes_id(26l);
		
		man3.setRiverWaterLevel(6.8f);
		man3.setSta_id(117l);
		man3.setRiver_id(40l);
		
		man4.setRainfall(1.8f);
		man4.setResvoirWaterLevel(13.3f);
		man4.setSta_id(115l);
		man4.setRainfall_id(101l);
		man4.setRes_id(34l);
		
		man5.setRainfall(0.5f);
		man5.setRiverWaterLevel(6.5f);
		man5.setSta_id(116l);
		man5.setRainfall_id(102l);
		man5.setRiver_id(32l);
		
		manMadeStationList.add(man1);
		manMadeStationList.add(man2);
		manMadeStationList.add(man3);
		manMadeStationList.add(man4);
		manMadeStationList.add(man5);
		
		System.out.println(manMadeStationList);
		
		boolean reportFlag = manMadeStaionService.ManMadeDataReport(manMadeStationList,new Date());
		
		System.out.println(reportFlag);
		
	}
	
	@Test
	public void findResportStatus(){
		
		ReportStatusEntity statusEntity = 
				manMadeStaionService.findReportStatus("2017-08-12");
		System.out.println(statusEntity);
	}
	
	@Test
	public void getListOfReportStatusData(){
		
		List<ReportStatusEntity> statusEntities = 
				manMadeStaionService.getListOfReportStatusData("2017-08-01 00:00:00", "2017-08-24 23:59:59");
		
		System.out.println(statusEntities);
	}
}