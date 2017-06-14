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
 * The source payload containing shared configuration.
 */
public class Source {

	private final String uri;

	private final String username;

	private final String password;

	private final String repo;

	@JsonCreator
	public Source(@JsonProperty("uri") String uri,
			@JsonProperty("username") String username,
			@JsonProperty("password") String password,
			@JsonProperty("repo") String repo) {
		Assert.hasText(uri, "URI must not be empty");
		Assert.hasText(username, "Username must not be empty");
		Assert.hasText(password, "Password must not be empty");
		Assert.hasText(repo, "Repo must not be empty");
		this.uri = uri;
		this.username = username;
		this.password = password;
		this.repo = repo;
	}

	public String getUri() {
		return this.uri;
	}

	public String getUsername() {
		return this.username;
	}

	public String getPassword() {
		return this.password;
	}

	public String getRepo() {
		return this.repo;
	}

}
