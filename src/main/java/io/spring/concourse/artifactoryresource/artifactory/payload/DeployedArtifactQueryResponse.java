package io.spring.concourse.artifactoryresource.artifactory.payload;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A single response from an Artifactory Query Language request for deployed artifacts.
 *
 * @author Madhura Bhave
 * @author Phillip Webb
 */
public class DeployedArtifactQueryResponse {

	private List<DeployedArtifact> results;

	private Range range;

	@JsonCreator
	public DeployedArtifactQueryResponse(
			@JsonProperty("results") List<DeployedArtifact> results,
			@JsonProperty("range") Range range) {
		this.results = results;
		this.range = range;
	}

	public List<DeployedArtifact> getResults() {
		return this.results;
	}

	public Range getRange() {
		return this.range;
	}

	public static class Range {

		private int startPos;

		private int endPos;

		private int total;

		@JsonCreator
		public Range(@JsonProperty("start_pos") int startPos,
				@JsonProperty("end_pos") int endPos, @JsonProperty("total") int total) {
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
