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

package io.spring.concourse.artifactoryresource;

import com.palantir.docker.compose.DockerComposeRule;
import com.palantir.docker.compose.connection.DockerPort;
import com.palantir.docker.compose.connection.waiting.HealthChecks;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

public class ApplicationIT {

	@ClassRule
	public static DockerComposeRule docker = DockerComposeRule.builder()
			.file("src/integration/resources/docker-compose.yml")
			.waitingForService("artifactory",
					HealthChecks.toRespondOverHttp(8081, ApplicationIT::rootUrl))
			.build();

	private static DockerPort port;

	@BeforeClass
	public static void initialize() {
		port = docker.containers().container("artifactory").port(8081);
	}

	@Test
	public void testName() throws Exception {
		System.out.println(rootUrl(port));
	}

	private static String rootUrl(DockerPort port) {
		return port.inFormat("http://$HOST:$EXTERNAL_PORT");
	}
}
