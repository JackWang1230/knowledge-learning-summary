package cn.wr.datastruct.stack;

/**
 * @author : WangRui
 * @date : 2022/11/3
 */

public class ArrayStackDemo {

    public static void main(String[] args) {
        ArrayStack arrayStack = new ArrayStack(4);
        arrayStack.push(1);
    }

}

//
class ArrayStack{
    private int maxSize; // 栈的大小
    private int[] stack; // 数组模拟栈 数据放在该数组中
    private int top = -1 ; // top表示栈顶，初始化为-1


    public ArrayStack(int maxSize) {
        this.maxSize = maxSize;
        stack = new int[this.maxSize];
    }

    /**
     * 栈满
     * @return
     */
    public boolean isFull(){
        return top == maxSize-1;
    }

    /**
     * 栈空
     * @return
     */
    public boolean isEmpty(){
        return top==-1;
    }

    /**
     * 入栈
     * @param value
     */
    public void push(int value){
        if (isFull()) return;
        top++;
        stack[top] = value;
    }

    /**
     * 出栈
     * @return
     */
    public int pop(){

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
    public void list(){
        if (isEmpty()) return;
        for (int i = top; i >=0 ; i--) {
            System.out.println(stack[top]);
        }
    }
}
