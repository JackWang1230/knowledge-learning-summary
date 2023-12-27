package com.wr.module.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wr.module.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.sql.Timestamp;

/**
 * @author : WangRui
 * @date : 2023/12/5
 */

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_user")
public class SysUser extends BaseEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String username;

    private String password;

    private String status;

    private String sex;

    private String userType;


    public static void main(String[] args) {
        SysUser sysUser = new SysUser();
        sysUser.setCreateTime(new Timestamp(System.currentTimeMillis()));
    }

}
