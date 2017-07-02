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
 * Tests for {@link Source}.
 *
 * @author Phillip Webb
 * @author Madhura Bhave
 */
@RunWith(SpringRunner.class)
@JsonTest
public class SourceTests {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Autowired
	private JacksonTester<Source> json;

	@Test
	public void createWhenUriIsEmptyShouldThrowException() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("URI must not be empty");
		new Source("", "username", "password", "my-build");
	}

	@Test
	public void createWhenUsernameIsEmptyShouldThrowException() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("Username must not be empty");
		new Source("http://repo.example.com", "", "password", "my-build");
	}

	@Test
	public void createWhenPasswordIsEmptyShouldThrowException() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("Password must not be empty");
		new Source("http://repo.example.com", "username", "", "my-build");
	}

	@Test
	public void createWhenBuildNameIsEmptyShouldThrowException() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("Build Name must not be empty");
		new Source("http://repo.example.com", "username", "password", "");
	}

	@Test
	public void readShouldDeserialize() throws Exception {
		Source source = this.json.readObject("source.json");
		assertThat(source.getUri()).isEqualTo("http://repo.example.com");
		assertThat(source.getUsername()).isEqualTo("admin");
		assertThat(source.getPassword()).isEqualTo("password");
		assertThat(source.getBuildName()).isEqualTo("my-build");
	}

}
