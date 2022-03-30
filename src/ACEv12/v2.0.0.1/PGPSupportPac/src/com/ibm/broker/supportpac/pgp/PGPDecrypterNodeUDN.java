package com.ibm.broker.supportpac.pgp;

import com.ibm.broker.config.appdev.InputTerminal;
import com.ibm.broker.config.appdev.Node;
import com.ibm.broker.config.appdev.NodeProperty;
import com.ibm.broker.config.appdev.OutputTerminal;


import static com.ibm.broker.supportpac.pgp.PGPDecrypterNodeUDN.ENUM_PGPDECRYPTER_INPUTFILEACTION.*;

/*** 
 * <p>  <I>PGPDecrypterNodeUDN</I> instance</p>
 * <p></p>
 */
public class PGPDecrypterNodeUDN extends Node {

	private static final long serialVersionUID = 1L;

	// Node constants
	protected final static String NODE_TYPE_NAME = "com/ibm/broker/supportpac/pgp/PGPDecrypterNode";
	protected final static String NODE_GRAPHIC_16 = "platform:/plugin/PGPSupportPac/icons/full/obj16/com/ibm/broker/supportpac/pgp/PGPDecrypter.gif";
	protected final static String NODE_GRAPHIC_32 = "platform:/plugin/PGPSupportPac/icons/full/obj30/com/ibm/broker/supportpac/pgp/PGPDecrypter.gif";

	protected final static String PROPERTY_FILEENCRYPTION = "fileEncryption";
	protected final static String PROPERTY_OUTPUTLOCATION = "outputLocation";
	protected final static String PROPERTY_INPUTDIRECTORY = "inputDirectory";
	protected final static String PROPERTY_OUTPUTDIRECTORY = "outputDirectory";
	protected final static String PROPERTY_INPUTFILENAME = "inputFileName";
	protected final static String PROPERTY_OUTPUTFILENAME = "outputFileName";
	protected final static String PROPERTY_REPLACEOUTPUTFILE = "replaceOutputFile";
	protected final static String PROPERTY_INPUTFILEACTION = "inputFileAction";
	protected final static String PROPERTY_REPLACEDUPLICATEARCHIVE = "replaceDuplicateArchive";
	protected final static String PROPERTY_PGPPOLICY = "pgpPolicy";
	protected final static String PROPERTY_VALIDATESIGNATURE = "validateSignature";
	protected final static String PROPERTY_USEDEFAULTDECRYPTIONKEYPASSPHRASE = "useDefaultDecryptionKeyPassphrase";
	protected final static String PROPERTY_DECRYPTIONKEYPASSPHRASE = "decryptionKeyPassphrase";


	/**
	 * <I>ENUM_PGPDECRYPTER_INPUTFILEACTION</I>
	 * <pre>
	 * ENUM_PGPDECRYPTER_INPUTFILEACTION.NoAction = No Action
	 * ENUM_PGPDECRYPTER_INPUTFILEACTION.Delete = Delete
	 * ENUM_PGPDECRYPTER_INPUTFILEACTION.Archive = Move to Archive
	 * ENUM_PGPDECRYPTER_INPUTFILEACTION.ArchiveWithTimestamp = Add Timestamp and Move to Archive
	 * </pre>
	 */
	public static class ENUM_PGPDECRYPTER_INPUTFILEACTION {
		private String value;

		public static final ENUM_PGPDECRYPTER_INPUTFILEACTION NoAction = new ENUM_PGPDECRYPTER_INPUTFILEACTION("NoAction");
		public static final ENUM_PGPDECRYPTER_INPUTFILEACTION Delete = new ENUM_PGPDECRYPTER_INPUTFILEACTION("Delete");
		public static final ENUM_PGPDECRYPTER_INPUTFILEACTION Archive = new ENUM_PGPDECRYPTER_INPUTFILEACTION("Archive");
		public static final ENUM_PGPDECRYPTER_INPUTFILEACTION ArchiveWithTimestamp = new ENUM_PGPDECRYPTER_INPUTFILEACTION("ArchiveWithTimestamp");

		protected ENUM_PGPDECRYPTER_INPUTFILEACTION(String value) {
			this.value = value;
		}
		public String toString() {
			return value;
		}

