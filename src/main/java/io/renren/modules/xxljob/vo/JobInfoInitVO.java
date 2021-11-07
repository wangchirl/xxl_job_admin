package io.renren.modules.xxljob.vo;

import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import com.xxl.job.core.glue.GlueTypeEnum;
import io.renren.modules.xxljob.core.model.XxlJobGroup;
import io.renren.modules.xxljob.core.route.ExecutorRouteStrategyEnum;
import io.renren.modules.xxljob.core.scheduler.MisfireStrategyEnum;
import io.renren.modules.xxljob.core.scheduler.ScheduleTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@Accessors(chain = true)
public class JobInfoInitVO implements Serializable {

    private ExecutorRouteStrategyEnum[] executorRouteStrategyEnums;
    private GlueTypeEnum[] glueTypeEnums;
    private ExecutorBlockStrategyEnum[] executorBlockStrategyEnums;
    private ScheduleTypeEnum[] scheduleTypeEnums;
    private MisfireStrategyEnum[] misfireStrategyEnums;
    private List<XxlJobGroup> jobGroupList;
    private int jobGroup;

}
