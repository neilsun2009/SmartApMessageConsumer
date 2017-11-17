package smartAP.MessageConsumer_2;

import java.util.Timer;
import java.util.TimerTask;

import smartAP.MessageConsumer_2.DataTableColumnDefind;
import smartAP.MessageConsumer_2.cache.DeviceInfoCache;
import smartAP.MessageConsumer_2.consumer.ApEpAssociatedConsumer;
import smartAP.MessageConsumer_2.consumer.CollectionHotspotConsumer;
import smartAP.MessageConsumer_2.consumer.TermianalFeatureConsumer;
import smartAP.MessageConsumer_2.consumer.VirtualIdentityConsumer;


public class App 
{
    public static void main( String[] args ) throws Exception{
    	DataTableColumnDefind.init();
		// init all deviceCode relations
		DeviceInfoCache.getInstance().reloadCache();
		// timmer every five mins update deviceinfocache
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				DeviceInfoCache.getInstance().reloadCache();

			}
		}, 1000, 300000);
		Thread.sleep(5000);
    	
		VirtualIdentityConsumer viConsumer = new VirtualIdentityConsumer();
		
		TermianalFeatureConsumer tfConsumer  = new TermianalFeatureConsumer();
		
		CollectionHotspotConsumer chConsumer = new CollectionHotspotConsumer();
		
		ApEpAssociatedConsumer aeaConsumer = new ApEpAssociatedConsumer();
		
		viConsumer.start();
		
		tfConsumer.start();
		
		chConsumer.start();
		
		aeaConsumer.start();
		
    	//new KafkaConsumer().consume();
    	
//    	new VirtualIdentityConsumer().consume();
//    	
//    	new TermianalFeatureConsumer().consume();
//    	
//    	new CollectionHotspotConsumer().consume();
//    	
//    	new ApEpAssociatedConsumer().consume();
    }
}
