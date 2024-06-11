package imagebed.utils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class UploadLimitChecker {

    private final RedisTemplate<String, String> redisTemplate;

    private final int dailyMaxUpload;
    private final int totalMaxUpload;

    @Autowired
    public UploadLimitChecker(RedisTemplate<String, String> redisTemplate,
                              @Value("${dailyMaxUpload}") int dailyMaxUpload,
                              @Value("${totalMaxUpload}") int totalMaxUpload) {
        this.redisTemplate = redisTemplate;
        this.dailyMaxUpload = dailyMaxUpload;
        this.totalMaxUpload = totalMaxUpload;
    }

    public boolean isDailyLimitExceeded(String ip) {
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String key = "uploadCount:" + ip + ":" + date;
        if (!redisTemplate.hasKey(key)) {
            redisTemplate.opsForValue().set(key, "0");
            // 设置键在当天结束后过期
            long timeUntilEndOfDay = calculateTimeUntilEndOfDay();
            redisTemplate.expire(key, timeUntilEndOfDay, TimeUnit.MILLISECONDS);
        }
        String countStr = redisTemplate.opsForValue().get(key);
        int count = countStr == null ? 0 : Integer.parseInt(countStr);
        return count >= dailyMaxUpload;
    }

    public boolean isTotalLimitExceeded(String ip) {
        String key = "uploadCount:" + ip + ":total";
        if (!redisTemplate.hasKey(key)) {
            redisTemplate.opsForValue().set(key, "0");
        }
        String countStr = redisTemplate.opsForValue().get(key);
        int count = countStr == null ? 0 : Integer.parseInt(countStr);
        return count >= totalMaxUpload;
    }

    public void incrementUploadCount(String ip) {
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String dailyKey = "uploadCount:" + ip + ":" + date;
        String totalKey = "uploadCount:" + ip + ":total";

        redisTemplate.opsForValue().increment(dailyKey);
        redisTemplate.opsForValue().increment(totalKey);
    }

    // 降低totalKey的值
    public void decrementTotalKey(String ip) {
        String totalKey = "uploadCount:" + ip + ":total";
        redisTemplate.opsForValue().decrement(totalKey);
    }


    private long calculateTimeUntilEndOfDay() {
        Date now = new Date();
        Date endOfDay = new Date(now.getYear(), now.getMonth(), now.getDate(), 23, 59, 59);
        return endOfDay.getTime() - now.getTime();
    }
}