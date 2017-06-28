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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.palantir.docker.compose.DockerComposeRule;
import com.palantir.docker.compose.connection.DockerPort;
import com.palantir.docker.compose.connection.waiting.HealthChecks;
import io.spring.concourse.artifactoryresource.artifactory.payload.BuildArtifact;
import io.spring.concourse.artifactoryresource.artifactory.payload.BuildModule;
import io.spring.concourse.artifactoryresource.artifactory.payload.BuildRunsResponse;
import io.spring.concourse.artifactoryresource.artifactory.payload.ContinuousIntegrationAgent;
import io.spring.concourse.artifactoryresource.artifactory.payload.DeployableArtifact;
import io.spring.concourse.artifactoryresource.artifactory.payload.DeployableByteArrayArtifact;
import io.spring.concourse.artifactoryresource.artifactory.payload.DeployedArtifact;
import io.spring.concourse.artifactoryresource.artifactory.payload.DeployedArtifactQueryResponse;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author Phillip Webb
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class HttpArtifactoryIT {

	@ClassRule
	public static DockerComposeRule docker = DockerComposeRule.builder()
			.file("src/integration/resources/docker-compose.yml")
			.waitingForService("artifactory", HealthChecks.toRespond2xxOverHttp(8081,
					HttpArtifactoryIT::artifactoryUri))
			.build();

	public static DockerPort port;

	@BeforeClass
	public static void initialize() {
		port = docker.containers().container("artifactory").port(8081);
	}

	@Autowired
	private Artifactory artifactory;

	@Test
	public void repositoryDeploy() throws Exception {
		ArtifactoryRepository repository = server().repository("example-repo-local");
		Map<String, String> properties = new HashMap<>();
		properties.put("buildNumber", "1");
		properties.put("revision", "123");
		DeployableArtifact artifact = new DeployableByteArrayArtifact("foo/bar",
				"foo".getBytes(), properties);
		repository.deploy(artifact);
	}

	@Test
	public void testName() throws Exception {
		ArtifactoryBuildRuns buildRuns = server().buildRuns("my-build");
		BuildArtifact artifact = new BuildArtifact("test", null, null, "bar");
		BuildModule modules = new BuildModule("foo-test",
				Collections.singletonList(artifact));
		buildRuns.add("1", "ci.example.com",
				new ContinuousIntegrationAgent("Concourse", null),
				Collections.singletonList(modules));
	}

	@Test
	public void getBuildRuns() throws Exception {
		ArtifactoryBuildRuns buildRuns = server().buildRuns("my-build");
		BuildRunsResponse runs = buildRuns.getAll();
	}

	@Test
	public void fetchArtifacts() throws Exception {
		DeployedArtifactQueryResponse results = server().repository("libs-snapshot-local")
				.getDeployedArtifacts("spring-boot-dependencies", "1497125213592");
		for (DeployedArtifact artifact : results.getResults()) {
			server().repository("libs-snapshot-local").download(
					"/" + artifact.getPath() + "/" + artifact.getName(),
					"/tmp/helloworld");
		}
	}

	private ArtifactoryServer server() {
		return this.artifactory.server(artifactoryUri(), "admin", "password");
	}

	public static String artifactoryUri() {
		return artifactoryUri(port);
	}

	private static String artifactoryUri(DockerPort port) {
		return port.inFormat("http://$HOST:$EXTERNAL_PORT/artifactory");
	}

}
