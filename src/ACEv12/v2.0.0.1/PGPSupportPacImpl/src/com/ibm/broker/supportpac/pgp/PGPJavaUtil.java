package com.ibm.broker.supportpac.pgp;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.util.Iterator;
import java.util.UUID;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.CompressionAlgorithmTags;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.bcpg.PublicKeyAlgorithmTags;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.operator.PBESecretKeyDecryptor;
import org.bouncycastle.openpgp.operator.PGPDigestCalculatorProvider;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPDigestCalculatorProviderBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;

/**
 * Common Java utility functionalities for various PGP operations.
 * @version 1.0
 * @author Dipak K Pal (IBM)
 * <br><br>
 * <b>Description:</b>
 * Common Java utility functionalities for various PGP operations.
 *
 */
public class PGPJavaUtil {
	
	/**
	 * Get Security Provider
	 * @param providerName
	 * @return
	 * @throws NoSuchProviderException
	 */
	public static Provider getProvider(String providerName) throws NoSuchProviderException {
		Provider prov = Security.getProvider(providerName);

	    if (prov == null) {
	    	throw new NoSuchProviderException("provider " + providerName + " not found.");
	    }

	    return prov;
	}

	public static byte[] compressFile(String fileName, int algorithm) throws IOException {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		PGPCompressedDataGenerator comData = new PGPCompressedDataGenerator(algorithm);
		PGPUtil.writeFileToLiteralData(comData.open(bOut), PGPLiteralData.BINARY, new File(fileName));
		comData.close();
		return bOut.toByteArray();
	}

	/**
	 * Search a secret key ring collection for a secret key corresponding to
	 * keyID if it exists.
	 *
	 * @param pgpSec
	 *            a secret key ring collection.
	 * @param keyID
	 *            keyID we want.
	 * @param pass
	 *            passphrase to decrypt secret key with.
	 * @return
	 * @throws PGPException
	 * @throws NoSuchProviderException
	 */
	public static PGPPrivateKey findSecretKey(PGPSecretKeyRingCollection pgpSec, long keyID, char[] pass) throws PGPException, NoSuchProviderException {
		PGPSecretKey pgpSecKey = pgpSec.getSecretKey(keyID);

		if (pgpSecKey == null) {
			return null;
		}
		
		PGPDigestCalculatorProvider calcProv = new JcaPGPDigestCalculatorProviderBuilder().build();
	    PBESecretKeyDecryptor decryptor = new JcePBESecretKeyDecryptorBuilder(calcProv).setProvider(PGPJavaUtil.getDefaultProvider()).build(pass);

		return pgpSecKey.extractPrivateKey(decryptor);
	}
	
	/**
	 * Read Public Key
	 * @param fileName
	 * @return
	 * @throws IOException
	 * @throws PGPException
	 */
	public static PGPPublicKey readPublicKey(String fileName) throws IOException, PGPException {
		InputStream keyIn = new BufferedInputStream(new FileInputStream(fileName));
		PGPPublicKey pubKey = readPublicKey(keyIn);
		keyIn.close();
		return pubKey;
	}	
	
	/**
	 * Read Public Key
	 * @return
	 * @throws IOException
	 * @throws PGPException
	 */
	public static PGPPublicKey readPublicKey(byte[] pgpPublicKey) throws IOException, PGPException {
		PGPPublicKey pubKey = readPublicKey(new ByteArrayInputStream(pgpPublicKey));
		return pubKey;
	}	
	

	/**
	 * A simple routine that opens a key ring file and loads the first available
	 * key suitable for encryption.
	 *
	 * @param input
	 * @return
	 * @throws IOException
	 * @throws PGPException
	 */
	public static PGPPublicKey readPublicKey(InputStream input) throws IOException, PGPException {
		PGPPublicKeyRingCollection pgpPub = new PGPPublicKeyRingCollection(PGPUtil.getDecoderStream(input), new JcaKeyFingerprintCalculator());

		Iterator<PGPPublicKeyRing> keyRingIter = pgpPub.getKeyRings();
		while (keyRingIter.hasNext()) {
			PGPPublicKeyRing keyRing = (PGPPublicKeyRing) keyRingIter.next();

			Iterator<PGPPublicKey> keyIter = keyRing.getPublicKeys();
			while (keyIter.hasNext()) {
				PGPPublicKey key = (PGPPublicKey) keyIter.next();

				if (key.isEncryptionKey()) {
					return key;
				}
			}
		}

		throw new IllegalArgumentException("Can't find encryption key in key repository.");
	}

