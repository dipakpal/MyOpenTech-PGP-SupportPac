package com.ibm.broker.supportpac.pgp.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ibm.broker.config.proxy.BrokerProxy;
import com.ibm.broker.config.proxy.ConfigurableService;
import com.ibm.broker.plugin.MbBLOB;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbInputTerminal;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbNode;
import com.ibm.broker.plugin.MbNodeInterface;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;
import com.ibm.broker.supportpac.pgp.PGPEncrypter;
import com.ibm.broker.supportpac.pgp.PGPEnvironment;


/**
 * Java class for PGP Encryption and Signature generation
 * @author Dipak K Pal (IBM)
 * Created on Aug 25 2013
 */
public class PGPEncrypterNode extends MbNode implements MbNodeInterface {

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
	
	// Encryption Properties
	private String pgpConfigService = "";
	private String asciiArmor = "Yes";
	private String integrityCheck = "Yes";
	private String encryptionKeyUserId = "";
	
	// Signature Properties
	private String signatureRequired = "No";
	private String signKeyUserId = "";
	private String signKeyPassphrase = "";
	private String useDefaultSignKey = "Yes";
	
	private String defaultSignKeyUserId = "";
	private String defaultSignKeyPassphrase = null;
	
	// Advanced Properties
	private String hashAlgorithm = "SHA1";
	private String compressionAlgorithm = "ZIP";
	private String cipherAlgorithm = "CAST5";
	
	private boolean initialized = false;
	
	private static final Object lock = new Object();
	private static int count = 1;
	private String instancename = "PGPEncrypterNode-";
	
