package toXLS;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import domain.Dupla;
import domain.Metrics;
import util.PropertiesLoader;
import util.Utils;

public class ExportData2XLS {
	
	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		
	       	Long beginFecha, endFecha, beginFechaDos, endFechaDos = new Long(1);
	        
	        Calendar fechaUno = Calendar.getInstance();
	        fechaUno.add(Calendar.WEEK_OF_MONTH, -1);
	        endFecha = fechaUno.getTimeInMillis();
	        fechaUno.add(Calendar.HOUR, -24);
	        beginFecha = fechaUno.getTimeInMillis();
	        fechaUno.add(Calendar.HOUR, 24);
	        fechaUno.add(Calendar.WEEK_OF_MONTH, -1);
	        endFechaDos = fechaUno.getTimeInMillis();
	        fechaUno.add(Calendar.HOUR, -24);
	        beginFechaDos = fechaUno.getTimeInMillis();
	        
	        PropertiesLoader pl = new PropertiesLoader();
			try {
				pl.createContext(args[0].trim());
			} catch (Exception e) {
				e.printStackTrace();
			}
	        
			final String ROOT_REST_PATH = "Business Transaction Performance|Business Transactions|";
			final String PATH_SEPARATOR = "|";
			final String APP_NAME = PropertiesLoader.APP_NAME;
			final String TIER_NAME = PropertiesLoader.TIER_NAME;
			final String BT_NAME = PropertiesLoader.BT_NAME;
			final String OUTPUT_FULL_PATH = PropertiesLoader.OUTPUT_FULL_PATH;
			
			SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			
			String PARTIAL_PATH = ROOT_REST_PATH.concat(TIER_NAME).concat(PATH_SEPARATOR).concat(BT_NAME).concat(PATH_SEPARATOR);
	        
	        String header = "Serie uno : ".concat(format1.format(beginFecha)).concat(" to ").concat( format1.format(endFecha))
	        						.concat(", Serie dos : ").concat(format1.format(beginFechaDos)).concat(" to ").concat(format1.format(endFechaDos));
	        
	        String endSheet = " BT ".concat((BT_NAME.contains("/") ? BT_NAME.substring(BT_NAME.lastIndexOf("/") + 1, BT_NAME.length()) : BT_NAME));
	        
			Utils utils = new Utils();
			
			for (Metrics metric : Metrics.values()) 
				utils.exportDataByRESTGenericMetricQuery(APP_NAME, PARTIAL_PATH.concat(metric.getDescription()), 
						beginFecha, endFecha, beginFechaDos, endFechaDos, header, utils.getConnection(pl.CONTROLER, pl.PORT, pl.USER, pl.PASSWD, pl.ACCOUNT), new ArrayList<Dupla>(), 
									OUTPUT_FULL_PATH, metric.getDescription().concat(endSheet));				

	}

}
