package imagebed.controller;

import imagebed.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@RestController
@RequestMapping("/config")
public class ConfigController {

    @Autowired
    private ConfigService configService;

    @GetMapping("/get")
    public Properties getConfig() {
        return configService.getConfig();
    }

    @PostMapping("/update")
    public Map<String, String> updateConfig(@RequestBody Map<String, String> newConfig) {
        Map<String, String> response = new HashMap<>();
        try {
            Properties properties = new Properties();
            properties.putAll(newConfig);
            configService.updateConfig(properties);
            response.put("state", "ok");
            response.put("msg", "配置项更新成功");
        } catch (IOException e) {
            response.put("state", "error");
            response.put("msg", "配置项更新失败");
            e.printStackTrace();
        }
        return response;
    }
}