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

import io.spring.concourse.artifactoryresource.payload.OutRequest.Params;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link OutRequest}.
 */
@RunWith(SpringRunner.class)
@JsonTest
public class OutRequestTests {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private Source source = new Source("http://localhost:8181", "username", "password",
			"libs-snapshot-local", "my-build");

	private Params params = new Params("1234", null, null);

	@Autowired
	private JacksonTester<OutRequest> json;

	@Test
	public void createWhenSourceIsNullShouldThrowException() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("Source must not be null");
		new OutRequest(null, this.params);
	}

	@Test
	public void createWhenParamsIsNullShouldThrowException() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("Params must not be null");
		new OutRequest(this.source, null);
	}

	@Test
	public void createParamsWhenBuildNumberIsEmptyShouldThrowException()
			throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("Build Number must not be empty");
		new Params("", null, null);
	}

	@Test
	public void readShouldDeserialize() throws Exception {
		OutRequest request = this.json.readObject("out-request.json");
		assertThat(request.getSource().getUri()).isEqualTo("http://repo.example.com");
		assertThat(request.getSource().getUsername()).isEqualTo("admin");
		assertThat(request.getSource().getPassword()).isEqualTo("password");
		assertThat(request.getSource().getRepo()).isEqualTo("libs-snapshot-local");
		assertThat(request.getParams().getBuildNumber()).isEqualTo("1234");
		assertThat(request.getParams().getExclude()).containsExactly("foo", "bar");
		assertThat(request.getParams().getBuildUrl()).isEqualTo("http://ci.example.com");
	}

}