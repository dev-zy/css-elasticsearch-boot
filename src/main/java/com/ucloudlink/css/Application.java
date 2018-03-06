package com.ucloudlink.css;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.UUIDs;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.codahale.metrics.Counter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;
import com.ucloudlink.css.elasticsearch.http.ElasticsearchExtendHttpFactory;
import com.ucloudlink.css.elasticsearch.rest.ElasticsearchExtendHighRestFactory;
import com.ucloudlink.css.elasticsearch.rest.ElasticsearchExtendRestFactory;
import com.ucloudlink.css.elasticsearch.transport.ElasticsearchExtendTransportFactory;
import com.ucloudlink.css.util.DateUtil;
import com.ucloudlink.css.util.NumberUtil;

@SpringBootApplication
public class Application implements InitializingBean{
	private static Logger logger = LogManager.getLogger();
	@Autowired
	private Meter meter;
	@Autowired
	private Histogram histogram;
	@Autowired
	private Counter counter;
	@Autowired
	private Timer timer;
	@Autowired
	private ElasticsearchExtendTransportFactory tfactory;
	@Autowired
	private ElasticsearchExtendHttpFactory hfactory;
	@Autowired
	private ElasticsearchExtendRestFactory rfactory;
	@Autowired
	private ElasticsearchExtendHighRestFactory hrfactory;
	@Autowired
//	private ElasticsearchTemplate sfactory;
	/**
	 * 访问方式:0.HTTP[标准HTTP方式],1.Rest[内置HTTP方式],2.HighRest[内置HTTP方式],3.Transport方式[内置接口],4.Spring方式[内置接口]
	 */
	private static int ES_TYPE = 0;
	/**
	 * 线程数
	 */
	private static int ES_THREAD = 1;
	/**
	 * 循环次数
	 */
	private static int ES_LOOP = 1;
	/**
	 * 读写操作
	 */
	private static String ES_READ_WRITE = "w";
	/**
	 * 写入数据大于1KB
	 */
	private static boolean ES_DATA_GT_1KB = false;
	/**
	 * 参数个数
	 */
	private static int ES_PARAM_COUNT = 0;
	/**
	 * 计数器
	 */
	private static AtomicInteger atomic = new AtomicInteger(0);
	
	private void write(){
		logger.info("--Write Loop Count:"+ES_LOOP);
		long start = System.currentTimeMillis();
		for(int i=0;(ES_LOOP<1?true:i<ES_LOOP);i++){
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
			if(ES_DATA_GT_1KB){
				String description = "["+DateUtil.formatDateTimeStr(beginTime)+"]"+"Elasticsearch 是一个分布式可扩展的实时搜索和分析引擎。它能帮助你搜索、分析和浏览数据，而往往大家并没有在某个项目一开始就预料到需要这些功能。Elasticsearch 之所以出现就是为了重新赋予硬盘中看似无用的原始数据新的活力。Elasticsearch 为很多世界流行语言提供良好的、简单的、开箱即用的语言分析器集合：阿拉伯语、亚美尼亚语、巴斯克语、巴西语、保加利亚语、加泰罗尼亚语、中文、捷克语、丹麦、荷兰语、英语、芬兰语、法语、加里西亚语、德语、希腊语、北印度语、匈牙利语、印度尼西亚、爱尔兰语、意大利语、日语、韩国语、库尔德语、挪威语、波斯语、葡萄牙语、罗马尼亚语、俄语、西班牙语、瑞典语、土耳其语和泰语";
				json.put("description", description);
			}
			meter.mark();
			counter.inc();
			histogram.update(i);
			final Timer.Context context = timer.time();
			String result = "";
			try {
				if(ES_TYPE==0)result = hfactory.insert("http", "test", json.toJSONString());
				if(ES_TYPE==1)result = rfactory.insert("rest", "test", json.toJSONString());
				if(ES_TYPE==2)result = hrfactory.insert("high", "test", json.toJSONString());
				if(ES_TYPE==3)result = tfactory.insert("transport", "test", json.toJSONString());
				atomic.incrementAndGet();
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
	            context.stop();
	        }
			long end = System.currentTimeMillis();
			double time = (end - start) / 1000.00;
			if(time>=100){
				System.exit(1);
			}
			double ss = time % 60;
			int mm = Double.valueOf(time / 60).intValue() % 60;
			int hh = Double.valueOf(time / 60 / 60).intValue() % 60;
			logger.info("["+atomic.get()+"]ES Write 耗时:"+(hh>0?hh+"小时":"")+(mm>0?mm+"分钟":"")+ss+"秒-------------"+result);
		}
	}
	private void read(){
		logger.info("--Read Loop Count:"+ES_LOOP);
		long start = System.currentTimeMillis();
		for(int i=0;(ES_LOOP<1?true:i<ES_LOOP);i++){
			String query = "Elasticsearch 语言分析器 中文 西安";
			meter.mark();
			counter.inc();
			histogram.update(i);
			final Timer.Context context = timer.time();
			String result = "";
			try {
				if(ES_TYPE==0)result = tfactory.selectAll("transport", "test", query);
				if(ES_TYPE==1)result = hfactory.selectAll("http", "test", query);
				if(ES_TYPE==2)result = rfactory.selectAll("rest", "test", query);
				if(ES_TYPE==3)result = hrfactory.selectAll("high", "test", query);
				atomic.incrementAndGet();
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
	            context.stop();
	        }
			long end = System.currentTimeMillis();
			double time = (end - start) / 1000.00;
			if(time>=100){
				System.exit(1);
			}
			double ss = time % 60;
			int mm = Double.valueOf(time / 60).intValue() % 60;
			int hh = Double.valueOf(time / 60 / 60).intValue() % 60;
			logger.info("["+atomic.get()+"]ES Read 耗时:"+(hh>0?hh+"小时":"")+(mm>0?mm+"分钟":"")+ss+"秒-------------"+result.length());
		}
	}
	
