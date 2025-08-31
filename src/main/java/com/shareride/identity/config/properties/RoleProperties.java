package com.shareride.identity.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component("roleProps")
@ConfigurationProperties(prefix = "roles")
public class RoleProperties {

    private String user;
    private String admin;
}
