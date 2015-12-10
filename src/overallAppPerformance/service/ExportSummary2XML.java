package overallAppPerformance.service;

import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import util.PropertiesLoader;
import util.Utils;
import util.XMLWriter;
import domain.EmptyDataException;
import domain.Metrics;

public class ExportSummary2XML {

	@SuppressWarnings("static-access")
	public void process(String APP_NAME, String PARTIAL_PATH,long beginFecha, long endFecha, String OUTPUT, String frequency, PropertiesLoader pl, FileHandler fh, ConsoleHandler ch) throws Exception{
		
		log.setLevel(Level.ALL);
		
		Utils utils = new Utils();
		
		XMLWriter xmlWriter = new XMLWriter();
		
		Document doc = null;
		
		try {
			 doc = xmlWriter.initDocument();
		
			Element element = xmlWriter.appendCommonValues(APP_NAME, "ALL_TIERS", doc, beginFecha, endFecha, frequency);
			
			Element summary_element = doc.createElement("summary_data");
			
			for (Metrics metric : metrics)
				try {
					utils.exportData2XMLGenericMetricQuery(APP_NAME, PARTIAL_PATH.concat(metric.getDescription()), 
								beginFecha, endFecha, utils.getConnection(pl.CONTROLER, pl.PORT, pl.USER, pl.PASSWD, pl.ACCOUNT), xmlWriter, doc, summary_element, metric.getDescription());
				} catch (EmptyDataException e) {
					log.log( Level.SEVERE, "Couldn't excecute metric query : " + PARTIAL_PATH.concat(metric.getDescription()) );
				}
			
			element.appendChild(summary_element);
			
			xmlWriter.appendTiersData(doc, utils.getConnection(pl.CONTROLER, pl.PORT, pl.USER, pl.PASSWD, pl.ACCOUNT).getTiersForApplication(APP_NAME).getTiers());
			xmlWriter.saveAndClose(doc, OUTPUT.concat(System.getProperty("file.separator").concat(APP_NAME).concat("_SUMMARY.xml").trim()));
			
			log.log( Level.INFO, "File {0} was created successfully.", OUTPUT.concat(System.getProperty("file.separator").concat(APP_NAME).concat("_SUMMARY.xml").trim()) );
			
		} catch (Exception e) {
			
			log.log(Level.SEVERE, "ERROR to create summary xml file for : " + APP_NAME + e);
			
			throw new Exception("ERROR to create xml file for Summary. " + e);
			
		}
		
	}

	static Metrics[] metrics = {Metrics.NORMAL_AVERAGE_RESPONSE_TIME, Metrics.SLOW_CALLS, Metrics.VERY_SLOW_CALLS, Metrics.STALL_CALLS, Metrics.ERRORS_PER_MINUTE};
	
	private final Logger log = Logger.getGlobal();
	
}