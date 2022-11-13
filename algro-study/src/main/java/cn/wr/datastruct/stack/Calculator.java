package cn.wr.datastruct.stack;

/**
 * @author : WangRui
 * @date : 2022/11/3
 */

public class Calculator {
    public static void main(String[] args) {

        // 根据前面老师思路，完成表达式的运算
        String expression = "3+201*6-2";
        //创建两个栈 一个是数栈 一个是符号栈
        ArrayStack2 numStack = new ArrayStack2(10);
        ArrayStack2 oprStack = new ArrayStack2(10);
        // 定义需要的相关变量
        int index = 0; // 用于扫描
        int num1 = 0;
        int num2 = 0;
        int opr = 0;
        int res = 0;
        String keepNum="";
        char ch = ' '; // 将每次扫描得到的char保存到ch
        // while循环扫描
        while (true) {
            ch = expression.substring(index, index + 1).charAt(0);
            // 判断ch 是什么 然后做相应的操作
            if (oprStack.isOpr(ch)) { // 如果是运算符
                if (!oprStack.isEmpty()) { // 符号栈不为空
                    //1。符号栈的操作符 ch 优先级小于等于 栈顶
                    if (oprStack.priority(ch) <= oprStack.priority(oprStack.peek())) {
                        num1 = numStack.pop();
                        num2 = numStack.pop();
                        opr = oprStack.pop();
                        res = numStack.cal(num1, num2, opr);
                        // 把运算的结果入数栈
                        numStack.push(res);
                        // 将当前的操作符入符号栈
                        oprStack.push(ch);
                    } else {
                        // 如果当前的操作符的优先级大于栈中的操作符，就直接符号栈
                        oprStack.push(ch);
                    }
                } else {
                    // 如果为空 直接入符号栈
                    oprStack.push(ch); // 1+3
                }
            }
            else {
                // 如果是数 直接入栈
                // 因为 asci的字符1 对应的是 数字49 所以需要减48
                // numStack.push(ch - 48);
                // 1。当处理多位数时，不能发现一个数就立即入数栈
                // 2。因此在处理数时，需要向expression的表达式index 后再看一位，如果是数不能立即入栈，只有是运算符再入栈
                // 3。定义字符串变量进行拼接多位数
                keepNum +=ch;

                // 如果ch已经是expression的最后一位，就直接入栈
                if (index == expression.length()-1){
                    numStack.push(Integer.parseInt(keepNum));
                }

                // 注意是看一位 不是index++
                // 判断下一个字符是不是数字，如果是数字，就继续扫描，如果是运算符，则入栈
                else  if (oprStack.isOpr(expression.substring(index+1,index+2).charAt(0))){
                    // 后一位是运算符，直接入栈，不是的话 不会进入该循环 字符串继续拼接
                    numStack.push(Integer.parseInt(keepNum));
                    // 重要的！！！！ keepNum 清空
                    keepNum ="";

                }
            }
            // 让index +1 并判断是否扫描到expression的最后一个值
            index ++;
            if (index == expression.length()){
                break;
            }

        }
        while (true){
            // 如果符号栈为空 则计算到最后的结果，数栈中只有一个值
            if (oprStack.isEmpty()){
                break;
            }
            num1 = numStack.pop();
            num2 = numStack.pop();
            opr = oprStack.pop();
            res =numStack.cal(num1,num2,opr);
            numStack.push(res); // 入栈
        }
        // 输出结果
        System.out.println(res);
    }
}

// 扩展功能
class ArrayStack2 {
    private int maxSize; // 栈的大小
    private int[] stack; // 数组模拟栈 数据放在该数组中
    private int top = -1; // top表示栈顶，初始化为-1


    public ArrayStack2(int maxSize) {
        this.maxSize = maxSize;
        stack = new int[this.maxSize];
    }

    /**
     * 栈满
     *
     * @return
     */
    public boolean isFull() {
        return top == maxSize - 1;
    }

    // 增加一个方法 可以返回当前栈顶的值，但不是真正出栈
    public int peek() {
        return stack[top];
    }

    /**
     * 栈空
     *
     * @return
     */
    public boolean isEmpty() {
        return top == -1;
    }

    /**
     * 入栈
     *
     * @param value
     */
    public void push(int value) {
        if (isFull()) return;
        top++;
        stack[top] = value;
    }

    /**
     * 出栈
     *
     * @return
     */
    public int pop() {

        if (isEmpty()) {
            throw new RuntimeException("栈空");
        }
        // 需要辅助变量 保存栈值
        int value = stack[top];
        top--;
        return value;
    }

    /**
     * 遍历时 需要从栈顶开显示数据
     */
    public void list() {
        if (isEmpty()) return;
        for (int i = top; i >= 0; i--) {
            System.out.println(stack[top]);
        }
    }

    // 返回运算符的优先级，优先级使用数字表示，数字越大，优先级越高
    public int priority(int opr) {

        if (opr == '*' || opr == '/') {
            return 1;
        } else if (opr == '+' || opr == '-') {
            return 0;
        } else {
            return -1;
        }
    }

    /**
     * 判断是不是一个运算符
     *
     * @param val
     * @return
     */
    public boolean isOpr(char val) {

        return val == '+' || val == '-' || val == '*' || val == '/';
    }

    /**
     * 计算数值方法
     *
     * @param num1
     * @param num2
     * @param opr
     * @return
     */
    public int cal(int num1, int num2, int opr) {
        int res = 0; // 用于存放计算的结果
        switch (opr) {
            case '+':
                res = num1 + num2;
                break;
            case '-':
                res = num2 - num1;
                break;
            case '*':
                res = num1 * num2;
                break;
            case '/':
                res = num2 / num1;
                break;
            default:
                break;
        }
        return res;
    }
}
