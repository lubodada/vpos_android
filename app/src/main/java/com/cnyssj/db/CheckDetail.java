package com.cnyssj.db;

/**
 * 盘点操作明细实体类
 *
 * @author Administrator
 */
public class CheckDetail {

    private String count_id;// 盘点唯一id
    private String title;// 商品名称
    private String productsn;// 商品条码
    private double quantity;// 商品数量
    private String updatetime;// 更新数量时间
    private int updatetype;// 更新数量方式 修改/添加
    private int storage_num;

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

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    public int getUpdatetype() {
        return updatetype;
    }

    public void setUpdatetype(int updatetype) {
        this.updatetype = updatetype;
    }

    public String getCount_id() {
        return count_id;
    }

    public void setCount_id(String count_id) {
        this.count_id = count_id;
    }

    public int getStorage_num() {
        return storage_num;
    }

    public void setStorage_num(int storage_num) {
        this.storage_num = storage_num;
    }

}
