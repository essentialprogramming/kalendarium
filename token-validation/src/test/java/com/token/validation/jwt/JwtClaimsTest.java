package com.token.validation.jwt;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

class JwtClaimsTest {

    @Test
    void testJwtClaims() {
        JwtClaims claims = new JwtClaims();

        claims.setIssuer("issuer");
        Assertions.assertEquals("issuer", claims.getIssuer());

        claims.setSubject("subject");
        Assertions.assertEquals("subject", claims.getSubject());

        claims.setAudience("audience");
        Assertions.assertEquals("audience", claims.getAudience());

        claims.setExpiration(123);
        Assertions.assertEquals(123, claims.getExpiration());

        claims.setNotBefore(123);
        Assertions.assertEquals(123, claims.getNotBefore());

        claims.setIssuedAt(123);
        Assertions.assertEquals(123, claims.getIssuedAt());

        claims.setID("id");
        Assertions.assertEquals("id", claims.getID());

        claims.setScope("scope");
        Assertions.assertEquals("scope", claims.getScope());

        claims.setDomain("domain");
        Assertions.assertEquals("domain", claims.getDomain());

        claims.setValue("testKey", "testValue");
        Assertions.assertEquals("testValue", claims.get("testKey"));

        claims.setValue("testKey", null);
        Assertions.assertNull(claims.get("testKey"));

    }

    @Test
    void testCreateJwtClaims() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("iss", "issuer");
        map.put("iat", 100);

        JwtClaims claims = new JwtClaims(map);

        Assertions.assertEquals("issuer", claims.getIssuer());
        Assertions.assertEquals(100, claims.getIssuedAt());
    }

    @Test
    void testEmptyJwtClaims() {
        JwtClaims claims = new JwtClaims();

        Assertions.assertNull(claims.getIssuer());
        Assertions.assertNull(claims.getSubject());
        Assertions.assertNull(claims.getAudience());
        Assertions.assertEquals(0l, claims.getExpiration());
        Assertions.assertEquals(0l, claims.getNotBefore());
        Assertions.assertEquals(0l, claims.getIssuedAt());
        Assertions.assertNull(claims.getID());
        Assertions.assertNull(claims.getScope());
        Assertions.assertNull(claims.getDomain());
    }
}
