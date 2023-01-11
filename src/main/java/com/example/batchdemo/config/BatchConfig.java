package com.example.batchdemo.config;

import com.example.batchdemo.listener.JobCompletionNotificationListener;
import com.example.batchdemo.model.Address;
import com.example.batchdemo.model.Location;
import com.example.batchdemo.model.dto.AddressDTO;
import com.example.batchdemo.model.dto.LocationDTO;
import com.example.batchdemo.processor.AddressItemProcessor;
import com.example.batchdemo.processor.LocationItemProcessor;
import com.example.batchdemo.repository.AddressRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.WritableResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
public class BatchConfig {

  @Value("file:xml/output.xml")
  private WritableResource outputXml;

  @Bean
  public FlatFileItemReader<AddressDTO> addressReader() {
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
  public FlatFileItemReader<LocationDTO> locationReader() {
    return new FlatFileItemReaderBuilder<LocationDTO>()
        .name("locationItemReader")
        .resource(new ClassPathResource("locations.csv"))
        .delimited()
        .names(new String[]{"accessibility", "adminEmails", "alternateName", "description", "email",
            "languages", "latitude", "longitude", "name", "shortDesc", "transportation", "website",
            "virtual"})
        .fieldSetMapper(new BeanWrapperFieldSetMapper<LocationDTO>() {{
          setTargetType(LocationDTO.class);
        }})
        .build();
  }

  @Bean
  public RepositoryItemReader<Address> jpaAddressReader(AddressRepository addressRepository) {
    Map<String, Direction> sortOrder = new HashMap<>();
    sortOrder.put("addressId", Direction.ASC);
    return new RepositoryItemReaderBuilder<Address>()
        .name("jdbcAddressReader")
        .repository(addressRepository)
        .methodName("findAll")
        .arguments(new ArrayList<>())
        .sorts(sortOrder)
        .build();
  }

  @Bean
  public AddressItemProcessor addressItemProcessor() {
    return new AddressItemProcessor();
  }

  @Bean
  public ItemProcessor<LocationDTO, LocationDTO> locationItemProcessor() {
    return new LocationItemProcessor();
  }

  @Bean
  public JdbcBatchItemWriter<AddressDTO> AddressWriter(DataSource dataSource) {
    log.info("address writer called");
    return new JdbcBatchItemWriterBuilder<AddressDTO>()
        .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
        .sql("INSERT INTO address (location_id,address1,address2,city,state,zip_code,country)"
            + " VALUES (:locationId,:address1,:address2,:city,:state,:zipCode,:country)")
        .dataSource(dataSource)
        .build();
  }

  @Bean
  public JdbcBatchItemWriter<LocationDTO> locationWriter(DataSource dataSource) {
    log.info("location writer called");
    return new JdbcBatchItemWriterBuilder<LocationDTO>()
        .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
        .sql(
            "INSERT INTO location (accessibility,adminEmails,alternateName,description,email,languages,latitude,longitude,name,shortDesc,transportation,website,is_virtual)"
                + " VALUES (:accessibility,:adminEmails,:alternateName,:description,:email,:languages,:latitude,:longitude,:name,:shortDesc,:transportation,:website,:virtual)")
        .dataSource(dataSource)
        .build();
  }

  @Bean
  public StaxEventItemWriter<Address> xmlWriter() {
    try {
      return new StaxEventItemWriterBuilder<Address>()
          .name("addressXmlWriter")
          .marshaller(marshaller())
          .resource(outputXml)
          .rootTagName("addresses")
          .overwriteOutput(true)
          .build();
    } catch (ClassNotFoundException e) {
      log.error("Class not found exception {}", e);
    }
    return null;
  }

  @Bean
  public XStreamMarshaller marshaller() throws ClassNotFoundException {
    XStreamMarshaller marshaller = new XStreamMarshaller();
    Map<String, Class> aliases = new HashMap<>();
    aliases.put("address", Address.class);
    aliases.put("location", Location.class);
    marshaller.setAliases(aliases);
    return marshaller;
  }

  @Bean
  public Job importAddressJob(JobRepository jobRepository,
      JobCompletionNotificationListener listener,
      Step step1, Step step2, Step step3) {
    Flow flow1 = new FlowBuilder<Flow>("flow1").from(step1).end();
    Flow flow2 = new FlowBuilder<Flow>("flow2").from(step2).end();
    return new JobBuilder("importAddressJob", jobRepository)
        .incrementer(new RunIdIncrementer())
        .listener(listener)
        .start(flow1)
        .split(new SimpleAsyncTaskExecutor()).add(flow2)
        .next(step3)
        .end()
        .build();
  }

  @Bean
  public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager,
      JdbcBatchItemWriter<AddressDTO> writer) {
    return new StepBuilder("step1", jobRepository)
        .<AddressDTO, AddressDTO>chunk(10, transactionManager)
        .reader(addressReader())
//        .processor(addressItemProcessor())
        .writer(writer)
        .build();
  }

  @Bean
  public Step step2(JobRepository jobRepository, PlatformTransactionManager transactionManager,
      JdbcBatchItemWriter<LocationDTO> writer) {
    return new StepBuilder("step2", jobRepository)
        .<LocationDTO, LocationDTO>chunk(10, transactionManager)
        .reader(locationReader())
//        .processor(locationItemProcessor())
        .writer(writer)
        .build();
  }

  @Bean
  public Step step3(JobRepository jobRepository, PlatformTransactionManager transactionManager,
      ItemWriter<Address> writer, AddressRepository addressRepository) {
    return new StepBuilder("step3", jobRepository)
        .<Address, Address>chunk(10, transactionManager)
        .reader(jpaAddressReader(addressRepository))
        .writer(writer)
        .build();
  }
}
