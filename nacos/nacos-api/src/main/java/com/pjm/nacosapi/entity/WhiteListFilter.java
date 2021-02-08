package com.pjm.nacosapi.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author pjm
 * @since 2020-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class WhiteListFilter implements Serializable{

    private static final long serialVersionUID = 1L;
    private String id;
    private String filterCode;
    private String filterName;
    private Double filterType;
    private String filterParentId;

}