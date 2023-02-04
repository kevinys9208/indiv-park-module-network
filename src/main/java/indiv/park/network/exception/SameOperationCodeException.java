package indiv.park.network.exception;

public class SameOperationCodeException extends RuntimeException {

	private static final long serialVersionUID = -6587864311573560044L;

	public SameOperationCodeException() {
		super("동일한 값의 Operation Code가 존재합니다.");
	}
}
