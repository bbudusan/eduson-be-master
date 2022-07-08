package com.servustech.eduson.security.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserRequest {
    @NotBlank
    @Size(max = 140)
    private String question;

    @NotNull
    @Size(min = 2, max = 6)
    private String answer;

}
