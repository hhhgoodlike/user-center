package com.hh.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 8284153576107391866L;
    
    private String userAccount;

    private String password;

    private String checkPassword;

    private String plantCode;
}
