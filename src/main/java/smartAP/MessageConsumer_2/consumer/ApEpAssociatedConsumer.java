package smartAP.MessageConsumer_2.consumer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.serializer.StringDecoder;
import kafka.utils.VerifiableProperties;
import smartAP.MessageConsumer_2.SourceFilesHelper;
import smartAP.MessageConsumer_2.constant.TableInsertParameter;
import smartAP.MessageConsumer_2.jdbc.ConnectionPool;
import smartAP.MessageConsumer_2.util.PropertiesReader;

public class ApEpAssociatedConsumer extends Thread {

	public void run() {
		try {
			new ApEpAssociatedConsumer().consume();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	private final ConsumerConnector consumer;
	
	private final String TOPIC = "AP_EP_ASSOCIATED";
	
	private final String DATA_TYPE = "SMART_EXT_AP_EP";
	
	private static Logger msgLogInfo = Logger.getLogger("msgInfo");
	
	private Connection connection = ConnectionPool.getInstance().getConnection();
	
	private ConnectionPool pool = ConnectionPool.getInstance();

    public ApEpAssociatedConsumer() {
        Properties props = new Properties();
        
        //zookeeper 配置
        props.put("zookeeper.connect", PropertiesReader.getProperty("zookeeperUrl"));

        //group 代表一个消费组
        props.put("group.id", PropertiesReader.getProperty("Group"));

        //zk连接超时
        props.put("zookeeper.session.timeout.ms", PropertiesReader.getProperty("Timeout"));
        props.put("zookeeper.sync.time.ms", PropertiesReader.getProperty("SyncTime"));
        props.put("auto.commit.interval.ms", PropertiesReader.getProperty("AutoCommitInterval"));
        props.put("auto.offset.reset", PropertiesReader.getProperty("AutoOffsetReset"));
        
        //序列化类
        props.put("serializer.class", PropertiesReader.getProperty("SerializerClass"));

        ConsumerConfig config = new ConsumerConfig(props);

        
        consumer = kafka.consumer.Consumer.createJavaConsumerConnector(config);
    }

    public void consume() throws Exception {
    	
    	
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(TOPIC, new Integer(1));

        StringDecoder keyDecoder = new StringDecoder(new VerifiableProperties());
        StringDecoder valueDecoder = new StringDecoder(new VerifiableProperties());

        Map<String, List<KafkaStream<String, String>>> consumerMap = 
                consumer.createMessageStreams(topicCountMap,keyDecoder,valueDecoder);
        KafkaStream<String, String> stream = consumerMap.get(TOPIC).get(0);
        ConsumerIterator<String, String> it = stream.iterator();
        
        List<String> message = new ArrayList<String>();
        
        while (it.hasNext()) {
        	String data[] = it.next().message().toString().split("\n");
        	for(int i = 0 ; i < data.length ; i++) {
        		message.add(data[i]);
        	}
        	writeIntoTable(message);
        	message.clear();
        }
        
            
    }
    
    
    
    private void writeIntoTable(List<String> message) throws Exception {

		for (SourceFilesHelper.DataTypeProperties item : SourceFilesHelper.DataTypeProperties
				.values()) {
			if (item.getIdentifier() != null
					&& DATA_TYPE.equals(item.getIdentifier())) {
			
				Map<Integer, Object> sqls = item.getSqlTransformer()
						.transform(message);
				if (sqls == null || sqls.size() == 0 || sqls.get(SourceFilesHelper.EXTERNAL) == null) {
					return;
				}
				
				List<String> datas = (List<String>) sqls.get(SourceFilesHelper.EXTERNAL);
				
				List<String> updateLocation = (List<String>) sqls.get(SourceFilesHelper.UPDATE_LOCATION);
				
				List<String> sqlList = new ArrayList<String >();
				
				for(int i = 0 ; i < datas.size(); i++) {
					
					String sql = null;
					String tableName = TableInsertParameter.apEpAssociatedTableName;
					sql = " INSERT INTO " + tableName + "("
							+ TableInsertParameter.inertParameter.get(tableName)
							+ ") VALUES ("
							+ datas.get(i)
							+ ")" ;
					
					sqlList.add(sql);
	
				}
				
				if(pool == null)
					pool = ConnectionPool.getInstance();

				try {
						if(pool.execBatchSql(sqlList))
							System.out.println("AP_EP_ASSOCIATED：WriteIntoNormalTableSucceed");
						else
							System.out.println("AP_EP_ASSOCIATED：WriteIntoNormalTableFailed");
							
				} catch (SQLException e) {
					msgLogInfo.error(
							"failed to write datas into db table .the flag name :"+ e);
				} 
				
				break;
			}
		}    		
	
    }

	
}
