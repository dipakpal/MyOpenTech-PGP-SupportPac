package com.ibm.broker.supportpac.pgp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Provider;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Iterator;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPLiteralDataGenerator;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureGenerator;
import org.bouncycastle.openpgp.PGPSignatureSubpacketGenerator;
import org.bouncycastle.openpgp.operator.PBESecretKeyDecryptor;
import org.bouncycastle.openpgp.operator.PGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.PGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.PGPKeyEncryptionMethodGenerator;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPDigestCalculatorProviderBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator;

/**
 * PGP Encryption and Signature generation.
 * @version 1.0
 * @author Dipak K Pal (IBM)
 * <br><br>
 * <b>Description:</b>
 * PGP Encryption and Signature generation.
 * Updated on Jul 28 2016: EncryptionKey/PGPPublicKey can be set through Local Environment which will have highest preference
 */
public class PGPEncrypter {

    private final static int BUFFER_SIZE = 1 << 16;

    /**
     * Encrypt byte[] with specified Key Repository
     * @param bIn - Input data byte[]
     * @param pgpEncryptionKey - PGP Encryption key (Recipient' Public key) User Id
     * @param pgpKeyRepositoryName - PGP Repository Name
     * @return byte[]
     * @throws PGPException
     */
    public static byte[] encrypt(byte[] bIn, String pgpEncryptionKey, String pgpKeyRepositoryName) throws PGPException {

    	try {
    		// Get PGP Keyring
    		PGPKeyRing pgpKeyRing = PGPEnvironment.getPGPKeyRing(pgpKeyRepositoryName);
    		
    		// Find PGP Encryption Key
			PGPPublicKey encKey = searchPGPPublicKey(pgpKeyRing, pgpEncryptionKey);

			if(encKey == null){
				throw new RuntimeException("PGP Public Key (Encryption Key) not found: "+pgpEncryptionKey);
			}

			return encrypt(bIn, encKey, null, "");
		} catch (Exception e) {
			throw new PGPException(e.getMessage());
		}
    }
    
    /**
     * Encrypt byte[] with default Key Repository
     * @param bIn - Input data byte[]
     * @param pgpEncryptionKey - PGP Encryption key (Recipient' Public key) User Id
     * @return byte[]
     * @throws PGPException
     */
    public static byte[] encrypt(byte[] bIn, String pgpEncryptionKey) throws PGPException {

    	try {
    		// Get PGP Keyring
    		PGPKeyRing pgpKeyRing = PGPEnvironment.getDefaultPGPKeyRing();
    		
    		// Find PGP Encryption Key
			PGPPublicKey encKey = searchPGPPublicKey(pgpKeyRing, pgpEncryptionKey);

			if(encKey == null){
				throw new RuntimeException("PGP Public Key (Encryption Key) not found: "+pgpEncryptionKey);
			}

			return encrypt(bIn, encKey, null, "");
		} catch (Exception e) {
			throw new PGPException(e.getMessage());
		}
    }
    
    /**
     * Sign and Encrypt with specified PGP key repository
     * @param bIn
     * @param pgpPassPhraseSignature
     * @param pgpEncryptionKey
     * @param pgpSigningKey
     * @param pgpKeyRepositoryName
     * @return
     * @throws PGPException
     */
    public static byte[] signAndEncrypt(byte[] bIn, String pgpPassPhraseSignature, 
    		String pgpEncryptionKey, String pgpSigningKey, String pgpKeyRepositoryName) throws PGPException {

    	try {

    		// Get PGP Keyring
    		PGPKeyRing pgpKeyRing = PGPEnvironment.getPGPKeyRing(pgpKeyRepositoryName);
    		
    		// Get PGP Keys
			PGPPublicKey encKey = searchPGPPublicKey(pgpKeyRing, pgpEncryptionKey);
			PGPSecretKey signWithKey = searchPGPSecretKey(pgpKeyRing, pgpSigningKey);			

			if(encKey == null){
				throw new RuntimeException("PGP Public Key (Encryption Key) not found: "+pgpEncryptionKey);
			}

			if(signWithKey == null){
				throw new RuntimeException("PGP Private Key (Signature Key) not found: "+pgpSigningKey);
			}

			return encrypt(bIn, encKey, signWithKey, pgpPassPhraseSignature);
		} catch (Exception e) {
			throw new PGPException(e.getMessage());
		}
    }
    
