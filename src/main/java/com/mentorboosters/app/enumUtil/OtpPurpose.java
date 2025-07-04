package com.mentorboosters.app.enumUtil;

import com.mentorboosters.app.exceptionHandling.InvalidFieldValueException;

public enum OtpPurpose {

    MENTOR_REGISTER,
    MENTEE_REGISTER,
    FORGOT_PASSWORD;

    public static OtpPurpose from(String value){
        return switch (value){
            case "mentor-register" -> MENTOR_REGISTER;
            case "forgot-password" -> FORGOT_PASSWORD;
            case "mentee-register" -> MENTEE_REGISTER;
            default -> throw new InvalidFieldValueException("Purpose should be either mentor-register or mentee-register or forgot-password");
        };
    }

    public boolean isMentorRegister(){
        return this==MENTOR_REGISTER;
    }

    public boolean isMenteeRegister(){
        return this==MENTEE_REGISTER;
    }

    public boolean isForgotPassword(){
        return this==FORGOT_PASSWORD;
    }
}
