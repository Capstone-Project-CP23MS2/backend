package sit.cp23ms2.sportconnect.tokens;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import sit.cp23ms2.sportconnect.repositories.UserRepository;


import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtil {

    @Value("SportConnectSecretz")
    private String secret;

    @Value("80000000")
    private Long expiration;

    @Autowired
    UserRepository userRepository;


//    private String SECRET_KEY = "secret";

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }


    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }


    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims getClaim(String token){
        Claims claim = extractAllClaims(token);
        return claim;
    }

    public String getEmailFromToken(String token){
        DecodedJWT decodedJWT = JWT.decode(token);
        String email = decodedJWT.getClaim("email").toString();
        email = email.replace("\"", "");
        return email;
    }

    public Date getExpirationFromToken(String token){
        DecodedJWT decodedJWT = JWT.decode(token);
        long secondExp = Long.parseLong(decodedJWT.getClaim("exp").toString());
        Instant expInstant = Instant.ofEpochMilli(secondExp * 1000);
        Date exp = Date.from(expInstant);
        System.out.println(exp);
        return exp;
    }

    private Claims extractAllClaims(String token) {
//        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
//        Claims claims;
//
//        claims = Jwts.parser()
//                .setSigningKey(publicKey)
//                .parseClaimsJws(token)
//                .getBody();
//
//        return claims;
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }


    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean isTokenExpireOrNot(String token) {
//        System.out.println("token exp date: " + getExpirationFromToken(token));
//        System.out.println("actual date: " + new Date());
//        System.out.println("is before: " + getExpirationFromToken(token).before(new Date()));
        return getExpirationFromToken(token).before(new Date());
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        Integer userId = userRepository.findByUsername(userDetails.getUsername()).getUserId();
        claims.put("userId", userId);
        System.out.println(claims);
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {

        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30 )) // 30 Minutes
//                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 )) // 1 Minutes
                .signWith(SignatureAlgorithm.HS256, secret).compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        //final String username = extractUsername(token);
        final String email = getEmailFromToken(token);
//        System.out.println(email);
//        System.out.println(userDetails.getUsername());
       // System.out.println(email.equals(userDetails.getUsername()));
        //System.out.println(!isTokenExpireOrNot(token));
        return (email.equals(userDetails.getUsername()) && !isTokenExpireOrNot(token));
//        try {
//            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
//            return true;
//        } catch (SignatureException e) {
//            e.printStackTrace();
//        } catch (MalformedJwtException e) {
//            e.printStackTrace();
//        } catch (ExpiredJwtException e) {
//            e.printStackTrace();
//        } catch (UnsupportedJwtException e) {
//            e.printStackTrace();
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//        }
//
//        return false;
    }

    public String refreshToken(String token) {
        final Date createdDate = new Date();
        final Date expirationDate = calculateExpirationDate(createdDate);

        final Claims claims = getAllClaimsFromToken(token);
        claims.setIssuedAt(createdDate);
        claims.setExpiration(expirationDate);

        return Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    private Boolean ignoreTokenExpiration(String token) {
        // here you specify tokens, for that the expiration is ignored
        return false;
    }

    public Boolean canTokenBeRefreshed(String token) {
        return (!isTokenExpired(token) || ignoreTokenExpiration(token));
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    private Date calculateExpirationDate(Date createdDate) {
        return new Date(createdDate.getTime() + expiration * 1000);
    }

}
