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

package io.spring.concourse.artifactoryresource.artifactory.payload;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.spring.concourse.artifactoryresource.util.ArtifactoryDateFormat;
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
 * Tests for {@link BuildInfo}.
 *
 * @author Phillip Webb
 * @author Madhura Bhave
 */
@RunWith(SpringRunner.class)
@JsonTest
public class BuildInfoTests {

	private static final String BUILD_NAME = "my-build";

	private static final String BUILD_NUMBER = "5678";

	private static final ContinuousIntegrationAgent CI_AGENT = new ContinuousIntegrationAgent(
			"Concourse", "3.0.0");

	private static final Date STARTED = ArtifactoryDateFormat
			.parse("2014-09-30T12:00:19.893+0000");

	private static final String BUILD_URI = "http://ci.example.com";

	private static final BuildArtifact ARTIFACT = new BuildArtifact("jar",
			"a9993e364706816aba3e25717850c26c9cd0d89d",
			"900150983cd24fb0d6963f7d28e17f72", "foo.jar");

	private static final List<BuildModule> MODULES = Collections
			.singletonList(new BuildModule("com.example.module:my-module:1.0.0-SNAPSHOT",
					Collections.singletonList(ARTIFACT)));

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Autowired
	private JacksonTester<BuildInfo> json;

	@Test
	public void createWhenBuildNameIsEmptyShouldThrowException() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("BuildName must not be empty");
		new BuildInfo("", BUILD_NUMBER, CI_AGENT, STARTED, BUILD_URI, MODULES);
	}

	@Test
	public void createWhenBuildNumberIsEmptyShouldThrowException() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("BuildNumber must not be empty");
		new BuildInfo(BUILD_NAME, "", CI_AGENT, STARTED, BUILD_URI, MODULES);
	}

	@Test
	public void createWhenModulesIsNullShouldUseEmptyList() throws Exception {
		BuildInfo buildInfo = new BuildInfo(BUILD_NAME, BUILD_NUMBER, CI_AGENT, STARTED,
				BUILD_URI, null);
		assertThat(buildInfo.getModules()).isNotNull().isEmpty();
	}

	@Test
	public void writeShouldSerialize() throws Exception {
		BuildInfo buildInfo = new BuildInfo(BUILD_NAME, BUILD_NUMBER, CI_AGENT, STARTED,
				BUILD_URI, MODULES);
		assertThat(this.json.write(buildInfo)).isEqualToJson("build-info.json");
	}

}
