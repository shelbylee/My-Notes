import java.util.Arrays;

class BubbleSort {

	public static void bubbleSort1(int[] arr) {
		int n = arr.length;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n - i - 1; j++) {
				if (arr[j] > arr[j + 1])
					swap(arr, j, j + 1);
			}
		}
	}

	public static void bubbleSort2(int[] arr) {

		int n = arr.length;
		int i = n - 1;

		while (i > 0) {
			int pos = 0;
			for (int j = 0; j < i; j++) {
				if (arr[j] > arr[j + 1]) {
					swap(arr, j, j + 1);
					pos = i;
				}
			}
			i = pos;
		}

	}

	public static void bubbleSort3(int[] arr) {

		int n = arr.length;
		int l = 0;
		int r = n - 1;

		while (l < r) {

			for (int i = l; i < r; i++) {
				if (arr[i] > arr[i + 1])
					swap(arr, i, i + 1);
			}
			r--;

			for (int i = r; i > l; i--) {
				if (arr[i] < arr[i - 1])
					swap(arr, i, i - 1);
			}
			l++;

		}

	}

	public static void swap(int[] arr, int i, int j) {
		int temp = arr[i];
		arr[i] = arr[j];
		arr[j] = temp;
	}


	public static void main(String[] args) {
		int[] arr = {2,3,1,4};
		BubbleSort.bubbleSort3(arr);
		System.out.println(Arrays.toString(arr));
	}
}