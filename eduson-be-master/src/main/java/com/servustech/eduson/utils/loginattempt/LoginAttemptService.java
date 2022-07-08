package com.servustech.eduson.utils.loginattempt;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.concurrent.ExecutionException;

import static java.util.concurrent.TimeUnit.MINUTES;

@Service
public class LoginAttemptService {
    private static final int MAXIMUM_NUMBER_OF_ATTEMPTS = 5;
    private static final int ATTEMPT_INCREMENT = 1;
    private final LoadingCache<String, LoginAttempt> loginAttemptCache;

    public LoginAttemptService() {
        super();
        loginAttemptCache = CacheBuilder.newBuilder().expireAfterWrite(15, MINUTES)
                .maximumSize(100).build(new CacheLoader<>() {
                    public LoginAttempt load(String key) {
                        return new LoginAttempt(0, ZonedDateTime.now());
                    }
                });
    }

    public void evictUserFromLoginAttemptCache(String username) {
        loginAttemptCache.invalidate(username);
    }

    public void addUserToLoginAttemptCache(String username) {
        LoginAttempt loginAttempt = new LoginAttempt(0, ZonedDateTime.now());
        try {
            loginAttempt = loginAttemptCache.get(username);
            loginAttempt.setNr(ATTEMPT_INCREMENT + loginAttempt.getNr());
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        loginAttemptCache.put(username, loginAttempt);
    }

    public boolean hasExceededMaxAttempts(String username) {
        try {
            return loginAttemptCache.get(username).getNr() >= MAXIMUM_NUMBER_OF_ATTEMPTS;
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

}
