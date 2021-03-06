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
 * A literal path element. In the pattern '/foo/bar/goo' there are three
 * literal path elements 'foo', 'bar' and 'goo'.
 * 
 * @author Andy Clement
 */
class LiteralPathElement extends PathElement {

	private char[] text;
	
	private int len;
	
	private boolean caseSensitive;

	public LiteralPathElement(int pos, char[] literalText, boolean caseSensitive) {
		super(pos);
		this.len = literalText.length;
		this.caseSensitive = caseSensitive;
		if (caseSensitive) {
			this.text = literalText;
		} else {
			// Force all the text lower case to make matching faster
			this.text = new char[literalText.length];
			for (int i = 0; i < len; i++) {
				this.text[i] = Character.toLowerCase(literalText[i]);
			}
		}
	}

	@Override
	public boolean matches(int candidateIndex, MatchingContext matchingContext) {
		if ((candidateIndex + text.length) > matchingContext.candidateLength) {
			return false; // not enough data, cannot be a match
		}
		if (caseSensitive) {
			for (int i = 0; i < len; i++) {
				if (matchingContext.candidate[candidateIndex++] != text[i]) {
					return false;
				}
			}
		} else {
			for (int i = 0; i < len; i++) {
				// TODO consider performance of this?
				if (Character.toLowerCase(matchingContext.candidate[candidateIndex++]) != text[i]) {
					return false;
				}
			}
		}
		if (next == null) {
			return candidateIndex == matchingContext.candidateLength;
		} else {
			if (matchingContext.isMatchStartMatching && candidateIndex == matchingContext.candidateLength) {
				return true; // no more data but everything matched so far
			}
			return next.matches(candidateIndex, matchingContext);
		}
	}

	@Override
	public int getNormalizedLength() {
		return len;
	}

	public String toString() {
		return "Literal(" + new String(text) + ")";
	}

	@Override
	public String getText() {
		return new String(this.text);
	}

}