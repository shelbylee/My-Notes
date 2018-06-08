import java.util.Arrays;

class SelectSort {

	public static void selectSort1(int[] arr) {

		int n = arr.length;

		for (int i = 0; i < n; i++) {

			int minIndex = i;

			for (int j = i + 1; j < n; j++) {
				if (arr[j] < arr[minIndex])
					minIndex = j;
			}

			if (minIndex != i)
				swap(arr, minIndex, i);
		}

	}

	public static void selectSort2(int[] arr) {

		int n = arr.length;

		for (int i = 0; i < n / 2; i++) {

			int min = i;
			int max = i;

			for (int j = i + 1; j < n - i; j++) {
				if (arr[j] < arr[min]) {
					min = j;
					continue;
				}
				if (arr[j] > arr[max])
					max = j;
			}

			swap(arr, i, min);

			if (max != i)
				swap(arr, max, n - i - 1);
			else
				swap(arr, min, n - i - 1);

		}

	}

	public static void heapSort(int[] arr) {

		int n = arr.length;

		for (int i = n / 2 - 1; i >= 0; i--)
			adjustHeap(arr, i, n);

		for (int i = n - 1; i >= 0; i--) {
			swap(arr, 0, i);
			adjustHeap(arr, 0, i);
		}

	}

	public static void adjustHeap(int[] arr, int i, int n) {

		int parent = arr[i];

		for (int k = 2*i + 1; k < n; k = 2*k + 1) {
			if (k + 1 < n && arr[k + 1] > arr[k])
				k++;
			if (arr[k] > arr[i]) {
				arr[i] = arr[k];
				i = k;
			} else break;
		}

		arr[i] = parent;
	}
	
	public static void swap(int[] arr, int i, int j) {
		int temp = arr[i];
		arr[i] = arr[j];
		arr[j] = temp;
	}

	public static void main(String[] args) {
		int[] arr = {2,3,1,4};
		SelectSort.heapSort(arr);
		System.out.println(Arrays.toString(arr));
	}

}