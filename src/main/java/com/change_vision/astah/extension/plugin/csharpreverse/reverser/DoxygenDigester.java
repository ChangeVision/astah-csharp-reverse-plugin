package com.change_vision.astah.extension.plugin.csharpreverse.reverser;

import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

public class DoxygenDigester extends Digester {

	static String current = "";

	@Override
	public void characters(char[] buffer, int start, int length)
			throws SAXException {
		super.characters(buffer, start, length);
		if (!bodyText.toString().trim().equals("")) {
			if (this.match
					.equals("doxygen/compounddef/sectiondef/memberdef/initializer")) {
				String sb = new String(buffer);
				int lastIndexOf = sb.lastIndexOf("<initializer>", start);
				int indexOf = sb.indexOf("</initializer>", start + length);
				if (lastIndexOf != -1 && indexOf != -1) {
					current = sb.substring(
							lastIndexOf + "<initializer>".length(), indexOf)
							.trim();
				} else if (lastIndexOf != -1 && indexOf == -1) {
					current = sb.substring(
							lastIndexOf + "<initializer>".length(),
							buffer.length - 1).trim();
				} else if (lastIndexOf == -1 && indexOf != -1
						&& !"".equals(current)) {
					current += sb.substring(0, indexOf).trim();
				}
			}
		}

		defragCurrent();
	}

	/**
	 * currentを最適化します。
	 */
	private void defragCurrent() {
		// 改行が含まれる場合、半角スペースに置き換え
		if (current.indexOf("\n") != -1) {
			current = current.replace("\n", " ");
		}
		// 先頭がイコールの場合空文字と置き換え
		if (current.startsWith("=")) {
			current = current.replaceFirst("=", "");
		}
		// 前後の空白を削除する
		current = current.trim();
	}

}