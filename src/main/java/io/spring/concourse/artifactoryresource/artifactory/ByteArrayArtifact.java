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

package io.spring.concourse.artifactoryresource.artifactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.springframework.util.Assert;

/**
 * {@link Artifact} backed by a byte array.
 */
public class ByteArrayArtifact extends AbstractArtifact {

	private final byte[] content;

	public ByteArrayArtifact(String path, byte[] content) {
		this(path, content, null);
	}

	public ByteArrayArtifact(String path, byte[] content,
			Map<String, String> properties) {
		this(path, content, properties, null);
	}

	public ByteArrayArtifact(String path, byte[] content, Map<String, String> properties,
			Checksums checksums) {
		super(path, properties, checksums);
		Assert.notNull(content, "Content must not be null");
		this.content = content;
	}

	@Override
	public InputStream getContent() throws IOException {
		return new ByteArrayInputStream(this.content);
	}

}
