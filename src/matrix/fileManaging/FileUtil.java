package matrix.fileManaging;

import matrix.util.ErrorsAndSyntax;

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
                ErrorsAndSyntax.showErrorPopup("Failed to create directory: " + parentDir);
                throw new IOException("Failed to create directory: " + parentDir);
            }
            if (!file.exists() && !file.createNewFile()) {
                ErrorsAndSyntax.showErrorPopup("Failed to create file: " + file);
                throw new IOException("Failed to create file: " + file);
            }
        } catch (IOException e) {
            ErrorsAndSyntax.showErrorPopup("You done messed up.");
            throw new IllegalArgumentException(e);
        }
    }
}



