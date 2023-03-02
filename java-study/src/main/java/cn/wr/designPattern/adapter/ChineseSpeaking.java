package cn.wr.designPattern.adapter;

/**
 * @author : WangRui
 * @date : 2023/2/28
 */

public class ChineseSpeaking extends Language {

    @Override
    public void speakChinese() {

        System.out.println("我是中国人，我会说中文！");
    }
}
