package util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.appdynamics.appdrestapi.data.BusinessTransaction;
import org.appdynamics.appdrestapi.data.MetricItem;
import org.appdynamics.appdrestapi.data.Node;
import org.appdynamics.appdrestapi.data.Tier;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import domain.Elements;

public class XMLWriter {

	public void createXMLDocument(Document doc, String appName, String tierName, long initFecha, long finalFecha){

		appendCommonValues(appName, tierName, doc, initFecha, finalFecha);

		appendSummaryData(doc);

	}

	public Element appendCommonValues(String appName, String tierName, Document doc, long initFecha, long finalFecha){

		// rest_metrics elements
		Element rest_metrics = doc.createElement("rest_metrics");
		doc.appendChild(rest_metrics);

		// set app_name to rest_metrics element
		Attr app_name = doc.createAttribute("app_name");
		app_name.setValue(appName);
		rest_metrics.setAttributeNode(app_name);			

		// set tier_name to rest_metrics element
		Attr tier_name = doc.createAttribute("tier_name");
		tier_name.setValue(tierName);
		rest_metrics.setAttributeNode(tier_name);			

		// set time_range to rest_metrics element
		Element time_range = doc.createElement("time_range");
		setUpTimeRange(doc, time_range, initFecha, finalFecha, null);
		rest_metrics.appendChild(time_range);

		return rest_metrics;

	}

	public Element appendCommonValues(String appName, String tierName, Document doc, long initFecha, long finalFecha, String frequency){

		// rest_metrics elements
		Element rest_metrics = doc.createElement("rest_metrics");
		doc.appendChild(rest_metrics);

		// set app_name to rest_metrics element
		Attr app_name = doc.createAttribute("app_name");
		app_name.setValue(appName);
		rest_metrics.setAttributeNode(app_name);			

		// set tier_name to rest_metrics element
		Attr tier_name = doc.createAttribute("tier_name");
		tier_name.setValue(tierName);
		rest_metrics.setAttributeNode(tier_name);			

		// set time_range to rest_metrics element
		Element time_range = doc.createElement("time_range");
		setUpTimeRange(doc, time_range, initFecha, finalFecha, frequency);
		rest_metrics.appendChild(time_range);

		return rest_metrics;

	}
	

	public Element appendCommonValues(String appName, String tierName,
			String btName, String elementValue, Document doc, long initFecha,
			long finalFecha, String frequency, Elements element) {
		// rest_metrics elements
		Element rest_metrics = doc.createElement("rest_metrics");
		doc.appendChild(rest_metrics);

		// set app_name to rest_metrics element
		Attr app_name = doc.createAttribute("app_name");
		app_name.setValue(appName);
		rest_metrics.setAttributeNode(app_name);			

		// set tier_name to rest_metrics element
		Attr tier_name = doc.createAttribute("tier_name");
		tier_name.setValue(tierName);
		rest_metrics.setAttributeNode(tier_name);
		
		// set tier_name to rest_metrics element
		Attr bt_name = doc.createAttribute("bt_name");
		bt_name.setValue(btName);
		rest_metrics.setAttributeNode(bt_name);

		// set rest_metrics element
		Attr detailElement = doc.createAttribute(element.getDescripcion());
		detailElement.setValue(elementValue);
		rest_metrics.setAttributeNode(detailElement);			

		// set time_range to rest_metrics element
		Element time_range = doc.createElement("time_range");
		setUpTimeRange(doc, time_range, initFecha, finalFecha, frequency);
		rest_metrics.appendChild(time_range);

		return rest_metrics;	}
	
	public Element appendCommonValues(String appName, String tierName, String elementValue, Document doc, long initFecha, long finalFecha, String frequency, Elements element){
		
		// rest_metrics elements
		Element rest_metrics = doc.createElement("rest_metrics");
		doc.appendChild(rest_metrics);

		// set app_name to rest_metrics element
		Attr app_name = doc.createAttribute("app_name");
		app_name.setValue(appName);
		rest_metrics.setAttributeNode(app_name);			

		// set tier_name to rest_metrics element
		Attr tier_name = doc.createAttribute("tier_name");
		tier_name.setValue(tierName);
		rest_metrics.setAttributeNode(tier_name);			

		// set rest_metrics element
		Attr detailElement = doc.createAttribute(element.getDescripcion());
		detailElement.setValue(elementValue);
		rest_metrics.setAttributeNode(detailElement);			

		// set time_range to rest_metrics element
		Element time_range = doc.createElement("time_range");
		setUpTimeRange(doc, time_range, initFecha, finalFecha, frequency);
		rest_metrics.appendChild(time_range);

		return rest_metrics;

	}
	
