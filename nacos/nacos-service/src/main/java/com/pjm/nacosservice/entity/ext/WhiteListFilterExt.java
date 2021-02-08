package com.pjm.nacosservice.entity.ext;

import com.pjm.nacosservice.entity.WhiteListFilter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class WhiteListFilterExt extends WhiteListFilter {
    private List<String> ids;
    private Integer pageNum;
    private Integer pageSize;
}
