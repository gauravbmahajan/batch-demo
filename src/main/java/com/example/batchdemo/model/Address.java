package com.example.batchdemo.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.Data;

@Data
@Entity
@Table(name = "address")
public class Address implements Serializable {

  @Id
  @Column(name = "address_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long addressId;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "location_id", referencedColumnName = "location_id")
  private Location location;

  @Column(name = "address1")
  private String address1;

  @Column(name = "address2")
  private String address2;

  @Column(name = "city")
  private String city;

  @Column(name = "state")
  private String state;

  @Column(name = "zip_code")
  private String zipCode;

  @Column(name = "country")
  private String country;
}
