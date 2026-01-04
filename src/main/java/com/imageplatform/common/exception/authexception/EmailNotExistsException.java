package com.imageplatform.common.exception.authexception;

public class EmailNotExistsException extends RuntimeException {

    public EmailNotExistsException(String email) {
        super("邮箱：" + email + "不存在");
    }
}
