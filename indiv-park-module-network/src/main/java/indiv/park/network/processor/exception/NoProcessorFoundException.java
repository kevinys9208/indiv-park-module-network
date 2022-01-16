package indiv.park.network.processor.exception;

public class NoProcessorFoundException extends RuntimeException {
	
	private static final long serialVersionUID = -5584847835405984648L;

	public NoProcessorFoundException() {
		super("등록된 프로세서가 존재하지 않습니다.");
	}
}
