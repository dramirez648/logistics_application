package exception;

public class InvalidParamException extends LogisticsException{

  private static final long serialVersionUID = 1L;

	public InvalidParamException(String msg) {
		super(msg);
	}
}
