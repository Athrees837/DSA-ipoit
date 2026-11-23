
package by.it.group510972.savitskiy.lesson05;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.Arrays;

public class A_QSort {

    public static void main(String[] args) throws FileNotFoundException {
        InputStream stream = A_QSort.class.getResourceAsStream("dataA.txt");
        A_QSort instance = new A_QSort();
        int[] result = instance.getAccessory(stream);

        for (int index : result) {
            System.out.print(index + " ");
        }
    }

    int[] getAccessory(InputStream stream) throws FileNotFoundException {
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
        int[] starts = new int[n];
        int[] stops = new int[n];

        for (int i = 0; i < n; i++) {
            starts[i] = segments[i].start;
            stops[i] = segments[i].stop;
        }

        Arrays.sort(starts);
        Arrays.sort(stops);

        for (int i = 0; i < m; i++) {
            int left = upperBound(starts, points[i]);
            int right = lowerBound(stops, points[i]);

            result[i] = left - right;
        }

        scanner.close();
        return result;
    }

    private int upperBound(int[] array, int value) {
        int left = 0;
        int right = array.length;

        while (left < right) {
            int middle = left + (right - left) / 2;

            if (array[middle] <= value) {
                left = middle + 1;
            } else {
                right = middle;
            }
        }

        return left;
    }
    private int lowerBound(int[] array, int value) {
        int left = 0;
        int right = array.length;

        while (left < right) {
            int middle = left + (right - left) / 2;

            if (array[middle] < value) {
                left = middle + 1;
            } else {
                right = middle;
            }
        }

        return left;
    }
    private class Segment implements Comparable<Segment> {
        int start;
        int stop;

        Segment(int start, int stop) {
            this.start = Math.min(start, stop);
            this.stop = Math.max(start, stop);
        }

        @Override
        public int compareTo(Segment o) {
            return Integer.compare(this.start, o.start);
        }
    }
}
