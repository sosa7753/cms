package commerce.cms.domain.config;

import commerce.cms.domain.domain.common.UserType;
import commerce.cms.domain.domain.common.UserVo;
import commerce.cms.domain.util.Aes256Util;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.util.Date;
import java.util.Objects;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;

public class JwtAuthenticationProvider {
  @Value("${secret.key}")
  private String key;
  private SecretKey secretKey;

  @PostConstruct
  private void createSecretKey() {
    this.secretKey = Keys.hmacShaKeyFor(key.getBytes()); // hmac 알고리즘
  }

  private long tokenValidTime = 1000L * 60 * 60 * 24;

  public String createToken(String userPK, Long id, UserType userType) {
    Date now = new Date();
    return Jwts.builder()
        .id(Aes256Util.encrypt(id.toString()))
        .subject(Aes256Util.encrypt(userPK)) // 여기서는 이메일
        .claim("roles", userType)
        .issuedAt(now)
        .expiration(new Date(now.getTime()+tokenValidTime))
        .signWith(secretKey, SIG.HS256)
        .compact();
  }

  public boolean validateToken(String jwtToken) {
    try {
      Jws<Claims> claimsJws = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(jwtToken);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public UserVo getUserVo(String token) {
    Claims c = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token)
        .getPayload();
    return new UserVo(Long.valueOf(Objects.requireNonNull(Aes256Util.decrypt(c.getId()))), Aes256Util.decrypt(c.getSubject()));
  }
}
