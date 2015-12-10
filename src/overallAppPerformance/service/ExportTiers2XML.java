package overallAppPerformance.service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.appdynamics.appdrestapi.data.Tier;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import util.PropertiesLoader;
import util.Utils;
import util.XMLWriter;
import domain.EmptyDataException;
import domain.Metrics;

public class ExportTiers2XML {

	@SuppressWarnings("static-access")
	public void process(String APP_NAME, String PARTIAL_PATH,long beginFecha, long endFecha, String OUTPUT, String frequency, PropertiesLoader pl, ExportNodes2XML exportNodes2XML) throws Exception{
		
		Utils utils = new Utils();
		
		XMLWriter xmlWriter = new XMLWriter();
		
		String FINAL_PATH, FINAL_METRIC_PATH = "";
		
		Document doc = null;
		
		int filesCount = 0;
		
		List<Tier> tiers = utils.getConnection(pl.CONTROLER, pl.PORT, pl.USER, pl.PASSWD, pl.ACCOUNT).getTiersForApplication(APP_NAME).getTiers();
		
		log.log( Level.INFO, "--------------------------- Processing tiers ------------------------------- " );
		
		log.log( Level.INFO, "Processing {0} tiers in loop.", tiers.size() );
		
		for (Tier tier : tiers) {
		
			log.log( Level.INFO, "--------------------------- Processing {0} tier ------------------------------- ", tier.getName() );
			
			try {
				doc = xmlWriter.initDocument();
			
				FINAL_PATH = OUTPUT.concat(System.getProperty("file.separator")).concat(APP_NAME.concat("_")).concat(tier.getName()).trim().concat(".xml");
				
				FINAL_METRIC_PATH = PARTIAL_PATH.concat(tier.getName()).concat("|");
				
				Element element = xmlWriter.appendCommonValues(APP_NAME, tier.getName(), doc, beginFecha, endFecha, frequency);
				
				Element summary_element = doc.createElement("summary_data");
				
				for (Metrics metric : metrics)
					try {
						utils.exportData2XMLGenericMetricQuery(APP_NAME, FINAL_METRIC_PATH.concat(metric.getDescription()), 
									beginFecha, endFecha, utils.getConnection(pl.CONTROLER, pl.PORT, pl.USER, pl.PASSWD, pl.ACCOUNT), xmlWriter, doc, summary_element, metric.getDescription());
					} catch (EmptyDataException e) {
						log.log( Level.SEVERE, "Couldn't excecute metric query : " + FINAL_METRIC_PATH.concat(metric.getDescription()) );
						break;
					}
				
				element.appendChild(summary_element);
				
				Element tier_datas = doc.createElement("tier_datas");
				
				for (Metrics metric : metrics)
					utils.exportData2XMLGenericMetricQueryFullInfo(APP_NAME, FINAL_METRIC_PATH.concat(metric.getDescription()), 
								beginFecha, endFecha, utils.getConnection(pl.CONTROLER, pl.PORT, pl.USER, pl.PASSWD, pl.ACCOUNT), xmlWriter, doc, tier_datas, metric.getDescription());
				
				element.appendChild(tier_datas);
				
				xmlWriter.appendNodesData(doc, utils.getConnection(pl.CONTROLER, pl.PORT, pl.USER, pl.PASSWD, pl.ACCOUNT).getNodesFromTier(APP_NAME, tier.getName()).getNodes());	
				
				xmlWriter.saveAndClose(doc, FINAL_PATH.trim());
				
				log.log( Level.INFO, "File {0} was created successfully.", FINAL_PATH.trim() );
				
				// export Nodes Info
				exportNodes2XML.process(APP_NAME, tier.getName(), PARTIAL_PATH, beginFecha, endFecha, OUTPUT, frequency, pl);
				
				filesCount++;
			
			} catch (Exception e) {
				
				log.log(Level.SEVERE, "ERROR to create xml file for Tier : " + tier.getName() + e);
				
				throw new Exception("ERROR to create xml file for Tier : " + tier.getName() + e);
				
			}
			
		}
		
		if(tiers.size() == filesCount)
			log.log(Level.FINE, " {0} Tier files were created.", filesCount);
		else {
			Object[] objs = {filesCount, tiers.size()};
			log.log(Level.SEVERE, " {0} Tier files were created from {1}.", objs);
		}
	
		log.log( Level.INFO, "--------------------------- End of tiers block ------------------------------- " );
		
	}
	
	static Metrics[] metrics = {Metrics.AVG_RESPONSE_TIME, Metrics.CALLS_PER_MINUTE, Metrics.SLOW_CALLS, Metrics.VERY_SLOW_CALLS, Metrics.STALL_CALLS, Metrics.ERRORS_PER_MINUTE, Metrics.PERCETILE_RESPONSE_TIME, Metrics.PERCETILE2_RESPONSE_TIME};
	
	private final Logger log = Logger.getGlobal();
	
}