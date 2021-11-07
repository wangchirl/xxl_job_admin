package io.renren.modules.xxljob.vo;

import com.xxl.job.core.glue.GlueTypeEnum;
import io.renren.modules.xxljob.core.model.XxlJobInfo;
import io.renren.modules.xxljob.core.model.XxlJobLogGlue;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@Accessors(chain = true)
public class JobCodeInitVO implements Serializable {

    private GlueTypeEnum[] glueTypeEnums;

    private XxlJobInfo jobInfo;

    private List<XxlJobLogGlue> jobLogGlues;

}
