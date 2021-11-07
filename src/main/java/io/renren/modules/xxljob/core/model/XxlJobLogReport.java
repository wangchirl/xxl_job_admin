package io.renren.modules.xxljob.core.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 统计报表实体类
 */
@Data
@Accessors(chain = true)
public class XxlJobLogReport {

    private int id;

    private Date triggerDay;

    private int runningCount;
    private int sucCount;
    private int failCount;

}
