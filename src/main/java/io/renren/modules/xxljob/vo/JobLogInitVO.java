package io.renren.modules.xxljob.vo;

import io.renren.modules.xxljob.core.model.XxlJobGroup;
import io.renren.modules.xxljob.core.model.XxlJobInfo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@Accessors(chain = true)
public class JobLogInitVO implements Serializable {

    private List<XxlJobGroup> jobGroupList;

    private XxlJobInfo jobInfo;

}
