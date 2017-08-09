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

package io.spring.concourse.artifactoryresource.command.payload;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;

/**
 * Request to the {@code "/opt/resource/out"} script.
 *
 * @author Phillip Webb
 * @author Madhura Bhave
 */
public class OutRequest {

	private final Source source;

	private final Params params;

	@JsonCreator
	public OutRequest(@JsonProperty("source") Source source,
			@JsonProperty("params") Params params) {
		Assert.notNull(source, "Source must not be null");
		Assert.notNull(params, "Params must not be null");
		this.source = source;
		this.params = params;
	}

	public Source getSource() {
		return this.source;
	}

	public Params getParams() {
		return this.params;
	}

	@Override
	public String toString() {
		return new ToStringCreator(this).append("source", this.source)
				.append("params", this.params).toString();
	}

	/**
	 * Parameters for the {@link OutRequest}.
	 */
	public static class Params {

		@JsonProperty("build_number")
		private final String buildNumber;

		private final String repo;

		private final String folder;

		private final List<String> include;

		private final List<String> exclude;

		private final String moduleLayout;

		@JsonProperty("build_uri")
		private final String buildUri;

		private final List<ArtifactSet> artifactSet;

		@JsonCreator
		public Params(@JsonProperty("build_number") String buildNumber,
				@JsonProperty("repo") String repo, @JsonProperty("folder") String folder,
				@JsonProperty("include") List<String> include,
				@JsonProperty("exclude") List<String> exclude,
				@JsonProperty("module_layout") String moduleLayout,
				@JsonProperty("build_uri") String buildUri,
				@JsonProperty("artifact_set") List<ArtifactSet> artifactSet) {
			Assert.hasText(repo, "Repo must not be empty");
			Assert.hasText(folder, "Folder must not be empty");
			this.buildNumber = buildNumber;
			this.repo = repo;
			this.folder = folder;
			this.include = (include == null ? Collections.emptyList()
					: Collections.unmodifiableList(new ArrayList<>(include)));
			this.exclude = (exclude == null ? Collections.emptyList()
					: Collections.unmodifiableList(new ArrayList<>(exclude)));
			this.moduleLayout = moduleLayout;
			this.buildUri = buildUri;
			this.artifactSet = (artifactSet == null ? Collections.emptyList()
					: Collections.unmodifiableList(new ArrayList<>(artifactSet)));
		}

		public String getBuildNumber() {
			return this.buildNumber;
		}

		public String getRepo() {
			return this.repo;
		}

		public String getFolder() {
			return this.folder;
		}

		public List<String> getInclude() {
			return this.include;
		}

		public List<String> getExclude() {
			return this.exclude;
		}

		public String getModuleLayout() {
			return this.moduleLayout;
		}

		public String getBuildUri() {
			return this.buildUri;
		}

		public List<ArtifactSet> getArtifactSet() {
			return this.artifactSet;
		}

		@Override
		public String toString() {
			return new ToStringCreator(this).append("buildNumber", this.buildNumber)
					.append("folder", this.folder).append("include", this.include)
					.append("exclude", this.exclude).append("buildUri", this.buildUri)
					.append("artifactSet", this.artifactSet).toString();
		}

	}

	/**
	 * An artifact set for additional configuration.
	 */
	public static class ArtifactSet {

		private final List<String> include;

		private final List<String> exclude;

		private final Map<String, String> properties;

		@JsonCreator
		public ArtifactSet(@JsonProperty("include") List<String> include,
				@JsonProperty("exclude") List<String> exclude,
				@JsonProperty("properties") Map<String, String> properties) {
			this.include = (include == null ? Collections.emptyList()
					: Collections.unmodifiableList(new ArrayList<>(include)));
			this.exclude = (exclude == null ? Collections.emptyList()
					: Collections.unmodifiableList(new ArrayList<>(exclude)));
			this.properties = properties;
		}

		public List<String> getInclude() {
			return this.include;
		}

		public List<String> getExclude() {
			return this.exclude;
		}

		public Map<String, String> getProperties() {
			return this.properties;
		}

		@Override
		public String toString() {
			return new ToStringCreator(this).append("include", this.include)
					.append("exclude", this.exclude).append("properties", this.properties)
					.toString();
		}

	}

}
