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
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Abstract {@link DeployableArtifact} implementation.
 *
 * @author Phillip Webb
 * @author Madhura Bhave
 */
public abstract class AbstractDeployableArtifact implements DeployableArtifact {

	private final String path;

	private final Map<String, String> properties;

	private Checksums checksums;

	public AbstractDeployableArtifact(String path, Map<String, String> properties,
			Checksums checksums) {
		Assert.hasText(path, "Path must not be empty");
		this.path = cleanPath(path);
		this.properties = (properties == null ? Collections.emptyMap()
				: Collections.unmodifiableMap(new LinkedHashMap<>(properties)));
		this.checksums = checksums;
	}

	private String cleanPath(String path) {
		path = StringUtils.cleanPath(path);
		path = (path.startsWith("/") ? path : "/" + path);
		return path;
	}

	@Override
	public String getPath() {
		return this.path;
	}

	@Override
	public Map<String, String> getProperties() {
		return this.properties;
	}

	@Override
	public Checksums getChecksums() {
		if (this.checksums == null) {
			this.checksums = Checksums.calculate(getContent());
		}
		return this.checksums;
	}

}
