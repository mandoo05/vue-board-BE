package kr.co.board.security.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import kr.co.board.domain.member.infra.entity.Member;
import kr.co.board.security.auth.MemberDetails;
import kr.co.board.security.auth.MemberRole;
import kr.co.board.security.auth.MemberStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtProvider {
  @Value("${jwt.secret}")
  private String secretKey;

  @Value("${jwt.refresh}")
  private String refreshKey;

  public final int JWT_EXPIRATION_MILLI_TIME = 60 * 60 * 1000;
  public final int REFRESH_EXPIRATION_MILLI_TIME = 15 * 24 * 60 * 60 * 1000;

  public static final String TOKEN_PREFIX_JWT = "Bearer ";
  public static final String REFRESH_HEADER_STRING = "Refresh";
  public static final String JWT_HEADER_STRING = "Authorization";

  public SecretKey getJwtKey() {
    return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
  }

  public SecretKey getRefreshKey() {
    return Keys.hmacShaKeyFor(refreshKey.getBytes(StandardCharsets.UTF_8));
  }

  public String createToken(
      MemberDetails memberDetails, int validityInMilliseconds, SecretKey key) {
    Claims claims = Jwts.claims().setSubject(String.valueOf(memberDetails.getId()));
    claims.put("userId", memberDetails.getId());
    claims.put("username", memberDetails.getUsername());
    claims.put("nickname", memberDetails.getNickname());
    claims.put(
        "status",
        memberDetails.getStatus() != null
            ? memberDetails.getStatus().name()
            : MemberStatus.ACTIVE.name());
    claims.put("v", memberDetails.isAdmin());

    Date now = new Date();
    Date validity = new Date(now.getTime() + validityInMilliseconds);

    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(validity)
        .signWith(key)
        .compact();
  }

  public MemberDetails parseToken(String token, SecretKey key) {
    Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();

    UUID id = UUID.fromString(claims.getSubject());
    String status = claims.get("status", String.class);

    Member member =
        Member.builder()
            .id(id)
            .username(claims.get("username", String.class))
            .nickname(claims.get("nickname", String.class))
            .role(
                Boolean.TRUE.equals(claims.get("v")) ? MemberRole.ROLE_ADMIN : MemberRole.ROLE_USER)
            .status(status != null ? MemberStatus.valueOf(status) : MemberStatus.ACTIVE)
            .build();

    return new MemberDetails(member);
  }
}
