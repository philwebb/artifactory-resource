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

import java.io.IOException;
import java.net.URI;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Default {@link ArtifactoryRepoistory} implementation communicating over HTTP.
 */
class HttpArtifactoryRepoistory implements ArtifactoryRepoistory {

	private static final MediaType BINARY_OCTET_STREAM = MediaType
			.parseMediaType("binary/octet-stream");

	private static final Object[] NO_VARIABLES = {};

	private final UriComponentsBuilder uri;

	private final RestTemplate restTemplate;

	public HttpArtifactoryRepoistory(UriComponentsBuilder uri,
			RestTemplate restTemplate) {
		this.uri = uri;
		this.restTemplate = restTemplate;
	}

	@Override
	public void deploy(Artifact artifact) {
		try {
			Assert.notNull(artifact, "Artifact must not be null");
			URI deployUri = this.uri.path(artifact.getPath()).build(NO_VARIABLES);
			Checksums checksums = artifact.getChecksums();
			RequestEntity<Resource> request = RequestEntity.put(deployUri)
					.contentType(BINARY_OCTET_STREAM)
					.header("X-Checksum-Sha1", checksums.getSha1())
					.header("X-Checksum-Md5", checksums.getMd5())
					.body(new InputStreamResource(artifact.getContent()));
			// FIXME "X-Checksum-Deploy", "true" (based on min size 10240)
			this.restTemplate.exchange(request, String.class);
		}
		catch (IOException ex) {
			throw new RuntimeException(ex);
		}

	}

}
