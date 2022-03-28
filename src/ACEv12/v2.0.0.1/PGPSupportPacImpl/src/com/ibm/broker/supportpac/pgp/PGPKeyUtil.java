package com.ibm.broker.supportpac.pgp;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.security.NoSuchProviderException;

import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRing;

/**
 * A simple utility class that manages PGP key add/delete/update/import/export into/from Key repository.
 * @version 1.0
 * @author Dipak K Pal (IBM)
 * <br><br>
 * <b>Description:</b>
 * A simple utility class that manages PGP key add/delete/update/import/export into/from Key repository.
  */
public class PGPKeyUtil {
	
	private static final String usage =  "Usage: " +
	"\njava pgpkeytool importPrivateKey -sr privateKeyRepositoryFile -i asciiArmor -sf privateKeyFile"+
	"\njava pgpkeytool importPublicKey -pr publicKeyRepositoryFile -i asciiArmor -pf publicKeyFile"+
	"\njava pgpkeytool exportPrivateKey -sr privateKeyRepositoryFile -su privateKeyUserId -i asciiArmor -sf privateKeyFile"+
	"\njava pgpkeytool exportPublicKey -pr publicKeyRepositoryFile -pu publicKeyUserId -i asciiArmor -pf publicKeyFile"+
	"\njava pgpkeytool changePrivateKeyPassphrase -sr privateKeyRepositoryFile -su privateKeyUserId"+
	"\njava pgpkeytool deletePublicKey -pr publicKeyRepositoryFile -pu publicKeyUserId"+
	"\njava pgpkeytool deletePrivateKey -sr privateKeyRepositoryFile -su privateKeyUserId"+
	"\njava pgpkeytool listPrivateKeys -sr privateKeyRepositoryFile"+
	"\njava pgpkeytool listPublicKeys -pr publicKeyRepositoryFile"+
	"\n\nSupported Operations on PGP Key Repositories:"+
	"\nchangePrivateKeyPassphrase: Change passphrase for specified private key." +
	"\nimportPrivateKey: \tImport specified Private key into Private key Repository file." +
	"\nimportPublicKey: \tImport specified Public key into Public key Repository file." +
	"\nexportPrivateKey: \tExport specified Private key from Private key Repository file into separate Private key file." +
	"\nexportPublicKey: \tExport specified Public key from Public key Repository file into separate Public key file." +
	"\ndeletePrivateKey: \tDelete specified Private key from Private key Repository file." +
	"\ndeletePublicKey: \tDelete specified Public key from Public key Repository file." +		
	"\nlistPrivateKeys: \tList all Private keys in Private key Repository file." +
	"\nlistPublicKeys: \tList all Public keys in Public key Repository file."+
	"\n\nOptions:" +
	"\n-sr privateKeyRepositoryFile : \tPrivateKey Repository File (Absolute Path)."+
	"\n-pr publicKeyRepositoryFile : \tPublicKey Repository File (Absolute Path)."+
	"\n-sf privateKeyFile : \t\tPrivateKey File (Absolute Path)."+
	"\n-pf publicKeyFile : \t\tPublicKey File (Absolute Path)."+
	"\n-su privateKeyUserId : \t\tPrivateKey User Id"+
	"\n-pu publicKeyUserId : \t\tPublicKey User Id"+
	"\n-i asciiArmor [true|false] : \tWhether Key file is Ascii armored. (Optional) Default: true"+
	"\n\nExamples:"+
	"\njava pgpkeytool importPrivateKey -sr C:/PGP/KeyRepository/private.pgp -i true -sf C:/PGP/KeyRepository/SecretKey.asc"+
	"\njava pgpkeytool importPublicKey -pr C:/PGP/KeyRepository/public.pgp -i true -pf C:/PGP/KeyRepository/PublicKey.asc"+
	"\njava pgpkeytool exportPrivateKey -sr C:/PGP/KeyRepository/private.pgp -su \"IBM <ibm-pgp-keys@in.ibm.com>\" -i true -sf C:/PGP/KeyRepository/SecretKeyExported.asc"+
	"\njava pgpkeytool exportPublicKey -pr C:/PGP/KeyRepository/public.pgp -pu \"IBM <ibm-pgp-keys@in.ibm.com>\" -i true -pf C:/PGP/KeyRepository/PublicKeyExported.asc"+
	"\njava pgpkeytool changePrivateKeyPassphrase -sr C:/PGP/KeyRepository/private.pgp -su \"IBM <ibm-pgp-keys@in.ibm.com>\""+
	"\njava pgpkeytool deletePublicKey -pr C:/PGP/KeyRepository/public.pgp -pu \"IBM <ibm-pgp-keys@in.ibm.com>\""+
	"\njava pgpkeytool deletePrivateKey -sr C:/PGP/KeyRepository/private.pgp -su \"IBM <ibm-pgp-keys@in.ibm.com>\""+
	"\njava pgpkeytool listPrivateKeys -sr C:/PGP/KeyRepository/private.pgp"+
	"\njava pgpkeytool listPublicKeys -pr C:/PGP/KeyRepository/public.pgp";

