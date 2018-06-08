import java.util.Arrays;

class MergeSort {

	public static void sort(int[] arr) {
		int n = arr.length;
		int[] temp_arr = new int[n];
		mergeSort(arr, 0, n - 1, temp_arr);
	}

	public static void mergeSort(int[] arr, int l, int r, int[] temp_arr) {
		if (l < r) {
			int mid = l + (r - l) / 2;
			mergeSort(arr, l, mid, temp_arr);
			mergeSort(arr, mid + 1, r, temp_arr);
			merge(arr, l, mid, r, temp_arr);
		}
	}

	public static void merge(int[] arr, int l, int mid, int r, int[] temp_arr) {

		int i = l;
		int j = mid + 1;
		int t = 0;

		while (i <= mid && j <= r) {
			if (arr[i] < arr[j]) {
				temp_arr[t++] = arr[i];
				i++;
			} else {
				temp_arr[t++] = arr[j];
				j++;
			}
		}

		while (i <= mid)
			temp_arr[t++] = arr[i++];
		while (j <= r)
			temp_arr[t++] = arr[j++];

		t = 0;
		while (l < r)
			arr[l++] = temp_arr[t++];

	}


	public static void main(String[] args) {
		int[] arr = {2,3,1,4};
		MergeSort.sort(arr);
		System.out.println(Arrays.toString(arr));
	}
}