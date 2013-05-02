/**
 * 
 */
package com.change_vision.astah.extension.plugin.csharpreverse.reverser;

import static org.junit.Assert.assertEquals;

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
	public void testReplaceInvalidEnd_() {
		InitBeanPropertySetterRule rule = new InitBeanPropertySetterRule();
		String currentContent = "<ref a a>a</sp>c</ref><ref a a>a</sp>c</ref>b";
		assertEquals("<ref a a>a c</ref><ref a a>a c</ref>b",
				rule.replaceInvalidEnd(currentContent));
	}

	/**
	 * {@link com.change_vision.astah.extension.plugin.csharpreverse.reverser.InitBeanPropertySetterRule#replaceRefClassname(String)}
	 * のためのテスト・メソッド。
	 */
	@Test
	public void testReplaceRefClassname_refタグがすべて削除されること() {
		InitBeanPropertySetterRule rule = new InitBeanPropertySetterRule();
		String currentContent = "<ref a a>a</sp>c</ref><ref a a>a</sp>c</ref>b";
		assertEquals("a</sp>ca</sp>cb",
				rule.replaceRefClassname(currentContent));
	}

}
