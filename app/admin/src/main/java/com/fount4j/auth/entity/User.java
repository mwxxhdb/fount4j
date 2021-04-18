package com.fount4j.auth.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class User {
    private String id;
    private String mobilePhone;
    private String email;
    private String password;
    private String theme;
    private String language;
    private List<String> roles;
    private List<String> permissions;
    private List<ThirdPartyAuthentication> thirdPartyAuthentications;
}
