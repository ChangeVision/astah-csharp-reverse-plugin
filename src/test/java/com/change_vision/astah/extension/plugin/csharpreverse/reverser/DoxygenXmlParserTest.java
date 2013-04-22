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
import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.LicenseNotFoundException;
import com.change_vision.jude.api.inf.exception.NonCompatibleException;
import com.change_vision.jude.api.inf.exception.ProjectLockedException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IParameter;
import com.change_vision.jude.api.inf.project.ModelFinder;
import com.change_vision.jude.api.inf.project.ProjectAccessor;

public class DoxygenXmlParserTest {

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

	@Test
	public void testParser_1次配列がリバースできていること() throws Throwable {
		String modelPath = parseProject("array");
		INamedElement[] elements = findElements(modelPath, "Aaa");
		for (INamedElement element : elements) {
			if (element instanceof IClass) {
				IClass a = (IClass) element;
				IParameter parameter = a.getOperations()[0].getParameters()[0];
				assertEquals(
						"int[]",
						parameter.getTypeExpression()
								+ parameter.getTypeModifier());
			}
		}
		elements = findElements(modelPath, "Bbb");
		for (INamedElement element : elements) {
			if (element instanceof IClass) {
				IClass a = (IClass) element;
				IParameter parameter = a.getOperations()[0].getParameters()[0];
				assertEquals(
						"Aaa[]",
						parameter.getTypeExpression()
								+ parameter.getTypeModifier());
			}
		}
	}

	@Test
	public void testParser_2次配列がリバースできていること() throws Throwable {
		String modelPath = parseProject("array");
		INamedElement[] elements = findElements(modelPath, "Aaa");
		for (INamedElement element : elements) {
			if (element instanceof IClass) {
				IClass a = (IClass) element;
				IParameter parameter = a.getOperations()[1].getParameters()[0];
				assertEquals("int[][]", parameter.getTypeExpression()
						+ parameter.getTypeModifier());

			}
		}
		elements = findElements(modelPath, "Bbb");
		for (INamedElement element : elements) {
			if (element instanceof IClass) {
				IClass a = (IClass) element;
				IParameter parameter = a.getOperations()[1].getParameters()[0];
				assertEquals("Aaa[][]", parameter.getTypeExpression()
						+ parameter.getTypeModifier());

			}
		}
	}

	@Test
	public void testParser_3次配列がリバースできていること() throws Throwable {
		String modelPath = parseProject("array");
		INamedElement[] elements = findElements(modelPath, "Aaa");
		for (INamedElement element : elements) {
			if (element instanceof IClass) {
				IClass a = (IClass) element;
				IParameter parameter = a.getOperations()[2].getParameters()[0];
				assertEquals("int[][][]", parameter.getTypeExpression()
						+ parameter.getTypeModifier());

			}
		}
		 elements = findElements(modelPath, "Bbb");
		for (INamedElement element : elements) {
			if (element instanceof IClass) {
				IClass a = (IClass) element;
				IParameter parameter = a.getOperations()[2].getParameters()[0];
				assertEquals("Aaa[][][]", parameter.getTypeExpression()
						+ parameter.getTypeModifier());

			}
		}
	}

	@Test
	public void testParser_関連端の可視性が正しく設定できること() throws Throwable {
		String modelPath = parseProject("visibility");
		INamedElement[] elements = findElements(modelPath, "Aaa");
		for (INamedElement element : elements) {
			if (element instanceof IClass) {
				IClass a = (IClass) element;
				IAttribute[] attributes = a.getAttributes();
				assertEquals(true, attributes[0].isPublicVisibility());
				assertEquals(true, attributes[1].isPrivateVisibility());
				assertEquals(true, attributes[2].isProtectedVisibility());
				assertEquals(true, attributes[3].isPackageVisibility());
			}
		}
	}

	@Test
	public void testParser_xmlファイルの変数の宣言中に改行が含まれる場合でもリバース結果の属性の初期値に改行や不要なイコールが含まれないこと()
			throws Throwable {
		String modelPath = parseProject("linefeed");
		INamedElement[] elements = findElements(modelPath, "ConsoleRunnerTest");

		for (INamedElement element : elements) {
			if (element instanceof IClass) {
				IClass a = (IClass) element;
				IAttribute[] attributes = a.getAttributes();
				for (IAttribute attribute : attributes) {
					assertEquals(-1, attribute.getInitialValue().indexOf("\n"));
					assertEquals(-1, attribute.getInitialValue().indexOf("="));
				}
			}
		}
	}

