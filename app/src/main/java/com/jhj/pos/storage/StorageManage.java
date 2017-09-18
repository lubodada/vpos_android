package com.jhj.pos.storage;

/**
 * 入库管理实体类
 * @author lb
 */
public class StorageManage {

	private String count_id;//入库唯一id
	private int storage_status;//入库状态  0:未完成  1：已完成   -1:无数据
	private String count_time;//入库时间
	private String remark;//备注
	
	public String getCount_id() {
		return count_id;
	}
	public void setCount_id(String count_id) {
		this.count_id = count_id;
	}
	public int getStorage_status() {
		return storage_status;
	}
	public void setStorage_status(int storage_status) {
		this.storage_status = storage_status;
	}
	public String getCount_time() {
		return count_time;
	}
	public void setCount_time(String count_time) {
		this.count_time = count_time;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}
