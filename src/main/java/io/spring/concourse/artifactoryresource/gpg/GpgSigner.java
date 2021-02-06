/*
 * Copyright 2017-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.concourse.artifactoryresource.gpg;

import java.io.IOException;
import java.io.InputStream;
import java.security.Security;
import java.util.Iterator;

import io.spring.concourse.artifactoryresource.artifactory.payload.DeployableArtifact;
import io.spring.concourse.artifactoryresource.io.FileSet.Category;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.bc.BcPGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;

import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;

/**
 * @author Phillip Webb
 */
public class GpgSigner {

	private static final JcaKeyFingerprintCalculator FINGERPRINT_CALCULATOR = new JcaKeyFingerprintCalculator();

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	public GpgSigner(String privateKey, String passphrase) throws IOException, PGPException {
		InputStream secretKeyInputStream = null;
		InputStream decoderStream = PGPUtil.getDecoderStream(secretKeyInputStream);
		BcPGPSecretKeyRingCollection collection = new BcPGPSecretKeyRingCollection(decoderStream);
		Iterator<PGPSecretKeyRing> keyRings = collection.getKeyRings();
		PGPSecretKeyRing keyRing = keyRings.next();
		PGPSecretKey secretKey = keyRing.getSecretKey();
		secretKey.extractPrivateKey(null);
	}

	public MultiValueMap<Category, DeployableArtifact> sign(
			MultiValueMap<Category, DeployableArtifact> batchedArtifacts) {
		return batchedArtifacts;
	}

	static PGPSecretKey getSigningKey(InputStream inputStream) throws IOException, PGPException {
		try (InputStream decoderStream = PGPUtil.getDecoderStream(inputStream)) {
			PGPSecretKeyRingCollection keyrings = new PGPSecretKeyRingCollection(decoderStream, FINGERPRINT_CALCULATOR);
			return getSigningKey(keyrings);
		}
	}

	private static PGPSecretKey getSigningKey(PGPSecretKeyRingCollection keyrings) {
		PGPSecretKey result = null;
		for (PGPSecretKeyRing keyring : keyrings) {
			Iterable<PGPSecretKey> secretKeys = keyring::getSecretKeys;
			for (PGPSecretKey candidate : secretKeys) {
				if (candidate.isSigningKey()) {
					return candidate;
					// System.err.println(candidate.getKeyEncryptionAlgorithm());
					// SupportedAlgorithm algorithm = SupportedAlgorithm.get(candidate);
					// if (candidate.isSigningKey() && algorithm != null &&
					// algorithm.isBetterThan(result)) {
					// result = candidate;
					// }
				}
			}
		}
		Assert.notNull(result, "Keyring does not contain a suitable signing key");
		return result;
	}

	enum SupportedAlgorithm {

		/**
		 * SHA-1
		 */
		SHA1(HashAlgorithmTags.SHA1),

		/**
		 * SHA-256
		 */
		SHA256(HashAlgorithmTags.SHA256),

		/**
		 * SHA-512
		 */
		SHA512(HashAlgorithmTags.SHA512);

		private final int tag;

		SupportedAlgorithm(int tag) {
			this.tag = tag;
		}

		boolean isBetterThan(PGPSecretKey key) {
			SupportedAlgorithm algorithm = get(key);
			return algorithm != null && algorithm.ordinal() > ordinal();
		}

		static SupportedAlgorithm get(PGPSecretKey key) {
			for (SupportedAlgorithm candidate : values()) {
				if (candidate.tag == key.getKeyEncryptionAlgorithm()) {
					return candidate;
				}
			}
			return null;
		}

	}

}
