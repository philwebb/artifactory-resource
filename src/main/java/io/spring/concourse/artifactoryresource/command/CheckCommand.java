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

package io.spring.concourse.artifactoryresource.command;

import io.spring.concourse.artifactoryresource.command.payload.CheckRequest;
import io.spring.concourse.artifactoryresource.command.payload.CheckResponse;
import io.spring.concourse.artifactoryresource.system.SystemInput;
import io.spring.concourse.artifactoryresource.system.SystemOutput;

import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

/**
 * Command to handle requests to the {@code "/opt/resource/check"} script.
 *
 * @author Phillip Webb
 * @author Madhura Bhave
 */
@Component
public class CheckCommand implements Command {

	private final SystemInput input;

	private final SystemOutput output;

	private final CheckCommandHandler hander;

	public CheckCommand(SystemInput systemInput, SystemOutput systemOutput,
			CheckCommandHandler handler) {
		this.input = systemInput;
		this.output = systemOutput;
		this.hander = handler;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		CheckRequest request = this.input.read(CheckRequest.class);
		CheckResponse response = this.hander.handle(request);
		this.output.write(response);
	}

}
