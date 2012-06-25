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
 * this class is the tag of index.xml
 *  the tag is <param>. the class is named Param
 *  the sub-tag is <type>.it is named "type"
 *  the sub-tag is <declname>.it is named "declname"
 *  the sub-tag is <defname>.it is named "defname"
 *  the sub-tag is <array>.it is named "array"
 *  it's have Astah C#'s field
 *  ref,out
 */
public class Param implements IConvertToJude {
	
	protected String type;
	
	protected Ref typeRef;
	
	protected String declname;
	
	protected String defname;
	
	protected String array;
	
	protected static int paramNum = 0;
	
	public static final String REF = "ref";
	
	public static final String OUT = "out";
	
	public static final String IN = "in";
	
	public static final String PARAMETERS = "params";
	
	public static final String THIS = "this";
	
	public static final String AND = "&";
	
	public static final String STAR = "*";
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Ref getTypeRef() {
		return typeRef;
	}

	public void setTypeRef(Ref typeRef) {
		this.typeRef = typeRef;
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
		throws InvalidEditingException,	ClassNotFoundException,	ProjectNotFoundException {
		Object[] result = filterKeyword(type);
		String type = ((String) result[1]).trim();
		if ("".equals(type) && typeRef != null) {
			type = typeRef.value;
		}
		checkBoundClass(parent, type);
		Object name = getParamTypeName(type);
		if (type.indexOf(".") != -1) {
			String[] split = type.split("\\.");
			List allPath = Arrays.asList(split);
			name = Tool.getClass((String[]) allPath.subList(0, allPath.size() - 1).toArray(new String[] {})
					, split[split.length - 1], null);
		}
		String paramArray = "";
		String paramName = null;
		if (array != null) {
			paramArray = array;
		} else if (this.type.endsWith("[]")) {
			//to fix dev bug 1276: [C#][Doxygen]5.4\54_09_doxygen\C_Sharp\C_Sharp_6.3_1.7.1.astaでメソッドのパラメタがおかしい。
			paramArray = "[]";
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
			param = createParameter(parent, type.toString(), paramArray, paramName);
		else {
			BasicModelEditor basicModelEditor = ModelEditorFactory.getBasicModelEditor();
			param = basicModelEditor.createParameter(((IOperation) parent), paramName, (IClass) name);
		}
		dealKeyword(param, (HashSet) result[0]);
	}
	
	private void checkBoundClass(IElement parent, String origiName) throws ProjectNotFoundException, ClassNotFoundException, InvalidEditingException {
		int beginIndex = origiName.indexOf("<");
		int endIndex = origiName.lastIndexOf(">");
		if (beginIndex == -1 || endIndex == -1) {
			return;
		}
		String templateName = origiName.substring(0, beginIndex);
		String[] paths = templateName.split("\\.");
		String[] namespace = new String[paths.length - 1];
		System.arraycopy(paths, 0, namespace, 0, paths.length - 1);
		Tool.getClass(namespace, origiName, (IClass)parent.getOwner());
	}
	
	private String getParamTypeName(String origiName) {
		int beginIndex = origiName.indexOf("<");
		int endIndex = origiName.lastIndexOf(">");
		if (beginIndex == -1 || endIndex == -1) {
			return getEndPath(origiName);
		}
		String templateName = getEndPath(origiName.substring(0, beginIndex));
		templateName += "<";		
		String[] contents = origiName.substring(beginIndex + 1, endIndex).split(",");
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
	 * @param param: IElement
	 * @param keywords : HashSet
	 * @return void
	 * @throws InvalidEditingException
	 */
	void dealKeyword(IElement param, HashSet keywords) throws InvalidEditingException, ClassNotFoundException {}

	/**
	 * 
	 * @param toType: String
	 * @return Object[]
	 */
	Object[] filterKeyword(String toType) {
		Set keywords = new HashSet();
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
		if (toType.indexOf("[") != -1) {
			toType = toType.substring(0, toType.indexOf("["));
		}
		return new Object[] {keywords, toType};
	}
	
	/**
	 * 
	 * @param parent: IElement
	 * @param names : String[]
	 * @param paramArray : String
	 * @param paramName : String
	 * @return IElement
	 * @throws InvalidEditingException
	 * @throws ClassNotFoundException
	 * @throws ProjectNotFoundException
	 */
	protected IElement createParameter(IElement parent, String name, String paramArray, String paramName)
		throws InvalidEditingException, ProjectNotFoundException, ClassNotFoundException {
		BasicModelEditor basicModelEditor = ModelEditorFactory.getBasicModelEditor();
		return basicModelEditor.createParameter(((IOperation) parent), paramName
				, name + paramArray);
	}
	
	/**
	 * 
	 * @param names : String[]
	 * @return List
	 */
	protected List convertPath(String[] names) {
		List path = new ArrayList();
		if (names.length > 1) {
			for (int i = 0; i < (names.length - 1); i++) {
				path.add(names[i]);
			}
		}
		return path;
	}
}