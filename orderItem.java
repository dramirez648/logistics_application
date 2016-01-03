package order;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import exception.InvalidParamException;
import exception.LogisticsException;
import exception.UnknownItemException;
import facility.Catalog;

public class OrderItem {

	private String itemId;
	private int quantity;
	
	private List<OrderItemProcessRecord> processRecords = new ArrayList<OrderItemProcessRecord>();
	
	private int totalCost = 0;
	private int firstDay = Integer.MAX_VALUE;
	private int endDay = 0;
	private int processedQuantity = 0;
	
	public OrderItem(String itemId, int quantity) throws LogisticsException{
		setItemId(itemId);
		setItemQuantity(quantity);
	}
	
	private void setItemId(String itemId) throws LogisticsException {
		if (itemId == null) {
			throw new InvalidParamException("Bad order item id: " + itemId);
		}
		if (!Catalog.hasItem(itemId)) {
			throw new UnknownItemException(itemId);
		}
		this.itemId = itemId;
	}
	
	private void setItemQuantity(int quantity) throws InvalidParamException {
		if (quantity <= 0) {
			throw new InvalidParamException("Bad order item quantity: " + quantity);
		}
		this.quantity = quantity;
	}
	
	public void addItemProcessRecord(OrderItemProcessRecord record) {
		processRecords.add(record);		
	}
	
	public void processCompleted() {
		for (OrderItemProcessRecord rec : processRecords) {
			processedQuantity += rec.getQuantity();			
			firstDay = Math.min(firstDay, rec.getTravelDays() + rec.getProcessEndDay());
			endDay = Math.max(endDay, rec.getTravelDays() + rec.getProcessEndDay());
			totalCost += rec.getCost();
		}
		if (firstDay == Integer.MAX_VALUE)  firstDay = -1;
		if (endDay == 0) endDay = -1;
		
	}
	
	public List<OrderItemProcessRecord> getItemProcessRecords(OrderItemProcessRecord record) {
		return processRecords;
	}
	
	public String getItemId() {
		return itemId;
	}
	
	public int getQuantity() {
		return quantity;
	}
	
	public int getTotalCost() {
		return totalCost;
	}
	
	public int getNumOfResources() {
		return processRecords.size();
	}
	
	public int getTravelFirstDay() {
		return firstDay;
	}
	
	public int getTravelLastDay() {
		return endDay;
	}
	
	public int getProcessedQuantity() {
		return processedQuantity;
	}
	
	public void printDetails(PrintStream ps) {
		for (OrderItemProcessRecord rec : processRecords) {
			ps.format(" Name: %12s (%d of %d)\n", rec.getFacility(), rec.getQuantity(), quantity);
			ps.format("   Cost: %d\n",rec.getCost());
			ps.format("     %-25s %.2f\n", "Average Item Cost:", 1.0 * rec.getCost() / rec.getQuantity());
			ps.format("     %-25s Day " + (rec.getProcessStartDay() + 1) + "\n", "Processing Start:");
			ps.format("     %-25s Day " + (rec.getProcessEndDay() + 1) + "\n", "Processing End:");
			ps.format("     %-25s %.2f\n", "Actual Process Days:", rec.getActualProcessDay());
			ps.format("     %-25s Day " + (rec.getProcessEndDay() + (rec.getTravelDays() == 0 ? 0 : 1) + 1) + "\n", "Travel Start:");
			ps.format("     %-25s Day " + (rec.getProcessEndDay() + rec.getTravelDays() + 1) + "\n", "Travel End:");
			ps.format("     %-25s Day " + (rec.getProcessEndDay() + rec.getTravelDays() + 1) + "\n", "Arrival:");
			ps.println();
		}
	}
}
