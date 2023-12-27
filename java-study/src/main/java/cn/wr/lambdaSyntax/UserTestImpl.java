package cn.wr.lambdaSyntax;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 匿名表达式 新的认知
 * @author : WangRui
 * @date : 2023/11/16
 */

public class UserTestImpl implements UserTest{

    /**
     *  andThen 的作用 第一个接口 getUser执行完成后返回值 作为 第二个接口 getUsers的入参
     *  是串行执行的
     *
     *  apply 的作用 表示 第一个接口 getUser需要的入参值
     * @param getUser
     * @param getUsers
     * @return
     */
    @Override
    public String getUser(Function<User, User> getUser, Function<User, String> getUsers) {
        return getUser.andThen(getUsers).apply(new User());
    }


    public String getPassword(User user){
        return user.getUsername();
    }

    public static void main(String[] args) {

        UserTestImpl userTest = new UserTestImpl();

        String user = userTest.getUser(t -> t,
                userTest::getPassword);

        ArrayList<User> users = new ArrayList<>();
        User user1 = new User();
        user1.setUsername("1");
        user1.setPassword("11");

        User user2 = new User();
        user2.setUsername("2");
        user2.setPassword("22");

        users.add(user1);
        users.add(user2);

        Map<String, String> collect = users.stream().collect(Collectors.toMap(User::getUsername, User::getPassword));

        System.out.println("s");

    }
}
