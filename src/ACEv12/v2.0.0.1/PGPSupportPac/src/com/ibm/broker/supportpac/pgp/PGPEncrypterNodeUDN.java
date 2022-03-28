package com.ibm.broker.supportpac.pgp;

import com.ibm.broker.config.appdev.InputTerminal;
import com.ibm.broker.config.appdev.Node;
import com.ibm.broker.config.appdev.NodeProperty;
import com.ibm.broker.config.appdev.OutputTerminal;

import static com.ibm.broker.supportpac.pgp.PGPEncrypterNodeUDN.ENUM_PGPENCRYPTER_REPLACEDUPLICATEARCHIVE.*;

/*** 
 * <p>  <I>PGPEncrypterNodeUDN</I> instance</p>
 * <p></p>
 */
public class PGPEncrypterNodeUDN extends Node {

	private static final long serialVersionUID = 1L;

	// Node constants
	protected final static String NODE_TYPE_NAME = "com/ibm/broker/supportpac/pgp/PGPEncrypterNode";
	protected final static String NODE_GRAPHIC_16 = "platform:/plugin/PGPSupportPac/icons/full/obj16/com/ibm/broker/supportpac/pgp/PGPEncrypter.gif";
	protected final static String NODE_GRAPHIC_32 = "platform:/plugin/PGPSupportPac/icons/full/obj30/com/ibm/broker/supportpac/pgp/PGPEncrypter.gif";

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
	protected final static String PROPERTY_ENCRYPTIONKEYUSERID = "encryptionKeyUserId";
	protected final static String PROPERTY_ASCIIARMOR = "asciiArmor";
	protected final static String PROPERTY_INTEGRITYCHECK = "integrityCheck";
	protected final static String PROPERTY_SIGNATUREREQUIRED = "signatureRequired";
	protected final static String PROPERTY_USEDEFAULTSIGNKEY = "useDefaultSignKey";
	protected final static String PROPERTY_SIGNKEYUSERID = "signKeyUserId";
	protected final static String PROPERTY_SIGNKEYPASSPHRASE = "signKeyPassphrase";
	protected final static String PROPERTY_HASHALGORITHM = "hashAlgorithm";
	protected final static String PROPERTY_CIPHERALGORITHM = "cipherAlgorithm";
	protected final static String PROPERTY_COMPRESSIONALGORITHM = "compressionAlgorithm";


	/**
	 * <I>ENUM_PGPENCRYPTER_SIGNATUREREQUIRED</I>
	 * <pre>
	 * ENUM_PGPENCRYPTER_SIGNATUREREQUIRED.No = No
	 * ENUM_PGPENCRYPTER_SIGNATUREREQUIRED.Yes = Yes
	 * </pre>
	 */
	public static class ENUM_PGPENCRYPTER_SIGNATUREREQUIRED {
		private String value;

		public static final ENUM_PGPENCRYPTER_SIGNATUREREQUIRED No = new ENUM_PGPENCRYPTER_SIGNATUREREQUIRED("No");
		public static final ENUM_PGPENCRYPTER_SIGNATUREREQUIRED Yes = new ENUM_PGPENCRYPTER_SIGNATUREREQUIRED("Yes");

		protected ENUM_PGPENCRYPTER_SIGNATUREREQUIRED(String value) {
			this.value = value;
		}
		public String toString() {
			return value;
		}

		protected static ENUM_PGPENCRYPTER_SIGNATUREREQUIRED getEnumFromString(String enumValue) {
			ENUM_PGPENCRYPTER_SIGNATUREREQUIRED enumConst = ENUM_PGPENCRYPTER_SIGNATUREREQUIRED.No;
			if (ENUM_PGPENCRYPTER_SIGNATUREREQUIRED.Yes.value.equals(enumValue)) enumConst = ENUM_PGPENCRYPTER_SIGNATUREREQUIRED.Yes;
			return enumConst;
		}

		public static String[] values = new String[]{ "No", "Yes" };

	}

	/**
	 * <I>ENUM_PGPENCRYPTER_INPUTFILEACTION</I>
	 * <pre>
	 * ENUM_PGPENCRYPTER_INPUTFILEACTION.NoAction = No Action
	 * ENUM_PGPENCRYPTER_INPUTFILEACTION.Delete = Delete
	 * ENUM_PGPENCRYPTER_INPUTFILEACTION.Archive = Move to Archive
	 * ENUM_PGPENCRYPTER_INPUTFILEACTION.ArchiveWithTimestamp = Add Timestamp and Move to Archive 
	 * </pre>
	 */
	public static class ENUM_PGPENCRYPTER_INPUTFILEACTION {
		private String value;

		public static final ENUM_PGPENCRYPTER_INPUTFILEACTION NoAction = new ENUM_PGPENCRYPTER_INPUTFILEACTION("NoAction");
		public static final ENUM_PGPENCRYPTER_INPUTFILEACTION Delete = new ENUM_PGPENCRYPTER_INPUTFILEACTION("Delete");
		public static final ENUM_PGPENCRYPTER_INPUTFILEACTION Archive = new ENUM_PGPENCRYPTER_INPUTFILEACTION("Archive");
		public static final ENUM_PGPENCRYPTER_INPUTFILEACTION ArchiveWithTimestamp = new ENUM_PGPENCRYPTER_INPUTFILEACTION("ArchiveWithTimestamp");

		protected ENUM_PGPENCRYPTER_INPUTFILEACTION(String value) {
			this.value = value;
		}
		public String toString() {
			return value;
		}

		protected static ENUM_PGPENCRYPTER_INPUTFILEACTION getEnumFromString(String enumValue) {
			ENUM_PGPENCRYPTER_INPUTFILEACTION enumConst = ENUM_PGPENCRYPTER_INPUTFILEACTION.NoAction;
			if (ENUM_PGPENCRYPTER_INPUTFILEACTION.Delete.value.equals(enumValue)) enumConst = ENUM_PGPENCRYPTER_INPUTFILEACTION.Delete;
			if (ENUM_PGPENCRYPTER_INPUTFILEACTION.Archive.value.equals(enumValue)) enumConst = ENUM_PGPENCRYPTER_INPUTFILEACTION.Archive;
			if (ENUM_PGPENCRYPTER_INPUTFILEACTION.ArchiveWithTimestamp.value.equals(enumValue)) enumConst = ENUM_PGPENCRYPTER_INPUTFILEACTION.ArchiveWithTimestamp;
			return enumConst;
		}

		public static String[] values = new String[]{ "NoAction", "Delete", "Archive", "ArchiveWithTimestamp" };

	}

	/**
	 * <I>ENUM_PGPENCRYPTER_REPLACEDUPLICATEARCHIVE</I>
	 * <pre>
	 * ENUM_PGPENCRYPTER_REPLACEDUPLICATEARCHIVE.Yes = Yes
	 * ENUM_PGPENCRYPTER_REPLACEDUPLICATEARCHIVE.No = No
	 * </pre>
	 */
	public static class ENUM_PGPENCRYPTER_REPLACEDUPLICATEARCHIVE {
		private String value;

		public static final ENUM_PGPENCRYPTER_REPLACEDUPLICATEARCHIVE Yes = new ENUM_PGPENCRYPTER_REPLACEDUPLICATEARCHIVE("Yes");
		public static final ENUM_PGPENCRYPTER_REPLACEDUPLICATEARCHIVE No = new ENUM_PGPENCRYPTER_REPLACEDUPLICATEARCHIVE("No");

