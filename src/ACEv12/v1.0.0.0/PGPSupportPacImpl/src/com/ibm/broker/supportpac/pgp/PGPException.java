package com.ibm.broker.supportpac.pgp;

/**
 * PGP custom Exception class.
 * @version 1.0
 * @author Dipak K Pal (IBM)
 * <br><br>
 * <b>Description:</b>
 * PGP custom Exception class
 *
 */
public class PGPException extends Exception {

	private static final long serialVersionUID = 1467346734673647L;

	public PGPException() {
	}

	public PGPException(String arg0) {
		super(arg0);
	}

	public PGPException(Throwable arg0) {
		super(arg0);
	}

	public PGPException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
