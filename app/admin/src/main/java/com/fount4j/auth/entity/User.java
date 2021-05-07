package com.fount4j.auth.entity;

import com.fount4j.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@Entity(name = "user")
public class User extends BaseEntity {
    @Id
    private long id;
    /**
     * 登录名
     * <p>login name</p>
     */
    private String username;
    /**
     * 手机号，带国家代码
     * <p>mobile phone number with international code</p>
     */
    @Column(name = "mobile_phone")
    private String mobilePhone;
    /**
     * 电子邮箱
     */
    private String email;
    /**
     * 密码（加 salt 后进行 MD5 加密）
     * <p>MD5 password + salt</p>
     */
    private String password;
    /**
     * MD5 加密用到的 salt
     * <p>Random UUID, used in MD5 encryption</p>
     */
    private String salt;
    /**
     * 界面主题
     * <p>User theme preference</p>
     */
    private String theme;
    /**
     * 国家
     */
    private String country;
    /**
     * 语言
     */
    private String language;
    /**
     * 时区
     */
    private String timezone;
}
