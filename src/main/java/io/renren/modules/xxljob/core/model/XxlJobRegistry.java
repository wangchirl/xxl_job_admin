package io.renren.modules.xxljob.core.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 执行器注册信息
 */
@Data
@Accessors(chain = true)
public class XxlJobRegistry {

    private int id;
    private String registryGroup;
    private String registryKey;
    private String registryValue;
    private Date updateTime;

}
