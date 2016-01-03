package facility;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import exception.InvalidParamException;
import exception.LogisticsException;

public class WarehouseFacility implements Facility{

	private String name;
	private int rate;
	private int cost;
	private int limit;

	private Map<Facility, Integer> adjacentFacilityMap;
	private Inventory inventory;
	private Schedule schedule;
	
	private static final int MIN_DISPLAY_DAYS = 20;
	private static final int DISPLAY_WIDTH = 20;
	
	public WarehouseFacility(String name) throws LogisticsException {
		setName(name);
		adjacentFacilityMap = new HashMap<Facility, Integer>();
		inventory = new Inventory();
		schedule = new Schedule();
		limit = Integer.MAX_VALUE;
	}
	
	private void setName(String name) throws LogisticsException {
		if (name == null) {
			throw new InvalidParamException("Null facility name when constructing a facility");
		}
		this.name = name;
	}
	
	public void setRate(int rate) throws LogisticsException {
		if (rate <= 0) {
			throw new InvalidParamException("Non-positive facility rate: " + rate);
		}
		this.rate = rate;
		schedule.setDailyRate(rate);
	}
	
	public void setCost(int cost) throws LogisticsException {
		if (cost <= 0) {
			throw new InvalidParamException("Non-positive facility cost: " + cost);
		}
		this.cost = cost;
	}
	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) throws LogisticsException {
		if (limit <= 0) {
			throw new InvalidParamException("Non-positive facility limit: " + limit);
		}
		this.limit = limit;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getRate() {
		return rate;
	}

	@Override
	public Map<Facility, Integer> getAdjacencies() {
		return adjacentFacilityMap;
	}
	
	public void addAdjacent(Facility adjacent, int distance) throws LogisticsException {
		if (adjacent == null) {
			throw new InvalidParamException("Null facility instance when adding a directly reachable facility");
		}
		if (distance <= 0) {
			throw new InvalidParamException("Invalid distance value: " + distance);
		}
		adjacentFacilityMap.put(adjacent, distance);
	}
	
	public boolean equals(Object o) {
		if (o == null || !(o instanceof WarehouseFacility)) {
			return false;
		}
		return name.equals(((WarehouseFacility)o).name);
	}
	
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public int getCost() {
	  return cost;
	}

	@Override
	public Inventory getInventory() {
		return inventory;
	}

	@Override
	public int getDistanceToAdjFacility(Facility target) {
		return adjacentFacilityMap.get(target);
	}

	@Override
	public Schedule getSchedule() {
		return schedule;
	}
	
	public String toString() {
		return name;
	}
	
	public void reset() {
		inventory.reset();
		schedule.reset();
	}
	
	public void printStatus(PrintStream ps) {
		
		if (schedule.getLastBookDay() < 0) return;
		
		ps.println("----------------------------------------------------------------------------------");
		ps.println(name);
		
		ps.print("Direct Links: ");
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<Facility, Integer> entry : adjacentFacilityMap.entrySet()) {
			sb.append("; " + entry.getKey().getName() + " (" + 
					String.format("%.2f", 1.0f * entry.getValue() / FacilityNetwork.getTravelHoursPerDay() / FacilityNetwork.getTravelSpeed()) + ")");
		}
		if (sb.length() > 0) {
			ps.println(sb.toString().substring(2));
		}
		
		Map<String, Integer> availableItems = new HashMap<String, Integer>();
		for (Map.Entry<String, Integer> entry : inventory.getItems().entrySet()) {
			if (entry.getValue() == 0) continue;
			availableItems.put(entry.getKey(), entry.getValue());
		}

		ps.println("\nActive Inventory: ");
		String separator = "      ";
		if (availableItems.size() > 0) {
			ps.format("  %-12s%-12s", "Item ID", "Quantity");
		}
		
		if (availableItems.size() > 1) {
			ps.format(separator + "%-12s%-12s", "Item ID", "Quantity");
		}
		
		if (availableItems.size() > 2) {
			ps.format(separator + "%-12s%-12s", "Item ID", "Quantity");
		}
		
		int itemIdx = 0;
		for (Map.Entry<String, Integer> entry : availableItems.entrySet()) {
			if (itemIdx % 3 == 0) {
				ps.print("\n  ");
			} else {
				ps.print(separator);
			}
			ps.print(String.format("%-12s%-12d", entry.getKey(), entry.getValue()));
			itemIdx++;			
		}
		if (availableItems.size() > 0) {
			ps.println();		
		}
		ps.println();		

		ps.print("Depleted Items: ");
		Set<String> itemSet = inventory.getDepletedItems();
		sb.delete(0, sb.length());
		for (String item : itemSet) {
			sb.append(", " + item);
		}
		if (sb.length() > 0) {
			ps.print(sb.toString().substring(2));
		}
		ps.println("\n");
		
		ps.println("Schedule:");
		int displayDayCount = Math.max(schedule.getLastBookDay(), MIN_DISPLAY_DAYS);
		int rows = displayDayCount / DISPLAY_WIDTH + (displayDayCount % DISPLAY_WIDTH == 0 ? 0 : 1);
		for (int idx = 0; idx < rows; idx++) {
			int col = idx < rows ? DISPLAY_WIDTH : displayDayCount % DISPLAY_WIDTH;
			ps.print("  " + String.format("%12s", "Day "));
			for (int i = 0; i < col; i++) {
				ps.format("%-4d", idx * DISPLAY_WIDTH + i + 1);
			}
			ps.println();
			ps.print("  " + String.format("%12s", "Available "));
			for (int i = 0; i < col; i++) {
				ps.format("%-4d", rate - schedule.getBookedAmount(idx * DISPLAY_WIDTH + i));
			}
			if (idx < rows) ps.println();
			ps.println();
		}		
	}

	@Override
  public int getMaxQuantityToProvide(String itemId) {
	  return Math.min(limit, inventory.getItemQuantity(itemId));
  }
}
