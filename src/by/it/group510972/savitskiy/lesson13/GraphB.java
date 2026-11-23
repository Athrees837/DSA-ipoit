
package by.it.group510972.savitskiy.lesson13;

import java.util.Scanner;

public class GraphB {

    // Поиск цикла в глубину
    private static boolean hasCycle(int vertex, boolean[][] graph, int[] state) {
        state[vertex] = 1;

        for (int i = 0; i < 256; i++) {
            if (graph[vertex][i]) {

                // Обнаружили обратное ребро — есть цикл
                if (state[i] == 1) {
                    return true;
                }

                // Если вершина ещё не посещалась
                if (state[i] == 0) {
                    if (hasCycle(i, graph, state)) {
                        return true;
                    }
                }
            }
        }

        // Вершина полностью обработана
        state[vertex] = 2;
        return false;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();

        // Создаём новые массивы при каждом запуске
        boolean[][] graph = new boolean[256][256];
        int[] state = new int[256];

        // Разбираем рёбра графа
        String[] edges = input.split(",");

        for (String edge : edges) {
            String[] vertices = edge.trim().split("->");

            char from = vertices[0].trim().charAt(0);
            char to = vertices[1].trim().charAt(0);

            graph[from][to] = true;
        }

        // Проверяем все вершины
        boolean cycle = false;

        for (int i = 0; i < 256; i++) {
            if (state[i] == 0) {
                if (hasCycle(i, graph, state)) {
                    cycle = true;
                    break;
                }
            }
        }

        System.out.println(cycle ? "yes" : "no");
    }
}
