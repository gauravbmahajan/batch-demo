package com.example.batchdemo.config;

import com.example.batchdemo.listener.JobCompletionNotificationListener;
import com.example.batchdemo.model.AddressDTO;
import com.example.batchdemo.processor.AddressItemProcessor;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
public class BatchConfig {

  @Bean
  public FlatFileItemReader<AddressDTO> reader() {
    log.info("reader called");
    return new FlatFileItemReaderBuilder<AddressDTO>()
        .name("addressItemReader")
        .resource(new ClassPathResource("addresses.csv"))
        .delimited()
        .names(new String[]{"locationId", "address1", "address2", "city", "state",
            "zipCode", "country"})
        .fieldSetMapper(new BeanWrapperFieldSetMapper<AddressDTO>() {{
          setTargetType(AddressDTO.class);
        }})
        .build();
  }

  @Bean
  public AddressItemProcessor addressItemProcessor() {
    return new AddressItemProcessor();
  }

  @Bean
  public JdbcBatchItemWriter<AddressDTO> writer(DataSource dataSource) {

    return new JdbcBatchItemWriterBuilder<AddressDTO>()
        .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
        .sql("INSERT INTO address (location_id,address1,address2,city,state,zip_code,country)"
            + " VALUES (:locationId,:address1,:address2,:city,:state,:zipCode,:country)")
        .dataSource(dataSource)
        .build();
  }

  @Bean
  public Job importAddressJob(JobRepository jobRepository,
      JobCompletionNotificationListener listener,
      Step step1) {
    return new JobBuilder("importAddressJob", jobRepository)
        .incrementer(new RunIdIncrementer())
        .listener(listener)
        .flow(step1)
        .end()
        .build();
  }

  @Bean
  public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager,
      JdbcBatchItemWriter<AddressDTO> writer) {
    return new StepBuilder("step1", jobRepository)
        .<AddressDTO, AddressDTO>chunk(10, transactionManager)
        .reader(reader())
        .processor(addressItemProcessor())
        .writer(writer)
        .build();
  }

}
