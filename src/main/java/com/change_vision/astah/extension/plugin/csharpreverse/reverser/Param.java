package com.change_vision.astah.extension.plugin.csharpreverse.reverser;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.change_vision.jude.api.inf.editor.BasicModelEditor;
import com.change_vision.jude.api.inf.editor.ModelEditorFactory;
import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IElement;
import com.change_vision.jude.api.inf.model.IOperation;

/**
 * 
 * this class is the tag of index.xml the tag is <param>. the class is named
 * Param the sub-tag is <type>.it is named "type" the sub-tag is <declname>.it
 * is named "declname" the sub-tag is <defname>.it is named "defname" the
 * sub-tag is <array>.it is named "array" it's have Astah C#'s field ref,out
 */
public class Param implements IConvertToJude {

	private String type;

	private List<Ref> typeRefs = new ArrayList<Ref>();

	private String declname;

	private String defname;

	private String array;

	private static int paramNum = 0;

	public static final String REF = "ref";

	public static final String OUT = "out";

	public static final String IN = "in";

	public static final String PARAMETERS = "params";

	public static final String THIS = "this";

	public static final String AND = "&";

	public static final String STAR = "*";

    private boolean isTypeCreatedFirst = true;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
        this.isTypeCreatedFirst = typeRefs == null || typeRefs.isEmpty();
	}

	public List<Ref> getTypeRefs() {
		return typeRefs;
	}

	public void setTypeRefs(List<Ref> typeRefs) {
		this.typeRefs = typeRefs;
        this.isTypeCreatedFirst = type != null;
	}

	public void addTypeRef(Ref typeRef) {
		this.typeRefs.add(typeRef);
        this.isTypeCreatedFirst = type != null;
	}

	public String getDeclname() {
		return declname;
	}

	public void setDeclname(String declname) {
		this.declname = declname;
	}

	public String getArray() {
		return array;
	}

	public void setArray(String array) {
		this.array = array;
	}

	public String getDefname() {
		return defname;
	}

	public void setDefname(String defname) {
		this.defname = defname;
	}

	public void convertToJudeModel(IElement parent, File[] files)
			throws InvalidEditingException, ClassNotFoundException,
			ProjectNotFoundException {

//		if (typeRefs.isEmpty()) {
//			System.out.println("Type:" + type + " declname:" + declname
//					+ " defname:" + defname + " array:" + array);
//		} else {
//			System.out.println("Type:" + type + " kindRef:"
//					+ typeRefs.get(0).getKindref() + " refid:"
//					+ typeRefs.get(0).getRefid() + " value:"
//					+ typeRefs.get(0).getValue() + " declname:" + declname
//					+ " defname:" + defname + " array:" + array);
//		}

		FilterKeyword result = filterKeyword(type);
		String type = (result.toType).trim();
		if ("".equals(type) && !typeRefs.isEmpty()) {
			type = typeRefs.get(0).value;
		} else if (null != type) {
            if (this.isTypeCreatedFirst) {
                int begin = type.indexOf("<");
                int refIndex = 0;
                if (0 <= begin) {
                    while (true) {
                        ++begin;
                        int end = type.indexOf(",", begin);
                        if (0 > end) {
                            end = type.indexOf(">", begin);
                        }
                        if (0 > end) {
                            end = type.length();
                        }
                        if (begin > end) {
                            break;
                        }
                        String theTypeStr = type.substring(begin, end);
                        if ("".equals(theTypeStr.trim())) {
                            if (!typeRefs.isEmpty() && typeRefs.size() > refIndex) {
                                StringBuilder sb = new StringBuilder(type);
                                sb.insert(begin, typeRefs.get(refIndex).value);
                                type = sb.toString();
                                ++refIndex;
                            }
                        }
                        begin = end;
                    }
                }
            } else {
                StringBuilder sb = new StringBuilder();
                for (Ref ref : typeRefs) {
                    sb.append(ref.value);
                }
                sb.append(type);
                type = sb.toString();
            }
		}
		checkBoundClass(parent, type);
		Object name = getParamTypeName(type);
		if (type.indexOf(".") != -1) {
			String[] split = type.split("\\.");
			List<String> allPath = Arrays.asList(split);
			name = Tool.getClass(allPath.subList(0, allPath.size() - 1)
					.toArray(new String[] {}), split[split.length - 1], null);
		}
		String paramArray = "";
		String paramName = null;
		if (array != null) {
			paramArray = array;
		} else if (this.type.endsWith("[]")) {
			// to fix dev bug
			// 1276:[C#][Doxygen]5.4\54_09_doxygen\C_Sharp\C_Sharp_6.3_1.7.1.astaでメソッドのパラメタがおかしい。
			// 1次元配列のときのみ、typeに[]がはいってしまう。
			paramArray = "[]";
			type = this.type.substring(0, this.type.length() - "[]".length());
			if(type.isEmpty()){
				type = typeRefs.get(0).getValue();
			}
		}
		if (declname != null) {
			paramName = declname;
		}
		if (defname != null) {
			paramName = defname;
		}
		if (paramName == null) {
			paramName = "param" + paramNum++;
		}
		IElement param;
		if (name instanceof String)
			param = createParameter(parent, type, paramArray, paramName);
		else {
			BasicModelEditor basicModelEditor = ModelEditorFactory
					.getBasicModelEditor();
			param = basicModelEditor.createParameter(((IOperation) parent),
					paramName, (IClass) name);
		}
		dealKeyword(param, result.keywords);
	}

	private void checkBoundClass(IElement parent, String origiName)
			throws ProjectNotFoundException, ClassNotFoundException,
			InvalidEditingException {
		int beginIndex = origiName.indexOf("<");
		int endIndex = origiName.lastIndexOf(">");
		if (beginIndex == -1 || endIndex == -1) {
			return;
		}
		String templateName = origiName.substring(0, beginIndex);
		String[] paths = templateName.split("\\.");
		String[] namespace = new String[paths.length - 1];
		System.arraycopy(paths, 0, namespace, 0, paths.length - 1);
		Tool.getClass(namespace, origiName, (IClass) parent.getOwner());
	}

	private String getParamTypeName(String origiName) {
		int beginIndex = origiName.indexOf("<");
		int endIndex = origiName.lastIndexOf(">");
		if (beginIndex == -1 || endIndex == -1) {
			return getEndPath(origiName);
		}
		String templateName = getEndPath(origiName.substring(0, beginIndex));
		templateName += "<";
		String[] contents = origiName.substring(beginIndex + 1, endIndex)
				.split(",");
		for (int i = 0; i < contents.length; i++) {
			templateName += getEndPath(contents[i]).trim();
		}
		templateName += ">";
		return templateName;
	}

	private String getEndPath(String path) {
		String[] paths = path.split("\\.");
		return paths[paths.length - 1];
	}

	/**
	 * 
	 * @param param
	 *            : IElement
	 * @param keywords
	 *            : HashSet
	 * @return void
	 * @throws InvalidEditingException
	 */
	void dealKeyword(IElement param, Set<String> keywords)
			throws InvalidEditingException, ClassNotFoundException {
	}

	/**
	 * 
	 * @param toType
	 *            : String
	 * @return Object[]
	 */
	FilterKeyword filterKeyword(String toType) {
		Set<String> keywords = new HashSet<String>();
		if (toType.indexOf(REF) != -1) {
			toType = toType.replaceFirst(REF + " ", "");
			keywords.add(REF);
		}
		if (toType.indexOf(OUT) != -1) {
			toType = toType.replaceFirst(OUT + " ", "");
			keywords.add(OUT);
		}
		if (toType.indexOf(IN) != -1) {
			toType = toType.replaceFirst(IN + " ", "");
			keywords.add(IN);
		}
		if (toType.indexOf(PARAMETERS) != -1) {
			toType = toType.replaceFirst(PARAMETERS + " ", "");
			keywords.add(PARAMETERS);
		}
		if (toType.indexOf(THIS) == 0) {
			toType = toType.replaceFirst(THIS + " ", "");
			keywords.add(THIS);
		}
		if (toType.trim().indexOf(AND) != -1) {
			toType = toType.replaceFirst(AND, "").trim();
			toType = toType.replaceFirst(" ", "");
			keywords.add(AND);
		}
		if (toType.trim().indexOf(STAR + STAR) != -1) {
			toType = toType.replaceFirst("\\*" + "\\*", "").trim();
			toType = toType.replaceFirst(" ", "");
			keywords.add(STAR + STAR);
		}
		if (toType.trim().indexOf(STAR) != -1) {
			toType = toType.replaceFirst("\\*", "").trim();
			toType = toType.replaceFirst(" ", "");
			keywords.add(STAR);
		}
		if (toType.indexOf("< ") != -1) {
			toType = toType.replaceFirst("< ", "<");
			keywords.add("< ");
		}
		if (toType.indexOf(" >") != -1) {
			toType = toType.replaceFirst(" >", ">");
			keywords.add(" >");
		}
		// if (toType.indexOf("[") != -1) {
		// toType = toType.substring(0, toType.indexOf("["));
		// }
		return new FilterKeyword(keywords, toType);
	}

	/**
	 * 
	 * @param parent
	 *            : IElement
	 * @param names
	 *            : String[]
	 * @param paramArray
	 *            : String
	 * @param paramName
	 *            : String
	 * @return IElement
	 * @throws InvalidEditingException
	 * @throws ClassNotFoundException
	 * @throws ProjectNotFoundException
	 */
	protected IElement createParameter(IElement parent, String name,
			String paramArray, String paramName)
			throws InvalidEditingException, ProjectNotFoundException,
			ClassNotFoundException {
		BasicModelEditor basicModelEditor = ModelEditorFactory
				.getBasicModelEditor();
		return basicModelEditor.createParameter(((IOperation) parent),
				paramName, name + paramArray);
	}

	/**
	 * 
	 * @param names
	 *            : String[]
	 * @return List
	 */
	protected List<String> convertPath(String[] names) {
		List<String> path = new ArrayList<String>();
		if (names.length > 1) {
			for (int i = 0; i < (names.length - 1); i++) {
				path.add(names[i]);
			}
		}
		return path;
	}
}