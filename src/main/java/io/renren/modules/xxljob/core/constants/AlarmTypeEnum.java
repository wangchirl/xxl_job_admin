package io.renren.modules.xxljob.core.constants;

import io.renren.modules.xxljob.core.alarm.impl.DingJobAlarm;
import io.renren.modules.xxljob.core.alarm.impl.EmailJobAlarm;
import io.renren.modules.xxljob.core.alarm.impl.SmsJobAlarm;
import io.renren.modules.xxljob.core.alarm.JobAlarm;

public enum AlarmTypeEnum {

    EMAIL("email", "邮件", EmailJobAlarm.class, "alarm.mail.enable"),
    DING_TALK("dingtalk", "钉钉", DingJobAlarm.class, "alarm.dingtalk.enable"),
    SMS("sms", "短信", SmsJobAlarm.class, "alarm.sms.enable"),
    WX("wx", "微信号", null, ""),
    WX_APP("wx_app", "微信公众号", null, ""),
    WX_APPLET("wx_applet", "微信小程序", null, "");


    /**
     * 告警方式 code
     */
    private String code;
    /**
     * 告警方式名称
     */
    private String name;

    /**
     * 告警方式实现类
     */
    private Class<? extends JobAlarm> implClass;

    /**
     * 开启标准 -> 配置文件配置属性名称
     */
    private String enableName;

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public Class<? extends JobAlarm> getImplClass() {
        return implClass;
    }

    public String getEnableName() {
        return enableName;
    }

    AlarmTypeEnum(String code, String name, Class<? extends JobAlarm> implClass, String enableName) {
        this.code = code;
        this.name = name;
        this.implClass = implClass;
        this.enableName = enableName;
    }
}
