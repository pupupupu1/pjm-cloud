package com.pjm.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor                 //无参构造
@AllArgsConstructor                //有参构造
public class PageVo<T> {
    private int pageNum;
    private int pageSize;
    private long total;
    private T list;
}