	/**
	 * @param args
	 */
	public static void execute(String[] args) throws Exception {

		String operation = args[0];

		String privateKeyRepositoryFile = getOptionValue(args, "-sr");
		String publicKeyRepositoryFile = getOptionValue(args, "-pr");

		String privateKeyFile = getOptionValue(args, "-sf");
		String publicKeyFile = getOptionValue(args, "-pf");

		String privateKeyUserId = getOptionValue(args, "-su");
		String publicKeyUserId = getOptionValue(args, "-pu");
		String asciiArmorStr = getOptionValue(args, "-i");
		
		boolean asciiArmor = true;
		if(asciiArmorStr != null){
			if("false".equals(asciiArmorStr.toLowerCase())){
				asciiArmor = false;
			}
		}

		String newPassphrase = "";

		if("importPrivateKey".equalsIgnoreCase(operation)){

			validate(privateKeyRepositoryFile);
			validate(privateKeyFile);

			importPrivateKey(privateKeyRepositoryFile, privateKeyFile, asciiArmor);	
			
			printPrivateKeys(privateKeyRepositoryFile);			

		} else if("importPublicKey".equalsIgnoreCase(operation)){

			validate(publicKeyRepositoryFile);
			validate(publicKeyFile);

			importPublicKey(publicKeyRepositoryFile, publicKeyFile, asciiArmor);
			
			printPublicKeys(publicKeyRepositoryFile);

		} else if("exportPrivateKey".equalsIgnoreCase(operation)){

			validate(privateKeyRepositoryFile);
			validate(privateKeyUserId);
			validate(privateKeyFile);

			exportPrivateKey(privateKeyRepositoryFile, privateKeyUserId, privateKeyFile, asciiArmor);

		} else if("exportPublicKey".equalsIgnoreCase(operation)){

			validate(publicKeyRepositoryFile);
			validate(publicKeyUserId);
			validate(publicKeyFile);

			exportPublicKey(publicKeyRepositoryFile, publicKeyUserId, publicKeyFile, asciiArmor);

		} else if("changePrivateKeyPassphrase".equalsIgnoreCase(operation)){
			
			validate(privateKeyRepositoryFile);
			validate(privateKeyUserId);
			
			System.out.println("Please enter old PGP Passphrase: ");
			InputStreamReader converter = new InputStreamReader(System.in);
	    	BufferedReader in = new BufferedReader(converter);
	    	String oldPassphrase = in.readLine();

	    	System.out.println("Please enter new PGP Passphrase: ");
			converter = new InputStreamReader(System.in);
	    	in = new BufferedReader(converter);
	    	String newPassphrase1 = in.readLine();

	    	System.out.println("Please Re-enter new PGP Passphrase: ");
			converter = new InputStreamReader(System.in);
	    	in = new BufferedReader(converter);
	    	String newPassphrase2 = in.readLine();

	    	if(!newPassphrase1.equals(newPassphrase2)){
	    		System.out.println("Incorrect password");
	    		System.out.println(usage);
	    		System.exit(0);
	    	}

	    	newPassphrase = newPassphrase1;

			validate(privateKeyRepositoryFile);
			validate(privateKeyUserId);

			changePrivateKeyPassphrase(privateKeyRepositoryFile, privateKeyUserId, oldPassphrase, newPassphrase);

		} else if("deletePublicKey".equalsIgnoreCase(operation)){

			validate(publicKeyRepositoryFile);
			validate(publicKeyUserId);

			deletePublicKey(publicKeyRepositoryFile, publicKeyUserId);
			
			printPublicKeys(publicKeyRepositoryFile);

		} else if("deletePrivateKey".equalsIgnoreCase(operation)){

			validate(privateKeyRepositoryFile);
			validate(privateKeyUserId);

			deletePrivateKey(privateKeyRepositoryFile, privateKeyUserId);
			
			printPrivateKeys(privateKeyRepositoryFile);

		} else if("listPrivateKeys".equalsIgnoreCase(operation)){

			validate(privateKeyRepositoryFile);

			printPrivateKeys(privateKeyRepositoryFile);

		} else if("listPublicKeys".equalsIgnoreCase(operation)){

			validate(publicKeyRepositoryFile);

			printPublicKeys(publicKeyRepositoryFile);

		} else {
			System.out.println("Operation not supported: "+operation);
			System.out.println(usage);
            System.exit(0);
		}
	}

