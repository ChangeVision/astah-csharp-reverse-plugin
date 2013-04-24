package com.change_vision.astah.extension.plugin.csharpreverse.reverser;

import org.apache.commons.digester.BeanPropertySetterRule;
import org.xml.sax.Attributes;

/**
 * Rule implements sets initializer. it will get All value between
 * tag<initializer> and </initializer>,
 * 
 * 
 * The property set: can be specified when the rule is created or can match the
 * current element when the rule is called.
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
		if (!text.trim().equals(currentContent) && !"".equals(currentContent)) {
			currentContent = currentContent.replaceAll("&quot;", "\"");
			currentContent = currentContent.replaceAll("&apos;", "\'");
			currentContent = currentContent.replaceAll("&gt;", ">");
			currentContent = replaceRefClassname(currentContent);
			this.content = replaceInvalidEnd(currentContent);
		}
		super.body(namespace, name, text);
	}

	/**
	 * </ref>タグ以外の</○○>タグを削除します。 JUnitテストのために可視性を protected にします。
	 * 
	 * @param currentContent
	 *            現在のコンテンツ
	 * @return </○○>タグ削除後の文字列
	 */
	protected String replaceInvalidEnd(String currentContent) {

		if (currentContent.indexOf("</") == -1) {
			return currentContent;
		}

		int refIndex = currentContent.indexOf("</ref>");
		int startIndex = currentContent.indexOf("</");

		if (refIndex == startIndex) {
			int refEndIndex = refIndex + "</ref>".length();
			String forwardString = currentContent.substring(0, refEndIndex);
			String backString = currentContent.substring(refEndIndex,
					currentContent.length());
			return forwardString + replaceInvalidEnd(backString);
		}

		int endIndex = currentContent.indexOf(">", startIndex) + ">".length();
		String refStr = currentContent.substring(startIndex, endIndex);
		String newStr = currentContent.replaceFirst(refStr, " ");

		if (currentContent.indexOf("</") != -1) {
			newStr = replaceInvalidEnd(newStr);
		}

		return newStr;
	}

	/**
	 * コンテンツから<ref>タグを除去します。 JUnitテストのために可視性を protected にします。
	 * 
	 * @param currentContent
	 *            現在のコンテンツ
	 * @return <ref>タグを除去した文字列
	 */
	protected String replaceRefClassname(String currentContent) {

		if (currentContent.indexOf("<ref") == -1) {
			return currentContent;
		}

		// <ref>タグの始まりのインデックス
		int fstRefIndexOf = currentContent.indexOf("<ref");

		// <ref>タグの終わりのインデックス
		int lstIndexOf = currentContent.indexOf(">", fstRefIndexOf)
				+ ">".length();

		// 削除したい<ref>タグの抽出
		String refStr = currentContent.substring(fstRefIndexOf, lstIndexOf);

		// タグを空文字と置き換え（削除）
		String newStr = currentContent.replaceFirst(refStr, "");
		newStr = newStr.replaceFirst("</ref>", "");

		if (currentContent.indexOf("<ref") != -1) {
			newStr = replaceRefClassname(newStr);
		}
		return newStr;

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