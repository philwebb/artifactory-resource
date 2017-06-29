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

package io.spring.concourse.artifactoryresource.system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Read input from {@link SystemStreams#in()} (dealing with timeouts).
 *
 * @author Phillip Webb
 * @author Madhura Bhave
 */
@Component
public class SystemInput {

	private static final long TIMEOUT = TimeUnit.SECONDS.toMillis(1);

	private final SystemStreams systemStreams;

	private final ObjectMapper objectMapper;

	private final Environment environment;

	private final long timeout;

	@Autowired
	public SystemInput(SystemStreams systemStreams, ObjectMapper objectMapper, Environment environment) {
		this(systemStreams, objectMapper, TIMEOUT, environment);
	}

	protected SystemInput(SystemStreams systemStreams, ObjectMapper objectMapper,
			long timeout, Environment environment) {
		this.systemStreams = systemStreams;
		this.objectMapper = objectMapper;
		this.timeout = timeout;
		this.environment = environment;
	}

	public <T> T read(Class<T> type) throws IOException {
		BufferedReader in = new BufferedReader(
				new InputStreamReader(this.systemStreams.in()));
		long startTime = System.currentTimeMillis();
		while (!in.ready()) {
			Assert.state(System.currentTimeMillis() - startTime < this.timeout,
					"Timeout waiting for input");
		}
		String resolved = resolvePlaceholders(in);
		return this.objectMapper.readValue(resolved, type);
	}

	private String resolvePlaceholders(BufferedReader in) throws IOException {
		StringBuilder inputString = new StringBuilder();
		String s;
		while ((s = in.readLine()) != null) {
			inputString.append(s);
		}
		return this.environment.resolvePlaceholders(inputString.toString());
	}

}
