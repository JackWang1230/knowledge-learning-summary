package cn.wr.reflection;

import lombok.Data;

/**
 * @author RWang
 * @Date 2022/6/7
 */

@Data
public class Son extends Father {

    private String hobby;

    public String girlFriend;


    public void getGirlFriendV1(){
        System.out.println("have enough money");
    }


}
