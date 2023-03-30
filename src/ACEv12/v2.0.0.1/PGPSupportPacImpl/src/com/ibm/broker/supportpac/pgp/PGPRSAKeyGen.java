package com.ibm.broker.supportpac.pgp;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.SignatureException;
import java.util.Date;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPKeyPair;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.operator.PGPDigestCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPDigestCalculatorProviderBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPKeyPair;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyEncryptorBuilder;

/**
 * A simple utility class that generates a RSA PGPPublicKey/PGPSecretKey pair.
 * @version 1.0
 * @author Dipak K Pal (IBM)
 * <br><br>
 * <b>Description:</b>
 * A simple utility class that generates a RSA PGPPublicKey/PGPSecretKey pair.
 */
public class PGPRSAKeyGen {

	/**
	 * Generate RSA PGP KeyPair
	 * @param secretOut - Secret Key Output stream
	 * @param publicOut - Public Key Output stream
	 * @param publicKey - RSA Public Key
	 * @param privateKey - RSA Private Key
	 * @param identity - PGP Key User Id
	 * @param passPhrase - PGP Passphrase
	 * @param armor - ASCII Armor (true|false)
	 * @param cipher - Cipher Algorithm
	 * @throws IOException
	 * @throws InvalidKeyException
	 * @throws NoSuchProviderException
	 * @throws SignatureException
	 * @throws PGPException
	 * @throws Exception
	 */
	private static void exportKeyPair(
			OutputStream 		secretOut,
			OutputStream 		publicOut,
			PublicKey 			publicKey,
			PrivateKey 			privateKey,
			String 				identity,
			char[] 				passPhrase,
			boolean 			armor,
			String 				cipher)
			throws Exception {
		
		
		if (armor) {
            secretOut = new ArmoredOutputStream(secretOut);
        }
		
		KeyPair pair = new KeyPair(publicKey, privateKey);

        PGPDigestCalculator sha1Calc = new JcaPGPDigestCalculatorProviderBuilder().build().get(HashAlgorithmTags.SHA1);
        PGPKeyPair          keyPair = new JcaPGPKeyPair(PGPPublicKey.RSA_GENERAL, pair, new Date());
        PGPSecretKey        secretKey = new PGPSecretKey(PGPSignature.DEFAULT_CERTIFICATION, keyPair, identity, sha1Calc, null, null, new JcaPGPContentSignerBuilder(keyPair.getPublicKey().getAlgorithm(), PGPJavaUtil.getDefaultHashAlgorithm()), new JcePBESecretKeyEncryptorBuilder(PGPJavaUtil.getCipherAlgorithm(cipher), sha1Calc).setProvider("BC").build(passPhrase));
        
        secretKey.encode(secretOut);
        
        secretOut.close();
        
        if (armor) {
            publicOut = new ArmoredOutputStream(publicOut);
        }

        PGPPublicKey    key = secretKey.getPublicKey();
        
        key.encode(publicOut);
        
        publicOut.close();
	}

	/**
	 * Generate KeyPair
	 * @param identity - PGP Key User Id
	 * @param passPhrase - PGP Passphrase
	 * @param asciiArmor - Ascii Aromor (true|false)
	 * @param keysize - Key size in bits
	 * @param cipher - Cipher Algorithm
	 * @param secretKeyFile - Secret Key File path
	 * @param publicKeyFile - Public Key File path
	 * @throws Exception
	 */
	 public static void generateKeyPair(
	    		String identity,
	    		String passPhrase,
	    		String asciiArmor,
	    		String keysize,
	    		String cipher,
	    		String secretKeyFile,
	    		String publicKeyFile ) throws Exception {

		if(asciiArmor == null){
			asciiArmor = "true";
		}

		if(keysize == null){
			keysize = "1024";
		}

		if(cipher == null){
			cipher = "CAST5";
		}

		System.out.println("Identity: "+identity);
		System.out.println("PassPhrase: "+passPhrase);
		System.out.println("AsciiArmor: "+asciiArmor);
		System.out.println("Keysize (RSA): "+keysize);
		System.out.println("Cipher Algorithm: "+cipher);
		System.out.println("SecretKeyFile: "+secretKeyFile);
		System.out.println("PublicKeyFile: "+publicKeyFile);

		Security.addProvider(new BouncyCastleProvider());

		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "BC");

		kpg.initialize(Integer.parseInt(keysize));

		KeyPair kp = kpg.generateKeyPair();

		FileOutputStream out1 = new FileOutputStream(secretKeyFile);
		FileOutputStream out2 = new FileOutputStream(publicKeyFile);

		exportKeyPair(out1, out2, kp.getPublic(), kp.getPrivate(), identity, passPhrase.toCharArray(), Boolean.parseBoolean(asciiArmor), cipher);

		out1.close();
		out2.close();

		System.out.println("******************* PGP Private Key **********************\n");
		System.out.println(new String(PGPJavaUtil.readFile(secretKeyFile)));
		
		System.out.println("\n\n\n******************* PGP Public Key **********************\n");
		System.out.println(new String(PGPJavaUtil.readFile(publicKeyFile)));
	 }
}
