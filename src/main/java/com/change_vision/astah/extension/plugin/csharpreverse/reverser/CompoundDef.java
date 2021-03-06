package com.change_vision.astah.extension.plugin.csharpreverse.reverser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.SAXException;

import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IElement;
import com.change_vision.jude.api.inf.model.IModel;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IPackage;

/**
 * 
 * this class is the tag of **.xml<br/>
 * the tag is {@literal <compounddef>}. the class is named Compounddef<br/>
 * the tag is {@literal <compounddef id>}.it is named "compounddefId"<br/>
 * the tag is {@literal <compounddef kind>}.it is named "compounddefKind"<br/>
 * the tag kind type is "class","namespace","union","struct"<br/>
 * the tag is {@literal <compounddef port>}.it is named "compounddefVisible"<br/>
 * the sub-tag is {@literal <compoundname>}.it is named "compoundName"<br/>
 * the sub-tag is {@literal <location>}.it is named "locationFile"<br/>
 * the sub-tag is {@literal <detaileddescription><para>}.it is named
 * "detaileddescriptionPara"<br/>
 * templateParamList is list of the templateParam'class.tag is
 * {@literal <templateparamlist><param>}<br/>
 * sections is the list of section'class.tag is {@literal <sectiondef>}.<br/>
 * basecompoundList is the list of basecompound'class.tag is
 * {@literal <basecompoundref>}.<br/>
 * derivedcompoundList is the list of derivedcompound'class.tag is
 * {@literal <derivedcompoundref>}<br/>
 * innerclass is the list of innerclass'class.tag is {@literal <innerclass>}<br/>
 * innernamespace is the list of innernamespace'class.tag is
 * {@literal <innernamespace>}<br/>
 */
public class CompoundDef implements IConvertToJude {
	private String compounddefId;

	private String compounddefVisible;

	private String compounddefKind;

	private String compoundName;

	private List<TemplateParam> templateParamList = new ArrayList<TemplateParam>();

	private List<Section> sections = new ArrayList<Section>();

	private String detaileddescriptionPara;

	private String briefdescriptionPara;

	private String locationFile;

	// #205 #219
	private int locationLine;

	private String locationBodyFile;

	private int locationBodyStart;

	private int locationBodyEnd;
	//

	private List<BaseCompoundDefRef> basecompoundList = new ArrayList<BaseCompoundDefRef>();

	private List<DerivedCompoundRef> derivedcompoundList = new ArrayList<DerivedCompoundRef>();

	private List<InnerClass> innerclass = new ArrayList<InnerClass>();

	private List<InnerNameSpace> innernamespace = new ArrayList<InnerNameSpace>();

	public static Map<String, INamedElement> compounddef = new HashMap<String, INamedElement>();

	public static final String KIND_CLASS = "class";

	public static final String KIND_NAMESPACE = "namespace";

	public static final String KIND_INTERFACE = "interface";

	public List<Section> getSections() {
		return sections;
	}

	public void setSections(List<Section> sections) {
		this.sections = sections;
	}

	/**
	 * add Section class to the list and set the Parent relation
	 */
	public void addSection(Section newSection) {
		sections.add(newSection);
		newSection.setParent(this);
	}

	public String getCompounddefVisible() {
		return compounddefVisible;
	}

	public void setCompounddefVisible(String compounddefVisible) {
		this.compounddefVisible = compounddefVisible;
	}

	public String getCompoundName() {
		return compoundName;
	}

	public void setCompoundName(String compoundName) {
		this.compoundName = compoundName;
	}

	public String getCompounddefId() {
		return compounddefId;
	}

	public void setCompounddefId(String compounddefId) {
		this.compounddefId = compounddefId;
	}

	public String getCompounddefKind() {
		return compounddefKind;
	}

	public void setCompounddefKind(String compounddefKind) {
		this.compounddefKind = compounddefKind;
	}

	public String getDetaileddescriptionPara() {
		return detaileddescriptionPara;
	}

	public void setDetaileddescriptionPara(String detaileddescriptionPara) {
		this.detaileddescriptionPara = detaileddescriptionPara;
	}