		protected ENUM_PGPENCRYPTER_REPLACEDUPLICATEARCHIVE(String value) {
			this.value = value;
		}
		public String toString() {
			return value;
		}

		protected static ENUM_PGPENCRYPTER_REPLACEDUPLICATEARCHIVE getEnumFromString(String enumValue) {
			ENUM_PGPENCRYPTER_REPLACEDUPLICATEARCHIVE enumConst = Yes;
			if (No.value.equals(enumValue)) enumConst = No;
			return enumConst;
		}

		public static String[] values = new String[]{ "Yes", "No" };

	}

	/**
	 * <I>ENUM_PGPENCRYPTER_ASCIIARMOR</I>
	 * <pre>
	 * ENUM_PGPENCRYPTER_ASCIIARMOR.Yes = Yes
	 * ENUM_PGPENCRYPTER_ASCIIARMOR.No = No
	 * </pre>
	 */
	public static class ENUM_PGPENCRYPTER_ASCIIARMOR {
		private String value;

		public static final ENUM_PGPENCRYPTER_ASCIIARMOR Yes = new ENUM_PGPENCRYPTER_ASCIIARMOR("Yes");
		public static final ENUM_PGPENCRYPTER_ASCIIARMOR No = new ENUM_PGPENCRYPTER_ASCIIARMOR("No");

		protected ENUM_PGPENCRYPTER_ASCIIARMOR(String value) {
			this.value = value;
		}
		public String toString() {
			return value;
		}

		protected static ENUM_PGPENCRYPTER_ASCIIARMOR getEnumFromString(String enumValue) {
			ENUM_PGPENCRYPTER_ASCIIARMOR enumConst = ENUM_PGPENCRYPTER_ASCIIARMOR.Yes;
			if (ENUM_PGPENCRYPTER_ASCIIARMOR.No.value.equals(enumValue)) enumConst = ENUM_PGPENCRYPTER_ASCIIARMOR.No;
			return enumConst;
		}

		public static String[] values = new String[]{ "Yes", "No" };

	}

	/**
	 * <I>ENUM_PGPENCRYPTER_INTEGRITYCHECK</I>
	 * <pre>
	 * ENUM_PGPENCRYPTER_INTEGRITYCHECK.Yes = Yes
	 * ENUM_PGPENCRYPTER_INTEGRITYCHECK.No = No
	 * </pre>
	 */
	public static class ENUM_PGPENCRYPTER_INTEGRITYCHECK {
		private String value;

		public static final ENUM_PGPENCRYPTER_INTEGRITYCHECK Yes = new ENUM_PGPENCRYPTER_INTEGRITYCHECK("Yes");
		public static final ENUM_PGPENCRYPTER_INTEGRITYCHECK No = new ENUM_PGPENCRYPTER_INTEGRITYCHECK("No");

		protected ENUM_PGPENCRYPTER_INTEGRITYCHECK(String value) {
			this.value = value;
		}
		public String toString() {
			return value;
		}

		protected static ENUM_PGPENCRYPTER_INTEGRITYCHECK getEnumFromString(String enumValue) {
			ENUM_PGPENCRYPTER_INTEGRITYCHECK enumConst = ENUM_PGPENCRYPTER_INTEGRITYCHECK.Yes;
			if (ENUM_PGPENCRYPTER_INTEGRITYCHECK.No.value.equals(enumValue)) enumConst = ENUM_PGPENCRYPTER_INTEGRITYCHECK.No;
			return enumConst;
		}

		public static String[] values = new String[]{ "Yes", "No" };

	}

	/**
	 * <I>ENUM_PGPENCRYPTER_USEDEFAULTSIGNKEY</I>
	 * <pre>
	 * ENUM_PGPENCRYPTER_USEDEFAULTSIGNKEY.Yes = Yes
	 * ENUM_PGPENCRYPTER_USEDEFAULTSIGNKEY.No = No
	 * </pre>
	 */
	public static class ENUM_PGPENCRYPTER_USEDEFAULTSIGNKEY {
		private String value;

		public static final ENUM_PGPENCRYPTER_USEDEFAULTSIGNKEY Yes = new ENUM_PGPENCRYPTER_USEDEFAULTSIGNKEY("Yes");
		public static final ENUM_PGPENCRYPTER_USEDEFAULTSIGNKEY No = new ENUM_PGPENCRYPTER_USEDEFAULTSIGNKEY("No");

		protected ENUM_PGPENCRYPTER_USEDEFAULTSIGNKEY(String value) {
			this.value = value;
		}
		public String toString() {
			return value;
		}

		protected static ENUM_PGPENCRYPTER_USEDEFAULTSIGNKEY getEnumFromString(String enumValue) {
			ENUM_PGPENCRYPTER_USEDEFAULTSIGNKEY enumConst = ENUM_PGPENCRYPTER_USEDEFAULTSIGNKEY.Yes;
			if (ENUM_PGPENCRYPTER_USEDEFAULTSIGNKEY.No.value.equals(enumValue)) enumConst = ENUM_PGPENCRYPTER_USEDEFAULTSIGNKEY.No;
			return enumConst;
		}

		public static String[] values = new String[]{ "Yes", "No" };

	}

	/**
	 * <I>ENUM_PGPENCRYPTER_HASHALGORITHM</I>
	 * <pre>
	 * ENUM_PGPENCRYPTER_HASHALGORITHM.SHA1 = SHA1
	 * ENUM_PGPENCRYPTER_HASHALGORITHM.MD5 = MD5
	 * ENUM_PGPENCRYPTER_HASHALGORITHM.RIPEMD160 = RIPEMD160
	 * ENUM_PGPENCRYPTER_HASHALGORITHM.MD2 = MD2
	 * ENUM_PGPENCRYPTER_HASHALGORITHM.SHA256 = SHA256
	 * ENUM_PGPENCRYPTER_HASHALGORITHM.SHA384 = SHA384
	 * ENUM_PGPENCRYPTER_HASHALGORITHM.SHA512 = SHA512
	 * ENUM_PGPENCRYPTER_HASHALGORITHM.SHA224 = SHA224
	 * </pre>
	 */
	public static class ENUM_PGPENCRYPTER_HASHALGORITHM {
		private String value;

		public static final ENUM_PGPENCRYPTER_HASHALGORITHM SHA1 = new ENUM_PGPENCRYPTER_HASHALGORITHM("SHA1");
		public static final ENUM_PGPENCRYPTER_HASHALGORITHM MD5 = new ENUM_PGPENCRYPTER_HASHALGORITHM("MD5");
		public static final ENUM_PGPENCRYPTER_HASHALGORITHM RIPEMD160 = new ENUM_PGPENCRYPTER_HASHALGORITHM("RIPEMD160");
		public static final ENUM_PGPENCRYPTER_HASHALGORITHM MD2 = new ENUM_PGPENCRYPTER_HASHALGORITHM("MD2");
		public static final ENUM_PGPENCRYPTER_HASHALGORITHM SHA256 = new ENUM_PGPENCRYPTER_HASHALGORITHM("SHA256");
		public static final ENUM_PGPENCRYPTER_HASHALGORITHM SHA384 = new ENUM_PGPENCRYPTER_HASHALGORITHM("SHA384");
		public static final ENUM_PGPENCRYPTER_HASHALGORITHM SHA512 = new ENUM_PGPENCRYPTER_HASHALGORITHM("SHA512");
		public static final ENUM_PGPENCRYPTER_HASHALGORITHM SHA224 = new ENUM_PGPENCRYPTER_HASHALGORITHM("SHA224");

