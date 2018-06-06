- [滑动窗口](#----)
  * [1. 76. Minimum Window Substring](#1-76-minimum-window-substring)
    + [题目描述](#----)
  * [2. 438. Find All Anagrams in a String](#2-438-find-all-anagrams-in-a-string)
    + [题目描述](#-----1)
  * [3. 3. Longest Substring Without Repeating Characters](#3-3-longest-substring-without-repeating-characters)
    + [题目描述](#-----2)
  * [4. 209. Minimum Size Subarray Sum](#4-209-minimum-size-subarray-sum)
    + [题目描述](#-----3)

## 滑动窗口

套路

### 1. 76. Minimum Window Substring

[LeetCode: 76. Minimum Window Substring](https://leetcode.com/problems/minimum-window-substring/description/)

#### 题目描述

Given a string S and a string T, find the minimum window in S which will contain all the characters in T in complexity O(n).

Example:

Input: S = "ADOBECODEBANC", T = "ABC"

Output: "BANC"

Note:

If there is no such window in S that covers all characters in T, return the empty string "".

If there is such window, you are guaranteed that there will always be only one unique minimum window in S.

```java
class Solution {
    public String minWindow(String s, String t) {
        
        int s_len = s.length();
        int t_len = t.length();
        int ruler = t_len;
        
        int res = Integer.MAX_VALUE;
        int start = 0;
        
        int[] freq = new int[128];
        
        char[] c_s = s.toCharArray();
        char[] c_t = t.toCharArray();
        
        // count char in t
        for (char c : c_t)
            freq[c]++;
        
        int i = 0, j = 0;
        while (j < s_len) {
            
            // whether ruler can expand
            if (freq[c_s[j++]]-- >= 1)
                ruler--;
            
            // if ruler == 0, it means all char in t has been contained in window now
            while (ruler == 0) {
                
                // choose min window
                if (res > j - i) {
                    res = j - i;
                    start = i;
                }
                
                // narrow left border
                if (freq[c_s[i++]]++ == 0)
                    ruler++;
                    
            }
            
        }
        
        return res == Integer.MAX_VALUE ? "" : s.substring(start, start + res);
        
    }
}
```

### 2. 438. Find All Anagrams in a String

[LeetCode: 438. Find All Anagrams in a String](https://leetcode.com/problems/find-all-anagrams-in-a-string/description/)

#### 题目描述

Given a string s and a non-empty string p, find all the start indices of p's anagrams in s.

Strings consists of lowercase English letters only and the length of both strings s and p will not be larger than 20,100.

The order of output does not matter.

Example 1:

Input:
s: "cbaebabacd" p: "abc"

Output:
[0, 6]

Explanation:

The substring with start index = 0 is "cba", which is an anagram of "abc".

The substring with start index = 6 is "bac", which is an anagram of "abc".


Example 2:

Input:
s: "abab" p: "ab"

Output:
[0, 1, 2]

Explanation:

The substring with start index = 0 is "ab", which is an anagram of "ab".

The substring with start index = 1 is "ba", which is an anagram of "ab".

The substring with start index = 2 is "ab", which is an anagram of "ab".

窗口大小被要求成 p_len
```java
class Solution {
    public List<Integer> findAnagrams(String s, String p) {
        
        List<Integer> res = new ArrayList<>();
        
        int s_len = s.length();
        int p_len = p.length();
        int ruler = p_len;
        int i = 0, j = 0;
        
        int[] freq = new int[128];
        
        char[] c_s = s.toCharArray();
        
        for (char c : p.toCharArray())
            freq[c]++;
        
        while (j < s_len) {
            
            if (freq[c_s[j++]]-- >= 1) {
                ruler--;
            }
            
            if (ruler == 0)
                res.add(i);
            
            if (j - i == p_len && freq[c_s[i++]]++ >= 0) {
                ruler++;
            }
            
        }
        
        return res;
        
    }
}
```

### 3. 3. Longest Substring Without Repeating Characters

[LeetCode: 3. Longest Substring Without Repeating Characters](https://leetcode.com/problems/longest-substring-without-repeating-characters/description/)

#### 题目描述

Given a string, find the length of the longest substring without repeating characters.

Examples:

Given "abcabcbb", the answer is "abc", which the length is 3.

Given "bbbbb", the answer is "b", with the length of 1.

Given "pwwkew", the answer is "wke", with the length of 3.

Note that the answer must be a substring, "pwke" is a subsequence and not a substring.

```java
class Solution {
    public int lengthOfLongestSubstring(String s) {
        
        int n = s.length();
        int[] freq = new int[128];
        int res = 0;
        int i = 0, j = -1;
        char[] c = s.toCharArray();
        
        while (i < n) {
            
            if (j + 1 < n && freq[c[j + 1]] == 0)
                freq[c[++j]]++;
            else
                freq[c[i++]]--;
            
            res = Math.max(res, j - i + 1);
        }
        
        return res;
    }
}
```

### 4. 209. Minimum Size Subarray Sum

[LeetCode: 209. Minimum Size Subarray Sum](https://leetcode.com/problems/minimum-size-subarray-sum/description/)

#### 题目描述

Given an array of n positive integers and a positive integer s, find the minimal length of a contiguous subarray of which the sum ≥ s. If there isn't one, return 0 instead.

Example: 

Input: s = 7, nums = [2,3,1,2,4,3]

Output: 2

Explanation: the subarray [4,3] has the minimal length under the problem constraint.

Follow up:

If you have figured out the O(n) solution, try coding another solution of which the time complexity is O(n log n). 

```java
class Solution {
    public int minSubArrayLen(int s, int[] nums) {
        
        int n = nums.length;
        int i = 0, j = 0;
        int sum = 0;
        int res = Integer.MAX_VALUE;
        
        while (j < n) {
            
            if (sum < s)
                sum += nums[j];
            j++;
            
            while (sum >= s) {
                
                if (res > j - i)
                    res = j - i;
                
                sum -= nums[i];
                i++;
                
            }

        }
        
        return res == Integer.MAX_VALUE ? 0 : res;
        
    }
}
```