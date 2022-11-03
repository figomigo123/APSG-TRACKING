package org.traccar.api.resource.new_models;

import java.util.Collection;

public class Pageable {
    int pageNo;
    long Pages;
    long size;
    Collection<?> data;

    public Pageable(int pageNo, long pages,  Collection<?> data) {
        this.pageNo = pageNo;
        Pages = pages;
        this.data = data;
        this.size = data.size();

    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public long getPages() {
        return Pages;
    }

    public void setPages(long pages) {
        Pages = pages;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Collection<?> getData() {
        return data;
    }

    public void setData(Collection<?> data) {
        this.data = data;
    }
}
