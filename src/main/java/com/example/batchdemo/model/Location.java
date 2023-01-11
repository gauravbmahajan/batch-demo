package com.example.batchdemo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.io.Serializable;
import lombok.Data;

@Data
@Entity
@Table(name = "location")
public class Location implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "location_id")
  private Long locationId;

  @OneToOne(mappedBy = "location")
  @JsonIgnore
  @Transient
  private Address address;

  @Column(name = "accessibility")
  private String accessibility;

  @Column(name = "adminEmails")
  private String adminEmails;

  @Column(name = "alternateName")
  private String alternateName;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Column(name = "email")
  private String email;

  @Column(name = "languages")
  private String languages;

  @Column(name = "latitude")
  private String latitude;

  @Column(name = "longitude")
  private String longitude;

  @Column(name = "name")
  private String name;

  @Column(name = "shortDesc")
  private String shortDesc;

  @Column(name = "transportation")
  private String transportation;

  @Column(name = "website")
  private String website;

  @Column(name = "is_virtual")
  private boolean isVirtual;
}