		protected ENUM_PGPENCRYPTER_HASHALGORITHM(String value) {
			this.value = value;
		}
		public String toString() {
			return value;
		}

		protected static ENUM_PGPENCRYPTER_HASHALGORITHM getEnumFromString(String enumValue) {
			ENUM_PGPENCRYPTER_HASHALGORITHM enumConst = ENUM_PGPENCRYPTER_HASHALGORITHM.SHA1;
			if (ENUM_PGPENCRYPTER_HASHALGORITHM.MD5.value.equals(enumValue)) enumConst = ENUM_PGPENCRYPTER_HASHALGORITHM.MD5;
			if (ENUM_PGPENCRYPTER_HASHALGORITHM.RIPEMD160.value.equals(enumValue)) enumConst = ENUM_PGPENCRYPTER_HASHALGORITHM.RIPEMD160;
			if (ENUM_PGPENCRYPTER_HASHALGORITHM.MD2.value.equals(enumValue)) enumConst = ENUM_PGPENCRYPTER_HASHALGORITHM.MD2;
			if (ENUM_PGPENCRYPTER_HASHALGORITHM.SHA256.value.equals(enumValue)) enumConst = ENUM_PGPENCRYPTER_HASHALGORITHM.SHA256;
			if (ENUM_PGPENCRYPTER_HASHALGORITHM.SHA384.value.equals(enumValue)) enumConst = ENUM_PGPENCRYPTER_HASHALGORITHM.SHA384;
			if (ENUM_PGPENCRYPTER_HASHALGORITHM.SHA512.value.equals(enumValue)) enumConst = ENUM_PGPENCRYPTER_HASHALGORITHM.SHA512;
			if (ENUM_PGPENCRYPTER_HASHALGORITHM.SHA224.value.equals(enumValue)) enumConst = ENUM_PGPENCRYPTER_HASHALGORITHM.SHA224;
			return enumConst;
		}

		public static String[] values = new String[]{ "SHA1", "MD5", "RIPEMD160", "MD2", "SHA256", "SHA384", "SHA512", "SHA224" };

	}

	/**
	 * <I>ENUM_PGPENCRYPTER_FILEENCRYPTION</I>
	 * <pre>
	 * ENUM_PGPENCRYPTER_FILEENCRYPTION.No = No
	 * ENUM_PGPENCRYPTER_FILEENCRYPTION.Yes = Yes
	 * </pre>
	 */
	public static class ENUM_PGPENCRYPTER_FILEENCRYPTION {
		private String value;

		public static final ENUM_PGPENCRYPTER_FILEENCRYPTION No = new ENUM_PGPENCRYPTER_FILEENCRYPTION("No");
		public static final ENUM_PGPENCRYPTER_FILEENCRYPTION Yes = new ENUM_PGPENCRYPTER_FILEENCRYPTION("Yes");

		protected ENUM_PGPENCRYPTER_FILEENCRYPTION(String value) {
			this.value = value;
		}
		public String toString() {
			return value;
		}

		protected static ENUM_PGPENCRYPTER_FILEENCRYPTION getEnumFromString(String enumValue) {
			ENUM_PGPENCRYPTER_FILEENCRYPTION enumConst = ENUM_PGPENCRYPTER_FILEENCRYPTION.No;
			if (ENUM_PGPENCRYPTER_FILEENCRYPTION.Yes.value.equals(enumValue)) enumConst = ENUM_PGPENCRYPTER_FILEENCRYPTION.Yes;
			return enumConst;
		}

		public static String[] values = new String[]{ "No", "Yes" };

	}

	/**
	 * <I>ENUM_PGPENCRYPTER_CIPHERALGORITHM</I>
	 * <pre>
	 * ENUM_PGPENCRYPTER_CIPHERALGORITHM.CAST5 = CAST5
	 * ENUM_PGPENCRYPTER_CIPHERALGORITHM.TRIPLE_DES = TRIPLE_DES
	 * ENUM_PGPENCRYPTER_CIPHERALGORITHM.IDEA = IDEA
	 * ENUM_PGPENCRYPTER_CIPHERALGORITHM.BLOWFISH = BLOWFISH
	 * ENUM_PGPENCRYPTER_CIPHERALGORITHM.DES = DES
	 * ENUM_PGPENCRYPTER_CIPHERALGORITHM.AES_128 = AES_128
	 * ENUM_PGPENCRYPTER_CIPHERALGORITHM.AES_192 = AES_192
	 * ENUM_PGPENCRYPTER_CIPHERALGORITHM.AES_256 = AES_256
	 * ENUM_PGPENCRYPTER_CIPHERALGORITHM.TWOFISH = TWOFISH
	 * </pre>
	 */
	public static class ENUM_PGPENCRYPTER_CIPHERALGORITHM {
		private String value;

		public static final ENUM_PGPENCRYPTER_CIPHERALGORITHM CAST5 = new ENUM_PGPENCRYPTER_CIPHERALGORITHM("CAST5");
		public static final ENUM_PGPENCRYPTER_CIPHERALGORITHM TRIPLE_DES = new ENUM_PGPENCRYPTER_CIPHERALGORITHM("TRIPLE_DES");
		public static final ENUM_PGPENCRYPTER_CIPHERALGORITHM IDEA = new ENUM_PGPENCRYPTER_CIPHERALGORITHM("IDEA");
		public static final ENUM_PGPENCRYPTER_CIPHERALGORITHM BLOWFISH = new ENUM_PGPENCRYPTER_CIPHERALGORITHM("BLOWFISH");
		public static final ENUM_PGPENCRYPTER_CIPHERALGORITHM DES = new ENUM_PGPENCRYPTER_CIPHERALGORITHM("DES");
		public static final ENUM_PGPENCRYPTER_CIPHERALGORITHM AES_128 = new ENUM_PGPENCRYPTER_CIPHERALGORITHM("AES_128");
		public static final ENUM_PGPENCRYPTER_CIPHERALGORITHM AES_192 = new ENUM_PGPENCRYPTER_CIPHERALGORITHM("AES_192");
		public static final ENUM_PGPENCRYPTER_CIPHERALGORITHM AES_256 = new ENUM_PGPENCRYPTER_CIPHERALGORITHM("AES_256");
		public static final ENUM_PGPENCRYPTER_CIPHERALGORITHM TWOFISH = new ENUM_PGPENCRYPTER_CIPHERALGORITHM("TWOFISH");

		protected ENUM_PGPENCRYPTER_CIPHERALGORITHM(String value) {
			this.value = value;
		}
		public String toString() {
			return value;
		}

