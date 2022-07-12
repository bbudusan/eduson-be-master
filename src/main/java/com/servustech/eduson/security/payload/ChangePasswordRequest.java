package com.servustech.eduson.security.payload;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Builder
@AllArgsConstructor
public class ChangePasswordRequest {


    @Size(min = 6, max = 20)
    @NotBlank
    private final String oldPassword;

    @Size(min = 6, max = 20)
    @NotBlank
    private final String newPassword;

}
