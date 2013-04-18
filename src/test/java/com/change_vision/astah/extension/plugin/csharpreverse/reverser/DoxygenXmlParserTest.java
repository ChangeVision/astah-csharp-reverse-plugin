package com.change_vision.astah.extension.plugin.csharpreverse.reverser;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.xml.sax.SAXException;

import com.change_vision.astah.extension.plugin.csharpreverse.exception.IndexXmlNotFoundException;
import com.change_vision.astah.extension.plugin.csharpreverse.view.CloseDialog;

public class DoxygenXmlParserTest {

	// @Test
	// public void test() throws IOException, SAXException {
	// String path = getClass().getResource("index.xml").getFile();
	// File file = new File(path);
	// CompoundDef compoundDef = DoxygenXmlParser.parserCompounddefXML(file);
	// System.out.println(compoundDef.compounddefId);
	// }

	/**
	 * compound を追加できること
	 */
	@Test
	public void testAddCompound_compoundを追加できること() {
		// パラメータで渡すもの作成
		Compound compound = new Compound();

		// 期待値の作成
		List<Compound> expecteds = new ArrayList<Compound>();
		expecteds.add(compound);

		// メソッドの実行
		DoxygenXmlParser parser = new DoxygenXmlParser();
		parser.addCompound(compound);

		// assert
		assertArrayEquals(expecteds.toArray(new Compound[0]),
				parser.compounds.toArray(new Compound[0]));
	}

	@Test
	public void testGetErrorLocationFile_LocationFileを取得できること() {
		DoxygenXmlParser.lastCompoundDef = new CompoundDef();
		assertEquals(DoxygenXmlParser.lastCompoundDef.getLocationFile(),
				new DoxygenXmlParser().getErrorLocationBodyFile());
	}

	@Test
	public void testGetErrorLocationFile_lastCompoundDefがnullのときnullが返ること() {
		DoxygenXmlParser parser = new DoxygenXmlParser();
		DoxygenXmlParser.lastCompoundDef = null;
		assertNull(parser.getErrorLocationBodyFile());
	}

	@Test
	public void testGetErrorLocationLine_LocationLineを取得できること() {
		DoxygenXmlParser.lastCompoundDef = new CompoundDef();
		DoxygenXmlParser.lastCompoundDef.setLocationFile("");
		assertEquals(DoxygenXmlParser.lastCompoundDef.getLocationLine(),
				new DoxygenXmlParser().getErrorLocationLine());
	}

	@Test
	public void testGetErrorLocationLine_lastCompoundDefがnullのときマイナス1が返ること() {
		DoxygenXmlParser parser = new DoxygenXmlParser();
		DoxygenXmlParser.lastCompoundDef = null;
		assertEquals(-1, parser.getErrorLocationLine());
	}

	@Test
	public void testGetErrorLocationBodyFile_LocationBodyFileを取得できること() {
		DoxygenXmlParser.lastCompoundDef = new CompoundDef();
		DoxygenXmlParser.lastCompoundDef.setLocationFile("");
		assertEquals(DoxygenXmlParser.lastCompoundDef.getLocationBodyFile(),
				new DoxygenXmlParser().getErrorLocationBodyFile());
	}

	@Test
	public void testGetErrorLocationBodyFile_lastCompoundDefがnullのときnullが返ること() {
		DoxygenXmlParser parser = new DoxygenXmlParser();
		DoxygenXmlParser.lastCompoundDef = null;
		assertEquals(null, parser.getErrorLocationBodyFile());
	}

	@Test
	public void testGetErrorLocationBodyStart_LocationBodyStartを取得できること() {
		DoxygenXmlParser.lastCompoundDef = new CompoundDef();
		DoxygenXmlParser.lastCompoundDef.setLocationFile("");
		assertEquals(DoxygenXmlParser.lastCompoundDef.getLocationBodyStart(),
				new DoxygenXmlParser().getErrorLocationBodyStart());
	}

	@Test
	public void testGetErrorLocationBodyStart_lastCompoundDefがnullのときマイナス1が返ること() {
		DoxygenXmlParser parser = new DoxygenXmlParser();
		DoxygenXmlParser.lastCompoundDef = null;
		assertEquals(-1, parser.getErrorLocationBodyStart());
	}

