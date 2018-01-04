package com.cnyssj.db;

/**
 * 盘点管理实体类
 *
 * @author lb
 */
public class CheckManage {

    private String count_id;//盘点唯一id
    private int check_status;//盘点状态  0:未完成  1：已完成   -1:无数据
    private String count_time;//盘点时间
    private String remark;//备注


    public String getCount_id() {
        return count_id;
    }

    public void setCount_id(String count_id) {
        this.count_id = count_id;
    }

    public int getCheck_status() {
        return check_status;
    }

    public void setCheck_status(int check_status) {
        this.check_status = check_status;
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