		protected static ENUM_PGPDECRYPTER_INPUTFILEACTION getEnumFromString(String enumValue) {
			ENUM_PGPDECRYPTER_INPUTFILEACTION enumConst = NoAction;
			if (Delete.value.equals(enumValue)) enumConst = Delete;
			if (Archive.value.equals(enumValue)) enumConst = Archive;
			if (ArchiveWithTimestamp.value.equals(enumValue)) enumConst = ArchiveWithTimestamp;
			return enumConst;
		}

		public static String[] values = new String[]{ "NoAction", "Delete", "Archive", "ArchiveWithTimestamp" };

	}

	/**
	 * <I>ENUM_PGPDECRYPTER_REPLACEDUPLICATEARCHIVE</I>
	 * <pre>
	 * ENUM_PGPDECRYPTER_REPLACEDUPLICATEARCHIVE.Yes = Yes
	 * ENUM_PGPDECRYPTER_REPLACEDUPLICATEARCHIVE.No = No
	 * </pre>
	 */
	public static class ENUM_PGPDECRYPTER_REPLACEDUPLICATEARCHIVE {
		private String value;

		public static final ENUM_PGPDECRYPTER_REPLACEDUPLICATEARCHIVE Yes = new ENUM_PGPDECRYPTER_REPLACEDUPLICATEARCHIVE("Yes");
		public static final ENUM_PGPDECRYPTER_REPLACEDUPLICATEARCHIVE No = new ENUM_PGPDECRYPTER_REPLACEDUPLICATEARCHIVE("No");

		protected ENUM_PGPDECRYPTER_REPLACEDUPLICATEARCHIVE(String value) {
			this.value = value;
		}
		public String toString() {
			return value;
		}

		protected static ENUM_PGPDECRYPTER_REPLACEDUPLICATEARCHIVE getEnumFromString(String enumValue) {
			ENUM_PGPDECRYPTER_REPLACEDUPLICATEARCHIVE enumConst = ENUM_PGPDECRYPTER_REPLACEDUPLICATEARCHIVE.Yes;
			if (ENUM_PGPDECRYPTER_REPLACEDUPLICATEARCHIVE.No.value.equals(enumValue)) enumConst = ENUM_PGPDECRYPTER_REPLACEDUPLICATEARCHIVE.No;
			return enumConst;
		}

		public static String[] values = new String[]{ "Yes", "No" };

	}

	/**
	 * <I>ENUM_PGPDECRYPTER_VALIDATESIGNATURE</I>
	 * <pre>
	 * ENUM_PGPDECRYPTER_VALIDATESIGNATURE.No = No
	 * ENUM_PGPDECRYPTER_VALIDATESIGNATURE.Yes = Yes
	 * </pre>
	 */
	public static class ENUM_PGPDECRYPTER_VALIDATESIGNATURE {
		private String value;

		public static final ENUM_PGPDECRYPTER_VALIDATESIGNATURE No = new ENUM_PGPDECRYPTER_VALIDATESIGNATURE("No");
		public static final ENUM_PGPDECRYPTER_VALIDATESIGNATURE Yes = new ENUM_PGPDECRYPTER_VALIDATESIGNATURE("Yes");

		protected ENUM_PGPDECRYPTER_VALIDATESIGNATURE(String value) {
			this.value = value;
		}
		public String toString() {
			return value;
		}

		protected static ENUM_PGPDECRYPTER_VALIDATESIGNATURE getEnumFromString(String enumValue) {
			ENUM_PGPDECRYPTER_VALIDATESIGNATURE enumConst = ENUM_PGPDECRYPTER_VALIDATESIGNATURE.No;
			if (ENUM_PGPDECRYPTER_VALIDATESIGNATURE.Yes.value.equals(enumValue)) enumConst = ENUM_PGPDECRYPTER_VALIDATESIGNATURE.Yes;
			return enumConst;
		}

		public static String[] values = new String[]{ "No", "Yes" };

	}

	/**
	 * <I>ENUM_PGPDECRYPTER_USEDEFAULTDECRYPTIONKEYPASSPHRASE</I>
	 * <pre>
	 * ENUM_PGPDECRYPTER_USEDEFAULTDECRYPTIONKEYPASSPHRASE.Yes = Yes
	 * ENUM_PGPDECRYPTER_USEDEFAULTDECRYPTIONKEYPASSPHRASE.No = No
	 * </pre>
	 */
	public static class ENUM_PGPDECRYPTER_USEDEFAULTDECRYPTIONKEYPASSPHRASE {
		private String value;

