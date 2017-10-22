package mybatisPro.mybatisEntity;

import java.io.Serializable;
import java.util.Date;

/**
 * 水库信息实体类
 * @author si.yu
 * @date 2017/6/27
 * @version 1.0
 * */
public class ReservoirEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Long id;//水库ID
	private String cnnmcd;//水库代码
	private String resnm;//水库名称
	private String restp;//水库类型
	private String addvcd;//行政区划代码
	private String adumnm;//管理单位
	private String damsite;//坝址所在地点
	private String esstym;//建成日期
	private String encl;//工程等别
	private String lvbslv;//水准基面
	private String rscci;//水库枢纽建筑物组成
	private String schdep;//调度主管部门
	private String saflev;//安全类别
	private String rsdasi;//水库病险情况
	private Float vlar;//流域面积
	private String comments;//备注
	private String Sort;//水库排序
	private Date modify_time = new Date();
	
	@Override
	public String toString(){
		return "ReservoirEntity[id="+id+",cnnmcd="+cnnmcd+",resnm="+resnm+",restp="+restp+",addvcd="+addvcd
				+",adumnm="+adumnm+",damsite="+damsite+",esstym="+esstym+",encl="+encl+""
				+",lvbslv="+lvbslv+",rscci="+rscci+",schdep="+schdep+",saflev="+saflev+""
				+",rsdasi="+rsdasi+",vlar="+vlar+",comments="+comments+",Sort="+Sort+"]";
	}
	
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getCnnmcd() {
		return cnnmcd;
	}
	public void setCnnmcd(String cnnmcd) {
		this.cnnmcd = cnnmcd;
	}
	
	public String getResnm() {
		return resnm;
	}
	public void setResnm(String resnm) {
		this.resnm = resnm;
	}
	
	public String getRestp() {
		return restp;
	}
	public void setRestp(String restp) {
		this.restp = restp;
	}
	
	public String getAddvcd() {
		return addvcd;
	}
	public void setAddvcd(String addvcd) {
		this.addvcd = addvcd;
	}
	
	public String getAdumnm() {
		return adumnm;
	}
	public void setAdumnm(String adumnm) {
		this.adumnm = adumnm;
	}
	
	public String getDamsite() {
		return damsite;
	}
	public void setDamsite(String damsite) {
		this.damsite = damsite;
	}
	
	public String getEsstym() {
		return esstym;
	}
	public void setEsstym(String esstym) {
		this.esstym = esstym;
	}
	
	public String getEncl() {
		return encl;
	}
	public void setEncl(String encl) {
		this.encl = encl;
	}
	
	public String getLvbslv() {
		return lvbslv;
	}
	public void setLvbslv(String lvbslv) {
		this.lvbslv = lvbslv;
	}
	
	public String getRscci() {
		return rscci;
	}
	public void setRscci(String rscci) {
		this.rscci = rscci;
	}
	
	public String getSchdep() {
		return schdep;
	}
	public void setSchdep(String schdep) {
		this.schdep = schdep;
	}
	
	public String getSaflev() {
		return saflev;
	}
	public void setSaflev(String saflev) {
		this.saflev = saflev;
	}
	
	public String getRsdasi() {
		return rsdasi;
	}
	public void setRsdasi(String rsdasi) {
		this.rsdasi = rsdasi;
	}
	
	public Float getVlar() {
		return vlar;
	}
	public void setVlar(Float vlar) {
		this.vlar = vlar;
	}
	
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	public String getSort() {
		return Sort;
	}
	public void setSort(String sort) {
		Sort = sort;
	}

	public Date getModify_time() {
		return modify_time;
	}
	public void setModify_time(Date modify_time) {
		this.modify_time = modify_time;
	}
	
}
