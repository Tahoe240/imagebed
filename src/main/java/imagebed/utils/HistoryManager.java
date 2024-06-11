package imagebed.utils;

import java.io.*;
import java.util.Properties;

public class HistoryManager {

    private static final String HISTORY_DIR = "src/main/resources/history/";

    static {
        File dir = new File(HISTORY_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static void addRecord(String ipAddress, String originalFilename, String newFilename) {
        Properties properties = new Properties();
        File historyFile = new File(HISTORY_DIR + ipAddress + ".txt");

        try {
            if (historyFile.exists()) {
                FileInputStream in = new FileInputStream(historyFile);
                properties.load(in);
                in.close();
            }

            int count = properties.size();
            properties.put(String.valueOf(count), originalFilename + ":" + newFilename);

            FileOutputStream out = new FileOutputStream(historyFile);
            properties.store(out, null);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
