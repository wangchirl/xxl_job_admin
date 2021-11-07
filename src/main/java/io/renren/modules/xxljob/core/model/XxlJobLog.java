package io.renren.modules.xxljob.core.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 执行日志实体类
 */
@Data
@Accessors(chain = true)
public class XxlJobLog {

    private long id;

    // job info
    private int jobGroup;
    private int jobId;

    // execute info
    private String executorAddress;
    private String executorHandler;
    private String executorParam;
    private String executorShardingParam;
    private int executorFailRetryCount;

    // trigger info
    private Date triggerTime;
    private int triggerCode;
    private String triggerMsg;

    // handle info
    private Date handleTime;
    private int handleCode;
    private String handleMsg;

    // alarm info
    private int alarmStatus;

}
