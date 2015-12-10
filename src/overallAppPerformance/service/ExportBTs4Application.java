package overallAppPerformance.service;

import java.util.List;

import org.appdynamics.appdrestapi.data.BusinessTransaction;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.logging.Level;
import java.util.logging.Logger;

import util.PropertiesLoader;
import util.Utils;
import util.XMLWriter;
import domain.Elements;
import domain.EmptyDataException;
import domain.Metrics;

public class ExportBTs4Application {
	
	@SuppressWarnings("static-access")
	public void process(String APP_NAME, String PARTIAL_PATH,long beginFecha, long endFecha, String OUTPUT, String frequency, PropertiesLoader pl, ExportExternalCalls2XML exportExternalCalls2XML) throws Exception{
		
		log.setLevel(Level.ALL);
		
		Utils utils = new Utils();
		
		XMLWriter xmlWriter = new XMLWriter();
		
		String FINAL_PATH = null, FINAL_METRIC_PATH = "";
		
		Document doc = null;
		
		int filesCount = 0;
		
		List<BusinessTransaction> BTs = utils.getConnection(pl.CONTROLER, pl.PORT, pl.USER, pl.PASSWD, pl.ACCOUNT).getBTSForApplication(APP_NAME).getBusinessTransactions();
		
		List<String> btsScoped = utils.getResponseValuesByTagName(pl.BTS_FILE, "Bt");
		
		log.log( Level.INFO, "--------------------------- Processing business transactions ------------------------------- " );
		
		log.log( Level.INFO, "Processing {0} business transactions in loop.", btsScoped.size() );
		
		for (BusinessTransaction bt : BTs) {
			
			if(btsScoped.contains(bt.getName())){
				
				log.log( Level.INFO, "Processing {0}.", bt.getName() );
				
				try {
					
					doc = xmlWriter.initDocument();
				
					FINAL_PATH = OUTPUT.concat(System.getProperty("file.separator")).concat(APP_NAME.concat("_")).
							concat(bt.getTierName()).concat("_").
							concat(Utils.scapes(bt.getName()).concat(".xml"));
		
					FINAL_METRIC_PATH = PARTIAL_PATH.concat(bt.getTierName()).concat("|").concat(bt.getName()).concat("|");
					
					Element element = xmlWriter.appendCommonValues(APP_NAME, bt.getTierName(), Utils.scapes(bt.getName()), doc, beginFecha, endFecha, frequency, Elements.BUSINESS_TRANSACTIONS);
					
					Element summary_element = doc.createElement("summary_data");
					
					for (Metrics metric : metrics)
						try {
							utils.exportData2XMLGenericMetricQuery(APP_NAME, FINAL_METRIC_PATH.concat(metric.getDescription()), 
										beginFecha, endFecha, utils.getConnection(pl.CONTROLER, pl.PORT, pl.USER, pl.PASSWD, pl.ACCOUNT), xmlWriter, doc, summary_element, metric.getDescription());
						} catch (EmptyDataException e) {
							log.log( Level.SEVERE, "Couldn't excecute metric query : " + FINAL_METRIC_PATH.concat(metric.getDescription()) );
						}
					
					element.appendChild(summary_element);
					
					Element tier_datas = doc.createElement("business_transaction_datas");
					
					for (Metrics metric : metrics)
						utils.exportData2XMLGenericMetricQueryFullInfo(APP_NAME, FINAL_METRIC_PATH.concat(metric.getDescription()), 
									beginFecha, endFecha, utils.getConnection(pl.CONTROLER, pl.PORT, pl.USER, pl.PASSWD, pl.ACCOUNT), xmlWriter, doc, tier_datas, metric.getDescription());
					
					element.appendChild(tier_datas);
					
					xmlWriter.appendExternalCallsData(doc, utils.getConnection(pl.CONTROLER, pl.PORT, pl.USER, pl.PASSWD, pl.ACCOUNT).getBaseMetricListPath(APP_NAME, FINAL_METRIC_PATH.concat("External Calls")).getMetricItems());
					
					xmlWriter.saveAndClose(doc, FINAL_PATH.trim());
				
					exportExternalCalls2XML.process(APP_NAME, bt.getTierName(), bt.getName(), FINAL_METRIC_PATH, beginFecha, endFecha, OUTPUT, frequency, pl);
					
				} catch (Exception e) {
					
					log.log(Level.SEVERE, "ERROR to create xml file for BT : " + bt.getName() + e);
					
					throw new Exception("ERROR to create xml file for BT : " + bt.getName() + e);
					
				}
				
				log.log( Level.INFO, "File {0} was created successfully.", FINAL_PATH.trim() );
			
				filesCount++;
				
			}	
			
		}
		
		if(btsScoped.size() == filesCount)
			log.log(Level.INFO, " {0} BT files were created.", filesCount);
		else {
			Object[] objs = {filesCount, btsScoped.size()};
			log.log(Level.SEVERE, " {0} BT files were created from {1}.", objs);
		}
	
		log.log( Level.INFO, "--------------------------- End of business transactions block ------------------------------- " );
	
	}
	
	
	static Metrics[] metrics = {Metrics.AVG_RESPONSE_TIME, Metrics.CALLS_PER_MINUTE, Metrics.SLOW_CALLS, Metrics.VERY_SLOW_CALLS, Metrics.STALL_CALLS, Metrics.ERRORS_PER_MINUTE, Metrics.PERCETILE_RESPONSE_TIME, Metrics.PERCETILE2_RESPONSE_TIME};
	
	private static final Logger log = Logger.getGlobal();
	
}