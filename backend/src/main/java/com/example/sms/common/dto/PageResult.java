package com.example.sms.common.dto;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {
    private List<T> list;
    private long total;
    private int page;
    private int pageSize;
    private int totalPages;

    public PageResult() {}

    public PageResult(List<T> list, long total, int page, int pageSize) {
        this.list = list;
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
        this.totalPages = pageSize > 0 ? (int) Math.ceil((double) total / pageSize) : 0;
    }

    public static <T> PageResult<T> of(List<T> list, long total, int page, int pageSize) {
        return new PageResult<>(list, total, page, pageSize);
    }
}
