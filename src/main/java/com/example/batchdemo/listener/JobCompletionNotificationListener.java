package com.example.batchdemo.listener;

import com.example.batchdemo.model.dto.AddressDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

  private final JdbcTemplate jdbcTemplate;

  @Override
  public void afterJob(JobExecution jobExecution) {
    if (BatchStatus.COMPLETED.equals(jobExecution.getStatus())) {
      log.info("Job finished! ");

      jdbcTemplate.query(
          "SELECT location_id,address1,address2,city,state,zip_code,country from address",
          (rs, row) -> new AddressDTO(rs.getLong(1), rs.getString(2), rs.getString(3)
              , rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7))
      ).forEach(address -> log.info("Found <" + address + "> in the database."));
    }
  }
}
