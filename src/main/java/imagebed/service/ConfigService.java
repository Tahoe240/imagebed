//package imagebed.service;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.core.env.Environment;
//import org.springframework.core.env.ConfigurableEnvironment;
//import org.springframework.core.env.PropertiesPropertySource;
//
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.Properties;
//
//@Service
//public class ConfigService {
//
//    @Value("${upload.dir}")
//    private String uploadDir;
//
//    @Value("${dailyMaxUpload}")
//    private int dailyMaxUpload;
//
//    @Value("${totalMaxUpload}")
//    private int totalMaxUpload;
//
//    @Value("${image.prefix}")
//    private String imagePrefix;
//
//    private final Environment env;
//
//    public ConfigService(Environment env) {
//        this.env = env;
//    }
//
//    public Properties getConfig() {
//        Properties properties = new Properties();
//        properties.setProperty("upload.dir", uploadDir);
//        properties.setProperty("dailyMaxUpload", String.valueOf(dailyMaxUpload));
//        properties.setProperty("totalMaxUpload", String.valueOf(totalMaxUpload));
//        properties.setProperty("image.prefix", imagePrefix);
//        return properties;
//    }
//
//    public void updateConfig(Properties newConfig) throws IOException {
//        // 更新内存中的配置项
//        if (env instanceof ConfigurableEnvironment) {
//            ConfigurableEnvironment configEnv = (ConfigurableEnvironment) env;
//            PropertiesPropertySource propertySource = (PropertiesPropertySource) configEnv.getPropertySources().get("applicationConfig: [classpath:/application.properties]");
//            if (propertySource != null) {
//                for (String key : newConfig.stringPropertyNames()) {
//                    propertySource.getSource().put(key, newConfig.getProperty(key));
//                }
//            }
//        }
//
//        // 更新配置文件中的配置项
//        String filePath = getClass().getClassLoader().getResource("application.properties").getPath();
//        System.out.println("filepath:"+filePath);
//        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
//            Properties properties = new Properties();
//            properties.load(input);
//            properties.putAll(newConfig);
//
//            try (FileOutputStream output = new FileOutputStream(filePath)) {
//                properties.store(output, null);
//            }
//        }
//    }
//}


package imagebed.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.core.env.Environment;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.nio.file.Paths;

@Service
public class ConfigService {

    @Value("${upload.dir}")
    private String uploadDir;

    @Value("${dailyMaxUpload}")
    private int dailyMaxUpload;

    @Value("${totalMaxUpload}")
    private int totalMaxUpload;

    @Value("${image.prefix}")
    private String imagePrefix;

    private final Environment env;

    public ConfigService(Environment env) {
        this.env = env;
    }

    public Properties getConfig() {
        Properties properties = new Properties();
        properties.setProperty("upload.dir", uploadDir);
        properties.setProperty("dailyMaxUpload", String.valueOf(dailyMaxUpload));
        properties.setProperty("totalMaxUpload", String.valueOf(totalMaxUpload));
        properties.setProperty("image.prefix", imagePrefix);
        return properties;
    }

    public void updateConfig(Properties newConfig) throws IOException {
        // 更新内存中的配置项
        if (env instanceof ConfigurableEnvironment) {
            ConfigurableEnvironment configEnv = (ConfigurableEnvironment) env;
            PropertiesPropertySource propertySource = (PropertiesPropertySource) configEnv.getPropertySources().get("applicationConfig: [classpath:/application.properties]");
            if (propertySource != null) {
                for (String key : newConfig.stringPropertyNames()) {
                    propertySource.getSource().put(key, newConfig.getProperty(key));
                }
            }
        }

        // 获取源文件路径
        String filePath = Paths.get("src/main/resources/application.properties").toAbsolutePath().toString();
        System.out.println("filepath:" + filePath);

        // 更新配置文件中的配置项
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            Properties properties = new Properties();
            properties.load(input);
            properties.putAll(newConfig);

            try (FileOutputStream output = new FileOutputStream(filePath)) {
                properties.store(output, null);
            }
        }
    }
}
