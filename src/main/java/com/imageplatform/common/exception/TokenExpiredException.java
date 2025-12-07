package com.imageplatform.common.exception;

public class TokenExpiredException extends RuntimeException {
  public TokenExpiredException() {
    super("令牌已过期");
  }
  public TokenExpiredException(String message) {
    super(message);
  }
}
