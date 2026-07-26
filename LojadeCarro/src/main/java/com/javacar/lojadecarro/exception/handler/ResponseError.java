package com.javacar.lojadecarro.exception.handler;

import java.time.LocalDateTime;

public record ResponseError(String message, Integer status, LocalDateTime timestamp, String path) {
}