	public void thread(){
		int cpu = Runtime.getRuntime().availableProcessors();
		for(int i=0;(ES_THREAD<1?true:i<ES_THREAD);i++){
			Thread service = new Thread(new Runnable() {
				@Override
				public void run() {
					if(ES_READ_WRITE.equalsIgnoreCase("r")||ES_READ_WRITE.startsWith("r")){
						read();
					}else{
						write();
					}
				}
			});
			service.setName(cpu+"-Elasticsearch-Thread-"+i);
			service.start();
		}
	}
	public void execute(){
		ExecutorService service = Executors.newSingleThreadExecutor();
		if(ES_THREAD>1){
			service = Executors.newFixedThreadPool(ES_THREAD);
		}else{
			if(ES_THREAD<1){
				service = Executors.newCachedThreadPool();
			}
		}
		for(int i=0;(ES_THREAD<1?true:i<ES_THREAD);i++){
			service.submit(new Runnable() {
				@Override
				public void run() {
					if(ES_READ_WRITE.equalsIgnoreCase("r")||ES_READ_WRITE.startsWith("r")){
						read();
					}else{
						write();
					}
				}
			});
		}
	}
	@Override
	public void afterPropertiesSet() throws Exception {
		logger.info("--{es_loop:"+ES_LOOP+",es_read_write:"+ES_READ_WRITE+",ES_THREAD:"+ES_THREAD+",ES_TYPE:"+ES_TYPE+"[0.HTTP,1.Rest,2.HighRest,3.Transport,4.Spring(unknow)]}--");
		if(ES_PARAM_COUNT>6){
			execute();
		}else{
			thread();
		}
	}
	public static void main(String[] args) {
		if(args!=null&&args.length>0){
			for(int i=0;i<args.length;i++){
				String arg = args[i];
				if(!StringUtils.isEmpty(arg)&&arg.contains("=")){
					String value = arg.substring(arg.indexOf("=")+1);
					if(arg.startsWith("base")){
						System.setProperty("log.path", value);
					}
					if((arg.contains("es.thread")||arg.contains("elasticsearch.thread"))&&NumberUtil.isNumber(value)){
						ES_THREAD = Integer.valueOf(value);
					}
					if(arg.contains("es.opt")||arg.contains("elasticsearch.opt")){
						ES_READ_WRITE = value;
					}
					if((arg.contains("es.type")||arg.contains("elasticsearch.type"))&&NumberUtil.isNumber(value)){
						ES_TYPE = Integer.valueOf(value);
					}
					if((arg.contains("es.loop")||arg.contains("elasticsearch.loop"))&&NumberUtil.isNumber(value)){
						ES_LOOP = Integer.valueOf(value);
					}
					if(arg.contains("es.data.gt1k")||arg.contains("es.data.gt1k")){
						ES_DATA_GT_1KB = Boolean.valueOf(value);
					}
					if(arg.contains("es.")||arg.contains("elasticsearch")){
						ES_PARAM_COUNT++;
					}
				}
			}
		}
		SpringApplication.run(Application.class, args);
	}
}