	/**
	 * Read Secret Key
	 * @return
	 * @throws IOException
	 * @throws PGPException
	 */
	public static PGPSecretKey readSecretKey(byte[] pgpSecretKey) throws IOException, PGPException {
		PGPSecretKey secKey = readSecretKey(new ByteArrayInputStream(pgpSecretKey));
		return secKey;
	}
	
	/**
	 * Read Secret Key
	 * @param fileName
	 * @return
	 * @throws IOException
	 * @throws PGPException
	 */
	public static PGPSecretKey readSecretKey(String fileName) throws IOException, PGPException {
		InputStream keyIn = new BufferedInputStream(new FileInputStream(fileName));
		PGPSecretKey secKey = readSecretKey(keyIn);
		keyIn.close();
		return secKey;
	}

	/**
	 * A simple routine that opens a key ring file and loads the first available
	 * key suitable for signature generation.
	 *
	 * @param input
	 *            stream to read the secret key ring collection from.
	 * @return a secret key.
	 * @throws IOException
	 *             on a problem with using the input stream.
	 * @throws PGPException
	 *             if there is an issue parsing the input stream.
	 */
	public static PGPSecretKey readSecretKey(InputStream input) throws IOException, PGPException {
		PGPSecretKeyRingCollection pgpSec = new PGPSecretKeyRingCollection(PGPUtil.getDecoderStream(input), new JcaKeyFingerprintCalculator());

		Iterator<PGPSecretKeyRing> keyRingIter = pgpSec.getKeyRings();
		while (keyRingIter.hasNext()) {
			PGPSecretKeyRing keyRing = (PGPSecretKeyRing) keyRingIter.next();

			Iterator<PGPSecretKey> keyIter = keyRing.getSecretKeys();
			while (keyIter.hasNext()) {
				PGPSecretKey key = (PGPSecretKey) keyIter.next();

				if (key.isSigningKey()) {
					return key;
				}
			}
		}

		throw new IllegalArgumentException("Can't find signing key in key ring.");
	}

	/**
	 * Create temporary data file
	 * @param data
	 * @param tempDirectory
	 * @return
	 */
	public static String createFile(byte[] data, String tempDirectory, boolean isEncrypted) throws Exception {

		String dataFile = tempDirectory + "/" + getRandomFileName();

		if(isEncrypted){
			dataFile = dataFile + ".asc";
		}

		FileOutputStream fos = new FileOutputStream(new File(dataFile));
		fos.write(data);
		fos.close();

		return dataFile;
	}
	
	/**
	 * Write ASCII armored data into specified file
	 * @param data
	 * @param fileName
	 * @param asciiAromor
	 * @throws Exception
	 */
	public static void writeFile(byte[] data, String fileName, boolean asciiAromor) throws Exception {

        File outFile = new File(fileName);
        FileOutputStream fout = new FileOutputStream(outFile);
        
        if(asciiAromor){
        	OutputStream ostream = new ArmoredOutputStream(fout);
            ostream.write(data);
            ostream.close();
        } else {
        	fout.write(data);
        }
        
        fout.close();
    }
	
	/**
	 * Encode data as ASCII armored
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static byte[] encodeAsciiArmored(byte[] data) throws Exception {

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        
        OutputStream ostream = new ArmoredOutputStream(bout);
        ostream.write(data);
        ostream.close();
        
        byte[] output = bout.toByteArray();
        bout.close();
        
        return output;
    }

	/**
	 * Read File and return byte[]
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static byte[] readFile(String file) throws Exception {

		FileInputStream fis = new FileInputStream(new File(file));
		byte[] data = new byte[fis.available()];
		fis.read(data);
		fis.close();

		return data;
	}

	/**
	 * Delete Temporary Files
	 * @param fileName
	 */
	public static void deleteFile(String fileName){
		File file = new File(fileName);
		file.delete();
	}

	/**
	 * Translate Compression Algorithm
	 * @param algorithm
	 * @return
	 */
	public static int getCompressionAlgorithm(String algorithm) throws Exception {

		algorithm = algorithm.trim();

		if("UNCOMPRESSED".equalsIgnoreCase(algorithm)){
			return CompressionAlgorithmTags.UNCOMPRESSED;
		} else if("ZIP".equalsIgnoreCase(algorithm)){
			return CompressionAlgorithmTags.ZIP;
		} else if("BZIP2".equalsIgnoreCase(algorithm)){
			return CompressionAlgorithmTags.BZIP2;
		} else if("ZLIB".equalsIgnoreCase(algorithm)){
			return CompressionAlgorithmTags.ZLIB;
		} else {
			throw new NoSuchAlgorithmException("Compression Algorithm not supported :"+algorithm);
		}

	}

