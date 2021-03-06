package com.devzy.share.config;
import com.codahale.metrics.*;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class MetricConfig {

    @Bean
    public MetricRegistry metrics() {
        return new MetricRegistry();
    }

    /**
     * Reporter 数据的展现位置
     *
     * @param metrics
     * @return
     */
    @Bean
    public ConsoleReporter consoleReporter(MetricRegistry metrics) {
        return ConsoleReporter.forRegistry(metrics).convertRatesTo(TimeUnit.SECONDS).convertDurationsTo(TimeUnit.SECONDS).build();
    }

    @Bean
    public Slf4jReporter slf4jReporter(MetricRegistry metrics) {
        return Slf4jReporter.forRegistry(metrics).outputTo(LoggerFactory.getLogger(MetricConfig.class)).convertRatesTo(TimeUnit.SECONDS).convertDurationsTo(TimeUnit.SECONDS).build();
    }

    @Bean
    public JmxReporter jmxReporter(MetricRegistry metrics) {
        return JmxReporter.forRegistry(metrics).build();
    }

    /**
     * TPS 计算器
     *
     * @param metrics
     * @return
     */
    @Bean
    public Meter meter(MetricRegistry metrics) {
        return metrics.meter("Metric-TPS");
    }
    /**
     * 直方图
     *
     * @param metrics
     * @return
     */
    @Bean
    public Histogram histogram(MetricRegistry metrics) {
        return metrics.histogram("Metric-Histogram");
    }
    /**
     * 计时器
     *
     * @param metrics
     * @return
     */
    @Bean
    public Timer timer(MetricRegistry metrics) {
        return metrics.timer("Metric-Execute-Time");
    }
}