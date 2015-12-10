package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {

	public void createContext(String path) throws Exception {

		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream(path.trim());

			prop.load(input);

			loadProperties(prop);

		} catch (IOException  e) {

			throw new Exception("ERROR: No pudo cargar archivo de properties: " + path + e);

		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private void loadProperties(Properties prop) throws Exception {
		
		CONTROLER = prop.getProperty(CONTROLER);
		PORT = prop.getProperty(PORT);
		USER = prop.getProperty(USER);
		PASSWD = prop.getProperty(PASSWD);
		ACCOUNT = prop.getProperty(ACCOUNT);
		LOGS_PATH = prop.getProperty(LOGS_PATH);
		DATE = prop.getProperty(DATE);
		HOUR = prop.getProperty(HOUR);
		BT_NAME = prop.getProperty(BT_NAME);
		BTS_FILE = prop.getProperty(BTS_FILE);
		APP_NAME = prop.getProperty(APP_NAME);
		TIER_NAME = prop.getProperty(TIER_NAME);
		TIME_RANGE = prop.getProperty(TIME_RANGE);
		XLS_FILE_NAME = prop.getProperty(XLS_FILE_NAME);
		OUTPUT_FULL_PATH = prop.getProperty(OUTPUT_FULL_PATH);
		OUTPUT_XML_PARTIAL_PATH = prop.getProperty(OUTPUT_XML_PARTIAL_PATH);

	}
	
	public static String CONTROLER = "CONTROLER";
	public static String PORT = "PORT";
	public static String USER = "USER";
	public static String PASSWD = "PASSWD";
	public static String ACCOUNT = "ACCOUNT";
	
	public static String LOGS_PATH = "LOGS_PATH";
	public static String DATE = "DATE";
	public static String HOUR = "HOUR";
	public static String BT_NAME = "BT_NAME";
	public static String BTS_FILE = "BTS_FILE";
	public static String APP_NAME = "APP_NAME";
	public static String TIER_NAME = "TIER_NAME";
	public static String TIME_RANGE = "TIME_RANGE";
	public static String XLS_FILE_NAME = "XLS_FILE_NAME";
	public static String OUTPUT_FULL_PATH = "OUTPUT_FULL_PATH";
	public static String OUTPUT_XML_PARTIAL_PATH = "OUTPUT_XML_PARTIAL_PATH";

}