    /**
     * Sing and Encrypt with default PGP key repository
     * @param bIn
     * @param pgpPassPhraseSignature
     * @param pgpEncryptionKey
     * @param pgpSigningKey
     * @return
     * @throws PGPException
     */
    public static byte[] signAndEncrypt(byte[] bIn, String pgpPassPhraseSignature, 
    		String pgpEncryptionKey, String pgpSigningKey) throws PGPException {

    	try {

    		// Get PGP Keyring
    		PGPKeyRing pgpKeyRing = PGPEnvironment.getDefaultPGPKeyRing();
    		
    		// Get PGP Keys
			PGPPublicKey encKey = searchPGPPublicKey(pgpKeyRing, pgpEncryptionKey);
			PGPSecretKey signWithKey = searchPGPSecretKey(pgpKeyRing, pgpSigningKey);

			if(encKey == null){
				throw new RuntimeException("PGP Public Key (Encryption Key) not found: "+pgpEncryptionKey);
			}

			if(signWithKey == null){
				throw new RuntimeException("PGP Private Key (Signature Key) not found: "+pgpSigningKey);
			}

			return encrypt(bIn, encKey, signWithKey, pgpPassPhraseSignature);
		} catch (Exception e) {
			throw new PGPException(e.getMessage());
		}
    }

    /**
     * Encrypt
     * @param bIn - Input data byte[]
     * @param encKey - Encryption Public key
     * @param signWithKey - Secret Key for Signature generation
     * @param signKeyPassPhrase - Secret key passphrase
     * @return byte[]
     * @throws PGPException
     */
    private static byte[] encrypt(byte[] bIn, PGPPublicKey encKey, PGPSecretKey signWithKey, String signKeyPassPhrase) throws PGPException {
    	
    	byte[] data = null;

    	try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ByteArrayInputStream in = new ByteArrayInputStream(bIn);

			encrypt(in, out, encKey, signWithKey, signKeyPassPhrase);

			data = out.toByteArray();

			in.close();
			out.close();
		} catch (IOException e) {
			throw new PGPException(e.getMessage());
		}

