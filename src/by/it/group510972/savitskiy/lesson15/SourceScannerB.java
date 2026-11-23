
package by.it.group510972.savitskiy.lesson15;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class SourceScannerB {

    static class FileInfo {
        long size;
        String path;

        FileInfo(long size, String path) {
            this.size = size;
            this.path = path;
        }
    }

    // Чтение файла с обработкой некорректных UTF-8-последовательностей
    static String readFile(Path path) throws IOException {
        byte[] bytes = Files.readAllBytes(path);

        return StandardCharsets.UTF_8.newDecoder()
                .onMalformedInput(CodingErrorAction.REPLACE)
                .onUnmappableCharacter(CodingErrorAction.REPLACE)
                .decode(ByteBuffer.wrap(bytes))
                .toString();
    }

    // Удаление комментариев за O(n)
    static String removeComments(String text) {
        StringBuilder result = new StringBuilder();

        // 0 - обычный код
        // 1 - строковый литерал
        // 2 - символьный литерал
        // 3 - однострочный комментарий
        // 4 - многострочный комментарий
        // 5 - текстовый блок
        int state = 0;

        int i = 0;
        int length = text.length();

        while (i < length) {
            char c = text.charAt(i);

            if (state == 0) {
                if (i + 2 < length &&
                        text.startsWith("\"\"\"", i)) {
                    result.append("\"\"\"");
                    state = 5;
                    i += 3;
                } else if (c == '"') {
                    result.append(c);
                    state = 1;
                    i++;
                } else if (c == '\'') {
                    result.append(c);
                    state = 2;
                    i++;
                } else if (i + 1 < length &&
                        c == '/' && text.charAt(i + 1) == '/') {
                    result.append(' ');
                    state = 3;
                    i += 2;
                } else if (i + 1 < length &&
                        c == '/' && text.charAt(i + 1) == '*') {
                    result.append(' ');
                    state = 4;
                    i += 2;
                } else {
                    result.append(c);
                    i++;
                }
            }

            else if (state == 1 || state == 2) {
                result.append(c);

                if (c == '\\' && i + 1 < length) {
                    result.append(text.charAt(i + 1));
                    i += 2;
                } else {
                    if ((state == 1 && c == '"') ||
                            (state == 2 && c == '\'')) {
                        state = 0;
                    }
                    i++;
                }
            }

            else if (state == 3) {
                // Однострочный комментарий заканчивается переводом строки
                if (c == '\n' || c == '\r') {
                    result.append(c);
                    state = 0;
                }
                i++;
            }

            else if (state == 4) {
                // В многострочном комментарии сохраняем переводы строк
                if (c == '\n' || c == '\r') {
                    result.append(c);
                    i++;
                } else if (i + 1 < length &&
                        c == '*' && text.charAt(i + 1) == '/') {
                    state = 0;
                    i += 2;
                } else {
                    i++;
                }
            }

            else if (state == 5) {
                // Текстовый блок Java
                if (c == '\\' && i + 1 < length) {
                    result.append(c);
                    result.append(text.charAt(i + 1));
                    i += 2;
                } else if (i + 2 < length &&
                        text.startsWith("\"\"\"", i)) {
                    result.append("\"\"\"");
                    state = 0;
                    i += 3;
                } else {
                    result.append(c);
                    i++;
                }
            }
        }

        return result.toString();
    }

    // Проверка начала строки на package или import
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
            return next < end && text.charAt(next) < 33;
        }

        if (text.startsWith("import", pos)) {
            int next = pos + 6;
            return next < end && text.charAt(next) < 33;
        }

        return false;
    }

    // Удаление строк package и import за O(n)
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

            boolean remove = isPackageOrImport(text, start, end);

            if (!remove) {
                result.append(text, start, end);
            }

            // Обработка окончания строки
            if (end < length) {
                if (text.charAt(end) == '\r' &&
                        end + 1 < length &&
                        text.charAt(end + 1) == '\n') {

                    if (!remove) {
                        result.append("\r\n");
                    }

                    end += 2;
                } else {
                    if (!remove) {
                        result.append(text.charAt(end));
                    }

                    end++;
                }
            }

            start = end;
        }

        return result.toString();
    }

    // Удаление пустых строк
    static String removeEmptyLines(String text) {
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

            // Проверяем, есть ли в строке символ с кодом >= 33
            boolean notEmpty = false;

            for (int i = start; i < end; i++) {
                if (text.charAt(i) >= 33) {
                    notEmpty = true;
                    break;
                }
            }

            if (notEmpty) {
                result.append(text, start, end);
            }

            // Сохраняем исходный перевод строки
            if (end < length) {
                if (text.charAt(end) == '\r' &&
                        end + 1 < length &&
                        text.charAt(end + 1) == '\n') {

                    if (notEmpty) {
                        result.append("\r\n");
                    }

                    end += 2;
                } else {
                    if (notEmpty) {
                        result.append(text.charAt(end));
                    }

                    end++;
                }
            }

            start = end;
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

                            // Пропускаем тестовые файлы
                            if (text.contains("@Test") ||
                                    text.contains("org.junit.Test")) {
                                return;
                            }

                            // Удаляем комментарии
                            text = removeComments(text);

                            // Удаляем package и import
                            text = removePackageAndImports(text);

                            // Удаляем пустые строки
                            text = removeEmptyLines(text);

                            // Очищаем начало и конец текста
                            text = trimText(text);

                            // Размер обработанного текста в байтах UTF-8
                            long size = text.getBytes(
                                    StandardCharsets.UTF_8
                            ).length;

                            // Относительный путь от src
                            String relativePath =
                                    srcPath.relativize(path).toString();

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

        // Вывод результата
        for (FileInfo file : files) {
            System.out.println(file.size + " " + file.path);
        }
    }
}
