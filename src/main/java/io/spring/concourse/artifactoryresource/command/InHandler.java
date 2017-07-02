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

import java.io.File;
import java.util.List;

import io.spring.concourse.artifactoryresource.artifactory.Artifactory;
import io.spring.concourse.artifactoryresource.artifactory.ArtifactoryServer;
import io.spring.concourse.artifactoryresource.artifactory.payload.DeployedArtifact;
import io.spring.concourse.artifactoryresource.command.payload.InRequest;
import io.spring.concourse.artifactoryresource.command.payload.InResponse;
import io.spring.concourse.artifactoryresource.command.payload.Source;
import io.spring.concourse.artifactoryresource.command.payload.Version;
import io.spring.concourse.artifactoryresource.io.Directory;
import io.spring.concourse.artifactoryresource.maven.MavenMetadataGenerator;

import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Delegate used to handle operations triggered from the {@link InCommand}.
 *
 * @author Phillip Webb
 * @author Madhura Bhave
 */
@Component
public class InHandler {

	private final Artifactory artifactory;

	private final MavenMetadataGenerator mavenMetadataGenerator;

	public InHandler(Artifactory artifactory,
			MavenMetadataGenerator mavenMetadataGenerator) {
		this.artifactory = artifactory;
		this.mavenMetadataGenerator = mavenMetadataGenerator;
	}

	public InResponse handle(InRequest request, Directory directory) {
		Source source = request.getSource();
		Version version = request.getVersion();
		String buildNumber = version.getBuildNumber();
		ArtifactoryServer artifactoryServer = getArtifactoryServer(request.getSource());
		List<DeployedArtifact> artifacts = artifactoryServer
				.buildRuns(source.getBuildName()).getDeployedArtifacts(buildNumber);
		download(artifactoryServer, groupByRepo(artifacts), directory.getFile());
		if (request.getParams().isGenerateMavenMetadata()) {
			this.mavenMetadataGenerator.generate(directory);
		}
		return new InResponse(version);
	}

	private ArtifactoryServer getArtifactoryServer(Source source) {
		return this.artifactory.server(source.getUri(), source.getUsername(),
				source.getPassword());
	}

	private MultiValueMap<String, DeployedArtifact> groupByRepo(
			List<DeployedArtifact> artifacts) {
		MultiValueMap<String, DeployedArtifact> artifactsByRepo = new LinkedMultiValueMap<>();
		artifacts.stream().forEach((a) -> artifactsByRepo.add(a.getRepo(), a));
		return artifactsByRepo;
	}

	private void download(ArtifactoryServer artifactoryServer,
			MultiValueMap<String, DeployedArtifact> artifacts, File destination) {
		artifacts.forEach(
				(k, v) -> artifactoryServer.repository(k).download(v, destination));
	}

}
