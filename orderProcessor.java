package processor;

import order.Order;

public interface OrderProcessor {

	public Priority getPriority();
	
	public void process(Order order);
}
