package mybatisPro.mybatisEntity;

import java.io.Serializable;

/**
 * 平均降雨量测站系数实体类
 * @author si.yu
 * @date 2017/07/26
 * @version 1.0
 * */
public class StationParamEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Long id;//系数ID
	private Long area_id;//区域ID
	private Long sta_id;//测站ID
	private Float param;//系数
	
	@Override
	public String toString(){
		return "StationParamEntity[id="+id+",area_id="+area_id+",sta_id="+sta_id+",param="+param+"]";
	}

	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getArea_id() {
		return area_id;
	}

	public void setArea_id(Long area_id) {
		this.area_id = area_id;
	}

	public Long getSta_id() {
		return sta_id;
	}

	public void setSta_id(Long sta_id) {
		this.sta_id = sta_id;
	}

	public Float getParam() {
		return param;
	}

	public void setParam(Float param) {
		this.param = param;
	}
	
	
}
