
package by.it.group510972.savitskiy.lesson13;

import java.util.Scanner;

public class GraphA {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();

        // Вершины графа: символы от A до Z
        boolean[][] graph = new boolean[256][256];
        int[] inDegree = new int[256];
        boolean[] exists = new boolean[256];

        // Разбираем входную строку
        String[] edges = input.split(",");

        for (String edge : edges) {
            String[] vertices = edge.trim().split("->");

            char from = vertices[0].trim().charAt(0);
            char to = vertices[1].trim().charAt(0);

            exists[from] = true;
            exists[to] = true;

            // Не добавляем одинаковое ребро дважды
            if (!graph[from][to]) {
                graph[from][to] = true;
                inDegree[to]++;
            }
        }

        StringBuilder result = new StringBuilder();
        boolean[] used = new boolean[256];

        // Подсчитываем количество вершин
        int vertexCount = 0;
        for (int i = 0; i < 256; i++) {
            if (exists[i]) {
                vertexCount++;
            }
        }

        // Топологическая сортировка
        for (int i = 0; i < vertexCount; i++) {

            // Ищем минимальную доступную вершину
            int vertex = -1;

            for (int j = 0; j < 256; j++) {
                if (exists[j] && !used[j] && inDegree[j] == 0) {
                    vertex = j;
                    break;
                }
            }

            // Если вершины нет, граф содержит цикл
            if (vertex == -1) {
                return;
            }

            used[vertex] = true;

            if (result.length() > 0) {
                result.append(" ");
            }

            result.append((char) vertex);

            // Удаляем исходящие рёбра
            for (int j = 0; j < 256; j++) {
                if (graph[vertex][j]) {
                    inDegree[j]--;
                }
            }
        }

        System.out.println(result);
    }
}
