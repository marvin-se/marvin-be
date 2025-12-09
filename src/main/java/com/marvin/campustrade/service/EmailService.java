package com.marvin.campustrade.service;

import com.marvin.campustrade.data.entity.Token;

public interface EmailService {
    public void sendVerificationEmail(String to, Token token);
    void sendResetEmail(String email, Token token);
}


