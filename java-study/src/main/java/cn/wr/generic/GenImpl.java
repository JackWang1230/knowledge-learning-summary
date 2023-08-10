package cn.wr.generic;

/**
 * @author : WangRui
 * @date : 2023/8/10
 */

public class GenImpl implements Gen<String>{

    private String content;


    @Override
    public String getGenInfo() {
        return content;
    }

    @Override
    public void addGenInfo(String content) {
            this.content = content;
    }
}
