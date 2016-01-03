package order;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import exception.InvalidParamException;
import exception.LogisticsException;
import exception.UnknownFacilityException;
import facility.Facility;
import facility.FacilityManager;
import processor.Priority;

public class Order {
	private String id;
	private int orderTime;
	private String dstFacilityName;
	private Priority priority;
	List<OrderItem> items;
	
	private static NumberFormat nf;
	static {
		nf = NumberFormat.getCurrencyInstance();
		nf.setMaximumFractionDigits(0);
		DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) nf).getDecimalFormatSymbols();
		decimalFormatSymbols.setCurrencySymbol("$");
		((DecimalFormat) nf).setDecimalFormatSymbols(decimalFormatSymbols);
	}
	
	public Order(String id, int orderTime, String destFacilityName, String priorityStr) throws LogisticsException {
		setOrderId(id);
		setOrderTime(orderTime);
		setDstFacility(destFacilityName);
		setPriority(priorityStr);		
		items = new ArrayList<OrderItem>();
	}
	
	private void setOrderId(String orderId) throws InvalidParamException {
		if (orderId == null) {
			throw new InvalidParamException("Bad order id value: " + id);
		}
		this.id = orderId;
	}
	
	private void setOrderTime(int orderTime) throws InvalidParamException {
		if (orderTime < 0) {
			throw new InvalidParamException("Bad order time value: " + id);
		}
		this.orderTime = orderTime;
	}
	
	private void setDstFacility(String facilityName) throws UnknownFacilityException {
		Facility destFacility = FacilityManager.getFacility(facilityName);
		if (destFacility == null) {
			throw new UnknownFacilityException(facilityName);
		}
		dstFacilityName = facilityName;
	}
	
	private void setPriority(String priorityStr) throws InvalidParamException {
		this.priority = Priority.from(priorityStr);
		if (priority == null) {
			throw new InvalidParamException("Bad priority value: " + priorityStr);
		}
	}

	public String getId() {
		return id;
	}

	public int getOrderTime() {
		return orderTime;
	}

	public String getDstFacilityName() {
		return dstFacilityName;
	}

	public Priority getPriority() {
		return priority;
	}

	public List<OrderItem> getItems() {
		return items;
	}
	
	public int getTotalCost() {
		int totalCost = 0;
		for (OrderItem item : items) {
			totalCost += item.getTotalCost();
		}
		return totalCost;
	}
	
	public int getFirstDeliveryDay() {
		int result = Integer.MAX_VALUE;
		for (OrderItem item : items) {
			result = Math.min(item.getTravelFirstDay(), result);
		}
		
		return orderTime + result;
	}
	
	public int getLastDeliveryDay() {

		int result = 0;
		for (OrderItem item : items) {
			result = Math.max(item.getTravelLastDay(), result);
		}
		
		return orderTime + result;
	}

	public void printOrder(PrintStream ps) {
		ps.format("%-16s%-15s\n", "* Order Id:", id);
		ps.format("%-16s%-15s\n", "* Order Time:",  "Day " + (orderTime + 1));
		ps.format("%-16s%-15s\n", "* Destination:", dstFacilityName);
		ps.format("%-16s%-15s\n", "* A Priority: ", priority);
		ps.println("* A List of Order Items: ");
		for (OrderItem item : items) {
			ps.println(String.format("   - Item ID: %-10s Quantity: %5s", item.getItemId() + ",", item.getQuantity()));
		}
		
		ps.println("\nProcessing Solution: ");
		ps.println("Order Id: " + id);
		ps.format("%-20s%-15s\n", "* Destination:", dstFacilityName);
		ps.format("%-20s%-15s\n", "* Priority:", priority);
		ps.format("%-20s%-15s\n", "* Total Cost:", nf.format(getTotalCost()));
		ps.format("%-20s%-15s\n", "* 1st Delivery Day:", getFirstDeliveryDay() + 1);
		ps.format("%-20s%-15s\n", "* Last Delivery Day:", getLastDeliveryDay() + 1);
		ps.println("* Order Items: ");
		ps.format("  %-12s%-12s%-12s%-15s%-12s%-12s\n", "Item ID", "Quantity", "Cost", "Num. Sources", "First Day", "Last Day");
		for (OrderItem item : items) {
			ps.format("  %-12s%-12d%-12s%-15d%-12d%-12d\n", 
					item.getItemId(), 
					item.getProcessedQuantity(), 
					nf.format(item.getTotalCost()), 
					item.getNumOfResources(),
					item.getTravelFirstDay() + 1,
					item.getTravelLastDay() + 1);
			
			//item.printDetails(ps);
		}
	}

}
