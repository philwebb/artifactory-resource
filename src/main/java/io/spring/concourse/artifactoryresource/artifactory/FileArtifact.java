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

package io.spring.concourse.artifactoryresource.artifactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.springframework.util.Assert;

/**
 * {@link Artifact} backed by a {@link File}.
 */
public class FileArtifact extends AbstractArtifact {

	private File file;

	public FileArtifact(File parent, File file) {
		this(parent, file, null);
	}

	public FileArtifact(File parent, File file, Map<String, String> properties) {
		this(parent, file, properties, null);
	}

	public FileArtifact(File parent, File file, Map<String, String> properties,
			Checksums checksums) {
		super(calculatePath(parent, file), properties, checksums);
		Assert.isTrue(file.exists(), "File '" + file + "' does not exist");
		Assert.isTrue(file.isFile(), "Path '" + file + "' does not refer to a file");
		this.file = file;
	}

	@Override
	public InputStream getContent() throws IOException {
		return new FileInputStream(this.file);
	}

	private static String calculatePath(File parent, File file) {
		String parentPath = parent.getAbsolutePath();
		String filePath = file.getAbsolutePath();
		Assert.isTrue(filePath.startsWith(parentPath),
				"File '" + parent + "' is not a parent of '" + file + "'");
		return filePath.substring(parentPath.length());
	}

}
