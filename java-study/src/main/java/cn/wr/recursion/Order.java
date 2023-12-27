package cn.wr.recursion;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author : WangRui
 * @date : 2023/11/28
 */

public class Order {

    private Integer id;
    private BigDecimal price;
    private String itemName;
    private Date createTime;
    private Date updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public List<Order> getSubList() {
        return subList;
    }

    public void setSubList(List<Order> subList) {
        this.subList = subList;
    }

    private List<Order> subList;
}

