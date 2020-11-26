package code.stack;

import java.util.Stack;

public class Test {

    public static void main(String[] args){

        //有效的括号
        System.out.println("有效的括号："+isValid("{[]}"));

    }

    public static boolean isValid(String s) {
        Stack<Character> stack = new Stack<Character>();
        for(char c : s.toCharArray()){
            boolean isLeft = isLeft(c);
            if(isLeft){
                stack.push(c);
            }else{
                if(stack.isEmpty()){
                    return false;
                }
                if(isRight(stack.peek(),c)){
                    stack.pop();
                }else{
                    return false;
                }
            }
        }
        return stack.isEmpty();
    }


    public static boolean isLeft(char c){
        return c == '(' || c == '{' || c == '[';
    }

    public static boolean isRight(char left,char c){
        switch(left){
            case '(':
                return c == ')';
            case '{':
                return c == '}';
            case '[':
                return c == ']';
        }
        return false;
    }

}
