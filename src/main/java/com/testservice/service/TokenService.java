package com.testservice.service;

import java.util.Base64;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.map.PassiveExpiringMap;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.testservice.domain.User;

/**
 * TokenService is the service for user authenticating and caching authentication tokens.
 * 
 * @author taras
 *
 */
@Component
public class TokenService {

    private static final Logger LOGGER = Logger.getLogger(TokenService.class);

    private static final int timeToLive = 20;
    private final Map<String, User> map = new PassiveExpiringMap<>(timeToLive, TimeUnit.SECONDS);

    @Autowired
    private UserService userService;

    /**
     * Checks availability of user token in cache.
     * 
     * @param token user token should be checked
     * @return boolean value of availability of user token in cache
     */
    public boolean contains(String token) {
        return map.containsKey(token);
    }

    /**
     * Tries to authenticate user uses its token.
     * 
     * @param token identifier of user
     * @return boolean value of attempt of user authentication
     */
    public boolean tryAuthenticate(String token) {
        String decodedToken = null;
        try {
            decodedToken = new String(Base64.getDecoder().decode(token));
            LOGGER.info("(TRY AUTHENTICATE) decoded token: " + decodedToken);
        } catch (IllegalArgumentException e) {
            LOGGER.info("bad token: " + token);
            return false;
        }
        String[] data = decodedToken.split(":");
        String name = data[0];
        String password = data[1];
        User user = userService.load(name, password);
        LOGGER.info("(TRY AUTHENTICATE) user: " + user);
        if (user != null) {
            map.put(token, user);
            return true;
        }
        return false;
    }

    /**
     * Retrieves user principal from cache.
     * 
     * @param token identifier of user
     * @return user principal
     */
    public User get(String token) {
        return map.get(token);
    }
}