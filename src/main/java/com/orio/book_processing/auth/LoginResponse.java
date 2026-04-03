package com.orio.book_processing.auth;

import java.time.Instant;

public record LoginResponse(Long userId, String email, String tokenType, String accessToken, Instant expiresAt) {

}
