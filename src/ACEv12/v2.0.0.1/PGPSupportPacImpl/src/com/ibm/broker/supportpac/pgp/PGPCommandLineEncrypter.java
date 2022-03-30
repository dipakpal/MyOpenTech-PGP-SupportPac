package com.ibm.broker.supportpac.pgp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * A utility class for encryption and signature generation.
 * @version 1.0
 * @author Dipak K Pal (IBM)
 * <br><br>
 * <b>Description:</b>
 * A simple utility class for encryption and signature generation.
 */
public class PGPCommandLineEncrypter {
	
	private static final String usage =  "Usage: java pgpkeytool [encrypt|signAndEncrypt] [-options] InputFileName" +
	"\n\nExample: (Encryption Only)" +
	"\njava pgpkeytool encrypt -sr PrivateKeyRepositoryFile -pr PublicKeyRepositoryFile -r RecipientPublicKeyUserId -c Cipher -z CompressionAlgorithm -o AsciiArmor -i IntegrityCheck InputFileName"+
	"\njava pgpkeytool encrypt -sr C:/PGP/KeyRepository/private.pgp -pr C:/PGP/KeyRepository/public.pgp -r \"Customer <customer-pgp-keys@customer.com>\" -c AES_256 -z ZIP -o true -i true C:/PGP/Data/Sample.txt"+
	"\n\nExample: (Sign and Encryption)" +
	"\njava pgpkeytool signAndEncrypt -sr PrivateKeyRepositoryFile -pr PublicKeyRepositoryFile -r RecipientPublicKeyUserId -u SignPrivateKeyUserId -p SignKeyPassphrase -h HashAlgorithm -c Cipher -z CompressionAlgorithm -o AsciiArmor -i IntegrityCheck InputFileName"+
	"\njava pgpkeytool signAndEncrypt -sr C:/PGP/KeyRepository/private.pgp -pr C:/PGP/KeyRepository/public.pgp -r \"Customer <customer-pgp-keys@customer.com>\" -u \"IBM <ibm-pgp-keys@in.ibm.com>\" -p pgppassphrase -h SHA1 -c AES_256 -z ZIP -o true -i true C:/PGP/Data/Sample.txt"+
	"\n\nOptions:" +
	"\n-sr PrivateKeyRepositoryFile: \t\tAbsolute path of the PrivateKey Repository file. If you do not have a private key repository file, provide an empty file. Please note that, a PrivateKey Repository file is mandatory for Signature generation."+
	"\n\n-pr PublicKeyRepositoryFile: \t\tAbsolute path of the PublicKey Repository File."+
	"\n-r RecipientPublicKeyUserId: \t\tRecipient PublicKey User Id for Encryption."+
	"\n-u SignPrivateKeyUserId: \t\tSigner PrivateKey User Id for Signature generation."+
	"\n-p Passphrase: \t\t\t\tPGP passphrase for Sign Private Key. If not provided as parameter, it will be prompted runtime."+	
	"\n\n-h HashAlgorithm: \t(Optional) \tSupported Algorithms: MD5, SHA1, RIPEMD160, MD2, SHA256, SHA384, SHA512, SHA224. Default: SHA1" +
	"\n\n-c Cipher: \t\t(Optional) \tSupported Algorithms: IDEA, TRIPLE_DES, CAST5, BLOWFISH, DES, AES_128, AES_192, AES_256, TWOFISH. Default: CAST5" +
	"\n\n-z CompressionAlgorithm:(Optional) \tSupported Algorithms: Uncompressed, ZIP, ZLIB, BZIP2. Default: ZIP" +
	"\n-o AsciiArmor: \t\t(Optional) \tASCII armored output. Values [true|false]. Default: true" +
	"\n-i IntegrityCheck: \t(Optional) \tEnable Integrity Check. Values [true|false]. Default: true" +
	"\nInputFileName : \t\t\tInput file name (Absolute Path).";

