
package com.ibm.broker.supportpac.pgp;

/**
 * Java class to hold Decryption result info.
 * @version 1.0
 * @author Dipak K Pal
 * <br><br>
 * <b>Description:</b>
 * Java class to hold Decryption result info.
 */
public class PGPDecryptionResult {

    private String decryptFileName = "";
    private boolean isSigned = false;
    private PGPPublicKeyWrapper signee = null;
    private boolean isSignatureValid = false;
    private Exception signatureException = null;
    private String decryptedText;
    private boolean isIntegrityProtected = false;
    private boolean integrityCheckFailure = false;

    public String getDecryptFileName() {
        return decryptFileName;
    }

    public void setDecryptFileName(String decryptFileName) {
        this.decryptFileName = decryptFileName;
    }

    public boolean isIsSigned() {
        return isSigned;
    }

    public void setIsSigned(boolean isSigned) {
        this.isSigned = isSigned;
    }

    public PGPPublicKeyWrapper getSignee() {
        return signee;
    }

    public void setSignee(PGPPublicKeyWrapper signee) {
        this.signee = signee;
    }

    public boolean isIsSignatureValid() {
        return isSignatureValid;
    }

    public void setIsSignatureValid(boolean isSignatureValid) {
        this.isSignatureValid = isSignatureValid;
    }

    public Exception getSignatureException() {
        return signatureException;
    }

    public void setSignatureException(Exception signatureException) {
        this.signatureException = signatureException;
    }

    public String getDecryptedText() {
        return decryptedText;
    }

    public void setDecryptedText(String decryptedText) {
        this.decryptedText = decryptedText;
    }

	public boolean isSigned() {
		return isSigned;
	}

	public void setSigned(boolean isSigned) {
		this.isSigned = isSigned;
	}

	public boolean isSignatureValid() {
		return isSignatureValid;
	}

	public void setSignatureValid(boolean isSignatureValid) {
		this.isSignatureValid = isSignatureValid;
	}

	public boolean isIntegrityProtected() {
		return isIntegrityProtected;
	}

	public void setIntegrityProtected(boolean isIntegrityProtected) {
		this.isIntegrityProtected = isIntegrityProtected;
	}

	public boolean isIntegrityCheckFailure() {
		return integrityCheckFailure;
	}

	public void setIntegrityCheckFailure(boolean integrityCheckFailure) {
		this.integrityCheckFailure = integrityCheckFailure;
	}    
    
}
