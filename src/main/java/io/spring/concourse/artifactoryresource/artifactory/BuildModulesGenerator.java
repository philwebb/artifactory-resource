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

import java.util.Collections;
import java.util.List;

import io.spring.concourse.artifactoryresource.artifactory.payload.BuildModule;
import io.spring.concourse.artifactoryresource.artifactory.payload.DeployableArtifact;

/**
 * Strategy used to generate {@link BuildModule BuildModules} from
 * {@link DeployableArtifact DeployableArtifacts}.
 *
 * @author Phillip Webb
 * @author Madhura Bhave
 */
public interface BuildModulesGenerator {

	BuildModulesGenerator NONE = ((a) -> Collections.emptyList());

	List<BuildModule> getBuildModules(List<DeployableArtifact> deployableArtifacts);

}
