package cn.wr.lambdaSyntax;

import java.util.function.Function;

public interface UserTest {

    String getUser(Function<User,User> getUser,
                 Function<User,String> getUsers);
}
