/*
 * Copyright 2012-2017 the original author or authors.
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

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.junit.Test;

import org.springframework.util.FileCopyUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link AbstractDeployableArtifact}.
 *
 * @author Phillip Webb
 */
public abstract class AbstractArtifactTests {

	private static final byte[] CONTENT = "abc".getBytes();

	@Test
	public void createWhenPropertiesIsNullShouldUseEmptyProperties() throws Exception {
		AbstractDeployableArtifact artifact = create("foo", CONTENT, null, null);
		assertThat(artifact.getProperties()).isNotNull().isEmpty();
	}

	@Test
	public void createWhenChecksumIsNullShouldCalculateChecksums() throws Exception {
		AbstractDeployableArtifact artifact = create("foo", CONTENT, null, null);
		assertThat(artifact.getChecksums().getSha1())
				.isEqualTo("A9993E364706816ABA3E25717850C26C9CD0D89D");
		assertThat(artifact.getChecksums().getMd5())
				.isEqualTo("900150983CD24FB0D6963F7D28E17F72");
	}

	@Test
	public void getPropertiesShouldReturnProperties() throws Exception {
		Map<String, String> properties = Collections.singletonMap("foo", "bar");
		AbstractDeployableArtifact artifact = create("foo", CONTENT, properties, null);
		assertThat(artifact.getProperties()).isEqualTo(properties);
	}

	@Test
	public void getChecksumShouldReturnChecksum() throws Exception {
		Checksums checksums = new Checksums("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
				"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		AbstractDeployableArtifact artifact = create("foo", CONTENT, null, checksums);
		assertThat(artifact.getChecksums()).isEqualTo(checksums);
	}

	@Test
	public void getPathShouldReturnPath() throws Exception {
		AbstractDeployableArtifact artifact = create("foo/bar", CONTENT, null, null);
		assertThat(artifact.getPath()).isEqualTo("/foo/bar");
	}

	@Test
	public void getContentShouldReturnContent() throws Exception {
		AbstractDeployableArtifact artifact = create("foo", CONTENT, null, null);
		assertThat(FileCopyUtils.copyToByteArray(artifact.getContent().getInputStream()))
				.isEqualTo(CONTENT);
	}

	protected abstract AbstractDeployableArtifact create(String path, byte[] content,
			Map<String, String> properties, Checksums checksums) throws IOException;

}
