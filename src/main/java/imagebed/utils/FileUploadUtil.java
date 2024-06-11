package imagebed.utils;

//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.util.ClassUtils;
//import org.springframework.beans.factory.annotation.Value;
//
//import java.io.File;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.*;
//import java.util.logging.FileHandler;
//import java.util.logging.Logger;
//import java.util.logging.SimpleFormatter;
//
//import org.springframework.web.multipart.MultipartFile;
//
//@Component
//public class FileUploadUtil {
//
//    private static final Logger logger = Logger.getLogger(FileUploadUtil.class.getName());
//
//    // 本地用这个上传路径
//    private static final String UPLOAD_DIR = ClassUtils.getDefaultClassLoader().getResource("").getPath() + "static/uploadImages/";
//
////    @Value("${upload.dir}")
////    private String UPLOAD_DIR;
//
//    @Value("${image.prefix}")
//    private String imagePrefix;
//
//    @Autowired
//    private RedisUploadHistoryUtil redisUploadHistoryUtil;
//
//    static {
//        try {
//            FileHandler fh = new FileHandler("upload.log", true);
//            fh.setFormatter(new SimpleFormatter());
//            logger.addHandler(fh);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public Map<String, String> saveFile(MultipartFile multipartFile, String address) {
//        Map<String, String> map = new HashMap<>();
//
//        String filename = multipartFile.getOriginalFilename();
//        try {
//            String suffixName = getSuffix(filename);
//            if (suffixName != null && isPic(suffixName)) {
//                String time = new SimpleDateFormat("yyyy/MM").format(new Date());
//                String name = UUID.randomUUID() + suffixName;
//
//                File dir = new File(UPLOAD_DIR, time);
//                if (!dir.exists()) {
//                    dir.mkdirs();
//                }
//                File destinationFile = new File(dir, name);
//                multipartFile.transferTo(destinationFile);
//
//                String logMessage = String.format("%s---%s---上传了图片---%s---%s",
//                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
//                        address, filename, name);
//                logger.info(logMessage);
//
//                String imageUrl = imagePrefix + "/uploadImages/" + time + "/" + name;
//                redisUploadHistoryUtil.addUploadHistory(address, imageUrl);
//
//                map.put("state", "ok");
//                map.put("msg", imageUrl);
//            } else {
//                logger.info("[Upload] 用户上传非图片文件");
//                map.put("state", "!pic");
//                map.put("msg", "不是图片文件");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            map.put("state", "error");
//            map.put("msg", "出错，请重试");
//        }
//
//        return map;
//    }
//
//    public boolean deleteFile(String imageUrl) {
//        try {
//            // 根据 imageUrl 解析文件路径
//            String relativePath = imageUrl.replace(imagePrefix + "/uploadImages/", "");
//            System.out.println("relativePath: " + relativePath);
//            String filePath = UPLOAD_DIR + "/" + relativePath;
//
//            // 打印调试信息
//            System.out.println("Trying to delete file at: " + filePath);
//
//            File file = new File(filePath);
//            if (file.exists()) {
//                return file.delete();
//            } else {
//                System.out.println("File does not exist: " + filePath);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//
//
//
//    private String getSuffix(String filename) {
//        if (filename == null || !filename.contains(".")) {
//            return null;
//        }
//        return filename.substring(filename.lastIndexOf("."));
//    }
//
//    private boolean isPic(String suffix) {
//        String[] picSuffixes = {".jpg", ".jpeg", ".png", ".gif", ".bmp"};
//        for (String picSuffix : picSuffixes) {
//            if (picSuffix.equalsIgnoreCase(suffix)) {
//                return true;
//            }
//        }
//        return false;
//    }
//}
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

@Component
public class FileUploadUtil {

    private static final Logger logger = Logger.getLogger(FileUploadUtil.class.getName());

    // 本地用这个上传路径
    private static final String UPLOAD_DIR = ClassUtils.getDefaultClassLoader().getResource("").getPath() + "static/uploadImages/";

    @Value("${image.prefix}")
    private String imagePrefix;

    @Autowired
    private RedisUploadHistoryUtil redisUploadHistoryUtil;

    @Autowired
    private UploadLimitChecker uploadLimitChecker;

    static {
        try {
            FileHandler fh = new FileHandler("upload.log", true);
            fh.setFormatter(new SimpleFormatter());
            logger.addHandler(fh);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, String> saveFile(MultipartFile multipartFile, String address) {
        Map<String, String> map = new HashMap<>();

        // 检查每日上传限制
        if (uploadLimitChecker.isDailyLimitExceeded(address)) {
            map.put("state", "error");
            map.put("msg", "已超过每日上传限制");
            return map;
        }

        // 检查总上传限制
        if (uploadLimitChecker.isTotalLimitExceeded(address)) {
            map.put("state", "error");
            map.put("msg", "已超过总上传限制");
            return map;
        }

        String filename = multipartFile.getOriginalFilename();
        try {
            String suffixName = getSuffix(filename);
            if (suffixName != null && isPic(suffixName)) {
                String time = new SimpleDateFormat("yyyy/MM").format(new Date());
                String name = UUID.randomUUID() + suffixName;

                File dir = new File(UPLOAD_DIR, time);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File destinationFile = new File(dir, name);
                multipartFile.transferTo(destinationFile);

                String logMessage = String.format("%s---%s---上传了图片---%s---%s",
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
                        address, filename, name);
                logger.info(logMessage);

                String imageUrl = imagePrefix + "/uploadImages/" + time + "/" + name;
                redisUploadHistoryUtil.addUploadHistory(address, imageUrl);

                // 增加上传计数
                uploadLimitChecker.incrementUploadCount(address);

                map.put("state", "ok");
                map.put("msg", imageUrl);
            } else {
                logger.info("[Upload] 用户上传非图片文件");
                map.put("state", "!pic");
                map.put("msg", "不是图片文件");
            }
        } catch (IOException e) {
            e.printStackTrace();
            map.put("state", "error");
            map.put("msg", "出错，请重试");
        }

        return map;
    }

    public boolean deleteFile(String imageUrl, String address) {
        try {
            // 根据 imageUrl 解析文件路径
            String relativePath = imageUrl.replace(imagePrefix + "/uploadImages/", "");
            System.out.println("relativePath: " + relativePath);
            String filePath = UPLOAD_DIR + "/" + relativePath;

            // 打印调试信息
            System.out.println("Trying to delete file at: " + filePath);

            File file = new File(filePath);
            if (file.exists()) {
                boolean deleted = file.delete();
                if (deleted) {
                    // 减少上传计数
                    uploadLimitChecker.decrementTotalKey(address);
                    return true;
                }
            } else {
                System.out.println("File does not exist: " + filePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private String getSuffix(String filename) {
        if (filename == null || !filename.contains(".")) {
            return null;
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    private boolean isPic(String suffix) {
        String[] picSuffixes = {".jpg", ".jpeg", ".png", ".gif", ".bmp"};
        for (String picSuffix : picSuffixes) {
            if (picSuffix.equalsIgnoreCase(suffix)) {
                return true;
            }
        }
        return false;
    }
}
