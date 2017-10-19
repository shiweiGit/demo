package mybatisPro.mybatisEntity;

import java.io.Serializable;
import java.util.Date;

/**
 * 水库水位信息实体类
 * @author si.yu
 * @date 2017/08/10
 * @version 1.0
 * */
public class ReservoirWaterLevelEntity implements Serializable{

	private static final long serialVersionUID = 1L;

	private Long id;
	private Long sta_id;//测站ID
	private Date time;//数据上报时间
	private Float waterLevel;//库水位
	private Float storageCapacity;//库容
	private Date createtime = new Date();//创建/修改时间
	
	
	@Override
	public String toString(){
		return "ReservoirWaterLevelEntity[id="+id+",sta_id="+sta_id+",time="+time
				+",waterLevel="+waterLevel+",storageCapacity="+storageCapacity+",createtime="+createtime+"]";
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public Long getSta_id() {
		return sta_id;
	}
	public void setSta_id(Long sta_id) {
		this.sta_id = sta_id;
	}
	
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	
	public Float getWaterLevel() {
		return waterLevel;
	}
	public void setWaterLevel(Float waterLevel) {
		this.waterLevel = waterLevel;
	}
	
	public Float getStorageCapacity() {
		return storageCapacity;
	}
	public void setStorageCapacity(Float storageCapacity) {
		this.storageCapacity = storageCapacity;
	}
	
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

}