
package by.it.group510972.savitskiy.lesson13;

import java.util.Scanner;

public class GraphC {

    private static boolean[][] graph = new boolean[256][256];
    private static boolean[][] reverseGraph = new boolean[256][256];

    private static boolean[] visited = new boolean[256];
    private static int[] order = new int[256];
    private static int orderSize = 0;

    private static boolean[] exists = new boolean[256];

    // Первый DFS: запоминаем порядок завершения обхода
    private static void dfs1(int vertex) {
        visited[vertex] = true;

        for (int i = 0; i < 256; i++) {
            if (graph[vertex][i] && !visited[i]) {
                dfs1(i);
            }
        }

        order[orderSize++] = vertex;
    }

    // Второй DFS: собираем компоненту сильной связности
    private static void dfs2(int vertex, StringBuilder component) {
        visited[vertex] = true;

        component.append((char) vertex);

        for (int i = 0; i < 256; i++) {
            if (reverseGraph[vertex][i] && !visited[i]) {
                dfs2(i, component);
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();

        // Сбрасываем данные для нового запуска
        graph = new boolean[256][256];
        reverseGraph = new boolean[256][256];
        visited = new boolean[256];
        order = new int[256];
        exists = new boolean[256];
        orderSize = 0;

        // Читаем рёбра графа
        String[] edges = input.split(",");

        for (String edge : edges) {
            String[] vertices = edge.trim().split("->");

            char from = vertices[0].trim().charAt(0);
            char to = vertices[1].trim().charAt(0);

            graph[from][to] = true;
            reverseGraph[to][from] = true;

            exists[from] = true;
            exists[to] = true;
        }

        // Первый проход по исходному графу
        for (int i = 0; i < 256; i++) {
            if (exists[i] && !visited[i]) {
                dfs1(i);
            }
        }

        // Сбрасываем посещённые вершины
        visited = new boolean[256];

        // Второй проход по транспонированному графу
        for (int i = orderSize - 1; i >= 0; i--) {
            int vertex = order[i];

            if (!visited[vertex]) {
                StringBuilder component = new StringBuilder();

                dfs2(vertex, component);

                // Сортируем вершины компоненты по алфавиту
                char[] vertices = component.toString().toCharArray();

                for (int j = 0; j < vertices.length - 1; j++) {
                    for (int k = j + 1; k < vertices.length; k++) {
                        if (vertices[j] > vertices[k]) {
                            char temp = vertices[j];
                            vertices[j] = vertices[k];
                            vertices[k] = temp;
                        }
                    }
                }

                // Выводим компоненту без пробелов
                System.out.println(new String(vertices));
            }
        }
    }
}
