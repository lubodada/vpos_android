package com.cnyssj.db.util;

/**
 * 商品信息实体类
 * @author lubo
 * @time 2016-11-11 
 */
public class Goods {
	//商品主键id
	private String _id;
	//商品id
	private String id;
	//商品类型
	private String title;
	//商品条码
	private String productsn;
    //商品价格
	private double price;
    //商品描述
	private String thumb;
	public Goods(){
    	
    }
    
    public Goods(String id, String title, String productsn, double price, String thumb) {
		// TODO Auto-generated constructor stub
    	this.id=id;
    	this.title=title;
    	this.productsn=productsn;
    	this.price=price;
    	this.thumb=thumb;
    	
	}
    public String get_id() {
    	return _id;
    }
    
    public void set_id(String _id) {
    	this._id = _id;
    }
	public String getThumb() {
		return thumb;
	}
	public void setThumb(String thumb) {
		this.thumb = thumb;
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
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public String getId() {
    	return id;
    }
    public void setId(String id) {
    	this.id = id;
    }
	
    
    

}
