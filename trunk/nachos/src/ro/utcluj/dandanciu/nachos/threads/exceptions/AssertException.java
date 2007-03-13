package ro.utcluj.dandanciu.nachos.threads.exceptions;

/************************
 **
 ** AssertException:
 **
 ** This is an exception which (presumably) nobody will catch, and
 ** will therefore halt the program completely.  It's used largely for
 ** ASSERT purposes.
 **
 ** Richard Cobbe
 ** Comp421 Staff, Spring 1998
 **
 ** Copyright (c) 1998 Rice University.
 *************************/

public class AssertException extends RuntimeException {

  /**
	 * 
	 */
	private static final long serialVersionUID = 7075489687479352088L;

public AssertException() {
    super("Assert failed");
  }

  public AssertException(String msg) {
    super(msg);
  }
}
