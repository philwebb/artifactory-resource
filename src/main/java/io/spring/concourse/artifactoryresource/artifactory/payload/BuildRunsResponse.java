package io.spring.concourse.artifactoryresource.artifactory.payload;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A single response from request for build runs.
 *
 * @author Madhura Bhave
 * @author Phillip Webb
 */
public class BuildRunsResponse {

	private String uri;

	private List<BuildRun> buildsRuns;

	@JsonCreator
	public BuildRunsResponse(@JsonProperty("uri") String uri,
			@JsonProperty("buildsNumbers") List<BuildRun> buildsRuns) {
		this.uri = uri;
		this.buildsRuns = (buildsRuns == null ? Collections.emptyList()
				: Collections.unmodifiableList(new ArrayList<>(buildsRuns)));
	}

	public String getUri() {
		return this.uri;
	}

	public List<BuildRun> getBuildsRuns() {
		return this.buildsRuns;
	}

}
