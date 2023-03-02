package cn.wr.designPattern.adapter;

/**
 * @author : WangRui
 * @date : 2023/2/28
 */

public class AdapterMode {

    public static void main(String[] args) {

        //适配器模式
        LanguageInterface chinese = new ChineseSpeaking();
        chinese.speakChinese();
    }
}
