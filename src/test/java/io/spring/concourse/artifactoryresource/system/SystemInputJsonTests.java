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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SystemInputJson}.
 */
public class SystemInputJsonTests {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void readWhenNoDataShouldTimeout() throws Exception {
		SystemInputJson inputJson = new SystemInputJson(new MockSystemStreams(""),
				new ObjectMapper(), 10);
		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage("Timeout waiting for input");
		inputJson.read(String[].class);
	}

	@Test
	public void readShouldDeserialize() throws Exception {
		SystemInputJson inputJson = new SystemInputJson(
				new MockSystemStreams("[\"foo\",\"bar\"]"), new ObjectMapper());
		String[] result = inputJson.read(String[].class);
		assertThat(result).containsExactly("foo", "bar");
	}

}