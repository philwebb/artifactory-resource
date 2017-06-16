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

import io.spring.concourse.artifactoryresource.artifactory.payload.DeployableArtifact;
import io.spring.concourse.artifactoryresource.artifactory.payload.DeployableByteArrayArtifact;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Tests for {@link HttpArtifactoryRepoistory}.
 *
 * @author Phillip Webb
 */
@RunWith(SpringRunner.class)
@RestClientTest(HttpArtifactory.class)
public class HttpArtifactoryRepoistoryTests {

	@Autowired
	private MockRestServiceServer server;

	@Autowired
	private Artifactory artifactory;

	@Test
	public void test() {
		ArtifactoryRepoistory repository = this.artifactory
				.server("http://repo.example.com", "admin", "password")
				.repository("libs-snapshot-local");
		DeployableArtifact artifact = new DeployableByteArrayArtifact("/foo/bar.jar", "foo".getBytes());
		this.server
				.expect(requestTo(
						"http://repo.example.com/libs-snapshot-local/foo/bar.jar"))
				.andRespond(withSuccess());
		repository.deploy(artifact);
	}

}
