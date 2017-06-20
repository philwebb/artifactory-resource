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

import java.net.URI;
import java.util.Date;
import java.util.List;

import io.spring.concourse.artifactoryresource.artifactory.payload.BuildInfo;
import io.spring.concourse.artifactoryresource.artifactory.payload.BuildModule;
import io.spring.concourse.artifactoryresource.artifactory.payload.BuildRuns;
import io.spring.concourse.artifactoryresource.artifactory.payload.ContinuousIntegrationAgent;

import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Default {@link ArtifactoryBuildRuns} implementation communicating over HTTP.
 *
 * @author Phillip Webb
 */
public class HttpArtifactoryBuildRuns implements ArtifactoryBuildRuns {

	private static final Object[] NO_VARIABLES = {};

	private final RestTemplate restTemplate;

	private final UriComponentsBuilder uri;

	private final String buildName;

	public HttpArtifactoryBuildRuns(RestTemplate restTemplate, UriComponentsBuilder uri,
			String buildName) {
		this.restTemplate = restTemplate;
		this.uri = uri;
		this.buildName = buildName;
	}

	@Override
	public void add(String buildNumber, String buildUri,
			ContinuousIntegrationAgent continuousIntegrationAgent,
			List<BuildModule> modules) {
		add(new BuildInfo(this.buildName, buildNumber, continuousIntegrationAgent,
				new Date(), buildUri, modules));
	}

	@Override
	public BuildRuns getAll() {
		URI uri = this.uri.path("api/build/{buildName}").build(this.buildName);
		return this.restTemplate.getForObject(uri, BuildRuns.class);
	}

	private void add(BuildInfo buildInfo) {
		URI uri = this.uri.path("api/build").build(NO_VARIABLES);
		RequestEntity<BuildInfo> request = RequestEntity.put(uri)
				.contentType(MediaType.APPLICATION_JSON).body(buildInfo);
		ResponseEntity<Void> exchange = this.restTemplate.exchange(request, Void.class);
		exchange.getBody();
	}

}
