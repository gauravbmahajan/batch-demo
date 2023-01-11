package com.example.batchdemo.repository;

import com.example.batchdemo.model.Address;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

  List<Address> findByZipCode(String zipCode, PageRequest pageRequest);

}
