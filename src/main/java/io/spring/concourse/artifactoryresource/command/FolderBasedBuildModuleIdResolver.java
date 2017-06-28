package io.spring.concourse.artifactoryresource.command;

import io.spring.concourse.artifactoryresource.artifactory.payload.DeployableArtifact;

/**
 * @author Madhura Bhave
 */
public class FolderBasedBuildModuleIdResolver implements BuildModuleIdResolver {

	private final DeployableArtifact artifact;

	public FolderBasedBuildModuleIdResolver(DeployableArtifact artifact) {
		this.artifact = artifact;
	}

	@Override
	public String getBuildModuleId() {
		return null; // FIXME
	}
}
