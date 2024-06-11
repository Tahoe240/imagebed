package imagebed.controller;

import imagebed.utils.FileUploadUtil;
import imagebed.utils.IPUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/upload")
public class UploadController {

    @Autowired
    private FileUploadUtil fileUploadUtil;

    @PostMapping("/image")
    public Map<String, String> handleFileUpload(@RequestParam("image") MultipartFile file,
                                                HttpServletRequest request) {
        String ipAddress = IPUtils.getIpAddress(request);
        return fileUploadUtil.saveFile(file, ipAddress);
    }
}
