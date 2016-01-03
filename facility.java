package facility;

import java.io.PrintStream;
import java.util.Map;

import exception.LogisticsException;

public interface Facility {
	public String getName();
	
	public int getRate();
	
	public int getCost();
	
	public Map<Facility, Integer> getAdjacencies();
	
	public int getDistanceToAdjFacility(Facility target);
	
	public void addAdjacent(Facility adjacent, int distance) throws LogisticsException;
	
	public Inventory getInventory();
	
	public Schedule getSchedule();
	
	public int getLimit();
	
	public void printStatus(PrintStream ps);
	
	public int getMaxQuantityToProvide(String itemId);
}
