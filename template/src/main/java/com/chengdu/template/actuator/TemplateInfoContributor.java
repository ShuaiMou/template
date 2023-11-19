package com.chengdu.template.actuator;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 自定义 info 信息
 */

@Component
public class TemplateInfoContributor implements InfoContributor {
    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("msg", "hello")
                .withDetail("time", new Date())
                .withDetail("address", "成都");
    }
}
