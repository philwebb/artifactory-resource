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

package io.spring.concourse.artifactoryresource.maven;

import java.io.File;
import java.io.IOException;

import io.spring.concourse.artifactoryresource.io.Directory;
import io.spring.concourse.artifactoryresource.io.DirectoryScanner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.springframework.util.FileCopyUtils;

/**
 * Tests for {@link MavenMetadataGenerator}.
 *
 * @author Phillip Webb
 * @author Madhura Bhave
 */
public class MavenMetadataGeneratorTests {

	private static final byte[] NO_BYTES = {};

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private MavenMetadataGenerator generator = new MavenMetadataGenerator(
			new DirectoryScanner());

	@Test
	public void generateWhenUsingNonSnapshotShouldCreateMetadata() throws Exception {
		Directory directory = createStructure("1.0.0-RELEASE");
		this.generator.generate(directory);
	}

	@Test
	public void generateWhenUsingSnapshotShouldCreateMetadata() throws Exception {

	}

	@Test
	public void generateWhenUsingSnapshotTimestampShouldCreateMetadata()
			throws Exception {

	}

	private Directory createStructure(String version) throws IOException {
		return createStructure(version, version);
	}

	private Directory createStructure(String folderVersion, String fileVersion)
			throws IOException {
		Directory root = new Directory(this.temporaryFolder.newFolder());
		String prefix = "com/example/project/my-project/";
		add(root, prefix + folderVersion + "/my-project-" + fileVersion + ".pom");
		add(root, prefix + folderVersion + "/my-project-" + fileVersion + ".jar");
		add(root, prefix + folderVersion + "/my-project-" + fileVersion + ".asc");
		add(root, prefix + folderVersion + "/my-project-" + fileVersion + ".sha");
		add(root, prefix + folderVersion + "/my-project-" + fileVersion + ".md5");
		add(root, prefix + folderVersion + "/my-project-" + fileVersion + "-sources.jar");
		add(root, prefix + folderVersion + "/other" + fileVersion + ".jar");
		return root;
	}

	private void add(Directory root, String path) throws IOException {
		File file = new File(root.getFile(), path);
		file.getParentFile().mkdirs();
		FileCopyUtils.copy(NO_BYTES, file);
	}

}
