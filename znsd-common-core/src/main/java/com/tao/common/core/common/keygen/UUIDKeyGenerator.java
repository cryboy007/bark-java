package com.tao.common.core.common.keygen;

import java.util.UUID;


public class UUIDKeyGenerator {

    public String getType() {
        return "UUID";
    }

    public static synchronized String generateKey() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