	public String getBriefdescriptionPara() {
		return briefdescriptionPara;
	}

	public void setBriefdescriptionPara(String briefdescriptionPara) {
		this.briefdescriptionPara = briefdescriptionPara;
	}

	public String getLocationFile() {
		return locationFile;
	}

	public void setLocationFile(String locationFile) {
		this.locationFile = locationFile;
	}

	public int getLocationLine() {
		return locationLine;
	}

	public void setLocationLine(int locationLine) {
		this.locationLine = locationLine;
	}

	public String getLocationBodyFile() {
		return locationBodyFile;
	}

	public void setLocationBodyFile(String locationBodyFile) {
		this.locationBodyFile = locationBodyFile;
	}

	public int getLocationBodyStart() {
		return locationBodyStart;
	}

	public void setLocationBodyStart(int locationBodyStart) {
		this.locationBodyStart = locationBodyStart;
	}

	public int getLocationBodyEnd() {
		return locationBodyEnd;
	}

	public void setLocationBodyEnd(int locationBodyEnd) {
		this.locationBodyEnd = locationBodyEnd;
	}

	public List<DerivedCompoundRef> getDerivedcompoundList() {
		return derivedcompoundList;
	}

	public void setDerivedcompoundList(
			List<DerivedCompoundRef> derivedcompoundList) {
		this.derivedcompoundList = derivedcompoundList;
	}

	public void addDerivedcompoundList(DerivedCompoundRef derivedcompoundList) {
		this.derivedcompoundList.add(derivedcompoundList);
	}

	public List<BaseCompoundDefRef> getBasecompoundList() {
		return basecompoundList;
	}

	public void setBasecompoundList(List<BaseCompoundDefRef> basecompoundList) {
		this.basecompoundList = basecompoundList;
	}

	public void addBasecompoundList(BaseCompoundDefRef basecompoundList) {
		this.basecompoundList.add(basecompoundList);
	}

	public List<InnerClass> getInnerclass() {
		return innerclass;
	}

	public void addInnerclass(InnerClass innerclass) {
		this.innerclass.add(innerclass);
	}

	public void setInnerclass(List<InnerClass> innerclass) {
		this.innerclass = innerclass;
	}

	public List<InnerNameSpace> getInnernamespace() {
		return innernamespace;
	}

	public void addInnernamespace(InnerNameSpace innernamespace) {
		this.innernamespace.add(innernamespace);
	}

	public void setInnernamespace(List<InnerNameSpace> innernamespace) {
		this.innernamespace = innernamespace;
	}

	public List<TemplateParam> getTemplateParamList() {
		return templateParamList;
	}

	public void setTemplateParamList(List<TemplateParam> templateParamList) {
		this.templateParamList = templateParamList;
	}

