package com.ibm.broker.supportpac.pgp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.operator.KeyFingerPrintCalculator;
import org.bouncycastle.openpgp.operator.PBESecretKeyDecryptor;
import org.bouncycastle.openpgp.operator.PBESecretKeyEncryptor;
import org.bouncycastle.openpgp.operator.PGPDigestCalculator;
import org.bouncycastle.openpgp.operator.PGPDigestCalculatorProvider;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPDigestCalculatorProviderBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyEncryptorBuilder;

/**
 * @version 1.0
 * @author Dipak K Pal (IBM)
 * <br><br>
 * <b>Description:</b>
 * Contains key repositories and provides various operations over Public/Private key repository.
 */
public class PGPKeyRing {

	private String secretKeyringFile;
	private String publicKeyringFile;
	private String repositoryName;

	private PGPPublicKeyRingCollection publicKeyring;
	private PGPSecretKeyRingCollection secretKeyring;

	private final Object lock = new Object();
	
	private boolean initialized = false;
	
	/**
	 * Make default constructor private 
	 */
	@SuppressWarnings("unused")
	private PGPKeyRing(){}

	/**
	 * Public Constructor
	 * @param repositoryName
	 */
	public PGPKeyRing(String repositoryName) throws Exception {
		
		if(repositoryName != null && repositoryName.trim().length() > 0){
			this.repositoryName = repositoryName;
		} else {
			throw new RuntimeException("Invalid Repository Name");
		}		
	}

	/**
	 * Initialize PGPKeyRing
	 * @param privateKeyRing
	 * @param publicKeyRing
	 */
	public void init(String privateKeyRing, String publicKeyRing) throws Exception {
		synchronized (lock) {
			Security.addProvider(new BouncyCastleProvider());

			secretKeyringFile = privateKeyRing;
			publicKeyringFile = publicKeyRing;

			loadPrivateKeyRings();
			loadPublicKeyRings();
			
			initialized = true;
		}
	}

	/**
	 * Clrear Key Repository
	 * @throws Exception
	 */
	public void clear() throws Exception {
		synchronized (lock) {
			publicKeyring = null;
			secretKeyring = null;
		}
	}

	/**
	 * Load Secret Keyring from the Secret key repository file
	 * @param privateKeyRing - Secret key repository file
	 * @throws Exception
	 */
	public void loadPrivateKeyRepository(String privateKeyRing) throws Exception {
		synchronized (lock) {
			Security.addProvider(new BouncyCastleProvider());
			secretKeyringFile = privateKeyRing;
			loadPrivateKeyRings();
		}
	}

	/**
	 * Load Public Keyring from the Public key repository file
	 * @param publicKeyRing - Public key repository file
	 * @throws Exception
	 */
	public void loadPublicKeyRepository(String publicKeyRing) throws Exception {
		synchronized (lock) {
			Security.addProvider(new BouncyCastleProvider());
			publicKeyringFile = publicKeyRing;
			loadPublicKeyRings();
		}
	}

	/**
	 * Load Secret KeyRings
	 * @throws Exception
	 */
	private void loadPrivateKeyRings() throws Exception {

		File secringFile = new File(secretKeyringFile);

		if (secringFile.exists()) {
			InputStream is = new FileInputStream(secringFile);
			secretKeyring = new PGPSecretKeyRingCollection(PGPUtil.getDecoderStream(is), new JcaKeyFingerprintCalculator());
			is.close();
		} else {
			throw new RuntimeException("PGP Private key file not found: " + secretKeyringFile);
		}
	}

	/**
	 * Load Public Keyrings
	 */
	private void loadPublicKeyRings() throws Exception {

		File pubringFile = new File(publicKeyringFile);

		if (pubringFile.exists()) {
			InputStream is = new FileInputStream(pubringFile);
			publicKeyring = new PGPPublicKeyRingCollection(PGPUtil.getDecoderStream(is), new JcaKeyFingerprintCalculator());
			is.close();
		} else {
			throw new RuntimeException("PGP Public key file not found: " + publicKeyringFile);
		}
	}

	/**
	 * Import Public key into Public Key Repository
	 * @param iKeyStream
	 * @return
	 * @throws IOException
	 */
	public PGPPublicKeyRing importPublicKey(InputStream iKeyStream) throws IOException, PGPException {
		
		KeyFingerPrintCalculator fingerPrintCalculator = new JcaKeyFingerprintCalculator();
		PGPPublicKeyRing newKey = new PGPPublicKeyRing(new ArmoredInputStream(iKeyStream),fingerPrintCalculator);

		// Create an empty PGPPublicKeyRingCollection if not exists
		if(publicKeyring == null){
			publicKeyring = new PGPPublicKeyRingCollection(Collections.<PGPPublicKeyRing> emptyList());
		}

		publicKeyring = PGPPublicKeyRingCollection.addPublicKeyRing(publicKeyring, newKey);
		savePubring();
		return newKey;
	}
	
