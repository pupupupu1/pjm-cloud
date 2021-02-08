package com.pjm.common.util.common;

import java.util.UUID;

public class UuidUtil {
    public static String next(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }
}
