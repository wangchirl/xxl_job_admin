package io.renren.modules.xxljob.core.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * glue 编辑代码实体类
 */
@Data
@Accessors(chain = true)
public class XxlJobLogGlue {
	
	private int id;
	private int jobId;				// 任务主键ID
	private String glueType;		// GLUE类型	#com.xxl.job.core.glue.GlueTypeEnum
	private String glueSource;
	private String glueRemark;
	private Date addTime;
	private Date updateTime;

}
