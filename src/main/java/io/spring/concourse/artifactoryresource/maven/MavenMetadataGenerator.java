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
import java.util.Collections;
import java.util.List;

import io.spring.concourse.artifactoryresource.io.Directory;
import io.spring.concourse.artifactoryresource.io.DirectoryScanner;

import org.springframework.stereotype.Component;

import org.springframework.stereotype.Component;

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

	private final DirectoryScanner scanner;

	public MavenMetadataGenerator(DirectoryScanner scanner) {
		this.scanner = scanner;
	}

	public void generate(Directory directory) {
		List<File> pomFiles = this.scanner.scan(directory, POM_PATTERN);

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
	}

}
