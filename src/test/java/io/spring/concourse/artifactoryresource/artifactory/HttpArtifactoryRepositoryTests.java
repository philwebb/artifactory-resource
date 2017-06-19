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
import java.util.HashMap;
import java.util.Map;

import io.spring.concourse.artifactoryresource.artifactory.payload.DeployableArtifact;
import io.spring.concourse.artifactoryresource.artifactory.payload.DeployableByteArrayArtifact;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Tests for {@link HttpArtifactoryRepository}.
 *
 * @author Phillip Webb
 */
@RunWith(SpringRunner.class)
@RestClientTest(HttpArtifactory.class)
public class HttpArtifactoryRepositoryTests {

	@Autowired
	private MockRestServiceServer server;

	@Autowired
	private Artifactory artifactory;

	@Test
	public void deployShouldUploadTheDeployableArtifact() throws IOException {
		ArtifactoryRepoistory repository = this.artifactory
				.server("http://repo.example.com", "admin", "password")
				.repository("libs-snapshot-local");
		DeployableArtifact artifact = new DeployableByteArrayArtifact("/foo/bar.jar", "foo".getBytes());
		this.server
				.expect(requestTo(
						"http://repo.example.com/libs-snapshot-local/foo/bar.jar"))
				.andExpect(method(PUT))
				.andExpect(header("X-Checksum-Deploy", "true"))
				.andExpect(header("X-Checksum-Sha1", artifact.getChecksums().getSha1()))
				.andRespond(withStatus(HttpStatus.NOT_FOUND));
		this.server
				.expect(requestTo(
						"http://repo.example.com/libs-snapshot-local/foo/bar.jar"))
				.andRespond(withSuccess());
		repository.deploy(artifact);
		this.server.verify();
	}

	@Test
	public void deployShouldUploadTheDeployableArtifactWithMatrixParameters() {
		ArtifactoryRepoistory repository = this.artifactory
				.server("http://repo.example.com", "admin", "password")
				.repository("libs-snapshot-local");
		Map<String, String> properties = new HashMap<>();
		properties.put("buildNumber", "1");
		properties.put("revision", "123");
		DeployableArtifact artifact = new DeployableByteArrayArtifact("/foo/bar.jar", "foo".getBytes(), properties);
		this.server
				.expect(requestTo(
						"http://repo.example.com/libs-snapshot-local/foo/bar.jar;buildNumber=1;revision=123"))
				.andRespond(withSuccess());
		repository.deploy(artifact);
		this.server.verify();
	}

	@Test
	public void deployWhenChecksumMatchesShouldNotUpload() throws Exception {
		ArtifactoryRepoistory repository = this.artifactory
				.server("http://repo.example.com", "admin", "password")
				.repository("libs-snapshot-local");
		DeployableArtifact artifact = new DeployableByteArrayArtifact("/foo/bar.jar", "foo".getBytes());
		this.server
				.expect(requestTo(
						"http://repo.example.com/libs-snapshot-local/foo/bar.jar"))
				.andExpect(method(PUT))
				.andExpect(header("X-Checksum-Deploy", "true"))
				.andExpect(header("X-Checksum-Sha1", artifact.getChecksums().getSha1()))
				.andRespond(withSuccess());
		repository.deploy(artifact);
		this.server.verify();
	}

}
