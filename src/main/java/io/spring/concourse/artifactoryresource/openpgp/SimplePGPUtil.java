package io.spring.concourse.artifactoryresource.openpgp;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.Security;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.BCPGOutputStream;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureGenerator;
import org.bouncycastle.openpgp.PGPUtil;

/**
 * Simple PGP utility that takes a target file and produces a .asc-style signature for it.
 * Specifically designed not to depend on the Artifactory public API, such that it may
 * easily be tested and modified in a standalone way via copy-and-paste if need be.
 *
 * @author Chris Beams
 */
class SimplePGPUtil {

	/**
	 * Sign the contents of the given input stream using the given secret key and
	 * passphrase, writing the resulting signature to the given armored output stream,
	 * i.e. suitable for .asc file suffix.
	 * @param secretKeyFile normal filesystem path to a PGP secret key file that must
	 * contain one and only one secret key
	 * @param passphrase for the secret key
	 * @param targetFileIn input stream of the file to be signed, closed by this method
	 * @param signatureFileOut output stream of the signature file, closed by this method
	 * @throws Exception if anything fails in the signing process
	 */
	static void signFile(String secretKeyFile, char[] passphrase, InputStream targetFileIn,
			ArmoredOutputStream signatureFileOut) throws Exception {

		Security.addProvider(new BouncyCastleProvider());

		FileInputStream secretKeyIn = new FileInputStream(secretKeyFile);

		// as advertised, the .key file must have one and only one secret key within due
		// to the opinionated nature of the following call chain.
		PGPSecretKey pgpSec = (new PGPSecretKeyRingCollection(PGPUtil.getDecoderStream(secretKeyIn)).getKeyRings()
				.next().getSecretKeys().next());

		PGPPrivateKey pgpPrivKey = pgpSec.extractPrivateKey(passphrase, "BC");

		PGPSignatureGenerator signatureGenerator = new PGPSignatureGenerator(pgpSec.getPublicKey().getAlgorithm(),
				HashAlgorithmTags.SHA1, "BC");

		signatureGenerator.initSign(PGPSignature.BINARY_DOCUMENT, pgpPrivKey);

		BCPGOutputStream bcpgOut = new BCPGOutputStream(signatureFileOut);

		int ch = 0;
		while ((ch = targetFileIn.read()) >= 0) {
			bcpgOut.write(ch);
			signatureGenerator.update((byte) ch);
		}

		PGPSignature signature = signatureGenerator.generate();
		signature.encode(bcpgOut);

		targetFileIn.close();
		secretKeyIn.close();
		bcpgOut.close();
		signatureFileOut.close();
	}

}
