package cn.wr.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 基于反射调用获取类的属性
 * @author RWang
 * @Date 2022/6/7
 */

public class HowToReflect {


    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException {

        Son son = new Son();

        /** getFields 方法只能获取子类及其父类的 public属性*/
        Field[] fields = son.getClass().getFields();

        /** 基于子类获取父类的 public 属性 需要先获取父类 getSuperclass ，再调用 getFields*/
        Field[] fields1 = son.getClass().getSuperclass().getFields();

        /** 基于子类获取父类的所有属性 需要先 获取父类 getSuperclass ，之后再调用 getDeclaredFields */
        Field[] declaredFields1 = son.getClass().getSuperclass().getDeclaredFields();

        /** 获取子类的所有属性 getDeclaredFields */
        Field[] declaredFields = son.getClass().getDeclaredFields();

        /**  获取子类的所有方法 */
        Method[] declaredMethods = son.getClass().getDeclaredMethods();

        /** 获取父类的所有方法*/
        Method[] declaredMethods1 = son.getClass().getSuperclass().getDeclaredMethods();

        /** 基于反射 调用类中的方法*/
        for (Method declaredMethod : declaredMethods) {

            if (declaredMethod.getName().equals("getGirlFriendV1"))
            declaredMethod.invoke(son);
        }

        for (Method method : declaredMethods1) {
//            System.out.println("=========>>>>");
//            System.out.println(method.getName());
//            System.out.println("=========>>>>");
            if (method.getName().equals("getSon"))
                method.invoke(son);
            if (method.getName().equals("getGirlFriendV1")){
                method.invoke(son);
                System.out.println("可以获取子类");
            }



        }

        System.out.println("dd");
    }
}
