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

import io.spring.concourse.artifactoryresource.artifactory.payload.BuildArtifact;
import io.spring.concourse.artifactoryresource.artifactory.payload.BuildModule;
import io.spring.concourse.artifactoryresource.artifactory.payload.BuildRun;
import io.spring.concourse.artifactoryresource.artifactory.payload.ContinuousIntegrationAgent;
import io.spring.concourse.artifactoryresource.artifactory.payload.DeployableArtifact;
import io.spring.concourse.artifactoryresource.artifactory.payload.DeployableByteArrayArtifact;
import io.spring.concourse.artifactoryresource.artifactory.payload.DeployedArtifact;
import io.spring.concourse.artifactoryresource.command.BuildNumberGenerator;
import org.junit.rules.TemporaryFolder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests run against a real artifactory instance.
 *
 * @author Phillip Webb
 * @author Madhura Bhave
 */
public class IntegrationTestRun {

	private TemporaryFolder temporaryFolder;

	public IntegrationTestRun(TemporaryFolder temporaryFolder) {
		this.temporaryFolder = temporaryFolder;
	}

	public void run(ArtifactoryServer server) throws Exception {
		String buildNumber = generateBuildNumber();
		ArtifactoryRepository artifactoryRepository = server
				.repository("example-repo-local");
		ArtifactoryBuildRuns artifactoryBuildRuns = server.buildRuns("my-build");
		deployArtifact(artifactoryRepository, buildNumber);
		addBuildRun(artifactoryBuildRuns, buildNumber);
		getBuildRuns(artifactoryBuildRuns, buildNumber);
		downloadUsingBuildRun(artifactoryRepository, artifactoryBuildRuns, buildNumber);
	}

	private String generateBuildNumber() {
		return new BuildNumberGenerator().generateBuildNumber();
	}

	private void deployArtifact(ArtifactoryRepository artifactoryRepository,
			String buildNumber) throws Exception {
		Map<String, String> properties = new HashMap<>();
		properties.put("build.name", "my-build");
		properties.put("build.number", buildNumber);
		DeployableArtifact artifact = new DeployableByteArrayArtifact("foo/bar",
				"foo".getBytes(), properties);
		artifactoryRepository.deploy(artifact);
	}

	private void addBuildRun(ArtifactoryBuildRuns artifactoryBuildRuns,
			String buildNumber) throws Exception {
		BuildArtifact artifact = new BuildArtifact("test", "my-sha", "my-md5", "bar");
		BuildModule modules = new BuildModule("foo-test",
				Collections.singletonList(artifact));
		artifactoryBuildRuns.add(buildNumber, "ci.example.com",
				new ContinuousIntegrationAgent("Concourse", null),
				Collections.singletonList(modules));
	}

	private void getBuildRuns(ArtifactoryBuildRuns artifactoryBuildRuns,
			String buildNumber) {
		List<BuildRun> runs = artifactoryBuildRuns.getAll();
		assertThat(runs.get(0).getBuildNumber()).isEqualTo(buildNumber);
	}

	private void downloadUsingBuildRun(ArtifactoryRepository artifactoryRepository,
			ArtifactoryBuildRuns artifactoryBuildRuns, String buildNumber)
					throws Exception {
		this.temporaryFolder.create();
		List<DeployedArtifact> results = artifactoryBuildRuns
				.getDeployedArtifacts(buildNumber);
		File folder = this.temporaryFolder.newFolder();
		artifactoryRepository.download(results, folder);
		assertThat(new File(folder, "foo/bar")).hasContent("foo");
	}

}
