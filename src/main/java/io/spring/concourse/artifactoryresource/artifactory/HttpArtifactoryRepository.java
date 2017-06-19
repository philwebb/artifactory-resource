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
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Map;

import io.spring.concourse.artifactoryresource.artifactory.payload.Checksums;
import io.spring.concourse.artifactoryresource.artifactory.payload.DeployableArtifact;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Default {@link ArtifactoryRepository} implementation communicating over HTTP.
 *
 * @author Phillip Webb
 */
class HttpArtifactoryRepository implements ArtifactoryRepository {

	private static final MediaType BINARY_OCTET_STREAM = MediaType
			.parseMediaType("binary/octet-stream");

	private static final Object[] NO_VARIABLES = {};

	private final RestTemplate restTemplate;

	private final UriComponentsBuilder uri;

	private final String repositoryName;

	public HttpArtifactoryRepository(RestTemplate restTemplate, UriComponentsBuilder uri,
			String repositoryName) {
		this.restTemplate = restTemplate;
		this.uri = uri;
		this.repositoryName = repositoryName;
	}

	@Override
	public void deploy(DeployableArtifact artifact) {
		try {
			Assert.notNull(artifact, "Artifact must not be null");
			Map<String, String> properties = artifact.getProperties();
			URI deployUri = this.uri.path(this.repositoryName).path(artifact.getPath()).path(buildMatrixParams(properties))
					.build(NO_VARIABLES);
			Checksums checksums = artifact.getChecksums();
			RequestEntity checksumRequest = RequestEntity.put(deployUri)
					.contentType(BINARY_OCTET_STREAM)
					.header("X-Checksum-Sha1", checksums.getSha1())
					.header("X-Checksum-Deploy", "true").build();
			try {
				this.restTemplate.exchange(checksumRequest, Void.class);
			} catch (HttpClientErrorException ex) {
				if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
					RequestEntity<Resource> request = RequestEntity.put(deployUri)
							.contentType(BINARY_OCTET_STREAM)
							.header("X-Checksum-Sha1", checksums.getSha1())
							.header("X-Checksum-Md5", checksums.getMd5())
							.body(artifact.getContent());
					this.restTemplate.exchange(request, Void.class);
				}
			}
		}
		catch (IOException ex) {
			throw new RuntimeException(ex);
		}

	}

	private String buildMatrixParams(Map<String, String> matrixParams) throws UnsupportedEncodingException {
		StringBuilder matrix = new StringBuilder();
		if (matrixParams != null && !matrixParams.isEmpty()) {
			for (Map.Entry<String, String> property : matrixParams.entrySet()) {
				matrix.append(";").append(property.getKey())
						.append("=").append(property.getValue());
			}
		}
		return matrix.toString();
	}

}
