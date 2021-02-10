package com.pjm.rabbitmqservice.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author pjm
 * @since 2021-02-10
 */
@ApiModel(value = "")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MqLocalMessage extends Model<MqLocalMessage> {

    private static final long serialVersionUID = 1L;

    @TableId
    private String id;
    private String topicName;
    private String queueName;
    @ApiModelProperty(value = "传输内容")
    private String context;
    @ApiModelProperty(value = "执行状态（0，未发送，1已发送，2消费失败，3消费成功）")
    private String msgStatus;
    @ApiModelProperty(value = "发送次数")
    private Integer sendTimes;
    @ApiModelProperty(value = "修改时间")
    private Long updateTime;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}