	/**
	 * Export specified Public Key into file
	 * @param pubKeyUserId
	 * @param fileName
	 * @param asciiArmor
	 * @throws Exception
	 */
	public void exportPublicKey(String pubKeyUserId, String fileName, boolean asciiArmor) throws Exception {
		
		PGPPublicKeyRing pubKeyring = getPublicKeyRingByUserId(pubKeyUserId);
		
		if(pubKeyring == null){
			throw new RuntimeException("Public key ["+ pubKeyUserId + "] not found at Public Key Repository");
		}

		PGPJavaUtil.writeFile(pubKeyring.getEncoded(), fileName, asciiArmor);
	}

	/**
	 * Import Secret key into Secret Key Repository
	 * @param iKeyStream
	 * @return
	 * @throws IOException
	 * @throws PGPException
	 */
	@SuppressWarnings("unused")
	public PGPSecretKeyRing importPrivateKey(InputStream iKeyStream) throws IOException, PGPException {
		BufferedInputStream iStream = new BufferedInputStream(iKeyStream);

		PGPSecretKeyRing newKey;
		Exception lastException = null;

		iStream.mark(1024 * 128);

		try {
			// 1st try: Import private first, then public
			KeyFingerPrintCalculator fingerPrintCalculator = new JcaKeyFingerprintCalculator();
			newKey = new PGPSecretKeyRing(new ArmoredInputStream(iStream),fingerPrintCalculator);
			try {
				importPublicKey(iStream);
			} catch (Exception ex2) {
				//ex2.printStackTrace();
			}
		} catch (IOException ex) {
			// 2nd try: Import public first, then private
			iStream.reset();
			try {
				importPublicKey(iStream);
			} catch (Exception ex2) {
				//ex2.printStackTrace();
			}
			KeyFingerPrintCalculator fingerPrintCalculator = new JcaKeyFingerprintCalculator();
			newKey = new PGPSecretKeyRing(new ArmoredInputStream(iStream),fingerPrintCalculator);
		}

		if (newKey == null)
			throw new IOException("Cannot import private Key. " + lastException);

		// Create an empty PGPSecretKeyRingCollection if not created
		if(secretKeyring == null){
			secretKeyring = new PGPSecretKeyRingCollection(Collections.<PGPSecretKeyRing> emptyList());
		}

		secretKeyring = PGPSecretKeyRingCollection.addSecretKeyRing(secretKeyring, newKey);
		saveSecring();

		return newKey;
	}
	
	/**
	 * Export Private Key into file
	 * @param secKeyUserId
	 * @param fileName
	 * @param asciiArmor
	 * @throws Exception
	 */
	public void exportPrivateKey(String secKeyUserId, String fileName, boolean asciiArmor) throws Exception {
		
		PGPSecretKeyRing secKeyring = getSecretKeyRingByUserId(secKeyUserId);
		
		if(secKeyring == null){
			throw new RuntimeException("Private key ["+ secKeyUserId + "] not found at Private Key Repository");
		}

		PGPJavaUtil.writeFile(secKeyring.getEncoded(), fileName, asciiArmor);
	}

	/**
	 * Check whether Public Key exists
	 * @param publicKeyUserId
	 * @return
	 */
	public boolean containsPublicKey(String publicKeyUserId){
		boolean exists = false;
		try {
			PGPPublicKey publicKey = getEncryptionKeyByUserId(publicKeyUserId);

			if(publicKey != null){
				publicKey.getKeyID();
				exists = true;
			}
		} catch (Exception e) {
			//Ignore Exception
		}
		return exists;
	}

	/**
	 * Check If Privte Key exists or not
	 * @param privateKeyUserId
	 * @return
	 */
	public boolean containsPrivateKey(String privateKeyUserId){
		boolean exists = false;
		try {
			PGPSecretKeyRing secretKeyRing = getSecretKeyRingByUserId(privateKeyUserId);

			if(secretKeyRing != null){
				PGPSecretKey secretKey = secretKeyRing.getSecretKey();

				if(secretKey != null){
					secretKey.getKeyID();
					exists = true;
				}
			}
		} catch (Exception e) {
			//Ignore Exception
		}
		return exists;
	}