        return data;
    }

    /**
     * Encrypt input stream with specified key repository
     * @param in
     * @param out
     * @param pgpEncryptionKey
     * @throws Exception
     */
    public static void encrypt(InputStream in, OutputStream out, String pgpEncryptionKey, String pgpKeyRepositoryName) throws PGPException {
    	try {

    		// Get PGP Keyring
    		PGPKeyRing pgpKeyRing = PGPEnvironment.getPGPKeyRing(pgpKeyRepositoryName);

			PGPPublicKey encKey = searchPGPPublicKey(pgpKeyRing, pgpEncryptionKey);

			if(encKey == null){
				throw new RuntimeException("PGP Public Key not found: "+pgpEncryptionKey);
			}

			encrypt(in, out, encKey, null, "");
		} catch (Exception e) {
			throw new PGPException(e.getMessage());
		}
    }
    
    /**
     * Encrypt input stream with default key repository
     * @param in
     * @param out
     * @param pgpEncryptionKey
     * @throws PGPException
     */
    public static void encrypt(InputStream in, OutputStream out, String pgpEncryptionKey) throws PGPException {
    	try {

    		// Get PGP Keyring
    		PGPKeyRing pgpKeyRing = PGPEnvironment.getDefaultPGPKeyRing();

			PGPPublicKey encKey = searchPGPPublicKey(pgpKeyRing, pgpEncryptionKey);

			if(encKey == null){
				throw new RuntimeException("PGP Public Key not found: "+pgpEncryptionKey);
			}

			encrypt(in, out, encKey, null, "");
		} catch (Exception e) {
			throw new PGPException(e.getMessage());
		}
    }
    
    /**
     * Sing and Encrypt with specified Key repository
     * @param in
     * @param out
     * @param pgpEncryptionKey
     * @param pgpSigningKey
     * @param pgpPassPhraseSignature
     * @param pgpKeyRepositoryName
     * @throws Exception
     */
    public static void signAndEncrypt(InputStream in,
    		OutputStream out,
            String pgpEncryptionKey,
            String pgpSigningKey,
            String pgpPassPhraseSignature,
            String pgpKeyRepositoryName)
            throws Exception {

    	try {

    		// Get PGP Keyring
    		PGPKeyRing pgpKeyRing = PGPEnvironment.getPGPKeyRing(pgpKeyRepositoryName);

			PGPPublicKey encKey = searchPGPPublicKey(pgpKeyRing, pgpEncryptionKey);
			PGPSecretKey signWithKey = searchPGPSecretKey(pgpKeyRing, pgpSigningKey);

			if(encKey == null){
				throw new RuntimeException("PGP Public Key not found: "+pgpEncryptionKey);
			}

			if(signWithKey == null){
				throw new RuntimeException("PGP Private Key not found: "+pgpSigningKey);
			}

			encrypt(in, out, encKey, signWithKey, pgpPassPhraseSignature);
		} catch (Exception e) {
			throw new PGPException(e.getMessage());
		}
    }
    
    /**
     * Sign and Encrypt with default key repository
     * @param in
     * @param out
     * @param pgpEncryptionKey
     * @param pgpSigningKey
     * @param pgpPassPhraseSignature
     * @throws Exception
     */
    public static void signAndEncrypt(InputStream in,
    		OutputStream out,
            String pgpEncryptionKey,
            String pgpSigningKey,
            String pgpPassPhraseSignature)
            throws Exception {

    	try {

    		// Get PGP Keyring
    		PGPKeyRing pgpKeyRing = PGPEnvironment.getDefaultPGPKeyRing();

			PGPPublicKey encKey = searchPGPPublicKey(pgpKeyRing, pgpEncryptionKey);
			PGPSecretKey signWithKey = searchPGPSecretKey(pgpKeyRing, pgpSigningKey);

			if(encKey == null){
				throw new RuntimeException("PGP Public Key not found: "+pgpEncryptionKey);
			}

			if(signWithKey == null){
				throw new RuntimeException("PGP Private Key not found: "+pgpSigningKey);
			}

			encrypt(in, out, encKey, signWithKey, pgpPassPhraseSignature);
		} catch (Exception e) {
			throw new PGPException(e.getMessage());
		}
    }

    /**
     * Encryption
     * @param in - Input data stream
     * @param out - Output data stream
     * @param encKey - Encryption Public key
     * @param signWithKey - Secret Key for Signature generation
     * @param signKeyPassPhrase - Secret key passphrase
     * @throws PGPException
     */
    private static void encrypt(InputStream in,
    		OutputStream out,
            PGPPublicKey encKey,
            PGPSecretKey signWithKey,
            String signKeyPassPhrase)
            throws PGPException {

    	String symmetricKeyAlgorithm = PGPEnvironment.getDefaultCipherAlgorithm();
        String compressionAlgorithm = PGPEnvironment.getDefaultCompressionAlgorithm();
        String hashAlgorithm = PGPEnvironment.getDefaultHashAlgorithm();
        
    	try {
			encrypt(in, out, encKey, signWithKey, "", true, true, signKeyPassPhrase,symmetricKeyAlgorithm,compressionAlgorithm,hashAlgorithm);
		} catch (Exception e) {
			throw new PGPException(e.getMessage());
		}
    }

    /**
     * Sign and Encrypt with specified key repository
     * @param bIn - Input data byte[]
     * @param pgpEncryptionKey - PGP Encryption key (Recipient' Public key) User Id
     * @param pgpKeyRepositoryName - PGP Key repository name
     * @param AsciiArmor - ASCII Armor (true|false)
     * @param withIntegrityCheck - Enable Integrity check (Checksum)
     * @param symmetricKeyAlgorithm - Cipher Algorithm
     * @param compressionAlgorithm - Compression Algorithm
     * @param hashAlgorithm - Digest Algorithm
     * @return
     * @throws PGPException
     */
    public static byte[] encrypt(byte[] bIn,
		String pgpEncryptionKey,
		String pgpKeyRepositoryName,
		boolean AsciiArmor,
        boolean withIntegrityCheck,
		String symmetricKeyAlgorithm,
		String compressionAlgorithm,
		String hashAlgorithm) throws PGPException {

    	try {    		
    		// Get PGP Keyring
    		PGPKeyRing pgpKeyRing = PGPEnvironment.getPGPKeyRing(pgpKeyRepositoryName);
    		
			PGPPublicKey encKey = searchPGPPublicKey(pgpKeyRing, pgpEncryptionKey);

			if(encKey == null){
				throw new RuntimeException("PGP Public Key not found: "+pgpEncryptionKey);
			}

			ByteArrayOutputStream out = new ByteArrayOutputStream();
	        ByteArrayInputStream in = new ByteArrayInputStream(bIn);

	        encrypt(in, out, encKey, null, "", AsciiArmor, withIntegrityCheck, "", symmetricKeyAlgorithm, compressionAlgorithm, hashAlgorithm);

	        byte[] data = out.toByteArray();

	        in.close();
	        out.close();

	        return data;
		} catch (Exception e) {
			throw new PGPException(e.getMessage());
		}
    }
    
 
    /**
     * Sign and Encrypt with default key repository
     * @param bIn - Input data byte[]
     * @param pgpEncryptionKey - PGP Encryption key (Recipient' Public key) User Id
     * @param AsciiArmor - ASCII Armor (true|false)
     * @param withIntegrityCheck - Enable Integrity check (Checksum)
     * @param symmetricKeyAlgorithm - Cipher Algorithm
     * @param compressionAlgorithm - Compression Algorithm
     * @param hashAlgorithm - Digest Algorithm
     * @return
     * @throws PGPException
     */
    public static byte[] encrypt(byte[] bIn,
		String pgpEncryptionKey,
		boolean AsciiArmor,
        boolean withIntegrityCheck,
		String symmetricKeyAlgorithm,
		String compressionAlgorithm,
		String hashAlgorithm) throws PGPException {

    	try {    		
    		// Get PGP Keyring
    		PGPKeyRing pgpKeyRing = PGPEnvironment.getDefaultPGPKeyRing();
    		
			PGPPublicKey encKey = searchPGPPublicKey(pgpKeyRing, pgpEncryptionKey);

			if(encKey == null){
				throw new RuntimeException("PGP Public Key not found: "+pgpEncryptionKey);
			}

			ByteArrayOutputStream out = new ByteArrayOutputStream();
	        ByteArrayInputStream in = new ByteArrayInputStream(bIn);

	        encrypt(in, out, encKey, null, "", AsciiArmor, withIntegrityCheck, "", symmetricKeyAlgorithm, compressionAlgorithm, hashAlgorithm);

	        byte[] data = out.toByteArray();

	        in.close();
	        out.close();

	        return data;
		} catch (Exception e) {
			throw new PGPException(e.getMessage());
		}
    }

    /**
     * Sign and Encrypt with specified key repository
     * @param bIn
     * @param pgpPassPhraseSignature
     * @param pgpEncryptionKey
     * @param pgpSigningKey
     * @param AsciiArmor
     * @param withIntegrityCheck
     * @param symmetricKeyAlgorithm
     * @param compressionAlgorithm
     * @param hashAlgorithm
     * @return byte[]
     * @throws PGPException
     */
    public static byte[] signAndEncrypt(byte[] bIn,
		String pgpPassPhraseSignature,
		String pgpEncryptionKey,
		String pgpSigningKey,
		String pgpKeyRepositoryName,
		boolean AsciiArmor,
        boolean withIntegrityCheck,
		String symmetricKeyAlgorithm,
		String compressionAlgorithm,
		String hashAlgorithm) throws PGPException {

    	try {
    		// Get PGP Keyring
    		PGPKeyRing pgpKeyRing = PGPEnvironment.getPGPKeyRing(pgpKeyRepositoryName);
    		
			PGPPublicKey encKey = searchPGPPublicKey(pgpKeyRing, pgpEncryptionKey);
			PGPSecretKey signWithKey = searchPGPSecretKey(pgpKeyRing, pgpSigningKey);

			if(encKey == null){
				throw new RuntimeException("PGP Public Key not found: "+pgpEncryptionKey);
			}

			if(signWithKey == null){
				throw new RuntimeException("PGP Private Key not found: "+pgpSigningKey);
			}

			ByteArrayOutputStream out = new ByteArrayOutputStream();
	        ByteArrayInputStream in = new ByteArrayInputStream(bIn);

	        encrypt(in, out, encKey, signWithKey, "", AsciiArmor, withIntegrityCheck, pgpPassPhraseSignature,symmetricKeyAlgorithm,compressionAlgorithm,hashAlgorithm);

	        byte[] data = out.toByteArray();

	        in.close();
	        out.close();

	        return data;
		} catch (Exception e) {
			throw new PGPException(e.getMessage());
		}
    }
    
    /**
     * Sign and Encrypt with default key repository
     * @param bIn
     * @param pgpPassPhraseSignature
     * @param pgpEncryptionKey
     * @param pgpSigningKey
     * @param AsciiArmor
     * @param withIntegrityCheck
     * @param symmetricKeyAlgorithm
     * @param compressionAlgorithm
     * @param hashAlgorithm
     * @return
     * @throws PGPException
     */
    public static byte[] signAndEncrypt(byte[] bIn,
		String pgpPassPhraseSignature,
		String pgpEncryptionKey,
		String pgpSigningKey,
		boolean AsciiArmor,
        boolean withIntegrityCheck,
		String symmetricKeyAlgorithm,
		String compressionAlgorithm,
		String hashAlgorithm) throws PGPException {

    	try {
    		// Get PGP Keyring
    		PGPKeyRing pgpKeyRing = PGPEnvironment.getDefaultPGPKeyRing();
    		
			PGPPublicKey encKey = searchPGPPublicKey(pgpKeyRing, pgpEncryptionKey);
			PGPSecretKey signWithKey = searchPGPSecretKey(pgpKeyRing, pgpSigningKey);

			if(encKey == null){
				throw new RuntimeException("PGP Public Key not found: "+pgpEncryptionKey);
			}

			if(signWithKey == null){
				throw new RuntimeException("PGP Private Key not found: "+pgpSigningKey);
			}

			ByteArrayOutputStream out = new ByteArrayOutputStream();
	        ByteArrayInputStream in = new ByteArrayInputStream(bIn);

	        encrypt(in, out, encKey, signWithKey, "", AsciiArmor, withIntegrityCheck, pgpPassPhraseSignature,symmetricKeyAlgorithm,compressionAlgorithm,hashAlgorithm);

	        byte[] data = out.toByteArray();

	        in.close();
	        out.close();

	        return data;
		} catch (Exception e) {
			throw new PGPException(e.getMessage());
		}
    }
    
    /**
     * Sign and Encrypt with specified key repository
     * @param in - Input Stream
     * @param out - Output stream
     * @param pgpEncryptionKey - PGP Encryption key (Recipient' Public key) User Id
     * @param pgpKeyRepositoryName - Key repository name
     * @param AsciiArmor - ASCII Armor (true|false)
     * @param withIntegrityCheck - Enable Integrity check (Checksum)
     * @param symmetricKeyAlgorithm - Cipher Algorithm
     * @param compressionAlgorithm - Compression Algorithm
     * @param hashAlgorithm - Digest Algorithm
     * @return
     * @throws PGPException
     */
    public static void encrypt(InputStream in,
        OutputStream out,
		String pgpEncryptionKey,
		String pgpKeyRepositoryName,
		boolean AsciiArmor,
        boolean withIntegrityCheck,
		String symmetricKeyAlgorithm,
		String compressionAlgorithm,
		String hashAlgorithm) throws PGPException {

    	try {    		
    		// Get PGP Keyring
    		PGPKeyRing pgpKeyRing = PGPEnvironment.getPGPKeyRing(pgpKeyRepositoryName);
    		
			PGPPublicKey encKey = searchPGPPublicKey(pgpKeyRing, pgpEncryptionKey);

			if(encKey == null){
				throw new RuntimeException("PGP Public Key not found: "+pgpEncryptionKey);
			}

			encrypt(in, out, encKey, null, "", AsciiArmor, withIntegrityCheck, "", symmetricKeyAlgorithm, compressionAlgorithm, hashAlgorithm);

	     } catch (Exception e) {
			throw new PGPException(e.getMessage());
		}
    }
    
    
    /**
     * Sign and Encrypt with specified key repository
     * @param in - Input Stream
     * @param out - Output stream
     * @param pgpEncryptionKey - PGP Encryption key - raw byte[]
     * @param AsciiArmor - ASCII Armor (true|false)
     * @param withIntegrityCheck - Enable Integrity check (Checksum)
     * @param symmetricKeyAlgorithm - Cipher Algorithm
     * @param compressionAlgorithm - Compression Algorithm
     * @param hashAlgorithm - Digest Algorithm
     * @return
     * @throws PGPException
     */
    public static void encrypt(InputStream in,
            OutputStream out,
    		byte[] pgpEncryptionKey,
    		boolean AsciiArmor,
            boolean withIntegrityCheck,
    		String symmetricKeyAlgorithm,
    		String compressionAlgorithm,
    		String hashAlgorithm) throws PGPException {

        	try {        		  		
    			PGPPublicKey encKey = PGPJavaUtil.readPublicKey(pgpEncryptionKey);

    			if(encKey == null){
    				throw new RuntimeException("PGP Public Key not found: "+pgpEncryptionKey);
    			}

    			encrypt(in, out, encKey, null, "", AsciiArmor, withIntegrityCheck, "", symmetricKeyAlgorithm, compressionAlgorithm, hashAlgorithm);

    	     } catch (Exception e) {
    			throw new PGPException(e.getMessage());
    		}
        } 
    
   
    /**
     * Sign and Encrypt with default key repository
     * @param in - Input Stream
     * @param out - Output stream
     * @param pgpEncryptionKey - PGP Encryption key (Recipient' Public key) User Id
     * @param AsciiArmor - ASCII Armor (true|false)
     * @param withIntegrityCheck - Enable Integrity check (Checksum)
     * @param symmetricKeyAlgorithm - Cipher Algorithm
     * @param compressionAlgorithm - Compression Algorithm
     * @param hashAlgorithm - Digest Algorithm
     * @return
     * @throws PGPException
     */
    public static void encrypt(InputStream in,
        OutputStream out,
		String pgpEncryptionKey,
		boolean AsciiArmor,
        boolean withIntegrityCheck,
		String symmetricKeyAlgorithm,
		String compressionAlgorithm,
		String hashAlgorithm) throws PGPException {

    	try {    		
    		// Get PGP Keyring
    		PGPKeyRing pgpKeyRing = PGPEnvironment.getDefaultPGPKeyRing();
    		
			PGPPublicKey encKey = searchPGPPublicKey(pgpKeyRing, pgpEncryptionKey);

			if(encKey == null){
				throw new RuntimeException("PGP Public Key not found: "+pgpEncryptionKey);
			}

			encrypt(in, out, encKey, null, "", AsciiArmor, withIntegrityCheck, "", symmetricKeyAlgorithm, compressionAlgorithm, hashAlgorithm);

	    } catch (Exception e) {
			throw new PGPException(e.getMessage());
		}
    }
    
    /**
     * Sign and Encrypt with specified key repository
     * @param in
     * @param out
     * @param pgpPassPhraseSignature
     * @param pgpEncryptionKey
     * @param pgpSigningKey
     * @param pgpKeyRepositoryName
     * @param AsciiArmor
     * @param withIntegrityCheck
     * @param symmetricKeyAlgorithm
     * @param compressionAlgorithm
     * @param hashAlgorithm
     * @return
     * @throws PGPException
     */
    public static void signAndEncrypt(InputStream in,
        OutputStream out,
		String pgpPassPhraseSignature,
		String pgpEncryptionKey,
		String pgpSigningKey,
		String pgpKeyRepositoryName,
		boolean AsciiArmor,
        boolean withIntegrityCheck,
		String symmetricKeyAlgorithm,
		String compressionAlgorithm,
		String hashAlgorithm) throws PGPException {

    	try {
    		// Get PGP Keyring
    		PGPKeyRing pgpKeyRing = PGPEnvironment.getPGPKeyRing(pgpKeyRepositoryName);
    		
			PGPPublicKey encKey = searchPGPPublicKey(pgpKeyRing, pgpEncryptionKey);
			PGPSecretKey signWithKey = searchPGPSecretKey(pgpKeyRing, pgpSigningKey);

			if(encKey == null){
				throw new RuntimeException("PGP Public Key not found: "+pgpEncryptionKey);
			}

			if(signWithKey == null){
				throw new RuntimeException("PGP Private Key not found: "+pgpSigningKey);
			}

			encrypt(in, out, encKey, signWithKey, "", AsciiArmor, withIntegrityCheck, pgpPassPhraseSignature,symmetricKeyAlgorithm,compressionAlgorithm,hashAlgorithm);

	    } catch (Exception e) {
			throw new PGPException(e.getMessage());
		}
    }
    
    /**
     * Sign and Encrypt with specified key repository
     * @param in
     * @param out
     * @param pgpPassPhraseSignature
     * @param pgpEncryptionKey
     * @param pgpSigningKey
     * @param pgpKeyRepositoryName
     * @param AsciiArmor
     * @param withIntegrityCheck
     * @param symmetricKeyAlgorithm
     * @param compressionAlgorithm
     * @param hashAlgorithm
     * @return
     * @throws PGPException
     */
    public static void signAndEncrypt(InputStream in,
        OutputStream out,
		String pgpPassPhraseSignature,
		byte[] pgpEncryptionKey,
		String pgpSigningKey,
		String pgpKeyRepositoryName,
		boolean AsciiArmor,
        boolean withIntegrityCheck,
		String symmetricKeyAlgorithm,
		String compressionAlgorithm,
		String hashAlgorithm) throws PGPException {

    	try {
    		// Get PGP Keyring
    		PGPKeyRing pgpKeyRing = PGPEnvironment.getPGPKeyRing(pgpKeyRepositoryName);
    		
    		PGPPublicKey encKey = PGPJavaUtil.readPublicKey(pgpEncryptionKey);
			PGPSecretKey signWithKey = searchPGPSecretKey(pgpKeyRing, pgpSigningKey);

			if(encKey == null){
				throw new RuntimeException("PGP Public Key not found: "+pgpEncryptionKey);
			}

			if(signWithKey == null){
				throw new RuntimeException("PGP Private Key not found: "+pgpSigningKey);
			}

			encrypt(in, out, encKey, signWithKey, "", AsciiArmor, withIntegrityCheck, pgpPassPhraseSignature,symmetricKeyAlgorithm,compressionAlgorithm,hashAlgorithm);

	    } catch (Exception e) {
			throw new PGPException(e.getMessage());
		}
    }
    
    /**
     * Sign and Encrypt with specified key repository
     * @param in
     * @param out
     * @param pgpPassPhraseSignature
     * @param pgpEncryptionKey
     * @param pgpSigningKey
     * @param pgpKeyRepositoryName
     * @param AsciiArmor
     * @param withIntegrityCheck
     * @param symmetricKeyAlgorithm
     * @param compressionAlgorithm
     * @param hashAlgorithm
     * @return
     * @throws PGPException
     */
    public static void signAndEncrypt(InputStream in,
        OutputStream out,
		String pgpPassPhraseSignature,
		String pgpEncryptionKey,
		byte[] pgpSigningKey,
		String pgpKeyRepositoryName,
		boolean AsciiArmor,
        boolean withIntegrityCheck,
		String symmetricKeyAlgorithm,
		String compressionAlgorithm,
		String hashAlgorithm) throws PGPException {

    	try {
    		// Get PGP Keyring
    		PGPKeyRing pgpKeyRing = PGPEnvironment.getPGPKeyRing(pgpKeyRepositoryName);
    		
			PGPPublicKey encKey = searchPGPPublicKey(pgpKeyRing, pgpEncryptionKey);
			PGPSecretKey signWithKey = PGPJavaUtil.readSecretKey(pgpSigningKey);

			if(encKey == null){
				throw new RuntimeException("PGP Public Key not found: "+pgpEncryptionKey);
			}

			if(signWithKey == null){
				throw new RuntimeException("PGP Private Key not found: "+pgpSigningKey);
			}

			encrypt(in, out, encKey, signWithKey, "", AsciiArmor, withIntegrityCheck, pgpPassPhraseSignature,symmetricKeyAlgorithm,compressionAlgorithm,hashAlgorithm);

	    } catch (Exception e) {
			throw new PGPException(e.getMessage());
		}
    }
    
    /**
     * Sign and Encrypt with specified key repository
     * @param in
     * @param out
     * @param pgpPassPhraseSignature
     * @param pgpEncryptionKey
     * @param pgpSigningKey
     * @param AsciiArmor
     * @param withIntegrityCheck
     * @param symmetricKeyAlgorithm
     * @param compressionAlgorithm
     * @param hashAlgorithm
     * @return
     * @throws PGPException
     */
    public static void signAndEncrypt(InputStream in,
        OutputStream out,
		String pgpPassPhraseSignature,
		byte[] pgpEncryptionKey,
		byte[] pgpSigningKey,
		boolean AsciiArmor,
        boolean withIntegrityCheck,
		String symmetricKeyAlgorithm,
		String compressionAlgorithm,
		String hashAlgorithm) throws PGPException {

    	try {
    		
    		PGPPublicKey encKey = PGPJavaUtil.readPublicKey(pgpEncryptionKey);
			PGPSecretKey signWithKey = PGPJavaUtil.readSecretKey(pgpSigningKey);

			if(encKey == null){
				throw new RuntimeException("PGP Public Key not found: "+pgpEncryptionKey);
			}

			if(signWithKey == null){
				throw new RuntimeException("PGP Private Key not found: "+pgpSigningKey);
			}

			encrypt(in, out, encKey, signWithKey, "", AsciiArmor, withIntegrityCheck, pgpPassPhraseSignature,symmetricKeyAlgorithm,compressionAlgorithm,hashAlgorithm);

	    } catch (Exception e) {
			throw new PGPException(e.getMessage());
		}
    }
    
    /**
     * Sign and Encrypt with default key repository
     * @param in
     * @param out
     * @param pgpPassPhraseSignature
     * @param pgpEncryptionKey
     * @param pgpSigningKey
     * @param AsciiArmor
     * @param withIntegrityCheck
     * @param symmetricKeyAlgorithm
     * @param compressionAlgorithm
     * @param hashAlgorithm
     * @throws PGPException
     */
    public static void signAndEncrypt(InputStream in,
    	OutputStream out,
		String pgpPassPhraseSignature,
		String pgpEncryptionKey,
		String pgpSigningKey,
		boolean AsciiArmor,
        boolean withIntegrityCheck,
		String symmetricKeyAlgorithm,
		String compressionAlgorithm,
		String hashAlgorithm) throws PGPException {

    	try {
    		// Get PGP Keyring
    		PGPKeyRing pgpKeyRing = PGPEnvironment.getDefaultPGPKeyRing();
    		
			PGPPublicKey encKey = searchPGPPublicKey(pgpKeyRing, pgpEncryptionKey);
			PGPSecretKey signWithKey = searchPGPSecretKey(pgpKeyRing, pgpSigningKey);

			if(encKey == null){
				throw new RuntimeException("PGP Public Key not found: "+pgpEncryptionKey);
			}

			if(signWithKey == null){
				throw new RuntimeException("PGP Private Key not found: "+pgpSigningKey);
			}

	        encrypt(in, out, encKey, signWithKey, "", AsciiArmor, withIntegrityCheck, pgpPassPhraseSignature,symmetricKeyAlgorithm,compressionAlgorithm,hashAlgorithm);
	        
		} catch (Exception e) {
			throw new PGPException(e.getMessage());
		}
    }
    
    /**
     * Method for PGP Encryption/Signature generation
     * @param in
     * @param out
     * @param encKey
     * @param signWithKey
     * @param inputFile
     * @param armor
     * @param withIntegrityCheck
     * @param signKeyPassPhrase
     * @param symmetricKeyAlgorithm
     * @param compressionAlgorithm
     * @param hashAlgorithm
     * @throws Exception
     */
	@SuppressWarnings("rawtypes")
	private static void encrypt(InputStream in,
    		OutputStream out,
            PGPPublicKey encKey,
            PGPSecretKey signWithKey,
            String inputFile,
            boolean armor,
            boolean withIntegrityCheck,
            String signKeyPassPhrase,
            String symmetricKeyAlgorithm,
            String compressionAlgorithm,
            String hashAlgorithm)
            throws Exception {

    	char[] signKeyPass = signKeyPassPhrase.toCharArray();
    	Provider provider = PGPJavaUtil.getProvider(PGPJavaUtil.getDefaultProvider());
    	SecureRandom rand = new SecureRandom();
    	
        PGPSignatureGenerator signatureGenerator = null;
        if (armor) {
            out = new ArmoredOutputStream(out);
        }

        // Init encrypted data generator
        PGPDataEncryptorBuilder encryptorBuilder = 
        		new JcePGPDataEncryptorBuilder(PGPJavaUtil.getCipherAlgorithm(symmetricKeyAlgorithm)).setWithIntegrityPacket(withIntegrityCheck).setSecureRandom(rand).setProvider(provider);
        PGPEncryptedDataGenerator encryptedDataGenerator = new PGPEncryptedDataGenerator(encryptorBuilder);

        PGPKeyEncryptionMethodGenerator method = 
        		new JcePublicKeyKeyEncryptionMethodGenerator(encKey).setProvider(provider).setSecureRandom(rand);
        encryptedDataGenerator.addMethod(method);

        OutputStream encryptedOut = encryptedDataGenerator.open(out, new byte[BUFFER_SIZE]);

        // Init compression -- PGPCompressedData.ZIP
        PGPCompressedDataGenerator compressedDataGenerator = new PGPCompressedDataGenerator(PGPJavaUtil.getCompressionAlgorithm(compressionAlgorithm));
        OutputStream compressedOut = compressedDataGenerator.open(encryptedOut);

        //Init signature if signWithKey != null
        if(signWithKey != null){
            PGPPublicKey pubSigKey = signWithKey.getPublicKey();
            PGPPrivateKey secretKey = null;
			try {
				PBESecretKeyDecryptor decryptorFactory = 
						new JcePBESecretKeyDecryptorBuilder(new JcaPGPDigestCalculatorProviderBuilder().setProvider(provider).build()).setProvider(provider).build(signKeyPass);
				secretKey = signWithKey.extractPrivateKey(decryptorFactory);
			} catch (Exception e) {
				String message = "Private(Sign) key [0x" + Integer.toHexString((int)signWithKey.getKeyID()).toUpperCase() + "] " +
				 "can not be extracted from Key Repository. Verify the key repository and/or passphrase.";
	
				message = message + "Root cause: " + e.getMessage();
				
				throw new IllegalArgumentException(message);
			}
			PGPContentSignerBuilder contentSignerBuilder = 
					new JcaPGPContentSignerBuilder(pubSigKey.getAlgorithm(),PGPJavaUtil.getHashAlgorithm(hashAlgorithm)).setProvider(provider).setDigestProvider(provider);
            signatureGenerator = new PGPSignatureGenerator(contentSignerBuilder); //PGPUtil.SHA1
            signatureGenerator.init(PGPSignature.BINARY_DOCUMENT, secretKey);
            Iterator it = pubSigKey.getUserIDs();
            if(it.hasNext())
            {
                PGPSignatureSubpacketGenerator  spGen = new PGPSignatureSubpacketGenerator();
                spGen.setSignerUserID(false, (String)it.next());
                signatureGenerator.setHashedSubpackets(spGen.generate());
            }
            signatureGenerator.generateOnePassVersion(false).encode(compressedOut);
        }

        PGPLiteralDataGenerator literalDataGenerator = new PGPLiteralDataGenerator();
        OutputStream literalOut = literalDataGenerator.open(compressedOut, PGPLiteralData.BINARY, inputFile, new Date(),new byte[BUFFER_SIZE]);

        byte[] buf = new byte[BUFFER_SIZE];
        int len;
        while((len = in.read(buf))>0){
            literalOut.write(buf,0,len);
            if(signatureGenerator != null)
                signatureGenerator.update(buf,0,len);
        }

        literalOut.close();
        literalDataGenerator.close();
        if(signatureGenerator != null)
            signatureGenerator.generate().encode(compressedOut);

        compressedOut.close();
        compressedDataGenerator.close();
        encryptedOut.close();
        encryptedDataGenerator.close();
        in.close();
        out.close();

    }

    /**
     * Plain Text (UTF8) encryption
     * @param plainText
     * @param encKey
     * @param signWithKey
     * @param signKeyPass
     * @return
     * @throws Exception
     */
    public static String encryptUTF8Text(String plainText,
    		PGPPublicKey encKey,
            PGPSecretKey signWithKey,
            String signKeyPass)
            throws Exception {

        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        ByteArrayInputStream bIn = new ByteArrayInputStream(plainText.getBytes("UTF8"));

        encrypt(bIn, bOut, encKey, signWithKey, signKeyPass);
        byte[] data = bOut.toByteArray();

        bIn.close();
        bOut.close();

        return new String(data, "UTF8");
    }
    
    /**
     * Search PGP Sign Key
     * @param pgpKeyRing
     * @param pgpSigningKey
     * @return
     * @throws Exception
     */
    public static PGPSecretKey searchPGPSecretKey(PGPKeyRing pgpKeyRing, String pgpSigningKey) throws Exception {
    	
    	// First serach PGPSecrectKey by Key User id
    	PGPSecretKey signWithKey = pgpKeyRing.getSigningKeyByUserId(pgpSigningKey);
    	
    	// If Key not found, then try with Hex Key Id
    	if(signWithKey == null){
    		signWithKey = pgpKeyRing.getSignKeyByHexKeyId(pgpSigningKey);
    	}
    	
    	return signWithKey;  
    }
    
    /**
     * Serach PGP Public Key
     * @param pgpKeyRing
     * @param pgpEncryptionKey
     * @return
     * @throws Exception
     */
    public static PGPPublicKey searchPGPPublicKey(PGPKeyRing pgpKeyRing, String pgpEncryptionKey) throws Exception {
    	
    	// First search PGPPublicKey by Key user Id
    	PGPPublicKey encKey = pgpKeyRing.getEncryptionKeyByUserId(pgpEncryptionKey);
    	
    	// If Key not found, then try with Hex Key Id
    	if(encKey == null){
    		encKey = pgpKeyRing.getEncryptionKeyByHexKeyId(pgpEncryptionKey);
    	}
    	
    	return encKey;    	
    }
}
