MyOpenTech (PGP SupportPac v1.0.0.1)
======================================================

<h2>PGP SupportPac for IBM Integration Bus</h2>
======================================================

<h4>Security facilities offered by IBM Integration Bus</h4>
Security facilities in IBM Integration Bus are typically based on<br>

1. Websphere MQ security.<br>
2. Transport layer security (e.g. SSL/TLS) provided by underlying transport mechanism.<br>
3. Access Controls (e.g. Authentication and Authorization) mechanism powered by internal (broker’s security manager) and external security providers (e.g. WS-Trust v1.3 compliant security token servers, Tivoli Federated Identity Manager, Lightweight Directory Access Protocol)<br>
4. WS-Security for Web Services using SOAP nodes.<br>


<h4>Limitation in security features offered by IBM Integration Bus</h4>

1. Webservice technology is not considered as a preferred solution in today’s enterprise integration world for asynchronous and one-way data communication especially while dealing with large volume of data communication and data transfer in batch mode. In this scenario WS-Security standard can not be applied.<br>
2. Use of SSL at transport layer slows down overall transfer rate as it encrypts the entire traffic. Use of SSL can be eliminated while transferring data over trusted network (e.g. Intranet) by encrypting only sensitive and confidential information at application layer itself.<br>
3. External applications (e.g. third party vendors, customers, government agencies) often ask for data encryption before transferring to/from them, even if SSL is used at transport layer.<br>
4. Apart from WS-Security standard (which is applicable for Webservices only), IBM Integration Bus does not provide any in-built solution for data security enforcing data confidentiality (Encryption) and integrity (Digital Signature).<br>
5. Many organizations use various third party softwares/tools for implementing data security. But those third party softwares/tools are completely decoupled from IBM Integration Bus.<br>
6. Why to use such third party softwares/tools at Integration Layer if IBM Integration Bus provides a solution.

<h4>Solution (PGP)</h4>

1. Solution to the above stated problems requires implementing a strong industry standard cryptographic solution to enforce data confidentiality and integrity with an optional data compression feature.<br>
2. PGP (Pretty Good Privacy) is a widely used cryptographic solution for data communication. It was created by Phil Zimmermann in 1991. PGP follows the OpenPGP standard (RFC 4880) for encrypting and decrypting data. Besides data confidentiality and integrity, PGP also supports data compression.<br>
3. PGP SupportPac (version 1.0.0.1) for IBM Integration Bus v9 implements PGP cryptographic solution providing encryption, decryption, and signature functionalities as an extended feature (SupportPac) of IBM Integration Bus product.<br>
4. This SupportPac leverages Bouncy Castle PGP Java libraries for core PGP functionalities. Bouncy Castle is a Java based open source (MIT License: https://www.bouncycastle.org/licence.html) solution for PGP implementation.<br>


<h3>PGP SupportPac Features<h3>
===============================================================
<h4>Easily pluggable to IBM Integration Bus Toolkit</h4>

Once PGP SupportPac plugins is applied to the IBM Integration Bus Toolkit, PGP Encrypter/Decrypter nodes will be available in the PGP drawer of the message flow node palette.

<h4>Easy Runtime Installation</h4>

It requires standard UserDefined Node installation process. SupportPac ships with following runtime libraries (.jar files) which needs to be placed at Broker's User Lil Path.<br><br>
<b>
bcpg-jdk15on-149.jar<br>
bcprov-ext-jdk15on-149.jar<br>
com.ibm.broker.supportpac.PGP.jar<br>
</b>


<h4>PGP key pair generation and key/repository management</h4>

This SupportPac ships with a Java based command-line tool (pgpkeytool) for PGP key generation and key/repository management. You do not need any third-party open source or commercial tool for PGP key/repository management.


<h4>Centralized key repository and some default parameters configuration through UserDefined Configurable Service</h4>

1. You do not need to specify private/public key repository details, default sign key and passphrase, decryption key passphrase information at each PGP Encrypter/Decrypter node used in the messageflow.<br>
2. Just create a UserDefined Configurable Service for all (or a group of messageflows) and specify the service name at node properties.<br>
3. In general just one Configurable Service is sufficient for all the messageflows deployed in a Broker.<br>


<h4>PGP Encrypter Node</h4>

1. Provides PGP signature generation (optional) and encryption functionalities.<br>
2. Supports both Message and File encryption regardless of transport protocol or message domain.<br>
3. Node can be configured to write encrypted data into Output Message Tree or File System directly.<br>
4. In case of File encryption, Input file can be deleted or archived (with or without timestamp suffix) after successful encryption process.<br>
5. Some node properties can be overridden at node's input local environment during runtime. Node properties overridden at input local environment are applicable at current invocation of the messageflow only.<br>
6. Node reads PGP private/public keys and default signature key/passphrase information configured at UserDefined Configurable Service.<br>
7. Key information can be provided as either Key User Id (e.g. Sender <sender-pgp-keys@ibm.com>) or Hexadecimal Key Id (e.g. 0x73E56D78)<br>
8. Supports wide range of required algorithms.<br>
Hash (Digest) Algorithms: MD5, SHA1, RIPEMD160, MD2, SHA256, SHA384, SHA512, SHA224<br>
Cipher Algorithms: IDEA, TRIPLE_DES, CAST5, BLOWFISH, DES, AES_128, AES_192, AES_256, TWOFISH<br>
Compression Algorithms: UNCOMPRESSED, ZIP, ZLIB, BZIP2<br>


<h4>PGP Decrypter Node</h4>

1. Provides PGP signature validation (optional) and decryption functionalities.<br>
2. Supports both Message and File decryption.<br>
3. Node can be configured to write decrypted data into Output Message Tree or File System directly.<br>
4. In case of File decryption, Input file can be deleted or archived (with or without timestamp suffix) after successful decryption process.<br>
5. Some node properties can be overridden at node's input local environment during runtime. Node properties overridden at input local environment are applicable at current invocation of the messageflow only.<br>
6. Node reads PGP private/public keys and default decryption key passphrase information configured at UserDefined Configurable Service.


<h4>Conclusion</h4>

1. This SupportPac provides application-layer security enforcing data confidentiality and integrity powered by PGP cryptographic solution.<br>
2. Current version (v1.0.0.1) of this SupportPac only supports signature generation/validation integrated with encryption/decryption process.<br>
3. Future version will provide isolated signature generation/validation functionalities.<br>
4. Future version will provide better GUI at node properties view.<br>
5. Future version of pgpkeytool will be powered by user-friendly GUI similar to IBM Key Management tool shipped with Websphere MQ.<br>


<h4>Resources</h4>

1. PGP SupportPac binary, source code, documents, samples and other artifacts are available at GitHub (https://github.com/dipakpal/MyOpenTech-PGP-SupportPac)

2. Do you have any question ? Just put your question(s) at following IBM developerWorks
public community forum (https://www.ibm.com/developerworks/community/groups/community/pgpsupportpaciib)
or
MQSeries.net public forum (http://www.mqseries.net/phpBB2/viewtopic.php?t=68728)


<h4>Feedback</h4>
You can provide your valuable feedback/suggestion to <b>dipakpal.opentech@gmail.com</b>
