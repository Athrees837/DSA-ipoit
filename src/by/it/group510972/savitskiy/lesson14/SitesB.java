
package by.it.group510972.savitskiy.lesson14;

import java.util.Scanner;

public class SitesB {

    private static int[] parent;
    private static int[] size;
    private static int siteCount;

    // Собственная хеш-таблица для хранения названий сайтов
    private static String[] hashKeys;
    private static int[] hashValues;
    private static int hashCapacity;
    private static int hashSize;

    // Увеличение массива сайтов
    private static void ensureSiteCapacity() {
        if (siteCount == parent.length) {
            int newCapacity = parent.length * 2;

            int[] newParent = new int[newCapacity];
            int[] newSize = new int[newCapacity];

            System.arraycopy(parent, 0, newParent, 0, siteCount);
            System.arraycopy(size, 0, newSize, 0, siteCount);

            parent = newParent;
            size = newSize;
        }
    }

    // Хеш-функция
    private static int hash(String key) {
        int h = key.hashCode();
        h ^= h >>> 16;
        return h & (hashCapacity - 1);
    }

    // Поиск позиции в хеш-таблице
    private static int findSlot(String key) {
        int index = hash(key);

        while (hashKeys[index] != null) {
            if (hashKeys[index].equals(key)) {
                return index;
            }

            index = (index + 1) & (hashCapacity - 1);
        }

        return index;
    }

    // Расширение хеш-таблицы
    private static void resizeHashTable() {
        String[] oldKeys = hashKeys;
        int[] oldValues = hashValues;

        hashCapacity *= 2;
        hashKeys = new String[hashCapacity];
        hashValues = new int[hashCapacity];

        for (int i = 0; i < oldKeys.length; i++) {
            if (oldKeys[i] != null) {
                int index = findSlot(oldKeys[i]);

                hashKeys[index] = oldKeys[i];
                hashValues[index] = oldValues[i];
            }
        }
    }

    // Получение индекса сайта
    private static int getSiteIndex(String site) {
        int index = findSlot(site);

        if (hashKeys[index] != null) {
            return hashValues[index];
        }

        // Поддерживаем коэффициент заполнения не выше 0.5
        if ((hashSize + 1) * 2 > hashCapacity) {
            resizeHashTable();
            index = findSlot(site);
        }

        ensureSiteCapacity();

        int newIndex = siteCount++;

        parent[newIndex] = newIndex;
        size[newIndex] = 1;

        hashKeys[index] = site;
        hashValues[index] = newIndex;
        hashSize++;

        return newIndex;
    }

    // Поиск корня со сжатием путей
    private static int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }

        return parent[x];
    }

    // Объединение по размеру
    private static void union(int a, int b) {
        int rootA = find(a);
        int rootB = find(b);

        if (rootA == rootB) {
            return;
        }

        if (size[rootA] < size[rootB]) {
            int temp = rootA;
            rootA = rootB;
            rootB = temp;
        }

        parent[rootB] = rootA;
        size[rootA] += size[rootB];
    }

    // Сортировка слиянием по убыванию
    private static void mergeSort(int[] array, int[] temp, int left, int right) {
        if (left >= right) {
            return;
        }

        int middle = left + (right - left) / 2;

        mergeSort(array, temp, left, middle);
        mergeSort(array, temp, middle + 1, right);

        int i = left;
        int j = middle + 1;
        int k = left;

        while (i <= middle && j <= right) {
            if (array[i] >= array[j]) {
                temp[k++] = array[i++];
            } else {
                temp[k++] = array[j++];
            }
        }

        while (i <= middle) {
            temp[k++] = array[i++];
        }

        while (j <= right) {
            temp[k++] = array[j++];
        }

        for (i = left; i <= right; i++) {
            array[i] = temp[i];
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Инициализация DSU
        parent = new int[16];
        size = new int[16];
        siteCount = 0;

        // Инициализация хеш-таблицы
        hashCapacity = 32;
        hashKeys = new String[hashCapacity];
        hashValues = new int[hashCapacity];
        hashSize = 0;

        // Чтение входных данных
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();

            if (line.equals("end")) {
                break;
            }

            if (line.isEmpty()) {
                continue;
            }

            String[] pair = line.split("\\+");

            String siteA = pair[0].trim();
            String siteB = pair[1].trim();

            int a = getSiteIndex(siteA);
            int b = getSiteIndex(siteB);

            union(a, b);
        }

        // Собираем размеры кластеров
        int[] clusters = new int[siteCount];
        int clusterCount = 0;

        for (int i = 0; i < siteCount; i++) {
            if (find(i) == i) {
                clusters[clusterCount++] = size[i];
            }
        }

        // Сортируем по убыванию
        if (clusterCount > 1) {
            int[] temp = new int[clusterCount];
            mergeSort(clusters, temp, 0, clusterCount - 1);
        }

        // Вывод результата
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < clusterCount; i++) {
            if (i > 0) {
                result.append(" ");
            }

            result.append(clusters[i]);
        }

        System.out.println(result);
    }
}
