package com.springboot.blog.payload;

import lombok.Data;

@Data
public class JwtAuthResponse {

    private String accessToken;
    private String tokeType = "Bearer";
}
