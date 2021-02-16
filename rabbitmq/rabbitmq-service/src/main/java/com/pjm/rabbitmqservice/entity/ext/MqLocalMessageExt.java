package com.pjm.rabbitmqservice.entity.ext;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.pjm.rabbitmqservice.entity.MqLocalMessage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author pjm
 * @since 2021-02-10
 */
@ApiModel(value = "")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MqLocalMessageExt extends MqLocalMessage {
    private Integer pageNum=1;
    private Integer pageSize=10;
    private List<String> ids;
}