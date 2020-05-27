package code;

import java.util.ArrayList;
import java.util.Collections;

/**
 * 设计一个逻辑表达式计算器
 * <p>
 * 表达式定义：
 * 输入运算符包括 '&','|','!'
 * 运算符优先级 ! > & > |，有括号优先计算括号内表达式
 * 输入计算值为 '1' 表示'true'，'0'表示'false'
 * 输入字符串可能有空格
 * <p>
 * 示例：
 * 输入："1"			    输出：true
 * 输入："1 & 0" 			输出：false
 * 输入："1|0"  			输出：true
 * 输入："1|(1&0)"			输出：true
 * 输入："1|1&0"			输出：true
 * 输入："1&0|0&1"			输出：false
 * 输入："!0&1|0"			输出：true
 * 输入："((!0&1))|0"		输出：true
 * <p>
 * 要求：
 * 编程语言用java，测试用例参考以上示例自己测试。
 * 以下代码只是基础框架，可以自己增加方法
 */

public class LogicOperation {


    public static void main(String[] args) {
        System.out.println(operation("1"));
        System.out.println(operation("1 & 0"));
        System.out.println(operation("1|0"));
        System.out.println(operation("1|(1&0)"));
        System.out.println(operation("1|1&0"));
        System.out.println(operation("1&0|0&1"));
        System.out.println(operation("!0&1|0"));
        String expression = "((!0&1))|0";
        boolean result = operation(expression);
        System.out.println(result);
    }

    /**
     * 表达式计算
     * @param expression
     * @return
     */
    public static boolean operation(String expression) {
        if (expression == null) {
            return false;
        }
        expression = expression.replaceAll(" ","");
        if ("".equals(expression)) {
            return false;
        }
        if (!expression.contains("0") && !expression.contains("1")) {
            return false;
        }
        if (!isCharValid(expression)) {
            return false;
        }
        if (!isOperatorValid(expression)) {
            return false;
        }
        return calculation(expression);
    }

    /**
     * 是否包含无效字符，包含无效字符返回false
     * 左括号和右括号数量不相等返回false
     * @param expression
     * @return
     */
    public static boolean isCharValid(String expression) {
        int leftBrackets = 0;
        int rightBrackets = 0;
        for (int i = 0; i < expression.length(); i++) {
            String value = String.valueOf(expression.charAt(i));
            boolean e1 = "&".equals(value);
            boolean e2 = "|".equals(value);
            boolean e3 = "!".equals(value);
            boolean e4 = "0".equals(value);
            boolean e5 = "1".equals(value);
//            boolean e6 = " ".equals(value);
            boolean e7 = "(".equals(value);
            boolean e8 = ")".equals(value);
            if (e7) {
                leftBrackets++;
            }
            if (e8) {
                rightBrackets++;
            }
            if (!(e1 || e2 || e3 || e4 || e5 || e7 || e8)) {
                return false;
            }
        }
        if (leftBrackets != rightBrackets) {
            return false;
        }
        return true;
    }

    /**
     * 运算符位置是否有效，无效返回false
     * @param expression
     * @return
     */
    public static boolean isOperatorValid(String expression) {
        if (expression.startsWith("&") || expression.startsWith("|")) {
            return false;
        }
        if (expression.endsWith("&") || expression.endsWith("|") || expression.endsWith("!")) {
            return false;
        }
        if (expression.contains("(") && !expression.contains(")")) {
            return false;
        }
        if (!expression.contains("(") && expression.contains(")")) {
            return false;
        }
        if(expression.contains("!!") || expression.contains("!&") || expression.contains("!|") ){
            return false;
        }
        if(expression.contains("&!") || expression.contains("&&") || expression.contains("&|") ){
            return false;
        }
        if(expression.contains("|!") || expression.contains("|&") || expression.contains("||") ){
            return false;
        }
        if(expression.contains("00") || expression.contains("01") || expression.contains("10") || expression.contains("11")){
            return false;
        }
        return true;
    }

    /**
     * 计算表达式
     * @param expression
     * @return
     */
    public static boolean calculation(String expression) {
        if (expression.contains("(") && expression.contains(")")) {
            ArrayList<Integer> leftIndexList = new ArrayList<>();
            ArrayList<Integer> rightIndexList = new ArrayList<>();
            for (int i = 0; i < expression.length(); i++) {
                String value = String.valueOf(expression.charAt(i));
                if ("(".equals(value)) {
                    leftIndexList.add(i);
                }
                if (")".equals(value)) {
                    rightIndexList.add(i);
                }
            }
            if (leftIndexList.size() != rightIndexList.size()) {
                return false;
            }
            Collections.reverse(leftIndexList);
            for (int i = 0; i < leftIndexList.size(); i++) {
                int left = leftIndexList.get(i);
                int right = rightIndexList.get(i);
                String newStr = call(expression, left, right);
                String oldStr = expression.substring(left, right + 1);
                StringBuilder sb = new StringBuilder();
                sb.append(newStr);
                for (int n = 0; n < oldStr.length() - 1; n++) {
                    sb.append(" ");
                }
                expression = expression.replace(oldStr,sb.toString());
            }
        }
        String call = call(expression, -1, expression.length());
        return "1".equals(call);
    }

    /**
     * 计算表达式无括号,true返回1，false返回0
     * @param expression
     * @return
     */
    public static String call(String expression, int left, int right) {
        String sub = expression.substring(left + 1, right);
        sub = sub.replaceAll(" ","");
        if (sub.contains("!")) {
            sub = sub.replaceAll("!0", "1")
                    .replaceAll("!1", "0");
        }
        if (sub.contains("&")) {
            sub = sub.replaceAll("1&0", "0")
                    .replaceAll("1&1", "1")
                    .replaceAll("0&1", "0")
                    .replaceAll("0&0", "0");
        }
        if (sub.contains("|")) {
            sub = sub.replaceAll("0\\|1", "1")
                    .replaceAll("1\\|1", "1")
                    .replaceAll("1\\|0", "1")
                    .replaceAll("0\\|0", "0");
        }
        return sub;
    }


}
