package com.pjm.nettyservice.socket;


public enum MessageTypeEnum4Pjm {

    SYSTEM("0"),EMPTY("1"), PEOPLE("2"), GROUP("3"), TALK_REQUEST("4"),TALK_RESPONSE("5"),TALK_CLOSE("6");


    private String type;


    MessageTypeEnum4Pjm(String type) {


        this.type = type;

    }


    public String getType() {

        return type;

    }


    public static MessageTypeEnum4Pjm get(String type) {

        for (MessageTypeEnum4Pjm value : values()) {

            if (value.type == type) {

                return value;

            }

        }


        throw new RuntimeException("unsupported type: " + type);

    }

}
