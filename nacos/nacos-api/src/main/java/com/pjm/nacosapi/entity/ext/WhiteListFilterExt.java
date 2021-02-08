package com.pjm.nacosapi.entity.ext;

import com.pjm.nacosapi.entity.WhiteListFilter;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class WhiteListFilterExt extends WhiteListFilter {
    private List<String> ids;
    private Integer pageNum;
    private Integer pageSize;
}
