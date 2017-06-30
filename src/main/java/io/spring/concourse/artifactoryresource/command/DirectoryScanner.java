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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

/**
 * Utility to scan a {@link Directory} for contents.
 *
 * @author Phillip Webb
 * @author Madhura Bhave
 */
public class DirectoryScanner {

	/**
	 * Scan the given directory for files, accounting for the include and exclude
	 * patterns.
	 * @param directory the source directory
	 * @param include the include patterns
	 * @param exclude the exclude patterns
	 * @return the scanned list of files
	 */
	public List<File> scan(Directory directory, List<String> include,
			List<String> exclude) {
		try {
			Path path = directory.getFile().toPath();
			BiPredicate<Path, BasicFileAttributes> filter = getFilter(include, exclude);
			List<File> files = Files.find(path, Integer.MAX_VALUE, filter)
					.map(Path::toFile).collect(Collectors.toCollection(ArrayList::new));
			Collections.sort(files);
			return files;
		}
		catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	}

	private BiPredicate<Path, BasicFileAttributes> getFilter(List<String> include,
			List<String> exclude) {
		return (path, fileAttributes) -> {
			if (!path.toFile().isFile()) {
				return false;
			}
			return true;
		};
	}

}
