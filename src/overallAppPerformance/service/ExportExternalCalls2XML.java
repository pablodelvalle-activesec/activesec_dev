package overallAppPerformance.service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.appdynamics.appdrestapi.data.MetricItem;
import org.appdynamics.appdrestapi.data.MetricItems;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import util.PropertiesLoader;
import util.Utils;
import util.XMLWriter;
import domain.Elements;
import domain.EmptyDataException;
import domain.Metrics;

public class ExportExternalCalls2XML {

	@SuppressWarnings("static-access")
	public void process(String APP_NAME, String TIER_NAME, String BT_NAME, String PARTIAL_PATH,long beginFecha, long endFecha, String OUTPUT, String frequency, PropertiesLoader pl) throws Exception{
		
		log.setLevel(Level.ALL);
		
		Utils utils = new Utils();
		
		XMLWriter xmlWriter = new XMLWriter();
		
		String FINAL_PATH, FINAL_METRIC_PATH = "";
		
		Document doc = null;
		
		int filesCount = 0;
		
		String PATH = PARTIAL_PATH.concat("External Calls");
		
		MetricItems items = utils.getConnection(pl.CONTROLER, pl.PORT, pl.USER, pl.PASSWD, pl.ACCOUNT).getBaseMetricListPath(APP_NAME, PATH);
		
		if(items != null){
		
			List<MetricItem> metricItems = items.getMetricItems();
			
			log.log( Level.INFO, "--------------------------- Processing external calls ------------------------------- " );
			
			log.log( Level.INFO, "Processing {0} external calls in loop.", metricItems.size() );
			
			for (MetricItem metricItem : metricItems) {
				
				try {
					
					doc = xmlWriter.initDocument();
				
					FINAL_PATH = OUTPUT.concat(System.getProperty("file.separator")).concat(APP_NAME.concat("_")).concat(TIER_NAME).concat("_").concat(Utils.scapes(metricItem.getName()).trim().concat(".xml"));
					
					FINAL_METRIC_PATH = PATH.concat("|").concat(metricItem.getName()).concat("|");
					
					Element element = xmlWriter.appendCommonValues(APP_NAME, TIER_NAME, BT_NAME,metricItem.getName(), doc, beginFecha, endFecha, frequency, Elements.EXTERNAL_CALLS);
					
					Element summary_element = doc.createElement("summary_data");
					
					for (Metrics metric : metrics)
						try {
							utils.exportData2XMLGenericMetricQuery(APP_NAME, FINAL_METRIC_PATH.concat(metric.getDescription()), 
										beginFecha, endFecha, utils.getConnection(pl.CONTROLER, pl.PORT, pl.USER, pl.PASSWD, pl.ACCOUNT), xmlWriter, doc, summary_element, metric.getDescription());
						} catch (EmptyDataException e) {
							log.log( Level.SEVERE, "Couldn't excecute metric query : " + FINAL_METRIC_PATH.concat(metric.getDescription()) );
						}
					
					element.appendChild(summary_element);
					
					Element node_datas = doc.createElement("external_call_datas");
					
					for (Metrics metric : metrics)
						utils.exportData2XMLGenericMetricQueryFullInfo(APP_NAME, FINAL_METRIC_PATH.concat(metric.getDescription()), 
									beginFecha, endFecha, utils.getConnection(pl.CONTROLER, pl.PORT, pl.USER, pl.PASSWD, pl.ACCOUNT), xmlWriter, doc, node_datas, metric.getDescription());
					
					element.appendChild(node_datas);
					
					xmlWriter.saveAndClose(doc, FINAL_PATH.trim());
					
					filesCount++;
					
				} catch (Exception e) {
					
					log.log(Level.SEVERE, "ERROR to create xml file for External Call : " + metricItem.getName() + e);
					
					throw new Exception("ERROR to create xml file for External Call : " + metricItem.getName() + e);
					
				}
				
			}

			
			if(metricItems.size() == filesCount)
				log.log(Level.INFO, " {0} files were created.", filesCount);
			else {
				Object[] objs = {filesCount, metricItems.size()};
				log.log(Level.SEVERE, " {0} files were created from {1}.", objs);
			}
		
			log.log( Level.INFO, "--------------------------- End of external calls block ------------------------------- " );
			
			
		}
		
				
	}
	
	static Metrics[] metrics = {Metrics.AVG_RESPONSE_TIME, Metrics.CALLS_PER_MINUTE, Metrics.SLOW_CALLS, Metrics.VERY_SLOW_CALLS, Metrics.STALL_CALLS, Metrics.ERRORS_PER_MINUTE, Metrics.PERCETILE_RESPONSE_TIME, Metrics.PERCETILE2_RESPONSE_TIME};
	
	private static final Logger log = Logger.getGlobal();
	
}