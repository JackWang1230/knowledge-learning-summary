package cn.wr;

import java.util.ArrayList;

/**
 * @author RWang
 * @Date 2022/3/7
 */

public class TestData {

    public static void main(String[] args) {

        ArrayList<String> last = new ArrayList<>();
        ArrayList<String> current = new ArrayList<>();

        last.add("ad");
        last.add("ad");
        current.add("ad");

        last.removeAll(current);
        int size = last.size();
        System.out.println(size);

    }
}
