package com.change_vision.astah.extension.plugin.csharpreverse.reverser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.digester.Digester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.change_vision.astah.extension.plugin.csharpreverse.exception.IndexXmlNotFoundException;
import com.change_vision.astah.extension.plugin.csharpreverse.view.CloseDialog;
import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.editor.BasicModelEditor;
import com.change_vision.jude.api.inf.editor.ModelEditorFactory;
import com.change_vision.jude.api.inf.editor.TransactionManager;
import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.exception.LicenseNotFoundException;
import com.change_vision.jude.api.inf.exception.ProjectLockedException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IModel;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.project.ModelFinder;
import com.change_vision.jude.api.inf.project.ProjectAccessor;

/**
 * Parse Doxygen's xml to generate jude model
 * 
 */
public class DoxygenXmlParser {

	private static final Logger logger = LoggerFactory
			.getLogger(DoxygenXmlParser.class);

	List<Compound> compounds = new ArrayList<Compound>();

	static CompoundDef lastCompoundDef;

	public void addCompound(Compound compound) {
		compounds.add(compound);
	}

	public static String parser(String path, CloseDialog closeDialog)
			throws LicenseNotFoundException, ProjectLockedException,
			IndexXmlNotFoundException, Throwable {
		ProjectAccessor prjAccessor = AstahAPI.getAstahAPI()
				.getProjectAccessor();

		File indexFile;
		if ((indexFile = findFile(path, "index.xml")) == null) {
			throw new IndexXmlNotFoundException();
		}

		// check template project
		String astahModelPath = getTempAstahModelPath();

		InputStream stream = DoxygenXmlParser.class
				.getResourceAsStream("C_Sharp.asta");
		prjAccessor.open(stream);
		logger.info("Importing... please wait..");

		// get current jude project's root
		IModel project = AstahAPI.getAstahAPI().getProjectAccessor()
				.getProject();

		DoxygenXmlParser doxygenXmlParser = new DoxygenXmlParser();

		// begin transaction
		TransactionManager.beginTransaction();

		// get the availability the concourse
		List<CompoundDef> compounddefes = doxygenXmlParser
				.parserIndexXml(indexFile);

		setSystems(prjAccessor, compounddefes);

		for (CompoundDef compounddef : compounddefes) {
			// first deal namespace
			if (CompoundDef.KIND_NAMESPACE.equals(compounddef
					.getCompounddefKind())
					|| CompoundDef.KIND_INTERFACE.equals(compounddef
							.getCompounddefKind())) {
				lastCompoundDef = compounddef;
				compounddef.convertToJudeModel(project, indexFile
						.getParentFile().listFiles());
			}
		}

		for (CompoundDef compounddef : compounddefes) {
			// second deal all the compound
			lastCompoundDef = compounddef;
			compounddef.convertToJudeModel(project, indexFile.getParentFile()
					.listFiles());
		}

		for (CompoundDef compounddef : compounddefes) {
			// third convert all children
			lastCompoundDef = compounddef;
			compounddef.convertChildren(indexFile.getParentFile().listFiles());
		}
		CompoundDef.compounddef.clear();
		// end transaction
		TransactionManager.endTransaction();

		closeDialog.close();

		// save the project
		prjAccessor.saveAs(astahModelPath);

		// close the io
		prjAccessor.close();

		print("Import Done.");

		return astahModelPath;
	}

	/**
	 * テンプレートの中のSystemパッケージの中に、usingされているパッケージがあれば検索対象として取り込みます。
	 * 
	 * @param prjAccessor
	 *            プロジェクトアクセサ
	 * @param compounddefes
	 *            CompoundDefのリスト
	 * @throws ProjectNotFoundException
	 *             プロジェクトファイルが見つからないとき
	 */
	private static void setSystems(ProjectAccessor prjAccessor,
			List<CompoundDef> compounddefes) throws ProjectNotFoundException {
		for (final CompoundDef compoundDef : compounddefes) {
			lastCompoundDef = compoundDef;

			INamedElement[] foundElements = prjAccessor
					.findElements(new ModelFinder() {
						@Override
						public boolean isTarget(INamedElement namedElement) {
							return namedElement instanceof IClass
									&& compoundDef.getCompoundName()
											.equals(namedElement
													.getFullNamespace("::"));
						}
					});

			for (INamedElement iNamedElement : foundElements) {

				if (iNamedElement instanceof IClass) {
					IClass iClass = IClass.class.cast(iNamedElement);
					String type = CompoundDef.KIND_CLASS;
					if (Arrays.asList(iClass.getStereotypes()).contains(
							"interface")) {
						type = CompoundDef.KIND_INTERFACE;
					}
					CompoundDef.compounddef.put(getKey(iNamedElement, type),
							iNamedElement);
				}

			}
		}
	}

