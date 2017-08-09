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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.spring.concourse.artifactoryresource.artifactory.Artifactory;
import io.spring.concourse.artifactoryresource.artifactory.ArtifactoryRepository;
import io.spring.concourse.artifactoryresource.artifactory.ArtifactoryServer;
import io.spring.concourse.artifactoryresource.artifactory.payload.BuildModule;
import io.spring.concourse.artifactoryresource.artifactory.payload.DeployableArtifact;
import io.spring.concourse.artifactoryresource.artifactory.payload.DeployableFileArtifact;
import io.spring.concourse.artifactoryresource.command.payload.OutRequest;
import io.spring.concourse.artifactoryresource.command.payload.OutRequest.ArtifactSet;
import io.spring.concourse.artifactoryresource.command.payload.OutRequest.Params;
import io.spring.concourse.artifactoryresource.command.payload.OutResponse;
import io.spring.concourse.artifactoryresource.command.payload.Source;
import io.spring.concourse.artifactoryresource.command.payload.Version;
import io.spring.concourse.artifactoryresource.io.Directory;
import io.spring.concourse.artifactoryresource.io.DirectoryScanner;
import io.spring.concourse.artifactoryresource.io.PathFilter;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Delegate used to handle operations triggered from the {@link OutCommand}.
 *
 * @author Phillip Webb
 * @author Madhura Bhave
 */
@Component
public class OutHandler {

	private final Artifactory artifactory;

	private final BuildNumberGenerator buildNumberGenerator;

	private final ModuleLayouts moduleLayouts;

	private final DirectoryScanner directoryScanner;

	public OutHandler(Artifactory artifactory, BuildNumberGenerator buildNumberGenerator,
			ModuleLayouts moduleLayouts, DirectoryScanner directoryScanner) {
		this.artifactory = artifactory;
		this.buildNumberGenerator = buildNumberGenerator;
		this.moduleLayouts = moduleLayouts;
		this.directoryScanner = directoryScanner;
	}

	public OutResponse handle(OutRequest request, Directory directory) {
		Source source = request.getSource();
		Params params = request.getParams();
		String buildNumber = getOrGenerateBuildNumber(params);
		ArtifactoryServer artifactoryServer = getArtifactoryServer(source);
		List<DeployableArtifact> artifacts = getDeployableArtifacts(buildNumber, source,
				params, directory);
		Assert.state(artifacts.size() > 0, "No artifacts found to deploy");
		deployArtifacts(artifactoryServer, params, artifacts);
		addBuildRun(artifactoryServer, source, params, buildNumber, artifacts);
		return new OutResponse(new Version(buildNumber));
	}

	private ArtifactoryServer getArtifactoryServer(Source source) {
		return this.artifactory.server(source.getUri(), source.getUsername(),
				source.getPassword());
	}

	private String getOrGenerateBuildNumber(Params params) {
		if (StringUtils.hasLength(params.getBuildNumber())) {
			return params.getBuildNumber();
		}
		return this.buildNumberGenerator.generateBuildNumber();
	}

	private List<DeployableArtifact> getDeployableArtifacts(String buildNumber,
			Source source, Params params, Directory directory) {
		Directory root = directory.getSubDirectory(params.getFolder());
		List<File> files = this.directoryScanner.scan(root, params.getInclude(),
				params.getExclude());
		return files.stream().map((file) -> {
			String path = DeployableFileArtifact.calculatePath(root.getFile(), file);
			Map<String, String> properties = getDeployableArtifactProperties(path,
					buildNumber, source, params);
			return new DeployableFileArtifact(root.getFile(), file, properties);
		}).collect(Collectors.toCollection(ArrayList::new));
	}

	private Map<String, String> getDeployableArtifactProperties(String path,
			String buildNumber, Source source, Params params) {
		Map<String, String> properties = new LinkedHashMap<>();
		addArtifactSetProperties(path, params, properties);
		addBuildProperties(buildNumber, source, properties);
		return properties;
	}

	private void addArtifactSetProperties(String path, Params params,
			Map<String, String> properties) {
		for (ArtifactSet artifactSet : params.getArtifactSet()) {
			if (getFilter(artifactSet).isMatch(path)) {
				properties.putAll(artifactSet.getProperties());
			}
		}
	}

	private PathFilter getFilter(ArtifactSet artifactSet) {
		return new PathFilter(artifactSet.getInclude(), artifactSet.getExclude());
	}

	private void addBuildProperties(String buildNumber, Source source,
			Map<String, String> properties) {
		properties.put("build.name", source.getBuildName());
		properties.put("build.number", buildNumber);
	}

	private void deployArtifacts(ArtifactoryServer artifactoryServer, Params params,
			List<DeployableArtifact> deployableArtifacts) {
		ArtifactoryRepository artifactoryRepository = artifactoryServer
				.repository(params.getRepo());
		artifactoryRepository.deploy(deployableArtifacts);
	}

	private void addBuildRun(ArtifactoryServer artifactoryServer, Source source,
			Params params, String buildNumber, List<DeployableArtifact> artifacts) {
		List<BuildModule> modules = this.moduleLayouts
				.getBuildModulesGenerator(params.getModuleLayout())
				.getBuildModules(artifacts);
		artifactoryServer.buildRuns(source.getBuildName()).add(buildNumber,
				params.getBuildUri(), modules);
	}

}
