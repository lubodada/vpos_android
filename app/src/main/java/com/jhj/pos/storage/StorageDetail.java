package com.jhj.pos.storage;


/**
 * 入库操作明细实体类
 *
 * @author lb
 */
public class StorageDetail {

    private String count_id;//入库唯一id
    private String title;//商品名称
    private String productsn;//商品条码
    private double quantity;//商品数量
    private String updatetime;//更新数量时间
    private int updatetype;//更新数量方式   修改/添加


    public String getCount_id() {
        return count_id;
    }

    public void setCount_id(String count_id) {
        this.count_id = count_id;
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
}
