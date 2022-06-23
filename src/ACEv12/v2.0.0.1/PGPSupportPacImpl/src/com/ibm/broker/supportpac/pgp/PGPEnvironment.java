package com.ibm.broker.supportpac.pgp;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Initializes PGP Security Environment.
 * @version 1.0
 * @author Dipak K Pal (IBM)
 * <br><br>
 * <b>Description:</b>
 * Initializes PGP Security Environment.
 *
 */
public class PGPEnvironment {

	// Default Algorithms
	private static final String defaultHashAlgorithm = "SHA1"; //Default MD5
	private static final String defaultCipherAlgorithm = "CAST5"; //Default CAST5
	private static final String defaultCompressionAlgorithm = "ZIP"; //Default ZIP
	
	// Container (Map) to hold PGP Key Ring
	@SuppressWarnings("rawtypes")
	private static Map pgpKeyringMap;
	
	// Default Key repository name
	private static String defaultKeyRepository = "";

	private static final Object lock = new Object();
	
	/**
	 * Initialize PGP Security environment
	 * @param pgpRepositoryName: PGP key repository name
	 * @param pgpPrivateKeyRepository: PGP private key repository file name
	 * @param pgpPublicKeyRepository: PGP public key repository file name
	 * @param overwrite: If key repository already initiated, overwrite with new repository files if value is true
	 * @throws PGPException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void initialize(String pgpRepositoryName, String pgpPrivateKeyRepository, 
			String pgpPublicKeyRepository, boolean overwrite) throws PGPException {

		try {
			synchronized (lock) {
				
				if(pgpKeyringMap == null){
					pgpKeyringMap = Collections.synchronizedMap(new HashMap());
				}
				
				// Get Keyring from Container
				PGPKeyRing pgpKeyring = (PGPKeyRing)pgpKeyringMap.get(pgpRepositoryName);
				
				// If Keyring is not available or have been asked for overwrite, create new Keyring
				if(pgpKeyring == null || overwrite){
					
					// Validate repository files
					if(pgpPrivateKeyRepository == null || (pgpPrivateKeyRepository != null && pgpPrivateKeyRepository.trim().length() == 0)){
						throw new RuntimeException("PrivateKey repository file name can not be null. An empty file is required in case you do not require a private key.");
					}
					
					if(pgpPublicKeyRepository == null || (pgpPublicKeyRepository != null && pgpPublicKeyRepository.trim().length() == 0)){
						throw new RuntimeException("PublicKey repository file name can not be null. An empty file is required in case you do not require a public key.");
					}
					
					pgpKeyring = new PGPKeyRing(pgpRepositoryName);
					pgpKeyring.init(pgpPrivateKeyRepository, pgpPublicKeyRepository);
					pgpKeyringMap.put(pgpRepositoryName, pgpKeyring);
				}				
			}
		} catch (Exception e) {
			throw new PGPException(e.getMessage());
		}
	}
	
	/**
	 * Print Keyring
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String printKeyRing(){
		
		if(pgpKeyringMap == null){
			return "";
		}
		
		StringBuffer sb = new StringBuffer();
		Iterator iterator = pgpKeyringMap.keySet().iterator();
		
		while (iterator.hasNext()) {
			PGPKeyRing pgpKeyring = (PGPKeyRing)pgpKeyringMap.get(iterator.next());
			sb.append("\n<===================== PGP Key Repository: "+ pgpKeyring.getRepositoryName()+ " ========================>");
			sb.append("\n<===================== Private Keys =======================>\n");
			sb.append(pgpKeyring.printPrivateKeys());
			sb.append("<===================== Public Keys  =======================>\n");
			sb.append(pgpKeyring.printPublicKeys());
		}
		
		return sb.toString();
	}
	
	/**
	 * Return default PGP Keyring
	 * @return PGPKeyRing
	 */
	public static PGPKeyRing getDefaultPGPKeyRing() throws PGPException {
		PGPKeyRing pgpKeyring = null;
		try {
			pgpKeyring = findPGPKeyRing(defaultKeyRepository);
		} catch (Exception e) {
			throw new PGPException("Default PGP Keyring not initialized: " + e.getMessage());
		}
		return pgpKeyring;
	}
	
	/**
	 * Get PGP Keyring
	 * @param pgpRepositoryName
	 * @return PGPKeyRing
	 * @throws PGPException
	 */
	public static PGPKeyRing getPGPKeyRing(String pgpRepositoryName) throws PGPException {
		PGPKeyRing pgpKeyring = null;
		try {
			pgpKeyring = findPGPKeyRing(pgpRepositoryName);
		} catch (Exception e) {
			throw new PGPException(e.getMessage());
		}
		return pgpKeyring;
	}
	
	public static String getDefaultHashAlgorithm() {
		return defaultHashAlgorithm;
	}

	public static String getDefaultCipherAlgorithm() {
		return defaultCipherAlgorithm;
	}

	public static String getDefaultCompressionAlgorithm() {
		return defaultCompressionAlgorithm;
	}

	public static String getDefaultKeyRepository() {
		return defaultKeyRepository;
	}
	
	/**
	 * Set default PGP Key Repository. Make sure Key Repository is initialized
	 * @param defaultKeyRepository
	 * @throws PGPException
	 */
	public static void setDefaultKeyRepository(String defaultKeyRepository) throws PGPException {
		try {
			findPGPKeyRing(defaultKeyRepository);
		} catch (Exception e) {
			throw new PGPException(e.getMessage());
		}
		PGPEnvironment.defaultKeyRepository = defaultKeyRepository;
	}
	
	/**
	 * Find specific Key Repository
	 * @param keyRepositoryname
	 * @return PGPKeyRing
	 * @throws Exception
	 */
	private static PGPKeyRing findPGPKeyRing(String keyRepositoryname) throws Exception {
		
		// Validate whether PGP Key Repository/Container is initialized or not
		boolean invalid = false;
		if(pgpKeyringMap == null){
			invalid = true;
		}
		
		PGPKeyRing pgpKeyring = (PGPKeyRing)pgpKeyringMap.get(keyRepositoryname);
		
		if(pgpKeyring == null){
			invalid = true;
		}
		
		if(!pgpKeyring.isInitialized()){
			invalid = true;
		}
		
		if(invalid){
			throw new RuntimeException("PGP Key Repository not initialized: " + keyRepositoryname);
		}
		
		return pgpKeyring;
	}

}
