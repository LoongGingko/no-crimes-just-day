package com.loogingko.ncjd.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ncjd")
public class NCJDProperties {

    /**
     * 是否启用Cookie Secure
     */
    private Boolean cookieSecure;
}
