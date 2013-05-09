/**
 * 
 */
package com.change_vision.astah.extension.plugin.csharpreverse.reverser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * @author y-matsuda
 * 
 */
public class InitBeanPropertySetterRuleTest {

	/**
	 * {@link com.change_vision.astah.extension.plugin.csharpreverse.reverser.InitBeanPropertySetterRule#replaceInvalidEnd(String)}
	 * のためのテスト・メソッド。
	 */
	@Test
	public void testReplaceInvalidEnd_refタグ以外が削除されること() {
		InitBeanPropertySetterRule rule = new InitBeanPropertySetterRule();
		String currentContent = "<ref a a>a<sp />c</ref><ref a a>a<sp />c</ref>b";
		assertEquals("<ref a a>a c</ref><ref a a>a c</ref>b",
				rule.replaceInvalidEnd(currentContent));
	}

	/**
	 * {@link com.change_vision.astah.extension.plugin.csharpreverse.reverser.InitBeanPropertySetterRule#replaceInvalidEnd(String)}
	 * のためのテスト・メソッド。
	 */
	@Test
	public void testReplaceInvalidEnd_タグの後ろの括弧が見つからなくてもスタックオーバーフローしないこと() {
		InitBeanPropertySetterRule rule = new InitBeanPropertySetterRule();
		String currentContent = "<a a aac< a aacb";
		try {
			rule.replaceInvalidEnd(currentContent);
		} catch (StackOverflowError e) {
			fail();
		}
	}

	/**
	 * {@link com.change_vision.astah.extension.plugin.csharpreverse.reverser.InitBeanPropertySetterRule#replaceRefClassname(String)}
	 * のためのテスト・メソッド。
	 */
	@Test
	public void testReplaceRefClassname_refタグがすべて削除されること() {
		InitBeanPropertySetterRule rule = new InitBeanPropertySetterRule();
		String currentContent = "<ref a a>a<sp />c</ref><ref a a>a<sp />c</ref>b";
		assertEquals("a<sp />ca<sp />cb",
				rule.replaceRefClassname(currentContent));
	}

	/**
	 * {@link com.change_vision.astah.extension.plugin.csharpreverse.reverser.InitBeanPropertySetterRule#replaceRefClassname(String)}
	 * のためのテスト・メソッド。
	 */
	@Test
	public void testReplaceRefClassname_refタグの後ろの括弧が見つからなくてもスタックオーバーフローしないこと() {
		InitBeanPropertySetterRule rule = new InitBeanPropertySetterRule();
		String currentContent = "<ref a aac<ref a aacb";
		try {
			rule.replaceRefClassname(currentContent);
		} catch (StackOverflowError e) {
			fail();
		}
	}

}