	/**
	 * Change Secret key passphrase
	 * @param iKeyRing
	 * @param iOldPassphrase
	 * @param iNewPassphrase
	 * @throws PGPException
	 * @throws NoSuchProviderException
	 * @throws IOException
	 */
	public void changePrivateKeyPassphrase(PGPSecretKeyRing iKeyRing, String iOldPassphrase, String iNewPassphrase)
	throws PGPException, NoSuchProviderException, IOException {
		changePrivateKeyPassphrase(iKeyRing, iOldPassphrase.toCharArray(), iNewPassphrase.toCharArray());
	}

	/**
	 * Change Secret key passphrase
	 * @param iKeyRing - PGP Secret Keyring
	 * @param iOldPassphrase - Old passphrase
	 * @param iNewPassphrase - New passphrase
	 * @throws PGPException
	 * @throws NoSuchProviderException
	 * @throws IOException
	 */
	public void changePrivateKeyPassphrase(PGPSecretKeyRing iKeyRing,
			char[] iOldPassphrase, char[] iNewPassphrase) throws PGPException, NoSuchProviderException, IOException {

		PGPSecretKeyRing newKeyring = iKeyRing;
		Iterator<PGPSecretKey> i = iKeyRing.getSecretKeys();
		while (i.hasNext()) {
			PGPSecretKey oldKey = (PGPSecretKey) i.next();
			
			PGPDigestCalculatorProvider calcProv = new JcaPGPDigestCalculatorProviderBuilder().build();
		    PBESecretKeyDecryptor decryptor = new JcePBESecretKeyDecryptorBuilder(calcProv).setProvider(PGPJavaUtil.getDefaultProvider()).build(iOldPassphrase);

		    PGPDigestCalculator calc = new JcaPGPDigestCalculatorProviderBuilder().build().get(PGPJavaUtil.getDefaultHashAlgorithm());
		    PBESecretKeyEncryptor encryptor = new JcePBESecretKeyEncryptorBuilder(PGPJavaUtil.getDefaultCipherAlgorithm(), calc).setProvider(PGPJavaUtil.getDefaultProvider()).build(iNewPassphrase);
			
			PGPSecretKey newKey = PGPSecretKey.copyWithNewPassword(oldKey, decryptor, encryptor);

			newKeyring = PGPSecretKeyRing.removeSecretKey(newKeyring, oldKey);
			newKeyring = PGPSecretKeyRing.insertSecretKey(newKeyring, newKey);
		}

		secretKeyring = PGPSecretKeyRingCollection.removeSecretKeyRing(secretKeyring, secretKeyring.getSecretKeyRing(newKeyring.getSecretKey().getKeyID()));
		secretKeyring = PGPSecretKeyRingCollection.addSecretKeyRing(secretKeyring, newKeyring);

		saveSecring();
	}

	/**
	 *
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Collection<PGPPublicKeyRingWrapper> getPublicKeys() {
		Vector<PGPPublicKeyRingWrapper> outVec = new Vector<PGPPublicKeyRingWrapper>();
		Iterator iter = publicKeyring.getKeyRings();
		while (iter.hasNext()) {
			PGPPublicKeyRing kr = (PGPPublicKeyRing) iter.next();
			outVec.add(new PGPPublicKeyRingWrapper(kr));
		}

		return outVec;
	}

	/**
	 *
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Collection<PGPSecretKeyRingWrapper> getPrivateKeys() {
		Vector<PGPSecretKeyRingWrapper> outVec = new Vector<PGPSecretKeyRingWrapper>();
		Iterator iter = secretKeyring.getKeyRings();
		while (iter.hasNext()) {
			PGPSecretKeyRing kr = (PGPSecretKeyRing) iter.next();
			outVec.add(new PGPSecretKeyRingWrapper(kr));
		}

		return outVec;
	}
	
	/**
	 * @param iDHex
	 * @return
	 */
	private String formatHexString(String iDHex) {
		if(iDHex != null && iDHex.trim().length() > 0){
			iDHex = iDHex.trim();
			iDHex = iDHex.replace("0x", "");
			iDHex = iDHex.replace("0X", "");
			
			return iDHex;
		}
		return "";
	}
	
	/**
	 * Get PGPSecretKey by Hex Key Id
	 * @param iDHex
	 * @return
	 * @throws PGPException
	 */
	@SuppressWarnings("rawtypes")
	public PGPSecretKey getSignKeyByHexKeyId(String iDHex) throws PGPException {
		String keyId = formatHexString(iDHex);
		
		Collection privateKeyRing = getPrivateKeys();
    	Iterator privateKeys = privateKeyRing.iterator();

    	PGPSecretKeyRingWrapper pgpSecretKeyRingWrapper;
    	while(privateKeys.hasNext()){
    		pgpSecretKeyRingWrapper = (PGPSecretKeyRingWrapper)privateKeys.next();

    		if(pgpSecretKeyRingWrapper.containsKeyId(keyId)){
    			return pgpSecretKeyRingWrapper.getSigningKey();
    		}
    	}
		
		return null;
	}

