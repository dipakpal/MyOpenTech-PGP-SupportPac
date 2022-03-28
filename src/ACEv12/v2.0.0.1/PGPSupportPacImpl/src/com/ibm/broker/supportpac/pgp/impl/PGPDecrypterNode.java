package com.ibm.broker.supportpac.pgp.impl;

import com.ibm.broker.plugin.*;
import com.ibm.broker.supportpac.pgp.PGPDecrypter;
import com.ibm.broker.supportpac.pgp.PGPDecryptionResult;
import com.ibm.broker.supportpac.pgp.PGPEnvironment;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Java class for PGP Decryption and Signature validation
 * @author Dipak K Pal (IBM)
 * Created on Aug 25 2013
 */
public class PGPDecrypterNode extends MbNode implements MbNodeInterface {

	// Declare Node Properties with default values
	// Basic Properties
	private String fileEncryption = "No";
	private String outputLocation = "OutputRoot"; //OutputRoot/FileSystem
	private String inputDirectory = "";
	private String outputDirectory = "";
	private String inputFileName = "";
	private String outputFileName = "";
	private String replaceOutputFile = "Yes";
	private String inputFileAction = "NoAction";
	private String replaceDuplicateArchive = "Yes";
	
	// Decryption Properties
	private String pgpPolicy = "";
	private String validateSignature = "No";
	private String decryptionKeyPassphrase = "";
	private String useDefaultDecryptionKeyPassphrase = "Yes";

	private String defaultDecryptionKeyPassphrase = null;
	
	private boolean initialized = false;
	
	private static final Object lock = new Object();
	private static int count = 1;
	private String instancename = "PGPDecrypterNode-";

	/**
	 * Public Constructor
	 * @throws MbException MbException
	 */
	public PGPDecrypterNode() throws MbException {
		// create terminals here
		createInputTerminal("in");
		createOutputTerminal("out");
		
		synchronized (lock) {
			instancename = instancename + count;
			count = count + 1;
		}
	}	
	
	/**
	 * Return Node name
	 * @return node name
	 */
	public static String getNodeName() {
		return "PGPDecrypterNode";
	}
	
