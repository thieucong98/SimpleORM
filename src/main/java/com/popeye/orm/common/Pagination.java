package com.popeye.orm.common;

public class Pagination {
    private static final Integer DEFAULT_PAGE = 1;
    private static final Integer DEFAULT_SIZE = 5;

    private Integer page;
    private Integer size;

    private Pagination(Integer page, Integer size) {
        this.setPage(page);
        this.setSize(size);
    }

    public static Pagination of(Integer page, Integer size) {
        return new Pagination(page, size);
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        if (page == null || page < DEFAULT_PAGE) {
            this.page = DEFAULT_PAGE;
        } else {
            this.page = page;
        }
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {

        if (size == null || size < DEFAULT_SIZE) {
            this.size = DEFAULT_SIZE;
        } else {
            this.size = size;
        }
    }

    public Integer getOffset() {
        return (this.page - 1) * this.size;
    }
}
