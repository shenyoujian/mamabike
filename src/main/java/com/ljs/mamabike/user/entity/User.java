package com.ljs.mamabike.user.entity;

import lombok.Data;

@Data
public class User {
    private Long id;

    private String nickname;

    private String mobile;

    private String headImg;

    private Byte verifyFlag;

    private Byte enableFlag;
}