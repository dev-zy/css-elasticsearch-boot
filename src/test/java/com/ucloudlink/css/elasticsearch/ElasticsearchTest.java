package com.ucloudlink.css.elasticsearch;

import java.util.Date;
import java.util.Random;

import org.elasticsearch.common.UUIDs;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSONObject;
import com.ucloudlink.css.Application;
import com.ucloudlink.css.elasticsearch.http.ElasticsearchExtendHttpFactory;
import com.ucloudlink.css.elasticsearch.rest.ElasticsearchExtendHighRestFactory;
import com.ucloudlink.css.elasticsearch.rest.ElasticsearchExtendRestFactory;
import com.ucloudlink.css.elasticsearch.spring.ElasticsearchSpringFactory;
import com.ucloudlink.css.elasticsearch.transport.ElasticsearchExtendTransportFactory;
import com.ucloudlink.css.util.DateUtil;
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
public class ElasticsearchTest {
	@Autowired
	private ElasticsearchExtendTransportFactory tfactory;
	@Autowired
	private ElasticsearchExtendHttpFactory hfactory;
	@Autowired
	private ElasticsearchExtendRestFactory rfactory;
	@Autowired
	private ElasticsearchExtendHighRestFactory hrfactory;
	@Autowired
	private ElasticsearchSpringFactory sfactory;
	/**
	 * 访问方式:0.HTTP[标准HTTP方式],1.Rest[内置HTTP方式],2.HighRest[内置HTTP方式],3.Transport方式[内置接口],4.Spring方式[内置接口]
	 */
	private static int ES_TYPE = 4;
	@Test
	public void test(){
		Date today = new Date();
		Random random = new Random();
		JSONObject json = new JSONObject();
		json.put("id", UUIDs.randomBase64UUID());
		json.put("cellId", random.nextInt(1000));
		json.put("cid", random.nextInt());
		json.put("devicetype", random.nextInt(10));
		json.put("iso2", UUIDs.randomBase64UUID());
		json.put("lac", random.nextInt(10000));
		json.put("latitude", random.nextDouble());
		json.put("longitude", random.nextGaussian());
		json.put("logId", UUIDs.randomBase64UUID(random));
		json.put("mcc", 460);
		json.put("plmn", today.getTime());
		json.put("uid", UUIDs.randomBase64UUID(random));
		json.put("country", "CN[中国]");
		json.put("province", "ShannXi[陕西]");
		json.put("city", "Xi'An[西安]");
		json.put("sessionid", UUIDs.randomBase64UUID());
		json.put("userCode", random.nextInt(1000)+"@ucloudlink.com");
		json.put("imei", 1365222+random.nextInt(1000));
		json.put("imsi", 1365222+random.nextInt(1000));
		Date beginTime = new Date(today.getTime()+random.nextInt());
		json.put("beginTime", beginTime);
		json.put("countDay", today);
		json.put("card", random.nextInt(100));
		json.put("cardDownFlow", random.nextDouble()*1000*1000);
		json.put("cardUpFlow", random.nextDouble()*1000*1000);
		json.put("sys", random.nextDouble()*1000*1000);
		json.put("sysDownFlow", random.nextDouble()*1000*1000);
		json.put("sysUpFlow", random.nextDouble()*1000*1000);
		json.put("userDownFlow", random.nextDouble()*1000*1000);
		json.put("userUpFlow", random.nextDouble()*1000*1000);
		json.put("user", random.nextDouble()*1000*1000);
		String description = "["+DateUtil.formatDateTimeStr(beginTime)+"]"+"Elasticsearch 是一个分布式可扩展的实时搜索和分析引擎。它能帮助你搜索、分析和浏览数据，而往往大家并没有在某个项目一开始就预料到需要这些功能。Elasticsearch 之所以出现就是为了重新赋予硬盘中看似无用的原始数据新的活力。Elasticsearch 为很多世界流行语言提供良好的、简单的、开箱即用的语言分析器集合：阿拉伯语、亚美尼亚语、巴斯克语、巴西语、保加利亚语、加泰罗尼亚语、中文、捷克语、丹麦、荷兰语、英语、芬兰语、法语、加里西亚语、德语、希腊语、北印度语、匈牙利语、印度尼西亚、爱尔兰语、意大利语、日语、韩国语、库尔德语、挪威语、波斯语、葡萄牙语、罗马尼亚语、俄语、西班牙语、瑞典语、土耳其语和泰语";
		json.put("description", description);
		String result = "";
		try {
			if(ES_TYPE==0)result = hfactory.insert("http", "test", json);
			if(ES_TYPE==1)result = rfactory.insert("rest", "test", json);
			if(ES_TYPE==2)result = hrfactory.insert("high", "test", json);
			if(ES_TYPE==3)result = tfactory.insert("transport", "test", json);
			if(ES_TYPE==4)result = sfactory.insert("spring", "test", json);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(ES_TYPE+"(0.HTTP[标准HTTP方式],1.Rest[内置HTTP方式],2.HighRest[内置HTTP方式],3.Transport方式[内置接口],4.Spring方式[内置接口])ES Write -------------"+result);
	}
	@Test
	public void test1() throws Exception{
		String query = "Elasticsearch 语言分析器 中文 西安";
		String result = "";
		try {
			if(ES_TYPE==0)result = hfactory.selectAll("http", "test", query);
			if(ES_TYPE==1)result = rfactory.selectAll("rest", "test", query);
			if(ES_TYPE==2)result = hrfactory.selectAll("high", "test", query);
			if(ES_TYPE==3)result = tfactory.selectAll("transport", "test", query);
			if(ES_TYPE==4)result = sfactory.selectAll("spring", "test", query);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println(ES_TYPE+"(0.HTTP[标准HTTP方式],1.Rest[内置HTTP方式],2.HighRest[内置HTTP方式],3.Transport方式[内置接口],4.Spring方式[内置接口])ES Read -------------"+result);
	}
}