	/**
	 * 要素名とタイプからキーを作成、取得します。
	 * 
	 * @param iNamedElement
	 *            名前つき要素
	 * @param type
	 *            タイプ
	 * @return 作成したキー
	 */
	private static String getKey(INamedElement iNamedElement, String type) {
		return convertString(type + iNamedElement.getFullName("::"));
	}

	/**
	 * 文字列に次の文字が含まれていた場合に置換し、その文字列を返します。<br/>
	 * 
	 * (大文字) → _(小文字)<br/>
	 * : → _1<br/>
	 * . → _8
	 * 
	 * @param str
	 *            文字列
	 * @return 変換した文字列
	 */
	private static String convertString(String str) {
		StringBuffer sb = new StringBuffer();
		for (Character ch : str.toCharArray()) {
			if (ch.equals(':')) {
				sb.append("_1");
			} else if (ch.equals('.')) {
				sb.append("_8");
			} else if (Character.isUpperCase(ch)) {
				sb.append("_" + Character.toLowerCase(ch));
			} else {
				sb.append(ch);
			}
		}
		return sb.toString();
	}

	public String getErrorLocationFile() {
		if (lastCompoundDef != null) {
			return lastCompoundDef.getLocationFile();
		} else {
			return null;
		}
	}

	public int getErrorLocationLine() {
		if (getErrorLocationFile() != null) {
			return lastCompoundDef.getLocationLine();
		} else {
			return -1;
		}
	}

	public String getErrorLocationBodyFile() {
		if (getErrorLocationFile() != null) {
			return lastCompoundDef.getLocationBodyFile();
		} else {
			return null;
		}
	}

	public int getErrorLocationBodyStart() {
		if (getErrorLocationFile() != null) {
			return lastCompoundDef.getLocationBodyStart();
		} else {
			return -1;
		}
	}

	public int getErrorLocationBodyEnd() {
		if (getErrorLocationFile() != null) {
			return lastCompoundDef.getLocationBodyEnd();
		} else {
			return -1;
		}
	}

	/**
	 * 
	 * @return String
	 * @throws IOException
	 */
	private static String getTempAstahModelPath() throws IOException {
		Calendar cal = Calendar.getInstance();
		String year = Integer.toString(cal.get(Calendar.YEAR));
		String month = Integer.toString(cal.get(Calendar.MONTH) + 1);
		String day = Integer.toString(cal.get(Calendar.DATE));
		String hour = Integer.toString(cal.get(Calendar.HOUR_OF_DAY));
		String minute = Integer.toString(cal.get(Calendar.MINUTE));
		String second = Integer.toString(cal.get(Calendar.SECOND));

		String tempFileName = year + month + day + hour + minute + second;
		File tempFile = File.createTempFile(tempFileName, ".asta");
		tempFile.deleteOnExit();
		return tempFile.getAbsolutePath();
	}

