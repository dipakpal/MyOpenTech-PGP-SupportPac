package com.ibm.broker.supportpac.pgp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Provider;
import java.security.SignatureException;
import java.util.Iterator;

import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPOnePassSignatureList;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyEncryptedData;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSignatureList;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.bouncycastle.openpgp.operator.PBESecretKeyDecryptor;
import org.bouncycastle.openpgp.operator.PublicKeyDataDecryptorFactory;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPDigestCalculatorProviderBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyDataDecryptorFactoryBuilder;

/**
 * Decryption and Signature validation.
 * @version 1.0
 * @author Dipak K Pal (IBM)
 * <br><br>
 * <b>Description:</b>
 * Decryption and Signature validation.
 */
public class PGPDecrypter {

	/**
	 * Decrypt Plain Text
	 * @param cipherText - encrypted text
	 * @param passPhrase - PGP Secret key passphrase
	 * @return PGPDecryptionResult
	 * @throws PGPException
	 */
    public static PGPDecryptionResult decryptUTF8Text(String cipherText, String passPhrase) throws PGPException {
    	PGPDecryptionResult res = null;
    	try {
    		// Get PGP Keyring
    		PGPKeyRing pgpKeyRing = PGPEnvironment.getDefaultPGPKeyRing();
			ByteArrayInputStream in = new ByteArrayInputStream(cipherText.getBytes("UTF8"));
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			
			if(passPhrase == null){
    			passPhrase = "";
    		}
			
			char[] passwd = passPhrase.toCharArray();
			res = decrypt(in, out, passwd, pgpKeyRing);
			res.setDecryptedText(out.toString("UTF8"));
		} catch (Exception e) {
			throw new PGPException(e.getMessage());
		}
		return res;
    }

    /**
     * Decryption/Signature validation with specified key repository
     * @param in
     * @param passPhrase
     * @param out
     * @return PGPDecryptionResult
     * @throws PGPException
     */
    public static PGPDecryptionResult decrypt(InputStream in, OutputStream out, String passPhrase, String pgpKeyRepositoryName)
	throws PGPException {

    	PGPDecryptionResult decryptionRes = null;    	

    	try {
    		// Get PGP Keyring
    		PGPKeyRing pgpKeyRing = PGPEnvironment.getPGPKeyRing(pgpKeyRepositoryName);
    		
    		if(passPhrase == null){
    			passPhrase = "";
    		}
    		
    		char[] passwd = passPhrase.toCharArray();
			decryptionRes = decrypt(in, out, passwd, pgpKeyRing);
		} catch (Exception e) {
			throw new PGPException(e.getMessage());
		}
		return decryptionRes;
    }
    
    /**
     * Decryption/Signature validation with default key repository
     * @param in
     * @param out
     * @param passPhrase
     * @return PGPDecryptionResult
     * @throws PGPException
     */
    public static PGPDecryptionResult decrypt(InputStream in, OutputStream out, String passPhrase) throws PGPException {

    	PGPDecryptionResult decryptionRes = null;    	

    	try {
    		// Get PGP Keyring
    		PGPKeyRing pgpKeyRing = PGPEnvironment.getDefaultPGPKeyRing();
    		
    		if(passPhrase == null){
    			passPhrase = "";
    		}
    		
    		char[] passwd = passPhrase.toCharArray();
			decryptionRes = decrypt(in, out, passwd, pgpKeyRing);
		} catch (Exception e) {
			throw new PGPException(e.getMessage());
		}
		return decryptionRes;
    }

