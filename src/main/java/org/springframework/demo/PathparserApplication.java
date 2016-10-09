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
package org.springframework.demo;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.patterns.PathPattern;
import org.springframework.util.patterns.PathPatternParser;
import org.springframework.util.patterns.PatternParseException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;


/**
 * Crude application that parses the path/query params to test out the parser, displaying the results.
 * 
 * @author Andy Clement
 */
@SpringBootApplication
public class PathparserApplication {

	public static void main(String[] args) {
		SpringApplication.run(PathparserApplication.class, args);
	}
}

@RestController
class Listener {
	
	@RequestMapping("/**")
	public String foo(@RequestParam(value="path",required=false) String path, HttpServletRequest req) {
		String url = (String) req.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		if (url == null || url.length()==0) {
			return "<tt>Usage: http://pathparser.cfapps.io/specify/the/uri/template/here?path=/and/optionally/the/path/to/match/against/here<br>"+
					"Example: <a href=\"http://pathparser.cfapps.io/{foo}?path=/123\">http://pathparser.cfapps.io/{foo}?path=/123</a></tt>";
		}
		StringBuilder buf = new StringBuilder();
		buf.append("<tt>");
		buf.append("Processing "+url+"<br>");
		buf.append("<br>");
		PathPattern p = null;
		try {
			p = new PathPatternParser().parse(url);
			buf.append("Sections:<br>");
			buf.append(p.toChainString().replaceAll(" ","&nbsp;"));
		} catch (PatternParseException ppe) {
			buf.append(ppe.toDetailedString().replaceAll("\n", "<br>").replaceAll(" ", "&nbsp;")).append("<br>");
			buf.append("Stack trace is:<br>");
			buf.append(ExceptionUtils.getStackTrace(ppe).replaceAll("\n", "<br>").replaceAll(" ", "&nbsp;"));
		}
		if (path != null && p!= null) {
			buf.append("<br>Matching pattern against path '"+path+"'<br>");
			boolean b = p.matches(path);
			buf.append("<br>Matches? "+(b?"YES":"NO")+"<br><br>");
			if (b) {
				Map<String,String> ms = p.matchAndExtract(path);
				if (ms.size()!=0) {
					buf.append("Variables:<br>");
					for (Map.Entry<String,String> entry: ms.entrySet()) {
						buf.append(entry.getKey()+"="+entry.getValue()+"<br>");
					}
				}
			}
		}
		buf.append("</tt>");
		return buf.toString();
	}

}