package com.jhj.pos.storage;

/**
 * 入库实体类
 * @author lb
 */
public class Storage {
	
	private String id;//商品id
	private String title;//商品名称
	private String productsn;//商品条码
	private double quantity;//商品数量
	private String createtime;//插入商品时间
	private String updatetime;//更新数量时间
	private String count_time;//入库时间
	private int status;//上传状态
	private String count_id;//入库唯一id
	private String shelf;//货架
	private String storage_people;//入库人
	private String remark;//备注
	private int update_status;//数量更新状态
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getProductsn() {
		return productsn;
	}
	public void setProductsn(String productsn) {
		this.productsn = productsn;
	}
	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	public String getCreatetime() {
		return createtime;
	}
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
	public String getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(String updatetime) {
		this.updatetime = updatetime;
	}
	public String getCount_time() {
		return count_time;
	}
	public void setCount_time(String count_time) {
		this.count_time = count_time;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getCount_id() {
		return count_id;
	}
	public void setCount_id(String count_id) {
		this.count_id = count_id;
	}
	public String getShelf() {
		return shelf;
	}
	public void setShelf(String shelf) {
		this.shelf = shelf;
	}
	public String getStorage_people() {
		return storage_people;
	}
	public void setStorage_people(String storage_people) {
		this.storage_people = storage_people;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public int getUpdate_status() {
		return update_status;
	}
	public void setUpdate_status(int update_status) {
		this.update_status = update_status;
	}
	
	
	
}
