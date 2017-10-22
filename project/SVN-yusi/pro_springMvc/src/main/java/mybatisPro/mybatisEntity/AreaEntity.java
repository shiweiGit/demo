package mybatisPro.mybatisEntity;

import java.io.Serializable;
import java.util.Date;

/**
 * 区域管理实体类
 * @author si.yu
 * @date 2017/7/19
 * @version 1.0
 * */
public class AreaEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Long area_id;//区域ID
	private String area_name;//区域名称
	private Integer algorithmType;//区域平均雨量算法类型（0算术平均，1系数平均）
	private String comment;//说明
	private Date modify_time = new Date();//修改时间
	
	@Override
	public String toString(){
		return "AreaEntity[area_id="+area_id+",comment="+comment
				+",area_name="+area_name+",algorithmType="+algorithmType+"]";
	}
	
	public Long getArea_id() {
		return area_id;
	}
	public void setArea_id(Long area_id) {
		this.area_id = area_id;
	}
	
	public String getArea_name() {
		return area_name;
	}
	public void setArea_name(String area_name) {
		this.area_name = area_name;
	}
	
	public Integer getAlgorithmType() {
		return algorithmType;
	}
	public void setAlgorithmType(Integer algorithmType) {
		this.algorithmType = algorithmType;
	}
	
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getModify_time() {
		return modify_time;
	}
	public void setModify_time(Date modify_time) {
		this.modify_time = modify_time;
	}
	
	
	
}
