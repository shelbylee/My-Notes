import java.util.Arrays;

class CountingSort {

	public static int[] countingSort(int[] A, int k) {

		int n = A.length;
		int[] C = new int[k + 1];

		for (int i = 0; i < n; i++)
			C[A[i]]++;

		for (int i = 1; i <= k; i++)
			C[i] = C[i] + C[i - 1];

		int[] B = new int[n];
		for (int i = n - 1; i >= 0; i--) {
			B[C[A[i]] - 1] = A[i];
			C[A[i]]--;
		}

		return B;

	}

	public static void main(String[] args) {
		int[] arr = {2,3,1,4};
		int[] res = CountingSort.countingSort(arr, 4);
		System.out.println(Arrays.toString(res));
	}
}