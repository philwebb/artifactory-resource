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

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import io.spring.concourse.artifactoryresource.artifactory.payload.Checksums;
import io.spring.concourse.artifactoryresource.artifactory.payload.DeployableArtifact;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.RequestEntity.BodyBuilder;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Default {@link ArtifactoryRepository} implementation communicating over HTTP.
 *
 * @author Phillip Webb
 * @author Madhura Bhave
 */
class HttpArtifactoryRepository implements ArtifactoryRepository {

	private static final MediaType BINARY_OCTET_STREAM = MediaType
			.parseMediaType("binary/octet-stream");

	private static final Object[] NO_VARIABLES = {};

	private final RestTemplate restTemplate;

	private final String uri;

	private final String repositoryName;

	public HttpArtifactoryRepository(RestTemplate restTemplate, String uri,
			String repositoryName) {
		this.restTemplate = restTemplate;
		this.uri = uri;
		this.repositoryName = repositoryName;
	}

	@Override
	public void deploy(DeployableArtifact artifact) {
		try {
			Assert.notNull(artifact, "Artifact must not be null");
			try {
				deployUsingChecksum(artifact);
			}
			catch (HttpClientErrorException ex) {
				if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
					deployUsingContent(artifact);
				}
			}
		}
		catch (IOException ex) {
			throw new RuntimeException(ex);
		}

	}

	private void deployUsingChecksum(DeployableArtifact artifact) throws IOException {
		RequestEntity<Void> request = deployRequest(artifact)
				.header("X-Checksum-Deploy", "true").build();
		this.restTemplate.exchange(request, Void.class);
	}

	private void deployUsingContent(DeployableArtifact artifact) throws IOException {
		RequestEntity<Resource> request = deployRequest(artifact)
				.body(artifact.getContent());
		this.restTemplate.exchange(request, Void.class);
	}

	private BodyBuilder deployRequest(DeployableArtifact artifact) throws IOException {
		URI uri = UriComponentsBuilder.fromUriString(this.uri)
				.path(this.repositoryName).path(artifact.getPath())
				.path(buildMatrixParams(artifact.getProperties())).build(NO_VARIABLES);
		Checksums checksums = artifact.getChecksums();
		return RequestEntity.put(uri).contentType(BINARY_OCTET_STREAM)
				.header("X-Checksum-Sha1", checksums.getSha1())
				.header("X-Checksum-Md5", checksums.getMd5());
	}

	private String buildMatrixParams(Map<String, String> matrixParams)
			throws UnsupportedEncodingException {
		StringBuilder matrix = new StringBuilder();
		if (matrixParams != null && !matrixParams.isEmpty()) {
			for (Map.Entry<String, String> entry : matrixParams.entrySet()) {
				matrix.append(";" + entry.getKey() + "=" + entry.getValue());
			}
		}
		return matrix.toString();
	}

	@Override
	public void download(String path, File destination) {
		Assert.hasLength(path, "Path must not be empty");
		URI uri = UriComponentsBuilder.fromUriString(this.uri)
				.path(this.repositoryName).path("/" + path).build(NO_VARIABLES);
		ResponseExtractor<Void> responseExtractor = (response) -> {
			Path fullPath = destination.toPath().resolve(path);
			Files.createDirectories(fullPath.getParent());
			Files.copy(response.getBody(), fullPath);
			return null;
		};
		this.restTemplate.execute(uri, HttpMethod.GET, null, responseExtractor);
	}

}