    /**
     * Decrypt/Validate Signature
     * @param in - Input Stream
     * @param out - Output stream
     * @return PGPDecryptionResult
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
	private static PGPDecryptionResult decrypt(InputStream in, OutputStream out, char[] passwd, PGPKeyRing pgpKeyRing) throws Exception {
    	
    	Provider provider = PGPJavaUtil.getProvider("BC");
    	
    	PGPDecryptionResult decryptionRes = new PGPDecryptionResult();
    	
		PGPPublicKeyEncryptedData encryptedPublicKey = null;
		
		// Get decoded input stream
		in = PGPUtil.getDecoderStream(in);

		PGPObjectFactory pgpF = new JcaPGPObjectFactory(in);
		PGPEncryptedDataList pgpEncryptedDataList;
		
		Object pgpObject = null;
		try {
			pgpObject = pgpF.nextObject();
		} catch (Exception e) {
			throw new RuntimeException("Can not recognize input data. Input data might be corrupted or not encrypted correctly. Error detail: "+e.getMessage());
		}

		if (pgpObject == null){
		    throw new RuntimeException("Can not recognize input data. Input data might be corrupted or not encrypted correctly");
		}

		// First object can be a PGP marker packet.
		if (pgpObject instanceof PGPEncryptedDataList){
		    pgpEncryptedDataList = (PGPEncryptedDataList)pgpObject;
		} else {
		    pgpEncryptedDataList = (PGPEncryptedDataList) pgpF.nextObject();
		}

		// Find the secret key
		Iterator encObjects = pgpEncryptedDataList.getEncryptedDataObjects();
		if (!encObjects.hasNext()){
		    throw new RuntimeException("Input does not contain any encrypted data");
		}

		PGPPrivateKey pgpPrivateKey = null;
		PGPSecretKey secretKey = null;
		
		long keyID = -1L;
		String errMsg = "";
		while (encObjects.hasNext()) {
		    // Find a key that matches our private key
		    Object obj = encObjects.next();
		    if (!(obj instanceof PGPPublicKeyEncryptedData)){
		        continue;
		    }
		    
		    PGPPublicKeyEncryptedData encData = (PGPPublicKeyEncryptedData) obj;
		    keyID = encData.getKeyID();		    
		    secretKey = pgpKeyRing.getPrivateKeyByID(keyID);
		    
		    //If the public key id used does not match any in the private ring, try next
		    if(secretKey == null){
		    	errMsg = errMsg + "\n" + "Private key [0x" + Integer.toHexString((int)keyID).toUpperCase() + "] " +
		    			"with subkey Id [0x" + Long.toHexString(keyID).toUpperCase() + "] not found at private key repository.";
		        continue;
		    }

		    // Extract the private key from PGPKeyRing
		    try {
		    	PBESecretKeyDecryptor decryptorFactory = 
		    			new JcePBESecretKeyDecryptorBuilder(new JcaPGPDigestCalculatorProviderBuilder().setProvider(provider).build()).setProvider(provider).build(passwd);
		        pgpPrivateKey = secretKey.extractPrivateKey(decryptorFactory);
		    } catch(Exception ex) {
		    	errMsg = errMsg + "\n" + "Can not extract a suitable private key [0x" + Integer.toHexString((int)keyID).toUpperCase() + "] " +
		    			"with subkey Id [0x" + Long.toHexString(keyID).toUpperCase() + "]: Error: " +ex.getMessage();
		    	pgpPrivateKey = null;
		    }

		    if(pgpPrivateKey != null){
		        encryptedPublicKey = encData;
		        break;
		    }
		}
		
		// Throw exception is Private Key is not found
		if (pgpPrivateKey == null) {
			String message = "A suitable private key not found at Key Repository [" + pgpKeyRing.getRepositoryName() + "]. Verify the key repository and/or passphrase.";
			
			if(!"".equals(errMsg)){
				message = message + "\nError message: " + errMsg;
			}
			
		    throw new IllegalArgumentException(message);
		}

		PublicKeyDataDecryptorFactory dataDecryptorFactory = 
				new JcePublicKeyDataDecryptorFactoryBuilder().setProvider(provider).setContentProvider(provider).build(pgpPrivateKey);
		InputStream clear = encryptedPublicKey.getDataStream(dataDecryptorFactory);

		PGPObjectFactory plainFact = new JcaPGPObjectFactory(clear);

		Object message = plainFact.nextObject();
		Object sigLiteralData = null;
		PGPObjectFactory pgpFact = null;
		boolean isCompressed = false;
		
		// Check if input data is compressed
		if (message instanceof PGPCompressedData) {
		    PGPCompressedData cData = (PGPCompressedData) message;
		    pgpFact = new JcaPGPObjectFactory(cData.getDataStream());
		    message = pgpFact.nextObject();
		    if (message instanceof PGPOnePassSignatureList){
		        sigLiteralData = pgpFact.nextObject();
		    }
		    isCompressed = true;
		}

		if (message instanceof PGPLiteralData) {
		    //Message is just encrypted
		    processLiteralData((PGPLiteralData) message, out, null);
		} else if (message instanceof PGPOnePassSignatureList) {

			if(!isCompressed){
	        	sigLiteralData = plainFact.nextObject();
	        }

		    //Message is signed and encrypted with OnePassSignature
		    decryptionRes.setIsSigned(true);

		    PGPSignatureWrapper sigWrap = new PGPSignatureWrapper(((PGPOnePassSignatureList) message).get(0));

		    PGPPublicKey pubKey = pgpKeyRing.getPublicKeyByID(sigWrap.getKeyID());
		    
		    // If PublicKey is not available, then skip Signature validation and store exception message, but continue decryption process
		    if (pubKey == null) {
		        decryptionRes.setSignatureException(
		        		new RuntimeException("Cannot find the public key [0x" + Integer.toHexString((int)sigWrap.getKeyID()).toUpperCase() +
		        				"] with subkey Id [0x" + Long.toHexString(sigWrap.getKeyID()).toUpperCase() +
		        				"] in the PublicKey Repository [" + pgpKeyRing.getRepositoryName()+ "]"));
		        
		        // Decrypt without checking signature
		        processLiteralData((PGPLiteralData) sigLiteralData, out, null);
		    } else {
		        decryptionRes.setSignee(new PGPPublicKeyWrapper(pubKey));
		        sigWrap.initVerify(pubKey, "BC");
		        processLiteralData((PGPLiteralData) sigLiteralData, out, sigWrap);
		        PGPSignatureList sigList = null;
		        if(isCompressed){
		        	sigList = (PGPSignatureList) pgpFact.nextObject();
		        } else {
		        	sigList = (PGPSignatureList) plainFact.nextObject();
		        }
		        decryptionRes.setIsSignatureValid(sigWrap.verify(sigList.get(0)));
		    }
		} else if (message instanceof PGPSignatureList) {

			if(isCompressed){
				sigLiteralData = (PGPLiteralData) pgpFact.nextObject();
	        } else {
	        	sigLiteralData = (PGPLiteralData) plainFact.nextObject();
	        }

		    //Message is signed and encrypted
		    decryptionRes.setIsSigned(true);

		    PGPSignatureWrapper sigWrap = new PGPSignatureWrapper(((PGPSignatureList) message).get(0));

		    PGPPublicKey pubKey = pgpKeyRing.getPublicKeyByID(sigWrap.getKeyID());
		    
		    // If PublicKey is not available, then skip Signature validation and store exception message, but continue decryption process
		    if (pubKey == null) {
		    	decryptionRes.setSignatureException(
		        		new RuntimeException("Cannot find the public key [0x" + Integer.toHexString((int)sigWrap.getKeyID()).toUpperCase() +
		        				"] with subkey Id [0x" + Long.toHexString(sigWrap.getKeyID()).toUpperCase() +
		        				"] in the PublicKey Repository [" + pgpKeyRing.getRepositoryName()+ "]"));
		    	
		        // Decrypt without checking signature
		        processLiteralData((PGPLiteralData) sigLiteralData, out, null);
		    } else {
		        decryptionRes.setSignee(new PGPPublicKeyWrapper(pubKey));
		        sigWrap.initVerify(pubKey, "BC");
		        processLiteralData((PGPLiteralData) sigLiteralData, out, sigWrap);
		        decryptionRes.setIsSignatureValid(sigWrap.verify(null));
		    }
		} else {
		    throw new RuntimeException("Message is not a recognizable encrypted data or not supported by this decryption application. Type unknown.\n(" + message.getClass() + ")");
		}
		
		// Integrity Check
		if (encryptedPublicKey.isIntegrityProtected()){
			decryptionRes.setIntegrityProtected(true);
		    if (!encryptedPublicKey.verify()){
		    	decryptionRes.setIntegrityCheckFailure(true);
		        throw new RuntimeException("Message failed integrity check");
		    }
		}

		return decryptionRes;
    }

    /**
     * Process Literal Data
     * @param ld
     * @param out
     * @param sig
     * @return
     * @throws IOException
     * @throws SignatureException
     */
    private static void processLiteralData(PGPLiteralData ld, OutputStream out, PGPSignatureWrapper sig) throws IOException, SignatureException {
        InputStream unc = ld.getInputStream();
        int ch;
        if (sig == null){
            while ((ch = unc.read()) >= 0) {
                out.write(ch);
            }
        } else {
            while ((ch = unc.read()) >= 0) {
                out.write(ch);
                sig.update((byte) ch);
            }
        }
    }
}
