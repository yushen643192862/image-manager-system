package com.imageplatform.common.exception.authexception;

public class UsernameAlreadyExistsException extends RuntimeException {

    public UsernameAlreadyExistsException(String username) {
        super("用户名：" + username + "已存在");
    }
}
