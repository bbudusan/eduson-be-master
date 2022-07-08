package com.servustech.eduson.features.files;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileUploadResponseDto {
  private String imageUrl;
  private boolean status;
  private String originalName;
  private String generatedName;
  private String msg;
}
