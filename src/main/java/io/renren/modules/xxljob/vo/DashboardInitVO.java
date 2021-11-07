package io.renren.modules.xxljob.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class DashboardInitVO implements Serializable {

    private int jobInfoCount;
    private int jobLogCount;
    private int jobLogSuccessCount;
    private int executorCount;

}
