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

import java.io.File;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.util.Assert;

/**
 * A directory passed to a command.
 *
 * @author Phillip Webb
 */
public class Directory {

	private final File file;

	public Directory(String pathname) {
		this.file = new File(pathname);
		Assert.state(this.file.exists(), "Path '" + pathname + "' does not exist");
		Assert.state(this.file.isDirectory(),
				"Path '" + pathname + "' is not a directory");
	}

	@Override
	public String toString() {
		return this.file.toString();
	}

	public static Directory fromArgs(ApplicationArguments args) {
		List<String> nonOptionArgs = args.getNonOptionArgs();
		Assert.state(nonOptionArgs.size() >= 2, "No directory argument specified");
		return new Directory(nonOptionArgs.get(1));
	}

}
