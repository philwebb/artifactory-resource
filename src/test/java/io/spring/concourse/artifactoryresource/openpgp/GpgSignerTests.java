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

package io.spring.concourse.artifactoryresource.openpgp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.SignatureException;
import java.util.Date;

import io.spring.concourse.artifactoryresource.openpgp.GpgSigner;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureGenerator;
import org.bouncycastle.openpgp.PGPSignatureSubpacketGenerator;
import org.bouncycastle.openpgp.operator.PBESecretKeyDecryptor;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;
import org.junit.jupiter.api.Test;

import org.springframework.util.FileCopyUtils;

/**
 * Tests for {@link GpgSigner}.
 *
 * @author Phillip Webb
 */
class GpgSignerTests {

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	@Test
	void test() throws Exception {
		String string = FileCopyUtils
				.copyToString(new InputStreamReader(getClass().getResourceAsStream("test-private.txt")));
		ByteArrayInputStream keyIn = new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8));
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		signFile2("/Users/pwebb/tmp/gpg/test", keyIn, out, "password".toCharArray(), true);
		System.out.println(new String(out.toByteArray(), StandardCharsets.UTF_8));
	}

	private static void signFile2(String fileName, InputStream keyIn, OutputStream out, char[] pass, boolean armor)
			throws IOException, NoSuchAlgorithmException, NoSuchProviderException, PGPException, SignatureException {
		out = new ArmoredOutputStream(out);
		PGPSecretKey signingKey = GpgSigner.getSigningKey(keyIn);
		PBESecretKeyDecryptor secretKeyDecryptor = new JcePBESecretKeyDecryptorBuilder().setProvider("BC").build(pass);
		PGPPrivateKey privateKey = signingKey.extractPrivateKey(secretKeyDecryptor);
		int signingAlgorithm = signingKey.getPublicKey().getAlgorithm();
		JcaPGPContentSignerBuilder contentSigner = new JcaPGPContentSignerBuilder(signingAlgorithm,
				HashAlgorithmTags.SHA256).setProvider("BC");
		PGPSignatureSubpacketGenerator subpacketGenerator = new PGPSignatureSubpacketGenerator();
		subpacketGenerator.setIssuerFingerprint(false, signingKey.getPublicKey());
		subpacketGenerator.setSignatureCreationTime(false, new Date());
		PGPSignatureGenerator signatureGenerator = new PGPSignatureGenerator(contentSigner);
		signatureGenerator.init(PGPSignature.BINARY_DOCUMENT, privateKey);
		signatureGenerator.setHashedSubpackets(subpacketGenerator.generate());
		FileInputStream fileInputStream = new FileInputStream(new File(fileName));
		// Shite, use block
		int ch;
		while ((ch = fileInputStream.read()) >= 0) {
			signatureGenerator.update((byte) ch);
		}
		signatureGenerator.generate().encode(out);
		out.close();
	}

}
