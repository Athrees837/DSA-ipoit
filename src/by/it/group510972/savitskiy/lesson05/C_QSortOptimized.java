package by.it.group510972.savitskiy.lesson05;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

/*
Видеорегистраторы и площадь 2.
Условие то же что и в задаче А.

        По сравнению с задачей A доработайте алгоритм так, чтобы
        1) он оптимально использовал время и память:
            - за стек отвечает элиминация хвостовой рекурсии
            - за сам массив отрезков - сортировка на месте
            - рекурсивные вызовы должны проводиться на основе 3-разбиения

        2) при поиске подходящих отрезков для точки реализуйте метод бинарного поиска
        для первого отрезка решения, а затем найдите оставшуюся часть решения
        (т.е. отрезков, подходящих для точки, может быть много)

    Sample Input:
    2 3
    0 5
    7 10
    1 6 11
    Sample Output:
    1 0 0

*/


public class C_QSortOptimized {

    public static void main(String[] args) throws FileNotFoundException {
        InputStream stream = C_QSortOptimized.class.getResourceAsStream("dataC.txt");
        C_QSortOptimized instance = new C_QSortOptimized();
        int[] result = instance.getAccessory2(stream);
        for (int index : result) {
            System.out.print(index + " ");
        }
    }


    int[] getAccessory2(InputStream stream) throws FileNotFoundException {
        Scanner scanner = new Scanner(stream);

        int n = scanner.nextInt();
        Segment[] segments = new Segment[n];

        int m = scanner.nextInt();
        int[] points = new int[m];
        int[] result = new int[m];

        for (int i = 0; i < n; i++) {
            segments[i] = new Segment(scanner.nextInt(), scanner.nextInt());
        }

        for (int i = 0; i < m; i++) {
            points[i] = scanner.nextInt();
        }

        quickSort(segments, 0, n - 1);

        for (int i = 0; i < m; i++) {
            int point = points[i];

            int pos = upperBound(segments, point);

            int count = 0;

            for (int j = 0; j < pos; j++) {
                if (segments[j].stop >= point) {
                    count++;
                }
            }

            result[i] = count;
        }

        scanner.close();
        return result;
    }

    private int upperBound(Segment[] segments, int point) {
        int left = 0;
        int right = segments.length;

        while (left < right) {
            int middle = left + (right - left) / 2;

            if (segments[middle].start <= point) {
                left = middle + 1;
            } else {
                right = middle;
            }
        }

        return left;
    }

    private void quickSort(Segment[] segments, int left, int right) {
        while (left < right) {

            Segment pivot = segments[left + (right - left) / 2];

            int lt = left;
            int i = left;
            int gt = right;

            while (i <= gt) {
                int cmp = segments[i].compareTo(pivot);

                if (cmp < 0) {
                    Segment temp = segments[lt];
                    segments[lt] = segments[i];
                    segments[i] = temp;

                    lt++;
                    i++;
                } else if (cmp > 0) {
                    Segment temp = segments[i];
                    segments[i] = segments[gt];
                    segments[gt] = temp;
                    gt--;
                } else {
                    i++;
                }
            }
            if (lt - left < right - gt) {
                quickSort(segments, left, lt - 1);
                left = gt + 1;
            } else {
                quickSort(segments, gt + 1, right);
                right = lt - 1;
            }
        }
    }
    private class Segment implements Comparable<Segment> {
        int start;
        int stop;

        Segment(int start, int stop) {
            this.start = start;
            this.stop = stop;
        }

        @Override
        public int compareTo(Segment o) {
            int cmp = Integer.compare(this.start, o.start);

            if (cmp == 0) {
                return Integer.compare(this.stop, o.stop);
            }

            return cmp;
        }
    }


}
