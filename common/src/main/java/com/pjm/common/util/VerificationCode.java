package com.pjm.common.util;

public class VerificationCode {
    public static String getVC(int length){
        StringBuilder sb=new StringBuilder();
        for (int i=0;i<length;i++){
            sb.append((int)(1+Math.random()*(10-1+1)));
        }
        return sb.toString();
    }
}
