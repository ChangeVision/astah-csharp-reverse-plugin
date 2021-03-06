package com.change_vision.astah.extension.plugin.csharpreverse.reverser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xml.sax.SAXException;

import com.change_vision.jude.api.inf.editor.BasicModelEditor;
import com.change_vision.jude.api.inf.editor.ModelEditorFactory;
import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IAssociation;
import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IClassifierTemplateParameter;
import com.change_vision.jude.api.inf.model.IDependency;
import com.change_vision.jude.api.inf.model.IElement;
import com.change_vision.jude.api.inf.model.IGeneralization;
import com.change_vision.jude.api.inf.model.IModel;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IOperation;
import com.change_vision.jude.api.inf.model.IPackage;
import com.change_vision.jude.api.inf.model.ISubsystem;
import com.change_vision.jude.api.inf.model.ITemplateBinding;
import com.change_vision.jude.api.inf.model.IUsage;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;

/**
 * this class is the tag of **.xml<br />
 * the tag is {@literal <memberdef>}. the class is named Member<br />
 * the tag is {@literal <memberdef kind>}.it is named "kind"<br />
 * the kind type is "function","variable","property","enum"<br />
 * the tag is {@literal <memberdef prot>}.it is named "prot"<br />
 * the tag is {@literal <memberdef static>}.it is named "staticBoolean"<br />
 * the tag is {@literal <memberdef const>}.it is named "constBoolean"<br />
 * the tag is {@literal <memberdef virt>}.it is named "virt"<br />
 * the sub-tag is {@literal <name>}.it is named "name"<br />
 * the sub-tag is {@literal <type>}.it is named "type"<br />
 * the type's sub-tag is {@literal <type ref>}.it is named "typeRef"<br />
 * the sub-tag is {@literal <argsstring>}.it is named "argsstring"<br />
 * the sub-tag is {@literal <initializer>}.it is named "initializer"<br />
 * the initializer's sub-tag is {@literal <initializer ref">}.it is named
 * "initializerRef"<br />
 * the sub-tag is {@literal <detaileddescription>}.it is named
 * "detaileddescription"<br />
 * the tag is {@literal <memberdef gettable>}.it is named "gettable"<br />
 * the tag is {@literal <memberdef settable>}.it is named "settable"<br />
 * memberParaList is the list of the Param.tag is {@literal <param>}<br />
 * enumValues is the list of the EnumValue.tag is {@literal <enumvalue>}<br />
 * Parent is the Section relation<br />
 * it's have Astah C#'field.<br />
 * const,override,readonly,delegate,sealed,internal,unsafe,virtual,abstract
 */
public abstract class Member implements IConvertToJude {
	private static final String PARAMETER_NAME = "parametername_";
	private static final String PARAMETER_DESCRIPTION = "parameterdescription_";
	private String id;
	private String kind;
	private String prot;
	private String staticBoolean;
	private String constBoolean;
	private String virt;
	private String name;
	private String type;
	private List<Ref> typeRefs = new ArrayList<Ref>();
	private String argsstring;
	private String initializer;
	private Ref initializerRef;

	/**
	 * 属性または操作の説明文。ドキュメントコメントではsummaryにあたる。
	 */
	private String briefdescriptionPara;

	/**
	 * 属性または操作の概要。ドキュメントコメントではremarksにあたる。
	 */
	private String detaileddescriptionPara;

	/**
	 * パラメータの情報のリスト。パラメータの名前はPARAMETER_NAME、説明はPARAMETER_DESCRIPTIONが接頭辞につく。
	 */
	private List<String> parameters = new ArrayList<String>();

	/**
	 * 戻り値の説明。
	 */
	private String simplesectPara;

	private String gettable;
	private String settable;
	private List<Param> memberParaList;
	private List<EnumValue> enumValues;
	private Section parent;

	public static final Map<String, String> TYPEDEFS = new HashMap<String, String>();

	public static final String KIND_FUNCTION = "function";

	public static final String KIND_ATTRIBUTE = "variable";

	public static final String KIND_PROPERTY = "property";

	public static final String KIND_ENUM = "enum";

	public static final String KIND_EVENT = "event";

	public static final String AND = "&";

	public static final String STAR = "*";

	public Section getParent() {
		return parent;
	}

	public void setParent(Section parent) {
		this.parent = parent;
	}

	public Member() {
		enumValues = new ArrayList<EnumValue>();
		memberParaList = new ArrayList<Param>();
	}

	public void addEnum(EnumValue newEnum) {
		enumValues.add(newEnum);
	}

	public List<EnumValue> getEnums() {
		return enumValues;
	}

	public void setEnums(List<EnumValue> enums) {
		this.enumValues = enums;
	}

	public String getConstBoolean() {
		return constBoolean;
	}

	public void setConstBoolean(String constBoolean) {
		this.constBoolean = constBoolean;
	}

	public String getArgsstring() {
		return argsstring;
	}