	private void appendSummaryData(Document doc) {

		Element element = doc.createElement("summary_data");


		doc.getFirstChild().appendChild(element);

	}

	public void appendBTsData(Document doc, List<BusinessTransaction> bts) {

		Element element = doc.createElement("business_transactions");

		for (BusinessTransaction bt : bts) {
			
			// Set BT_NAME
			Element bt_name = doc.createElement("bt_name");
			bt_name.appendChild(doc.createTextNode(bt.getName().trim()));
			element.appendChild(bt_name);
			// Set TIER
			Attr tier = doc.createAttribute("tier");
			tier.setValue(bt.getTierName().trim());
			bt_name.setAttributeNode(tier);

		}

		doc.getFirstChild().appendChild(element);

	}

	public void appendTiersData(Document doc, List<Tier> tiers) {

		Element element = doc.createElement("tiers");

		for (Tier tier : tiers) {

			// set tier_name to rest_metrics element
			Element tier_name = doc.createElement("tier");
			tier_name.appendChild(doc.createTextNode(tier.getName().trim()));
			element.appendChild(tier_name);

		}

		doc.getFirstChild().appendChild(element);

	}

	public void appendNodesData(Document doc, List<Node> nodes) {

		Element element = doc.createElement("nodes");

		for (Node node : nodes) {
		
			// set tier_name to rest_metrics element
			Element node_name = doc.createElement("node");
			node_name.appendChild(doc.createTextNode(Utils.scapes(node.getName()).trim()));
			element.appendChild(node_name);
			
		}

		doc.getFirstChild().appendChild(element);

	}
	
	public void appendExternalCallsData(Document doc,
			ArrayList<MetricItem> metricItems) {
		
		Element element = doc.createElement("external_calls");

		for (MetricItem metricItem : metricItems) {
		
			// set tier_name to rest_metrics element
			Element external_call_name = doc.createElement("external_call");
			external_call_name.appendChild(doc.createTextNode(Utils.scapes(metricItem.getName()).trim()));
			element.appendChild(external_call_name);
			
		}

		doc.getFirstChild().appendChild(element);
		
	}
	
	public Document initDocument() throws ParserConfigurationException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		return doc;
	}

	private void setUpTimeRange(Document doc, Element time_range, Long initFecha, Long finalFecha, String frequency) {

		SimpleDateFormat format1 = new SimpleDateFormat("yyyy.MM.dd.HH.mm");

		Element date_begin = doc.createElement("date");
		Element before = doc.createElement("since");
		before.appendChild(doc.createTextNode(format1.format(initFecha)));
		Element after = doc.createElement("to");
		after.appendChild(doc.createTextNode(format1.format(finalFecha)));
		date_begin.appendChild(before);
		date_begin.appendChild(after);

		Element frequencyTag = doc.createElement("frequency");
		frequencyTag.appendChild(doc.createTextNode(frequency));

		time_range.appendChild(date_begin);
		time_range.appendChild(frequencyTag);
	}

	public void createBeforeAfterElement(Document doc, String elementName, String beforeValue, String afterValue, Element element){

		Element baElement = doc.createElement(elementName.trim().replace(" ", "_"));

		Element before = doc.createElement("before");
		before.appendChild(doc.createTextNode(beforeValue));
		Element after = doc.createElement("after");
		after.appendChild(doc.createTextNode(afterValue));

		baElement.appendChild(before);
		baElement.appendChild(after);

		element.appendChild(baElement);

	}

	public void createElement(Document doc, String elementName, String Value, Element element){

		Element baElement = doc.createElement(Utils.scapes(elementName).trim().replace(" ", "_").replace("(ms)", ""));
		baElement.appendChild(doc.createTextNode(Value));
		element.appendChild(baElement);

	}

	// write the content into xml file
	public void saveAndClose(Document doc, String FILE_PATH) throws TransformerException{

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		
		DOMSource source = new DOMSource(doc);
		
		StreamResult result = new StreamResult(new File(FILE_PATH));

		transformer.transform(source, result);

	}

}