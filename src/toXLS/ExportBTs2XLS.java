package toXLS;

import java.util.ArrayList;

import domain.Dupla;
import util.PropertiesLoader;
import util.Utils;

public class ExportBTs2XLS {

	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		
        PropertiesLoader pl = new PropertiesLoader();
		try {
			pl.createContext(args[0].trim());
		} catch (Exception e) {
			e.printStackTrace();
		}
        
		final String APP_NAME = PropertiesLoader.APP_NAME;

		String sheetName = "Business Transactions - VEN_PROD";
		
		String filePath2Export = PropertiesLoader.XLS_FILE_NAME;
		
		Utils utils = new Utils(); 
		
		utils.exportDataByRESTGenericMetricQueryXLS(APP_NAME, "TIER - Business Transaction", utils.getConnection(pl.CONTROLER, pl.PORT, pl.USER, pl.PASSWD, pl.ACCOUNT), new ArrayList<Dupla>(), filePath2Export, sheetName);

	}

}
