package order;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import processor.CostPriorOrderProcessor;
import processor.TimePriorOrderProcessor;
import exception.LogisticsException;

public class OrderManager {

	private static List<Order> orderList = new ArrayList<Order>();
	
	public static void addOrder(Order order) {
		orderList.add(order);
	}
	
	public static List<Order> getOrderList() {
		return orderList;
	}
	
	public static void processOrders() {
		for (Order order : orderList) {
			switch (order.getPriority()) {
			case TIME:
				TimePriorOrderProcessor.getInstance().process(order);
				break;
			case COST:
				CostPriorOrderProcessor.getInstance().process(order);
				break;
			}
		}
	}
	
	public static void printOrders(PrintStream ps) {
		ps.println();
		for (int i = 0; i < orderList.size(); i++) {
			Order order = orderList.get(i);
			ps.println("Order #" + (i+1));
			order.printOrder(ps);
			ps.println();
		}
	}
	
	public static void loadOrderInfo(String fileName) {

		try {	
			orderList.clear();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
	
			File xml = new File(fileName);
			if (!xml.exists()) {
				System.err.println("**** XML File '" + fileName + "' cannot be found");
				return;
			}
	
			Document doc = db.parse(xml);
			doc.getDocumentElement().normalize();
	
			NodeList orderEntries = doc.getDocumentElement().getChildNodes();
	
			for (int i = 0; i < orderEntries.getLength(); i++) {
				if (orderEntries.item(i).getNodeType() == Node.TEXT_NODE) {
					continue;
				}
	
				String entryName = orderEntries.item(i).getNodeName();
				if (!entryName.equals("Order")) {
					System.err.println("Unexpected node found: " + entryName);
					continue;
				}
		
				// Get a node attribute
				NamedNodeMap aMap = orderEntries.item(i).getAttributes();
				String orderId = aMap.getNamedItem("Id").getNodeValue();
		
				// Get a named nodes
				Element elem = (Element) orderEntries.item(i);
				String orderTime = elem.getElementsByTagName("Time").item(0).getTextContent();
				String orderDstAddress = elem.getElementsByTagName("Destination").item(0).getTextContent();
				String orderPriority = elem.getElementsByTagName("Priority").item(0).getTextContent();
				
				int iTime = -1;
				try {
					iTime = Integer.parseInt(orderTime) - 1;
				} catch (Exception e) {
					System.err.println("Invalid order time value: " + orderTime);
				}
				
				Order order = null;
				try {
					order = new Order(orderId, iTime, orderDstAddress, orderPriority);
				} catch (LogisticsException e) {
					System.err.println("Failed to initialize an order, error: " + e.getMessage());
					continue;
				}
				OrderManager.addOrder(order);
		
				// Get all nodes named "Item" - there can be 0 or more
				NodeList itemList = elem.getElementsByTagName("Item");
				for (int j = 0; j < itemList.getLength(); j++) {
					if (itemList.item(j).getNodeType() == Node.TEXT_NODE) {
						continue;
					}
		
					entryName = itemList.item(j).getNodeName();
					if (!entryName.equals("Item")) {
						System.err.println("Unexpected node found: " + entryName);
						continue;
					}
		
					// Get some named nodes
					elem = (Element) itemList.item(j);
					String itemId = elem.getElementsByTagName("Id").item(0).getTextContent();
					String itemQuantity = elem.getElementsByTagName("Quantity").item(0).getTextContent();
					int iQuantity = -1;
					try {
						iQuantity = Integer.parseInt(itemQuantity);
					} catch (Exception e) {
						System.err.println("Order item quantity value is not an integer: " + itemQuantity);
						continue;
					}
					try {
						order.items.add(new OrderItem(itemId, iQuantity));
						} catch (LogisticsException e) {
							System.err.println("Failed to add an order item, error: " + e.getMessage());
						}
				}
		
			}
		
	  } catch (ParserConfigurationException | SAXException | IOException | DOMException e) {
			System.err.println("Exception occurred when loading order information, details: " + e.getMessage());
		}

	}
	
}
