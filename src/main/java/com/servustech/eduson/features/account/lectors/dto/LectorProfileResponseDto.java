package com.servustech.eduson.features.account.lectors.dto;

import com.servustech.eduson.features.files.File;
import com.servustech.eduson.features.account.User;
import com.servustech.eduson.features.account.lectorTitle.LectorTitle;
import com.servustech.eduson.features.account.lectors.Lector;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class LectorProfileResponseDto {

    private String nameAndTitle;
    private String description;
    private File profileImage;
    private Long id;
    private Long userId;

}