		protected static ENUM_PGPENCRYPTER_CIPHERALGORITHM getEnumFromString(String enumValue) {
			ENUM_PGPENCRYPTER_CIPHERALGORITHM enumConst = ENUM_PGPENCRYPTER_CIPHERALGORITHM.CAST5;
			if (ENUM_PGPENCRYPTER_CIPHERALGORITHM.TRIPLE_DES.value.equals(enumValue)) enumConst = ENUM_PGPENCRYPTER_CIPHERALGORITHM.TRIPLE_DES;
			if (ENUM_PGPENCRYPTER_CIPHERALGORITHM.IDEA.value.equals(enumValue)) enumConst = ENUM_PGPENCRYPTER_CIPHERALGORITHM.IDEA;
			if (ENUM_PGPENCRYPTER_CIPHERALGORITHM.BLOWFISH.value.equals(enumValue)) enumConst = ENUM_PGPENCRYPTER_CIPHERALGORITHM.BLOWFISH;
			if (ENUM_PGPENCRYPTER_CIPHERALGORITHM.DES.value.equals(enumValue)) enumConst = ENUM_PGPENCRYPTER_CIPHERALGORITHM.DES;
			if (ENUM_PGPENCRYPTER_CIPHERALGORITHM.AES_128.value.equals(enumValue)) enumConst = ENUM_PGPENCRYPTER_CIPHERALGORITHM.AES_128;
			if (ENUM_PGPENCRYPTER_CIPHERALGORITHM.AES_192.value.equals(enumValue)) enumConst = ENUM_PGPENCRYPTER_CIPHERALGORITHM.AES_192;
			if (ENUM_PGPENCRYPTER_CIPHERALGORITHM.AES_256.value.equals(enumValue)) enumConst = ENUM_PGPENCRYPTER_CIPHERALGORITHM.AES_256;
			if (ENUM_PGPENCRYPTER_CIPHERALGORITHM.TWOFISH.value.equals(enumValue)) enumConst = ENUM_PGPENCRYPTER_CIPHERALGORITHM.TWOFISH;
			return enumConst;
		}

		public static String[] values = new String[]{ "CAST5", "TRIPLE_DES", "IDEA", "BLOWFISH", "DES", "AES_128", "AES_192", "AES_256", "TWOFISH" };

	}

	/**
	 * <I>ENUM_PGPENCRYPTER_OUTPUTLOCATION</I>
	 * <pre>
	 * ENUM_PGPENCRYPTER_OUTPUTLOCATION.OutputRoot = Output Message Tree
	 * ENUM_PGPENCRYPTER_OUTPUTLOCATION.FileSystem = File System
	 * </pre>
	 */
	public static class ENUM_PGPENCRYPTER_OUTPUTLOCATION {
		private String value;

		public static final ENUM_PGPENCRYPTER_OUTPUTLOCATION OutputRoot = new ENUM_PGPENCRYPTER_OUTPUTLOCATION("OutputRoot");
		public static final ENUM_PGPENCRYPTER_OUTPUTLOCATION FileSystem = new ENUM_PGPENCRYPTER_OUTPUTLOCATION("FileSystem");

		protected ENUM_PGPENCRYPTER_OUTPUTLOCATION(String value) {
			this.value = value;
		}
		public String toString() {
			return value;
		}

		protected static ENUM_PGPENCRYPTER_OUTPUTLOCATION getEnumFromString(String enumValue) {
			ENUM_PGPENCRYPTER_OUTPUTLOCATION enumConst = ENUM_PGPENCRYPTER_OUTPUTLOCATION.OutputRoot;
			if (ENUM_PGPENCRYPTER_OUTPUTLOCATION.FileSystem.value.equals(enumValue)) enumConst = ENUM_PGPENCRYPTER_OUTPUTLOCATION.FileSystem;
			return enumConst;
		}

		public static String[] values = new String[]{ "OutputRoot", "FileSystem" };

	}

	/**
	 * <I>ENUM_PGPENCRYPTER_COMPRESSIONALGORITHM</I>
	 * <pre>
	 * ENUM_PGPENCRYPTER_COMPRESSIONALGORITHM.ZIP = ZIP
	 * ENUM_PGPENCRYPTER_COMPRESSIONALGORITHM.BZIP2 = BZIP2
	 * ENUM_PGPENCRYPTER_COMPRESSIONALGORITHM.ZLIB = ZLIB
	 * ENUM_PGPENCRYPTER_COMPRESSIONALGORITHM.UNCOMPRESSED = UNCOMPRESSED
	 * </pre>
	 */
	public static class ENUM_PGPENCRYPTER_COMPRESSIONALGORITHM {
		private String value;

		public static final ENUM_PGPENCRYPTER_COMPRESSIONALGORITHM ZIP = new ENUM_PGPENCRYPTER_COMPRESSIONALGORITHM("ZIP");
		public static final ENUM_PGPENCRYPTER_COMPRESSIONALGORITHM BZIP2 = new ENUM_PGPENCRYPTER_COMPRESSIONALGORITHM("BZIP2");
		public static final ENUM_PGPENCRYPTER_COMPRESSIONALGORITHM ZLIB = new ENUM_PGPENCRYPTER_COMPRESSIONALGORITHM("ZLIB");
		public static final ENUM_PGPENCRYPTER_COMPRESSIONALGORITHM UNCOMPRESSED = new ENUM_PGPENCRYPTER_COMPRESSIONALGORITHM("UNCOMPRESSED");

		protected ENUM_PGPENCRYPTER_COMPRESSIONALGORITHM(String value) {
			this.value = value;
		}
		public String toString() {
			return value;
		}

		protected static ENUM_PGPENCRYPTER_COMPRESSIONALGORITHM getEnumFromString(String enumValue) {
			ENUM_PGPENCRYPTER_COMPRESSIONALGORITHM enumConst = ENUM_PGPENCRYPTER_COMPRESSIONALGORITHM.ZIP;
			if (ENUM_PGPENCRYPTER_COMPRESSIONALGORITHM.BZIP2.value.equals(enumValue)) enumConst = ENUM_PGPENCRYPTER_COMPRESSIONALGORITHM.BZIP2;
			if (ENUM_PGPENCRYPTER_COMPRESSIONALGORITHM.ZLIB.value.equals(enumValue)) enumConst = ENUM_PGPENCRYPTER_COMPRESSIONALGORITHM.ZLIB;
			if (ENUM_PGPENCRYPTER_COMPRESSIONALGORITHM.UNCOMPRESSED.value.equals(enumValue)) enumConst = ENUM_PGPENCRYPTER_COMPRESSIONALGORITHM.UNCOMPRESSED;
			return enumConst;
		}

		public static String[] values = new String[]{ "ZIP", "BZIP2", "ZLIB", "UNCOMPRESSED" };

	}

	/**
	 * <I>ENUM_PGPENCRYPTER_REPLACEOUTPUTFILE</I>
	 * <pre>
	 * ENUM_PGPENCRYPTER_REPLACEOUTPUTFILE.Yes = Yes
	 * ENUM_PGPENCRYPTER_REPLACEOUTPUTFILE.No = No
	 * </pre>
	 */
	public static class ENUM_PGPENCRYPTER_REPLACEOUTPUTFILE {
		private String value;

		public static final ENUM_PGPENCRYPTER_REPLACEOUTPUTFILE Yes = new ENUM_PGPENCRYPTER_REPLACEOUTPUTFILE("Yes");
		public static final ENUM_PGPENCRYPTER_REPLACEOUTPUTFILE No = new ENUM_PGPENCRYPTER_REPLACEOUTPUTFILE("No");

		protected ENUM_PGPENCRYPTER_REPLACEOUTPUTFILE(String value) {
			this.value = value;
		}
		public String toString() {
			return value;
		}

