import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

class RadixSort {

	public static void radixSort(int[] arr, int n) {

		int max = 0;
		for (int i = 1; i < n; i++) {
			if (arr[i] > max)
				max = arr[i];
		}

		for (int k = 1; max / k > 0; k *= 10)
			countingSort(arr, n, k);
	}

	public static void countingSort(int[] arr, int n, int k) {

		int C[] = new int[10];

		for (int i = 0; i < n; i++)
			C[(arr[i] / k) % 10]++;

		for (int i = 1; i < 10; i++)
			C[i] = C[i] + C[i - 1];

		int[] B = new int[n];
		for (int i = n - 1; i >= 0; i--) {
			B[C[(arr[i] / k) % 10] - 1] = arr[i];
			C[(arr[i] / k) % 10]--;
		}

		for (int i = 0; i < n; i++)
			arr[i] = B[i];
	}

	public static void main(String[] args) {
		int[] arr = {2,3,1,4};
		RadixSort.radixSort(arr, arr.length);
		System.out.println(Arrays.toString(arr));
	}
}