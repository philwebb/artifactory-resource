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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.springframework.mock.env.MockEnvironment;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SystemInput}.
 *
 * @author Phillip Webb
 * @author Madhura Bhave
 */
public class SystemInputTests {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private MockEnvironment environment;

	@Before
	public void setUp() throws Exception {
		this.environment = new MockEnvironment();
	}

	@Test
	public void readWhenNoDataShouldTimeout() throws Exception {
		SystemInput input = new SystemInput(this.environment, new MockSystemStreams(""),
				new ObjectMapper(), 10);
		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage("Timeout waiting for input");
		input.read(String[].class);
	}

	@Test
	public void readShouldDeserialize() throws Exception {
		SystemInput input = new SystemInput(this.environment,
				new MockSystemStreams("[\"foo\",\"bar\"]"), new ObjectMapper());
		String[] result = input.read(String[].class);
		assertThat(result).containsExactly("foo", "bar");
	}

	@Test
	public void readShouldResolvePlaceholders() throws Exception {
		this.environment.setProperty("bar", "hello-world");
		SystemInput input = new SystemInput(this.environment,
				new MockSystemStreams("[\"foo\",\"${bar}\"]"), new ObjectMapper());
		String[] result = input.read(String[].class);
		assertThat(result).containsExactly("foo", "hello-world");
	}
}