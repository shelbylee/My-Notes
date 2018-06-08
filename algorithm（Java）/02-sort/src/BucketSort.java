import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

class BucketSort {

	public static void bucketSort(int[] arr) {

		int n = arr.length;

		int max = arr[0];
		int min = arr[0];
		for (int num : arr) {
			if (num < min)
				min = num;
			if (num > max)
				max = num;
		}

		int bucketNum = max / 10 - min / 10 + 1;
		List bucketList = new ArrayList<List<Integer>>();
		for (int i = 1; i <= bucketNum; i++) {
			bucketList.add(new ArrayList<Integer>());
		}

		for (int i = 0; i < n; i++) {
			int index = (arr[i] - min) / 10;
			((ArrayList<Integer>)bucketList.get(index)).add(arr[i]);
		}

		ArrayList<Integer> bucket = null;
		int index = 0;
		for (int i = 0; i < bucketNum; i++) {
			bucket = (ArrayList<Integer>)bucketList.get(i);
			bucketInsertSort(bucket);
			for (int num : bucket) {
				arr[index++] = num;
			}
		}
	}

	public static void bucketInsertSort(List<Integer> bucket) {
		for (int i = 0; i < bucket.size(); i++) {
			int temp = bucket.get(i);
			int j = i - 1;
			for (; j >= 0 && bucket.get(j) > temp; j--) {
				bucket.set(j + 1, bucket.get(j));
			}
			bucket.set(j + 1, temp);
		}
	}

	public static void main(String[] args) {
		int[] arr = {2,3,1,4};
		BucketSort.bucketSort(arr);
		System.out.println(Arrays.toString(arr));
	}
}