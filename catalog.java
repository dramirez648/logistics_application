package facility;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class Catalog {

	private static Map<String, Integer> catalog = new HashMap<String, Integer>();
	
	public static int getItemPrice(String item) {
		return catalog.get(item);
	}
	
	public static boolean hasItem(String item) {
		return catalog.containsKey(item);
	}
	
	public static void addItem(String name, int price) {
		catalog.put(name, price);
	}
	
	public static void loadCatalogInfo(String fileName) {
		try {
      XMLReader reader = XMLReaderFactory.createXMLReader();
      InputSource is = new InputSource(new FileInputStream(new File(fileName)));
      
      reader.setContentHandler(new DefaultHandler() {
      	public void startElement(String uri, String localName, String qName,
            Attributes atts) throws SAXException {
          if (localName.equals("item")) {
          	addItem(atts.getValue("name"), Integer.parseInt(atts.getValue("price")));
          }
        }
      });
      reader.parse(is);
    } catch (Exception e) {
      System.err.println("Failed to load catalog information, error: " + e.getLocalizedMessage());
    }
	}
}
