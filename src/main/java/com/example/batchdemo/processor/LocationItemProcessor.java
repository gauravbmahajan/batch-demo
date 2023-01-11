package com.example.batchdemo.processor;

import com.example.batchdemo.model.dto.LocationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class LocationItemProcessor implements ItemProcessor<LocationDTO, LocationDTO> {

  @Override
  public LocationDTO process(LocationDTO locationDTO) throws Exception {
    return locationDTO;
  }
}