	/**
	 * Public Key Algorithm
	 * @param algorithm
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public static int getPublicKeyAlgorithmTags(String algorithm) throws Exception {

		algorithm = algorithm.trim();

		if("RSA_GENERAL".equalsIgnoreCase(algorithm)){
			return PublicKeyAlgorithmTags.RSA_GENERAL;
		} else if("RSA_ENCRYPT".equalsIgnoreCase(algorithm)){
			return PublicKeyAlgorithmTags.RSA_ENCRYPT;
		} else if("RSA_SIGN".equalsIgnoreCase(algorithm)){
			return PublicKeyAlgorithmTags.RSA_SIGN;
		} else if("ELGAMAL_ENCRYPT".equalsIgnoreCase(algorithm)){
			return PublicKeyAlgorithmTags.ELGAMAL_ENCRYPT;
		} else if("DSA".equalsIgnoreCase(algorithm)){
			return PublicKeyAlgorithmTags.DSA;
		} else if("EC".equalsIgnoreCase(algorithm)){
			return PublicKeyAlgorithmTags.EC;
		} else if("ECDH".equalsIgnoreCase(algorithm)){
			return PublicKeyAlgorithmTags.ECDH;
		} else if("ECDSA".equalsIgnoreCase(algorithm)){
			return PublicKeyAlgorithmTags.ECDSA;
		} else if("DIFFIE_HELLMAN".equalsIgnoreCase(algorithm)){
			return PublicKeyAlgorithmTags.DIFFIE_HELLMAN;
		} else {
			throw new NoSuchAlgorithmException("PublicKey Algorithm not supported :"+algorithm);
		}

	}

	/*
	 * Get Hash Algorithm
	 */
	public static int getHashAlgorithm(String algorithm) throws Exception {

		algorithm = algorithm.trim();

		if("MD5".equalsIgnoreCase(algorithm)){
			return HashAlgorithmTags.MD5;
		} else if("SHA1".equalsIgnoreCase(algorithm)){
			return HashAlgorithmTags.SHA1;
		} else if("RIPEMD160".equalsIgnoreCase(algorithm)){
			return HashAlgorithmTags.RIPEMD160;
		} else if("DOUBLE_SHA".equalsIgnoreCase(algorithm)){
			return HashAlgorithmTags.DOUBLE_SHA;
		} else if("MD2".equalsIgnoreCase(algorithm)){
			return HashAlgorithmTags.MD2;
		} else if("TIGER_192".equalsIgnoreCase(algorithm)){
			return HashAlgorithmTags.TIGER_192;
		} else if("HAVAL_5_160".equalsIgnoreCase(algorithm)){
			return HashAlgorithmTags.HAVAL_5_160;
		} else if("SHA256".equalsIgnoreCase(algorithm)){
			return HashAlgorithmTags.SHA256;
		} else if("SHA384".equalsIgnoreCase(algorithm)){
			return HashAlgorithmTags.SHA384;
		} else if("SHA512".equalsIgnoreCase(algorithm)){
			return HashAlgorithmTags.SHA512;
		} else if("SHA224".equalsIgnoreCase(algorithm)){
			return HashAlgorithmTags.SHA224;
		} else {
			throw new NoSuchAlgorithmException("Hash Algorithm not supported :"+algorithm);
		}

	}

