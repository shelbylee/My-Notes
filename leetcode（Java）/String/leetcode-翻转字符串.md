## 翻转字符串

<a id="557">[LeetCode: 557. Reverse Words in a String III](https://leetcode.com/problems/reverse-words-in-a-string-iii/description/)</a>
### 一、LeetCode: 557. Reverse Words in a String III
### 题目描述

Given a string, you need to reverse the order of characters in each word within a sentence while still preserving whitespace and initial word order.

Example 1:

Input: "Let's take LeetCode contest"

Output: "s'teL ekat edoCteeL tsetnoc"

Note: In the string, each word is separated by single space and there will not be any extra space in the string.

空格分隔，逐个反转
```java
class Solution {
    public String reverseWords(String s) {
        
        String[] ss = s.split(" ");
        StringBuilder sb = new StringBuilder();
        int n = ss.length;
        
        for (int i = 0; i < n - 1; i++) {
            sb.append(reverse(ss[i]) + " ");
        }
        sb.append(reverse(ss[n - 1]));
        
        return sb.toString();
    }
    
    public String reverse(String str) {
        StringBuilder sb = new StringBuilder(str);
        return sb.reverse().toString();
    }
}
    
```

<span id="541">[LeetCode: 541. Reverse String II](https://leetcode.com/problems/reverse-string-ii/description/)</span>
### 二、LeetCode: 541. Reverse String II
### 题目描述

Given a string and an integer k, you need to reverse the first k characters for every 2k characters counting from the start of the string. If there are less than k characters left, reverse all of them. If there are less than 2k but greater than or equal to k characters, then reverse the first k characters and left the other as original.

Example:

Input: s = "abcdefg", k = 2

Output: "bacdfeg"

Restrictions:

The string consists of lower English letters only.

Length of the given string and k will in the range [1, 10000]


```java
class Solution {
    public String reverseStr(String s, int k) {
        
        int n = s.length();
        int i = 0, j = n - 1;
        char[] c = s.toCharArray();
        
        while (i < n) {
            j = Math.min(i + k - 1, n - 1);
            reverse(c, i, j);
            i += 2 * k;
        }
        
        return new String(c);
        
    }
    
    public void reverse(char[] c, int i, int j) {
        while (i < j) {
            char temp = c[i];
            c[i] = c[j];
            c[j] = temp;
            i++;
            j--;
        }
    }
}
```

<span id="344">[LeetCode: 344. Reverse String](https://leetcode.com/problems/reverse-string/description/)</span>
### 三、LeetCode: 344. Reverse String
### 题目描述

Write a function that takes a string as input and returns the string reversed.

Example:

Given s = "hello", return "olleh".

当然也可以用 StringBuilder 的 reverse 做，不过用双指针更快。
```java
class Solution {
    public String reverseString(String s) {
        
        int len = s.length();
        int left = 0, right = len - 1;
        char[] c = s.toCharArray();
        
        while(left < right) {
            char temp = c[left];
            c[left] = c[right];
            c[right] = temp;
            left++;
            right--;
        }
        
        return new String(c);
        
    }
}
```
<span id="345">[LeetCode: 345. Reverse Vowels of a String](https://leetcode.com/problems/reverse-vowels-of-a-string/description/)</span>
### 四、LeetCode: 345. Reverse Vowels of a String
### 题目描述


Write a function that takes a string as input and reverse only the vowels of a string.

Example 1:

Given s = "hello", return "holle".

Example 2:

Given s = "leetcode", return "leotcede".

Note:

The vowels does not include the letter "y".

同样地，双指针，注意考虑 vowel 的大小写
```java
class Solution {
    public String reverseVowels(String s) {
        // a e i o u
        int len = s.length();
        int left = 0, right = len - 1;
        char[] c = s.toCharArray();
        
        while(left < right) {
            if (isVowel(c[left]) && isVowel(c[right])) {
                char temp = c[left];
                c[left] = c[right];
                c[right] = temp;
                left++;
                right--;
            } else if (!isVowel(c[left]))
                left++;
            else right--;
        }
        
        return new String(c);
    }
    
    public boolean isVowel(char c) {
        return c == 'a' || c == 'A'
           || c == 'e' || c == 'E'
           || c == 'i' || c == 'I'
           || c == 'o' || c == 'O'
           || c == 'u' || c == 'U';
    }
}
```

<span id="11">[LeetCode: 11. Container With Most Water](https://leetcode.com/problems/container-with-most-water/description/)</span>
### 五、LeetCode: 11. Container With Most Water
### 题目描述

Given n non-negative integers a1, a2, ..., an, where each represents a point at coordinate (i, ai). n vertical lines are drawn such that the two endpoints of line i is at (i, ai) and (i, 0). Find two lines, which together with x-axis forms a container, such that the container contains the most water.

Note: You may not slant the container and n is at least 2.

首先理解题意，就是找一个最大的容器容纳最多的水，这个容器由两个板子组成，每个 an 在数组中的值就是这个板子的高度，画一下图就更清晰了。

[2,1,2] 模拟画图 ≡ω≡ 
```
|   |
| | |     板子...
--------- x轴
0 1 2     数组下标
```

然后抽象出来就是求 <code>(j - i) * Math.min(height[i], height[j])</code> 的最大值

暴力算法很容易想到，但是会超时

有一种 O(n) 的做法，但是不是很好理解，discuss 里有比较清楚的讲解  [Yet another way to see what happens in the O(n) algorithm](https://leetcode.com/problems/container-with-most-water/discuss/6099/Yet-another-way-to-see-what-happens-in-the-O(n)-algorithm)

我理解的核心思想是突破那个短板，如果左边的短，就移动左边的板子，如果右边的短，就移动右边的板子，看能不能形成更大的容器。
```java
class Solution {
    public int maxArea(int[] height) {
        
        int n = height.length;
        int max = 0;
        int i = 0, j = n - 1;
        
        while (i < j) {
            max = Math.max(max, Math.min(height[i], height[j]) * (j - i));
            if (height[i] < height[j])
                i++;
            else
                j--;
        }
        
        return max;
    }
}
```