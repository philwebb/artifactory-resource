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

package io.spring.concourse.artifactoryresource.system;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Mock {@link SystemStreams} implementation.
 *
 * @author Phillip Webb
 */
public class MockSystemStreams extends SystemStreams {

	protected MockSystemStreams(String in) {
		super(new ByteArrayInputStream(in.getBytes()), new ByteArrayPrintStream());
	}

	public byte[] getOutBytes() {
		return ((ByteArrayPrintStream) out()).getByteArrayOutputStream().toByteArray();
	}

	private static class ByteArrayPrintStream extends PrintStream {

		public ByteArrayPrintStream() {
			super(new ByteArrayOutputStream());
		}

		private ByteArrayOutputStream getByteArrayOutputStream() {
			return (ByteArrayOutputStream) this.out;
		}

	}

}