		public static final ENUM_PGPDECRYPTER_USEDEFAULTDECRYPTIONKEYPASSPHRASE Yes = new ENUM_PGPDECRYPTER_USEDEFAULTDECRYPTIONKEYPASSPHRASE("Yes");
		public static final ENUM_PGPDECRYPTER_USEDEFAULTDECRYPTIONKEYPASSPHRASE No = new ENUM_PGPDECRYPTER_USEDEFAULTDECRYPTIONKEYPASSPHRASE("No");

		protected ENUM_PGPDECRYPTER_USEDEFAULTDECRYPTIONKEYPASSPHRASE(String value) {
			this.value = value;
		}
		public String toString() {
			return value;
		}

		protected static ENUM_PGPDECRYPTER_USEDEFAULTDECRYPTIONKEYPASSPHRASE getEnumFromString(String enumValue) {
			ENUM_PGPDECRYPTER_USEDEFAULTDECRYPTIONKEYPASSPHRASE enumConst = ENUM_PGPDECRYPTER_USEDEFAULTDECRYPTIONKEYPASSPHRASE.Yes;
			if (ENUM_PGPDECRYPTER_USEDEFAULTDECRYPTIONKEYPASSPHRASE.No.value.equals(enumValue)) enumConst = ENUM_PGPDECRYPTER_USEDEFAULTDECRYPTIONKEYPASSPHRASE.No;
			return enumConst;
		}

		public static String[] values = new String[]{ "Yes", "No" };

	}

	/**
	 * <I>ENUM_PGPDECRYPTER_FILEENCRYPTION</I>
	 * <pre>
	 * ENUM_PGPDECRYPTER_FILEENCRYPTION.No = No
	 * ENUM_PGPDECRYPTER_FILEENCRYPTION.Yes = Yes
	 * </pre>
	 */
	public static class ENUM_PGPDECRYPTER_FILEENCRYPTION {
		private String value;

		public static final ENUM_PGPDECRYPTER_FILEENCRYPTION No = new ENUM_PGPDECRYPTER_FILEENCRYPTION("No");
		public static final ENUM_PGPDECRYPTER_FILEENCRYPTION Yes = new ENUM_PGPDECRYPTER_FILEENCRYPTION("Yes");

		protected ENUM_PGPDECRYPTER_FILEENCRYPTION(String value) {
			this.value = value;
		}
		public String toString() {
			return value;
		}

		protected static ENUM_PGPDECRYPTER_FILEENCRYPTION getEnumFromString(String enumValue) {
			ENUM_PGPDECRYPTER_FILEENCRYPTION enumConst = ENUM_PGPDECRYPTER_FILEENCRYPTION.No;
			if (ENUM_PGPDECRYPTER_FILEENCRYPTION.Yes.value.equals(enumValue)) enumConst = ENUM_PGPDECRYPTER_FILEENCRYPTION.Yes;
			return enumConst;
		}

		public static String[] values = new String[]{ "No", "Yes" };

	}

	/**
	 * <I>ENUM_PGPDECRYPTER_OUTPUTLOCATION</I>
	 * <pre>
	 * ENUM_PGPDECRYPTER_OUTPUTLOCATION.OutputRoot = Output Message Tree
	 * ENUM_PGPDECRYPTER_OUTPUTLOCATION.FileSystem = File System
	 * </pre>
	 */
	public static class ENUM_PGPDECRYPTER_OUTPUTLOCATION {
		private String value;

		public static final ENUM_PGPDECRYPTER_OUTPUTLOCATION OutputRoot = new ENUM_PGPDECRYPTER_OUTPUTLOCATION("OutputRoot");
		public static final ENUM_PGPDECRYPTER_OUTPUTLOCATION FileSystem = new ENUM_PGPDECRYPTER_OUTPUTLOCATION("FileSystem");

		protected ENUM_PGPDECRYPTER_OUTPUTLOCATION(String value) {
			this.value = value;
		}
		public String toString() {
			return value;
		}

