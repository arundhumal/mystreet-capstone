package com.mystreet.security;

import com.mystreet.model.User;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtTokenProviderTest {

    private static final String SECRET =
            "test-secret-key-that-is-at-least-64-characters-long-for-hmac-sha512-algorithm-ok";
    private static final long EXPIRATION = 86400000L; // 24h

    private JwtTokenProvider tokenProvider;

    @BeforeEach
    void setUp() throws Exception {
        tokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(tokenProvider, "jwtSecret", SECRET);
        ReflectionTestUtils.setField(tokenProvider, "jwtExpiration", EXPIRATION);
        Method init = JwtTokenProvider.class.getDeclaredMethod("init");
        init.setAccessible(true);
        init.invoke(tokenProvider);
    }

    private User makeUser(String email, boolean isAdmin) {
        User user = new User();
        user.setEmail(email);
        user.setAdmin(isAdmin);
        return user;
    }

    @Test
    void generateToken_regularUser_returnsNonBlankToken() {
        String token = tokenProvider.generateToken(makeUser("user@example.com", false));
        assertThat(token).isNotBlank();
    }

    @Test
    void getAuthentication_validToken_returnsCorrectEmail() {
        User user = makeUser("user@example.com", false);
        String token = tokenProvider.generateToken(user);

        UsernamePasswordAuthenticationToken auth = tokenProvider.getAuthentication(token);

        assertThat(auth.getName()).isEqualTo("user@example.com");
    }

    @Test
    void getAuthentication_regularUser_hasOnlyRoleUser() {
        String token = tokenProvider.generateToken(makeUser("user@example.com", false));

        UsernamePasswordAuthenticationToken auth = tokenProvider.getAuthentication(token);

        assertThat(auth.getAuthorities()).hasSize(1);
        assertThat(auth.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_USER");
    }

    @Test
    void getAuthentication_adminUser_hasBothRoles() {
        String token = tokenProvider.generateToken(makeUser("admin@example.com", true));

        UsernamePasswordAuthenticationToken auth = tokenProvider.getAuthentication(token);

        assertThat(auth.getAuthorities()).hasSize(2);
        assertThat(auth.getAuthorities())
                .extracting("authority")
                .containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
    }

    @Test
    void getAuthentication_invalidToken_throwsJwtException() {
        assertThatThrownBy(() -> tokenProvider.getAuthentication("not.a.valid.token"))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void getAuthentication_expiredToken_throwsJwtException() throws Exception {
        // Build a provider with 1ms expiration so the token is immediately expired
        JwtTokenProvider expiredProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(expiredProvider, "jwtSecret", SECRET);
        ReflectionTestUtils.setField(expiredProvider, "jwtExpiration", 1L);
        Method init = JwtTokenProvider.class.getDeclaredMethod("init");
        init.setAccessible(true);
        init.invoke(expiredProvider);

        String expiredToken = expiredProvider.generateToken(makeUser("user@example.com", false));
        Thread.sleep(5);

        assertThatThrownBy(() -> tokenProvider.getAuthentication(expiredToken))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void generateToken_differentUsers_produceDifferentTokens() {
        String token1 = tokenProvider.generateToken(makeUser("alice@example.com", false));
        String token2 = tokenProvider.generateToken(makeUser("bob@example.com", false));

        assertThat(token1).isNotEqualTo(token2);
    }
}
