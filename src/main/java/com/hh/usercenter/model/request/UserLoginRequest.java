package com.hh.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 1087031388154855054L;

    private String userAccount;

    private String password;

}
