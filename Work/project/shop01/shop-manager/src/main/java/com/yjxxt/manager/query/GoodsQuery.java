package com.yjxxt.manager.query;

public class GoodsQuery {

    /**
     * 分类id
     */
    private Integer catId;

    /**
     * 品牌id
     */
    private Short brandId;

    /**
     * 是否上架
     */
    private Byte isOnSale;


    /**
     * 新品1  推荐2
     */
    private Byte flag;

    /**
     * 商品关键词
     */
    private String keywords;


    private Integer pageNum=1;

    private Integer pageSize=10;


    public Integer getCatId() {
        return catId;
    }

    public void setCatId(Integer catId) {
        this.catId = catId;
    }

    public Short getBrandId() {
        return brandId;
    }

    public void setBrandId(Short brandId) {
        this.brandId = brandId;
    }

    public Byte getIsOnSale() {
        return isOnSale;
    }

    public void setIsOnSale(Byte isOnSale) {
        this.isOnSale = isOnSale;
    }

    public Byte getFlag() {
        return flag;
    }

    public void setFlag(Byte flag) {
        this.flag = flag;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
