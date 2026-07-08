package com.javacar.lojadecarro.utils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.time.ZoneId;
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Utils {
    public static final ZoneId ZONE = ZoneId.of("America/Sao_Paulo");
}
