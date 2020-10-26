package com.crypto;


public interface PasswordEncoder {

    String encode(CharSequence password);

    boolean matches(CharSequence var1, String var2);

}