	/**
	 *
	 * @param iID
	 * @return
	 * @throws PGPException
	 */
	public PGPSecretKey getPrivateKeyByID(long iID) throws PGPException {
		return secretKeyring.getSecretKey(iID);
	}
	
	/**
	 * Get Encryption Key by Hex Key Id
	 * @param iDHex
	 * @return
	 * @throws PGPException
	 */
	@SuppressWarnings("rawtypes")
	public PGPPublicKey getEncryptionKeyByHexKeyId(String iDHex) throws PGPException {
		String keyId = formatHexString(iDHex);
		
		Collection publicKeyRing = getPublicKeys();
    	Iterator publicKeys = publicKeyRing.iterator();

    	PGPPublicKeyRingWrapper pgpPublicKeyRingWrapper;
    	while(publicKeys.hasNext()){
    		pgpPublicKeyRingWrapper = (PGPPublicKeyRingWrapper)publicKeys.next();

    		if(pgpPublicKeyRingWrapper.containsKeyId(keyId)){
    			return pgpPublicKeyRingWrapper.getEncryptionKey();
    		}
    	}
		
		return null;
	}

	/**
	 *
	 * @param iID
	 * @return
	 * @throws PGPException
	 */
	public PGPPublicKey getPublicKeyByID(long iID) throws PGPException {
		return publicKeyring.getPublicKey(iID);
	}

	/**
	 * Search Encryption Key by User Id
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public PGPPublicKey getEncryptionKeyByUserId(String userId){
		Collection publicKeyRing = getPublicKeys();
    	Iterator publicKeys = publicKeyRing.iterator();

    	PGPPublicKeyRingWrapper pgpPublicKeyRingWrapper;
    	while(publicKeys.hasNext()){
    		pgpPublicKeyRingWrapper = (PGPPublicKeyRingWrapper)publicKeys.next();

    		if(pgpPublicKeyRingWrapper.containsUserId(userId)){
    			return pgpPublicKeyRingWrapper.getEncryptionKey();
    		}
    	}
		return null;
	}

	/**
	 *
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public PGPPublicKeyRing getPublicKeyRingByUserId(String userId){
		Collection publicKeyRing = getPublicKeys();
    	Iterator publicKeys = publicKeyRing.iterator();

    	PGPPublicKeyRingWrapper pgpPublicKeyRingWrapper;
    	while(publicKeys.hasNext()){
    		pgpPublicKeyRingWrapper = (PGPPublicKeyRingWrapper)publicKeys.next();

    		if(pgpPublicKeyRingWrapper.matchUserId(userId)){
    			return pgpPublicKeyRingWrapper.getPublicKeyRing();
    		}
    	}
		return null;
	}

	/**
	 * Get Signing Key by UserId
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public PGPSecretKey getSigningKeyByUserId(String userId) {
		Collection privateKeyRing = getPrivateKeys();
    	Iterator privateKeys = privateKeyRing.iterator();

    	PGPSecretKeyRingWrapper pgpSecretKeyRingWrapper;
    	while(privateKeys.hasNext()){
    		pgpSecretKeyRingWrapper = (PGPSecretKeyRingWrapper)privateKeys.next();

    		if(pgpSecretKeyRingWrapper.containsUserId(userId)){
    			return pgpSecretKeyRingWrapper.getSigningKey();
    		}
    	}
    	return null;
	}

	/**
	 *
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public PGPSecretKeyRing getSecretKeyRingByUserId(String userId) {
		Collection privateKeyRing = getPrivateKeys();
    	Iterator privateKeys = privateKeyRing.iterator();

    	PGPSecretKeyRingWrapper pgpSecretKeyRingWrapper;
    	while(privateKeys.hasNext()){
    		pgpSecretKeyRingWrapper = (PGPSecretKeyRingWrapper)privateKeys.next();

    		if(pgpSecretKeyRingWrapper.matchUserId(userId)){
    			return pgpSecretKeyRingWrapper.getSecretKeyRing();
    		}
    	}
    	return null;
	}

	/**
	 * Print Private Keys
	 *
	 */
	@SuppressWarnings("rawtypes")
	public String printPrivateKeys(){

		StringBuffer sb = new StringBuffer();
		Collection privateKeyRing = getPrivateKeys();
    	Iterator privateKeys = privateKeyRing.iterator();
    	PGPSecretKeyRingWrapper pgpSecretKeyRingWrapper;

    	while(privateKeys.hasNext()){
    		pgpSecretKeyRingWrapper = (PGPSecretKeyRingWrapper)privateKeys.next();
    		sb.append(pgpSecretKeyRingWrapper + "\n");
    	}
    	
    	return sb.toString();
	}
	