	/**
	 * Evalute method to process messages
	 */
	public void evaluate(MbMessageAssembly inAssembly, MbInputTerminal inTerm) throws MbException {

		// Get input message and input local environment
		MbMessage inMessage = inAssembly.getMessage();
		MbMessage localEnv = inAssembly.getLocalEnvironment();

		// create new message
		MbMessage outMessage = new MbMessage(inMessage);
		MbMessageAssembly outAssembly;
		
		// Declare variables		
		boolean isInputStreamOpened = true;
		boolean isOutputStreamOpened = true;
		boolean isOutputFileExist = false;
		boolean isTempOutputFileExist = false;
		String  tempOutputFileExt = ".part";
		String  vOutputFileName = null;
		
		InputStream  inStream  = null;
		OutputStream outStream = null;
		
		String tempOutputFileName = "";
		String vInputFileNameDelete = "";
		String archiveBaseDirectory = "";
		
		// Outer Try block for message propagation, do not catch and re-throw downstream exceptions
		// Inner Try block for Message processing in this Java Compute Node
		try {
			try {				
				// Initialize PGP Environment during first time execution
				if(!initialized){
					MbPolicy mbPol = getPolicy("UserDefined", getPgpPolicy());

					if(mbPol == null){
						throw new RuntimeException("PGP Policy not found: " + getPgpPolicy()+ ". Please verify the UserDefined Configurable service.");
					}
					
					// Get Key Repository details
					String pgpPrivateKeyRepository = mbPol.getPropertyValueAsString("PrivateKeyRepository");
					String pgpPublicKeyRepository = mbPol.getPropertyValueAsString("PublicKeyRepository");
					
					// Set default values of defaultDecryptionKeyPassphrase from Policy if configured
					String pgpDefaultDecryptionKeyPassphrase = mbPol.getPropertyValueAsString("DefaultDecryptionKeyPassphrase");
					
					// Do not trim()
					if(pgpDefaultDecryptionKeyPassphrase != null){
						this.defaultDecryptionKeyPassphrase = pgpDefaultDecryptionKeyPassphrase;
					}
					
					// Initialize PGP Environment
					PGPEnvironment.initialize(getPgpPolicy(), pgpPrivateKeyRepository, pgpPublicKeyRepository, false);
					
					// Mark initialize
					initialized = true;
				}
				
				// Set following variables from Node properties/Policy
				String vInputDirectory 	= getInputDirectory();
				String vOutputDirectory = getOutputDirectory();
				String vInputFileName 	= getInputFileName();
				String pgpDecryptionPassPhrase = getDecryptionKeyPassphrase();			
				vOutputFileName 	= getOutputFileName();
				
				boolean signatureValidationRequired = "Yes".equals(getValidateSignature());

				// Overwrite default decryption key passphrase
				if("Yes".equals(useDefaultDecryptionKeyPassphrase)){
					pgpDecryptionPassPhrase = this.defaultDecryptionKeyPassphrase;
				} else {
					if(pgpDecryptionPassPhrase == null){
						pgpDecryptionPassPhrase = "";
					}
				}
				
				// Get Local Environment variables and overwrite following variables only for current Thread of execution
				MbElement pgp = localEnv.getRootElement().getFirstElementByPath("PGP");
				if(pgp != null){
					MbElement decryption = pgp.getFirstElementByPath("Decryption");
					
					if(decryption != null){
						MbElement inputDirectoryMbElement = decryption.getFirstElementByPath("InputDirectory");
						MbElement inputFileMbElement = decryption.getFirstElementByPath("InputFileName");
						MbElement outputDirectoryMbElement = decryption.getFirstElementByPath("OutputDirectory");
						MbElement outputFileMbElement = decryption.getFirstElementByPath("OutputFileName");
						MbElement validateSignatureMbElement = decryption.getFirstElementByPath("ValidateSignature");
						MbElement decryptionKeyPassphraseMbElement = decryption.getFirstElementByPath("DecryptionKeyPassphrase");
						
						// Overwrite following variables only for this specific thread of execution						
						String envInputDirectory = getValue(inputDirectoryMbElement);
						String envOutputDirectory = getValue(outputDirectoryMbElement);
						String envInputFileName = getValue(inputFileMbElement);
						String envOutputFileName = getValue(outputFileMbElement);
						String envValidateSignature = getValue(validateSignatureMbElement);
						String envDecryptionKeyPassphrase = getPassphraseValue(decryptionKeyPassphraseMbElement);
						
						if(envInputDirectory != null && envInputDirectory.trim().length() > 0){
							vInputDirectory = envInputDirectory.trim();
						}
						
						if(envOutputDirectory != null && envOutputDirectory.trim().length() > 0){
							vOutputDirectory = envOutputDirectory.trim();
						}
						
						if(envInputFileName != null && envInputFileName.trim().length() > 0){
							vInputFileName = envInputFileName.trim();
						}
						
						if(envOutputFileName != null && envOutputFileName.trim().length() > 0){
							vOutputFileName = envOutputFileName.trim();
						}
						
						if(envValidateSignature != null && envValidateSignature.trim().length() > 0){
							if(envValidateSignature.trim().equalsIgnoreCase("YES")){
								signatureValidationRequired = true;
							} else if("NO".equalsIgnoreCase(envValidateSignature.trim())){
								signatureValidationRequired = false;
							} else {
								throw new IllegalArgumentException("Invalid value of ValidateSignature: " + envValidateSignature);
							}
						}
						
						// Do not trim()
						if(envDecryptionKeyPassphrase != null){
							pgpDecryptionPassPhrase = envDecryptionKeyPassphrase;
						}
					}
				}
				
				if(pgpDecryptionPassPhrase == null){
					throw new RuntimeException("DecryptionKey passphrase missing");
				}
				
				// Determine source type
				if("No".equals(fileEncryption)){
					// Serialize Input Data, prepare bit Stream
					MbElement properties = inMessage.getRootElement().getFirstElementByPath("Properties");
					
					String messageSet = getValueAsString(properties.getFirstElementByPath("MessageSet"));
					String messageType = getValueAsString(properties.getFirstElementByPath("MessageType"));
					String messageFormat = getValueAsString(properties.getFirstElementByPath("MessageFormat"));
					int encoding = getValueAsInteger(properties.getFirstElementByPath("Encoding"));
					int codedCharSetId = getValueAsInteger(properties.getFirstElementByPath("CodedCharSetId"));
					
					MbElement msgBody = inMessage.getRootElement().getLastChild();
					byte[] msgByteStreamIn;
					try {
						msgByteStreamIn = msgBody.toBitstream(messageType, messageSet, messageFormat, encoding, codedCharSetId, 0);
					} catch (Exception e) {
						throw new RuntimeException("Input message can not be serialized into bit-stream. " +
								"Make sure you provided correct CCSID, Encoding and/or MessageSet details at " +
								"Properties folder in input message tree. Error details: " + e.getMessage());
					}				
					inStream = new ByteArrayInputStream(msgByteStreamIn);
					
				} else if ("Yes".equals(fileEncryption)) {
					if(vInputDirectory != null && vInputFileName != null){
						archiveBaseDirectory = vInputDirectory.trim();
						vInputFileNameDelete = vInputFileName.trim();
						vInputFileName = vInputDirectory.trim() + "/" + vInputFileName.trim();
						
						File inputFile = new File(vInputFileName);
						
						if(!inputFile.exists()){
							throw new RuntimeException("Input file: [" + vInputFileName + "] does not exist");
						}
						
						inStream = new FileInputStream(inputFile);
					} else {
						throw new RuntimeException("Input File Directory/Name missing");
					}
					
				} else {
					throw new RuntimeException("Invalid input data type: " + fileEncryption);
				}
				
				// Determine the target type - OutputRoot/FileSystem
				if("OutputRoot".equals(outputLocation)){
					outStream = new ByteArrayOutputStream();
					
				} else if("FileSystem".equals(outputLocation)){
					if(vOutputDirectory != null && vOutputFileName != null){
						
						// Validate Output Dir/File name
						if(vOutputDirectory.trim().length() == 0 || vOutputFileName.trim().length() == 0){
							throw new RuntimeException("Output File Directory/Name missing");
						}
						
						vOutputFileName = vOutputDirectory.trim() + "/" + vOutputFileName.trim();
						tempOutputFileName = vOutputFileName + tempOutputFileExt;
						
						File outputFile = new File(vOutputFileName);
						File tempOutputFile = new File(tempOutputFileName);
						
						if(tempOutputFile.exists()){
							isTempOutputFileExist = true;
						}
						
						if(outputFile.exists()){
							isOutputFileExist = true;
							if("No".equals(replaceOutputFile)){
								throw new RuntimeException("Output File already exists: "+vOutputFileName);
							}							
						}
		
						outStream = new FileOutputStream(tempOutputFile);
					} else {
						throw new RuntimeException("Output File Directory/Name missing");
					}
				} else {
					throw new RuntimeException("Invalid output data type: " + outputLocation);
				}
				
				// Decrypt and validate Signature				
				PGPDecryptionResult result = PGPDecrypter.decrypt(inStream, outStream, pgpDecryptionPassPhrase, getPgpPolicy());

			    // Throw Exception if Signature validation failed
				if(result.isIsSigned() && !result.isIsSignatureValid() && signatureValidationRequired){
					throw new RuntimeException("Invalid Signature: " + result.getSignatureException().getMessage());
				}
				
				// Read data if it is ByteArrayOutputStream
				byte[] encryptedData = null;
				if("OutputRoot".equals(outputLocation)){
					ByteArrayOutputStream tempOutStream = (ByteArrayOutputStream)outStream;
					encryptedData = tempOutStream.toByteArray();
				}
				
				// Close Streams
				try {
					inStream.close();
					isInputStreamOpened = false;
					outStream.close();
					isOutputStreamOpened = false;
				} catch (Exception ignored) {}
				
				// Rename File to actual file name
				if("FileSystem".equals(outputLocation)){
					File tempOutputFile = new File(tempOutputFileName);
					
					// Delete if the file already exist
					File file = new File(vOutputFileName);
					if(file.exists()){
						boolean result1 = file.delete();
						
						// Throw exception if the file can not be deleted
						if(!result1){
							throw new RuntimeException("Output file can not be replaced. File may be in use: "+vOutputFileName);
						}
					}

					tempOutputFile.renameTo(new File(vOutputFileName));
				}
				
				// Place data in out message
				if("OutputRoot".equals(outputLocation)){				
					outMessage.getRootElement().getLastChild().detach();		        
				    outMessage.getRootElement().createElementAsLastChildFromBitstream(encryptedData, MbBLOB.PARSER_NAME, "", "", "", 0, 0, 0);
				}
			    
				// Create Out message assembly
				outAssembly = new MbMessageAssembly(inAssembly, outMessage);				

			} catch (Exception e) {
				
				String msg = e.getMessage();
				// Close in/out stream if opened			
				try {
					if((isInputStreamOpened) && (inStream != null)){
						inStream.close();
					}
					
					if((isOutputStreamOpened) && (outStream != null)){
						outStream.close();
					}
				} catch (Exception exp1) {
					msg = msg + " :: " + exp1.getMessage();
				}
				
				// Delete temporary files if created a new one
				if(!isTempOutputFileExist){
					try {
						File outputFile = new File(tempOutputFileName);			
						if(outputFile.exists()){
							outputFile.delete();
						}
					} catch (Exception exp2) {
						msg = msg + " :: " + exp2.getMessage();
					}
				}
				
				// Delete output file if created a new one
				if(!isOutputFileExist){
					try {
						File outputFile = new File(vOutputFileName);			
						if(outputFile.exists()){
							outputFile.delete();
						}
					} catch (Exception exp3) {
						msg = msg + " :: " + exp3.getMessage();
					}
				}
				
				throw new MbUserException("com.ibm.broker.supportpac.pgp2.impl.PGPDecrypterNode", "evaluate", "Message Decryption Failed!", msg, msg, null);
			}
			
			// Propagate to out terminal
			MbOutputTerminal out = getOutputTerminal("out");
			out.propagate(outAssembly);
			
			// Take appropriate action for input file
			if ("Yes".equals(fileEncryption)) {
				
				try {
					if("Delete".equals(inputFileAction)){
						File file = new File(archiveBaseDirectory + "/" + vInputFileNameDelete);
						file.delete();	
						
					} else if("Archive".equals(inputFileAction)){
						
						// Create pgparchive directory
						String archiveDirectoryName = archiveBaseDirectory + "/pgparchive";
						File archiveDir = new File(archiveDirectoryName);
						archiveDir.mkdirs();

						if (!((new File(archiveDirectoryName)).exists())) {
							throw new RuntimeException("Archive Directory can not be created: " + archiveDirectoryName);
						}
						
						String archiveFileName = archiveDirectoryName + "/" + vInputFileNameDelete;
						File oldArchiveFile = new File(archiveFileName);
						
						if(oldArchiveFile.exists()){
							if("Yes".equals(replaceDuplicateArchive)){
								oldArchiveFile.delete();
								File inputFile = new File(archiveBaseDirectory + "/" + vInputFileNameDelete);
								inputFile.renameTo(new File(archiveFileName));
							} else {
								throw new RuntimeException("Input file can not be move to archive directory. Archive file already exists: " + archiveFileName);
							}
						} else {
							File inputFile = new File(archiveBaseDirectory + "/" + vInputFileNameDelete);
							inputFile.renameTo(new File(archiveFileName));
						}					
						
					} else if("ArchiveWithTimestamp".equals(inputFileAction)){
						
						// Create pgparchive directory
						String archiveDirectoryName = archiveBaseDirectory + "/pgparchive";
						File archiveDir = new File(archiveDirectoryName);
						archiveDir.mkdirs();

						if (!((new File(archiveDirectoryName)).exists())) {
							throw new RuntimeException("Archive Directory can not be created: " + archiveDirectoryName);
						}
						
						SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
						String archiveFileName = archiveDirectoryName + "/" + vInputFileNameDelete + "." + sdf.format(new Date());
						
						File inputFile = new File(archiveBaseDirectory + "/" + vInputFileNameDelete);
						inputFile.renameTo(new File(archiveFileName));
					}
				} catch (Exception e) {
					throw new MbUserException("com.ibm.broker.supportpac.pgp2.impl.PGPDecrypterNode", "evaluate",
							"Input file can not be moved to archive directory", e.getMessage(), e.getMessage(), null);
				}
			}
			
		}  finally {			
			// clear the outMessage
			outMessage.clearMessage();
		}
	
	}
	
	

