package facility;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import exception.LogisticsException;


public class FacilityManager {

	private static Map<String, Facility> facilityMap =
			new HashMap<String, Facility>();
	
	public static void addFacility(Facility facility) {
		facilityMap.put(facility.getName(), facility);
	}
	
	public static boolean hasFacility(String name) {
		return facilityMap.containsKey(name);
	}
	
	public static Facility getFacility(String name) {
		return facilityMap.get(name);
	}
	
	public static Collection<Facility> getAllFacilities() {
		return facilityMap.values();
	}
	
	public static List<Facility> getFacilitiesHaveItem(String itemId) {
		List<Facility> list = new ArrayList<Facility>();
		for (Facility facility : facilityMap.values()) {
			if (facility.getInventory().hasItem(itemId)) {
				list.add(facility);
			}
		}
		return list;
	}
	
	public static void loadFacilityBasicInfo(String fileName) {
		try {
      XMLReader reader = XMLReaderFactory.createXMLReader();
      InputSource is = new InputSource(new FileInputStream(new File(fileName)));
      
      reader.setContentHandler(new FacilityBasicInfoParser());
      reader.parse(is);
    } catch (Exception e) {
      System.err.println("Exception occurred when loading facility basic information, details: " + e.getLocalizedMessage());
    }
	}
	
	public static void printStatus(PrintStream ps) {
		for (Facility facility : getAllFacilities()) {
			facility.printStatus(ps); 
		}
	}	
	
	public static void printOverallInventoryStatus(PrintStream ps) { //for troubleshooting use
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (Facility f : facilityMap.values()) {
			for(Map.Entry<String, Integer> entry : f.getInventory().getItems().entrySet()) {
				if (map.containsKey(entry.getKey())) {
					map.put(entry.getKey(), entry.getValue() + map.get(entry.getKey()));
				} else {
					map.put(entry.getKey(), entry.getValue());
				}
			}
		}
		ps.println(map);
	}
	
	public static void resetFacilities() {
		for (Facility f : facilityMap.values()) {
			((WarehouseFacility)f).reset();
		}
	}
	
	public static void loadFacilityInventoryInfo(String fileName) {
		try {
      XMLReader reader = XMLReaderFactory.createXMLReader();
      InputSource is = new InputSource(new FileInputStream(new File(fileName)));
      
      reader.setContentHandler(new FacilityInventoryParser());
      reader.parse(is);
    } catch (Exception e) {
      System.err.println("Exception occurred when loading facility inventory information, details: " + e.getLocalizedMessage());
    }
	}
	
	private static class FacilityBasicInfoParser extends DefaultHandler{
    
		private WarehouseFacility currFacility;
		private WarehouseFacility adjFacility;
		private String nodeValue;

    @Override
    public void startElement(String uri, String localName, String qName,
        Attributes atts) throws SAXException {
      if (localName.equals("facility")) {
      	String facilityName = atts.getValue("name");
      	currFacility = (WarehouseFacility)getFacility(facilityName);
      	if (currFacility == null) {
      		try {
	          currFacility = new WarehouseFacility(atts.getValue("name"));
          } catch (LogisticsException e) {
	          System.err.println(e.getLocalizedMessage());
          }
      		addFacility(currFacility);
      	}
      } else if (localName.equals("link")) {
      	String linkName = atts.getValue("name");
      	adjFacility = (WarehouseFacility)getFacility(linkName);
      	if (adjFacility == null) {
      		try {
	          adjFacility = new WarehouseFacility(linkName);
	          addFacility(adjFacility);
          } catch (LogisticsException e) {
          	System.err.println(e.getLocalizedMessage());
          }
      	}
      }
    }

    @Override
    public void endElement(String uri, String localName, String qName)
        throws SAXException {
    	
      try {
	      if (localName.equals("facilityRate")) {
	        currFacility.setRate(Integer.parseInt(nodeValue));
	      } else if (localName.equals("facilityCost")) {
	        currFacility.setCost(Integer.parseInt(nodeValue));
	      } else if (localName.equals("facilityLimit")) {
	        currFacility.setLimit(Integer.parseInt(nodeValue));
	      } else if (localName.equals("link")) {      	
	      	currFacility.addAdjacent(adjFacility, Integer.parseInt(nodeValue));
	      }
      } catch (Exception e) {
      	System.err.println(e.getMessage());
      }
    }
    
    public void characters(char[] ch, int start, int length)
        throws SAXException {
    	nodeValue = new String(ch, start, length).trim();
    }
	}
		
	private static class FacilityInventoryParser extends DefaultHandler{
    
		private WarehouseFacility currFacility;
		private String itemId;
		private String nodeValue;

    @Override
    public void startElement(String uri, String localName, String qName,
        Attributes atts) throws SAXException {
      if (localName.equals("facility")) {
      	currFacility = (WarehouseFacility)getFacility(atts.getValue("name"));
      	if (currFacility == null) {
      		System.err.println("Unknown facility when loading facility inventory information, name: " + atts.getValue("name"));
      	}
      } else if (localName.equals("item")) {
      	itemId = atts.getValue("ID");
      }
    }

    @Override
    public void endElement(String uri, String localName, String qName)
        throws SAXException {
      if (localName.equals("item") && currFacility != null) {
        try {
	        currFacility.getInventory().setItemQuantity(itemId, Integer.parseInt(nodeValue));
        } catch (Exception e) {
        	System.err.println(e.getMessage());
        }
      } 
    }
    
    public void characters(char[] ch, int start, int length)
        throws SAXException {
    	nodeValue = new String(ch, start, length).trim();
    }
	}
}
