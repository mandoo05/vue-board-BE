package kr.co.board.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

  private static final String KEY_PREFIX = "refresh:";

  private final StringRedisTemplate redisTemplate;

  public void save(UUID memberId, String refreshToken, Duration ttl) {
    redisTemplate.opsForValue().set(buildKey(memberId), refreshToken, ttl);
  }

  public boolean validate(UUID memberId, String refreshToken) {
    String stored = redisTemplate.opsForValue().get(buildKey(memberId));
    return stored != null && stored.equals(refreshToken);
  }

  public void delete(UUID memberId) {
    redisTemplate.delete(buildKey(memberId));
  }

  private String buildKey(UUID memberId) {
    return KEY_PREFIX + memberId;
  }
}
