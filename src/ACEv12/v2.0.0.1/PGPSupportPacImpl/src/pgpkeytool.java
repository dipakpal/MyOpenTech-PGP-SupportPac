
import com.ibm.broker.supportpac.pgp.PGPCommandLineDecrypter;
import com.ibm.broker.supportpac.pgp.PGPCommandLineEncrypter;
import com.ibm.broker.supportpac.pgp.PGPKeyGen;
import com.ibm.broker.supportpac.pgp.PGPKeyUtil;


/**
 * A simple utility class provides various PGP operations.
 * @version 1.0
 * @author Dipak K Pal (IBM)
 * <br><br>
 * <b>Description:</b>
 * A simple utility class provides various PGP operations.
 */
public class pgpkeytool {
	
	private static final String usage = "Supported Operations:" +
		"\ngeneratePGPKeyPair: \t\tGenerate PGP key pair." +
		"\nchangePrivateKeyPassphrase: \tChange passphrase for specified private key." +
		"\nencrypt: \t\t\tPGP Encryption." +
		"\nsignAndEncrypt: \t\tPGP Encryption with Signature." +
		"\ndecrypt: \t\t\tPGP Decryption with Signature validation." +
		"\nimportPrivateKey: \t\tImport specified Private key into Private key Repository file." +
		"\nimportPublicKey: \t\tImport specified Public key into Public key Repository file." +
		"\nexportPrivateKey: \t\tExport specified Private key from Private key Repository file into separate Private key file." +
		"\nexportPublicKey: \t\tExport specified Public key from Public key Repository file into separate Public key file." +
		"\ndeletePrivateKey: \t\tDelete specified Private key from Private key Repository file." +
		"\ndeletePublicKey: \t\tDelete specified Public key from Public key Repository file." +		
		"\nlistPrivateKeys: \t\tList all Private keys in Private key Repository file." +
		"\nlistPublicKeys: \t\tList all Public keys in Public key Repository file."+
		"\n\nFor help, execute: \t\tjava pgpkeytool [operation] -help"+
		"\nHelp example: \t\t\tjava pgpkeytool generatePGPKeyPair -help";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			
			if(args.length < 1){
				System.out.println(usage);
				System.exit(0);
			}
			
			// Get operation name
			String operation = args[0];
			if(operation != null){
				operation = operation.trim();
			}
			
			// Check for help
			boolean help = false;
			for (int i = 0; i < args.length; i++) {
				if("-help".equals(args[i])){
					help = true;
				}
			}		
			
			// Check operation name and call necessary Java functions
			if("generatePGPKeyPair".equals(operation)){
				if(help){
					PGPKeyGen.printUsage();
					System.exit(0);
				}
				
				PGPKeyGen.execute(args);
				
			} else if("encrypt".equals(operation) || "signAndEncrypt".equals(operation)){
				if(help){
					PGPCommandLineEncrypter.printUsage();
					System.exit(0);
				}
				
				PGPCommandLineEncrypter.execute(args);
				
			} else if("decrypt".equals(operation)){
				if(help){
					PGPCommandLineDecrypter.printUsage();
					System.exit(0);
				}
				
				PGPCommandLineDecrypter.execute(args);
				
			} else if("importPrivateKey".equals(operation)
				|| "importPublicKey".equals(operation)
				|| "exportPrivateKey".equals(operation)
				|| "exportPublicKey".equals(operation)
				|| "changePrivateKeyPassphrase".equals(operation)
				|| "deletePublicKey".equals(operation)
				|| "deletePrivateKey".equals(operation)
				|| "listPrivateKeys".equals(operation)
				|| "listPublicKeys".equals(operation)){
				
				if(help){
					PGPKeyUtil.printUsage();
					System.exit(0);
				}
				
				PGPKeyUtil.execute(args);
			} else {
				System.out.println("Operation not supported: "+operation);
				System.out.println("\n"+usage);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
