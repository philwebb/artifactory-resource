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

public class CheckCommand implements Command {

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * io.spring.concourse.artifactoryresource.command.Command#run(java.lang.String[])
	 */
	@Override
	public void run(String[] commandArgs) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Auto-generated method stub");

		// Artifactory artifactory = ArtifactoryClient.create("ArtifactoryUrl",
		// "username",
		// "password");
		// artifactory.repository("foo").upload("foo", new File("bar"))
		// .withProperty("bar", "baz").doUpload();

	}

}
