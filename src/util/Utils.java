package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.appdynamics.appdrestapi.RESTAccess;
import org.appdynamics.appdrestapi.data.Application;
import org.appdynamics.appdrestapi.data.BusinessTransaction;
import org.appdynamics.appdrestapi.data.BusinessTransactions;
import org.appdynamics.appdrestapi.data.MetricData;
import org.appdynamics.appdrestapi.data.MetricDatas;
import org.appdynamics.appdrestapi.data.MetricValue;
import org.appdynamics.appdrestapi.data.MetricValues;
import org.appdynamics.appdrestapi.data.Snapshot;
import org.appdynamics.appdrestapi.data.Snapshots;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import domain.Dupla;
import domain.EmptyDataException;

public class Utils {

	public List<String> getResponseValuesByTagName(String filePath, String tagName)
			throws ParserConfigurationException, SAXException, IOException {

		DocumentBuilderFactory unsignDoc = DocumentBuilderFactory.newInstance();
		unsignDoc.setNamespaceAware(true);
		DocumentBuilder builder;
		FileInputStream input = null;

		builder = unsignDoc.newDocumentBuilder();

		input = new FileInputStream(filePath);

		Document doc = builder.parse(input);

		// get the first element
		Element element = doc.getDocumentElement();

		input.close();

		return getTextValuesByTagName(element, tagName);

	}

