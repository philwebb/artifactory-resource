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

package io.spring.concourse.artifactoryresource.payload;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.util.Assert;

/**
 * The version payload detailing a single build version. Can be used as both input and
 * output.
 */
public class Version {

	@JsonProperty("build_id")
	private final String buildId;

	@JsonCreator
	public Version(@JsonProperty("build_id") String buildId) {
		Assert.hasText(buildId, "Build ID must not be empty");
		this.buildId = buildId;
	}

	public String getBuildId() {
		return this.buildId;
	}

}
