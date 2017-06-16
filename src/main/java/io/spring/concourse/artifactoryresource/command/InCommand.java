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

import io.spring.concourse.artifactoryresource.command.payload.InRequest;
import io.spring.concourse.artifactoryresource.system.SystemInputJson;

import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

/**
 * Command to handle requests to the {@code "/opt/resource/in"} script.
 *
 * @author Phillip Webb
 */
@Component
public class InCommand implements Command {

	private final SystemInputJson inputJson;

	public InCommand(SystemInputJson inputJson) {
		this.inputJson = inputJson;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		InRequest request = this.inputJson.read(InRequest.class);
		Directory directory = Directory.fromArgs(args);
		System.out.println(directory);
		System.out.println(request);

	}

}
