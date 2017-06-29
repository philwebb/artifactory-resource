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

import org.springframework.boot.ApplicationArguments;
import org.springframework.util.Assert;

/**
 * A directory passed to a command.
 *
 * @author Phillip Webb
 * @author Madhura Bhave
 */
public class Directory {

	private final File file;

	public Directory(String path) {
		this(new File(path));
	}

	public Directory(File file) {
		Assert.state(file.exists(), "File '" + file + "' does not exist");
		Assert.state(file.isDirectory(), "File '" + file + "' is not a directory");
		this.file = file;
	}

	public File getFile() {
		return this.file;
	}

	@Override
	public String toString() {
		return this.file.toString();
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

	/**
	 * @param folder
	 * @return
	 */
	public Directory subFolder(String path) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Auto-generated method stub");
	}

	/**
	 * @param include
	 * @param exclude
	 */
	public List<File> scan(List<String> include, List<String> exclude) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Auto-generated method stub");
	}

	public static Directory fromArgs(ApplicationArguments args) {
		List<String> nonOptionArgs = args.getNonOptionArgs();
		Assert.state(nonOptionArgs.size() >= 1, "No directory argument specified");
		return new Directory(nonOptionArgs.get(0));
	}

}
