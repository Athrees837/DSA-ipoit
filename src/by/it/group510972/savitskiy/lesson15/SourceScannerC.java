
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

public class SourceScannerC {

    private static final int LIMIT = 9;
    private static final int INF = 10;

    static class FileInfo {
        String path;
        String text;

        FileInfo(String path, String text) {
            this.path = path;
            this.text = text;
        }
    }

    // Чтение файла
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

                if (i + 2 < length && text.startsWith("\"\"\"", i)) {
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

            } else if (state == 1 || state == 2) {

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

            } else if (state == 3) {

                if (c == '\n' || c == '\r') {
                    result.append(c);
                    state = 0;
                }
                i++;

            } else if (state == 4) {

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

            } else if (state == 5) {

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

    // Проверка package и import
    static boolean isPackageOrImport(String text, int start, int end) {
        int pos = start;

        while (pos < end && text.charAt(pos) < 33) {
            pos++;
        }

        if (text.startsWith("package", pos)) {
            int next = pos + 7;
            return next == end ||
                    text.charAt(next) < 33 ||
                    text.charAt(next) == ';';
        }

        if (text.startsWith("import", pos)) {
            int next = pos + 6;
            return next == end ||
                    text.charAt(next) < 33 ||
                    text.charAt(next) == ';';
        }

        return false;
    }

    // Удаление package и import
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

    // Замена последовательностей символов с кодом < 33 одним пробелом
    static String normalizeText(String text) {
        StringBuilder result = new StringBuilder();

        boolean needSpace = false;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c < 33) {
                needSpace = true;
            } else {
                if (needSpace && result.length() > 0) {
                    result.append(' ');
                }

                result.append(c);
                needSpace = false;
            }
        }

        return result.toString().trim();
    }

    // Обычный алгоритм Левенштейна для диагностики
    static int fullLevenshtein(String a, String b) {
        int n = a.length();
        int m = b.length();

        int[] previous = new int[m + 1];
        int[] current = new int[m + 1];

        for (int j = 0; j <= m; j++) {
            previous[j] = j;
        }

        for (int i = 1; i <= n; i++) {
            current[0] = i;

            for (int j = 1; j <= m; j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;

                current[j] = Math.min(
                        previous[j] + 1,
                        Math.min(
                                current[j - 1] + 1,
                                previous[j - 1] + cost
                        )
                );
            }

            int[] temp = previous;
            previous = current;
            current = temp;
        }

        return previous[m];
    }

    // Ограниченное расстояние Левенштейна
    static int levenshtein(String a, String b) {

        if (Math.abs(a.length() - b.length()) > LIMIT) {
            return INF;
        }

        int start = 0;

        while (start < a.length() &&
                start < b.length() &&
                a.charAt(start) == b.charAt(start)) {
            start++;
        }

        int endA = a.length();
        int endB = b.length();

        while (endA > start && endB > start &&
                a.charAt(endA - 1) == b.charAt(endB - 1)) {
            endA--;
            endB--;
        }

        int lenA = endA - start;
        int lenB = endB - start;

        if (Math.abs(lenA - lenB) > LIMIT) {
            return INF;
        }

        if (lenA == 0) {
            return lenB <= LIMIT ? lenB : INF;
        }

        if (lenB == 0) {
            return lenA <= LIMIT ? lenA : INF;
        }

        if (lenA > lenB) {
            String temp = a;
            a = b;
            b = temp;

            int tempLength = lenA;
            lenA = lenB;
            lenB = tempLength;
        }

        int n = lenA;
        int m = lenB;

        int[] previous = new int[n + 1];
        int[] current = new int[n + 1];

        for (int j = 0; j <= Math.min(n, LIMIT); j++) {
            previous[j] = j;
        }

        int previousLeft = 0;
        int previousRight = Math.min(n, LIMIT);

        for (int i = 1; i <= m; i++) {

            int left = Math.max(0, i - LIMIT);
            int right = Math.min(n, i + LIMIT);

            if (left == 0) {
                current[0] = i;
            }

            for (int j = Math.max(1, left); j <= right; j++) {

                int deletion = INF;
                int insertion = INF;
                int substitution = INF;

                if (j >= previousLeft && j <= previousRight) {
                    deletion = previous[j] + 1;
                }

                if (j - 1 >= left) {
                    insertion = current[j - 1] + 1;
                }

                if (j - 1 >= previousLeft &&
                        j - 1 <= previousRight) {

                    int cost = a.charAt(start + j - 1) ==
                            b.charAt(start + i - 1) ? 0 : 1;

                    substitution = previous[j - 1] + cost;
                }

                current[j] = Math.min(
                        deletion,
                        Math.min(insertion, substitution)
                );
            }

            int[] temp = previous;
            previous = current;
            current = temp;

            previousLeft = left;
            previousRight = right;
        }

        if (n < previousLeft || n > previousRight) {
            return INF;
        }

        return previous[n] <= LIMIT ? previous[n] : INF;
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

                            if (text.contains("@Test") ||
                                    text.contains("org.junit.Test")) {
                                return;
                            }

                            text = removeComments(text);
                            text = removePackageAndImports(text);
                            text = normalizeText(text);

                            String relativePath =
                                    srcPath.relativize(path).toString();

                            files.add(new FileInfo(relativePath, text));

                        } catch (IOException e) {
                            // Пропускаем нечитаемые файлы
                        }
                    });

        } catch (IOException e) {
            return;
        }

        files.sort(Comparator.comparing(f -> f.path));

        // Диагностика ближайшего файла для FiboA.java
        for (FileInfo a : files) {
            if (a.path.endsWith("FiboA.java")) {

                int minDistance = Integer.MAX_VALUE;
                String nearestPath = "";

                for (FileInfo b : files) {
                    if (a == b) {
                        continue;
                    }

                    int distance = fullLevenshtein(a.text, b.text);

                    if (distance < minDistance) {
                        minDistance = distance;
                        nearestPath = b.path;
                    }
                }

                System.out.println("Файл: " + a.path);
                System.out.println("Ближайший файл: " + nearestPath);
                System.out.println("Расстояние: " + minDistance);
            }
        }

        int count = files.size();

        List<List<Integer>> copies = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            copies.add(new ArrayList<>());
        }

        for (int i = 0; i < count; i++) {
            for (int j = i + 1; j < count; j++) {

                int distance = levenshtein(
                        files.get(i).text,
                        files.get(j).text
                );

                if (distance < 10) {
                    copies.get(i).add(j);
                    copies.get(j).add(i);
                }
            }
        }

        // Вывод результата
        for (int i = 0; i < count; i++) {
            if (!copies.get(i).isEmpty()) {

                System.out.println(files.get(i).path);

                for (int copyIndex : copies.get(i)) {
                    System.out.println(files.get(copyIndex).path);
                }
            }
        }
    }
}
