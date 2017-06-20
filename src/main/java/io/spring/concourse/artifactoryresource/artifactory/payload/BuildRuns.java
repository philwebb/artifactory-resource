package io.spring.concourse.artifactoryresource.artifactory.payload;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Madhura Bhave
 */
public class BuildRuns {

	private String uri;

	private List<BuildNumber> buildsNumbers;

	@JsonCreator
	public BuildRuns(@JsonProperty("uri") String uri,
			@JsonProperty("buildsNumbers") List<BuildNumber> buildsNumbers) {
		this.uri = uri;
		this.buildsNumbers = (buildsNumbers == null ? Collections.emptyList()
				: Collections.unmodifiableList(new ArrayList<>(buildsNumbers)));
	}

	public String getUri() {
		return uri;
	}

	public List<BuildNumber> getBuildsNumbers() {
		return this.buildsNumbers;
	}

	public static class BuildNumber {

		private String uri;

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
		private Date started;

		@JsonCreator
		public BuildNumber(@JsonProperty("uri") String uri, @JsonProperty("started") Date started) {
			this.uri = uri;
			this.started = started;
		}

		public String getUri() {
			return this.uri;
		}

		public Date getStarted() {
			return this.started;
		}

		public int compareDate(BuildNumber anotherBuildNumber) {
			Date started1 = this.getStarted();
			Date started2 = anotherBuildNumber.getStarted();
			return (started1.before(started2) ? -1 : (started1.equals(started2) ? 0 : 1));
		}
	}
}
