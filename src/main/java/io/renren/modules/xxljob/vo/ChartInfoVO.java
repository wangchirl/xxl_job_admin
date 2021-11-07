package io.renren.modules.xxljob.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@Accessors(chain = true)
public class ChartInfoVO implements Serializable {

    private List<String> triggerDayList;
    private List<Integer> triggerDayCountRunningList;
    private List<Integer> triggerDayCountSucList;
    private List<Integer> triggerDayCountFailList;
    private int triggerCountRunningTotal;
    private int triggerCountSucTotal;
    private int triggerCountFailTotal;

}
