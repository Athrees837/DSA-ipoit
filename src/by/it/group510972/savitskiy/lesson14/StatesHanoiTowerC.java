
package by.it.group510972.savitskiy.lesson14;

import java.util.Scanner;

public class StatesHanoiTowerC {

    // Поиск корня с сокращением пути
    static int find(int[] parent, int v) {
        if (parent[v] != v) {
            parent[v] = find(parent, parent[v]);
        }
        return parent[v];
    }

    // Объединение по размеру поддерева
    static void union(int[] parent, int[] size, int a, int b) {
        int rootA = find(parent, a);
        int rootB = find(parent, b);

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

    // Рекурсивное решение задачи Ханойских башен
    static void hanoi(
            int n,
            int from,
            int to,
            int auxiliary,
            int[] heights,
            int[] first,
            int[] parent,
            int[] size,
            int[] step
    ) {
        if (n == 0) {
            return;
        }

        // Перемещаем верхние n-1 дисков на вспомогательный стержень
        hanoi(n - 1, from, auxiliary, to,
                heights, first, parent, size, step);

        // Перемещаем диск со стержня from на стержень to
        heights[from]--;
        heights[to]++;

        step[0]++;

        // Находим максимальную высоту после текущего хода
        int maxHeight = Math.max(
                heights[0],
                Math.max(heights[1], heights[2])
        );

        int currentStep = step[0];

        // Объединяем шаги с одинаковой максимальной высотой
        if (first[maxHeight] == -1) {
            first[maxHeight] = currentStep;
        } else {
            union(parent, size, first[maxHeight], currentStep);
        }

        // Перемещаем оставшиеся n-1 дисков на целевой стержень
        hanoi(n - 1, auxiliary, to, from,
                heights, first, parent, size, step);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int n = scanner.nextInt();

        if (n <= 0) {
            return;
        }

        // Общее количество ходов
        int totalMoves = (1 << n) - 1;

        // Количество дисков на каждом стержне
        // A = 0, B = 1, C = 2
        int[] heights = {n, 0, 0};

        // DSU
        int[] parent = new int[totalMoves + 1];
        int[] size = new int[totalMoves + 1];

        for (int i = 1; i <= totalMoves; i++) {
            parent[i] = i;
            size[i] = 1;
        }

        // first[h] — первый шаг с максимальной высотой h
        int[] first = new int[n + 1];

        for (int i = 0; i <= n; i++) {
            first[i] = -1;
        }

        int[] step = {0};

        // Переносим все диски с A на B, используя C
        hanoi(
                n,
                0,
                1,
                2,
                heights,
                first,
                parent,
                size,
                step
        );

        // Собираем размеры получившихся множеств
        int[] result = new int[n];
        int count = 0;

        for (int i = 1; i <= totalMoves; i++) {
            if (parent[i] == i) {
                result[count++] = size[i];
            }
        }

        // Сортировка размеров по возрастанию
        for (int i = 0; i < count - 1; i++) {
            for (int j = 0; j < count - 1 - i; j++) {
                if (result[j] > result[j + 1]) {
                    int temp = result[j];
                    result[j] = result[j + 1];
                    result[j + 1] = temp;
                }
            }
        }

        // Вывод результата
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < count; i++) {
            if (i > 0) {
                output.append(" ");
            }
            output.append(result[i]);
        }

        System.out.println(output);
    }
}
