package indiv.park.network.exception;

public class NoHandlerFoundException extends RuntimeException {

	private static final long serialVersionUID = -7277569449878596070L;
	
	public NoHandlerFoundException() {
		super("등록된 핸들러가 존재하지 않습니다.");
	}
}
