

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import order.OrderManager;
import facility.Catalog;
import facility.FacilityManager;

public class Main {
	public static void main(String[] args) {
		Catalog.loadCatalogInfo("resources/catalog.xml");
		FacilityManager.loadFacilityBasicInfo("resources/facilityNetwork.xml");
		FacilityManager.loadFacilityInventoryInfo("resources/facilityInventory.xml");
		
		boolean printToConsole = false;
		
		PrintStream ps = System.out;
		if (!printToConsole) {
	    try {
		    ps = new PrintStream(new FileOutputStream(new File("facilityStatus.txt")));
	    } catch (FileNotFoundException e) {}
		}
		
		OrderManager.loadOrderInfo("resources/order.xml");
		OrderManager.processOrders();		
		OrderManager.printOrders(ps);
		
		FacilityManager.printStatus(ps);
		
		ps.println("==== Reset data, load cost prioritized orders and print the final result ====");
		FacilityManager.resetFacilities();
		OrderManager.loadOrderInfo("resources/order-cost.xml");
		OrderManager.processOrders();		
		OrderManager.printOrders(ps);
		
		FacilityManager.printStatus(ps);

		if (!printToConsole) {
			ps.close();
		}
	}
}
