package com.example.batchdemo.processor;

import com.example.batchdemo.model.dto.AddressDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class AddressItemProcessor implements ItemProcessor<AddressDTO, AddressDTO> {

  @Override
  public AddressDTO process(AddressDTO address) throws Exception {
    log.info("Transforming address {}", address);
    return address;
  }
}
