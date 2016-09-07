package com.ibm.broker.supportpac.pgp;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.SignatureException;
import java.util.Date;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPKeyPair;
import org.bouncycastle.openpgp.PGPKeyRingGenerator;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.operator.PGPDigestCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPDigestCalculatorProviderBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPKeyPair;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyEncryptorBuilder;

/**
 * A simple utility class that generates a public/secret key pair 
 * containing a DSA signing and RSA key for encryption
 * @version 1.0
 * @author Dipak K Pal (IBM)
 * <br><br>
 * <b>Description:</b>
 * A simple utility class that generates a public/secret keyring containing a DSA signing
 * key and an RSA key for encryption.
 */

public class PGPDSAKeyGen {

	/**
	 * Export PGP Key pair into specified files
	 * @param secretOut- Output stream for PGP Secret key
	 * @param publicOut- Output stream for PGP Public key
	 * @param dsaKp - DSA Key pair
	 * @param rsaKp - RSA Key pair
	 * @param identity - PGP Key User Id
	 * @param passPhrase - PGP Passphrase
	 * @param armor - ACSII Armor (true|false)
	 * @param cipher - Cipher Algorithm
	 * @throws IOException
	 * @throws InvalidKeyException
	 * @throws NoSuchProviderException
	 * @throws SignatureException
	 * @throws PGPException
	 * @throws Exception
	 */
    private static void exportKeyPair(
        OutputStream    secretOut,
        OutputStream    publicOut,
        KeyPair         dsaKp,
        KeyPair         rsaKp,
        String          identity,
        char[]          passPhrase,
        boolean         armor,
        String 			cipher)
        throws IOException, InvalidKeyException, NoSuchProviderException, SignatureException, PGPException, Exception {
    	
    	if (armor) {
            secretOut = new ArmoredOutputStream(secretOut);
        }

        PGPKeyPair        dsaKeyPair = new JcaPGPKeyPair(PGPPublicKey.DSA, dsaKp, new Date());
        PGPKeyPair        elgKeyPair = new JcaPGPKeyPair(PGPPublicKey.RSA_GENERAL, rsaKp, new Date());
        PGPDigestCalculator sha1Calc = new JcaPGPDigestCalculatorProviderBuilder().build().get(HashAlgorithmTags.SHA1);
        PGPKeyRingGenerator    keyRingGen = new PGPKeyRingGenerator(PGPSignature.POSITIVE_CERTIFICATION, dsaKeyPair,
                 identity, sha1Calc, null, null, new JcaPGPContentSignerBuilder(dsaKeyPair.getPublicKey().getAlgorithm(), PGPJavaUtil.getDefaultHashAlgorithm()), new JcePBESecretKeyEncryptorBuilder(PGPJavaUtil.getCipherAlgorithm(cipher), sha1Calc).setProvider("BC").build(passPhrase));
        
        keyRingGen.addSubKey(elgKeyPair);
        
        keyRingGen.generateSecretKeyRing().encode(secretOut);
        
        secretOut.close();
        
        if (armor) {
            publicOut = new ArmoredOutputStream(publicOut);
        }
        
        keyRingGen.generatePublicKeyRing().encode(publicOut);
        
        publicOut.close();
    }

    /**
     * Generates Key pair
     * @param identity - PGP Key User Id
     * @param passPhrase - PGP Passphrase
     * @param asciiArmor - ASCII Aromr output (true|false)
     * @param keysizeRSA - RSA Key size in bits
     * @param keysizeDSA - DSA Key size in bits
     * @param cipher - Cipher Algorithm
     * @param secretKeyFile - Secret key file path
     * @param publicKeyFile - Public key file path
     * @throws Exception
     */
    public static void generateKeyPair(
    		String identity,
    		String passPhrase,
    		String asciiArmor,
    		String keysizeRSA,
    		String keysizeDSA,
    		String cipher,
    		String secretKeyFile,
    		String publicKeyFile ) throws Exception {

    	if(asciiArmor == null){
			asciiArmor = "true";
		}

		if(cipher == null){
			cipher = "CAST5";
		}

		System.out.println("Identity: "+identity);
		System.out.println("PassPhrase: "+passPhrase);
		System.out.println("AsciiArmor: "+asciiArmor);
		System.out.println("Keysize (RSA): "+keysizeRSA);
		System.out.println("Keysize (DSA): "+keysizeDSA);
		System.out.println("Cipher Algorithm: "+cipher);
		System.out.println("SecretKeyFile: "+secretKeyFile);
		System.out.println("PublicKeyFile: "+publicKeyFile);

        Security.addProvider(new BouncyCastleProvider());

        KeyPairGenerator dsaKpg = KeyPairGenerator.getInstance("DSA","BC");
        dsaKpg.initialize(Integer.parseInt(keysizeDSA));

        KeyPair dsaKp  = dsaKpg.generateKeyPair();
        KeyPairGenerator rsaKpg = KeyPairGenerator.getInstance("RSA", "BC");
        rsaKpg.initialize(Integer.parseInt(keysizeRSA));

		KeyPair rsaKp = rsaKpg.generateKeyPair();

		FileOutputStream out1 = new FileOutputStream(secretKeyFile);
        FileOutputStream out2 = new FileOutputStream(publicKeyFile);

		exportKeyPair(out1, out2, dsaKp, rsaKp, identity, passPhrase.toCharArray(), Boolean.parseBoolean(asciiArmor), cipher);

		out1.close();
		out2.close();

		System.out.println("******************* PGP Private Key **********************\n");
		System.out.println(new String(PGPJavaUtil.readFile(secretKeyFile)));
		
		System.out.println("\n\n\n******************* PGP Public Key **********************\n");
		System.out.println(new String(PGPJavaUtil.readFile(publicKeyFile)));
    }
    
}