	@Test
	public void testParser_定数の初期値が不正でないこと() throws Throwable {
		String modelPath = parseProject("two_parameters");
		INamedElement[] elements = findElements(modelPath, "Aaa");

		for (INamedElement element : elements) {
			if (element instanceof IClass) {
				IClass a = (IClass) element;
				IAttribute[] attributes = a.getAttributes();
				for (IAttribute attribute : attributes) {
					assertEquals("new Bbb(0, 1)", attribute.getInitialValue());
				}
			}
		}
	}

	@Test
	public void testParser_メソッドで定義されていても変数の初期値が不正でないこと() throws Throwable {
		String modelPath = parseProject("default_value1");
		INamedElement[] elements = findElements(modelPath, "Aaa");

		for (INamedElement element : elements) {
			if (element instanceof IClass) {
				IClass a = (IClass) element;
				IAttribute[] attributes = a.getAttributes();
				for (IAttribute attribute : attributes) {
					assertEquals(
							"InternalTrace.GetLogger(typeof(NUnitTestAgent))",
							attribute.getInitialValue());
				}
			}
		}
	}

	/**
	 * モデルを開いて、探したい要素の名前と等しい名前の要素を返します。
	 * 
	 * @param modelPath
	 *            モデルパス
	 * @param elementName
	 *            探したい要素の名前
	 * @return 探したい要素
	 * @throws ClassNotFoundException
	 * @throws LicenseNotFoundException
	 * @throws ProjectNotFoundException
	 * @throws NonCompatibleException
	 * @throws IOException
	 * @throws ProjectLockedException
	 */
	private INamedElement[] findElements(String modelPath,
			final String elementName) throws ClassNotFoundException,
			LicenseNotFoundException, ProjectNotFoundException,
			NonCompatibleException, IOException, ProjectLockedException {
		ProjectAccessor accessor = AstahAPI.getAstahAPI().getProjectAccessor();
		accessor.open(modelPath);
		INamedElement[] elements = accessor.findElements(new ModelFinder() {
			@Override
			public boolean isTarget(INamedElement element) {
				return element.getName().equals(elementName);
			}
		});
		return elements;
	}

	@Test
	public void testParser_クラスや属性の定義が設定されていること() throws Throwable {
		String modelPath = parseProject("para");
		INamedElement[] elements = findElements(modelPath, "Aaa");

		for (INamedElement element : elements) {
			if (element instanceof IClass) {
				IClass a = (IClass) element;
				assertEquals("クラスAaa", a.getDefinition());
				assertEquals("文字列aaa", a.getAttributes()[0].getDefinition());
				assertEquals("メソッドaaa()", a.getOperations()[0].getDefinition());
			}
		}
	}
	
//	@Test
//	public void testParser_System配下のクラスを継承していること() throws Throwable {
//		String modelPath = parseProject("extend");
//		INamedElement[] elements = findElements(modelPath, "Aaa");
//
//		for (INamedElement element : elements) {
//			if (element instanceof IClass) {
//				IClass a = (IClass) element;
//				IClass superClass = a.getGeneralizations()[0].getSuperType();
//				assertEquals("System",
//						IPackage.class.cast(superClass.getOwner()).getName());
//			}
//		}
//	}
	

	/**
	 * DoxgenのXMLからastahモデルに構文解析した一次ファイルを作成し、そのパスを返します。
	 * 
	 * @param name
	 *            xmlファイルを置いたフォルダの名前
	 * @return modelPath
	 * @throws LicenseNotFoundException
	 * @throws ProjectLockedException
	 * @throws IndexXmlNotFoundException
	 * @throws Throwable
	 */
	private String parseProject(String name) throws LicenseNotFoundException,
			ProjectLockedException, IndexXmlNotFoundException, Throwable {
		String path = getClass().getResource(name).getFile();
		CloseDialog dialog = mock(CloseDialog.class);
		return DoxygenXmlParser.parser(path, dialog);
	}

}
