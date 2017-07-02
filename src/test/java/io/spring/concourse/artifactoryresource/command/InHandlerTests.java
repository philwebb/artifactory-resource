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

package io.spring.concourse.artifactoryresource.command;

import java.util.ArrayList;
import java.util.List;

import io.spring.concourse.artifactoryresource.artifactory.Artifactory;
import io.spring.concourse.artifactoryresource.artifactory.ArtifactoryBuildRuns;
import io.spring.concourse.artifactoryresource.artifactory.ArtifactoryRepository;
import io.spring.concourse.artifactoryresource.artifactory.ArtifactoryServer;
import io.spring.concourse.artifactoryresource.artifactory.payload.DeployedArtifact;
import io.spring.concourse.artifactoryresource.command.payload.InRequest;
import io.spring.concourse.artifactoryresource.command.payload.InRequest.Params;
import io.spring.concourse.artifactoryresource.command.payload.InResponse;
import io.spring.concourse.artifactoryresource.command.payload.Source;
import io.spring.concourse.artifactoryresource.command.payload.Version;
import io.spring.concourse.artifactoryresource.io.Directory;
import io.spring.concourse.artifactoryresource.maven.MavenMetadataGenerator;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * Tests for {@link InHandler}.
 *
 * @author Madhura Bhave
 * @author Phillip Webb
 */
public class InHandlerTests {

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Mock
	private Artifactory artifactory;

	@Mock
	private MavenMetadataGenerator mavenMetadataGenerator;

	@Mock
	private ArtifactoryServer artifactoryServer;

	@Mock
	private ArtifactoryBuildRuns artifactoryBuildRuns;

	@Mock
	private ArtifactoryRepository artifactoryRepository;

	private List<DeployedArtifact> deployedArtifacts;

	private InHandler handler;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.deployedArtifacts = createDeployedArtifacts();
		given(this.artifactory.server("http://ci.example.com", "admin", "password"))
				.willReturn(this.artifactoryServer);
		given(this.artifactoryServer.buildRuns("my-build"))
				.willReturn(this.artifactoryBuildRuns);
		given(this.artifactoryServer.repository("libs-snapshot-local"))
				.willReturn(this.artifactoryRepository);
		given(this.artifactoryBuildRuns.getDeployedArtifacts("1234"))
				.willReturn(this.deployedArtifacts);
		this.handler = new InHandler(this.artifactory, this.mavenMetadataGenerator);
	}

	private List<DeployedArtifact> createDeployedArtifacts() {
		List<DeployedArtifact> deployedArtifacts = new ArrayList<>();
		deployedArtifacts.add(
				new DeployedArtifact("libs-snapshot-local", "foo.jar", "com/example"));
		deployedArtifacts.add(
				new DeployedArtifact("libs-snapshot-local", "bar.jar", "com/example"));
		return deployedArtifacts;
	}

	@Test
	public void handleShouldDownloadArtifacts() throws Exception {
		InRequest request = createRequest(false);
		Directory directory = new Directory(this.temporaryFolder.newFolder());
		InResponse response = this.handler.handle(request, directory);
		verify(this.artifactoryRepository).download(this.deployedArtifacts,
				directory.getFile());
		assertThat(response.getVersion()).isEqualTo(request.getVersion());
	}

	@Test
	public void handleWhenHasGenerateMavenMetadataParamShouldGenerateMetadata()
			throws Exception {
		InRequest request = createRequest(true);
		Directory directory = new Directory(this.temporaryFolder.newFolder());
		this.handler.handle(request, directory);
		verify(this.mavenMetadataGenerator).generate(directory);
	}

	@Test
	public void handleWhenHasNoGenerateMavenMetadataParamShouldNotGenerateMetadata()
			throws Exception {
		InRequest request = createRequest(false);
		Directory directory = new Directory(this.temporaryFolder.newFolder());
		this.handler.handle(request, directory);
		verifyZeroInteractions(this.mavenMetadataGenerator);
	}

	private InRequest createRequest(boolean generateMavenMetadata) {
		InRequest request = new InRequest(
				new Source("http://ci.example.com", "admin", "password", "my-build"),
				new Version("1234"), new Params(generateMavenMetadata));
		return request;
	}

}
