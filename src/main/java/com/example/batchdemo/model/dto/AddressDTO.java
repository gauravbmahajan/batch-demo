package com.example.batchdemo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressDTO {

  private Long locationId;
  private String address1;
  private String address2;
  private String city;
  private String state;
  private String zipCode;
  private String country;
}
