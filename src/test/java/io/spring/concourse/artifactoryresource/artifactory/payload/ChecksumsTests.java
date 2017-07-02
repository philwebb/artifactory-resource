/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.concourse.artifactoryresource.artifactory.payload;

import java.io.ByteArrayInputStream;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link Checksums}.
 *
 * @author Phillip Webb
 * @author Madhura Bhave
 */
public class ChecksumsTests {

	private static final String SHA1 = "a9993e364706816aba3e25717850c26c9cd0d89d";

	private static final String MD5 = "900150983cd24fb0d6963f7d28e17f72";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void createWhenSha1IsEmptyShouldThrowException() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("SHA1 must not be empty");
		new Checksums("", MD5);
	}

	@Test
	public void createWhenMd5IsEmptyShouldThrowException() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("MD5 must not be empty");
		new Checksums(SHA1, "");
	}

	@Test
	public void createWhenSha1IsIncorrectLengthShouldThrowException() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("SHA1 must be 40 characters long");
		new Checksums("0", MD5);
	}

	@Test
	public void createWhenMd5IsIncorrectLengthShouldThrowException() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("MD5 must be 32 characters long");
		new Checksums(SHA1, "0");
	}

	@Test
	public void getSha1ShouldGetSha1() throws Exception {
		Checksums checksums = new Checksums(SHA1, MD5);
		assertThat(checksums.getSha1()).isEqualTo(SHA1);
	}

	@Test
	public void getMd5ShouldGetMd5() throws Exception {
		Checksums checksums = new Checksums(SHA1, MD5);
		assertThat(checksums.getMd5()).isEqualTo(MD5);
	}

	@Test
	public void calculateShouldCalculateChecksums() throws Exception {
		ByteArrayInputStream bytes = new ByteArrayInputStream("abc".getBytes());
		Checksums checksums = Checksums.calculate(bytes);
		assertThat(checksums.getSha1()).isEqualTo(SHA1);
		assertThat(checksums.getMd5()).isEqualTo(MD5);
	}

}
