package indiv.park.network.processor.exception;

public class NoProcessFoundException extends RuntimeException {
	
	private static final long serialVersionUID = -5584847835405984648L;

	public NoProcessFoundException() {
		super("등록된 프로세스가 존재하지 않습니다.");
	}
}
