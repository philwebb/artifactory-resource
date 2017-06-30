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

/**
 * Utility to scan a {@link Directory} for contents.
 *
 * @author Phillip Webb
 * @author Madhura Bhave
 */
public class DirectoryScanner {

	/**
	 * @param include
	 * @param exclude
	 */
	public List<File> scan(Directory directory, List<String> include,
			List<String> exclude) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Auto-generated method stub");
	}

	//
	// private List<Resource> getResourcesToDeploy(OutRequest request) throws IOException
	// {
	// Params params = request.getParams();
	// List<String> excludes = params.getExclude();
	// List<String> includes = params.getInclude();
	// String folder = params.getFolder();
	// List<Resource> includeResources = new ArrayList<>();
	// for (String include : includes) {
	// getResources(folder, includeResources, include);
	// }
	// List<Resource> excludeResources = new ArrayList<>();
	// for (String exclude : excludes) {
	// getResources(folder, excludeResources, exclude);
	// }
	// includeResources.removeAll(excludeResources);
	// return includeResources;
	// }
	//
	// private void getResources(String folder, List<Resource> includeResources,
	// String include) throws IOException {
	// ResourceLoader resourceLoader = getResourceLoader();
	// PathMatchingResourcePatternResolver patternResolver = new
	// PathMatchingResourcePatternResolver(
	// resourceLoader);
	// String locationPattern = folder + "/" + include;
	// includeResources
	// .addAll(Arrays.asList(patternResolver.getResources(locationPattern)));
	// }
	//
	// private ResourceLoader getResourceLoader() {
	// return new FileSystemResourceLoader() {
	// @Override
	// protected Resource getResourceByPath(String path) {
	// return new FileSystemResource(path);
	// }
	// };
	// }

}
