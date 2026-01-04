package com.imageplatform.common.exception.authexception;

public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String email) {
            super("邮箱：" + email + "已存在");
    }
}