	/**
	 * Translate Cipher Algorithm
	 * @param algorithm
	 * @return
	 * @throws Exception
	 */
	public static int getCipherAlgorithm(String algorithm) throws Exception {

		algorithm = algorithm.trim();

		if("NULL".equalsIgnoreCase(algorithm)){ // Plain text or unencrypted data
			return PGPEncryptedData.NULL;
		} else if("IDEA".equalsIgnoreCase(algorithm)){ // IDEA [IDEA]
			return PGPEncryptedData.IDEA;
		} else if("TRIPLE_DES".equalsIgnoreCase(algorithm)){ // Triple-DES (DES-EDE, as per spec -168 bit key derived from 192)
			return PGPEncryptedData.TRIPLE_DES;
		} else if("CAST5".equalsIgnoreCase(algorithm)){ // CAST5 (128 bit key, as per RFC 2144)
			return PGPEncryptedData.CAST5;
		} else if("BLOWFISH".equalsIgnoreCase(algorithm)){ // Blowfish (128 bit key, 16 rounds) [BLOWFISH]
			return PGPEncryptedData.BLOWFISH;
		} else if("SAFER".equalsIgnoreCase(algorithm)){ // SAFER-SK128 (13 rounds) [SAFER]
			return PGPEncryptedData.SAFER;
		} else if("DES".equalsIgnoreCase(algorithm)){ // Reserved for DES/SK
			return PGPEncryptedData.DES;
		} else if("AES_128".equalsIgnoreCase(algorithm)){ // Reserved for AES with 128-bit key
			return PGPEncryptedData.AES_128;
		} else if("AES_192".equalsIgnoreCase(algorithm)){ // Reserved for AES with 192-bit key
			return PGPEncryptedData.AES_192;
		} else if("AES_256".equalsIgnoreCase(algorithm)){ // Reserved for AES with 256-bit key
			return PGPEncryptedData.AES_256;
		} else if("TWOFISH".equalsIgnoreCase(algorithm)){ // Reserved for Twofish
			return PGPEncryptedData.TWOFISH;
		} else if("CAMELLIA_128".equalsIgnoreCase(algorithm)){ // Reserved for CAMELLIA_128
			return PGPEncryptedData.CAMELLIA_128;
		} else if("CAMELLIA_192".equalsIgnoreCase(algorithm)){ // Reserved for CAMELLIA_192
			return PGPEncryptedData.CAMELLIA_192;
		} else if("CAMELLIA_256".equalsIgnoreCase(algorithm)){ // Reserved for Twofish
			return PGPEncryptedData.CAMELLIA_256;
		} else {
			throw new NoSuchAlgorithmException("Cipher Algorithm not supported :"+algorithm);
		}

	}

	/**
	 *
	 * @return
	 */
	public static byte[] getLineSeparator(){
		String lineSeparator = System.getProperty("line.separator");
		return lineSeparator.getBytes();
	}

	/**
	 *
	 * @param data
	 * @param index
	 * @return
	 */
	public static String getLine(String data, Long index){

		String[] list = data.split("\n");
		int len = list.length;

		int cnt = 0;
		for (int i = 0; i < len; i++) {
			String line = list[i];
			line = line.replaceAll("\r", "");

			if(line != null && line.trim().length() > 0){
				cnt++;
				if(index.intValue() == cnt){
					return line;
				}
			}
		}

		return null;
	}

	/**
	 *
	 * @param data
	 * @return
	 */
	public static String formatData(String data){

		String[] list = data.split("\n");
		int len = list.length;

		StringBuffer buf = new StringBuffer();
		boolean first = true;

		for (int i = 0; i < len; i++) {
			String line = list[i];
			line = line.replaceAll("\r", "");

			if(line != null && line.trim().length() > 0){
				if(first){
					buf.append(line);
					first = false;
				} else {
					buf.append("\n");
					buf.append(line);
				}
			}
		}

		return buf.toString();
	}
	
	/**
	 * Generate Random file name
	 * @return
	 */
	public static final String getRandomFileName(){

		String uuid = UUID.randomUUID().toString();
		uuid = uuid.replaceAll("-", "");

		return uuid + ".dat";
	}

	/**
	 *
	 * @return
	 */
	public static String getNewLineCharacter(){
		return "\n";
	}
	
	/**
	 * Format with correct path separator
	 * @param filePath
	 * @return
	 */
	public static String formatFilePath(String filePath){
		
		System.out.println(filePath);
		
		if(filePath != null && filePath.length() > 0){
			filePath = filePath.replaceAll("\\\\", "/");
			filePath = filePath.replaceAll("\"", "/");
			filePath = filePath.trim();
			while(filePath.charAt(filePath.length()-1) == '/' && filePath.length() != 1){
				filePath = filePath.substring(0,filePath.length()-1);
			}
		}
		
		System.out.println(filePath);
		
		return filePath;
	}
	
	/**
	 * 
	 * @return
	 */
	public static int getDefaultHashAlgorithm(){
		return HashAlgorithmTags.SHA256;
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getDefaultProvider(){
		return "BC";
	}
	
	/**
	 * 
	 * @return
	 */
	public static int getDefaultCipherAlgorithm(){
		return PGPEncryptedData.AES_256;
	}
}