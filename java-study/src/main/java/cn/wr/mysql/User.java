package cn.wr.mysql;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;


/**
 * @author : WangRui
 * @date : 2023/3/10
 */

@Data
@Getter
@Setter
public class User {

    private String userId;
    private String userName;
    private String gender;
    private int age;
    private String address;
    private String phone;
    private String email;
    private Timestamp createTime;
}
