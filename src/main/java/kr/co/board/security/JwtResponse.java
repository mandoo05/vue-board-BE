package kr.co.board.security;

import kr.co.board.security.config.JwtProvider;
import lombok.Builder;

@Builder
public record JwtResponse(String accessToken) {
  public static JwtResponse from(String accessToken) {
    return JwtResponse.builder().accessToken(JwtProvider.TOKEN_PREFIX_JWT + accessToken).build();
  }
}
