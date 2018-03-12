package com.ucloudlink.css.elasticsearch.spring;

import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.elasticsearch.client.TransportClientFactoryBean;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.IndexQuery;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.ucloudlink.css.util.StringUtil;

public class ElasticsearchSpringFactory {
	private static Logger logger = LogManager.getLogger();
	protected ElasticsearchTemplate template;
	private String clusterName;
	private String servers;
	private String username;
	private String password;
	private int port=9300;
	
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

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * 描述: Elasticsearch服务初始化
	 * 时间: 2017年11月14日 上午10:55:02
	 * @author yi.zhang
	 */
	public void init(){
		try {
			TransportClientFactoryBean client = new TransportClientFactoryBean();
			client.setClusterName(clusterName);
			String clusterNodes = "";
			for(String server : servers.split(",")){
				String[] address = server.split(":");
				String ip = address[0];
				int port=this.port;
				if(address.length>1){
					port = Integer.valueOf(address[1]);
				}
				if(StringUtil.isEmpty(clusterNodes)){
					clusterNodes = ip+":"+port;
				}else{
					clusterNodes +=","+ ip+":"+port;
				}
			}
			client.setClusterNodes(clusterNodes);
			client.setClientIgnoreClusterName(true);
			client.setClientTransportSniff(true);
			if(!StringUtil.isEmpty(username)&&!StringUtil.isEmpty(password)){
				Properties properties = new Properties();
				properties.put("xpack.security.user",username+":"+password);
				client.setProperties(properties);
			}
			client.afterPropertiesSet();
			template = new ElasticsearchTemplate(client.getObject());
		} catch (Exception e) {
			logger.error("-----Elasticsearch Config init Error-----", e);
		}
	}
	public void close(){
		if(template!=null)template.getClient().close();
	}
	
	public ElasticsearchTemplate getTemplate(){
		return template;
	}
	public String insert(String index, String type, Object json) {
		String source = json instanceof String ?json.toString():JSON.toJSONString(json);
		IndexQuery query = new IndexQuery();
		query.setIndexName(index);
		query.setType(type);
		query.setSource(source);
		String result = template.index(query);
		return result;
	}
	public String selectAll(String indices, String types, String condition) {
		if(StringUtil.isEmpty(indices))indices="_all";
		Criteria criteria = new Criteria();
		criteria.expression(condition);
		CriteriaQuery query = new CriteriaQuery(criteria);
		query.addIndices(indices);
		query.addTypes(types);
		String result = JSON.toJSONString(template.queryForList(query, JSONArray.class));
		return result;
	}
}
