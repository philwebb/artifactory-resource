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

package io.spring.concourse.artifactoryresource.command.payload;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;

/**
 * Request to the {@code "/opt/resource/out"} script.
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

	public static class Params {

		private final String buildNumber;

		private final List<String> exclude;

		private final String buildUri;

		@JsonCreator
		public Params(@JsonProperty("build_number") String buildNumber,
				@JsonProperty("exclude") List<String> exclude,
				@JsonProperty("build_uri") String buildUri) {
			Assert.hasText(buildNumber, "Build Number must not be empty");
			this.buildNumber = buildNumber;
			this.exclude = (exclude == null ? Collections.emptyList()
					: Collections.unmodifiableList(new ArrayList<>(exclude)));
			this.buildUri = buildUri;
		}

		public String getBuildNumber() {
			return this.buildNumber;
		}

		public List<String> getExclude() {
			return this.exclude;
		}

		public String getBuildUrl() {
			return this.buildUri;
		}

		@Override
		public String toString() {
			return new ToStringCreator(this).append("buildNumber", this.buildNumber)
					.append("exclude", this.exclude).append("buildUri", this.buildUri)
					.toString();
		}

	}

}
