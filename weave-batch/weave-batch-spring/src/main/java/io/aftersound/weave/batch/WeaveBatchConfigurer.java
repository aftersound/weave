package io.aftersound.weave.batch;

import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.repository.ExecutionContextSerializer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;

import javax.sql.DataSource;

public class WeaveBatchConfigurer extends DefaultBatchConfigurer {

    private final DataSource dataSource;
    private final ExecutionContextSerializer executionContextSerializer;

    public WeaveBatchConfigurer(DataSource dataSource, ExecutionContextSerializer executionContextSerializer) {
        super(dataSource);
        this.dataSource = dataSource;
        this.executionContextSerializer = executionContextSerializer;
    }

    @Override
    protected JobRepository createJobRepository() throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(dataSource);
        factory.setSerializer(executionContextSerializer);
        factory.setTransactionManager(getTransactionManager());
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    @Override
    protected JobExplorer createJobExplorer() throws Exception {
        final JobExplorerFactoryBean jobExplorerFactoryBean = new JobExplorerFactoryBean();
        jobExplorerFactoryBean.setDataSource(dataSource);
        jobExplorerFactoryBean.setSerializer(executionContextSerializer);
        jobExplorerFactoryBean.afterPropertiesSet();
        return jobExplorerFactoryBean.getObject();
    }

}
