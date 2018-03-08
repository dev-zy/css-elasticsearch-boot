package com.ucloudlink.css.elasticsearch;

import java.util.Date;

import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSONObject;
import com.ucloudlink.css.Application;
import com.ucloudlink.css.elasticsearch.http.ElasticsearchExtendHttpFactory;
import com.ucloudlink.css.elasticsearch.rest.ElasticsearchExtendRestFactory;
import com.ucloudlink.css.elasticsearch.transport.ElasticsearchExtendTransportFactory;
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
//	@Autowired
//	private ElasticsearchExtendHighRestFactory hrfactory;
	@Test
	public void test(){
		JSONObject json = new JSONObject();
		json.fluentPut("id", 1).fluentPut("name", "user").fluentPut("price", 2.36).fluentPut("flag", true);
		json.fluentPut("datetime", new Date()).fluentPut("array", new String[]{"11","22"}).fluentPut("chara", 'a').fluentPut("obj", new Object());
		hfactory.insert("test", "json", json.toJSONString());
	}
	@Test
	public void test1() throws Exception{
		String analyze = "{\"settings\":{\"analysis\":{\"filter\":{\"default_stopwords\":{\"type\":\"stop\",\"stopwords\":[\"a\",\"an\",\"and\",\"are\",\"as\",\"at\",\"be\",\"but\",\"by\",\"for\",\"if\",\"in\",\"into\",\"is\",\"it\",\"no\",\"not\",\"of\",\"on\",\"or\",\"such\",\"that\",\"the\",\"their\",\"then\",\"there\",\"these\",\"they\",\"this\",\"to\",\"was\",\"will\",\"with\"]}},\"char_filter\":{\"symbol_transform\":{\"mappings\":[\"&=> and \",\"||=> or \"],\"type\":\"mapping\"}},\"analyzer\":{\"es_analyzer\":{\"filter\":[\"lowercase\",\"default_stopwords\"],\"char_filter\":[\"html_strip\",\"symbol_transform\"],\"type\":\"custom\",\"tokenizer\":\"standard\"}}}}}";
		Settings settings = Settings.builder().loadFromSource(analyze, XContentType.JSON).build();
//		AnalysisRegistry registry = new AnalysisModule(new Environment(settings), Collections.singletonList(new AnalysisPlugin() {
//			 	@Override
//	            public Map<String, AnalysisProvider<TokenFilterFactory>> getTokenFilters() {
//	                return AnalysisPlugin.super.getTokenFilters();
//	            }
//			 	@Override
//			 	public Map<String, AnalysisProvider<TokenizerFactory>> getTokenizers() {
//			 		return AnalysisPlugin.super.getTokenizers();
//			 	}
//	            @Override
//	            public Map<String, AnalysisProvider<CharFilterFactory>> getCharFilters() {
//	                return AnalysisPlugin.super.getCharFilters();
//	            }
//		} )).getAnalysisRegistry();
//		IndexSettings idxSettings = new IndexSettings(IndexMetaData.builder("test").build(), settings);
//		IndexAnalyzers indexAnalyzers = registry.build(idxSettings);
		CreateIndexResponse response = tfactory.getClient().admin().indices().prepareCreate("thr2").setSettings(settings).get();
		System.out.println(response.isAcknowledged());
	}
}
