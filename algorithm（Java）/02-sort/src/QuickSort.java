import java.util.Arrays;

class QuickSort {

	public static void quickSort(int[] arr, int low, int high) {
		if (low < high) {
			int pivotLoc = partition(arr, low, high);
			quickSort(arr, low, pivotLoc - 1);
			quickSort(arr, pivotLoc + 1, high);
		}
	}

	public static int partition(int[] arr, int low, int high) {

		int pivot = arr[low];

		while (low < high) {
			while (low < high && arr[high] >= pivot)
				high--;
			swap(arr, low, high);

			while (low < high && arr[low] <= pivot)
				low++;
			swap(arr, low, high);
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
		QuickSort.quickSort(arr, 0, arr.length - 1);
		System.out.println(Arrays.toString(arr));
	}
}