package exception;

public class UnknownFacilityException extends LogisticsException{

  private static final long serialVersionUID = 1L;

	public UnknownFacilityException(String name) {
		super("Unknown facility of name: " + name);
	}
}