		protected static ENUM_PGPDECRYPTER_OUTPUTLOCATION getEnumFromString(String enumValue) {
			ENUM_PGPDECRYPTER_OUTPUTLOCATION enumConst = ENUM_PGPDECRYPTER_OUTPUTLOCATION.OutputRoot;
			if (ENUM_PGPDECRYPTER_OUTPUTLOCATION.FileSystem.value.equals(enumValue)) enumConst = ENUM_PGPDECRYPTER_OUTPUTLOCATION.FileSystem;
			return enumConst;
		}

		public static String[] values = new String[]{ "OutputRoot", "FileSystem" };

	}

	/**
	 * <I>ENUM_PGPDECRYPTER_REPLACEOUTPUTFILE</I>
	 * <pre>
	 * ENUM_PGPDECRYPTER_REPLACEOUTPUTFILE.Yes = Yes
	 * ENUM_PGPDECRYPTER_REPLACEOUTPUTFILE.No = No
	 * </pre>
	 */
	public static class ENUM_PGPDECRYPTER_REPLACEOUTPUTFILE {
		private String value;

		public static final ENUM_PGPDECRYPTER_REPLACEOUTPUTFILE Yes = new ENUM_PGPDECRYPTER_REPLACEOUTPUTFILE("Yes");
		public static final ENUM_PGPDECRYPTER_REPLACEOUTPUTFILE No = new ENUM_PGPDECRYPTER_REPLACEOUTPUTFILE("No");

		protected ENUM_PGPDECRYPTER_REPLACEOUTPUTFILE(String value) {
			this.value = value;
		}
		public String toString() {
			return value;
		}

		protected static ENUM_PGPDECRYPTER_REPLACEOUTPUTFILE getEnumFromString(String enumValue) {
			ENUM_PGPDECRYPTER_REPLACEOUTPUTFILE enumConst = ENUM_PGPDECRYPTER_REPLACEOUTPUTFILE.Yes;
			if (ENUM_PGPDECRYPTER_REPLACEOUTPUTFILE.No.value.equals(enumValue)) enumConst = ENUM_PGPDECRYPTER_REPLACEOUTPUTFILE.No;
			return enumConst;
		}

