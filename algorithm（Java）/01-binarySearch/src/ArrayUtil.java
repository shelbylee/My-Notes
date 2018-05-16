public class ArrayUtil {

    private ArrayUtil() {
        // prevent instantiation
    }

    /**
     * 生成包含 n 个元素的有序数组
     * @param n n
     * @return Integer 数组
     */
    public static Integer[] generateSortedArray(int n) {

        assert n > 0;

        Integer[] arr = new Integer[n];
        for (int i = 0; i < n; i++) {
            arr[i] = i;
        }

        return arr;
    }
}