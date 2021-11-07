package io.renren.modules.xxljob.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class JobLogVO implements Serializable {

    private int triggerCode;
    private int handleCode;
    private String executorAddress;
    private long triggerTime;
    private long logId;
}
