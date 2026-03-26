package com.alexander.auth.services;

import com.alexander.auth.dto.LoginRequest;
import com.alexander.auth.dto.TokenResponse;

public interface AuthService {

    TokenResponse autenticar(LoginRequest request) throws Exception;
}
