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
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.spring.concourse.artifactoryresource.io.Directory;
import io.spring.concourse.artifactoryresource.io.DirectoryScanner;
import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.SnapshotVersion;
import org.apache.maven.artifact.repository.metadata.Versioning;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

/**
 * Generate Maven metadata files for downloaded artifacts.
 *
 * @author Phillip Webb
 * @author Madhura Bhave
 */
@Component
public class MavenMetadataGenerator {

	private static final List<String> POM_PATTERN = Collections
			.unmodifiableList(Collections.singletonList("**/*.pom"));

	private static final Set<String> IGNORED_EXTENSIONS = Collections
			.unmodifiableSet(new LinkedHashSet<>(Arrays.asList("asc", "sha", "md5")));

	private final DirectoryScanner scanner;

	public MavenMetadataGenerator(DirectoryScanner scanner) {
		this.scanner = scanner;
	}

	public void generate(Directory root) {
		List<File> pomFiles = this.scanner.scan(root, POM_PATTERN);
		pomFiles.forEach((pomFile) -> generate(root, pomFile));
	}

	private void generate(Directory root, File pomFile) {
		String name = StringUtils.getFilename(pomFile.getName());
		String extension = StringUtils.getFilenameExtension(pomFile.getName());
		String prefix = name.substring(0, name.length() - extension.length() - 1);
		File[] files = pomFile.getParentFile().listFiles((f) -> include(f, prefix));
		MultiValueMap<File, Coordinates> coordinates = new LinkedMultiValueMap<>();
		for (File file : files) {
			coordinates.add(file.getParentFile(), new Coordinates(root, file, prefix));
		}
		coordinates.forEach(this::writeMetadata);
	}

	private boolean include(File file, String prefix) {
		String extension = StringUtils.getFilenameExtension(file.getName());
		if (IGNORED_EXTENSIONS.contains(extension.toLowerCase())) {
			return false;
		}
		return file.exists() && file.isFile()
				&& StringUtils.getFilename(file.getName()).startsWith(prefix);
	}

	private void writeMetadata(File folder, List<Coordinates> coordinatesList) {
		Metadata metadata = new Metadata();
		Versioning versioning = new Versioning();
		for (Coordinates coordinates : coordinatesList) {
			SnapshotVersion snapshotVersion = new SnapshotVersion();
			versioning.addSnapshotVersion(snapshotVersion);
		}
		metadata.setVersioning(versioning);
	}

	// FIXME

	/*
	 * org/springframework/boot/spring-boot/1.5.5.BUILD-SNAPSHOT/
	 *
	 * maven-metadata.xml
	 *
	 *
	 * <metadata
	 * modelVersion="1.1.0"><groupId>org.springframework.boot</groupId><artifactId>
	 * spring-boot</artifactId><version>1.5.5.BUILD-SNAPSHOT</version><versioning><
	 * snapshot><timestamp>20170629.183538</timestamp><buildNumber>27</buildNumber></
	 * snapshot><lastUpdated>20170629192731</lastUpdated><snapshotVersions><
	 * snapshotVersion><classifier>javadoc</classifier><extension>jar</extension><
	 * value>1.5.5.BUILD-20170629.183538-27</value><updated>20170629183538</updated></
	 * snapshotVersion><snapshotVersion><classifier>sources</classifier><extension>jar
	 * </extension><value>1.5.5.BUILD-20170629.183538-27</value><updated>
	 * 20170629183538</updated></snapshotVersion><snapshotVersion><extension>jar</
	 * extension><value>1.5.5.BUILD-20170629.183538-27</value><updated>20170629183538<
	 * /updated></snapshotVersion><snapshotVersion><extension>pom</extension><value>1.
	 * 5.5.BUILD-20170629.183538-27</value><updated>20170629183538</updated></
	 * snapshotVersion></snapshotVersions></versioning></metadata>
	 *
	 *
	 */
	//
	// Metadata metadata = new Metadata();
	// metadata.setGroupId("");
	// metadata.setArtifactId("");
	// metadata.setVersion("");
	// Versioning versioning = new Versioning();
	// SnapshotVersion snapshotVersion = new SnapshotVersion();
	// snapshotVersion.setClassifier("");
	// snapshotVersion.setExtension("");
	// snapshotVersion.setVersion("");
	// versioning.addSnapshotVersion(snapshotVersion);
	// metadata.setVersioning(versioning);
	//

	private static class Coordinates {

		private static final Pattern FOLDER_PATTERN = Pattern
				.compile("(.*)\\/(.*)\\/(.*)\\/(.*)");

		private final String groupId;

		private final String artifactId;

		private final String version;

		private final String classifier;

		private final String extension;

		private final String snapshotVersion;

		public Coordinates(Directory root, File file, String prefix) {
			String rootPath = StringUtils.cleanPath(root.getFile().getPath());
			String relativePath = StringUtils.cleanPath(file.getPath())
					.substring(rootPath.length() + 1);
			Matcher matcher = FOLDER_PATTERN.matcher(relativePath);
			Assert.state(matcher.matches(), "Unable to parse " + relativePath);
			this.groupId = matcher.group(1).replace('/', '.');
			this.artifactId = matcher.group(2);
			this.version = matcher.group(3);
			String name = matcher.group(4);
			String snapshotVersionAndClassifier = name.substring(prefix.length());
			this.extension = StringUtils
					.getFilenameExtension(snapshotVersionAndClassifier);
			snapshotVersionAndClassifier = snapshotVersionAndClassifier.substring(0,
					snapshotVersionAndClassifier.length() - this.extension.length() - 1);
			this.classifier = (snapshotVersionAndClassifier.length() > 1
					? snapshotVersionAndClassifier.substring(1) : "");
			this.snapshotVersion = prefix.substring(this.artifactId.length() + 1);
		}

		@Override
		public String toString() {
			return this.groupId + ":" + this.artifactId + ":" + this.version + ":"
					+ this.classifier + ":" + this.version + ":" + this.snapshotVersion;
		}

	}

}
