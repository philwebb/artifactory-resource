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

import java.io.PrintStream;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.concourse.artifactoryresource.artifactory.ArtifactoryBuildRuns;
import io.spring.concourse.artifactoryresource.artifactory.ArtifactoryServer;
import io.spring.concourse.artifactoryresource.artifactory.HttpArtifactory;
import io.spring.concourse.artifactoryresource.artifactory.payload.BuildRuns.BuildNumber;
import io.spring.concourse.artifactoryresource.command.payload.CheckRequest;
import io.spring.concourse.artifactoryresource.command.payload.CheckResponse;
import io.spring.concourse.artifactoryresource.command.payload.Source;
import io.spring.concourse.artifactoryresource.command.payload.Version;
import io.spring.concourse.artifactoryresource.system.SystemInputJson;

import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

/**
 * Command to handle requests to the {@code "/opt/resource/check"} script.
 *
 * @author Phillip Webb
 */
@Component
public class CheckCommand implements Command {

	private final SystemInputJson inputJson;

	private final HttpArtifactory artifactory;

	private final ObjectMapper mapper;

	public CheckCommand(SystemInputJson inputJson, HttpArtifactory artifactory) {
		this.inputJson = inputJson;
		this.artifactory = artifactory;
		this.mapper = new ObjectMapper();
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		CheckRequest request = this.inputJson.read(CheckRequest.class);
		Source source = request.getSource();
		ArtifactoryServer server = this.artifactory.server(source.getUri(), source.getUsername(),
				source.getPassword());
		ArtifactoryBuildRuns buildRuns = server.buildRuns(source.getBuildName());
		List<BuildNumber> buildsNumbers = buildRuns.getAll().getBuildsNumbers();
		PrintStream out = this.inputJson.getSystemStreams().out();
		CheckResponse response;
		response = getCheckResponse(request, buildsNumbers);
		out.println(this.mapper.writeValueAsString(response));
	}

	private CheckResponse getCheckResponse(CheckRequest request, List<BuildNumber> buildsNumbers) {
		CheckResponse response;
		if (request.getVersion() != null) {
			BuildNumber buildNumber = getBuildNumberForVersion(buildsNumbers, request.getVersion());
			List<BuildNumber> latestBuilds = stream(buildsNumbers).filter(n -> n.compareDate(buildNumber) >= 0)
					.collect(Collectors.toList());
			List<Version> versions = latestBuilds.stream().map(n -> new Version(extractBuildNumber(n))).collect(Collectors.toList());
			response = new CheckResponse(versions);
		} else {
			BuildNumber latestBuild = getLatestBuild(buildsNumbers);
			Version version = new Version(extractBuildNumber(latestBuild));
			response = new CheckResponse(Collections.singletonList(version));
		}
		return response;
	}

	private BuildNumber getLatestBuild(List<BuildNumber> buildsNumbers) {
		return stream(buildsNumbers).max(BuildNumber::compareDate).orElse(null);
	}

	private BuildNumber getBuildNumberForVersion(List<BuildNumber> buildsNumbers, Version version) {
		return stream(buildsNumbers).filter(n -> extractBuildNumber(n).equals(version.getBuildNumber())).findFirst().orElse(null);
	}

	private String extractBuildNumber(BuildNumber buildNumber) {
		return buildNumber.getUri().substring(1);
	}

	private Stream<BuildNumber> stream(List<BuildNumber> buildsNumbers) {
		return buildsNumbers.stream();
	}

}
