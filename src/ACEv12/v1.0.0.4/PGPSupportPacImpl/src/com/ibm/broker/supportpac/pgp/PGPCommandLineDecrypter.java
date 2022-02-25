package com.ibm.broker.supportpac.pgp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * A utility class for decryption and signature validation.
 * @version 1.0
 * @author Dipak K Pal (IBM)
 * <br><br>
 * <b>Description:</b>
 * A simple utility class for decryption and signature validation.
 */
public class PGPCommandLineDecrypter {
	
	private static final String usage =  "Usage: java pgpkeytool decrypt [-options] InputFileName" +
	"\njava pgpkeytool decrypt -sr PrivateKeyRepositoryFile -pr PublicKeyRepositoryFile -p DecryptionKeyPassphrase InputFileName"+
	"\n\nExample:" +
	"\njava pgpkeytool decrypt -sr C:/PGP/KeyRepository/private.pgp -pr C:/PGP/KeyRepository/public.pgp -p pgppassphrase C:/PGP/Data/Sample.txt.encrypted.asc"+
	"\n\nOptions:" +
	"\n-sr PrivateKeyRepositoryFile: \tAbsolute path of the PrivateKey Repository File."+
	"\n-pr PublicKeyRepositoryFile: \tAbsolute path of the PublicKey Repository File. Provide an empty file if you do not have a PublicKey Repository File."+
	"\n\n-p DecryptionKeyPassphrase: \tPGP passphrase for Decryption Private Key. If not provided as parameter, it will be prompted runtime."+
	"\n\nInputFileName: \t\t\tAbsolute path of the input file which is required to be encrypted. Decrypted file name will be <InputFileName>.decrypted.out";

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

			String pgpPassPhrase = getOptionValuePassphrase(args, "-p");
			
			String inputDataFile = args[args.length-1];
			
			// Validate Key Repository File
			validate(privateKeyRepositoryFile);
			validate(publicKeyRepositoryFile);
			
			// Initialize PGP Security Environment
			PGPEnvironment.initialize("PGP", privateKeyRepositoryFile, publicKeyRepositoryFile, true);
			PGPEnvironment.setDefaultKeyRepository("PGP");

			//Validate File
			File inputFile = new File(inputDataFile);
			if(!inputFile.exists()){
				System.out.println("File does not exist");
				System.out.println(usage);
		        System.exit(0);
			}

			//Get PGP Passphrase
			if(pgpPassPhrase == null){
		    	pgpPassPhrase = promptPassphrase();
			}
			
			String outputFile = inputDataFile + ".decrypted.out";
			String tempOutputFile = inputDataFile + ".decrypted.out.part";
			inputStream = new FileInputStream(inputFile);
			outputStream = new FileOutputStream(tempOutputFile);

			System.out.println("Decrypting........................................");

		    PGPDecryptionResult result = PGPDecrypter.decrypt(inputStream, outputStream, pgpPassPhrase);

		    try {
				inputStream.close();
				outputStream.close();
			} catch (Exception e) {}
		    
		    // Rename to original file name
			File file = new File(outputFile);
			if(file.exists()){
				file.delete();
			}
			file = new File(tempOutputFile);
			file.renameTo(new File(outputFile));

		    // Throw Exception if Signature validation failed
			if(result.isIsSigned() && !result.isIsSignatureValid()){
				System.out.println("Invalid Signature");
				result.getSignatureException().printStackTrace();
			}

			if(result.isIsSigned() && result.isIsSignatureValid()){
				System.out.println("Signature is validated successfully. Signature Key: " +result.getSignee());
			}
			
			if(result.isIntegrityProtected()){
				if(result.isIntegrityCheckFailure()){
					System.out.println("Integrity Check Failed!");
				} else {
					System.out.println("Integrity Check Successful");
				}
			}

			System.out.println("Decryption completed");
			System.out.println("Decrypted File: " + inputDataFile + ".decrypted.out");
		
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
	 * 
	 */
	public static void printUsage(){
		System.out.println(usage);
	}

}
