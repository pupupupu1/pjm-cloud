package com.pjm.quartzservice.entity;

import com.baomidou.mybatisplus.enums.IdType;

import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.activerecord.Model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author pjm
 * @since 2020-05-25
 */
@ApiModel(value = "")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ScheduleJob extends Model<ScheduleJob> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.INPUT)
    private String id;
    @ApiModelProperty(value = "job名称")
    private String jobName;
    @ApiModelProperty(value = "class路径")
    private String classPath;
    @ApiModelProperty(value = "方法名")
    private String methodName;
    @ApiModelProperty(value = "方法参数")
    private String params;
    @ApiModelProperty(value = "cron表达式")
    private String cronExpression;
    @ApiModelProperty(value = "耗时")
    private String timeConsuming;
    @ApiModelProperty(value = "任务状态")
    private Integer status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "上次执行时间")
    private Date lastExecutionTime;
    @ApiModelProperty(value = "备注")
    private String remark;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    @ApiModelProperty(value = "创建人")
    private String creater;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}