	/**
	 * 
	 * @param fileName
	 *            : target indexFile
	 * @return the List
	 * @throws IOException
	 * @throws SAXException
	 */
	private List<CompoundDef> parserIndexXml(File indexFile)
			throws IOException, SAXException, ProjectNotFoundException,
			ClassNotFoundException, InvalidEditingException {
		// init digester for parser index.xml...
		Digester digester = new Digester();
		digester.setValidating(false);
		// create the DoxygenXmlParser
		digester.addObjectCreate("doxygenindex", DoxygenXmlParser.class);
		// create the Compound
		digester.addObjectCreate("doxygenindex/compound", Compound.class);
		digester.addSetProperties("doxygenindex/compound", "refid",
				"compoundRefid");
		digester.addSetProperties("doxygenindex/compound", "kind",
				"compoundKind");
		digester.addBeanPropertySetter("doxygenindex/compound/name", "name");

		digester.addSetNext("doxygenindex/compound", "addCompound");
		// init digester for parser index.xml
		DoxygenXmlParser doxygenXmlParser = (DoxygenXmlParser) digester
				.parse(indexFile);

		for (Compound compound : doxygenXmlParser.compounds) {
			if ("file".equals(compound.getKind())) {
				// setLanguage
				String type = getFileType(compound.getName());
				LanguageManager.setCurrentLanguagePrimitiveType(type);

				// get current jude project's root
				IModel project = AstahAPI.getAstahAPI().getProjectAccessor()
						.getProject();

				BasicModelEditor basicModelEditor = ModelEditorFactory
						.getBasicModelEditor();
				// set the project type
				if (LanguageManager.LANGUAGE_CSHARP.equals(type)) {
					basicModelEditor.setLanguageCSharp(project, true);
				} else if (LanguageManager.LANGUAGE_JAVA.equals(type)) {
					basicModelEditor.setLanguageJava(project, true);
				}
			}
		}

		List<CompoundDef> compounddefs = new ArrayList<CompoundDef>();
		for (Compound compound : doxygenXmlParser.compounds) {
			// parse the kindName is class or interface or struct or union
			String kind = compound.getKind();
			if ("namespace".equals(kind) || "class".equals(kind)
					|| "interface".equals(kind) || "struct".equals(kind)
					|| "union".equals(kind) || "file".equals(kind)) {
				File file = findFile(indexFile.getParent(), compound.getRefid()
						+ ".xml");
				compounddefs.add(parserCompounddefXML(file));
			}
		}
		return compounddefs;
	}

