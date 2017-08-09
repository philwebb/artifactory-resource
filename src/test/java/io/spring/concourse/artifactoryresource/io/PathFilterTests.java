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

package io.spring.concourse.artifactoryresource.io;

import java.util.Collections;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link PathFilter}.
 *
 * @author Phillip Webb
 * @author Madhura Bhave
 */
public class PathFilterTests {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void createWhenIncludeIsNullShouldThrowException() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("Include must not be null");
		new PathFilter(null, Collections.emptyList());
	}

	@Test
	public void createWhenExcludeIsNullShouldThrowException() throws Exception {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("Exclude must not be null");
		new PathFilter(Collections.emptyList(), null);
	}

	@Test
	public void isMatchWhenIncludeIsEmptyAndExcludeIsEmptyShouldReturnTrue()
			throws Exception {
		PathFilter filter = new PathFilter(Collections.emptyList(),
				Collections.emptyList());
		assertThat(filter.isMatch("foo")).isTrue();
	}

	@Test
	public void isMatchWhenIncludeIsEmptyAndExcludeMatchesShouldReturnFalse()
			throws Exception {
		PathFilter filter = new PathFilter(Collections.emptyList(),
				Collections.singletonList("**/foo"));
		assertThat(filter.isMatch("foo/bar")).isTrue();
		assertThat(filter.isMatch("bar/foo")).isFalse();
	}

	@Test
	public void isMatchWhenIncludeMatchesAndExcludeIsEmptyShouldReturnTrue()
			throws Exception {
		PathFilter filter = new PathFilter(Collections.singletonList("**/foo"),
				Collections.emptyList());
		assertThat(filter.isMatch("foo/bar")).isFalse();
		assertThat(filter.isMatch("bar/foo")).isTrue();
	}

	@Test
	public void isMatchWhenIncludeMatchesAndExcludeMatchesShouldReturnFalse()
			throws Exception {
		PathFilter filter = new PathFilter(Collections.singletonList("foo/**"),
				Collections.singletonList("**/bar"));
		assertThat(filter.isMatch("foo/bar")).isFalse();
		assertThat(filter.isMatch("foo/baz")).isTrue();
	}

}
