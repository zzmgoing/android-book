package code;

import com.sun.jmx.remote.internal.ArrayQueue;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

public class Test {

    public static class ListNode {

        int val;
        ListNode next;

        ListNode() {
        }

        ListNode(int val) {
            this.val = val;
        }

        ListNode(int val, ListNode next) {
            this.val = val;
            this.next = next;
        }

        ListNode(int[] nums) {
            this.val = nums[0];
            ListNode cur = this;
            for (int i = 1; i < nums.length; i++) {
                cur.next = new ListNode(nums[i]);
                cur = cur.next;
            }
        }

    }

    public static void main(String[] args) {

        // 两数之和
        twoSum(new int[]{2, 7, 11, 15}, 9);

        // 三数之和
        threeSum(new int[]{-1, 0, 1, 2, -1, -4});

        // 两数相加
        addTwoNumbers(new ListNode(new int[]{2, 4, 3}), new ListNode(new int[]{5, 6, 4}));

        // 无重复字符的最长子串
        lengthOfLongestSubstring("abcabcbb");

        // 寻找两个正序数组的中位数
        findMedianSortedArrays(new int[]{1, 2}, new int[]{3, 4});

        // 最长回文子串
        longestPalindrome("babad");

        // Z字形变换
        convert("PAYPALISHIRING", 4);

        // 接雨水
        trap(new int[]{0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1});

        // 盛最多水的容器
        maxArea(new int[]{1, 8, 6, 2, 5, 4, 8, 3, 7});

        // 反转链表
        reverseList(new ListNode(new int[]{1, 2, 3, 4, 5}));

        // 合并两个有序链表
        mergeTwoLists(new ListNode(new int[]{1, 2, 4}), new ListNode(new int[]{1, 3, 4}));

        // K个一组翻转链表
        reverseKGroup(new ListNode(new int[]{1, 2, 3, 4, 5}), 2);

        // LRU缓存
//        LRUCache();

    }

    public static int[] twoSum(int[] nums, int target) {

    }

    public static List<List<Integer>> threeSum(int[] nums) {

    }

    public static ListNode addTwoNumbers(ListNode l1, ListNode l2) {

    }

    public static int lengthOfLongestSubstring(String s) {

    }

    public static double findMedianSortedArrays(int[] nums1, int[] nums2) {

    }

    public static String longestPalindrome(String s) {

    }

    public static String convert(String s, int numRows) {

    }

    public static int trap(int[] height) {

    }

    public static int maxArea(int[] height) {

    }

    public static ListNode reverseList(ListNode head) {

    }

    public static ListNode mergeTwoLists(ListNode list1, ListNode list2) {

    }

    public static ListNode reverseKGroup(ListNode head, int k) {

    }

    class LRUCache {



    }

}