	/**
	 * 
	 * @param file
	 *            : target file
	 * @return the Compounddef
	 * @throws IOException
	 * @throws SAXException
	 */
	public static CompoundDef parserCompounddefXML(File file)
			throws IOException, SAXException {
		// init digester for parser class.xml or interface.xml...
		Digester digester = new DoxygenDigester();
		digester.setValidating(false);
		// create the Compounddef
		digester.addRule("doxygen/compounddef",
				new ObjectCreateAfterLanguageRule(CompoundDef.class));
		digester.addSetProperties("doxygen/compounddef", "id", "compounddefId");
		digester.addSetProperties("doxygen/compounddef", "prot",
				"compounddefVisible");
		digester.addSetProperties("doxygen/compounddef", "kind",
				"compounddefKind");
		digester.addBeanPropertySetter("doxygen/compounddef/compoundname",
				"compoundName");
		digester.addBeanPropertySetter(
				"doxygen/compounddef/briefdescription/para",
				"detaileddescriptionPara");

		// crreate the TempleParam
		digester.addRule("doxygen/compounddef/templateparamlist/param",
				new ObjectCreateAfterLanguageRule(TemplateParam.class));
		digester.addBeanPropertySetter(
				"doxygen/compounddef/templateparamlist/param/type", "type");
		digester.addBeanPropertySetter(
				"doxygen/compounddef/templateparamlist/param/declname",
				"declname");
		digester.addBeanPropertySetter(
				"doxygen/compounddef/templateparamlist/param/defname",
				"defname");
		digester.addBeanPropertySetter(
				"doxygen/compounddef/templateparamlist/param/defval", "defval");
		digester.addSetNext("doxygen/compounddef/templateparamlist/param",
				"addTemplateParam");

		// create the BaseCompounddefref
		digester.addRule("doxygen/compounddef/basecompoundref",
				new ObjectCreateAfterLanguageRule(BaseCompoundDefRef.class));
		digester.addSetProperties("doxygen/compounddef/basecompoundref",
				"refid", "refid");
		digester.addSetProperties("doxygen/compounddef/basecompoundref",
				"prot", "prot");
		digester.addSetProperties("doxygen/compounddef/basecompoundref",
				"virt", "virt");
		digester.addBeanPropertySetter("doxygen/compounddef/basecompoundref",
				"value");
		digester.addSetNext("doxygen/compounddef/basecompoundref",
				"addBasecompoundList");

		// create the Derivedcompoundref
		digester.addRule("doxygen/compounddef/derivedcompoundref",
				new ObjectCreateAfterLanguageRule(DerivedCompoundRef.class));
		digester.addSetProperties("doxygen/compounddef/derivedcompoundref",
				"refid", "refid");
		digester.addSetProperties("doxygen/compounddef/derivedcompoundref",
				"prot", "prot");
		digester.addSetProperties("doxygen/compounddef/derivedcompoundref",
				"virt", "virt");
		digester.addBeanPropertySetter(
				"doxygen/compounddef/derivedcompoundref", "value");
		digester.addSetNext("doxygen/compounddef/derivedcompoundref",
				"addDerivedcompoundList");

		// create the InnerClass
		digester.addRule("doxygen/compounddef/innerclass",
				new ObjectCreateAfterLanguageRule(InnerClass.class));
		digester.addSetProperties("doxygen/compounddef/innerclass", "refid",
				"refid");
		digester.addSetProperties("doxygen/compounddef/innerclass", "prot",
				"prot");
		digester.addSetNext("doxygen/compounddef/innerclass", "addInnerclass");

		// create the Innernamespace
		digester.addRule("doxygen/compounddef/innernamespace",
				new ObjectCreateAfterLanguageRule(InnerNameSpace.class));
		digester.addSetProperties("doxygen/compounddef/innernamespace",
				"refid", "refid");
		digester.addSetNext("doxygen/compounddef/innernamespace",
				"addInnernamespace");

		digester.addSetProperties("doxygen/compounddef/location", "file",
				"locationFile");
		digester.addSetProperties("doxygen/compounddef/location", "line",
				"locationLine");
		digester.addSetProperties("doxygen/compounddef/location", "bodyfile",
				"locationBodyFile");
		digester.addSetProperties("doxygen/compounddef/location", "bodystart",
				"locationBodyStart");
		digester.addSetProperties("doxygen/compounddef/location", "bodyend",
				"locationBodyEnd");
		//
		digester.addBeanPropertySetter(
				"doxygen/compounddef/detaileddescription/para",
				"detaileddescriptionPara");

		// create the Section
		digester.addRule("doxygen/compounddef/sectiondef",
				new ObjectCreateAfterLanguageRule(Section.class));
		digester.addSetProperties("doxygen/compounddef/sectiondef", "kind",
				"kind");
		digester.addSetNext("doxygen/compounddef/sectiondef", "addSection");

		// create the Member
		digester.addRule("doxygen/compounddef/sectiondef/memberdef",
				new ObjectCreateAfterLanguageRule(Member.class));
		digester.addSetProperties("doxygen/compounddef/sectiondef/memberdef",
				"kind", "kind");
		digester.addSetProperties("doxygen/compounddef/sectiondef/memberdef",
				"id", "id");
		digester.addSetProperties("doxygen/compounddef/sectiondef/memberdef",
				"prot", "prot");
		digester.addSetProperties("doxygen/compounddef/sectiondef/memberdef",
				"const", "constBoolean");
		digester.addSetProperties("doxygen/compounddef/sectiondef/memberdef",
				"static", "staticBoolean");
		digester.addSetProperties("doxygen/compounddef/sectiondef/memberdef",
				"gettable", "gettable");
		digester.addSetProperties("doxygen/compounddef/sectiondef/memberdef",
				"settable", "settable");
		digester.addSetProperties("doxygen/compounddef/sectiondef/memberdef",
				"virt", "virt");
		digester.addBeanPropertySetter(
				"doxygen/compounddef/sectiondef/memberdef/briefdescription/para",
				"detaileddescriptionPara");

		digester.addBeanPropertySetter(
				"doxygen/compounddef/sectiondef/memberdef/type", "type");
		digester.addRule("doxygen/compounddef/sectiondef/memberdef/type/ref",
				new ObjectCreateAfterLanguageRule(Ref.class));
		digester.addSetProperties(
				"doxygen/compounddef/sectiondef/memberdef/type/ref", "refid",
				"refid");
		digester.addSetProperties(
				"doxygen/compounddef/sectiondef/memberdef/type/ref", "refid",
				"kindref");
		digester.addBeanPropertySetter(
				"doxygen/compounddef/sectiondef/memberdef/type/ref", "value");
		digester.addSetNext(
				"doxygen/compounddef/sectiondef/memberdef/type/ref",
				"addTypeRef");

		// resolve special initializer
		digester.addRule(
				"doxygen/compounddef/sectiondef/memberdef/initializer",
				new InitBeanPropertySetterRule());

		digester.addRule("doxygen/compounddef/sectiondef/memberdef/enumvalue",
				new ObjectCreateAfterLanguageRule(EnumValue.class));
		digester.addBeanPropertySetter(
				"doxygen/compounddef/sectiondef/memberdef/enumvalue/name",
				"name");
		digester.addBeanPropertySetter(
				"doxygen/compounddef/sectiondef/memberdef/enumvalue/initializer",
				"initializer");
		digester.addBeanPropertySetter(
				"doxygen/compounddef/sectiondef/memberdef/enumvalue/briefdescription",
				"briefdescription");
		digester.addBeanPropertySetter(
				"doxygen/compounddef/sectiondef/memberdef/enumvalue/detaileddescription",
				"detaileddescription");
		digester.addSetNext(
				"doxygen/compounddef/sectiondef/memberdef/enumvalue", "addEnum");

		digester.addBeanPropertySetter(
				"doxygen/compounddef/sectiondef/memberdef/name", "name");
		digester.addBeanPropertySetter(
				"doxygen/compounddef/sectiondef/memberdef/argsstring",
				"argsstring");

		// create the Param
		digester.addRule("doxygen/compounddef/sectiondef/memberdef/param",
				new ObjectCreateAfterLanguageRule(Param.class));
		digester.addBeanPropertySetter(
				"doxygen/compounddef/sectiondef/memberdef/param/type", "type");
		digester.addRule(
				"doxygen/compounddef/sectiondef/memberdef/param/type/ref",
				new ObjectCreateAfterLanguageRule(Ref.class));
		digester.addSetProperties(
				"doxygen/compounddef/sectiondef/memberdef/param/type/ref",
				"refid", "refid");
		digester.addSetProperties(
				"doxygen/compounddef/sectiondef/memberdef/param/type/ref",
				"refid", "kindref");
		digester.addBeanPropertySetter(
				"doxygen/compounddef/sectiondef/memberdef/param/type/ref",
				"value");
		digester.addSetNext(
				"doxygen/compounddef/sectiondef/memberdef/param/type/ref",
				"addTypeRef");
		digester.addBeanPropertySetter(
				"doxygen/compounddef/sectiondef/memberdef/param/declname",
				"declname");
		digester.addBeanPropertySetter(
				"doxygen/compounddef/sectiondef/memberdef/param/defname",
				"defname");
		digester.addBeanPropertySetter(
				"doxygen/compounddef/sectiondef/memberdef/param/array", "array");
		digester.addSetNext("doxygen/compounddef/sectiondef/memberdef/param",
				"addMemberParam");

		digester.addBeanPropertySetter(
				"doxygen/compounddef/sectiondef/memberdef/detaileddescription/para",
				"detaileddescriptionPara");
		digester.addSetNext("doxygen/compounddef/sectiondef/memberdef",
				"addMember");

		logger.trace(String.format("%s", file));
		return (CompoundDef) digester.parse(file);
	}

