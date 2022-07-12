package com.servustech.eduson.utils.loginattempt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginAttempt {
    private int nr;
    private ZonedDateTime localDateTime;
}
