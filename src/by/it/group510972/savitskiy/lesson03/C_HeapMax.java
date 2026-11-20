package by.it.group510972.savitskiy.lesson03;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Lesson 3. C_Heap.
// Задача: построить max-кучу = пирамиду = бинарное сбалансированное дерево на массиве.
// ВАЖНО! НЕЛЬЗЯ ИСПОЛЬЗОВАТЬ НИКАКИЕ КОЛЛЕКЦИИ, КРОМЕ ARRAYLIST (его можно, но только для массива)

//      Проверка проводится по данным файла
//      Первая строка входа содержит число операций 1 ≤ n ≤ 100000.
//      Каждая из последующих nn строк задают операцию одного из следующих двух типов:

//      Insert x, где 0 ≤ x ≤ 1000000000 — целое число;
//      ExtractMax.

//      Первая операция добавляет число x в очередь с приоритетами,
//      вторая — извлекает максимальное число и выводит его.

//      Sample Input:
//      6
//      Insert 200
//      Insert 10
//      ExtractMax
//      Insert 5
//      Insert 500
//      ExtractMax
//
//      Sample Output:
//      200
//      500


public class C_HeapMax {

    public static void main(String[] args) throws FileNotFoundException {
        InputStream stream = C_HeapMax.class.getResourceAsStream("dataC.txt");
        C_HeapMax instance = new C_HeapMax();
        System.out.println("MAX=" + instance.findMaxValue(stream));
    }

    Long findMaxValue(InputStream stream) {
        Long maxValue = 0L;
        MaxHeap heap = new MaxHeap();
        Scanner scanner = new Scanner(stream);
        Integer count = scanner.nextInt();
        for (int i = 0; i < count; ) {
            String s = scanner.nextLine();
            if (s.equalsIgnoreCase("extractMax")) {
                Long res = heap.extractMax();
                if (res != null && res > maxValue) maxValue = res;
                System.out.println();
                i++;
            }
            if (s.contains(" ")) {
                String[] p = s.split(" ");
                if (p[0].equalsIgnoreCase("insert"))
                    heap.insert(Long.parseLong(p[1]));
                i++;
            }
        }
        return maxValue;
    }


    private class MaxHeap {

        private List<Long> heap = new ArrayList<>();

        int siftDown(int i) { // просеивание вниз

            int size = heap.size();

            while (true) {
                int left = 2 * i + 1;
                int right = 2 * i + 2;
                int largest = i;

                // Проверяем левого потомка
                if (left < size && heap.get(left) > heap.get(largest)) {
                    largest = left;
                }

                // Проверяем правого потомка
                if (right < size && heap.get(right) > heap.get(largest)) {
                    largest = right;
                }

                // Если текущий элемент максимальный, заканчиваем
                if (largest == i) {
                    break;
                }

                // Меняем элементы местами
                Long temp = heap.get(i);
                heap.set(i, heap.get(largest));
                heap.set(largest, temp);

                i = largest;
            }

            return i;
        }

        int siftUp(int i) { // просеивание вверх

            while (i > 0) {
                int parent = (i - 1) / 2;

                // Если родитель больше или равен текущему элементу
                if (heap.get(parent) >= heap.get(i)) {
                    break;
                }

                // Меняем элементы местами
                Long temp = heap.get(i);
                heap.set(i, heap.get(parent));
                heap.set(parent, temp);

                i = parent;
            }

            return i;
        }

        void insert(Long value) { // вставка

            heap.add(value);

            siftUp(heap.size() - 1);
        }

        Long extractMax() { // извлечение и удаление максимума

            if (heap.isEmpty()) {
                return null;
            }

            Long result = heap.get(0);

            Long last = heap.remove(heap.size() - 1);

            if (!heap.isEmpty()) {
                heap.set(0, last);
                siftDown(0);
            }

            return result;
        }
    }
}