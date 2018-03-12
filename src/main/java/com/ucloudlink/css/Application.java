package com.ucloudlink.css;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.UUIDs;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Counter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;
import com.ucloudlink.css.elasticsearch.http.ElasticsearchExtendHttpFactory;
import com.ucloudlink.css.elasticsearch.rest.ElasticsearchExtendHighRestFactory;
import com.ucloudlink.css.elasticsearch.rest.ElasticsearchExtendRestFactory;
import com.ucloudlink.css.elasticsearch.spring.ElasticsearchSpringFactory;
import com.ucloudlink.css.elasticsearch.transport.ElasticsearchExtendTransportFactory;
import com.ucloudlink.css.util.DateUtil;
import com.ucloudlink.css.util.StringUtil;

@SpringBootApplication
public class Application implements ApplicationContextAware,InitializingBean{
	private static Logger logger = LogManager.getLogger();
	@Autowired 
	private Environment env; 
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
	private ElasticsearchSpringFactory sfactory;
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
	 * 压测参数
	 */
	private static Map<String,String> map = new HashMap<String,String>();
	/**
	 * 计数器
	 */
	private static AtomicLong atomic = new AtomicLong(0);
	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		ConsoleReporter console = ctx.getBean(ConsoleReporter.class);
		console.start(10, TimeUnit.SECONDS);
	}
	@Override
	public void afterPropertiesSet() throws Exception {
		sys();
		init();
		logger.info("--{es_loop:"+ES_LOOP+",es_read_write:"+ES_READ_WRITE+",ES_THREAD:"+ES_THREAD+",ES_TYPE:"+ES_TYPE+"[0.HTTP,1.Rest,2.HighRest,3.Transport,4.Spring(unknow)]}--");
		Thread.sleep(10*1000);
		if(ES_PARAM_COUNT>6){
			execute();
		}else{
			thread();
		}
	}
	private void sys(){
		Runtime sys = Runtime.getRuntime();
		int core = sys.availableProcessors();
		double free_mem = sys.freeMemory()/(1024*1024*1024);
		double max_mem = sys.maxMemory()/(1024*1024*1024);
		double total_mem = sys.totalMemory()/(1024*1024*1024);
		ES_THREAD = core*2;
		logger.info("--sys_core:"+core+",free_mem:"+free_mem+",max_mem:"+max_mem+",total_mem:"+total_mem+"---------");
	}
	private void init(){
		String es_thread = map.containsKey("es.thread")?map.get("es.thread"):map.get("elasticsearch.thread");
		if(StringUtil.isEmpty(es_thread)){
			es_thread = env.getProperty("es.thread");
			if(StringUtil.isEmpty(es_thread)){
				es_thread = env.getProperty("elasticsearch.thread");
			}
			map.put("es.thread", es_thread);
		}
		ES_THREAD = StringUtil.isEmpty(es_thread)||Integer.valueOf(es_thread)<1?ES_THREAD:Integer.valueOf(es_thread);
		String es_opt = map.containsKey("es.opt")?map.get("es.opt"):map.get("elasticsearch.opt");
		if(StringUtil.isEmpty(es_opt)){
			es_opt = env.getProperty("es.opt");
			if(StringUtil.isEmpty(es_opt)){
				es_opt = env.getProperty("elasticsearch.opt", "w");
			}
			map.put("es.opt", es_opt);
		}
		ES_READ_WRITE = StringUtil.isEmpty(es_opt)?ES_READ_WRITE:es_opt;
		String es_type = map.containsKey("es.type")?map.get("es.type"):map.get("elasticsearch.type");
		if(StringUtil.isEmpty(es_type)){
			es_type = env.getProperty("es.type");
			if(StringUtil.isEmpty(es_type)){
				es_type = env.getProperty("elasticsearch.type", "0");
			}
			map.put("es.type", es_type);
		}
		ES_TYPE = StringUtil.isEmpty(es_type)?ES_TYPE:Integer.valueOf(es_type);
		String es_loop = map.containsKey("es.loop")?map.get("es.loop"):map.get("elasticsearch.loop");
		if(StringUtil.isEmpty(es_loop)){
			es_loop = env.getProperty("es.loop");
			if(StringUtil.isEmpty(es_loop)){
				es_loop = env.getProperty("elasticsearch.loop", "1");
			}
			map.put("es.loop", es_loop);
		}
		ES_LOOP = StringUtil.isEmpty(es_loop)?ES_LOOP:Integer.valueOf(es_loop);
		String es_data_gt1k = map.containsKey("es.data.gt1k")?map.get("es.data.gt1k"):map.get("elasticsearch.data.gt1k");
		if(StringUtil.isEmpty(es_data_gt1k)){
			es_data_gt1k = env.getProperty("es.data.gt1k");
			if(StringUtil.isEmpty(es_data_gt1k)){
				es_data_gt1k = env.getProperty("elasticsearch.data.gt1k", "false");
			}
			map.put("es.data.gt1k", es_data_gt1k);
		}
		ES_DATA_GT_1KB = StringUtil.isEmpty(es_data_gt1k)?ES_DATA_GT_1KB:Boolean.valueOf(es_data_gt1k);
		String es_threadpool = map.containsKey("es.threadpool")?map.get("es.threadpool"):map.get("elasticsearch.threadpool");
		if(StringUtil.isEmpty(es_threadpool)){
			es_threadpool = env.getProperty("es.threadpool");
			if(StringUtil.isEmpty(es_threadpool)){
				es_threadpool = env.getProperty("elasticsearch.threadpool");
			}
			if(!StringUtil.isEmpty(es_threadpool)){
				map.put("es.threadpool", es_threadpool);
			}
		}
		ES_PARAM_COUNT = map.size();
	}
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
				if(ES_TYPE==0)result = hfactory.insert("http", "test", json);
				if(ES_TYPE==1)result = rfactory.insert("rest", "test", json);
				if(ES_TYPE==2)result = hrfactory.insert("high", "test", json);
				if(ES_TYPE==3)result = tfactory.insert("transport", "test", json);
				if(ES_TYPE==4)result = sfactory.insert("spring", "test", json);
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
			if(atomic.get()%10000==0){
				logger.info("["+atomic.get()+"]ES Write 耗时:"+(hh>0?hh+"小时":"")+(mm>0?mm+"分钟":"")+ss+"秒-------------"+result);
			}
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
				if(ES_TYPE==0)result = hfactory.selectAll("http", "test", query);
				if(ES_TYPE==1)result = rfactory.selectAll("rest", "test", query);
				if(ES_TYPE==2)result = hrfactory.selectAll("high", "test", query);
				if(ES_TYPE==3)result = tfactory.selectAll("transport", "test", query);
				if(ES_TYPE==4)result = sfactory.selectAll("spring", "test", query);
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
			if(atomic.get()%10000==0){
				logger.info("["+atomic.get()+"]ES Read 耗时:"+(hh>0?hh+"小时":"")+(mm>0?mm+"分钟":"")+ss+"秒-------------"+result.length());
			}
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
	public static void main(String[] args) {
		if(args!=null&&args.length>0){
			for(int i=0;i<args.length;i++){
				String arg = args[i];
				if(!StringUtils.isEmpty(arg)&&map.containsKey("=")){
					String key = arg.substring(0,arg.indexOf("="));
					String value = arg.substring(arg.indexOf("=")+1);
					if(key.startsWith("base.path")||key.startsWith("-base.path")){
						System.setProperty("log.path", value);
					}
					if(key.contains("es.")||key.contains("elasticsearch")){
						map.put(key, value);
					}
				}
			}
		}
		SpringApplication.run(Application.class, args);
	}
}