	private static List<String> getTextValuesByTagName(Element element,
			String tagName) {
		NodeList nodeList = element.getElementsByTagName(tagName);
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < nodeList.getLength(); i++) {
			list.add(getTextValue(nodeList.item(i)));
		}
		return list;
	}

	private static String getTextValue(Node node) {
		StringBuffer textValue = new StringBuffer();
		int length = node.getChildNodes().getLength();
		for (int i = 0; i < length; i++) {
			Node c = node.getChildNodes().item(i);
			if (c.getNodeType() == Node.TEXT_NODE) {
				textValue.append(c.getNodeValue());
			}
		}
		return textValue.toString().trim();
	}
	
	public void exportDataByRESTGenericMetricQuery(String appName, String RESTPath, Long initFecha,
			Long finalFecha, Long initFechaDos, Long finalFechaDos,
			String header, RESTAccess access, List<Dupla> duplas, String filePath2Export, String sheetName) {
		
		// primer consulta REST 
		MetricDatas metricDatas = access.getRESTGenericMetricQuery(appName, RESTPath, initFecha, finalFecha, false);
		loadFirstValues(duplas, metricDatas.getMetric_data());
		
		// segunda consulta REST
		MetricDatas metricDatas2 = access.getRESTGenericMetricQuery(appName, RESTPath, initFechaDos, finalFechaDos, false);
		loadSecondValues(duplas, metricDatas2.getMetric_data());
		
		// export
		try {
			if(new File(filePath2Export).exists())
				editXLS(filePath2Export, duplas, header, sheetName);
			else
				createXLS(filePath2Export, duplas, header, sheetName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void exportDataByRESTGenericMetricQueryXLS(String appName, String header, RESTAccess access, List<Dupla> duplas, String filePath2Export, String sheetName) {
		
		List<BusinessTransaction> bts = access.getBTSForApplication(appName).getBusinessTransactions();
		
		loadValues(duplas, bts);
		
		// export
		try {
			createXLS(filePath2Export, duplas, header, sheetName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void exportData2XMLGenericMetricQuery(String appName, String RESTPath, Long initFecha,
			Long finalFecha, RESTAccess access, XMLWriter xmlWriter, Document doc, Element element, String description) throws EmptyDataException {
		
		// consulta REST 
		MetricDatas metricDatas = access.getRESTGenericMetricQuery(appName, RESTPath, initFecha, finalFecha, true);
		
		if(metricDatas != null){
		
			List<MetricValue> mValues = metricDatas.getSingleMetricValues();
			
			if(mValues != null)
			
			for (MetricValue metricValue : mValues) {

				xmlWriter.createElement(doc, scapes(description).replace(" ", "_"), String.valueOf(metricValue.getValue()), element);

			}
		
		} else {
			throw new EmptyDataException();
		}
		
	}
	
	public void exportData2XMLGenericMetricQueryFullInfo(String appName, String RESTPath, Long initFecha,
			Long finalFecha, RESTAccess access, XMLWriter xmlWriter, Document doc, Element element, String description) {
		
		MetricDatas metricDatas = null;
		
		try {
			
			metricDatas = access.getRESTGenericMetricQuery(appName, RESTPath, initFecha, finalFecha, false);
		
		} catch (Exception e) {
			
			log.log(Level.SEVERE, "ERROR: Exception occurred executing REST query {0}", RESTPath);
			
		}
		
		if(metricDatas != null){
		
			List<MetricValue> mValues = metricDatas.getSingleMetricValues();
			
			SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd.HH.mm");

			if(mValues != null){
				
				Element baElement = doc.createElement(Utils.scapes(description).replace(" ", "_"));

				for (MetricValue metricValue : mValues) {

						Element valueElement = doc.createElement("value");
						valueElement.appendChild(doc.createTextNode(String.valueOf(metricValue.getValue())));
						baElement.appendChild(valueElement);
						
						Element dateElement = doc.createElement("date");
						dateElement.appendChild(doc.createTextNode(format.format(metricValue.getStartTimeInMillis())));
						baElement.appendChild(dateElement);
					
				}
				
				element.appendChild(baElement);
				
			}
			
		}
				
	}
	
	private void loadValues(List<Dupla> duplas,
			List<BusinessTransaction> bts) {
		Dupla dupla;
		
		for (BusinessTransaction bt : bts) {
			dupla = new Dupla();
			dupla.setValueUno(bt.getTierName());
			dupla.setValueDos(bt.getName());
			duplas.add(dupla);
		}
	}
	
	private void loadSecondValues(List<Dupla> duplas,
			List<MetricData> metricDts2) {
		Dupla dupla;
		int i = 0;
		
		for (MetricData metricData2 : metricDts2) {
			List<MetricValues> metricVls2 = metricData2.getMetricValues();
			for (MetricValues mv : metricVls2) {
				List<MetricValue> value = mv.getMetricValue();
				for (MetricValue metricValue : value) {
					dupla = duplas.get(i);
					dupla.setValueDos(metricValue.getCurrent());
					duplas.add(dupla);
					i++;
				}
			}
			
		}
	}

	private void loadFirstValues(List<Dupla> duplas,
			List<MetricData> metricDts) {
		Dupla dupla;
		for (MetricData metricData : metricDts) {
			List<MetricValues> metricVls = metricData.getMetricValues();
			for (MetricValues mv : metricVls) {
				List<MetricValue> value = mv.getMetricValue();
				for (MetricValue metricValue : value) {
					dupla = new Dupla();
					dupla.setValueUno(metricValue.getCurrent());
					duplas.add(dupla);
				}
			}
			
		}
	}
	
	public static void editXLS(String fileName, List<Dupla> metricsList, String header, String sheetName) throws Exception{
		
		FileInputStream fis = new FileInputStream(fileName);
        
        Workbook workbook = null;
        if(fileName.toLowerCase().endsWith("xlsx")){
            workbook = new XSSFWorkbook(fis);
        }else if(fileName.toLowerCase().endsWith("xls")){
            workbook = new HSSFWorkbook(fis);
        }else{
            extracted();
        } 
        
        Sheet sheet = workbook.createSheet(sheetName);
        
        Iterator<Dupla> iterator = metricsList.iterator();
        
        Row rowHeader = sheet.createRow(0);
        Cell cellHeader = rowHeader.createCell(0);
        cellHeader.setCellValue(header);
        
        int rowIndex = 1;
        while(iterator.hasNext()){
            Dupla values = iterator.next();
            Row row = sheet.createRow(rowIndex++);
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(values.getValueUno().toString());
            Cell cell1 = row.createCell(1);
            cell1.setCellValue(values.getValueDos().toString());
        }
         
        //lets write the excel data to file now
        FileOutputStream fos = new FileOutputStream(fileName);
        workbook.write(fos);
        fos.close();
        System.out.println(sheetName + " written successfully.");
	}

	private static void extracted() throws Exception {
		throw new Exception("invalid file name, should be xls or xlsx");
	}
	
	public static void createXLS(String fileName, List<Dupla> metricsList, String header, String sheetName) throws Exception{
        Workbook workbook = null;
         
        if(fileName.endsWith("xlsx")){
            workbook = new XSSFWorkbook();
        }else if(fileName.endsWith("xls")){
            workbook = new HSSFWorkbook();
        }else{
            extracted();
        }
         
        Sheet sheet = workbook.createSheet(sheetName);
         
        Iterator<Dupla> iterator = metricsList.iterator();
        
        Row rowHeader = sheet.createRow(0);
        Cell cellHeader = rowHeader.createCell(0);
        cellHeader.setCellValue(header);
        
        int rowIndex = 1;
        while(iterator.hasNext()){
            Dupla values = iterator.next();
            Row row = sheet.createRow(rowIndex++);
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(values.getValueUno().toString());
            Cell cell1 = row.createCell(1);
            cell1.setCellValue(values.getValueDos().toString());
        }
         
        //lets write the excel data to file now
        FileOutputStream fos = new FileOutputStream(fileName);
        workbook.write(fos);
        fos.close();
        System.out.println(sheetName + " written successfully.");
    }
	
	public RESTAccess getConnection(String controller, String port, String user, String passwd, String account){

		if(restAccess == null)
			restAccess = new RESTAccess(controller,port,true,user,passwd,account);
		
		return restAccess;
		
	}
	
	public List<Application> getApplications(String controller, String port, String user, String passwd, String account){
		
		return this.getConnection(controller, port, user, passwd, account).getApplications().getApplications();
		
	}
	
	public Map<Integer, BusinessTransaction> loadBTs2Map(int appId, String controller, String port, String user, String passwd, String account){
		Map<Integer, BusinessTransaction> map = new HashMap<Integer, BusinessTransaction>();
		BusinessTransactions BTs = getConnection(controller, port, user, passwd, account).getBTSForApplication(appId);
		List<BusinessTransaction> list2BTs = BTs.getBusinessTransactions();
		for (BusinessTransaction bt : list2BTs) 
			map.put(bt.getId(), bt);
		
		return map;
	}
	
	public List<Snapshot> getSnapshots(Integer appId, String controller, String port, String user, String passwd, String account){
		
		Long initFecha = new Long(1);
        Long finalFecha = new Long(1);
        
        Calendar fechaUno = Calendar.getInstance();
        finalFecha = fechaUno.getTimeInMillis();
        fechaUno.add(Calendar.MINUTE, -30);
        initFecha = fechaUno.getTimeInMillis();
        
        Snapshots snapshots = getConnection(controller, port, user, passwd, account).getSnapshots(appId, initFecha, finalFecha);
        
        return snapshots.getRequestDatas();
		
	}
	
	public static String scapes(String stn){
		String formated = stn.trim().replace("\r\n", " ").replace("\n", " ").
				replace("\t", ".t").replace("\\", ".").replace("/", ".").
				replace(":", ".").replace("\"", ".").replace("*", ".").replace("?", ".").
				replace("'", ".").replace("|", ".").replace("(ms)", "");
		
		return ("9".equals(formated.substring(0,1))) ? formated.substring(4).trim() : formated;
	}

	private static RESTAccess restAccess;
	private static final Logger log = Logger.getLogger( Utils.class.getName() );
	
}