	/**
	 * Print private keys with subkey details
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public String printPrivateSubKeys(){

		StringBuffer sb = new StringBuffer();
		Collection privateKeyRing = getPrivateKeys();
    	Iterator privateKeys = privateKeyRing.iterator();
    	PGPSecretKeyRingWrapper pgpSecretKeyRingWrapper;

    	while(privateKeys.hasNext()){
    		pgpSecretKeyRingWrapper = (PGPSecretKeyRingWrapper)privateKeys.next();
    		sb.append(pgpSecretKeyRingWrapper + " Subkey Id (Hex): ");
			
    		ArrayList arrayList = pgpSecretKeyRingWrapper.getSubKeyIds();    		
    		for (int i = 0; i < arrayList.size(); i++) {
    			sb.append("[0x" + arrayList.get(i).toString().toUpperCase() + "] ");
			}    		
    		sb.append("\n");
    	}
    	
    	return sb.toString();
	}


	/**
	 * Print Public Keys
	 *
	 */
	@SuppressWarnings("rawtypes")
	public String printPublicKeys(){
		
		StringBuffer sb = new StringBuffer();
		Collection publicKeyRing = getPublicKeys();
    	Iterator publicKeys = publicKeyRing.iterator();
    	PGPPublicKeyRingWrapper pgpPublicKeyRingWrapper;

    	while(publicKeys.hasNext()){
    		pgpPublicKeyRingWrapper = (PGPPublicKeyRingWrapper)publicKeys.next();
    		sb.append(pgpPublicKeyRingWrapper + "\n");
    	}
    	
    	return sb.toString();
	}
	
	/**
	 * Get list of sub key Ids
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public String printPublicSubKeys(){
		
		StringBuffer sb = new StringBuffer();
		Collection publicKeyRing = getPublicKeys();
    	Iterator publicKeys = publicKeyRing.iterator();
    	PGPPublicKeyRingWrapper pgpPublicKeyRingWrapper;

    	while(publicKeys.hasNext()){
    		pgpPublicKeyRingWrapper = (PGPPublicKeyRingWrapper)publicKeys.next();
    		sb.append(pgpPublicKeyRingWrapper + " Subkey Id (Hex): ");
    				
    		ArrayList arrayList = pgpPublicKeyRingWrapper.getSubKeyIds();    		
    		for (int i = 0; i < arrayList.size(); i++) {
    			sb.append("[0x" + arrayList.get(i).toString().toUpperCase() + "] ");
			}    		
    		sb.append("\n");
    	}
    	
    	return sb.toString();
	}

	/**
	 * Delete Public key from public keyring file
	 * @param iKey
	 * @throws IOException
	 */
	public void deletePublicKey(PGPPublicKeyRing iKey) throws IOException {
		publicKeyring = PGPPublicKeyRingCollection.removePublicKeyRing(publicKeyring, iKey);
		savePubring();
	}

	/**
	 * Delete Secret Key from secret keyring file
	 * @param iKey
	 * @throws IOException
	 */
	public void deletePrivateKey(PGPSecretKeyRing iKey) throws IOException {
		secretKeyring = PGPSecretKeyRingCollection.removeSecretKeyRing(secretKeyring, iKey);
		saveSecring();
	}

	/**
	 * Save Public Keyring File
	 * @throws IOException
	 */
	private void savePubring() throws IOException {
		OutputStream pub_out = new FileOutputStream(publicKeyringFile);
		publicKeyring.encode(pub_out);
		pub_out.close();
	}

	/**
	 * Save Secret Keyring File
	 * @throws IOException
	 */
	private void saveSecring() throws IOException {
		OutputStream sec_out = new FileOutputStream(secretKeyringFile);
		secretKeyring.encode(sec_out);
		sec_out.close();
	}

	/**
	 *
	 * @return
	 */
	public PGPPublicKeyRingCollection getPGPPublicKeyRingCollection() {
		return publicKeyring;
	}

	/**
	 *
	 * @return
	 */
	public PGPSecretKeyRingCollection getPGPSecretKeyRingCollection() {
		return secretKeyring;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public String getRepositoryName() {
		return repositoryName;
	}	
	
}
