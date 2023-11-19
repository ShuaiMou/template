package com.chengdu.template.actuator;

import org.springframework.boot.actuate.endpoint.annotation.DeleteOperation;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义
 */

@Component
@Endpoint(id = "sessions") // 相当于 RequestMapping --- http://localhost:8081/template/actuator/sessions
public class TemplateEndpoint {
    Map<String, Object>  map = new HashMap<>();

    @ReadOperation
    public Map<String, Object> read() {
        map.put("username", "saul");
        map.put("age", 98);
        return map;
    }

    @WriteOperation
    public void write(String key, String value) {
        map.put(key, value);
    }

    @DeleteOperation
    public void write(String key) {
        map.remove(key);
    }
}
