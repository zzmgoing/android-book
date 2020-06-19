package code.algorithm;

public class Test {

    public static void main(String[] args) {
        int num =  reverse(1534236469);
        System.out.println("结果="+num);
    }

    public static int reverse(int x) {
        boolean isNegative = false;
        if (x < 0) {
            isNegative = true;
            x = Math.abs(x);
        }
        long max = (long) Math.pow(2, 31);
        System.out.println("范围="+max);
        if (x > max) {
            return 0;
        }
        String numStr = String.valueOf(x);
        int result = 0;
        for (int i = numStr.length() - 1; i >= 0; i--) {
            int c = Integer.parseInt(String.valueOf(numStr.charAt(i)));
            if(c == 0 && result == 0){
                continue;
            }
            System.out.println("获取到c="+c+"   i="+i);
            int mu = (int)Math.pow(10,i);
            System.out.println("获取到单位数="+mu);
            result = result + c * mu;
            System.out.println("==="+result);
        }
        return isNegative ? -result : result;
    }

}
