package com.github.hbothra.DataTranfer.batch;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import com.github.hbothra.DataTranfer.dto.DTO;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private Map<String, DTO> dtoClasses;

	@Value(value = "${source.sql}")
	private String sourceSQL;

	@Value(value = "${target.sql}")
	private String targetSQL;

	@Value(value = "${dto}")
	private String dto;

	@Bean
	@Primary
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSourceProperties dataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean
	@ConfigurationProperties(prefix = "spring.datasource")
	public HikariDataSource dataSource(DataSourceProperties properties) {
		return properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
	}

	@Bean(name = "sourceDataSource")
	@ConfigurationProperties(prefix = "db1.datasource")
	public DataSource sourceDataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean(name = "targetDataSource")
	@ConfigurationProperties("db2.datasource")
	public DataSource targetDataSource() {
		return DataSourceBuilder.create().build();
	}

	public Class<? extends DTO> getImplClass() {
		return dtoClasses.get(dto).getClass();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Bean
	public ItemReader<Map> itemReader(@Qualifier("sourceDataSource") DataSource sourceDataSource) {
		return new JdbcCursorItemReaderBuilder<Map>().name("cursorItemReader").dataSource(sourceDataSource)
				.sql(sourceSQL).rowMapper(new BeanPropertyRowMapper(getImplClass())).build();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
	public JdbcBatchItemWriter writer(@Qualifier("targetDataSource") DataSource targetDataSource) {
		return new JdbcBatchItemWriterBuilder()
				.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider()).sql(targetSQL)
				.dataSource(targetDataSource).build();
	}

	@Bean
	public Job importUserJob(Step step1) {
		return jobBuilderFactory.get("importUserJob").incrementer(new RunIdIncrementer())
//	      .listener(listener)
				.flow(step1).end().build();
	}

	@Bean
	public Step step1(JdbcBatchItemWriter writer) {
		return stepBuilderFactory.get("step1").chunk(10).reader(itemReader(sourceDataSource()))
//	      .processor(processor())
				.writer(writer).build();
	}

}
