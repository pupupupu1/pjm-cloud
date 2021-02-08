package com.pjm.quartzservice.entityExt;

import com.pjm.quartzservice.entity.ScheduleJob;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ScheduleJobExt extends ScheduleJob {
    private String ids;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