	/**
	 * Import a Private Key into PrivateKey Repository
	 * @param privateKeyRepositoryFile
	 * @param privateKeyFile
	 * @throws Exception
	 */
	public static void importPrivateKey(String privateKeyRepositoryFile, String privateKeyFile, boolean asciiArmor) throws Exception {
		
		// Create an empty file if does not exist
		File file = new File(privateKeyRepositoryFile);
		if(!file.exists()){
			FileOutputStream fos = new FileOutputStream(file);
			fos.close();
		}
		
		PGPKeyRing pgpKeyRing = new PGPKeyRing("PGP");
		pgpKeyRing.loadPrivateKeyRepository(privateKeyRepositoryFile);

		FileInputStream fis = new FileInputStream(privateKeyFile);
		byte[] data = new byte[fis.available()];
		fis.read(data);
		fis.close();
		
		if(!asciiArmor){
			data = PGPJavaUtil.encodeAsciiArmored(data);
		}
		
		pgpKeyRing.importPrivateKey(new ByteArrayInputStream(data));

		System.out.println("Private Key imported successfully: "+privateKeyFile);
		pgpKeyRing.printPrivateKeys();
	}

	/**
	 * Import a Public Key into PublicKey Repository
	 * @param publicKeyRepositoryFile
	 * @param publicKeyFile
	 * @throws Exception
	 */
	public static void importPublicKey(String publicKeyRepositoryFile, String publicKeyFile, boolean asciiArmor) throws Exception {
		
		// Create an empty file if does not exist
		File file = new File(publicKeyRepositoryFile);
		if(!file.exists()){
			FileOutputStream fos = new FileOutputStream(file);
			fos.close();
		}

		PGPKeyRing pgpKeyRing = new PGPKeyRing("PGP");
		pgpKeyRing.loadPublicKeyRepository(publicKeyRepositoryFile);

		FileInputStream fis = new FileInputStream(publicKeyFile);
		byte[] data = new byte[fis.available()];
		fis.read(data);
		fis.close();
		
		if(!asciiArmor){
			data = PGPJavaUtil.encodeAsciiArmored(data);			
		}
		
		pgpKeyRing.importPublicKey(new ByteArrayInputStream(data));

		System.out.println("Public Key imported successfully: "+publicKeyFile);
		pgpKeyRing.printPublicKeys();
	}
	
	/**
	 * Export Public Key into file
	 * @param publicKeyRepositoryFile
	 * @param publicKeyUserId
	 * @param publicKeyFile
	 * @param asciiArmor
	 * @throws Exception
	 */
	public static void exportPublicKey(String publicKeyRepositoryFile, String publicKeyUserId, String publicKeyFile, boolean asciiArmor) throws Exception {

		PGPKeyRing pgpKeyRing = new PGPKeyRing("PGP");
		pgpKeyRing.loadPublicKeyRepository(publicKeyRepositoryFile);

		pgpKeyRing.exportPublicKey(publicKeyUserId, publicKeyFile, asciiArmor);		

		System.out.println("Public Key exported successfully");
	}
	
	/**
	 * Export Private Key into file
	 * @param privateKeyRepositoryFile
	 * @param privateKeyUserId
	 * @param privateKeyFile
	 * @param asciiArmor
	 * @throws Exception
	 */
	public static void exportPrivateKey(String privateKeyRepositoryFile, String privateKeyUserId, String privateKeyFile, boolean asciiArmor) throws Exception {

		PGPKeyRing pgpKeyRing = new PGPKeyRing("PGP");
		pgpKeyRing.loadPrivateKeyRepository(privateKeyRepositoryFile);

		pgpKeyRing.exportPrivateKey(privateKeyUserId, privateKeyFile, asciiArmor);		

		System.out.println("Private Key exported successfully");
	}

