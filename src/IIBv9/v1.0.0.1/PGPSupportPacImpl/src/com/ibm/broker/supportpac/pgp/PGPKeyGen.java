package com.ibm.broker.supportpac.pgp;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * A simple utility class that generates a PGPPublicKey/PGPSecretKey pair.
 * @version 1.0
 * @author Dipak K Pal (IBM)
 * <br><br>
 * <b>Description:</b>
 * A simple utility class that generates a PGPPublicKey/PGPSecretKey pair.
 */
public class PGPKeyGen {
	
	private static final String usage = "Usage:" +
	"\njava pgpkeytool generatePGPKeyPair -sa SignatureAlgorithm -pa PublicKeyAlgorithm -i identity -a asciiArmor -k[r|d|e] keysize -c cipher -s privateKeyFile -o publicKeyFile"+
	"\n\nExample:"+
	"\njava pgpkeytool generatePGPKeyPair -sa DSA -pa RSA -i \"IBM <ibm-pgp-keys@in.ibm.com>\" -a true -kr 1024 -kd 1024 -c AES_256 -s C:/PGP/KeyRepository/SecretKey.asc -o C:/PGP/KeyRepository/PublicKey.asc"+
	"\n\nExample (All default options)"+
	"\njava pgpkeytool generatePGPKeyPair -i \"IBM <ibm-pgp-keys@in.ibm.com>\" -s C:/PGP/KeyRepository/SecretKey.asc -o C:/PGP/KeyRepository/PublicKey.asc"+
	"\n\nOptions:" +
	"\n-sa SignatureAlgorithm: (Optional) \tSupported Signature Algorithms: RSA, DSA. Default: RSA"+
	"\n-pa PublicKeyAlgorithm: (Optional) \tSupported PublicKey Algorithms: RSA, ELG. Default: RSA"+
	"\n-i  identity: \t\t\t\tKey Identity (Key User Id) e.g. \"IBM <ibm-pgp-keys@in.ibm.com>\""+
	"\n-a  asciiArmor: \t(Optional) \tASCII encoding [true|false]. Default: true"+
	"\n-k[r|d|e] keysize: \t(Optional) \tKey size. kr - RSA Key Size, kd - DSA Key Size, ke - EL GAMAL Key Size. Default: 1024 bit:"+
	"\n-c  cipher: \t\t(Optional) \tSupported Cipher Algorithms: IDEA, TRIPLE_DES, CAST5, BLOWFISH, DES, AES_128, AES_192, AES_256, TWOFISH. Default: CAST5"+
	"\n-s  privateKeyFile: \t\t\tPrivate Key File Name (Absolute path) to export the private key."+
	"\n-o  publicKeyFile: \t\t\tPublic Key File Name (Absolute path) to export the public key.";

	/**
	 * Method to perform supported operations.
	 * @param args
	 * @throws Exception
	 */
	public static void execute(String[] args) throws Exception {
		
		// Get Options			
		String identity = getOptionValue(args, "-i");		
		String asciiArmor = getOptionValue(args, "-a");
		String keysizeRSA = getOptionValue(args, "-kr");
		String keysizeDSA = getOptionValue(args, "-kd");
		String keysizeELG = getOptionValue(args, "-ke");
		String cipher = getOptionValue(args, "-c");
		String secretKeyFile = getOptionValue(args, "-s");
		String publicKeyFile = getOptionValue(args, "-o");
		
		String signatureKeyAlgorithm = getOptionValue(args, "-sa");
		String publicKeyAlgorithm = getOptionValue(args, "-pa");
		
		if(signatureKeyAlgorithm == null){
			signatureKeyAlgorithm = "RSA";
		}
		
		if(publicKeyAlgorithm == null){
			publicKeyAlgorithm = "RSA";
		}
		
		String keyAlgorithm = "[" + signatureKeyAlgorithm + "-" + publicKeyAlgorithm + "]";
		
		// Validate parameters
		validate(identity);
		validate(secretKeyFile);
		validate(publicKeyFile);

		// Set default values if not specified
		if(asciiArmor == null){
			asciiArmor = "true";
		}

		if(keysizeRSA == null){
			keysizeRSA = "1024";
		}

		if(keysizeDSA == null){
			keysizeDSA = "1024";
		}

		if(keysizeELG == null){
			keysizeELG = "1024";
		}

		if(cipher == null){
			cipher = "CAST5";
		}
		
		// Get PGP Passphrase
		System.out.println("Please enter PGP Passphrase: ");
		InputStreamReader converter = new InputStreamReader(System.in);
    	BufferedReader in = new BufferedReader(converter);
    	String prePassphrase = in.readLine();

    	System.out.println("Please Re-enter PGP Passphrase: ");
		converter = new InputStreamReader(System.in);
    	in = new BufferedReader(converter);
    	String curPassphrase = in.readLine();

    	if(!prePassphrase.equals(curPassphrase)){
    		System.out.println("Incorrect password");
    		System.out.println(usage);
    		System.exit(0);
    	}
    	
    	String passPhrase = curPassphrase;
    	
    	if(passPhrase == null){
    		passPhrase = "";
		}

		System.out.println("PGP Signature Key Algorithm: "+signatureKeyAlgorithm);
		System.out.println("PGP PublicKey Algorithm: "+publicKeyAlgorithm);

		if("[RSA-RSA]".equals(keyAlgorithm)){
			PGPRSAKeyGen.generateKeyPair(identity, passPhrase, asciiArmor, keysizeRSA, cipher, secretKeyFile, publicKeyFile);
		} else if("[DSA-RSA]".equals(keyAlgorithm)){
			PGPDSAKeyGen.generateKeyPair(identity, passPhrase, asciiArmor, keysizeRSA, keysizeDSA, cipher, secretKeyFile, publicKeyFile);
		} else if("[DSA-ELG]".equals(keyAlgorithm)){
			PGPElGamalKeyGen.generateKeyPair(identity, passPhrase, asciiArmor, keysizeDSA, keysizeELG, cipher, secretKeyFile, publicKeyFile);
		} else {
			System.out.println("Invalid Signature/Public Key Algoritms");
			System.out.println(usage);
		    System.exit(0);
		}

	}

	/**
	 *
	 * @param args
	 * @param option
	 * @return
	 * @throws Exception
	 */
	private static String getOptionValue(String[] args, String option) throws Exception {
		String key = null;
		String value = null;
		for (int i = 0; i < args.length; i++) {
			key = args[i];
			if(key != null){
				key = key.trim();
			}
			if(option.equals(key)){				
				try {
					value = args[i+1];
				} catch (Exception e) {
					System.out.println("Invalid Options");
					System.out.println(usage);
		            System.exit(0);
				}
				
				if(value != null){
					value = value.trim();
					
					if(value.length() == 0){
						value = null;
					}
				}
			}
		}

		return value;
	}
	
	/**
	 * Validate parameters
	 * @param data
	 */
	private static void validate(String data){
		if(data == null || (data != null && data.trim().length() == 0)){
			System.out.println(usage);
            System.exit(0);
		}
	}
	
	/**
	 * 
	 */
	public static void printUsage(){
		System.out.println(usage);
	}
}
