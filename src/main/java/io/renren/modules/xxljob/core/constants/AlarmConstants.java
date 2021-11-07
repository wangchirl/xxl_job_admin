package io.renren.modules.xxljob.core.constants;

import java.util.regex.Pattern;

public interface AlarmConstants {

    String ACCESS_TOKEN = "access_token";
    String MOBILE = "mobile";
    String AUTHORIZATION = "Authorization";
    String APP_CODE = "APPCODE";
    String CONTENT = "content";

    Pattern EMAIL_PATTEN = Pattern.compile("[\\w.-]+@([\\w\\-]+\\.)+[\\w\\-]+");



}
