package imagebed.controller;

import imagebed.utils.FileUploadUtil;
import imagebed.utils.IPUtils;
import imagebed.utils.RedisUploadHistoryUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/history")
public class HistoryController {

    @Autowired
    private RedisUploadHistoryUtil redisUploadHistoryUtil;

    @Resource
    private FileUploadUtil fileUploadUtil;

    @GetMapping("/current-ip")
    public Map<String, Object> getCurrentIpUploadHistory(HttpServletRequest request) {
        String ipAddress = IPUtils.getIpAddress(request);
        List<String> uploadHistory = redisUploadHistoryUtil.getUploadHistory(ipAddress);

        Map<String, Object> response = new HashMap<>();
        response.put("ip", ipAddress);
        response.put("uploadHistory", uploadHistory);

        return response;
    }


    @Transactional
    @PostMapping("/delete")
    public Map<String, String> deleteImage(@RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
        String imageUrl = request.get("imageUrl");
        Map<String, String> response = new HashMap<>();
        try {
            String ipAddress = IPUtils.getIpAddress(httpRequest);
            redisUploadHistoryUtil.removeUploadHistory(ipAddress, imageUrl);

            boolean isDeleted = fileUploadUtil.deleteFile(imageUrl,ipAddress);
            if (isDeleted) {
                response.put("state", "ok");
                response.put("msg", "图片删除成功");
            } else {
                response.put("state", "error");
                response.put("msg", "图片删除失败");
            }
        } catch (Exception e) {
            response.put("state", "error");
            response.put("msg", "图片删除失败");
            e.printStackTrace();
        }
        return response;
    }
}
