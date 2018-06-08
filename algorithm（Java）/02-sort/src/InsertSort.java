import java.util.Arrays;

class InsertSort {

	public static void insertSort1(int[] arr) {

		int n = arr.length;

		for (int i = 1; i < n; i++) {
			for (int j = i - 1; j >= 0; j--) {
				if (arr[j + 1] < arr[j]) {
					swap(arr, j + 1, j);
				}
			}
		}

	}

	public static void insertSort2(int[] arr) {

		int n = arr.length;

		for (int i = 1; i < n; i++) {

			if (arr[i] < arr[i - 1]) {

				int key = arr[i];
				int insertIndex = binarySearch(arr, 0, i - 1, arr[i]);

				for (int j = i - 1; j >= insertIndex; j--) {
					arr[j + 1] = arr[j];
				}

				arr[insertIndex] = key;
			}

		}

	}

	public static int binarySearch(int[] arr, int low, int high, int key) {

		while (low <= high) {

			int mid = low + (high - low) / 2;

			if (key < arr[mid])
				high = mid - 1;
			else
				low = mid + 1;

		}

		return low;

	}

	public static void swap(int[] arr, int i, int j) {
		int temp = arr[i];
		arr[i] = arr[j];
		arr[j] = temp;
	}

	public static void main(String[] args) {
		int[] arr = {2,3,1,4};
		InsertSort.insertSort2(arr);
		System.out.println(Arrays.toString(arr));
	}

}