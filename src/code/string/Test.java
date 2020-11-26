package code.string;

public class Test {

    public static void main(String[] args){
        System.out.println("无重复字符的最长子串:"+lengthOfLongestSubstring("abcdaf"));
        System.out.println("罗马数字转整数:"+romanToInt("III"));
    }

    /**
     * 无重复字符的最长子串
     * @param s
     * @return
     */
    public static int lengthOfLongestSubstring(String s) {
        if(s == null || s.equals("")){
            return 0;
        }
        String str = "";
        StringBuilder sb = new StringBuilder();
        char[] arr = s.toCharArray();
        for(int i = 0; i < arr.length; i++){
            String last = sb.toString();
            int index = last.indexOf(arr[i]);
            if(i > 0 && index != -1){
                if(last.length() > str.length()){
                    str = last;
                }
                if(index == 0){
                    sb.deleteCharAt(index);
                }else if(index == sb.length() - 1){
                    sb.delete(0,sb.length());
                }else{
                    sb.delete(0,index + 1);
                }
            }
            sb.append(arr[i]);
        }
        return Math.max(sb.length(), str.length());
    }

    /**
     * 罗马数字转整数
     * @param s
     * @return
     */
    public static int romanToInt(String s) {
        char[] arr = s.toCharArray();
        int preNum = changeValue(arr[0]),sum = 0;
        for(int i=1; i<arr.length; i++){
            int num = changeValue(arr[i]);
            if(preNum < num){
                sum -= preNum;
            }else{
                sum += preNum;
            }
            preNum = num;
        }
        sum += preNum;
        return sum;
    }

    public static int changeValue(char c){
        switch(c){
            case 'I': return 1;
            case 'V': return 5;
            case 'X': return 10;
            case 'L': return 50;
            case 'C': return 100;
            case 'D': return 500;
            case 'M': return 1000;
            default: return 0;
        }
    }
}
