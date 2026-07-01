package com.JavangularCar.LojadeCarro.exception;

import java.time.LocalDateTime;

public record ResponseError(String message, Integer status, LocalDateTime timestamp, String path) {
}
