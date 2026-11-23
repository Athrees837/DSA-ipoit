
package by.it.group510972.savitskiy.lesson15;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class SourceScannerA {

    static class FileInfo {
        long size;
        String path;

        FileInfo(long size, String path) {
            this.size = size;
            this.path = path;
        }
    }

    // Чтение файла с игнорированием некорректных символов
    static String readFile(Path file) throws IOException {
        byte[] bytes = Files.readAllBytes(file);

        try {
            return StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.IGNORE)
                    .onUnmappableCharacter(CodingErrorAction.IGNORE)
                    .decode(ByteBuffer.wrap(bytes))
                    .toString();
        } catch (CharacterCodingException e) {
            return "";
        }
    }

    // Проверка, является ли строка package или import
    static boolean isPackageOrImport(String text, int start, int end) {
        int pos = start;

        while (pos < end && text.charAt(pos) < 33) {
            pos++;
        }

        if (pos >= end) {
            return false;
        }

        if (text.startsWith("package", pos)) {
            int next = pos + 7;
            return next < end &&
                    (text.charAt(next) < 33 || text.charAt(next) == ';');
        }

        if (text.startsWith("import", pos)) {
            int next = pos + 6;
            return next < end &&
                    (text.charAt(next) < 33 || text.charAt(next) == ';');
        }

        return false;
    }

    // Удаление строк package и import
    static String removePackageAndImports(String text) {
        StringBuilder result = new StringBuilder();

        int start = 0;
        int length = text.length();

        while (start < length) {
            int end = start;

            while (end < length &&
                    text.charAt(end) != '\n' &&
                    text.charAt(end) != '\r') {
                end++;
            }

            if (!isPackageOrImport(text, start, end)) {
                result.append(text, start, end);
            }

            // Сохраняем перевод строки, если строка не удаляется
            if (!isPackageOrImport(text, start, end)) {
                if (end < length) {
                    if (text.charAt(end) == '\r' &&
                            end + 1 < length &&
                            text.charAt(end + 1) == '\n') {
                        result.append("\r\n");
                        end++;
                    } else {
                        result.append(text.charAt(end));
                    }
                }
            } else {
                if (end < length &&
                        text.charAt(end) == '\r' &&
                        end + 1 < length &&
                        text.charAt(end + 1) == '\n') {
                    end++;
                }
            }

            start = end + 1;
        }

        return result.toString();
    }

    // Удаление символов с кодом меньше 33 в начале и конце
    static String trimText(String text) {
        int left = 0;
        int right = text.length();

        while (left < right && text.charAt(left) < 33) {
            left++;
        }

        while (right > left && text.charAt(right - 1) < 33) {
            right--;
        }

        return text.substring(left, right);
    }

    public static void main(String[] args) {
        String src = System.getProperty("user.dir")
                + File.separator + "src" + File.separator;

        Path srcPath = Paths.get(src);

        List<FileInfo> files = new ArrayList<>();

        try (Stream<Path> stream = Files.walk(srcPath)) {
            stream.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .forEach(path -> {
                        try {
                            String text = readFile(path);

                            // Исключаем тестовые файлы
                            if (text.contains("@Test") ||
                                    text.contains("org.junit.Test")) {
                                return;
                            }

                            // Удаляем package и import
                            text = removePackageAndImports(text);

                            // Очищаем начало и конец текста
                            text = trimText(text);

                            // Рассчитываем размер в байтах UTF-8
                            long size = text.getBytes(
                                    StandardCharsets.UTF_8
                            ).length;

                            // Получаем относительный путь
                            String relativePath = srcPath.relativize(path).toString();

                            files.add(new FileInfo(size, relativePath));

                        } catch (IOException e) {
                            // Пропускаем файл, который не удалось прочитать
                        }
                    });

        } catch (IOException e) {
            return;
        }

        // Сортировка по размеру, затем по пути
        files.sort(Comparator
                .comparingLong((FileInfo f) -> f.size)
                .thenComparing(f -> f.path));

        // Вывод результатов
        for (FileInfo file : files) {
            System.out.println(file.size + " " + file.path);
        }
    }
}
