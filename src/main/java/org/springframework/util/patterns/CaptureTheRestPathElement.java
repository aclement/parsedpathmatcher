/*
 * Copyright 2016 the original author or authors.
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
package org.springframework.util.patterns;

import org.springframework.util.patterns.PathPattern.MatchingContext;

/**
 * A path element representing capturing the rest of a path. In the pattern
 * '/foo/{*foobar}' the {*foobar} is represented as a {@link CaptureTheRestPathElement}.
 * 
 * @author Andy Clement
 */
class CaptureTheRestPathElement extends PathElement {

	private String variableName;

	/**
	 * @param pos
	 * @param captureDescriptor a character array containing contents like '{' '*' 'a' 'b' '}'
	 */
	CaptureTheRestPathElement(int pos, char[] captureDescriptor) {
		super(pos);
		variableName = new String(captureDescriptor, 2, captureDescriptor.length - 3);
	}

	@Override
	public boolean matches(int candidateIndex, MatchingContext matchingContext) {
		// No need to handle 'match start' checking as this captures everything
		// anyway and cannot be followed by anything else
		// assert next == null
		// TODO regex constraint on this?
		if (matchingContext.extractingVariables) {
			matchingContext.set(variableName, new String(matchingContext.candidate, candidateIndex,
					matchingContext.candidateLength - candidateIndex));
		}
		return true;
	}

	@Override
	public String getText() {
		StringBuilder buf = new StringBuilder();
		buf.append("{*");
		buf.append(variableName);
		buf.append('}');
		return buf.toString();
	}

	public String toString() {
		return "CaptureTheRest({*" + variableName + "})";
	}

	@Override
	public int getNormalizedLength() {
		return 1;
	}

	@Override
	public int getWildcardCount() {
		return 0;
	}

	@Override
	public int getCaptureCount() {
		return 1;
	}
}