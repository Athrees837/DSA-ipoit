
package by.it.group510972.savitskiy.lesson04;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

public class C_GetInversions {

    public static void main(String[] args) throws FileNotFoundException {
        InputStream stream = C_GetInversions.class.getResourceAsStream("dataC.txt");
        C_GetInversions instance = new C_GetInversions();
        int result = instance.calc(stream);
        System.out.print(result);
    }

    int calc(InputStream stream) throws FileNotFoundException {
        Scanner scanner = new Scanner(stream);

        int n = scanner.nextInt();
        int[] a = new int[n];

        for (int i = 0; i < n; i++) {
            a[i] = scanner.nextInt();
        }

        int result = mergeSort(a, 0, n - 1);

        scanner.close();
        return result;
    }
    private int mergeSort(int[] a, int left, int right) {
        if (left >= right) {
            return 0;
        }

        int middle = left + (right - left) / 2;

        int inversions = 0;

        inversions += mergeSort(a, left, middle);
        inversions += mergeSort(a, middle + 1, right);
        inversions += merge(a, left, middle, right);

        return inversions;
    }
    private int merge(int[] a, int left, int middle, int right) {
        int[] temp = new int[right - left + 1];

        int i = left;
        int j = middle + 1;
        int k = 0;
        int inversions = 0;
        while (i <= middle && j <= right) {
            if (a[i] <= a[j]) {
                temp[k++] = a[i++];
            } else {
                temp[k++] = a[j++];
                inversions += middle - i + 1;
            }
        }

        while (i <= middle) {
            temp[k++] = a[i++];
        }

        while (j <= right) {
            temp[k++] = a[j++];
        }
        for (int p = 0; p < temp.length; p++) {
            a[left + p] = temp[p];
        }

        return inversions;
    }
}