	public void setArgsstring(String argsstring) {
		this.argsstring = argsstring;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getProt() {
		return prot;
	}

	public void setProt(String prot) {
		this.prot = prot;
	}

	public String getStaticBoolean() {
		return staticBoolean;
	}

	public void setStaticBoolean(String staticBoolean) {
		this.staticBoolean = staticBoolean;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getInitializer() {
		return initializer;
	}

	public void setInitializer(String initializer) {
		this.initializer = initializer;
	}

	/**
	 * 属性または操作の説明文を取得します。
	 * 
	 * @return 属性または操作の説明文
	 */
	public String getBriefdescriptionPara() {
		return briefdescriptionPara;
	}

	/**
	 * 属性または操作の説明文を設定します。
	 * 
	 * @param briefdescriptionPara
	 *            属性または操作の説明文
	 */
	public void setBriefdescriptionPara(String briefdescriptionPara) {
		this.briefdescriptionPara = briefdescriptionPara;
	}

	/**
	 * 属性または操作の説明文を取得します。
	 * 
	 * @return 属性または操作の説明文
	 */
	public String getDetaileddescriptionPara() {
		return detaileddescriptionPara;
	}

	/**
	 * 属性または操作の説明文を設定します。
	 * 
	 * @param detaileddescriptionPara
	 *            属性または操作の説明文
	 */
	public void setDetaileddescriptionPara(String detaileddescriptionPara) {
		if (this.detaileddescriptionPara != null) {
			return;
		}
		this.detaileddescriptionPara = detaileddescriptionPara;

	}

	/**
	 * パラメータの名前を接頭辞PARAMETER_NAMEをつけてパラメータのリストに追加します。<br />
	 * 本当はメソッド名をaddParameternames()という名前にしたかったのですが、 Commons Digester のために
	 * setParametername() とします。
	 * 
	 * @param parametername
	 *            パラメータの名前
	 */
	public void setParametername(String parametername) {
		parameters.add(PARAMETER_NAME + parametername);
	}

	/**
	 * パラメータの説明を接頭辞PARAMETER_DESCRIPTIONをつけてパラメータのリストに追加します。<br />
	 * 本当はメソッド名をaddParameterdescriptionParas()という名前にしたかったのですが、 Commons Digester
	 * のために setParameterdescriptionPara() とします。
	 * 
	 * @param parameterdescriptionPara
	 *            パラメータの説明
	 */
	public void setParameterdescriptionPara(String parameterdescriptionPara) {
		parameters.add(PARAMETER_DESCRIPTION + parameterdescriptionPara);
	}

	/**
	 * 戻り値の説明を取得します。
	 * 
	 * @return 戻り値の説明
	 */
	public String getSimplesectPara() {
		return simplesectPara;
	}

	/**
	 * 戻り値の説明を設定します。
	 * 
	 * @param simplesectPara
	 *            戻り値の説明
	 */
	public void setSimplesectPara(String simplesectPara) {
		this.simplesectPara = simplesectPara;
	}

	public String getGettable() {
		return gettable;
	}

	public void setGettable(String gettable) {
		this.gettable = gettable;
	}

	public String getSettable() {
		return settable;
	}

	public void setSettable(String settable) {
		this.settable = settable;
	}

	public Ref getInitializerRef() {
		return initializerRef;
	}

	public void setInitializerRef(Ref initializerRef) {
		this.initializerRef = initializerRef;
	}

	public List<Param> getMemberParaList() {
		return memberParaList;
	}

	public void setMemberParaList(List<Param> memberParaList) {
		this.memberParaList = memberParaList;
	}

	public void addMemberParam(Param memberParam) {
		memberParaList.add(memberParam);
	}

	public List<Ref> getTypeRefs() {
		return typeRefs;
	}

	public void addTypeRef(Ref typeRef) {
		typeRefs.add(typeRef);
	}

	public String getVirt() {
		return virt;
	}

	public void setVirt(String virt) {
		this.virt = virt;
	}

	public List<EnumValue> getEnumValues() {
		return enumValues;
	}

	public void setEnumValues(List<EnumValue> enumValues) {
		this.enumValues = enumValues;
	}

	public String getIClassFullName(IClass cls) {
		return cls.getFullName("::");
	}

	public void convertToJudeModel(IElement parent, File[] files)
			throws InvalidEditingException, ClassNotFoundException,
			ProjectNotFoundException, IOException, SAXException {
		BasicModelEditor basicModelEditor = ModelEditorFactory
				.getBasicModelEditor();
		if (KIND_ATTRIBUTE.equals(this.getKind())) {
			convertToAttributeOrOperation(parent, basicModelEditor);
		} else if (KIND_FUNCTION.equals(this.getKind())) {
			convertToAttributeOrOperation(parent, basicModelEditor);
		} else if (KIND_PROPERTY.equals(this.getKind())) {
			convertToAttributeOrOperation(parent, basicModelEditor);
		} else if (KIND_ENUM.equals(this.getKind())) {
			convertToEnum(parent, files);
		} else if (KIND_EVENT.equals(this.getKind())) {
			convertToAttributeOrOperation(parent, basicModelEditor);
		} else if ("typedef".equals(this.getKind())) {
			String[] split = getType().split(" ");
			TYPEDEFS.put(getName(), split[split.length - 1]);
		} else {
			System.out.println("NO DEAL(KIND)= " + this.getKind());
		}
	}

	protected void convertToEnum(IElement parent, File[] files)
			throws ProjectNotFoundException, ClassNotFoundException,
			InvalidEditingException, IOException, SAXException {
		IClass enumClass;
		if (parent instanceof IPackage) {
			enumClass = Tool.getClass(((IPackage) parent), getName(), null);
		} else {
			enumClass = Tool.getClass(((IClass) parent), getName());
		}
		enumClass.addStereotype("enum");
		CompoundDef.compounddef.put(getId(), enumClass);
		for (EnumValue enumValue : getEnumValues()) {
			enumValue.convertToJudeModel(enumClass, files);
		}
	}

	public static String getTypeFromTypeDef(String name) {
		String type = name;
		while ((type = (String) TYPEDEFS.get(type)) != null) {
			name = type;
		}
		return name;
	}

	protected void convertToAttributeOrOperation(IElement parent,
			BasicModelEditor basicModelEditor) throws InvalidEditingException,
			ProjectNotFoundException, ClassNotFoundException, IOException {
		// 1.5.8 always use String to express type.
		// 1.7.0&1.7.1, C# builtin class no typeRef, just String. user's class
		// use typeref.
		// 1.5.8 getType()="List<String>", getTypeRefs()={}
		// 1.7.0 getType()="IList<String>", getTypeRefs()={}
		// 1.7.0 getType()="MyList<>", getTypeRefs()={Ref(MyChild)}
		// 1.7.1 getType()="IList<String>", getTypeRefs()={}
		// 1.7.1 getType()="IList< >", getTypeRefs()={Ref(MyChild)}
		// 1.7.1 getType()="< >", getTypeRefs()={Ref(MyList), Ref(MyChild)}
		String paramString = "";
		if (getType().indexOf("<") != -1 && getType().indexOf(">") != -1) {
			paramString = getType().substring(getType().indexOf("<") + 1,
					getType().indexOf(">"));
		}
		String[] params = paramString.split(",");
		for (int i = 0; i < params.length; i++) {
			params[i] = params[i].trim();
		}
		IClass templateClass = getTemplateClass(params.length);
		boolean isConvertToAss = true;
		if (KIND_ATTRIBUTE.equals(this.getKind()) && isConvertToAss
				&& isCollectionClass(templateClass)) {
			IClass qualifierType = null;
			IClass iChild = null;
			String[][] multiplicity = null;
			if (isKeyValueCollectionClass(templateClass)) {// IMap<K,V> etc.
				// case: Map<Key, Value> get value
				// 1.5.8
				// type="IMap<String, Child>", typeRefs={}
				// 1.7.0 & 1.7.1
				// type="IMap<String, >", typeRefs={ref(Value)}
				// type="IMap<, String>", typeRefs={ref(Key)}
				// type="IMap< , >", typeRefs={ref(Key), ref(Value)}
				// 1.7.1:
				// type="< , >", typeRefs={ref(MyMap), ref(Key), ref(Value)}
				// type="<String, >", typeRefs={ref(MyMap), ref(Value)}
				// type="< , String>", typeRefs={ref(MyMap), ref(Key)}
				if (params.length == 2) {
					int paramIndex = (getType().indexOf("<") == 0) ? 1 : 0;
					if (params[0].equals("")
							&& paramIndex < getTypeRefs().size()) {
						qualifierType = (IClass) CompoundDef.compounddef
								.get(getTypeRefs().get(paramIndex++).getRefid());
					} else {
						qualifierType = getClassByName(params[0]);
					}
					if (params[1].equals("")) {
						iChild = (IClass) CompoundDef.compounddef
								.get(getTypeRefs().get(paramIndex).getRefid());
					} else {
						iChild = getClassByName(params[1]);
					}
				}
			} else { // ICollection<E> etc.
				// case:List<Child> get Child
				if (getType().indexOf("<") == 0 && getTypeRefs().size() > 1) {
					// 1.7.1: MyList<MyChild>, type="< >", typeRefs={MyList,
					// MyChild}
					iChild = (IClass) CompoundDef.compounddef.get(getTypeRefs()
							.get(1).getRefid());
				} else if (getType().indexOf("<") > 0 && !typeRefs.isEmpty()) {
					// 1.7.0 & 1.7.1, IList<MyChild>, type="IList<>",
					// typeRefs={MyChild}
					iChild = (IClass) CompoundDef.compounddef.get(getTypeRefs()
							.get(0).getRefid());
				} else {
					// type="IList<String>", typeRefs={}
					iChild = getClassByName(params[0]);
				}
				multiplicity = new String[][] { { "*" } };
			}
			if (iChild != null && !isCSharpPrimitive(iChild)
					&& !isCSharpString(iChild) && !isTemplateParameter(iChild)) {
				// create association and set name, constraint, multiplicity,
				// qualifier
				IAssociation ass = generateAssoication((IClass) parent,
						basicModelEditor, iChild);
				IAttribute[] ends = ass.getMemberEnds();
				if (qualifierType != null) {
					basicModelEditor.createQualifier(ends[1], "key",
							qualifierType);
				}
				if (isSortedCollectionClass(templateClass)) {
					basicModelEditor.createConstraint(ends[1], "ordered");
				}
				if (isUniqueCollectionClass(templateClass)) {
					basicModelEditor.createConstraint(ends[1], "unique");
				}
				if (multiplicity != null) {
					ends[1].setMultiplicityStrings(multiplicity);
				}
				return;
			}
		}

		if (getType() != null && !"".equals(getType())
				&& getType().indexOf("<") != 0) {
			// all typerefs are actual parameters
			String array = getArrayString();
			FilterKeyword result = filterKeyword(getType());
			Set<String> keywords = result.keywords;
			String type = (result.toType).trim();
			// like : IDictionary<String, >, ref={Child}
			// or: IDictionary<, >, ref={Key, Child}
			for (int i = 0, index = 0; i < params.length
					&& index < getTypeRefs().size(); i++) {
				if (params[i].equals("")) {
					params[i] = getTypeRefs().get(index++).getValue();
				}
			}
			if (templateClass != null) {
				type = templateClass.getName();
				if (getType().indexOf("<") != -1
						&& getType().indexOf(">") != -1 && params.length > 0) {
					type += "< ";
					for (int i = 0; i < params.length; i++) {
						type += params[i];
						if (i < params.length - 1) {
							type += ",";
						}
					}
					type += " >";
				}
			}
			String filteredType = getTypeFromTypeDef(type);
			String[] splits = (filteredType + array).split(" ");
			if (KIND_ATTRIBUTE.equals(this.getKind())) {
				IAttribute attr = convertAttri(parent, basicModelEditor,
						keywords, splits);
				if (null != attr) {
					IClass attrType = attr.getType();
					if (!"no".equals(this.getStaticBoolean())
							|| isCSharpPrimitive(attrType)
							|| isCSharpString(attrType)
							|| isCSharpSystemClass(attrType)) {
						// do nothing
					} else {
						// convert attribute to association
						IAssociation assoication = generateAssoication(
								(IClass) parent, basicModelEditor, attrType);
						if (getArrayString().indexOf("[") != -1
								&& getArrayString().indexOf("]") != -1) {
							assoication.getMemberEnds()[1]
									.setMultiplicityStrings(new String[][] { { "*" } });
						}
						basicModelEditor.delete(attr);
					}
				}
			} else if (KIND_FUNCTION.equals(this.getKind())
					|| KIND_EVENT.equals(this.getKind())) {
				convertOper(parent, basicModelEditor, keywords, splits);
			} else if (KIND_PROPERTY.equals(this.getKind())) {
				if (this.getName().equals("this")) {
					// indexer: this[int index]
					IOperation op = generateOper(parent, basicModelEditor,
							filteredType.split(" "));
					correctIndexer(basicModelEditor, op);
					String indexerParam = "";
					if (getArgsstring().indexOf("[") != -1
							&& getArgsstring().indexOf("]") != -1) {
						indexerParam = getArgsstring().substring(
								getArgsstring().indexOf("[") + 1,
								getArgsstring().indexOf("]"));
					}
					String[] indexerParams = indexerParam.split(",");
					for (String str : indexerParams) {
						String[] typeAndName = str.split(" ");
						if (typeAndName != null && typeAndName.length > 1) {
							basicModelEditor.createParameter(op,
									typeAndName[1], typeAndName[0]);
						}
					}
				} else {
					convertAttri(parent, basicModelEditor, keywords, splits);
				}
			}
		} else if (!getTypeRefs().isEmpty()) {
			// the first type ref is template
			IClass otherCls = Tool.getClass(getType(), getTypeRefs());
			if (otherCls != null) {
				if (KIND_ATTRIBUTE.equals(this.getKind())) {
					// generateAttri(parent, basicModelEditor, otherCls);
					if (!"no".equals(this.getStaticBoolean())
							|| Config
									.getClassNameAboutForbidCreateAssociation()
									.contains(getTypeRefs().get(0).getValue())
							|| Config
									.getClassNameAboutForbidCreateAssociation()
									.contains(
											INamedElement.class.cast(parent)
													.getFullName("::"))) {

						generateAttri(parent, basicModelEditor, otherCls);
					} else {
						// if not, create association
						generateAssoication((IClass) parent, basicModelEditor,
								otherCls);
					}
				} else if (KIND_PROPERTY.equals(this.getKind())) {
					generateAttri(parent, basicModelEditor, otherCls);
				} else {
					generateOper(parent, basicModelEditor, otherCls);
				}
			} else {
				// System.out.println("Why other class is null?");
			}
		} else if (getType() != null && "".equals(getType())
				&& getTypeRefs().isEmpty()) {
			if (KIND_FUNCTION.equals(this.getKind())
					|| KIND_EVENT.equals(this.getKind())) {
				IOperation oper = generateOper(parent, basicModelEditor,
						(IClass) null);
				if (KIND_EVENT.equals(this.getKind())) {
					oper.addStereotype("event");
				}
			}
		}
	}

	private boolean isTemplateParameter(IClass iChild) {
		if (iChild.getOwner() instanceof IClassifierTemplateParameter) {
			return true;
		}
		return false;
	}

	private boolean isCSharpSystemClass(IClass type) {
		IElement element = type;
		while (true) {
			IElement owner = element.getOwner();
			if (null == owner) {
				return false;
			}
			if (owner instanceof IModel) {
				if (element instanceof IPackage
						&& ((IPackage) element).getName().equals("System")) {
					return true;
				}
				break;
			} else {
				element = owner;
			}
		}
		return false;
	}

	private boolean isCSharpString(IClass iChild) {

		if (iChild.getName().equals("String")
				&& iChild.getOwner() instanceof IPackage) {
			IPackage pkg = (IPackage) iChild.getOwner();
			if (pkg.getName().equals("System")
					&& pkg.getOwner() instanceof IModel // project model
					&& pkg.getOwner().getOwner() == null) {
				return true;
			}
		}
		return false;
	}

	private boolean isCSharpPrimitive(IClass iChild) {

		for (Object obj : LanguageManager.CSHARP_PRIMITIVE_TYPE) {
			if (iChild.getName().equals(obj)) {
				return true;
			}
		}
		return false;
	}

	private IClass getClassByName(String name) throws ProjectNotFoundException,
			ClassNotFoundException {
		IModel project = ProjectAccessorFactory.getProjectAccessor()
				.getProject();
		if (project == null || name == null || name.equals("")) {
			return null;
		}

		for (IClass cls : getClasses(project)) {
			if (cls.getName().equals(name)) {
				return cls;
			}
		}

		return null;
	}

	private List<IClass> getClasses(INamedElement namespace) {
		List<IClass> results = new ArrayList<IClass>();
		INamedElement[] ownedElements = null;
		if (namespace instanceof IPackage) {
			ownedElements = ((IPackage) namespace).getOwnedElements();
		} else if (namespace instanceof IClass) {
			ownedElements = ((IClass) namespace).getNestedClasses();
		} else {
			return results;
		}
		for (INamedElement child : ownedElements) {
			if (child instanceof IPackage || child instanceof IClass) {
				results.addAll(getClasses(child));
			}
			if (child instanceof IClass && !(child instanceof ISubsystem)) {
				results.add((IClass) child);
			}
		}

		return results;
	}

	private IClass getTemplateClass(int paramSize)
			throws ProjectNotFoundException, ClassNotFoundException {
		if (getType() != null && !"".equals(getType())
				&& getType().indexOf("<") > 0) {
			String templateName = getType()
					.substring(0, getType().indexOf("<")).trim();
			IModel project = ProjectAccessorFactory.getProjectAccessor()
					.getProject();
			for (IClass cls : getClasses(project)) {
				if (cls.getName().equals(templateName)
						&& cls.getTemplateParameters().length == paramSize) {
					return cls;
				}
			}
		} else if (!getTypeRefs().isEmpty()) {
			IClass iClass = (IClass) CompoundDef.compounddef.get(getTypeRefs()
					.get(0).getRefid());
			return iClass;
		}
		return null;
	}

	private boolean isKeyValueCollectionClass(IClass cls)
			throws ProjectNotFoundException, ClassNotFoundException {
		String[] collectionNames = { "IDictionary", "Dictionary", "SortedList",
				"SortedDictionary" };
		return isCSharpCollectionClass(cls, collectionNames);
	}

	private boolean isSortedCollectionClass(IClass cls)
			throws ProjectNotFoundException, ClassNotFoundException {
		String[] collectionNames = { "IList", "List", "Queue", "LinkedList",
				"LinkedListNode", "SortedSet", "Stack" };
		return isCSharpCollectionClass(cls, collectionNames);
	}

	private boolean isUniqueCollectionClass(IClass cls)
			throws ProjectNotFoundException, ClassNotFoundException {
		String[] collectionNames = { "ISet", "HashSet", "SortedSet" };
		return isCSharpCollectionClass(cls, collectionNames);
	}

	private boolean isCollectionClass(IClass cls)
			throws ProjectNotFoundException, ClassNotFoundException {
		String[] collectionNames = { "ICollection" };
		if (isCSharpCollectionClass(cls, collectionNames)) {
			return true;
		}
		return isKeyValueCollectionClass(cls) || isSortedCollectionClass(cls)
				|| isUniqueCollectionClass(cls);
	}

	private boolean isCSharpCollectionClass(IClass cls, String[] collectionNames)
			throws ProjectNotFoundException, ClassNotFoundException {
		if (isCollectionByNames(cls, new String[] { "System", "Collections",
				"Generic" }, collectionNames)) {
			return true;
		}
		return false;
	}

	private boolean isCollectionByNames(IClass cls, String[] namespaces,
			String[] collectionNames) throws ProjectNotFoundException,
			ClassNotFoundException {
		if (cls == null || namespaces == null || collectionNames == null) {
			return false;
		}

		// judge is builtin collection class
		if (Arrays.asList(collectionNames).contains(cls.getName())) {
			List<String> namespaceStrings = getNamespaceStrings(cls);
			if (namespaceStrings.equals(Arrays.asList(namespaces))) {
				return true;
			}
		}

		// user defined Collection class, e.g. class MyList extends ArrayList{}
		List<IClass> ancestors = getAncestors(cls, new ArrayList<IClass>());
		for (IClass obj : ancestors) {
			if (isCollectionByNames(obj, namespaces, collectionNames)) {
				return true;
			}
		}
		return false;
	}

	private List<String> getNamespaceStrings(IClass cls)
			throws ProjectNotFoundException, ClassNotFoundException {
		IModel project = ProjectAccessorFactory.getProjectAccessor()
				.getProject();

		List<String> namespaces = new ArrayList<String>();
		for (IElement owner = cls.getOwner(); owner != project; owner = owner
				.getOwner()) {
			if (owner instanceof INamedElement) {
				namespaces.add(0, ((INamedElement) owner).getName());
			} else {
				break;
			}
		}
		return namespaces;
	}

	private List<IClass> getAncestors(IClass child, List<IClass> ancestors) {
		// template binding
		for (ITemplateBinding binding : child.getTemplateBindings()) {
			IClass template = binding.getTemplate();
			if (template != child && !ancestors.contains(template)) {
				ancestors.add(template);
				ancestors = getAncestors(template, ancestors);
			}
		}
		// generalizations
		for (IGeneralization gen : child.getGeneralizations()) {
			IClass superType = gen.getSuperType();
			if (!ancestors.contains(superType)) {
				ancestors.add(superType);
				ancestors = getAncestors(superType, ancestors);
			}
		}
		// realizations
		for (IDependency dep : child.getClientDependencies()) {
			if (dep instanceof IUsage
					&& Arrays.asList(dep.getStereotypes()).contains("realize")) {
				INamedElement o = dep.getSupplier();
				if (o instanceof IClass && o != child && !ancestors.contains(o)) {
					IClass clazz = (IClass) o;
					ancestors.add(clazz);
					ancestors = getAncestors(clazz, ancestors);
				}
			}
		}

		return ancestors;
	}

	private void convertOper(IElement parent,
			BasicModelEditor basicModelEditor, Set<String> keywords,
			String[] splits) throws InvalidEditingException,
			ProjectNotFoundException, ClassNotFoundException {
		IOperation oper = null;
		if (splits.length > 1) {
			StringBuilder sb = new StringBuilder();
			for (int i = 1; i < splits.length; i++) {
				sb.append(splits[i]);
			}
			int endIndex = sb.toString().indexOf(">");
			if (splits[0].endsWith("<") && (endIndex != -1)) {
				String clsName = splits[0].substring(0, splits[0].length() - 1);
				String[] splits2 = clsName.split("\\.");
				splits2[splits2.length - 1] += "<"
						+ sb.toString().substring(0, endIndex) + ">";
				oper = generateOper(parent, basicModelEditor, splits2);
			} else {
				oper = generateOper(parent, basicModelEditor,
						splits[0].split("\\."));
			}
		} else {
			oper = generateOper(parent, basicModelEditor,
					splits[0].split("\\."));
		}
		dealOperationKeyword(basicModelEditor, keywords, oper);

		for (Param param : getMemberParaList()) {
			param.convertToJudeModel(oper, null);
		}
	}

	private IAttribute convertAttri(IElement parent,
			BasicModelEditor basicModelEditor, Set<String> keywords,
			String[] splits) throws InvalidEditingException,
			ProjectNotFoundException, ClassNotFoundException {
		IAttribute attr = null;
		if (splits.length > 1) {
			StringBuilder sb = new StringBuilder();
			for (int i = 1; i < splits.length; i++) {
				sb.append(splits[i]);
			}
			int endIndex = sb.toString().indexOf(">");
			if (splits[0].endsWith("<") && (endIndex != -1)) {
				String clsName = splits[0].substring(0, splits[0].length() - 1);
				String[] splits2 = clsName.split("\\.");
				splits2[splits2.length - 1] += "<"
						+ sb.toString().substring(0, endIndex) + ">";
				attr = generateAttri(parent, basicModelEditor, splits2);
			} else {
				attr = generateAttri(parent, basicModelEditor,
						splits[0].split("\\."));
			}
		} else {
			attr = generateAttri(parent, basicModelEditor,
					splits[0].split("\\."));
		}
		if (null == attr) {
			return null;
		}
		correctProperty(basicModelEditor, attr);
		dealAttributeKeywords(basicModelEditor, keywords, attr);
		return attr;
	}

	private void correctProperty(BasicModelEditor basicModelEditor,
			IAttribute attr) throws InvalidEditingException {
		if (KIND_PROPERTY.equals(this.getKind())) {
			if (!Arrays.asList(attr.getStereotypes()).contains("property")) {
				attr.addStereotype("property");
				if ("yes".equals(getSettable())) {
					basicModelEditor.createTaggedValue(attr,
							"jude.c_sharp.property_set", "true");
				} else {
					basicModelEditor.createTaggedValue(attr,
							"jude.c_sharp.property_set", "false");
				}
				if ("yes".equals(getGettable())) {
					basicModelEditor.createTaggedValue(attr,
							"jude.c_sharp.property_get", "true");
				} else {
					basicModelEditor.createTaggedValue(attr,
							"jude.c_sharp.property_get", "false");
				}
			}
		}
	}

	private void correctIndexer(BasicModelEditor basicModelEditor,
			IOperation attr) throws InvalidEditingException {
		if (KIND_PROPERTY.equals(this.getKind())) {
			if (!Arrays.asList(attr.getStereotypes()).contains("indexer")) {
				attr.addStereotype("indexer");
				if ("yes".equals(getSettable())) {
					basicModelEditor.createTaggedValue(attr,
							"jude.c_sharp.indexer_set", "true");
				} else {
					basicModelEditor.createTaggedValue(attr,
							"jude.c_sharp.indexer_set", "false");
				}
				if ("yes".equals(getGettable())) {
					basicModelEditor.createTaggedValue(attr,
							"jude.c_sharp.indexer_get", "true");
				} else {
					basicModelEditor.createTaggedValue(attr,
							"jude.c_sharp.indexer_get", "false");
				}
			}
		}
	}

	abstract void dealOperationKeyword(BasicModelEditor basicModelEditor,
			Set<String> keywords, IOperation fun)
			throws InvalidEditingException;

	abstract void dealAttributeKeywords(BasicModelEditor basicModelEditor,
			Set<String> keywords, IAttribute attr)
			throws InvalidEditingException;

	abstract FilterKeyword filterKeyword(String type);

	private IOperation generateOper(IElement parent,
			BasicModelEditor basicModelEditor, IClass type)
			throws InvalidEditingException, ProjectNotFoundException,
			ClassNotFoundException {
		IOperation oper = Tool.getOperation((IClass) parent, getName(), type);
		oper.setVisibility(this.getProt());
		oper.setStatic(!"no".equals(this.getStaticBoolean()));
		oper.setDefinition(getPara());
		for (Param param : getMemberParaList()) {
			param.convertToJudeModel(oper, null);
		}
		return oper;
	}

	private IOperation generateOper(IElement parent,
			BasicModelEditor basicModelEditor, String[] path)
			throws InvalidEditingException, ProjectNotFoundException,
			ClassNotFoundException {
		String[] namespace = new String[] {};
		if (path.length > 1) {
			namespace = new String[path.length - 1];
			System.arraycopy(path, 0, namespace, 0, path.length - 1);
		}
		IOperation oper;
		if (LanguageManager.getCurrentLanguagePrimitiveType().contains(path[0])) {
			oper = Tool.getOperation((IClass) parent, getName(), path[0]);
		} else {
			oper = Tool.getOperation((IClass) parent, getName(),
					path.length > 0 ? path[path.length - 1] : getType());
		}
		oper.setVisibility(this.getProt());
		oper.setStatic(!"no".equals(this.getStaticBoolean()));
		oper.setDefinition(getPara());

		return oper;
	}

	private void generateAttri(IElement parent,
			BasicModelEditor basicModelEditor, IClass type)
			throws InvalidEditingException, ProjectNotFoundException,
			ClassNotFoundException {
        IAttribute attr = Tool.getAttribute((IClass) parent, getName(), type,
                KIND_PROPERTY.equals(this.getKind()));
		int[][] range = getMultiRange();
		if (range != null) {
			attr.setMultiplicity(range);
		} else {
			String[][] rangeStrings = getMultiRangeStrings();
			if (rangeStrings != null) {
				attr.setMultiplicityStrings(rangeStrings);
			}
		}
		attr.setChangeable(!"no".equals(this.getConstBoolean()));
		attr.setVisibility(this.getProt());
		attr.setStatic(!"no".equals(this.getStaticBoolean()));
		attr.setDefinition(getPara());
		attr.setInitialValue(this.getInitializer());
		correctProperty(basicModelEditor, attr);
	}

	/**
	 * ドキュメントコメントを取得します。
	 * 
	 * @return ドキュメントコメント
	 */
	private String getPara() {
		String string = new String();
		if (this.getBriefdescriptionPara() != null) {
			string = getLineBreak(string) + "<summary>"
					+ this.getBriefdescriptionPara() + "</summary>";
		}
		if (this.getDetaileddescriptionPara() != null) {
			string = getLineBreak(string) + "<remarks>"
					+ this.getDetaileddescriptionPara() + "</remarks>";
		}

		Map<String, String> map = new HashMap<String, String>();
		List<String> keys = new ArrayList<String>();
		for (int i = 0; i < parameters.size(); i++) {
			String firstParameter = parameters.get(i);
			if (!firstParameter.startsWith(PARAMETER_NAME)) {
				continue;
			}
			String name = firstParameter.replaceFirst(PARAMETER_NAME, "");
			String description = "";
			if (i < parameters.size() - 1) {
				String secondParameter = parameters.get(i + 1);
				if (secondParameter.startsWith(PARAMETER_DESCRIPTION)) {
					description = secondParameter.replaceFirst(
							PARAMETER_DESCRIPTION, "");
				}
			}
			map.put(name, description);
			keys.add(name);
		}

		for (String key : keys) {
			string = getLineBreak(string) + "<param name=\"" + key + "\">"
					+ map.get(key) + "</param>";
		}
		if (this.getSimplesectPara() != null) {
			string = getLineBreak(string) + "<returns>"
					+ this.getSimplesectPara() + "</returns>";
		}
		return string;
	}

	/**
	 * 引数が空文字列出なければ、改行コードをつけた文字列を返します。
	 * 
	 * @param string
	 *            文字列
	 * @return 末尾に改行をつけた文字列　または　文字列
	 */
	private String getLineBreak(String string) {
		if (!string.isEmpty()) {
			return string + "\n";
		}
		return string;
	}

	private IAttribute generateAttri(IElement parent,
			BasicModelEditor basicModelEditor, String[] path)
			throws InvalidEditingException, ProjectNotFoundException,
			ClassNotFoundException {
		String[] namespace = new String[] {};
		if (path.length > 1) {
			namespace = new String[path.length - 1];
			System.arraycopy(path, 0, namespace, 0, path.length - 1);
		}
		IAttribute attr;
		if (LanguageManager.getCurrentLanguagePrimitiveType().contains(path[0])) {
            attr = Tool.getAttribute((IClass) parent, getName(), path[0],
                    KIND_PROPERTY.equals(this.getKind()));
		} else {
            attr = Tool.getAttribute((IClass) parent, getName(),
                    path.length > 0 ? path[path.length - 1] : getType(),
                    KIND_PROPERTY.equals(this.getKind()));
		}
		if (null == attr) {
			return null;
		}
		attr.setChangeable(!"no".equals(this.getConstBoolean()));
		attr.setVisibility(this.getProt());
		attr.setStatic(!"no".equals(this.getStaticBoolean()));
		attr.setDefinition(getPara());
		attr.setInitialValue(this.getInitializer());

		return attr;
	}

	private IAssociation generateAssoication(IClass parent,
			BasicModelEditor basicModelEditor, IClass assocEnd)
			throws InvalidEditingException, ProjectNotFoundException,
			ClassNotFoundException {
		IAssociation association = basicModelEditor.createAssociation(parent,
				assocEnd, "", "", this.getName());

		IAttribute attribute = association.getMemberEnds()[1];
		attribute.setVisibility(this.getProt());

		attribute.setDefinition(getPara());

		return association;
	}

	protected String getArrayString() {
		String arrayString = "";
		if (type.indexOf("[") >= 0) {
			arrayString = getType();
		} else if (this.getArgsstring().indexOf("[") == 0) {
			arrayString = this.getArgsstring();
		}
		if (!"".equals(arrayString)) {
			int start = arrayString.indexOf("[");
			int end = arrayString.lastIndexOf("]");
			return arrayString.substring(start, end + 1);
		}
		return arrayString;
	}

	int[][] getMultiRange() {
		StringBuilder buffer = new StringBuilder();
		int length = 0;
		String arrayString;
		if (getType().indexOf("[") >= 0) {
			arrayString = getType();
		} else if (this.getArgsstring().indexOf("[") >= 0) {
			arrayString = this.getArgsstring();
		} else {
			return null;
		}
		while (arrayString.indexOf("[") >= 0) {
			int beginIndex = arrayString.indexOf("[");
			int endIndex = arrayString.indexOf("]");
			if (endIndex - beginIndex >= 1) {
				String num = arrayString.substring(beginIndex + 1, endIndex);
				try {
					Integer.parseInt(num);
				} catch (NumberFormatException e) {
					return null;
				}
				buffer.append(num);
				buffer.append(" ,");
				length++;
			}
			arrayString = arrayString.substring(endIndex + 1);
		}
		if (length == 0) {
			return null;
		}
		String[] mliti = buffer.toString().split(",");
		int[][] range = new int[length][1];
		for (int i = 0; i < length; i++) {
			int value = -100;
			try {
				value = Integer.valueOf(mliti[i].trim()).intValue();
			} catch (NumberFormatException e) {
				return null;
			}
			range[i][0] = value;
		}
		return range;
	}

	String[][] getMultiRangeStrings() {
		StringBuilder buffer = new StringBuilder();
		int length = 0;
		String arrayString;
		if (getType().indexOf("[") >= 0) {
			arrayString = getType();
		} else if (this.getArgsstring().indexOf("[") >= 0) {
			arrayString = this.getArgsstring();
		} else {
			return null;
		}
		while (arrayString.indexOf("[") >= 0) {
			int beginIndex = arrayString.indexOf("[");
			int endIndex = arrayString.indexOf("]");
			if (endIndex - beginIndex >= 1) {
				String num = arrayString.substring(beginIndex + 1, endIndex);
				try {
					Integer.parseInt(num);
				} catch (NumberFormatException e) {
					return null;
				}
				buffer.append(num);
				buffer.append(" ,");
				length++;
			}
			arrayString = arrayString.substring(endIndex + 1);
		}
		if (length == 0) {
			return null;
		}
		String[] mliti = buffer.toString().split(",");
		String[][] range = new String[length][1];
		for (int i = 0; i < length; i++) {
			range[i][0] = mliti[i].trim();
		}
		return range;
	}

}