	public String getFileEncryption() {
		return fileEncryption;
	}

	public void setFileEncryption(String fileEncryption) {
		this.fileEncryption = fileEncryption;
	}

	public String getOutputLocation() {
		return outputLocation;
	}

	public void setOutputLocation(String outputLocation) {
		this.outputLocation = outputLocation;
	}

	public String getInputDirectory() {
		return inputDirectory;
	}

	public void setInputDirectory(String inputDirectory) {
		this.inputDirectory = inputDirectory;
	}

	public String getOutputDirectory() {
		return outputDirectory;
	}

	public void setOutputDirectory(String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	public String getInputFileName() {
		return inputFileName;
	}

	public void setInputFileName(String inputFileName) {
		this.inputFileName = inputFileName;
	}

	public String getOutputFileName() {
		return outputFileName;
	}

	public void setOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
	}

	public String getPgpPolicy() {
		return pgpPolicy;
	}

	public void setPgpPolicy(String pgpPolicy) {
		this.pgpPolicy = pgpPolicy;
	}

	public String getValidateSignature() {
		return validateSignature;
	}

	public void setValidateSignature(String validateSignature) {
		this.validateSignature = validateSignature;
	}

	public String getDecryptionKeyPassphrase() {
		return decryptionKeyPassphrase;
	}