	/**
	 * Public Constructor
	 * @throws MbException
	 */
	public PGPEncrypterNode() throws MbException {
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
	 * @return
	 */
	public static String getNodeName() {
		return "PGPEncrypterNode";
	}
	
	/**
	 * Evalute method to process messages
	 */
	@SuppressWarnings("static-access")
	public void evaluate(MbMessageAssembly inAssembly, MbInputTerminal inTerm) throws MbException {
		
		// Get input message and input local environment
		MbMessage inMessage = inAssembly.getMessage();
		MbMessage localEnv = inAssembly.getLocalEnvironment();

		// create new message
		MbMessage outMessage = new MbMessage(inMessage);
		MbMessageAssembly outAssembly = null;
		
		// Declare variables		
		boolean isInputStreamOpened = true;
		boolean isOutputStreamOpened = true;
		boolean isOutputFileExist = false;
		boolean isTempOutputFileExist = false;
		String  tempOutputFileExt = ".part";
		String vOutputFileName = null;
		
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
					
					BrokerProxy b = BrokerProxy.getLocalInstance();

					// To ensure that the BrokerProxy object has been populated with data
					// from the broker before we access the configurable service,
					// wait for 180 seconds
					int waitInterval = 180;
					int waitCounter = 0;
					boolean propAvailable = b.hasBeenPopulatedByBroker();
					while (!propAvailable) {
						waitCounter = waitCounter + 1;
						
						// Sleep for 1 second
						Thread.currentThread().sleep(1000);
						
						// If 180 seconds over, throw exception
						if(waitCounter > waitInterval){
							throw new RuntimeException("Broker has not populated configurable properties yet. Please try later");
						}
						
						propAvailable = b.hasBeenPopulatedByBroker();
					}

					// As we have defined the configurable service with type 'UserDefined';
					ConfigurableService[] configurableServices = b.getConfigurableServices("UserDefined");
					ConfigurableService configurableService = null;
					for (int i = 0; i < configurableServices.length; i++) {
						if(configurableServices[i].getName().equals(getPgpConfigService())){
							configurableService = configurableServices[i];
						}
					}
					
					if(configurableService == null){
						throw new RuntimeException("PGP Configurable Service not found: " + getPgpConfigService()+ ". Please verify the UserDefined Configurable service.");
					}
					
					// Get Key Repository details
					String pgpPrivateKeyRepository = configurableService.getProperties().getProperty("PrivateKeyRepository");
					String pgpPublicKeyRepository = configurableService.getProperties().getProperty("PublicKeyRepository");
					
					// Set default values of defaultSignKeyUserId/defaultSignKeyPassphrase from Configurable Service if configured
					String pgpSignatureKeyId = configurableService.getProperties().getProperty("DefaultSignKeyUserId");
					String pgpSignaturePassPhrase = configurableService.getProperties().getProperty("DefaultSignKeyPassphrase");
					
					// Do not trim()
					if(pgpSignatureKeyId != null && pgpSignatureKeyId.trim().length() > 0){
						this.defaultSignKeyUserId = pgpSignatureKeyId;
					}
					
					// Do not trim()
					if(pgpSignaturePassPhrase != null){
						this.defaultSignKeyPassphrase = pgpSignaturePassPhrase;
					}
					
					// Disconnect BrokerProxy
					b.disconnect();
					
					// Initialize PGP Environment
					PGPEnvironment.initialize(getPgpConfigService(), pgpPrivateKeyRepository, pgpPublicKeyRepository, false);
					
					// Mark initialize
					initialized = true;
				}
				
				// Set following variables from Node properties/Configurable Service
				String vInputDirectory 	= getInputDirectory();
				String vOutputDirectory = getOutputDirectory();
				String vInputFileName 	= getInputFileName();				
				String pgpEncryptionKeyId = getEncryptionKeyUserId();
				String pgpSignatureKeyId = getSignKeyUserId();
				String pgpSignaturePassPhrase = getSignKeyPassphrase();				
				vOutputFileName 	= getOutputFileName();
				
				boolean signatureRequired = false;
				if("Yes".equals(getSignatureRequired())){
					signatureRequired = true;
				}
				
				// Overwrite pgpSignaturePassPhrase/pgpSignatureKeyId by defaultSignaturePassPhrase/defaultSignKeyUserId
				if("Yes".equals(useDefaultSignKey)){
					pgpSignaturePassPhrase = this.defaultSignKeyPassphrase;
					pgpSignatureKeyId = this.defaultSignKeyUserId;
				}
				
				// Get Local Environment variables and overwrite following variables only for current Thread of execution
				MbElement pgp = localEnv.getRootElement().getFirstElementByPath("PGP");
				if(pgp != null){
					MbElement encryption = pgp.getFirstElementByPath("Encryption");
					
					if(encryption != null){
						MbElement inputDirectoryMbElement = encryption.getFirstElementByPath("InputDirectory");
						MbElement inputFileMbElement = encryption.getFirstElementByPath("InputFileName");
						MbElement outputDirectoryMbElement = encryption.getFirstElementByPath("OutputDirectory");
						MbElement outputFileMbElement = encryption.getFirstElementByPath("OutputFileName");
						MbElement encryptionKeyUserIdMbElement = encryption.getFirstElementByPath("EncryptionKeyUserId");
						MbElement signatureRequiredMbElement = encryption.getFirstElementByPath("SignatureRequired");
						MbElement signKeyUserIdMbElement = encryption.getFirstElementByPath("SignKeyUserId");
						MbElement signKeyPassphraseMbElement = encryption.getFirstElementByPath("SignKeyPassphrase");
						
						// Overwrite following variables only for this specific thread of execution						
						String envInputDirectory = getValue(inputDirectoryMbElement);
						String envOutputDirectory = getValue(outputDirectoryMbElement);
						String envInputFileName = getValue(inputFileMbElement);
						String envOutputFileName = getValue(outputFileMbElement);
						String envEncryptionKeyUserId = getValue(encryptionKeyUserIdMbElement);
						String envSignatureRequired = getValue(signatureRequiredMbElement);
						String envSignKeyUserId = getValue(signKeyUserIdMbElement);
						String envSignKeyPassphrase = getPassphraseValue(signKeyPassphraseMbElement);
						
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
						
						// Do not trim()
						if(envEncryptionKeyUserId != null && envEncryptionKeyUserId.trim().length() > 0){
							pgpEncryptionKeyId = envEncryptionKeyUserId;
						}
						
						if(envSignatureRequired != null && envSignatureRequired.trim().length() > 0){
							if("YES".equals(envSignatureRequired.trim().toUpperCase())){
								signatureRequired = true;
							} else if("NO".equals(envSignatureRequired.trim().toUpperCase())){
								signatureRequired = false;
							} else {
								throw new IllegalArgumentException("Invalid value of SignatureRequired: " + envSignatureRequired);
							}
						}
						
						// Do not trim()
						if(envSignKeyUserId != null && envSignKeyUserId.trim().length() > 0){
							pgpSignatureKeyId = envSignKeyUserId;
						}
						
						// Do not trim()
						if(envSignKeyPassphrase != null){
							pgpSignaturePassPhrase = envSignKeyPassphrase;
						}
					}
				}			
				
				// Validate Signature Key
				if(pgpSignatureKeyId == null){
					pgpSignatureKeyId = "";
				}
				
				if(signatureRequired && pgpSignatureKeyId.trim().equals("")){
					throw new RuntimeException("PGP SignatureKey User Id required");
				}
				
				// Validate signKey passphrase
				if(signatureRequired && pgpSignaturePassPhrase == null){
					throw new RuntimeException("SignKey passphrase missing");
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
				
				// Encrypt and Sign
				if(signatureRequired){
					
					PGPEncrypter.signAndEncrypt(inStream, 
							outStream, 
							pgpSignaturePassPhrase, 
							pgpEncryptionKeyId, 
							pgpSignatureKeyId,
							getPgpConfigService(),
							getBooleanValue(asciiArmor), 
							getBooleanValue(integrityCheck),
							cipherAlgorithm,
							compressionAlgorithm,
							hashAlgorithm);
				} else {
					
					PGPEncrypter.encrypt(inStream, 
							outStream,
							pgpEncryptionKeyId, 
							getPgpConfigService(), 
							getBooleanValue(asciiArmor), 
							getBooleanValue(integrityCheck),
							cipherAlgorithm,
							compressionAlgorithm,
							hashAlgorithm);
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
				} catch (Exception e) {}
				
				// Rename File to actual file name
				if("FileSystem".equals(outputLocation)){
					File tempOutputFile = new File(tempOutputFileName);
					
					// Delete if the file already exist
					File file = new File(vOutputFileName);
					if(file.exists()){
						boolean result = file.delete();
						
						// Throw exception if the file can not be deleted
						if(!result){
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
				
				throw new MbUserException("com.ibm.broker.supportpac.pgp.impl.PGPEncrypterNode", "evaluate", "Message Encryption Failed!", msg, msg, null);
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
					throw new MbUserException("com.ibm.broker.supportpac.pgp.impl.PGPEncrypterNode", "evaluate", 
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

	public String getSignatureRequired() {
		return signatureRequired;
	}

	public void setSignatureRequired(String signatureRequired) {
		this.signatureRequired = signatureRequired;
	}

	public String getSignKeyUserId() {
		return signKeyUserId;
	}

	public void setSignKeyUserId(String signKeyUserId) {
		this.signKeyUserId = signKeyUserId;
	}

	public String getSignKeyPassphrase() {
		return signKeyPassphrase;
	}

	public void setSignKeyPassphrase(String signKeyPassphrase) {
		this.signKeyPassphrase = signKeyPassphrase;
	}

	public String getHashAlgorithm() {
		return hashAlgorithm;
	}

	public void setHashAlgorithm(String hashAlgorithm) {
		this.hashAlgorithm = hashAlgorithm;
	}

	public String getCompressionAlgorithm() {
		return compressionAlgorithm;
	}

	public void setCompressionAlgorithm(String compressionAlgorithm) {
		this.compressionAlgorithm = compressionAlgorithm;
	}

	public String getCipherAlgorithm() {
		return cipherAlgorithm;
	}

	public void setCipherAlgorithm(String cipherAlgorithm) {
		this.cipherAlgorithm = cipherAlgorithm;
	}

	public String getAsciiArmor() {
		return asciiArmor;
	}

	public void setAsciiArmor(String asciiArmor) {
		this.asciiArmor = asciiArmor;
	}

	public String getEncryptionKeyUserId() {
		return encryptionKeyUserId;
	}

	public void setEncryptionKeyUserId(String encryptionKeyUserId) {
		this.encryptionKeyUserId = encryptionKeyUserId;
	}

	public String getPgpConfigService() {
		return pgpConfigService;
	}

	public void setPgpConfigService(String pgpConfigService) {
		this.pgpConfigService = pgpConfigService;
	}	

	public String getIntegrityCheck() {
		return integrityCheck;
	}

	public void setIntegrityCheck(String integrityCheck) {
		this.integrityCheck = integrityCheck;
	}

	public String getUseDefaultSignKey() {
		return useDefaultSignKey;
	}

	public void setUseDefaultSignKey(String useDefaultSignKey) {
		this.useDefaultSignKey = useDefaultSignKey;
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
	 * @param element
	 * @return String
	 * @throws MbException
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
	 * Get Passphrase Value
	 * @param element
	 * @return
	 * @throws MbException
	 */
	private String getPassphraseValue(MbElement element) throws MbException {
		String value = null;
		if(element != null){
			value = element.getValueAsString();
		}
		return value;
	}
	
	/**
	 * Get Value as Integer
	 * @param element
	 * @return
	 * @throws MbException
	 */
	private int getValueAsInteger(MbElement element) throws MbException {
		String value = getValueAsString(element);
		if(value == null){
			value = "0";
		}
		return Integer.parseInt(value);
	}
	
	/**
	 * Get Value as String
	 * @param element
	 * @return
	 * @throws MbException
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
	
	/**
	 * Get boolean value
	 * @param value
	 * @return
	 */
	private boolean getBooleanValue(String value){
		if("Yes".equals(value)){
			return true;
		}
		return false;
	}

}
