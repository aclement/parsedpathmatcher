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
 * A separator path element. In the pattern '/foo/bar' the two occurrences
 * of '/' will be represented by a SeparatorPathElement (if the default
 * separator of '/' is being used).
 * 
 * @author Andy Clement
 */
class SeparatorPathElement extends PathElement {

	private char separator;

	SeparatorPathElement(int pos, char separator) {
		super(pos);
		this.separator = separator;
	}

	/**
	 * Matching a separator is easy, basically the character at candidateIndex
	 * must be the separator.
	 */
	@Override
	public boolean matches(int candidateIndex, MatchingContext matchingContext) {
		boolean matched = false;
		if (candidateIndex < matchingContext.candidateLength) {
			if (matchingContext.candidate[candidateIndex] == separator) {
				if (next == null) {
					matched = ((candidateIndex + 1) == matchingContext.candidateLength);
				} else {
					candidateIndex++;
					if (matchingContext.isMatchStartMatching && candidateIndex == matchingContext.candidateLength) {
						return true; // no more data but matches up to this point
					}
					matched = next.matches(candidateIndex, matchingContext);
				}
			}
		}
		return matched;
	}

	@Override
	public String getText() {
		return Character.toString(separator);
	}

	public String toString() {
		return "Separator(" + separator + ")";
	}

	@Override
	public int getNormalizedLength() {
		return 1;
	}

}