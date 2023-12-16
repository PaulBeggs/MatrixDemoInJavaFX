package matrix.fileManaging;

import java.io.File;
import java.io.IOException;

public class FileUtil {

    public static void ensureAllFilesExist() {
        for (FilePath filePath : FilePath.values()) {
            ensureFileExists(filePath);
        }
    }

    private static void ensureFileExists(FilePath filePathEnum) {
        String filePath = filePathEnum.getPath();
        File file = new File(filePath);

        try {
            File parentDir = file.getParentFile();
            if (!parentDir.exists() && !parentDir.mkdirs()) {
                throw new IOException("Failed to create directory: " + parentDir);
            }
            if (!file.exists() && !file.createNewFile()) {
                throw new IOException("Failed to create file: " + file);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating file or directories for: " + filePath, e);
        }
    }
}



