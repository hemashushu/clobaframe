package org.archboy.clobaframe.query;

/**
 *
 * @author yang
 */
public class QueryException extends RuntimeException{

	private static final long serialVersionUID = 1L;

//	public QueryException(){
//		super("Object query exception.");
//	}

	public QueryException(String message) {
		super(message);
	}
	
	public QueryException(String message, Throwable cause) {
		super(message, cause);
	}
}
