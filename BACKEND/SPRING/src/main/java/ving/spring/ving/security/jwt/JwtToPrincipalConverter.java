package ving.spring.ving.security.jwt;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.auth0.jwt.interfaces.DecodedJWT;
import ving.spring.ving.security.dto.UserPrincipal;

import java.util.List;

@Component
public class JwtToPrincipalConverter {
    public UserPrincipal convert(DecodedJWT jwt) {
        return UserPrincipal.builder()
                .userId(Integer.valueOf(jwt.getSubject()))
                .email(jwt.getClaim("e").asString())
                .authorities(null)
                .build();
    }

    private List<SimpleGrantedAuthority> extracAuthoritiesFromClaim(DecodedJWT jwt) {
        var claim = jwt.getClaim("a");
        if (claim.isNull() || claim.isMissing())
            return List.of();
        return claim.asList(SimpleGrantedAuthority.class);
    }

}