	/**
	 * Method to perform supported operations.
	 * @param args
	 * @throws Exception
	 */
	public static void execute(String[] args) throws Exception {
		
		InputStream inputStream = null;
		OutputStream outputStream = null;

		try {
			// Get all option values
			String privateKeyRepositoryFile = getOptionValue(args, "-sr");
			String publicKeyRepositoryFile = getOptionValue(args, "-pr");

			String recipientPublicKey = getOptionValue(args, "-r");
			String signPrivateKey = getOptionValue(args, "-u");
			String pgpPassPhrase = getOptionValuePassphrase(args, "-p");			
			
			String ascii = getOptionValue(args, "-o");
			String intCheck = getOptionValue(args, "-i");

			String hashAlgo = getOptionValue(args, "-h");
			String symmetricKeyAlgo = getOptionValue(args, "-c");
			String compressionAlgo = getOptionValue(args, "-z");
			
			String inputDataFile = args[args.length-1];
			
			// Get Default Algorithms			
			String hashAlgorithm = PGPEnvironment.getDefaultHashAlgorithm();
			String symmetricKeyAlgorithm = PGPEnvironment.getDefaultCipherAlgorithm();
			String compressionAlgorithm = PGPEnvironment.getDefaultCompressionAlgorithm();

			if(hashAlgo != null){
				hashAlgorithm = hashAlgo;
			}

			if(symmetricKeyAlgo != null){
				symmetricKeyAlgorithm = symmetricKeyAlgo;
			}

			if(compressionAlgo != null){
				compressionAlgorithm = compressionAlgo;
			}

			boolean encrypt = false;
			boolean signAndEncrypt = false;
			boolean asciiArmor = true;
			boolean integrityCheck = true;
			
			String operation = args[0];
			
			if("encrypt".equals(operation)){
				encrypt = true;
			}
			
			if("signAndEncrypt".equals(operation)){
				encrypt = true;
				signAndEncrypt = true;
			}
			
			if(ascii != null){
				if(encrypt){
					validateBoolean(ascii);
				}
				asciiArmor = Boolean.parseBoolean(ascii);
			}
			
			if(intCheck != null){
				if(encrypt){
					validateBoolean(intCheck);
				}
				integrityCheck = Boolean.parseBoolean(intCheck);
			}
			
			// Validate Key Repository File
			validate(privateKeyRepositoryFile);
			validate(publicKeyRepositoryFile);
			
			// Initialize PGP Security Environment
			PGPEnvironment.initialize("PGP", privateKeyRepositoryFile, publicKeyRepositoryFile, true);
			PGPEnvironment.setDefaultKeyRepository("PGP");

			//Encryption and Signature generation
			if(encrypt){
				// Validate Encryption Key
				validate(recipientPublicKey);

				//Validate File
				File inputFile = new File(inputDataFile);
				if(!inputFile.exists()){
					System.out.println("Inpput file does not exist: " + inputDataFile);
					System.out.println(usage);
			        System.exit(0);
				}
				
				String outputDataFile = inputDataFile + ".encrypted.asc";
				String tempOutputDataFile = inputDataFile + ".encrypted.asc.part";
				inputStream = new FileInputStream(inputFile);
				outputStream = new FileOutputStream(tempOutputDataFile);				
				
				// Sign and Encrypt
				if(signAndEncrypt){
					validate(signPrivateKey);
					
					// Check PGP Passphrase
					if(pgpPassPhrase == null){						
				    	pgpPassPhrase = promptPassphrase();
					}
					
					System.out.println("Encrypting........................................");					
					PGPEncrypter.signAndEncrypt(inputStream, 
							outputStream, 
							pgpPassPhrase, 
							recipientPublicKey, 
							signPrivateKey, 
							asciiArmor, 
							integrityCheck,
							symmetricKeyAlgorithm,
							compressionAlgorithm,
							hashAlgorithm);
				} else {
					System.out.println("Encrypting........................................");
					PGPEncrypter.encrypt(inputStream, 
							outputStream,
							recipientPublicKey,
							asciiArmor, 
							integrityCheck,
							symmetricKeyAlgorithm,
							compressionAlgorithm,
							hashAlgorithm);
				}
				
				try {
					inputStream.close();
					outputStream.close();
				} catch (Exception e) {}
				
				// Rename to original file name
				File file = new File(outputDataFile);
				if(file.exists()){
					file.delete();
				}
				file = new File(tempOutputDataFile);
				file.renameTo(new File(outputDataFile));

				System.out.println("Encryption completed");
				System.out.println("Encrypted File: " + inputDataFile + ".encrypted.asc");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("\n\n\n" + usage);
		} finally {
			// Close in/output stream
			try {
				inputStream.close();
				outputStream.close();
			} catch (Exception e) {}
		}

	}
	
	/**
	 * Prompt PGP Passphrase
	 * @return
	 * @throws Exception
	 */
	private static String promptPassphrase() throws Exception {
		
		System.out.println("Please enter PGP Passphrase: ");
		InputStreamReader converter = new InputStreamReader(System.in);
    	BufferedReader in = new BufferedReader(converter);
    	String prePassphrase = in.readLine();

    	System.out.println("Please Re-enter PGP Passphrase: ");
		converter = new InputStreamReader(System.in);
    	in = new BufferedReader(converter);
    	String curPassphrase = in.readLine();

    	if(!prePassphrase.equals(curPassphrase)){
    		System.out.println("Incorrect Passphrase");
    		System.out.println(usage);
    		System.exit(0);
    	}
    	
    	return prePassphrase;
	}

	/**
	 * Process runtime arguments
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
	 * Get passphrase from parameters
	 * @param args
	 * @param option
	 * @return
	 * @throws Exception
	 */
	private static String getOptionValuePassphrase(String[] args, String option) throws Exception {
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
			}
		}

		return value;
	}

	/**
	 * Validate
	 * @param data
	 */
	private static void validate(String data){
		if(data == null){
			System.out.println(usage);
            System.exit(0);
		}
	}
	
	/**
	 * Validate boolean data
	 * @param data
	 */
	private static void validateBoolean(String data){
		if("true".equals(data) || "false".equals(data)){
			// do nothing
		} else {
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
