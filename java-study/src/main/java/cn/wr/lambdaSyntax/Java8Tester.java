package cn.wr.lambdaSyntax;

import org.junit.Test;

/**
 * @author RWang
 * @Date 2022/2/23
 */

public class Java8Tester {

    @Test
    public void lambda1(){

        /**
         * 基于 jdk8 lambda 语法 => 实现了 无参数 无返回值的 这个接口
         */
        NoneReturnNoneParameter noneReturnNoneParameter=()->{
            System.out.println("没有参数,没有返回值 的函数式 接口  lambda式编程");
        };
        /**
         *  直接调用即可 返回这个接口的方法
         */
        noneReturnNoneParameter.test();
    }

    @Test
    public void lambda2(){
        /**
         * 基于 jdk8 lambda 语法 => 实现 有一个参数 无返回值的 这个接口
         */
        NoneReturnSingleParameter noneReturnSingleParameter = (int a)->{
            System.out.println("一个参数，没有返回值的 函数式 接口 传入的参数是: a= "+a);
        };
        noneReturnSingleParameter.test(10);
    }
    @Test
    public void lambda3(){
        /**
         * 基于 jdk8 lambda 语法 => 实现 有多个参数 无返回值的 这个接口
         */
        NoneReturnMultipleParameter noneReturnMultipleParameter = (int a,int b)->{
            System.out.println("多个参数，没有返回值的 函数式 接口 传入的参数是: a="+a+" b="+b);
        };
        noneReturnMultipleParameter.test(10,20);
    }

    @Test
    public void lambda4(){
        /**
         *  基于 jdk8 lambda 语法 => 实现 无参数 有返回值的 这个接口
         */
        SingleReturnNoneParameter singleReturnNoneParameter = ()->{
            System.out.println("没有参数,有返回值的 函数式 接口 返回值 为return 的内容");
            return 10;
        };
        singleReturnNoneParameter.test();
    }


    @Test
    public void lambda5(){
        /**
         * 基于 jdk8 lambda 语法 => 实现 有一个参数 有返回值的 这个接口
         */
        SingleReturnSingleParameter singleReturnSingleParameter = (int a)->{
            System.out.println("有一个参数，有返回值的 函数式 接口 入参是 a="+a+" 返回值为return 后的内容");
            return "字符串";
        };
        singleReturnSingleParameter.test(10);
    }

    @Test
    public void lambda6(){
        /**
         * 基于 jdk8 lambda 语法 => 实现 有多个参数 有返回值的 这个接口
         */
        SingleReturnMultipleParameter singleReturnMultipleParameter = (int a,int b)->{
            System.out.println("有一个参数，有返回值的 函数式 接口 入参是 a="+a+" b="+b+" 返回值为return 后的内容");
            return 10;
        };
    }

    public static void main(String[] args) {


    }
}
