package io.spring.concourse.artifactoryresource.artifactory.payload;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Madhura Bhave
 */
public class FetchResults {

	private List<FetchedArtifact> results;

	private Range range;

	@JsonCreator
	public FetchResults(@JsonProperty("results") List<FetchedArtifact> results, @JsonProperty("range") Range range) {
		this.results = results;
		this.range = range;
	}

	public List<FetchedArtifact> getResults() {
		return this.results;
	}

	public Range getRange() {
		return this.range;
	}

	public static class Range {

		@JsonProperty("start_pos")
		private int startPos;

		@JsonProperty("end_pos")
		private int endPos;

		private int total;

		@JsonCreator
		public Range(@JsonProperty("start_pos") int startPos, @JsonProperty("end_pos")int endPos, @JsonProperty("total")int total) {
			this.startPos = startPos;
			this.endPos = endPos;
			this.total = total;
		}

		public int getStartPos() {
			return this.startPos;
		}

		public int getEndPos() {
			return this.endPos;
		}

		public int getTotal() {
			return this.total;
		}
	}
}
