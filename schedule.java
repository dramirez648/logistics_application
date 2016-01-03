package facility;

import java.util.TreeMap;

import exception.InvalidParamException;
import exception.LogisticsException;

public class Schedule {

	private TreeMap<Integer, Integer> bookMap = new TreeMap<Integer, Integer>();
	
	private int dailyRate = 0;
	
	public Schedule() {
	}
	
	public Schedule(int dailyRate) throws LogisticsException{
		setDailyRate(dailyRate);
	}
	
	public void setDailyRate(int dailyRate) throws LogisticsException {
		if (dailyRate <= 0) {
			throw new InvalidParamException("Non-positive daily rate: " + dailyRate);
		}
		this.dailyRate = dailyRate;
	}
		
	public TreeMap<Integer, Integer> getBookInfo() {
		return bookMap;
	}
	
	public void book(int day, int amount) throws LogisticsException{
		if (amount < 0) {
			throw new InvalidParamException("Negative book amount: " + amount);
		}
		bookMap.put(day, amount);
	}
	
	public int getLastBookDay() {
		if (bookMap.size() > 0)
			return bookMap.lastKey();
		else
			return -1;
	}
	
	public int getBookedAmount(int day) {
		Integer bookAmount = bookMap.get(day);
		if (bookAmount == null) return 0;
		else return bookAmount;
	}
	
	public TreeMap<Integer, Integer> tryToBook(int startDay, int total, boolean bookNow){
		TreeMap<Integer, Integer> result = new TreeMap<Integer, Integer>();
		for (int i = 0; total > 0; i++) {
			int booked = getBookedAmount(startDay + i);
			if (booked < dailyRate) {
				int toBook = Math.min(total, dailyRate - booked);
				total -= toBook;
				result.put(startDay + i, toBook);
				try {
					if (bookNow) {
						book(startDay + i, booked + toBook);
					}
				} catch (LogisticsException e) {} //shall never happen
			} else {
				continue;
			}
		}
		
		return result;
	}
	
	public void reset() {
		bookMap.clear();
	}
}
