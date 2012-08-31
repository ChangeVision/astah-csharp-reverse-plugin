package com.change_vision.astah.extension.plugin.csharpreverse.reverser;


import org.apache.commons.digester.BeanPropertySetterRule;
import org.xml.sax.Attributes;

/**
 * Rule implements sets initializer. it will get All value between tag<initializer> and </initializer>,
 * 
 *
 * The property set:
 * can be specified when the rule is created
 * or can match the current element when the rule is called.
 *
 */
public class InitBeanPropertySetterRule extends BeanPropertySetterRule {
	
	private String content;

	public InitBeanPropertySetterRule() {
		this.propertyName = "initializer";
	}
	
	@Override
	public void body(String namespace, String name, String text)
			throws Exception {
		String currentContent = DoxygenDigester.current.trim();
		if (! text.trim().equals(currentContent) && !"".equals(currentContent)) {
			currentContent = currentContent.replaceAll("&quot;", "\"");
			currentContent = currentContent.replaceAll("&apos;", "\'");
			currentContent = currentContent.replaceAll("&gt;", ">");
			currentContent = replaceRefClassname(currentContent);
			this.content = replaceInvalidEnd(currentContent);
		}
		super.body(namespace, name, text);
	}
	
	private String replaceInvalidEnd(String currentContent) {
		while(currentContent.indexOf("</") != -1) {
			int start = currentContent.indexOf("</");
			int end = currentContent.indexOf(" ", start);
			if (end == -1) {
				currentContent = currentContent.substring(0, start);
			} else {
				currentContent = currentContent.substring(end);
			}
		}
		if (currentContent.endsWith("<")) {
			currentContent = currentContent.substring(0, currentContent.length() - 1);
		}
		return currentContent;
	}
	
	private String replaceRefClassname(String currentContent) {
		while(currentContent.indexOf("<ref") != -1) {
			int startIndex = currentContent.indexOf("<ref");
			int midIndex = currentContent.indexOf(">", startIndex);
			int endIndex = currentContent.indexOf("</ref>", startIndex);
			int specEndIndex = currentContent.indexOf("</", startIndex);
			if (startIndex != -1
					&& midIndex != -1
					&& (endIndex != -1
							|| specEndIndex != -1)) {
				String endString = "";
				if (endIndex != -1) {
				    endIndex += "</ref>".length();
					endString = currentContent.substring(endIndex, currentContent.length());
				} else if (specEndIndex != -1) {
					endIndex = specEndIndex;
					endString = currentContent.substring(endIndex + "</".length(), currentContent.length());
				}
				currentContent.substring(midIndex, endIndex);
				currentContent = currentContent.substring(0, startIndex)
				+ currentContent.substring(midIndex + ">".length(), endIndex)
				+ endString;
			} else {
				break;
			}
		}
		return currentContent;
	}

	@Override
	public void begin(String namespace, String name, Attributes attributes)
			throws Exception {
		super.begin(namespace, name, attributes);
	}
	
	@Override
	public void end(String namespace, String name) throws Exception {
		if (content != null) {
			this.bodyText = content;
			content = null;
		}
		super.end(namespace, name);
		DoxygenDigester.current = "";
	}
}