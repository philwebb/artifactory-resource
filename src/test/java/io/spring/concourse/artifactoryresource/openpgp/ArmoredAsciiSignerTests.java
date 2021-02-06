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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import org.springframework.util.FileCopyUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ArmoredAsciiSigner}.
 *
 * @author Phillip Webb
 */
class ArmoredAsciiSignerTests {

	private static final Clock FIXED = Clock.fixed(Instant.EPOCH, ZoneId.of("UTC"));

	static File signingKey;

	static String signingKeyContent;

	static String passphrase = "password";

	static File source;

	static String sourceContent;

	static String expectedSignature;

	@BeforeAll
	static void setup(@TempDir File temp) throws Exception {
		signingKey = copyClasspathFile(temp, "test-private.txt");
		signingKeyContent = copyToString(signingKey);
		source = copyClasspathFile(temp, "source.txt");
		sourceContent = copyToString(source);
		expectedSignature = copyToString(ArmoredAsciiSignerTests.class.getResourceAsStream("expected.asc"));
	}

	private static File copyClasspathFile(File temp, String name) throws IOException, FileNotFoundException {
		File file = new File(temp, name);
		FileCopyUtils.copy(ArmoredAsciiSignerTests.class.getResourceAsStream(name), new FileOutputStream(file));
		return file;
	}

	private static String copyToString(File file) throws IOException {
		return copyToString(new FileInputStream(file));
	}

	private static String copyToString(InputStream inputStream) throws IOException {
		return new String(FileCopyUtils.copyToByteArray(inputStream), StandardCharsets.UTF_8);
	}

	@Test
	void getWithStringSigningKeyWhenSigningKeyIsKeyReturnsSigner() throws Exception {
		ArmoredAsciiSigner signer = ArmoredAsciiSigner.get(FIXED, signingKeyContent, passphrase);
		assertThat(signer.sign(sourceContent)).isEqualTo(expectedSignature);
	}

	@Test
	void getWithStringSigningKeyWhenSigningKeyIsFileReturnsSigner() {

	}

	@Test
	void getWithStringSigningKeyWhenClockIsNullThrowsException() {

	}

	@Test
	void getWithStringSigningKeyWhenSigningKeyIsNullThrowsException() {

	}

	@Test
	void getWithStringSigningKeyWhenSigningKeyIsMultiLineWithoutHeaderThrowsException() {

	}

	@Test
	void getWithStringSigningKeyWhenSigningKeyIsMalformedThrowsException() {

	}

	@Test
	void getWithStringSigningKeyWhenPassphraseIsNullThrowsException() {

	}

	@Test
	void getWithStringSigningKeyWhenPassphraseIsWrongThrowsException() {

	}

	@Test
	void getWithFileSigningKeyKeyReturnsSigner() {

	}

	@Test
	void getWithFileSigningKeyWhenClockIsNullThrowsException() {

	}

	@Test
	void getWithFileSigningKeyWhenSigningKeyIsNullThrowsException() {

	}

	@Test
	void getWithFileSigningKeyWhenSigningKeyIsMalformedThrowsException() {

	}

	@Test
	void getWithFileSigningKeyWhenPassphraseIsNullThrowsException() {

	}

	@Test
	void getWithFileSigningKeyWhenPassphraseIsWrongThrowsException() {

	}

	@Test
	void getWithInputStreamSourceSigningKeyKeyReturnsSigner() {

	}

	@Test
	void getWithInputStreamSourceSigningKeyWhenClockIsNullThrowsException() {

	}

	@Test
	void getWithInputStreamSourceSigningKeyWhenSigningKeyIsNullThrowsException() {

	}

	@Test
	void getWithInputStreamSourceSigningKeyWhenSigningKeyIsMalformedThrowsException() {

	}

	@Test
	void getWithInputStreamSourceSigningKeyWhenPassphraseIsNullThrowsException() {

	}

	@Test
	void getWithInputStreamSourceSigningKeyWhenPassphraseIsWrongThrowsException() {

	}

	@Test
	void getWithInputStreamSigningKeyKeyReturnsSigner() {

	}

	@Test
	void getWithInputStreamSigningKeyWhenClockIsNullThrowsException() {

	}

	@Test
	void getWithInputStreamSigningKeyWhenSigningKeyIsNullThrowsException() {

	}

	@Test
	void getWithInputStreamSigningKeyWhenSigningKeyIsMalformedThrowsException() {

	}

	@Test
	void getWithInputStreamSigningKeyWhenPassphraseIsNullThrowsException() {

	}

	@Test
	void getWithInputStreamSigningKeyWhenPassphraseIsWrongThrowsException() {

	}

	@Test
	void signWithStringSourceReturnsSignature() {

	}

	@Test
	void signWithStringSourceWhenSourceIsNullThrowsException() {

	}

	@Test
	void signWithInputStreamSourceReturnsSignature() {

	}

	@Test
	void signWithInputStreamSourceWhenSourceIsNullThrowsException() {

	}

	@Test
	void signWithInputStreamReturnsSignature() {

	}

	@Test
	void signWithInputStreamWhenSourceIsNullThrowsException() {

	}

	@Test
	void signWithInputStreamAndOutputStreamWritesSignature() {

	}

	@Test
	void signWithInputStreamAndOutputStreamWritesWhenSourceIsNullThrowsException() {

	}

	@Test
	void signWithInputStreamAndOutputStreamWritesWhenDestinationIsNullThrowsException() {

	}

}
