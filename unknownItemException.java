package exception;

public class UnknownItemException extends LogisticsException{

  private static final long serialVersionUID = 1L;

	public UnknownItemException(String name) {
		super("Unknown item of name: " + name);
	}
}
