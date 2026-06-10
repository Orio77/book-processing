package com.orio.auth.dtos;

import java.time.Instant;

public record LoginResponse(Long userId, String email, String tokenType, String accessToken, Instant expiresAt) {

}
