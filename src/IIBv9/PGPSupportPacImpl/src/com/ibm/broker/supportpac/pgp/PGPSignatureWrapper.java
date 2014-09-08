package com.ibm.broker.supportpac.pgp;

import java.io.IOException;
import java.io.OutputStream;
import java.security.NoSuchProviderException;
import java.security.SignatureException;

import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPOnePassSignature;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.operator.PGPContentVerifierBuilderProvider;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentVerifierBuilderProvider;

/**
 * This class is a wrapper that handles both PGPSignature and
 * PGPOnePassSignature.
 * 
 * @version 1.0
 * @author Dipak K Pal (IBM) 
 * <br><br>
 * <b>Description:</b> This class is a wrapper that handles both PGPSignature and PGPOnePassSignature as an identical class
 */
public class PGPSignatureWrapper {

	PGPOnePassSignature sigOnePass;
	PGPSignature sigOldStyle;
	boolean isOnePass;

	/**
	 * 
	 * @param sigOnePass
	 */
	public PGPSignatureWrapper(PGPOnePassSignature sigOnePass) {
		this.sigOnePass = sigOnePass;
		isOnePass = true;
	}

	/**
	 * 
	 * @param sigOldStyle
	 */
	public PGPSignatureWrapper(PGPSignature sigOldStyle) {
		this.sigOldStyle = sigOldStyle;
		isOnePass = false;
	}

	/**
	 * 
	 * @param outStream
	 * @throws IOException
	 */
	public void encode(OutputStream outStream) throws IOException {
		if (isOnePass) {
			sigOnePass.encode(outStream);
		} else {
			sigOldStyle.encode(outStream);
		}
	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public byte[] getEncoded() throws IOException {
		if (isOnePass) {
			return sigOnePass.getEncoded();
		} else {
			return sigOldStyle.getEncoded();
		}
	}

	/**
	 * 
	 * @return
	 */
	public int getKeyAlgorithm() {
		if (isOnePass){
			return sigOnePass.getKeyAlgorithm();
		} else {
			return sigOldStyle.getKeyAlgorithm();
		}
	}

	/**
	 * 
	 * @return
	 */
	public int getHashAlgorithm() {
		if (isOnePass) {
			return sigOnePass.getHashAlgorithm();
		} else {
			return sigOldStyle.getHashAlgorithm();
		}
	}

	/**
	 * 
	 * @return
	 */
	public long getKeyID() {
		if (isOnePass) {
			return sigOnePass.getKeyID();
		} else {
			return sigOldStyle.getKeyID();
		}
	}

	/**
	 * 
	 * @return
	 */
	public long getSignatureType() {
		if (isOnePass){
			return sigOnePass.getKeyID();
		} else {
			return sigOldStyle.getKeyID();
		}
	}

	/**
	 * 
	 * @param pubKey
	 * @param provider
	 * @throws NoSuchProviderException
	 * @throws PGPException
	 */
	public void initVerify(PGPPublicKey pubKey, String provider) throws NoSuchProviderException, PGPException {
		PGPContentVerifierBuilderProvider verifierBuilderProvider = new JcaPGPContentVerifierBuilderProvider().setProvider(provider);
		if (isOnePass){
			sigOnePass.init(verifierBuilderProvider,pubKey);
		} else {
			sigOldStyle.init(verifierBuilderProvider,pubKey);
		}
	}

	/**
	 * Generate Signature
	 * @param b
	 * @throws SignatureException
	 */
	public void update(byte b) throws SignatureException {
		if (isOnePass){
			sigOnePass.update(b);
		} else {
			sigOldStyle.update(b);
		}
	}

	/**
	 * Generate Signature
	 * @param bytes
	 * @throws SignatureException
	 */
	public void update(byte[] bytes) throws SignatureException {
		if (isOnePass){
			sigOnePass.update(bytes);
		} else {
			sigOldStyle.update(bytes);
		}
	}

	/**
	 * Generate Signature
	 * @param bytes
	 * @param off
	 * @param len
	 * @throws SignatureException
	 */
	public void update(byte[] bytes, int off, int len) throws SignatureException {
		if (isOnePass){
			sigOnePass.update(bytes, off, len);
		} else {
			sigOldStyle.update(bytes, off, len);
		}
	}

	/**
	 * Verify Signature
	 * @param pgpSig
	 * @return
	 * @throws PGPException
	 * @throws SignatureException
	 */
	public boolean verify(PGPSignature pgpSig) throws PGPException, SignatureException {
		if (isOnePass){
			return sigOnePass.verify(pgpSig);
		} else {
			return sigOldStyle.verify();
		}
	}
}
