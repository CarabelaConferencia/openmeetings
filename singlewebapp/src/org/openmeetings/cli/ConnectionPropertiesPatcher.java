package org.openmeetings.cli;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class ConnectionPropertiesPatcher {
	
	ConnectionProperties connectionProperties;
	
	public enum PatcherType {
		db2
		, derby
		, mysql
		, oracle
		, postgres
	}
	
	static ConnectionPropertiesPatcher getPatcher(String _dbType) {
		PatcherType dbType = PatcherType.valueOf(_dbType);
		ConnectionPropertiesPatcher patcher = null;
		switch (dbType) {
			case db2:
				patcher = new Db2Patcher();
				break;
			case mysql:
				patcher = new MysqlPatcher();
				break;
			case oracle:
				patcher = new OraclePatcher();
				break;
			case postgres:
				patcher = new PostgresPatcher();
				break;
			case derby:
			default:
				patcher = new DerbyPatcher();
				break;
		}
		return patcher;
	}
	
	public void patch(File srcXml, File destXml, String host, String port, String db, String user, String pass, ConnectionProperties connectionProperties) throws Exception {
		this.connectionProperties = connectionProperties;
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		//dbFactory.setNamespaceAware(true);
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(srcXml);
		
		XPath xPath = XPathFactory.newInstance().newXPath();
		XPathExpression expr = xPath.compile("/persistence/persistence-unit/properties/property[@name='openjpa.ConnectionProperties']");

		Element element = (Element)expr.evaluate(doc, XPathConstants.NODE);
		Attr val = element.getAttributeNode("value");
		val = patchAttribute(val, host, port, db, user, pass);
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		transformer.transform(source, new StreamResult(destXml));
	}
	
	protected Attr patchAttribute(Attr attr, String host, String port, String db, String user, String pass) {
		String[] tokens = attr.getValue().split(",");
		patchUserPassDriver(tokens, user, pass);
		patchDb(tokens, host, port, db);
		attr.setValue(StringUtils.join(tokens, ","));
		return attr;
	}

	protected void patchProp(String[] tokens, int idx, String name, String value) {
		String prop = tokens[idx].trim();
		if (prop.startsWith(name)) {
			prop = name + "=" + StringEscapeUtils.escapeXml(value);
			tokens[idx] = prop;
		}
	}
	
	protected void patchUserPassDriver(String[] tokens, String user,
			String pass) {
		String prop;
		for (int i = 0; i < tokens.length; ++i) {
			prop = getPropFromPersistence(tokens, i, "DriverClassName");
			if (prop != null)
				connectionProperties.setDriverName(prop);
			
			if (user != null) {
				patchProp(tokens, i, "Username", user);
				connectionProperties.setConnectionLogin(user);
			} else {
				prop = getPropFromPersistence(tokens, i, "Username");
				if (prop != null)
					connectionProperties.setConnectionLogin(prop);
			}
			
			if (pass != null) {
				patchProp(tokens, i, "Password", pass);
				connectionProperties.setConnectionPass(pass);
			} else {
				prop = getPropFromPersistence(tokens, i, "Password");
				if (prop != null)
					connectionProperties.setConnectionPass(prop);
			}
		}
	}
	
	protected String getPropFromPersistence(String[] tokens, int idx, String name){
		String prop = tokens[idx].trim();
		if (prop.startsWith(name)) {
			//From "Username=root" getting only "root"
			return prop.substring(prop.indexOf("=") + 1);
		}
		return null;
	}
	
	protected abstract void patchDb(String[] tokens, String host, String port, String db);
}
