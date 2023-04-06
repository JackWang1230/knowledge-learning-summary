package cn.wr.examination;

/**
 * @author : WangRui
 * @date : 2023/3/31
 */

import java.util.Scanner;
import java.util.Random;

public class GuessingGame {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        Random random = new Random();
        int numberToGuess = random.nextInt(100) + 1; // 随机生成1-100的数字
        int guess;
        int numGuesses = 0;
        boolean correctGuess = false;

        System.out.println("猜数字游戏：请猜一个1-100之间的整数");

        while (!correctGuess) {
            System.out.print("你猜的数字是：");
            guess = input.nextInt();
            numGuesses++;

            if (guess == numberToGuess) {
                correctGuess = true;
                System.out.println("恭喜你，猜对了！");
            } else if (guess < numberToGuess) {
                System.out.println("你猜的数字太小了，请重新猜");
            } else {
                System.out.println("你猜的数字太大了，请重新猜");
            }
        }

        System.out.println("你一共猜了 " + numGuesses + " 次");
    }
}
