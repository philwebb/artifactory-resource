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

package io.spring.concourse.artifactoryresource.artifactory;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.palantir.docker.compose.DockerComposeRule;
import com.palantir.docker.compose.connection.DockerPort;
import com.palantir.docker.compose.connection.waiting.HealthChecks;
import io.spring.concourse.artifactoryresource.artifactory.payload.BuildArtifact;
import io.spring.concourse.artifactoryresource.artifactory.payload.BuildModule;
import io.spring.concourse.artifactoryresource.artifactory.payload.BuildRun;
import io.spring.concourse.artifactoryresource.artifactory.payload.ContinuousIntegrationAgent;
import io.spring.concourse.artifactoryresource.artifactory.payload.DeployableArtifact;
import io.spring.concourse.artifactoryresource.artifactory.payload.DeployableByteArrayArtifact;
import io.spring.concourse.artifactoryresource.artifactory.payload.DeployedArtifact;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests against a real artifactory instance.
 *
 * @author Phillip Webb
 * @author Madhura Bhave
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

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	public static DockerPort port;

	@BeforeClass
	public static void initialize() {
		port = docker.containers().container("artifactory").port(8081);
	}

	@Autowired
	private Artifactory artifactory;

	@Test
	public void integrationTest() throws Exception {
		ArtifactoryRepository artifactoryRepository = server()
				.repository("example-repo-local");
		ArtifactoryBuildRuns artifactoryBuildRuns = server().buildRuns("my-build");
		deployArtifact(artifactoryRepository);
		addBuildRun(artifactoryBuildRuns);
		getBuildRuns(artifactoryBuildRuns);
		downloadUsingBuildRun(artifactoryRepository, artifactoryBuildRuns);
	}

	private void deployArtifact(ArtifactoryRepository artifactoryRepository)
			throws Exception {
		Map<String, String> properties = new HashMap<>();
		properties.put("build.name", "my-build");
		properties.put("build.number", "1");
		DeployableArtifact artifact = new DeployableByteArrayArtifact("foo/bar",
				"foo".getBytes(), properties);
		artifactoryRepository.deploy(artifact);
	}

	private void addBuildRun(ArtifactoryBuildRuns artifactoryBuildRuns) throws Exception {
		BuildArtifact artifact = new BuildArtifact("test", "my-sha", "my-md5", "bar");
		BuildModule modules = new BuildModule("foo-test",
				Collections.singletonList(artifact));
		artifactoryBuildRuns.add("1", "ci.example.com",
				new ContinuousIntegrationAgent("Concourse", null),
				Collections.singletonList(modules));
	}

	private void getBuildRuns(ArtifactoryBuildRuns artifactoryBuildRuns) {
		List<BuildRun> runs = artifactoryBuildRuns.getAll();
		assertThat(runs.get(0).getBuildNumber()).isEqualTo("1");
	}

	private void downloadUsingBuildRun(ArtifactoryRepository artifactoryRepository,
			ArtifactoryBuildRuns artifactoryBuildRuns) throws Exception {
		this.temporaryFolder.create();
		List<DeployedArtifact> results = artifactoryBuildRuns.getDeployedArtifacts("1");
		File folder = this.temporaryFolder.newFolder();
		artifactoryRepository.download(results, folder);
		assertThat(new File(folder, "foo/bar")).hasContent("foo");
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
