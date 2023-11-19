package com.chengdu.template.actuator;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

/**
 * 定制 health 端点
 */
@Component
public class TemplateHealthIndicator extends AbstractHealthIndicator {

    // 自定义检查服务状态，会在 actuator 的 health 模块中显示
    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        boolean check = doCheck();
        if(check) {
            builder.up();
        } else {
            builder.down();
        }
        builder.withDetail("服务名","template 服务").withDetail("负责人","saul");
    }

    private boolean doCheck() {
        // 基于我们特定的服务逻辑来判断是否健康
        // 数据库 通过 jdbc 获取连接
        // redis， mongo 等使用对应 api 来测试连接
        return true;
    }
}
