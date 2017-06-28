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

import java.util.List;

import io.spring.concourse.artifactoryresource.artifactory.payload.DeployableArtifact;
import io.spring.concourse.artifactoryresource.artifactory.payload.DeployedArtifact;

/**
 * Access to an artifactory repository.
 *
 * @author Phillip Webb
 * @author Madhura Bhave
 */
public interface ArtifactoryRepository {

	/**
	 * Deploy the specified artifacts to the repository.
	 * @param artifacts the artifacts to deploy
	 */
	default void deploy(Iterable<DeployableArtifact> artifacts) {
		for (DeployableArtifact artifact : artifacts) {
			deploy(artifact);
		}
	}

	/**
	 * Deploy the specified artifact to the repository.
	 * @param artifact the artifact to deploy
	 */
	void deploy(DeployableArtifact artifact);

	// FIXME DC
	List<DeployedArtifact> getDeployedArtifacts(String buildName, String buildNumber);

	// FIXME use File for dest?
	void download(DeployedArtifact artifact, String destination);

}
