package com.ucloudlink.css.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

import com.ucloudlink.css.elasticsearch.http.ElasticsearchExtendHttpFactory;
import com.ucloudlink.css.elasticsearch.rest.ElasticsearchExtendHighRestFactory;
import com.ucloudlink.css.elasticsearch.rest.ElasticsearchExtendRestFactory;
import com.ucloudlink.css.elasticsearch.spring.ElasticsearchSpringFactory;
import com.ucloudlink.css.elasticsearch.transport.ElasticsearchExtendTransportFactory;
import com.ucloudlink.css.util.StringUtil;

@Configuration
public class ElasticsearchConfig {
    private Logger logger = LoggerFactory.getLogger(ElasticsearchConfig.class);
    @Value("${elasticsearch.cluster.name}")
    private String clusterName="elasticsearch";
    @Value("${elasticsearch.cluster.servers}")
    private String servers="localhost";
    @Value("${elasticsearch.cluster.username}")
    private String username;
    @Value("${elasticsearch.cluster.password}")
    private String password;
    @Value("${elasticsearch.http.port}")
    private String hport = "9200";
    @Value("${elasticsearch.transport.port}")
    private String tport = "9300";
    
    public String getClusterName() {
		return clusterName;
	}
	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}
	public String getServers() {
		return servers;
	}
	public void setServers(String servers) {
		this.servers = servers;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getHport() {
		return hport;
	}
	public void setHport(String hport) {
		this.hport = hport;
	}
	public String getTport() {
		return tport;
	}
	public void setTport(String tport) {
		this.tport = tport;
	}


	@Bean
    public ElasticsearchExtendHttpFactory httpES() throws Exception {
		logger.info("-----http elasticsearch config init.-------");
		ElasticsearchExtendHttpFactory factory = new ElasticsearchExtendHttpFactory(clusterName, servers, username, password);
		if(!StringUtil.isEmpty(hport))factory = new ElasticsearchExtendHttpFactory(clusterName, servers, username, password,Integer.valueOf(hport));
		factory.init();
		return factory;
    }
	@Bean
	public ElasticsearchExtendRestFactory restES() throws Exception {
		logger.info("-----rest elasticsearch config init.-------");
		ElasticsearchExtendRestFactory factory = new ElasticsearchExtendRestFactory(clusterName, servers, username, password);
		if(!StringUtil.isEmpty(hport))factory = new ElasticsearchExtendRestFactory(clusterName, servers, username, password,Integer.valueOf(hport));
		factory.init();
		return factory;
	}
	@Bean
	public ElasticsearchExtendHighRestFactory highES() throws Exception {
		logger.info("-----high elasticsearch config init.-------");
		ElasticsearchExtendHighRestFactory factory = new ElasticsearchExtendHighRestFactory(clusterName, servers, username, password);
		if(!StringUtil.isEmpty(hport))factory = new ElasticsearchExtendHighRestFactory(clusterName, servers, username, password,Integer.valueOf(hport));
		factory.init();
		return factory;
	}
	@Bean
	public ElasticsearchExtendTransportFactory transportES() throws Exception {
		logger.info("-----transport elasticsearch config init.-------");
		ElasticsearchExtendTransportFactory factory = new ElasticsearchExtendTransportFactory(clusterName, servers, username, password);
		if(!StringUtil.isEmpty(tport))factory = new ElasticsearchExtendTransportFactory(clusterName, servers, username, password,Integer.valueOf(tport));
		factory.init();
		return factory;
	}
	@Bean
	public ElasticsearchTemplate springES() throws Exception {
		logger.info("-----spring elasticsearch config init.-------");
		ElasticsearchSpringFactory factory = new ElasticsearchSpringFactory();
		factory.setClusterName(clusterName);
		factory.setServers(servers);
		factory.setUsername(username);
		factory.setPassword(password);
		if(!StringUtil.isEmpty(tport))factory.setPort(Integer.valueOf(tport));
		factory.init();
		return factory.getTemplate();
	}

}