	private static void print(String msg) {
		System.out.println(msg);
	}

	/**
	 * @param path
	 *            : target Path
	 * @param fileName
	 *            : target file's name
	 * @return the target file
	 */
	public static File findFile(String path, String fileName) {
		File dir = new File(path);
		if (dir.exists()) {
			File[] files = dir.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (fileName.equals(files[i].getName())) {
					return files[i];
				}
			}
		}
		return null;
	}

	/**
	 * analyze fileName to judge Language. *.cs: LANGUAGE_CSHARP *.java:
	 * LANGUAGE_JAVA *.cpp: LANGUAGE_CPLUS *.h: LANGUAGE_CPLUS *.cc:
	 * LANGUAGE_CPLUS
	 * 
	 * @return language type
	 */

	public static String getFileType(String fileName) {
		String[] fileNames = fileName.split("\\.");

		if ("cs".equals(fileNames[fileNames.length - 1])) {
			return LanguageManager.LANGUAGE_CSHARP;
		}
		if ("java".equals(fileNames[fileNames.length - 1])) {
			return LanguageManager.LANGUAGE_JAVA;
		}
		if ("cpp".equals(fileNames[fileNames.length - 1])) {
			return LanguageManager.LANGUAGE_CPLUS;
		}
		if ("h".equals(fileNames[fileNames.length - 1])) {
			return LanguageManager.LANGUAGE_CPLUS;
		}
		if ("cc".equals(fileNames[fileNames.length - 1])) {
			return LanguageManager.LANGUAGE_CPLUS;
		}
		return null;
	}
}