		public static String[] values = new String[]{ "Yes", "No" };

	}
	protected NodeProperty[] getNodeProperties() {
		return new NodeProperty[] {
			new NodeProperty(PGPDecrypterNodeUDN.PROPERTY_FILEENCRYPTION,		NodeProperty.Usage.OPTIONAL,	false,	NodeProperty.Type.ENUMERATION, "No", ENUM_PGPDECRYPTER_FILEENCRYPTION.class,"","",	"com/ibm/broker/supportpac/pgp/PGPDecrypter",	"PGPSupportPac"),
			new NodeProperty(PGPDecrypterNodeUDN.PROPERTY_OUTPUTLOCATION,		NodeProperty.Usage.OPTIONAL,	false,	NodeProperty.Type.ENUMERATION, "OutputRoot", ENUM_PGPDECRYPTER_OUTPUTLOCATION.class,"","",	"com/ibm/broker/supportpac/pgp/PGPDecrypter",	"PGPSupportPac"),
			new NodeProperty(PGPDecrypterNodeUDN.PROPERTY_INPUTDIRECTORY,		NodeProperty.Usage.OPTIONAL,	true,	NodeProperty.Type.STRING, null,"","",	"com/ibm/broker/supportpac/pgp/PGPDecrypter",	"PGPSupportPac"),
			new NodeProperty(PGPDecrypterNodeUDN.PROPERTY_OUTPUTDIRECTORY,		NodeProperty.Usage.OPTIONAL,	true,	NodeProperty.Type.STRING, null,"","",	"com/ibm/broker/supportpac/pgp/PGPDecrypter",	"PGPSupportPac"),
			new NodeProperty(PGPDecrypterNodeUDN.PROPERTY_INPUTFILENAME,		NodeProperty.Usage.OPTIONAL,	true,	NodeProperty.Type.STRING, null,"","",	"com/ibm/broker/supportpac/pgp/PGPDecrypter",	"PGPSupportPac"),
			new NodeProperty(PGPDecrypterNodeUDN.PROPERTY_OUTPUTFILENAME,		NodeProperty.Usage.OPTIONAL,	true,	NodeProperty.Type.STRING, null,"","",	"com/ibm/broker/supportpac/pgp/PGPDecrypter",	"PGPSupportPac"),
			new NodeProperty(PGPDecrypterNodeUDN.PROPERTY_REPLACEOUTPUTFILE,		NodeProperty.Usage.OPTIONAL,	false,	NodeProperty.Type.ENUMERATION, "Yes", ENUM_PGPDECRYPTER_REPLACEOUTPUTFILE.class,"","",	"com/ibm/broker/supportpac/pgp/PGPDecrypter",	"PGPSupportPac"),
			new NodeProperty(PGPDecrypterNodeUDN.PROPERTY_INPUTFILEACTION,		NodeProperty.Usage.OPTIONAL,	false,	NodeProperty.Type.ENUMERATION, "NoAction", ENUM_PGPDECRYPTER_INPUTFILEACTION.class,"","",	"com/ibm/broker/supportpac/pgp/PGPDecrypter",	"PGPSupportPac"),
			new NodeProperty(PGPDecrypterNodeUDN.PROPERTY_REPLACEDUPLICATEARCHIVE,		NodeProperty.Usage.OPTIONAL,	false,	NodeProperty.Type.ENUMERATION, "Yes", ENUM_PGPDECRYPTER_REPLACEDUPLICATEARCHIVE.class,"","",	"com/ibm/broker/supportpac/pgp/PGPDecrypter",	"PGPSupportPac"),
			new NodeProperty(PGPDecrypterNodeUDN.PROPERTY_PGPPOLICY,		NodeProperty.Usage.MANDATORY,	true,	NodeProperty.Type.STRING, null,"","",	"com/ibm/broker/supportpac/pgp/PGPDecrypter",	"PGPSupportPac"),
			new NodeProperty(PGPDecrypterNodeUDN.PROPERTY_VALIDATESIGNATURE,		NodeProperty.Usage.OPTIONAL,	false,	NodeProperty.Type.ENUMERATION, "No", ENUM_PGPDECRYPTER_VALIDATESIGNATURE.class,"","",	"com/ibm/broker/supportpac/pgp/PGPDecrypter",	"PGPSupportPac"),
			new NodeProperty(PGPDecrypterNodeUDN.PROPERTY_USEDEFAULTDECRYPTIONKEYPASSPHRASE,		NodeProperty.Usage.OPTIONAL,	false,	NodeProperty.Type.ENUMERATION, "Yes", ENUM_PGPDECRYPTER_USEDEFAULTDECRYPTIONKEYPASSPHRASE.class,"","",	"com/ibm/broker/supportpac/pgp/PGPDecrypter",	"PGPSupportPac"),
			new NodeProperty(PGPDecrypterNodeUDN.PROPERTY_DECRYPTIONKEYPASSPHRASE,		NodeProperty.Usage.OPTIONAL,	true,	NodeProperty.Type.STRING, null,"","",	"com/ibm/broker/supportpac/pgp/PGPDecrypter",	"PGPSupportPac")
		};
	}

	public PGPDecrypterNodeUDN() {
	}

	public final InputTerminal INPUT_TERMINAL_IN = new InputTerminal(this,"InTerminal.in");
	@Override
	public InputTerminal[] getInputTerminals() {
		return new InputTerminal[] {
			INPUT_TERMINAL_IN
	};
	}

	public final OutputTerminal OUTPUT_TERMINAL_OUT = new OutputTerminal(this,"OutTerminal.out");
	@Override
	public OutputTerminal[] getOutputTerminals() {
		return new OutputTerminal[] {
			OUTPUT_TERMINAL_OUT
		};
	}

	@Override
	public String getTypeName() {
		return NODE_TYPE_NAME;
	}

	protected String getGraphic16() {
		return NODE_GRAPHIC_16;
	}

	protected String getGraphic32() {
		return NODE_GRAPHIC_32;
	}

	/**
	 * Set the <I>PGPDecrypterNodeUDN</I> "<I>File Decryption</I>" property
	 * 
	 * @param value ENUM_PGPDECRYPTER_FILEENCRYPTION ; the value to set the property "<I>File Decryption</I>"
	 */
	public PGPDecrypterNodeUDN setFileEncryption(ENUM_PGPDECRYPTER_FILEENCRYPTION value) {
		setProperty(PGPDecrypterNodeUDN.PROPERTY_FILEENCRYPTION, value.toString());
		return this;
	}

