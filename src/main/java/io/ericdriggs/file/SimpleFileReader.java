package io.ericdriggs.file;

import lombok.SneakyThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SimpleFileReader {

    /**
     * @param relativePath a path relative to project root, e.g. src/test/resources/coverage/user-swagger.json
     * @return the contents of the file
     * @throws RuntimeException if unable to load file
     */
    @SneakyThrows(IOException.class)
    public static String fromRelativePath(String relativePath) {
        Path path = Path.of(Path.of("").toString(), relativePath).toAbsolutePath();

            return Files.readString(path.toAbsolutePath());

    }

    public static void saveRelativePath(String relativePath, String fileName, String fileContents ) {
        Path dirPath = Path.of(Path.of("").toString(), relativePath).toAbsolutePath();
        Path filePath = Path.of(dirPath.toString(), fileName);
        try {
            Files.createDirectories(dirPath);
            Files.write(filePath, fileContents.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
