package order;

import facility.Catalog;
import facility.Facility;
import facility.FacilityNetwork;

public class OrderItemProcessRecord {

	private Facility facility;
	private String itemId;
	private int quantity;
	private int processStartDay;
	private int processEndDay;
	private int travelDays;
	private double actualProcessDay;
	
	public OrderItemProcessRecord(String itemId, int quantity, Facility facility, double actualProcessDay, int processStartDay, int processEndDay,
			int travelDays) {
		this.itemId = itemId;
		this.quantity = quantity;
		this.facility = facility;
		this.actualProcessDay = actualProcessDay;
		this.processStartDay = processStartDay;
		this.processEndDay = processEndDay;
		this.travelDays = travelDays;
	}

	public Facility getFacility() {
		return facility;
	}
	
	public double getActualProcessDay() {
		return actualProcessDay;
	}

	public int getProcessStartDay() {
		return processStartDay;
	}

	public int getProcessEndDay() {
		return processEndDay;
	}

	public int getTravelDays() {
		return travelDays;
	}
	
	public int getQuantity() {
		return quantity;
	}
	
	public int getCost() {
		return Catalog.getItemPrice(itemId) * quantity 
				+ (int)(actualProcessDay * facility.getCost()) 
				+ travelDays * FacilityNetwork.DEFAULT_TRANSPORT_COST;
	}
	
}