	/**
	 * Change PrivateKey passphrase
	 * @param privateKeyRepositoryFile
	 * @param privateKeyUserId
	 * @param oldPassphrase
	 * @param newPassphrase
	 * @throws PGPException
	 * @throws NoSuchProviderException
	 * @throws Exception
	 */
	public static void changePrivateKeyPassphrase(
			String privateKeyRepositoryFile,
			String privateKeyUserId,
			String oldPassphrase,
			String newPassphrase) throws PGPException, NoSuchProviderException, Exception {

		PGPKeyRing pgpKeyRing = new PGPKeyRing("PGP");
		pgpKeyRing.loadPrivateKeyRepository(privateKeyRepositoryFile);		
		PGPSecretKeyRing iKeyRing = pgpKeyRing.getSecretKeyRingByUserId(privateKeyUserId);
		
		if(iKeyRing == null){
			System.out.println("PGP Private Key not found: " + privateKeyUserId);
			System.out.println(usage);
            System.exit(0);
		}
		
		char[] iOldPassphrase = oldPassphrase.toCharArray();
		char[] iNewPassphrase = newPassphrase.toCharArray();

		try {
			pgpKeyRing.changePrivateKeyPassphrase(iKeyRing, iOldPassphrase, iNewPassphrase);
		} catch (Exception e) {
			System.err.println("Passphrase can not be changed. Please verify old passphrase.");
			e.printStackTrace();
			System.out.println("\n"+usage);
            System.exit(0);
		}
		
		System.out.println("Passphrase is changed successfully");
	}

	/**
	 * Delete specified Public Key from public key repository
	 * @param publicKeyRepositoryFile
	 * @param publicKeyUserId
	 * @throws Exception
	 */
	public static void deletePublicKey(
			String publicKeyRepositoryFile,
			String publicKeyUserId) throws Exception {

		PGPKeyRing pgpKeyRing = new PGPKeyRing("PGP");
		pgpKeyRing.loadPublicKeyRepository(publicKeyRepositoryFile);
		pgpKeyRing.printPublicKeys();
		PGPPublicKeyRing iKeyRing = pgpKeyRing.getPublicKeyRingByUserId(publicKeyUserId);
		
		if(iKeyRing == null){
			System.out.println("PGP Public Key not found: " + publicKeyUserId);
			System.out.println(usage);
            System.exit(0);
		}
		
		pgpKeyRing.deletePublicKey(iKeyRing);
		System.out.println("Public Key deleted successfully: "+publicKeyUserId);
		pgpKeyRing.printPublicKeys();
	}

	/**
	 * Delete specified Private Key from private key repository
	 * @param privateKeyRepositoryFile
	 * @param privateKeyUserId
	 * @throws Exception
	 */
	public static void deletePrivateKey(
			String privateKeyRepositoryFile,
			String privateKeyUserId) throws Exception {

		PGPKeyRing pgpKeyRing = new PGPKeyRing("PGP");
		pgpKeyRing.loadPrivateKeyRepository(privateKeyRepositoryFile);
		pgpKeyRing.printPrivateKeys();
		PGPSecretKeyRing iKeyRing = pgpKeyRing.getSecretKeyRingByUserId(privateKeyUserId);
		
		if(iKeyRing == null){
			System.out.println("PGP Private Key not found: " + privateKeyUserId);
			System.out.println(usage);
            System.exit(0);
		}
		
		pgpKeyRing.deletePrivateKey(iKeyRing);
		System.out.println("Private Key deleted successfully: "+privateKeyUserId);
		pgpKeyRing.printPrivateKeys();
	}

	/**
	 * Print Private Keys
	 * @param privateKeyRepositoryFile
	 * @throws Exception
	 */
	public static void printPrivateKeys(String privateKeyRepositoryFile) throws Exception {
		PGPKeyRing pgpKeyRing = new PGPKeyRing("PGP");
		pgpKeyRing.loadPrivateKeyRepository(privateKeyRepositoryFile);
		System.out.println("\nList of PGP Private Keys with all subkeys:");
		System.out.println(pgpKeyRing.printPrivateSubKeys());
	}
	
	/**
	 * Print Public Keys
	 * @param publicKeyRepositoryFile
	 * @throws Exception
	 */
	public static void printPublicKeys(String publicKeyRepositoryFile) throws Exception {
		PGPKeyRing pgpKeyRing = new PGPKeyRing("PGP");
		pgpKeyRing.loadPublicKeyRepository(publicKeyRepositoryFile);
		System.out.println("\nList of PGP Public Keys with all subkeys:");
		System.out.println(pgpKeyRing.printPublicSubKeys());
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
		if(data == null || (data != null && data.trim().length() ==  0)){
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
