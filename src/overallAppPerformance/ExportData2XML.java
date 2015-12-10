package overallAppPerformance;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import overallAppPerformance.service.ExportBTs4Application;
import overallAppPerformance.service.ExportExternalCalls2XML;
import overallAppPerformance.service.ExportNodes2XML;
import overallAppPerformance.service.ExportSummary2XML;
import overallAppPerformance.service.ExportTiers2XML;
import util.PropertiesLoader;
import util.Utils;

public class ExportData2XML {

	@SuppressWarnings("static-access")
	public static void main(String[] args) {

		
	    Calendar fecha = Calendar.getInstance();
	    
	    SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd.HH.mm");
       	Long beginFecha, endFecha = new Long(1);
	    PropertiesLoader pl = new PropertiesLoader();
	    
		try {
			
			pl.createContext(args[0].trim());
			
		    fecha.set(Calendar.HOUR_OF_DAY, Integer.valueOf(pl.HOUR.split("\\.")[0]));
		    fecha.set(Calendar.MINUTE, Integer.valueOf(pl.HOUR.split("\\.")[1]));
			
			final String LOGS_PATH = PropertiesLoader.LOGS_PATH;
			new File(LOGS_PATH).mkdir();
			final String logFullPath = LOGS_PATH + System.getProperty("file.separator") + "restExportDataLog" + format.format(fecha.getTime()) + ".log";
			
			FileHandler fh = new FileHandler(logFullPath, true);
			ConsoleHandler ch = new ConsoleHandler();

			fh.setFormatter(new SimpleFormatter());
			ch.setFormatter(new SimpleFormatter());
			
			log.addHandler(fh);
			log.addHandler(ch);
			log.setLevel(Level.ALL);
			log.setUseParentHandlers(false);
    	
	    	Integer type = 0;
	    	Integer amount = Integer.valueOf(pl.TIME_RANGE.substring(0, pl.TIME_RANGE.length()-1));
	    	
	    	type = getType(pl, type);
	    	
	    	setTime(pl, fecha, format);
	    	
	        endFecha = fecha.getTimeInMillis();
	        fecha.add(type, -amount);
	        beginFecha = fecha.getTimeInMillis();
			
	        final String APP_NAME = PropertiesLoader.APP_NAME;
			String folder = format.format(beginFecha).concat("_to_").concat(format.format(endFecha));
			final String OUTPUT = PropertiesLoader.OUTPUT_XML_PARTIAL_PATH.concat(System.getProperty("file.separator")).concat(folder);
			new File(OUTPUT).mkdir();
			
			String frequency = new Utils().getConnection(pl.CONTROLER, pl.PORT, pl.USER, pl.PASSWD, pl.ACCOUNT).getRESTGenericMetricQuery(APP_NAME, "Overall Application Performance|Calls per Minute", beginFecha, endFecha, true).getMetric_data().iterator().next().getFrequency();
			
			// Summary Block			
			new ExportSummary2XML().process(APP_NAME, OVERALL_APP_PERFORMANCE, beginFecha, endFecha, OUTPUT, frequency, pl, fh, ch);
	
			// Tiers Block			
			new ExportTiers2XML().process(APP_NAME, OVERALL_APP_PERFORMANCE, beginFecha, endFecha, OUTPUT, frequency, pl, new ExportNodes2XML());		
			
			// BUSINESS TRANSACTIONS BLOCK		
			new ExportBTs4Application().process(APP_NAME, BUSINESS_TRANSACTIONS, beginFecha, endFecha, OUTPUT, frequency, pl, new ExportExternalCalls2XML());

		} catch (Exception e) {
			
			System.out.println("ERROR Durante el proceso de exportacion. " + e);
			
			log.log(Level.SEVERE, "End of process with ERRORS. " + System.getProperty("line.separator") + e);
			
		}
		
	}

	@SuppressWarnings("static-access")
	private static void setTime(PropertiesLoader pl, Calendar fecha,
			SimpleDateFormat format) {
		if(!"NULL".equals(pl.DATE)){
    	String dateInString = pl.DATE.concat(".").concat(pl.HOUR);
    	Date date = null;
		try {
				date = format.parse(dateInString);
			} catch (ParseException e) {
				e.printStackTrace();
			}
	       	fecha.setTime(date);
    	}
	}

	@SuppressWarnings("static-access")
	private static Integer getType(PropertiesLoader pl, Integer type) {
		switch (pl.TIME_RANGE.substring(pl.TIME_RANGE.length()-1)) {
		case "H":
			type = Calendar.HOUR;
			break;
		case "D":
			type = Calendar.DAY_OF_MONTH;
			break;
		case "W":
			type = Calendar.WEEK_OF_MONTH;
			break;
		case "M":
			type = Calendar.MONTH;
			break;
		default:
			break;
		}
		return type;
	}

	private static final Logger log = Logger.getGlobal();
	private static final String OVERALL_APP_PERFORMANCE = "Overall Application Performance|";
	private static final String BUSINESS_TRANSACTIONS = "Business Transaction Performance|Business Transactions|";
	
}