	public void addTemplateParam(TemplateParam templeParam) {
		templateParamList.add(templeParam);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CompoundDef) {
			return ((CompoundDef) obj).getCompounddefId().equals(
					this.getCompounddefId());
		} else {
			return false;
		}
	}

	/**
	 * XXX hashCodeのオーバーライドが必要か
	 * 
	 * equals をオーバーライドしたので、 hashCode もオーバーライドします。 <br />
	 * このコードが呼び出されることは想定されていません。
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		assert false : "hashCodeが呼び出されることは想定されていません。";
		return 42; // 適当な値
	}

	public void convertToJudeModel(IElement parent, File[] files)
			throws InvalidEditingException, ClassNotFoundException,
			ProjectNotFoundException, IOException, SAXException {
		if (compounddef.get(this.getCompounddefId()) != null) {
			return;
		}
		if (KIND_CLASS.equals(this.getCompounddefKind())) {
			convertClass(parent, files, false);
		} else if (KIND_INTERFACE.equals(this.getCompounddefKind())) {
			convertClass(parent, files, true);
		} else if (KIND_NAMESPACE.equals(this.getCompounddefKind())) {
			convertPackage(files);
		} else {
			if (parent instanceof IPackage) {
				dealWithGlobalElements((IPackage) parent, files);
			}
		}
	}

	private void convertPackage(File[] files) throws ProjectNotFoundException,
			ClassNotFoundException, InvalidEditingException, IOException,
			SAXException {
		// deal with the kind is namespace
		// new pkg
		IPackage pkg = Tool.getPackage(getCompoundName().split("::"));
		dealWithGlobalElements(pkg, files);
		dealCompounddefInnerClass(files, this, pkg);
		for (InnerNameSpace innerNamespace : getInnernamespace()) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].getName().equals(
						innerNamespace.getRefid() + ".xml")) {
					DoxygenXmlParser.parserCompounddefXML(files[i])
							.convertToJudeModel(pkg, files);
				}
			}
		}
		compounddef.put(this.getCompounddefId(), pkg);
	}

	protected IClass convertClass(IElement parent, File[] files,
			boolean isInterface) throws ProjectNotFoundException,
			ClassNotFoundException, InvalidEditingException, SAXException,
			IOException {
		IClass iclass = getIElement(parent, isInterface);
		if (this.getBriefdescriptionPara() != null) {
			iclass.setDefinition("<summary>" + this.getBriefdescriptionPara()
					+ "</summary>");
		}
		if (this.getDetaileddescriptionPara() != null) {
			if (this.getBriefdescriptionPara() != null) {
				iclass.setDefinition(iclass.getDefinition() + "\n");
			}
			iclass.setDefinition(iclass.getDefinition() + "<remarks>"
					+ this.getDetaileddescriptionPara() + "</remarks>");
		}
		if (this.getCompounddefVisible() != null) {
			iclass.setVisibility(this.getCompounddefVisible());
		}
		dealWithTemplatePara(iclass, files);
		dealCompounddefInnerClass(files, this, iclass);
		compounddef.put(this.getCompounddefId(), iclass);
		return iclass;
	}

	void dealWithTemplatePara(IClass parent, File[] files)
			throws InvalidEditingException, ClassNotFoundException,
			ProjectNotFoundException, SAXException {
		for (Param param : getTemplateParamList()) {
			param.convertToJudeModel(parent, files);
			// special code, to fix dev bug 1228 [doxygen1.7.1][C#]
			// doxygen1.7.1のXMLはおかしい
			String parentName = parent.getName();
			if (parentName.endsWith("-g")) {
				parent.setName(parentName.substring(0, parentName.length() - 2));
			}
		}
	}

	protected void dealWithGlobalElements(IPackage pkg, File[] files)
			throws ProjectNotFoundException, ClassNotFoundException,
			InvalidEditingException, IOException, SAXException {
	}

	public void convertChildren(File[] files) throws InvalidEditingException,
			ClassNotFoundException, ProjectNotFoundException, IOException,
			SAXException {

		// new generalization
		for (BaseCompoundDefRef baseCompound : getBasecompoundList()) {
			IClass baseClass = (IClass) compounddef
					.get(baseCompound.getRefid());
			if (baseClass != null) {
				Tool.getGeneralization(
						(IClass) compounddef.get(getCompounddefId()), baseClass);
			} else {
				String[] names = baseCompound.getValue().split("::");
				List<String> path = convertPath(names);
				Tool.getGeneralization((IClass) compounddef
						.get(getCompounddefId()), Tool.getClass(
						(String[]) path.toArray(new String[] {}),
						names[names.length - 1], baseClass));
			}
		}

		for (DerivedCompoundRef derivedCompound : getDerivedcompoundList()) {
			IClass derivedClass = (IClass) compounddef.get(this
					.getCompounddefId());
			if (derivedClass != null) {
				Tool.getGeneralization(
						(IClass) compounddef.get(derivedCompound.getRefid()),
						derivedClass);
			} else {
				String[] names = derivedCompound.getValue().split("::");
				List<String> path = convertPath(names);
				Tool.getGeneralization((IClass) compounddef.get(derivedCompound
						.getRefid()), Tool.getClass(
						(String[]) path.toArray(new String[] {}),
						names[names.length - 1], derivedClass));
			}
		}

		for (Section next : this.getSections()) {
			INamedElement element = (INamedElement) compounddef.get(this
					.getCompounddefId());
			if (element != null && !"".equals(element.getName())) {
				next.convertToJudeModel(element, files);
			}
		}
	}

	void dealCompounddefInnerClass(File[] files, CompoundDef compounddef,
			IElement pkg) throws IOException, SAXException,
			InvalidEditingException, ClassNotFoundException,
			ProjectNotFoundException {
		// new class in the pkg
		for (InnerClass innerCls : compounddef.getInnerclass()) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].getName().equals(innerCls.getRefid() + ".xml")) {
					CompoundDef newCompounddef = DoxygenXmlParser
							.parserCompounddefXML(files[i]);
					newCompounddef.convertToJudeModel(pkg, files);
				}
			}
		}
	}

	List<String> convertPath(String[] names) {
		List<String> path = new ArrayList<String>();
		if (names.length > 1) {
			for (int i = 0; i < (names.length - 1); i++) {
				path.add(names[i]);
			}
		}
		return path;
	}

	public IClass getIElement(IElement parent, boolean isInterface)
			throws ProjectNotFoundException, ClassNotFoundException,
			InvalidEditingException {
		// to fix bug 4120, compoundName need not include template param list.
		// doxygen1.7.0 compoundName = "List<E>", with templateparamlist
		// doxygen1.7.1 compoundName = "List", with templateparamlist
		if (!getTemplateParamList().isEmpty()
				&& getCompoundName().indexOf("<") != -1
				&& getCompoundName().endsWith(">")) {
			int startIndex = getCompoundName().indexOf("<");
			String paramStr = getCompoundName().substring(startIndex + 1,
					getCompoundName().length() - 1);
			String[] params = paramStr.split(",");
			if (params != null
					&& params.length == getTemplateParamList().size()) {
				boolean isParam = true;
				for (int i = 0; i < params.length; i++) {
					String templateParam = ((TemplateParamCSharp) getTemplateParamList()
							.get(i)).getType().trim();
					if (!params[i].trim().equals(templateParam)) {
						isParam = false;
					}
				}
				if (isParam) {
					setCompoundName(getCompoundName().substring(0, startIndex));
				}
			}
		}
		String[] names = getCompoundName().split("::");
		List<String> path = convertPath(names);

		IClass iclass = null;
		String className = names[names.length - 1];
		if (parent instanceof IModel) {
			IPackage pkg = Tool.getPackage((String[]) path
					.toArray(new String[0]));
			if (pkg != null) {
				if (isInterface) {
					iclass = Tool.getInterface(pkg, className);
				} else {
					iclass = Tool.getClass(pkg, className, null);
				}
			} else {
				IClass nestClass = getNestClass(path);
				if (nestClass != null) {
					if (isInterface) {
						iclass = Tool.getInterface(nestClass, className);
					} else {
						iclass = Tool.getClass(nestClass, className);
					}
				} else {
					if (isInterface) {
						iclass = Tool.getInterface((IModel) parent, className);
					} else {
						iclass = Tool.getClass((IModel) parent, className,
								nestClass);
					}
				}
			}
		} else if (parent instanceof IPackage) {
			if (isInterface) {
				iclass = Tool.getInterface(((IPackage) parent), className);
			} else {
				iclass = Tool.getClass(((IPackage) parent),
						className.toString(), null);
			}
		} else if (parent instanceof IClass) {
			if (isInterface) {
				iclass = Tool.getInterface(((IClass) parent), className);
			} else {
				iclass = Tool.getClass(((IClass) parent), className.toString());
			}
		}
		return iclass;
	}

	IClass getNestClass(List<String> paths) throws ProjectNotFoundException,
			ClassNotFoundException, InvalidEditingException {
		IClass current = null;
		for (int i = 0; i < paths.size(); i++) {
			String path = paths.get(i);
			if (i == 0) {
				current = Tool.getClass(new String[] {}, path, null);
			} else {
				current = Tool.getClass(current, path);
			}
		}
		return current;
	}

}