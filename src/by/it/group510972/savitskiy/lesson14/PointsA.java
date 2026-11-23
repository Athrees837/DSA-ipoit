
package by.it.group510972.savitskiy.lesson14;

import java.util.Scanner;

public class PointsA {

    // Поиск корня с использованием сжатия путей
    private static int find(int[] parent, int x) {
        if (parent[x] != x) {
            parent[x] = find(parent, parent[x]);
        }
        return parent[x];
    }

    // Объединение множеств по размеру
    private static void union(int[] parent, int[] size, int a, int b) {
        int rootA = find(parent, a);
        int rootB = find(parent, b);

        if (rootA == rootB) {
            return;
        }

        // Меньшее дерево присоединяем к большему
        if (size[rootA] < size[rootB]) {
            int temp = rootA;
            rootA = rootB;
            rootB = temp;
        }

        parent[rootB] = rootA;
        size[rootA] += size[rootB];
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        double D = scanner.nextDouble();
        int N = scanner.nextInt();

        // Координаты точек
        double[][] points = new double[N][3];

        for (int i = 0; i < N; i++) {
            points[i][0] = scanner.nextDouble();
            points[i][1] = scanner.nextDouble();
            points[i][2] = scanner.nextDouble();
        }

        // Инициализация DSU
        int[] parent = new int[N];
        int[] size = new int[N];

        for (int i = 0; i < N; i++) {
            parent[i] = i;
            size[i] = 1;
        }

        // Проверяем расстояние между всеми парами точек
        for (int i = 0; i < N; i++) {
            for (int j = i + 1; j < N; j++) {

                double dx = points[i][0] - points[j][0];
                double dy = points[i][1] - points[j][1];
                double dz = points[i][2] - points[j][2];

                double distance = Math.sqrt(
                        dx * dx + dy * dy + dz * dz
                );

                // Расстояние D не включается
                if (distance < D) {
                    union(parent, size, i, j);
                }
            }
        }

        // Подсчитываем количество точек в каждом кластере
        int[] clusters = new int[N];
        int count = 0;

        for (int i = 0; i < N; i++) {
            if (find(parent, i) == i) {
                clusters[count++] = size[i];
            }
        }

        // Сортировка размеров кластеров по возрастанию
        for (int i = 0; i < count - 1; i++) {
            for (int j = i + 1; j < count; j++) {
                if (clusters[i] < clusters[j]) {
                    int temp = clusters[i];
                    clusters[i] = clusters[j];
                    clusters[j] = temp;
                }
            }
        }

        // Вывод результата
        for (int i = 0; i < count; i++) {
            if (i > 0) {
                System.out.print(" ");
            }
            System.out.print(clusters[i]);
        }
    }
}
