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

import java.util.regex.Matcher;

import org.springframework.util.patterns.PathPattern.MatchingContext;

/**
 * A path element representing capturing a piece of the path as a variable. In the pattern
 * '/foo/{bar}/goo' the {bar} is represented as a {@link CaptureVariablePathElement}.
 * 
 * @author Andy Clement
 */
class CaptureVariablePathElement extends PathElement {

	private String variableName;
	
	private java.util.regex.Pattern constraintPattern;
	
	private boolean caseSensitive;

	/**
	 * @param pos the position in the pattern of this capture element
	 * @param captureDescriptor is of the form {AAAAA[:pattern]}
	 */
	CaptureVariablePathElement(int pos, char[] captureDescriptor, boolean caseSensitive) {
		super(pos);
		this.caseSensitive = caseSensitive;
		int colon = -1;
		for (int i = 0; i < captureDescriptor.length; i++) {
			if (captureDescriptor[i] == ':') {
				colon = i;
				break;
			}
		}
		if (colon == -1) {
			// no constraint
			variableName = new String(captureDescriptor, 1, captureDescriptor.length - 2);
		} else {
			variableName = new String(captureDescriptor, 1, colon - 1);
			if (caseSensitive) {
				constraintPattern = java.util.regex.Pattern
						.compile(new String(captureDescriptor, colon + 1, captureDescriptor.length - colon - 2));
			} else {
				constraintPattern = java.util.regex.Pattern.compile(
						new String(captureDescriptor, colon + 1, captureDescriptor.length - colon - 2),
						java.util.regex.Pattern.CASE_INSENSITIVE);
			}
		}
	}

	@Override
	public boolean matches(int candidateIndex, MatchingContext matchingContext) {
		int nextPos = matchingContext.scanAhead(candidateIndex);
		CharSequence candidateCapture = null;
		if (constraintPattern != null) {
			// TODO could push the regex match such that we only try it if the rest of the pattern matches - what is faster?
			candidateCapture = new SubSequence(matchingContext.candidate, candidateIndex, nextPos);
			Matcher m = constraintPattern.matcher(candidateCapture);
			if (!m.matches()) {
				return false;
			}
		}
		boolean match = false;
		if (next == null) {
			match = (nextPos == matchingContext.candidateLength);
		} else {
			if (matchingContext.isMatchStartMatching && nextPos == matchingContext.candidateLength) {
				match = true; // no more data but matches up to this point
			} else {
				match = next.matches(nextPos, matchingContext);
			}
		}
		if (match && matchingContext.extractingVariables) {
			matchingContext.set(variableName, new String(matchingContext.candidate, candidateIndex, nextPos - candidateIndex));
		}
		return match;
	}
	
	public String getVariableName() {
		return this.variableName;
	}
	
	@Override
	public String getText() {
		StringBuilder buf = new StringBuilder();
		buf.append('{');
		buf.append(variableName);
		if (constraintPattern != null) {
			buf.append(':').append(constraintPattern.pattern());
		}
		buf.append('}');
		return buf.toString();
	}

	public String toString() {
		return "CaptureVariable({" + variableName + (constraintPattern == null ? "" : ":" + constraintPattern.pattern()) + "})";
	}

	public boolean isCaseSensitive() {
		return caseSensitive;
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
	
	@Override
	public int getScore() {
		return CAPTURE_VARIABLE_WEIGHT;
	}
}