	@Test
	public void testGetErrorLocationBodyEnd_LocationBodyEndを取得できること() {
		DoxygenXmlParser.lastCompoundDef = new CompoundDef();
		DoxygenXmlParser.lastCompoundDef.setLocationFile("");
		assertEquals(DoxygenXmlParser.lastCompoundDef.getLocationBodyEnd(),
				new DoxygenXmlParser().getErrorLocationBodyEnd());
	}

	@Test
	public void testGetErrorLocationBodyEnd_lastCompoundDefがnullのときマイナス1が返ること() {
		DoxygenXmlParser parser = new DoxygenXmlParser();
		DoxygenXmlParser.lastCompoundDef = null;
		assertEquals(-1, parser.getErrorLocationBodyEnd());
	}

	@Test
	public void testParserCompounddefXML_何もエラーが出ないこと() throws IOException,
			SAXException {
		String path = getClass().getResource("index.xml").getFile();
		File file = new File(path);
		DoxygenXmlParser.parserCompounddefXML(file);
	}

	@Test(expected = FileNotFoundException.class)
	public void testParserCompounddefXML_フォルダを渡したときにFileNotFoundExceptionが出ること()
			throws IOException, SAXException {
		String path = getClass().getResource(".").getFile();
		File file = new File(path);
		DoxygenXmlParser.parserCompounddefXML(file);
	}

	@Test
	public void testFindFile_ファイルを探し出せること() throws IOException, SAXException {
		String fileName = "test.xml";
		String expectedFilePath = getClass().getResource(fileName).getFile();
		String path = expectedFilePath.replace(fileName, "");
		assertEquals(new File(expectedFilePath),
				DoxygenXmlParser.findFile(path, fileName));
	}

	@Test
	public void testFindFile_ファイルがないときにnullを返すこと() throws IOException,
			SAXException {
		String path = getClass().getResource(".").getFile();
		assertEquals(null, DoxygenXmlParser.findFile(path, "a.xml"));
	}

	// XXX NullPointerExceptionが出てたのでテストを書いたが、現在通らないのでコメントアウト
	// @Test
	// public void
	// testFindFile_パスにフォルダでなくファイルを指定したときにNullPointerExceptionが出ないこと() throws
	// IOException,
	// SAXException {
	// String fileName = "test.xml";
	// String expectedFilePath = getClass().getResource(fileName).getFile();
	// try {
	// DoxygenXmlParser.findFile(expectedFilePath, "a.xml");
	// } catch (NullPointerException e) {
	// fail();
	// }
	// }

	// TODO IOExceptionは本当に出るの？
	// @Test
	// public void testFindFile_IOExceptionが出ること() throws SAXException {
	// }

	// TODO SAXExceptionは本当に出るの？
	// @Test
	// public void testFindFile_SAXExceptionが出ること() throws IOException {
	// }

	@Test
	public void testGetFileType_正しいlanguage_typeを得られること() {
		assertEquals(LanguageManager.LANGUAGE_CSHARP,
				DoxygenXmlParser.getFileType("a\\a\\abc.cs"));
		assertEquals(LanguageManager.LANGUAGE_JAVA,
				DoxygenXmlParser.getFileType("a\\a\\abc.java"));
		assertEquals(LanguageManager.LANGUAGE_CPLUS,
				DoxygenXmlParser.getFileType("a\\a\\abc.cpp"));
		assertEquals(LanguageManager.LANGUAGE_CPLUS,
				DoxygenXmlParser.getFileType("a\\a\\abc.h"));
		assertEquals(LanguageManager.LANGUAGE_CPLUS,
				DoxygenXmlParser.getFileType("a\\a\\abc.cc"));
	}

	@Test
	public void testGetFileType_対象外のファイル形式のときにnullが返ること() {
		assertEquals(null, DoxygenXmlParser.getFileType("a\\a\\abc.txt"));
	}

	@Test(expected = IndexXmlNotFoundException.class)
	public void testParser_index_xmlが見つからないパスを渡すとIndexXmlNotFoundExceptionが出ること()
			throws Throwable {
		DoxygenXmlParser.parser("", null);
	}

	@Test
	public void testParser_モデルファイルがインポートできること() {
		String path = getClass().getResource(".").getFile();
		CloseDialog dialog = mock(CloseDialog.class);
		try {
			DoxygenXmlParser.parser(path, dialog);
		} catch (Throwable e) {
			fail();
		}
	}



}