		protected static ENUM_PGPENCRYPTER_REPLACEOUTPUTFILE getEnumFromString(String enumValue) {
			ENUM_PGPENCRYPTER_REPLACEOUTPUTFILE enumConst = ENUM_PGPENCRYPTER_REPLACEOUTPUTFILE.Yes;
			if (ENUM_PGPENCRYPTER_REPLACEOUTPUTFILE.No.value.equals(enumValue)) enumConst = ENUM_PGPENCRYPTER_REPLACEOUTPUTFILE.No;
			return enumConst;
		}

		public static String[] values = new String[]{ "Yes", "No" };

	}
	protected NodeProperty[] getNodeProperties() {
		return new NodeProperty[] {
			new NodeProperty(PGPEncrypterNodeUDN.PROPERTY_FILEENCRYPTION,		NodeProperty.Usage.OPTIONAL,	false,	NodeProperty.Type.ENUMERATION, "No", ENUM_PGPENCRYPTER_FILEENCRYPTION.class,"","",	"com/ibm/broker/supportpac/pgp/PGPEncrypter",	"PGPSupportPac"),
			new NodeProperty(PGPEncrypterNodeUDN.PROPERTY_OUTPUTLOCATION,		NodeProperty.Usage.OPTIONAL,	false,	NodeProperty.Type.ENUMERATION, "OutputRoot", ENUM_PGPENCRYPTER_OUTPUTLOCATION.class,"","",	"com/ibm/broker/supportpac/pgp/PGPEncrypter",	"PGPSupportPac"),
			new NodeProperty(PGPEncrypterNodeUDN.PROPERTY_INPUTDIRECTORY,		NodeProperty.Usage.OPTIONAL,	true,	NodeProperty.Type.STRING, null,"","",	"com/ibm/broker/supportpac/pgp/PGPEncrypter",	"PGPSupportPac"),
			new NodeProperty(PGPEncrypterNodeUDN.PROPERTY_OUTPUTDIRECTORY,		NodeProperty.Usage.OPTIONAL,	true,	NodeProperty.Type.STRING, null,"","",	"com/ibm/broker/supportpac/pgp/PGPEncrypter",	"PGPSupportPac"),
			new NodeProperty(PGPEncrypterNodeUDN.PROPERTY_INPUTFILENAME,		NodeProperty.Usage.OPTIONAL,	true,	NodeProperty.Type.STRING, null,"","",	"com/ibm/broker/supportpac/pgp/PGPEncrypter",	"PGPSupportPac"),
			new NodeProperty(PGPEncrypterNodeUDN.PROPERTY_OUTPUTFILENAME,		NodeProperty.Usage.OPTIONAL,	true,	NodeProperty.Type.STRING, null,"","",	"com/ibm/broker/supportpac/pgp/PGPEncrypter",	"PGPSupportPac"),
			new NodeProperty(PGPEncrypterNodeUDN.PROPERTY_REPLACEOUTPUTFILE,		NodeProperty.Usage.OPTIONAL,	false,	NodeProperty.Type.ENUMERATION, "Yes", ENUM_PGPENCRYPTER_REPLACEOUTPUTFILE.class,"","",	"com/ibm/broker/supportpac/pgp/PGPEncrypter",	"PGPSupportPac"),
			new NodeProperty(PGPEncrypterNodeUDN.PROPERTY_INPUTFILEACTION,		NodeProperty.Usage.OPTIONAL,	false,	NodeProperty.Type.ENUMERATION, "NoAction", ENUM_PGPENCRYPTER_INPUTFILEACTION.class,"","",	"com/ibm/broker/supportpac/pgp/PGPEncrypter",	"PGPSupportPac"),
			new NodeProperty(PGPEncrypterNodeUDN.PROPERTY_REPLACEDUPLICATEARCHIVE,		NodeProperty.Usage.OPTIONAL,	false,	NodeProperty.Type.ENUMERATION, "Yes", ENUM_PGPENCRYPTER_REPLACEDUPLICATEARCHIVE.class,"","",	"com/ibm/broker/supportpac/pgp/PGPEncrypter",	"PGPSupportPac"),
			new NodeProperty(PGPEncrypterNodeUDN.PROPERTY_PGPPOLICY,		NodeProperty.Usage.MANDATORY,	true,	NodeProperty.Type.STRING, null,"","",	"com/ibm/broker/supportpac/pgp/PGPEncrypter",	"PGPSupportPac"),
			new NodeProperty(PGPEncrypterNodeUDN.PROPERTY_ENCRYPTIONKEYUSERID,		NodeProperty.Usage.MANDATORY,	true,	NodeProperty.Type.STRING, null,"","",	"com/ibm/broker/supportpac/pgp/PGPEncrypter",	"PGPSupportPac"),
			new NodeProperty(PGPEncrypterNodeUDN.PROPERTY_ASCIIARMOR,		NodeProperty.Usage.OPTIONAL,	false,	NodeProperty.Type.ENUMERATION, "Yes", ENUM_PGPENCRYPTER_ASCIIARMOR.class,"","",	"com/ibm/broker/supportpac/pgp/PGPEncrypter",	"PGPSupportPac"),
			new NodeProperty(PGPEncrypterNodeUDN.PROPERTY_INTEGRITYCHECK,		NodeProperty.Usage.OPTIONAL,	false,	NodeProperty.Type.ENUMERATION, "Yes", ENUM_PGPENCRYPTER_INTEGRITYCHECK.class,"","",	"com/ibm/broker/supportpac/pgp/PGPEncrypter",	"PGPSupportPac"),
			new NodeProperty(PGPEncrypterNodeUDN.PROPERTY_SIGNATUREREQUIRED,		NodeProperty.Usage.OPTIONAL,	false,	NodeProperty.Type.ENUMERATION, "No", ENUM_PGPENCRYPTER_SIGNATUREREQUIRED.class,"","",	"com/ibm/broker/supportpac/pgp/PGPEncrypter",	"PGPSupportPac"),
			new NodeProperty(PGPEncrypterNodeUDN.PROPERTY_USEDEFAULTSIGNKEY,		NodeProperty.Usage.OPTIONAL,	false,	NodeProperty.Type.ENUMERATION, "Yes", ENUM_PGPENCRYPTER_USEDEFAULTSIGNKEY.class,"","",	"com/ibm/broker/supportpac/pgp/PGPEncrypter",	"PGPSupportPac"),
			new NodeProperty(PGPEncrypterNodeUDN.PROPERTY_SIGNKEYUSERID,		NodeProperty.Usage.OPTIONAL,	true,	NodeProperty.Type.STRING, null,"","",	"com/ibm/broker/supportpac/pgp/PGPEncrypter",	"PGPSupportPac"),
			new NodeProperty(PGPEncrypterNodeUDN.PROPERTY_SIGNKEYPASSPHRASE,		NodeProperty.Usage.OPTIONAL,	true,	NodeProperty.Type.STRING, null,"","",	"com/ibm/broker/supportpac/pgp/PGPEncrypter",	"PGPSupportPac"),
			new NodeProperty(PGPEncrypterNodeUDN.PROPERTY_HASHALGORITHM,		NodeProperty.Usage.OPTIONAL,	false,	NodeProperty.Type.ENUMERATION, "SHA1", ENUM_PGPENCRYPTER_HASHALGORITHM.class,"","",	"com/ibm/broker/supportpac/pgp/PGPEncrypter",	"PGPSupportPac"),
			new NodeProperty(PGPEncrypterNodeUDN.PROPERTY_CIPHERALGORITHM,		NodeProperty.Usage.OPTIONAL,	false,	NodeProperty.Type.ENUMERATION, "CAST5", ENUM_PGPENCRYPTER_CIPHERALGORITHM.class,"","",	"com/ibm/broker/supportpac/pgp/PGPEncrypter",	"PGPSupportPac"),
			new NodeProperty(PGPEncrypterNodeUDN.PROPERTY_COMPRESSIONALGORITHM,		NodeProperty.Usage.OPTIONAL,	false,	NodeProperty.Type.ENUMERATION, "ZIP", ENUM_PGPENCRYPTER_COMPRESSIONALGORITHM.class,"","",	"com/ibm/broker/supportpac/pgp/PGPEncrypter",	"PGPSupportPac")
		};
	}

