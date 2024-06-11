package imagebed.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RedisUploadHistoryUtil {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void addUploadHistory(String ip, String imageUrl) {
        String key = "history:" + ip;
        redisTemplate.opsForList().rightPush(key, imageUrl);
    }

    public List<String> getUploadHistory(String ip) {
        String key = "history:" + ip;
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    public void removeUploadHistory(String ip, String imageUrl) {
        String key = "history:" + ip;
        redisTemplate.opsForList().remove(key, 1, imageUrl);
    }
}