	/**
	 * Get the <I>PGPDecrypterNodeUDN</I> "<I>File Decryption</I>" property
	 * 
	 * @return ENUM_PGPDECRYPTER_FILEENCRYPTION; the value of the property "<I>File Decryption</I>"
	 */
	public ENUM_PGPDECRYPTER_FILEENCRYPTION getFileEncryption() {
		return ENUM_PGPDECRYPTER_FILEENCRYPTION.getEnumFromString((String)getPropertyValue(PGPDecrypterNodeUDN.PROPERTY_FILEENCRYPTION));
	}

	/**
	 * Set the <I>PGPDecrypterNodeUDN</I> "<I>Output Location</I>" property
	 * 
	 * @param value ENUM_PGPDECRYPTER_OUTPUTLOCATION ; the value to set the property "<I>Output Location</I>"
	 */
	public PGPDecrypterNodeUDN setOutputLocation(ENUM_PGPDECRYPTER_OUTPUTLOCATION value) {
		setProperty(PGPDecrypterNodeUDN.PROPERTY_OUTPUTLOCATION, value.toString());
		return this;
	}

	/**
	 * Get the <I>PGPDecrypterNodeUDN</I> "<I>Output Location</I>" property
	 * 
	 * @return ENUM_PGPDECRYPTER_OUTPUTLOCATION; the value of the property "<I>Output Location</I>"
	 */
	public ENUM_PGPDECRYPTER_OUTPUTLOCATION getOutputLocation() {
		return ENUM_PGPDECRYPTER_OUTPUTLOCATION.getEnumFromString((String)getPropertyValue(PGPDecrypterNodeUDN.PROPERTY_OUTPUTLOCATION));
	}

	/**
	 * Set the <I>PGPDecrypterNodeUDN</I> "<I>Input Directory</I>" property
	 * 
	 * @param value String ; the value to set the property "<I>Input Directory</I>"
	 */
	public PGPDecrypterNodeUDN setInputDirectory(String value) {
		setProperty(PGPDecrypterNodeUDN.PROPERTY_INPUTDIRECTORY, value);
		return this;
	}

	/**
	 * Get the <I>PGPDecrypterNodeUDN</I> "<I>Input Directory</I>" property
	 * 
	 * @return String; the value of the property "<I>Input Directory</I>"
	 */
	public String getInputDirectory() {
		return (String)getPropertyValue(PGPDecrypterNodeUDN.PROPERTY_INPUTDIRECTORY);
	}

	/**
	 * Set the <I>PGPDecrypterNodeUDN</I> "<I>Output Directory</I>" property
	 * 
	 * @param value String ; the value to set the property "<I>Output Directory</I>"
	 */
	public PGPDecrypterNodeUDN setOutputDirectory(String value) {
		setProperty(PGPDecrypterNodeUDN.PROPERTY_OUTPUTDIRECTORY, value);
		return this;
	}

	/**
	 * Get the <I>PGPDecrypterNodeUDN</I> "<I>Output Directory</I>" property
	 * 
	 * @return String; the value of the property "<I>Output Directory</I>"
	 */
	public String getOutputDirectory() {
		return (String)getPropertyValue(PGPDecrypterNodeUDN.PROPERTY_OUTPUTDIRECTORY);
	}

	/**
	 * Set the <I>PGPDecrypterNodeUDN</I> "<I>InputFile Name</I>" property
	 * 
	 * @param value String ; the value to set the property "<I>InputFile Name</I>"
	 */
	public PGPDecrypterNodeUDN setInputFileName(String value) {
		setProperty(PGPDecrypterNodeUDN.PROPERTY_INPUTFILENAME, value);
		return this;
	}

	/**
	 * Get the <I>PGPDecrypterNodeUDN</I> "<I>InputFile Name</I>" property
	 * 
	 * @return String; the value of the property "<I>InputFile Name</I>"
	 */
	public String getInputFileName() {
		return (String)getPropertyValue(PGPDecrypterNodeUDN.PROPERTY_INPUTFILENAME);
	}

	/**
	 * Set the <I>PGPDecrypterNodeUDN</I> "<I>OutputFile Name</I>" property
	 * 
	 * @param value String ; the value to set the property "<I>OutputFile Name</I>"
	 */
	public PGPDecrypterNodeUDN setOutputFileName(String value) {
		setProperty(PGPDecrypterNodeUDN.PROPERTY_OUTPUTFILENAME, value);
		return this;
	}

