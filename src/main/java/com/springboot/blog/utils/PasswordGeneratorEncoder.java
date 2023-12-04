package com.springboot.blog.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGeneratorEncoder {

    public static void main(String[] args) {
        var passwordEncoder = new BCryptPasswordEncoder();
        System.out.println(passwordEncoder.encode("gustavo1"));
        System.out.println(passwordEncoder.encode("admin1"));
    }
}
