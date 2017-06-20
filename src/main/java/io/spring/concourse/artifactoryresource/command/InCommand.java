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

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.concourse.artifactoryresource.artifactory.ArtifactoryRepository;
import io.spring.concourse.artifactoryresource.artifactory.ArtifactoryServer;
import io.spring.concourse.artifactoryresource.artifactory.HttpArtifactory;
import io.spring.concourse.artifactoryresource.artifactory.payload.FetchedArtifact;
import io.spring.concourse.artifactoryresource.artifactory.payload.FetchResults;
import io.spring.concourse.artifactoryresource.command.payload.InRequest;
import io.spring.concourse.artifactoryresource.command.payload.InResponse;
import io.spring.concourse.artifactoryresource.command.payload.Source;
import io.spring.concourse.artifactoryresource.command.payload.Version;
import io.spring.concourse.artifactoryresource.system.SystemInputJson;

import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

/**
 * Command to handle requests to the {@code "/opt/resource/in"} script.
 *
 * @author Phillip Webb
 */
@Component
public class InCommand implements Command {

	private final SystemInputJson inputJson;

	private final HttpArtifactory artifactory;

	public InCommand(SystemInputJson inputJson, HttpArtifactory artifactory) {
		this.inputJson = inputJson;
		this.artifactory = artifactory;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		InRequest request = this.inputJson.read(InRequest.class);
		Directory directory = Directory.fromArgs(args);
		Source source = request.getSource();
		ArtifactoryServer server = this.artifactory.server(source.getUri(), source.getUsername(),
				source.getPassword());
		ArtifactoryRepository repository = server.repository(source.getRepo());
		fetchArtifacts(request, directory, source, repository);
		Version version = new Version(request.getVersion().getBuildNumber());
		InResponse response = new InResponse(version, null); //FIXME for metadata
		String output = new ObjectMapper().writeValueAsString(response);
		this.inputJson.getSystemStreams().out().print(output);
	}

	private void fetchArtifacts(InRequest request, Directory directory, Source source, ArtifactoryRepository repository) {
		FetchResults fetchResults = repository.fetchAll(source.getBuildName(), request.getVersion().getBuildNumber());
		List<FetchedArtifact> artifacts = fetchResults.getResults();
		for (FetchedArtifact artifact: artifacts) {
			String path = "/" + artifact.getPath() + "/" + artifact.getName();
			repository.fetch(path, directory.toString());
		}
	}

}