	public PGPEncrypterNodeUDN() {
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
	 * Set the <I>PGPEncrypterNodeUDN</I> "<I>File Encryption</I>" property
	 * 
	 * @param value ENUM_PGPENCRYPTER_FILEENCRYPTION ; the value to set the property "<I>File Encryption</I>"
	 */
	public PGPEncrypterNodeUDN setFileEncryption(ENUM_PGPENCRYPTER_FILEENCRYPTION value) {
		setProperty(PGPEncrypterNodeUDN.PROPERTY_FILEENCRYPTION, value.toString());
		return this;
	}

	/**
	 * Get the <I>PGPEncrypterNodeUDN</I> "<I>File Encryption</I>" property
	 * 
	 * @return ENUM_PGPENCRYPTER_FILEENCRYPTION; the value of the property "<I>File Encryption</I>"
	 */
	public ENUM_PGPENCRYPTER_FILEENCRYPTION getFileEncryption() {
		ENUM_PGPENCRYPTER_FILEENCRYPTION value = ENUM_PGPENCRYPTER_FILEENCRYPTION.getEnumFromString((String)getPropertyValue(PGPEncrypterNodeUDN.PROPERTY_FILEENCRYPTION));
		return value;
	}

	/**
	 * Set the <I>PGPEncrypterNodeUDN</I> "<I>Output Location</I>" property
	 * 
	 * @param value ENUM_PGPENCRYPTER_OUTPUTLOCATION ; the value to set the property "<I>Output Location</I>"
	 */
	public PGPEncrypterNodeUDN setOutputLocation(ENUM_PGPENCRYPTER_OUTPUTLOCATION value) {
		setProperty(PGPEncrypterNodeUDN.PROPERTY_OUTPUTLOCATION, value.toString());
		return this;
	}

	/**
	 * Get the <I>PGPEncrypterNodeUDN</I> "<I>Output Location</I>" property
	 * 
	 * @return ENUM_PGPENCRYPTER_OUTPUTLOCATION; the value of the property "<I>Output Location</I>"
	 */
	public ENUM_PGPENCRYPTER_OUTPUTLOCATION getOutputLocation() {
		ENUM_PGPENCRYPTER_OUTPUTLOCATION value = ENUM_PGPENCRYPTER_OUTPUTLOCATION.getEnumFromString((String)getPropertyValue(PGPEncrypterNodeUDN.PROPERTY_OUTPUTLOCATION));
		return value;
	}

	/**
	 * Set the <I>PGPEncrypterNodeUDN</I> "<I>Input Directory</I>" property
	 * 
	 * @param value String ; the value to set the property "<I>Input Directory</I>"
	 */
	public PGPEncrypterNodeUDN setInputDirectory(String value) {
		setProperty(PGPEncrypterNodeUDN.PROPERTY_INPUTDIRECTORY, value);
		return this;
	}

	/**
	 * Get the <I>PGPEncrypterNodeUDN</I> "<I>Input Directory</I>" property
	 * 
	 * @return String; the value of the property "<I>Input Directory</I>"
	 */
	public String getInputDirectory() {
		return (String)getPropertyValue(PGPEncrypterNodeUDN.PROPERTY_INPUTDIRECTORY);
	}

	/**
	 * Set the <I>PGPEncrypterNodeUDN</I> "<I>Output Directory</I>" property
	 * 
	 * @param value String ; the value to set the property "<I>Output Directory</I>"
	 */
	public PGPEncrypterNodeUDN setOutputDirectory(String value) {
		setProperty(PGPEncrypterNodeUDN.PROPERTY_OUTPUTDIRECTORY, value);
		return this;
	}

	/**
	 * Get the <I>PGPEncrypterNodeUDN</I> "<I>Output Directory</I>" property
	 * 
	 * @return String; the value of the property "<I>Output Directory</I>"
	 */
	public String getOutputDirectory() {
		return (String)getPropertyValue(PGPEncrypterNodeUDN.PROPERTY_OUTPUTDIRECTORY);
	}

	/**
	 * Set the <I>PGPEncrypterNodeUDN</I> "<I>InputFile Name</I>" property
	 * 
	 * @param value String ; the value to set the property "<I>InputFile Name</I>"
	 */
	public PGPEncrypterNodeUDN setInputFileName(String value) {
		setProperty(PGPEncrypterNodeUDN.PROPERTY_INPUTFILENAME, value);
		return this;
	}

	/**
	 * Get the <I>PGPEncrypterNodeUDN</I> "<I>InputFile Name</I>" property
	 * 
	 * @return String; the value of the property "<I>InputFile Name</I>"
	 */
	public String getInputFileName() {
		return (String)getPropertyValue(PGPEncrypterNodeUDN.PROPERTY_INPUTFILENAME);
	}

	/**
	 * Set the <I>PGPEncrypterNodeUDN</I> "<I>OutputFile Name</I>" property
	 * 
	 * @param value String ; the value to set the property "<I>OutputFile Name</I>"
	 */
	public PGPEncrypterNodeUDN setOutputFileName(String value) {
		setProperty(PGPEncrypterNodeUDN.PROPERTY_OUTPUTFILENAME, value);
		return this;
	}

	/**
	 * Get the <I>PGPEncrypterNodeUDN</I> "<I>OutputFile Name</I>" property
	 * 
	 * @return String; the value of the property "<I>OutputFile Name</I>"
	 */
	public String getOutputFileName() {
		return (String)getPropertyValue(PGPEncrypterNodeUDN.PROPERTY_OUTPUTFILENAME);
	}

	/**
	 * Set the <I>PGPEncrypterNodeUDN</I> "<I>Replace OutputFile</I>" property
	 * 
	 * @param value ENUM_PGPENCRYPTER_REPLACEOUTPUTFILE ; the value to set the property "<I>Replace OutputFile</I>"
	 */
	public PGPEncrypterNodeUDN setReplaceOutputFile(ENUM_PGPENCRYPTER_REPLACEOUTPUTFILE value) {
		setProperty(PGPEncrypterNodeUDN.PROPERTY_REPLACEOUTPUTFILE, value.toString());
		return this;
	}

	/**
	 * Get the <I>PGPEncrypterNodeUDN</I> "<I>Replace OutputFile</I>" property
	 * 
	 * @return ENUM_PGPENCRYPTER_REPLACEOUTPUTFILE; the value of the property "<I>Replace OutputFile</I>"
	 */
	public ENUM_PGPENCRYPTER_REPLACEOUTPUTFILE getReplaceOutputFile() {
		ENUM_PGPENCRYPTER_REPLACEOUTPUTFILE value = ENUM_PGPENCRYPTER_REPLACEOUTPUTFILE.getEnumFromString((String)getPropertyValue(PGPEncrypterNodeUDN.PROPERTY_REPLACEOUTPUTFILE));
		return value;
	}

	/**
	 * Set the <I>PGPEncrypterNodeUDN</I> "<I>InputFile Action</I>" property
	 * 
	 * @param value ENUM_PGPENCRYPTER_INPUTFILEACTION ; the value to set the property "<I>InputFile Action</I>"
	 */
	public PGPEncrypterNodeUDN setInputFileAction(ENUM_PGPENCRYPTER_INPUTFILEACTION value) {
		setProperty(PGPEncrypterNodeUDN.PROPERTY_INPUTFILEACTION, value.toString());
		return this;
	}

	/**
	 * Get the <I>PGPEncrypterNodeUDN</I> "<I>InputFile Action</I>" property
	 * 
	 * @return ENUM_PGPENCRYPTER_INPUTFILEACTION; the value of the property "<I>InputFile Action</I>"
	 */
	public ENUM_PGPENCRYPTER_INPUTFILEACTION getInputFileAction() {
		ENUM_PGPENCRYPTER_INPUTFILEACTION value = ENUM_PGPENCRYPTER_INPUTFILEACTION.getEnumFromString((String)getPropertyValue(PGPEncrypterNodeUDN.PROPERTY_INPUTFILEACTION));
		return value;
	}

	/**
	 * Set the <I>PGPEncrypterNodeUDN</I> "<I>Replace Duplicate Archive</I>" property
	 * 
	 * @param value ENUM_PGPENCRYPTER_REPLACEDUPLICATEARCHIVE ; the value to set the property "<I>Replace Duplicate Archive</I>"
	 */
	public PGPEncrypterNodeUDN setReplaceDuplicateArchive(ENUM_PGPENCRYPTER_REPLACEDUPLICATEARCHIVE value) {
		setProperty(PGPEncrypterNodeUDN.PROPERTY_REPLACEDUPLICATEARCHIVE, value.toString());
		return this;
	}

	/**
	 * Get the <I>PGPEncrypterNodeUDN</I> "<I>Replace Duplicate Archive</I>" property
	 * 
	 * @return ENUM_PGPENCRYPTER_REPLACEDUPLICATEARCHIVE; the value of the property "<I>Replace Duplicate Archive</I>"
	 */
	public ENUM_PGPENCRYPTER_REPLACEDUPLICATEARCHIVE getReplaceDuplicateArchive() {
		ENUM_PGPENCRYPTER_REPLACEDUPLICATEARCHIVE value = getEnumFromString((String)getPropertyValue(PGPEncrypterNodeUDN.PROPERTY_REPLACEDUPLICATEARCHIVE));
		return value;
	}

	/**
	 * Set the <I>PGPEncrypterNodeUDN</I> "<I>PGP Configurable Service</I>" property
	 * 
	 * @param value String ; the value to set the property "<I>PGP Configurable Service</I>"
	 */
	public PGPEncrypterNodeUDN setPgpConfigService(String value) {
		setProperty(PGPEncrypterNodeUDN.PROPERTY_PGPPOLICY, value);
		return this;
	}

	/**
	 * Get the <I>PGPEncrypterNodeUDN</I> "<I>PGP Configurable Service</I>" property
	 * 
	 * @return String; the value of the property "<I>PGP Configurable Service</I>"
	 */
	public String getPgpPolicy() {
		return (String)getPropertyValue(PGPEncrypterNodeUDN.PROPERTY_PGPPOLICY);
	}

	/**
	 * Set the <I>PGPEncrypterNodeUDN</I> "<I>EncryptionKey UserId</I>" property
	 * 
	 * @param value String ; the value to set the property "<I>EncryptionKey UserId</I>"
	 */
	public PGPEncrypterNodeUDN setEncryptionKeyUserId(String value) {
		setProperty(PGPEncrypterNodeUDN.PROPERTY_ENCRYPTIONKEYUSERID, value);
		return this;
	}

	/**
	 * Get the <I>PGPEncrypterNodeUDN</I> "<I>EncryptionKey UserId</I>" property
	 * 
	 * @return String; the value of the property "<I>EncryptionKey UserId</I>"
	 */
	public String getEncryptionKeyUserId() {
		return (String)getPropertyValue(PGPEncrypterNodeUDN.PROPERTY_ENCRYPTIONKEYUSERID);
	}

	/**
	 * Set the <I>PGPEncrypterNodeUDN</I> "<I>Ascii Armor</I>" property
	 * 
	 * @param value ENUM_PGPENCRYPTER_ASCIIARMOR ; the value to set the property "<I>Ascii Armor</I>"
	 */
	public PGPEncrypterNodeUDN setAsciiArmor(ENUM_PGPENCRYPTER_ASCIIARMOR value) {
		setProperty(PGPEncrypterNodeUDN.PROPERTY_ASCIIARMOR, value.toString());
		return this;
	}

	/**
	 * Get the <I>PGPEncrypterNodeUDN</I> "<I>Ascii Armor</I>" property
	 * 
	 * @return ENUM_PGPENCRYPTER_ASCIIARMOR; the value of the property "<I>Ascii Armor</I>"
	 */
	public ENUM_PGPENCRYPTER_ASCIIARMOR getAsciiArmor() {
		ENUM_PGPENCRYPTER_ASCIIARMOR value = ENUM_PGPENCRYPTER_ASCIIARMOR.getEnumFromString((String)getPropertyValue(PGPEncrypterNodeUDN.PROPERTY_ASCIIARMOR));
		return value;
	}

	/**
	 * Set the <I>PGPEncrypterNodeUDN</I> "<I>Integrity Check</I>" property
	 * 
	 * @param value ENUM_PGPENCRYPTER_INTEGRITYCHECK ; the value to set the property "<I>Integrity Check</I>"
	 */
	public PGPEncrypterNodeUDN setIntegrityCheck(ENUM_PGPENCRYPTER_INTEGRITYCHECK value) {
		setProperty(PGPEncrypterNodeUDN.PROPERTY_INTEGRITYCHECK, value.toString());
		return this;
	}

	/**
	 * Get the <I>PGPEncrypterNodeUDN</I> "<I>Integrity Check</I>" property
	 * 
	 * @return ENUM_PGPENCRYPTER_INTEGRITYCHECK; the value of the property "<I>Integrity Check</I>"
	 */
	public ENUM_PGPENCRYPTER_INTEGRITYCHECK getIntegrityCheck() {
		ENUM_PGPENCRYPTER_INTEGRITYCHECK value = ENUM_PGPENCRYPTER_INTEGRITYCHECK.getEnumFromString((String)getPropertyValue(PGPEncrypterNodeUDN.PROPERTY_INTEGRITYCHECK));
		return value;
	}

	/**
	 * Set the <I>PGPEncrypterNodeUDN</I> "<I>Signature Required</I>" property
	 * 
	 * @param value ENUM_PGPENCRYPTER_SIGNATUREREQUIRED ; the value to set the property "<I>Signature Required</I>"
	 */
	public PGPEncrypterNodeUDN setSignatureRequired(ENUM_PGPENCRYPTER_SIGNATUREREQUIRED value) {
		setProperty(PGPEncrypterNodeUDN.PROPERTY_SIGNATUREREQUIRED, value.toString());
		return this;
	}

	/**
	 * Get the <I>PGPEncrypterNodeUDN</I> "<I>Signature Required</I>" property
	 * 
	 * @return ENUM_PGPENCRYPTER_SIGNATUREREQUIRED; the value of the property "<I>Signature Required</I>"
	 */
	public ENUM_PGPENCRYPTER_SIGNATUREREQUIRED getSignatureRequired() {
		ENUM_PGPENCRYPTER_SIGNATUREREQUIRED value = ENUM_PGPENCRYPTER_SIGNATUREREQUIRED.getEnumFromString((String)getPropertyValue(PGPEncrypterNodeUDN.PROPERTY_SIGNATUREREQUIRED));
		return value;
	}

	/**
	 * Set the <I>PGPEncrypterNodeUDN</I> "<I>Use Default SignKey</I>" property
	 * 
	 * @param value ENUM_PGPENCRYPTER_USEDEFAULTSIGNKEY ; the value to set the property "<I>Use Default SignKey</I>"
	 */
	public PGPEncrypterNodeUDN setUseDefaultSignKey(ENUM_PGPENCRYPTER_USEDEFAULTSIGNKEY value) {
		setProperty(PGPEncrypterNodeUDN.PROPERTY_USEDEFAULTSIGNKEY, value.toString());
		return this;
	}

	/**
	 * Get the <I>PGPEncrypterNodeUDN</I> "<I>Use Default SignKey</I>" property
	 * 
	 * @return ENUM_PGPENCRYPTER_USEDEFAULTSIGNKEY; the value of the property "<I>Use Default SignKey</I>"
	 */
	public ENUM_PGPENCRYPTER_USEDEFAULTSIGNKEY getUseDefaultSignKey() {
		ENUM_PGPENCRYPTER_USEDEFAULTSIGNKEY value = ENUM_PGPENCRYPTER_USEDEFAULTSIGNKEY.getEnumFromString((String)getPropertyValue(PGPEncrypterNodeUDN.PROPERTY_USEDEFAULTSIGNKEY));
		return value;
	}

	/**
	 * Set the <I>PGPEncrypterNodeUDN</I> "<I>SignKey UserId</I>" property
	 * 
	 * @param value String ; the value to set the property "<I>SignKey UserId</I>"
	 */
	public PGPEncrypterNodeUDN setSignKeyUserId(String value) {
		setProperty(PGPEncrypterNodeUDN.PROPERTY_SIGNKEYUSERID, value);
		return this;
	}

	/**
	 * Get the <I>PGPEncrypterNodeUDN</I> "<I>SignKey UserId</I>" property
	 * 
	 * @return String; the value of the property "<I>SignKey UserId</I>"
	 */
	public String getSignKeyUserId() {
		return (String)getPropertyValue(PGPEncrypterNodeUDN.PROPERTY_SIGNKEYUSERID);
	}

	/**
	 * Set the <I>PGPEncrypterNodeUDN</I> "<I>SignKey Passphrase</I>" property
	 * 
	 * @param value String ; the value to set the property "<I>SignKey Passphrase</I>"
	 */
	public PGPEncrypterNodeUDN setSignKeyPassphrase(String value) {
		setProperty(PGPEncrypterNodeUDN.PROPERTY_SIGNKEYPASSPHRASE, value);
		return this;
	}

	/**
	 * Get the <I>PGPEncrypterNodeUDN</I> "<I>SignKey Passphrase</I>" property
	 * 
	 * @return String; the value of the property "<I>SignKey Passphrase</I>"
	 */
	public String getSignKeyPassphrase() {
		return (String)getPropertyValue(PGPEncrypterNodeUDN.PROPERTY_SIGNKEYPASSPHRASE);
	}

	/**
	 * Set the <I>PGPEncrypterNodeUDN</I> "<I>Hash Algorithm</I>" property
	 * 
	 * @param value ENUM_PGPENCRYPTER_HASHALGORITHM ; the value to set the property "<I>Hash Algorithm</I>"
	 */
	public PGPEncrypterNodeUDN setHashAlgorithm(ENUM_PGPENCRYPTER_HASHALGORITHM value) {
		setProperty(PGPEncrypterNodeUDN.PROPERTY_HASHALGORITHM, value.toString());
		return this;
	}

	/**
	 * Get the <I>PGPEncrypterNodeUDN</I> "<I>Hash Algorithm</I>" property
	 * 
	 * @return ENUM_PGPENCRYPTER_HASHALGORITHM; the value of the property "<I>Hash Algorithm</I>"
	 */
	public ENUM_PGPENCRYPTER_HASHALGORITHM getHashAlgorithm() {
		ENUM_PGPENCRYPTER_HASHALGORITHM value = ENUM_PGPENCRYPTER_HASHALGORITHM.getEnumFromString((String)getPropertyValue(PGPEncrypterNodeUDN.PROPERTY_HASHALGORITHM));
		return value;
	}

	/**
	 * Set the <I>PGPEncrypterNodeUDN</I> "<I>Cipher Algorithm</I>" property
	 * 
	 * @param value ENUM_PGPENCRYPTER_CIPHERALGORITHM ; the value to set the property "<I>Cipher Algorithm</I>"
	 */
	public PGPEncrypterNodeUDN setCipherAlgorithm(ENUM_PGPENCRYPTER_CIPHERALGORITHM value) {
		setProperty(PGPEncrypterNodeUDN.PROPERTY_CIPHERALGORITHM, value.toString());
		return this;
	}

	/**
	 * Get the <I>PGPEncrypterNodeUDN</I> "<I>Cipher Algorithm</I>" property
	 * 
	 * @return ENUM_PGPENCRYPTER_CIPHERALGORITHM; the value of the property "<I>Cipher Algorithm</I>"
	 */
	public ENUM_PGPENCRYPTER_CIPHERALGORITHM getCipherAlgorithm() {
		ENUM_PGPENCRYPTER_CIPHERALGORITHM value = ENUM_PGPENCRYPTER_CIPHERALGORITHM.getEnumFromString((String)getPropertyValue(PGPEncrypterNodeUDN.PROPERTY_CIPHERALGORITHM));
		return value;
	}

	/**
	 * Set the <I>PGPEncrypterNodeUDN</I> "<I>Compression Algorithm</I>" property
	 * 
	 * @param value ENUM_PGPENCRYPTER_COMPRESSIONALGORITHM ; the value to set the property "<I>Compression Algorithm</I>"
	 */
	public PGPEncrypterNodeUDN setCompressionAlgorithm(ENUM_PGPENCRYPTER_COMPRESSIONALGORITHM value) {
		setProperty(PGPEncrypterNodeUDN.PROPERTY_COMPRESSIONALGORITHM, value.toString());
		return this;
	}

	/**
	 * Get the <I>PGPEncrypterNodeUDN</I> "<I>Compression Algorithm</I>" property
	 * 
	 * @return ENUM_PGPENCRYPTER_COMPRESSIONALGORITHM; the value of the property "<I>Compression Algorithm</I>"
	 */
	public ENUM_PGPENCRYPTER_COMPRESSIONALGORITHM getCompressionAlgorithm() {
		ENUM_PGPENCRYPTER_COMPRESSIONALGORITHM value = ENUM_PGPENCRYPTER_COMPRESSIONALGORITHM.getEnumFromString((String)getPropertyValue(PGPEncrypterNodeUDN.PROPERTY_COMPRESSIONALGORITHM));
		return value;
	}

	public String getNodeName() {
		String retVal = super.getNodeName();
		if ((retVal==null) || retVal.equals(""))
			retVal = "PGP Encrypter";
		return retVal;
	};
}