	/**
	 * Get the <I>PGPDecrypterNodeUDN</I> "<I>OutputFile Name</I>" property
	 * 
	 * @return String; the value of the property "<I>OutputFile Name</I>"
	 */
	public String getOutputFileName() {
		return (String)getPropertyValue(PGPDecrypterNodeUDN.PROPERTY_OUTPUTFILENAME);
	}

	/**
	 * Set the <I>PGPDecrypterNodeUDN</I> "<I>Replace OutputFile</I>" property
	 * 
	 * @param value ENUM_PGPDECRYPTER_REPLACEOUTPUTFILE ; the value to set the property "<I>Replace OutputFile</I>"
	 */
	public PGPDecrypterNodeUDN setReplaceOutputFile(ENUM_PGPDECRYPTER_REPLACEOUTPUTFILE value) {
		setProperty(PGPDecrypterNodeUDN.PROPERTY_REPLACEOUTPUTFILE, value.toString());
		return this;
	}

	/**
	 * Get the <I>PGPDecrypterNodeUDN</I> "<I>Replace OutputFile</I>" property
	 * 
	 * @return ENUM_PGPDECRYPTER_REPLACEOUTPUTFILE; the value of the property "<I>Replace OutputFile</I>"
	 */
	public ENUM_PGPDECRYPTER_REPLACEOUTPUTFILE getReplaceOutputFile() {
		return ENUM_PGPDECRYPTER_REPLACEOUTPUTFILE.getEnumFromString((String)getPropertyValue(PGPDecrypterNodeUDN.PROPERTY_REPLACEOUTPUTFILE));
	}

	/**
	 * Set the <I>PGPDecrypterNodeUDN</I> "<I>InputFile Action</I>" property
	 * 
	 * @param value ENUM_PGPDECRYPTER_INPUTFILEACTION ; the value to set the property "<I>InputFile Action</I>"
	 */
	public PGPDecrypterNodeUDN setInputFileAction(ENUM_PGPDECRYPTER_INPUTFILEACTION value) {
		setProperty(PGPDecrypterNodeUDN.PROPERTY_INPUTFILEACTION, value.toString());
		return this;
	}

	/**
	 * Get the <I>PGPDecrypterNodeUDN</I> "<I>InputFile Action</I>" property
	 * 
	 * @return ENUM_PGPDECRYPTER_INPUTFILEACTION; the value of the property "<I>InputFile Action</I>"
	 */
	public ENUM_PGPDECRYPTER_INPUTFILEACTION getInputFileAction() {
		return getEnumFromString((String)getPropertyValue(PGPDecrypterNodeUDN.PROPERTY_INPUTFILEACTION));
	}

	/**
	 * Set the <I>PGPDecrypterNodeUDN</I> "<I>Replace Duplicate Archive</I>" property
	 * 
	 * @param value ENUM_PGPDECRYPTER_REPLACEDUPLICATEARCHIVE ; the value to set the property "<I>Replace Duplicate Archive</I>"
	 */
	public PGPDecrypterNodeUDN setReplaceDuplicateArchive(ENUM_PGPDECRYPTER_REPLACEDUPLICATEARCHIVE value) {
		setProperty(PGPDecrypterNodeUDN.PROPERTY_REPLACEDUPLICATEARCHIVE, value.toString());
		return this;
	}

	/**
	 * Get the <I>PGPDecrypterNodeUDN</I> "<I>Replace Duplicate Archive</I>" property
	 * 
	 * @return ENUM_PGPDECRYPTER_REPLACEDUPLICATEARCHIVE; the value of the property "<I>Replace Duplicate Archive</I>"
	 */
	public ENUM_PGPDECRYPTER_REPLACEDUPLICATEARCHIVE getReplaceDuplicateArchive() {
		return ENUM_PGPDECRYPTER_REPLACEDUPLICATEARCHIVE.getEnumFromString((String)getPropertyValue(PGPDecrypterNodeUDN.PROPERTY_REPLACEDUPLICATEARCHIVE));
	}

	/**
	 * Set the <I>PGPDecrypterNodeUDN</I> "<I>PGP Configurable Service</I>" property
	 * 
	 * @param value String ; the value to set the property "<I>PGP Configurable Service</I>"
	 */
	public PGPDecrypterNodeUDN setPgpPolicy(String value) {
		setProperty(PGPDecrypterNodeUDN.PROPERTY_PGPPOLICY, value);
		return this;
	}

