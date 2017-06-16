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

import io.spring.concourse.artifactoryresource.AbstractArtifactoryIT;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class HttpArtifactoryRepoistoryIT extends AbstractArtifactoryIT {

	@Autowired
	private Artifactory artifactory;

	@Test
	public void deployArtifact() throws Exception {
		ArtifactoryRepoistory repository = this.artifactory
				.server(artifactoryUri(), "admin", "password").repository("example-repo-local");
		Artifact artifact = new ByteArrayArtifact("foo/bar", "foo".getBytes());
		repository.deploy(artifact);
	}

}