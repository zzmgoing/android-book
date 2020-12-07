package code.array;

import java.util.Arrays;
import java.util.HashSet;

public class Test {

    public static void main(String[] args){
        String s = "anagram";
        String t = "nagaram";
        System.out.println("是否是字母异位词："+isAnagram(s,t));
        System.out.println("最大子序和："+maxSubArray(new int[]{-2,1,-3,4,-1,2,1,-5,4}));
        System.out.println("买卖股票的最佳时机："+maxProfit(new int[]{7,1,5,3,6,4}));
        System.out.println("爬楼梯："+climbStairs(3));
        System.out.println("删除排序数组中的重复项："+removeDuplicates(new int[]{0,0,1,1,1,2,2,3,3,4}));


    }


    /**
     * 判断字符串t是否是s的字母异位词
     * @param s
     * @param t
     * @return
     */
    public static boolean isAnagram(String s, String t){
        if(s.length() != t.length()){
            return false;
        }
        int[] arr = new int[26];
        for(int i = 0; i < s.length(); i++){
            arr[s.charAt(i) - 'a'] ++;
            arr[t.charAt(i) - 'a'] --;
        }
        for (int i=0;i<26;i++){
            if(arr[i] != 0){
                return false;
            }
        }
        return true;
    }

    /**
     * 最大子序和
     * @param nums
     * @return
     */
    public static int maxSubArray(int[] nums) {
        int pre = 0,sum = nums[0];
        for(int x : nums){
            pre = Math.max(pre + x,x);
            sum = Math.max(sum,pre);
        }
        return sum;
    }

    /**
     * 买卖股票的最佳时机
     * @param prices
     * @return
     */
    public static int maxProfit(int[] prices) {
        if(prices.length <= 1){
            return 0;
        }
        int min = prices[0],max = 0;
        for(int i = 1; i < prices.length; i++){
            max = Math.max(max,prices[i] - min);
            min = Math.min(min,prices[i]);
        }
        return max;
    }

    /**
     * 爬楼梯
     * @param n
     * @return
     */
    public static int climbStairs(int n) {
        int x = 0, y = 0, m = 1;
        for(int i = 1; i <= n; i++){
            x = y;
            y = m;
            m = x + y;
        }
        return m;
    }

    public static int removeDuplicates(int[] nums) {
        HashSet<Integer> set = new HashSet<>();
        int length = nums.length;
        for(int i = 0; i < length;){
            if(set.add(nums[i])){
                i++;
            }else{
                nums[i] = nums[length - 1];
                length--;
            }
        }
        Arrays.sort(nums,0,set.size());
        return set.size();
    }

}