	/**
	 * Get the <I>PGPDecrypterNodeUDN</I> "<I>PGP Configurable Service</I>" property
	 * 
	 * @return String; the value of the property "<I>PGP Configurable Service</I>"
	 */
	public String getPgpPolicy() {
		return (String)getPropertyValue(PGPDecrypterNodeUDN.PROPERTY_PGPPOLICY);
	}

	/**
	 * Set the <I>PGPDecrypterNodeUDN</I> "<I>Validate Signature</I>" property
	 * 
	 * @param value ENUM_PGPDECRYPTER_VALIDATESIGNATURE ; the value to set the property "<I>Validate Signature</I>"
	 */
	public PGPDecrypterNodeUDN setValidateSignature(ENUM_PGPDECRYPTER_VALIDATESIGNATURE value) {
		setProperty(PGPDecrypterNodeUDN.PROPERTY_VALIDATESIGNATURE, value.toString());
		return this;
	}

	/**
	 * Get the <I>PGPDecrypterNodeUDN</I> "<I>Validate Signature</I>" property
	 * 
	 * @return ENUM_PGPDECRYPTER_VALIDATESIGNATURE; the value of the property "<I>Validate Signature</I>"
	 */
	public ENUM_PGPDECRYPTER_VALIDATESIGNATURE getValidateSignature() {
		return ENUM_PGPDECRYPTER_VALIDATESIGNATURE.getEnumFromString((String)getPropertyValue(PGPDecrypterNodeUDN.PROPERTY_VALIDATESIGNATURE));
	}

	/**
	 * Set the <I>PGPDecrypterNodeUDN</I> "<I>Use Default DecryptionKey Passphrase</I>" property
	 * 
	 * @param value ENUM_PGPDECRYPTER_USEDEFAULTDECRYPTIONKEYPASSPHRASE ; the value to set the property "<I>Use Default DecryptionKey Passphrase</I>"
	 */
	public PGPDecrypterNodeUDN setUseDefaultDecryptionKeyPassphrase(ENUM_PGPDECRYPTER_USEDEFAULTDECRYPTIONKEYPASSPHRASE value) {
		setProperty(PGPDecrypterNodeUDN.PROPERTY_USEDEFAULTDECRYPTIONKEYPASSPHRASE, value.toString());
		return this;
	}

	/**
	 * Get the <I>PGPDecrypterNodeUDN</I> "<I>Use Default DecryptionKey Passphrase</I>" property
	 * 
	 * @return ENUM_PGPDECRYPTER_USEDEFAULTDECRYPTIONKEYPASSPHRASE; the value of the property "<I>Use Default DecryptionKey Passphrase</I>"
	 */
	public ENUM_PGPDECRYPTER_USEDEFAULTDECRYPTIONKEYPASSPHRASE getUseDefaultDecryptionKeyPassphrase() {
		return ENUM_PGPDECRYPTER_USEDEFAULTDECRYPTIONKEYPASSPHRASE.getEnumFromString((String)getPropertyValue(PGPDecrypterNodeUDN.PROPERTY_USEDEFAULTDECRYPTIONKEYPASSPHRASE));
	}

	/**
	 * Set the <I>PGPDecrypterNodeUDN</I> "<I>DecryptionKey Passphrase</I>" property
	 * 
	 * @param value String ; the value to set the property "<I>DecryptionKey Passphrase</I>"
	 */
	public PGPDecrypterNodeUDN setDecryptionKeyPassphrase(String value) {
		setProperty(PGPDecrypterNodeUDN.PROPERTY_DECRYPTIONKEYPASSPHRASE, value);
		return this;
	}

	/**
	 * Get the <I>PGPDecrypterNodeUDN</I> "<I>DecryptionKey Passphrase</I>" property
	 * 
	 * @return String; the value of the property "<I>DecryptionKey Passphrase</I>"
	 */
	public String getDecryptionKeyPassphrase() {
		return (String)getPropertyValue(PGPDecrypterNodeUDN.PROPERTY_DECRYPTIONKEYPASSPHRASE);
	}

	public String getNodeName() {
		String retVal = super.getNodeName();
		if ((retVal==null) || retVal.equals(""))
			retVal = "PGP Decrypter";
		return retVal;
	}
}
