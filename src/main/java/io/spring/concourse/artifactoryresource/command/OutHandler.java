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

package io.spring.concourse.artifactoryresource.command;

import io.spring.concourse.artifactoryresource.command.payload.OutRequest;
import io.spring.concourse.artifactoryresource.command.payload.OutResponse;

import org.springframework.stereotype.Component;

/**
 * Delegate used to handle operations triggered from the {@link OutCommand}.
 *
 * @author Phillip Webb
 * @author Madhura Bhave
 */
@Component
public class OutHandler {

	public OutResponse handle(OutRequest request, Directory directory) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Auto-generated method stub");
	}

	// @Override
	// public void run(ApplicationArguments args) throws Exception {
	// OutRequest request = this.inputJson.read(OutRequest.class);
	// Source source = request.getSource();
	// Params params = request.getParams();
	// ArtifactoryServer server = this.artifactory.server(source.getUri(),
	// source.getUsername(), source.getPassword());
	// ArtifactoryRepository repository = server.repository(source.getRepo());
	// List<Resource> resourcesToDeploy = getResourcesToDeploy(request);
	// MultiValueMap<String, BuildArtifact> buildArtifacts = deployAndGetBuildArtifacts(
	// source, params, repository, resourcesToDeploy);
	// addBuildInfo(source, params, server, buildArtifacts);
	// System.out.println(request);
	// }
	//
	// private List<Resource> getResourcesToDeploy(OutRequest request) throws IOException
	// {
	// Params params = request.getParams();
	// List<String> excludes = params.getExclude();
	// List<String> includes = params.getInclude();
	// String folder = params.getFolder();
	// List<Resource> includeResources = new ArrayList<>();
	// for (String include : includes) {
	// getResources(folder, includeResources, include);
	// }
	// List<Resource> excludeResources = new ArrayList<>();
	// for (String exclude : excludes) {
	// getResources(folder, excludeResources, exclude);
	// }
	// includeResources.removeAll(excludeResources);
	// return includeResources;
	// }
	//
	// private void getResources(String folder, List<Resource> includeResources,
	// String include) throws IOException {
	// ResourceLoader resourceLoader = getResourceLoader();
	// PathMatchingResourcePatternResolver patternResolver = new
	// PathMatchingResourcePatternResolver(
	// resourceLoader);
	// String locationPattern = folder + "/" + include;
	// includeResources
	// .addAll(Arrays.asList(patternResolver.getResources(locationPattern)));
	// }
	//
	// private ResourceLoader getResourceLoader() {
	// return new FileSystemResourceLoader() {
	// @Override
	// protected Resource getResourceByPath(String path) {
	// return new FileSystemResource(path);
	// }
	// };
	// }
	//
	// private MultiValueMap<String, BuildArtifact> deployAndGetBuildArtifacts(Source
	// source,
	// Params params, ArtifactoryRepository repository,
	// List<Resource> resourcesToDeploy) throws IOException {
	// MultiValueMap<String, BuildArtifact> artifacts = new LinkedMultiValueMap<>();
	// Map<String, String> properties = getProperties(source, params);
	// for (Resource resource : resourcesToDeploy) {
	// File file = resource.getFile();
	// if (file.exists() && !file.isDirectory()) {
	// Checksums checksum = Checksums.calculate(resource);
	// DeployableArtifact deployableArtifact = new DeployableFileArtifact(
	// file.getParentFile(), file, properties, checksum);
	// BuildArtifact buildArtifact = new BuildArtifact("file",
	// checksum.getSha1(), checksum.getMd5(), resource.getFilename());
	// artifacts.add(file.getParentFile().getName(), buildArtifact);
	// repository.deploy(deployableArtifact);
	// }
	// }
	// return artifacts;
	// }
	//
	// private Map<String, String> getProperties(Source source, Params params) {
	// Map<String, String> properties = new LinkedHashMap<>();
	// properties.put("build.name", source.getBuildName());
	// properties.put("build.number", params.getBuildNumber());
	// return properties;
	// }
	//
	// private void addBuildInfo(Source source, Params params, ArtifactoryServer server,
	// MultiValueMap<String, BuildArtifact> artifacts) {
	// List<BuildModule> buildModules = new ArrayList<>();
	// for (String id : artifacts.keySet()) {
	// BuildModule module = new BuildModule(id, artifacts.get(id));
	// buildModules.add(module);
	// }
	// ContinuousIntegrationAgent agent = new ContinuousIntegrationAgent("Concourse",
	// null);
	// server.buildRuns(source.getBuildName()).add(params.getBuildNumber(),
	// params.getBuildUri(), agent, buildModules);
	// }

}
