package com.example.batchdemo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationDTO {

  private String accessibility;
  private String adminEmails;
  private String alternateName;
  private String description;
  private String email;
  private String languages;
  private String latitude;
  private String longitude;
  private String name;
  private String shortDesc;
  private String transportation;
  private String website;
  private boolean virtual;
}
