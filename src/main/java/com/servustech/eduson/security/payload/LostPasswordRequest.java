package com.servustech.eduson.security.payload;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.Email;

@Getter
@Builder
@AllArgsConstructor
@Data
@NoArgsConstructor
public class LostPasswordRequest {

    @Size(min = 6, max = 20)
    @NotBlank
    private String securityKey;

    @Size(min = 6, max = 20)
    @NotBlank
    private String newPassword;

    @Size(min = 6, max = 40)
    @NotBlank
    @Email
    private String email;
}