	public void setDecryptionKeyPassphrase(String decryptionKeyPassphrase) {
		this.decryptionKeyPassphrase = decryptionKeyPassphrase;
	}

	public String getUseDefaultDecryptionKeyPassphrase() {
		return useDefaultDecryptionKeyPassphrase;
	}

	public void setUseDefaultDecryptionKeyPassphrase(String useDefaultDecryptionKeyPassphrase) {
		this.useDefaultDecryptionKeyPassphrase = useDefaultDecryptionKeyPassphrase;
	}

	public String getInputFileAction() {
		return inputFileAction;
	}

	public void setInputFileAction(String inputFileAction) {
		this.inputFileAction = inputFileAction;
	}

	public String getReplaceDuplicateArchive() {
		return replaceDuplicateArchive;
	}

	public void setReplaceDuplicateArchive(String replaceDuplicateArchive) {
		this.replaceDuplicateArchive = replaceDuplicateArchive;
	}

	public String getReplaceOutputFile() {
		return replaceOutputFile;
	}

	public void setReplaceOutputFile(String replaceOutputFile) {
		this.replaceOutputFile = replaceOutputFile;
	}

	/**
	 * Get string value
	 * @param element mbelement
	 * @return String value
	 * @throws MbException exception
	 */
	private String getValue(MbElement element) throws MbException {
		String value = null;
		if(element != null){
			value = element.getValueAsString();
			if(value != null && value.trim().equals("")){
				value = null;
			}
		}
		return value;
	}
	
	/**
	 * Get Value as Integer
	 * @param element element
	 * @return integer value
	 * @throws MbException exception
	 */
	private int getValueAsInteger(MbElement element) throws MbException {
		String value = getValueAsString(element);
		if(value == null){
			value = "0";
		}
		return Integer.parseInt(value);
	}
	
	/**
	 * Get Passphrase Value
	 * @param element mbelement
	 * @return element value
	 * @throws MbException exception
	 */
	private String getPassphraseValue(MbElement element) throws MbException {
		String value = null;
		if(element != null){
			value = element.getValueAsString();
		}
		return value;
	}
	
	/**
	 * Get Value as String
	 * @param element input element
	 * @return trimmed value
	 * @throws MbException exception
	 */
	private String getValueAsString(MbElement element) throws MbException {
		String value = null;
		if(element != null){
			value = element.getValueAsString();
			if(value != null && value.trim().equals("")){
				value = null;
			}
		}
		if(value != null){
			value = value.trim();
		}
		return value;
	}

}
