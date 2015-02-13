package com.change_vision.astah.extension.plugin.csharpreverse.reverser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.change_vision.jude.api.inf.editor.BasicModelEditor;
import com.change_vision.jude.api.inf.editor.ModelEditorFactory;
import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IClassifierTemplateParameter;
import com.change_vision.jude.api.inf.model.IElement;
import com.change_vision.jude.api.inf.model.IGeneralization;
import com.change_vision.jude.api.inf.model.IModel;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IOperation;
import com.change_vision.jude.api.inf.model.IPackage;
import com.change_vision.jude.api.inf.model.IParameter;
import com.change_vision.jude.api.inf.model.ITemplateBinding;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;


public class Tool {
    private static final Logger logger = LoggerFactory.getLogger(Tool.class);    
	
	public static List<IClass> GlobalList = new ArrayList<IClass>();
	/**
	 * get package from the path, if not exits, will create one.
	 * @param path: the full path of the package
	 * @return IPackage of the path
	 */
	public static IPackage getPackage(String[] path) throws ProjectNotFoundException,
			ClassNotFoundException, InvalidEditingException {
		IModel project = ProjectAccessorFactory.getProjectAccessor()
				.getProject();
		IPackage result = project;
		for (int i = 0; i < path.length; i++) {
			result = getPackage(result, path[i]);
		}
		return result;
	}

	/**
	 * get package under parent, if not exits, will create one,
	 * @param parent: the parent package
	 * @param path: the name of the package
	 * @return the package under the parent, and the name is path
	 */
	static IPackage getPackage(IPackage parent, String path)
			throws ProjectNotFoundException, ClassNotFoundException,
			InvalidEditingException {
		INamedElement[] array = parent.getOwnedElements();
		for (int i = 0; i < array.length; i++) {
			if (array[i].getName().equals(path) && array[i] instanceof IPackage) {
				return (IPackage) array[i];
			}
		}
		return ModelEditorFactory.getBasicModelEditor().createPackage(parent,
				path);
	}
	
	public static IClass getClass(String type, List<Ref> typeRefs) throws InvalidEditingException, ClassNotFoundException, ProjectNotFoundException {
		if (typeRefs.isEmpty()) {
			return null;
		}
		IClass templateClass = (IClass) CompoundDef.compounddef.get(typeRefs.get(0).getRefid());
		if (templateClass == null) {
			return null;
		}
		if (type.indexOf("<") == -1) {
			return templateClass;
		}
		String paramString = type.substring(type.indexOf("<") + 1, type.lastIndexOf(">"));
		String[] params = paramString.split(",");
		
		Object[][] actualClasses = new Object[params.length][];
		for (int i = 0, index = 1; i < params.length; i++) {
			Object actual = null;
			if (!params[i].trim().equals("")) {
				actual = params[i].trim();
			} else if (index < typeRefs.size()
					&& CompoundDef.compounddef.containsKey(typeRefs.get(index).getRefid())) {
				actual = CompoundDef.compounddef.get(typeRefs.get(index++).getRefid());
			}
			actualClasses[i] = new Object[] {actual, ""};
		}
		//find existed anonymous bound class
		ITemplateBinding[] tBindings = templateClass.getTemplateBindings();
		for (int i = 0; i < tBindings.length; i++) {
			ITemplateBinding tBinding = tBindings[i];
			IClass boundClass = tBinding.getBoundElement();
			if (boundClass.getName().equals("") && matchesAllActualValues(tBinding, actualClasses)) {
				return boundClass;
			}
		}
		//create anonymous bound class
		BasicModelEditor basicModelEditor = ModelEditorFactory.getBasicModelEditor();
		IClassifierTemplateParameter[] tParams = templateClass.getTemplateParameters();
		if (tParams.length < actualClasses.length) {
			IClass paraType = null;
			for (int i = 0; i < actualClasses.length - tParams.length; i++) {
				try {
                    basicModelEditor.createTemplateParameter(templateClass, "param" + i, paraType, null);
                } catch (InvalidEditingException e) {
                    if (InvalidEditingException.NAME_DOUBLE_ERROR_KEY.equals(e.getKey())) {
                        // do nothing
                        logger.debug(String.format("%s", e.getMessage()));
                    }
                }
			}
		}
		tParams = templateClass.getTemplateParameters();
		ProjectAccessor prjAccessor = ProjectAccessorFactory.getProjectAccessor();
		IModel project = prjAccessor.getProject();
		String tempName = getUniqueName(project);
		IClass anonimousClass = basicModelEditor.createClass(project, tempName);
		ITemplateBinding binding = null;
        try {
            binding = basicModelEditor.createTemplateBinding(anonimousClass, templateClass);
        } catch (InvalidEditingException e) {
            if(InvalidEditingException.TEMPLATE_BINDING_LOOP_ERROR_KEY.equals(e.getKey())) {
                logger.debug(e.getMessage());
            }
        }
        if (null == binding) {
            return null;
        }
		for (int i = 0; i < actualClasses.length; i++) {
			IClassifierTemplateParameter param = tParams[i];
			binding.addActualParameter(param, actualClasses[i][0]);
			if (!"".equals(actualClasses[i][1])) {
				binding.setActualParameterTypeModifier(param, actualClasses[i][1].toString());
			}
		}			
		anonimousClass.setName("");
		return anonimousClass;
	}
	
	private static String getUniqueName(IPackage parent) {
		String initName = "anonymousboundclass";
		Set<String> names = new HashSet<String>();
		for (Object obj : parent.getOwnedElements()) {
			if (obj instanceof INamedElement) {
				names.add(((INamedElement) obj).getName());
			}
		}
		for (int i = 0; ; i++) {
			String name = initName + i;
			if (!names.contains(name)) {
				return name;
			}
		}
	}

	private static boolean matchesAllActualValues(ITemplateBinding binding, Object[] actualClasses) throws InvalidEditingException {
		List<IClassifierTemplateParameter> matchedParams = new ArrayList<IClassifierTemplateParameter>();
		@SuppressWarnings("unchecked")
        Map<Object, Object> actualMap = binding.getActualMap();
		for (int i = 0; i < actualClasses.length; i++) {
			Object[] actual = (Object[])actualClasses[i];
			for (Iterator<Object> it = actualMap.keySet().iterator(); it.hasNext(); ) {
				IClassifierTemplateParameter param = (IClassifierTemplateParameter)it.next();				
				if (actualMap.get(param).equals(actual[0])
				      && actual[1].equals(binding.getActualParameterTypeModifier(param))
				      && !matchedParams.contains(param)) {
					matchedParams.add(param);
					break;
				}
			}
		}
		if (matchedParams.size() == actualClasses.length) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * get class named className under the allPath
	 * @param allPath: the class's package's path
	 * @param className: the class's name
	 * @return the class named className under the allpath 
	 */
	public static IClass getClass(String[] allPath, String className, IClass owner) throws ProjectNotFoundException, ClassNotFoundException, InvalidEditingException {
		IPackage iPkg = getPackage(allPath);
		return getClass(iPkg, className, owner);
	}

	public static IClass getInterface(IClass parent, String className) throws ProjectNotFoundException, ClassNotFoundException, InvalidEditingException {
		IClass[] nestClasses = parent.getNestedClasses();
		for (int i = 0; i < nestClasses.length; i++) {
			if (nestClasses[i].getName().equals(className)) {
				return nestClasses[i];
			}
		}
		return setLanguage(ModelEditorFactory.getBasicModelEditor().createInterface(parent, className));
	}
	
	public static IClass getInterface(IPackage parent, String className) throws ProjectNotFoundException, ClassNotFoundException, InvalidEditingException {
		INamedElement[] nestClasses = parent.getOwnedElements();
		for (int i = 0; i < nestClasses.length; i++) {
			if (nestClasses[i].getName().equals(className) && nestClasses[i] instanceof IClass) {
				return (IClass) nestClasses[i];
			}
		}
		return setLanguage(ModelEditorFactory.getBasicModelEditor().createInterface(parent, className));
	}
	
	/**
	 * get the InnerClass named className under parentclass 
	 * @param parentclass
	 * @param className
	 * @return the innerClass under parentclass
	 */
	public static IClass getClass(IClass parentclass, String className) throws ProjectNotFoundException, ClassNotFoundException, InvalidEditingException {
		IClass[] nestClasses = parentclass.getNestedClasses();
		for (int i = 0; i < nestClasses.length; i++) {
			if (nestClasses[i].getName().equals(className)) {
				return nestClasses[i];
			}
		}
		return setLanguage(ModelEditorFactory.getBasicModelEditor().createClass(parentclass, className));
	}
	
	/**
	 * 
	 * @param parentPkg: the class's package
	 * @param className: the class's name
	 * @return the Class under parentPkg, which named className.
	 */
	static int INDEX = 0;
	static Map<IClass, List<ITemplateBinding>> BASECLS_BINDINGS = new HashMap<IClass, List<ITemplateBinding>>();
	static Map<IPackage, List<Object[]>> BOUND_CLASSES = new HashMap<IPackage, List<Object[]>>();
	public static IClass getClass(IPackage parentPkg, String className, IClass owner) throws ProjectNotFoundException, ClassNotFoundException, InvalidEditingException {
		int firstIndex;
		if ((firstIndex = className.indexOf("<", 1)) != -1 && className.endsWith(">")) {
			String[] params = className.substring(firstIndex + 1, className.length() - 1).split(",");
			BasicModelEditor editor = ModelEditorFactory.getBasicModelEditor();
			IClass baseCls = getClass(parentPkg, className.substring(0, firstIndex), owner);
			List<ITemplateBinding> exitedBindings = BASECLS_BINDINGS.get(baseCls);
			if (exitedBindings != null) {
				for (ITemplateBinding binding: exitedBindings) {
					if (binding.getTemplate() == binding 
							&& matchesAllActualValues(binding, params)) {
						IClass boundClass = binding.getBoundElement();
						if ("".equals(boundClass.getName())
								|| className.equals(boundClass.getName())) {
							return boundClass;
						}
					}
				}
			}
            if (BOUND_CLASSES.get(parentPkg) != null) {
                List<Object[]> cls = BOUND_CLASSES.get(parentPkg);
                for (Object[] nameAndCls : cls) {
                    if (nameAndCls[0].equals(className)) {
                        return (IClass) nameAndCls[1];
                    }
                }
            }
			while (baseCls.getTemplateParameters().length < params.length) {				
				editor.createTemplateParameter(baseCls, "param" + String.valueOf(++INDEX), (IClass) null, null);
			}
			IClass anomyousCls = getClass(parentPkg, "param" + String.valueOf(++INDEX), owner);
			ITemplateBinding binding = editor.createTemplateBinding(anomyousCls, baseCls);
			for (int i = 0; i < params.length; i++) {
				String[] split = splitNameAndTypeModifier(params[i]);
				IClassifierTemplateParameter parameter = baseCls.getTemplateParameters()[i];
				//fix dev-bug 831: [5.5][doxygen1.5.8]needless " T1 " class will create in C_Sharp_5.5_1.5.8.jude
				String actualName = split[0].trim();
				if (LanguageManager.getCurrentLanguagePrimitiveType().contains(actualName)) {				
					binding.addActualParameter(parameter, actualName);
				} else {
					IModel root = ProjectAccessorFactory.getProjectAccessor().getProject();
					IClass actualCls = getClass(root, actualName, owner);
					binding.addActualParameter(parameter, actualCls);
				}
				List<ITemplateBinding> bindingStore = BASECLS_BINDINGS.get(baseCls);
				if (bindingStore == null) {
					bindingStore = new ArrayList<ITemplateBinding>();
					BASECLS_BINDINGS.put(baseCls, bindingStore);
					bindingStore.add(binding);
				} else {
					bindingStore.add(binding);
				}
				if (!"".equals(split[1]))
					binding.setActualParameterTypeModifier(parameter, split[1]);
			}
			anomyousCls.setName("");
			if (BOUND_CLASSES.get(parentPkg) == null) {
				BOUND_CLASSES.put(parentPkg, new ArrayList<Object[]>());
			}
			((List<Object[]>) BOUND_CLASSES.get(parentPkg)).add(new Object[] {className, anomyousCls});
			return anomyousCls;
		} else {
			if (owner != null) {
				IClass[] parameters = owner.getNestedClasses();
				for (int i = 0; i < parameters.length; i++) {
					if (parameters[i].getName().equals(className)) {
						if(GlobalList.contains(parameters[i])){
							return changeGlobalName(parentPkg, className, parameters, i);
						}
						return (IClass) parameters[i];
					}
				}
			}
			INamedElement[] namedElements = parentPkg.getOwnedElements();
			for (int i = 0; i < namedElements.length; i++) {
				if (namedElements[i].getName().equals(className) && namedElements[i] instanceof IClass) {
					return (IClass) namedElements[i];
				}
			}
			String[] paths = className.split("\\.");
			String newClassName = null;
			for (int i = 1; i < paths.length; i++) {
				parentPkg = getPackage(parentPkg, paths[i - 1]);
				newClassName = paths[i];
			}
			newClassName = newClassName == null ? className : newClassName;
			for (int i = 0; i < parentPkg.getOwnedElements().length; i++) {
				if (parentPkg.getOwnedElements()[i] instanceof IClass
						&& parentPkg.getOwnedElements()[i].getName().equals(newClassName)) {
					return (IClass) parentPkg.getOwnedElements()[i];
				}
			}
			return setLanguage(ModelEditorFactory.getBasicModelEditor().createClass(parentPkg, newClassName));
		}
	}
	
	/**
	 * @param parentPkg
	 * @param className
	 * @param namedElements
	 * @param i
	 * @return
	 * @throws ProjectNotFoundException
	 * @throws ClassNotFoundException
	 * @throws InvalidEditingException
	 */
	private static IClass changeGlobalName(IPackage parentPkg, String className, INamedElement[] namedElements, int i)
			throws ProjectNotFoundException, ClassNotFoundException, InvalidEditingException {
		int index = 0;
		String name = "Global";
		IClass globalClass = null;
		while (index < 100) {
			globalClass = Tool.getGlobalClass(parentPkg, name);
			if(globalClass==null){
				break;
			}
			name = "Global" + "_" + index++;	
		}
        for (int j = 0; j < GlobalList.size(); j++) {
            if ((IClass) namedElements[i] == GlobalList.get(j)) {
                GlobalList.get(j).setName(name);
            }
        }
		return setLanguage(ModelEditorFactory.getBasicModelEditor().createClass(parentPkg, className));
	}
	
	/**
	 * 
	 * @param parentPkg: the Global class's package
	 * @param className: the Global class's name
	 * @return the Global Class under parentPkg, which named className.
	 */
	public static IClass getGlobalClass(IPackage parentPkg, String className) throws ProjectNotFoundException,
			ClassNotFoundException, InvalidEditingException {
		INamedElement[] namedElements = parentPkg.getOwnedElements();
		for (int i = 0; i < namedElements.length; i++) {
			if (namedElements[i].getName().equals(className) && namedElements[i] instanceof IClass) {
				return (IClass) namedElements[i];
			}
		}
		return null;
	}
	
	private static boolean matchesAllActualValues(ITemplateBinding binding, String[] actualClasses) throws InvalidEditingException {
		List<IClassifierTemplateParameter> matchedParams = new ArrayList<IClassifierTemplateParameter>();
		@SuppressWarnings("unchecked")
        Map<Object, Object> actualMap = binding.getActualMap();
		for (int i = 0; i < actualClasses.length; i++) {
			String actual = actualClasses[i];
			String[] split = splitNameAndTypeModifier(actual);
			for (Iterator<Object> it = actualMap.keySet().iterator(); it.hasNext(); ) {
				IClassifierTemplateParameter param = (IClassifierTemplateParameter)it.next();				
				if (actualMap.get(param) == split[0]
				      && split[1].equals(binding.getActualParameterTypeModifier(param))
				      && !matchedParams.contains(param)) {
					matchedParams.add(param);
					break;
				}
			}
		}
		if (matchedParams.size() == actualClasses.length) {
			return true;
		}
		
		return false;
	}
	
	private static String[] splitNameAndTypeModifier(String type) {
		String[] split = new String[2];
		if (type.endsWith("**")) {
			split[0] = type.substring(0, type.indexOf("**")).trim();
			split[1] = "**";
		} else if (type.endsWith("*")) {
			split[0] = type.substring(0, type.indexOf("*")).trim();
			split[1] = "*";
		} else if (type.endsWith("&")) {
			split[0] = type.substring(0, type.indexOf("&")).trim();
			split[1] = "&";
		} else {
			split[0] = type;
			split[1] = "";
		}
		return split;
	}
	
	/**
	 * 
	 * @param subClass
	 * @param superClass
	 * @return the Generalization between subClass and superClass
	 */
	public static IGeneralization getGeneralization(IClass subClass, IClass superClass) throws InvalidEditingException, ClassNotFoundException {
		IGeneralization[] generlations = subClass.getGeneralizations();
		for (int i = 0; i < generlations.length; i++) {
			if (generlations[i].getSuperType().equals(superClass))
				return generlations[i];
		}
		return ModelEditorFactory.getBasicModelEditor().createGeneralization(subClass, superClass, "");
	}
	
	/**
	 * 
	 * @param target: the attribute in the class
	 * @param name: the attribute's name
	 * @param type: the attribute's type, the type is IClass
	 * @param isProp: Is the kind of attributes a property?
	 * @return the attribute in the target Class, which's name and type
	 */
    public static IAttribute getAttribute(IClass target, String name, IClass type, boolean isProp)
            throws InvalidEditingException, ClassNotFoundException {
		IAttribute[] attrs = target.getAttributes();
        for (IAttribute attr : attrs) {
            if (attr.getName().equals(name)) {
                if (!isProp && attr.getType().equals(type)) {
                    return attr;
                }
                BasicModelEditor basicModelEditor = ModelEditorFactory.getBasicModelEditor();
                basicModelEditor.delete(attr);
                break;
			}
		}
		return setLanguage(ModelEditorFactory.getBasicModelEditor().createAttribute(target, name, type));
	}
	
	/**
	 * 
	 * @param target: the attribute in the class
	 * @param name: the attribute's name
	 * @param type: the attribute's type, the type is the primitive type in jude
	 * @param isProp: Is the kind of attributes a property?
	 * @return the attribute in the target Class, which's name and type
	 */
    public static IAttribute getAttribute(IClass target, String name, String type, boolean isProp)
            throws InvalidEditingException, ClassNotFoundException {
		IAttribute[] attrs = target.getAttributes();
        for (IAttribute attr : attrs) {
            if (attr.getName().equals(name)) {
                if (!isProp && attr.getType().getName().equals(type)) {
                    return attr;
                }
                BasicModelEditor basicModelEditor = ModelEditorFactory.getBasicModelEditor();
                basicModelEditor.delete(attr);
                break;
			}
		}
		try {
            return setLanguage(ModelEditorFactory.getBasicModelEditor().createAttribute(target, name, type));
        } catch (InvalidEditingException e) {
            logger.debug(e.getMessage());
            return null;
        }
	}
	
	public static IOperation getOperation(IClass target, String name, String type) throws InvalidEditingException, ClassNotFoundException {
		return setLanguage(ModelEditorFactory.getBasicModelEditor().createOperation(target, name, type));
	}

	public static IOperation getOperation(IClass target, String name, IClass type) throws InvalidEditingException, ClassNotFoundException {
		return setLanguage(ModelEditorFactory.getBasicModelEditor().createOperation(target, name, type));
	}
	
	public static IParameter getOperationParameter(IOperation operation, String name, String type) throws InvalidEditingException, ClassNotFoundException {
		IParameter[] params = operation.getParameters();
		for (int i = 0; i < params.length; i++) {
			if (params[i].getName().equals(name)
					&& type.equals(params[i].getType().getName())) {
				return params[i];
			}
		}
		return ModelEditorFactory.getBasicModelEditor().createParameter(operation, name, type);
	}
	
	//set iattribute's Language, Now Jude support C# and JAVA
	private static IAttribute setLanguage(IAttribute iattr) throws InvalidEditingException {
		if (LanguageManager.isCSHARP()) {
			String[] steretypes = iattr.getStereotypes();
			for (int i = 0; i < steretypes.length; i++) {
				if (steretypes[i].equals("C# Attribute")) {
					return iattr;
				}
			}
			iattr.addStereotype("C# Attribute");
		} else if (LanguageManager.isJAVA()) {
			String[] steretypes = iattr.getStereotypes();
			for (int i = 0; i < steretypes.length; i++) {
				if (steretypes[i].equals("Java Attribute")) {
					return iattr;
				}
			}
			iattr.addStereotype("Java Attribute");
		}
		return iattr;
	}

	//set iclass's Language, Now Jude support C# and JAVA
	public static IClass setLanguage(IClass iclass) throws InvalidEditingException {
		if (LanguageManager.isCSHARP()) {
			String[] steretypes = iclass.getStereotypes();
			for (int i = 0; i < steretypes.length; i++) {
				if (steretypes[i].equals("C# Class")) {
					return iclass;
				}
			}
			iclass.addStereotype("C# Class");
		} else if (LanguageManager.isJAVA()) {
			String[] steretypes = iclass.getStereotypes();
			for (int i = 0; i < steretypes.length; i++) {
				if (steretypes[i].equals("Java Class")) {
					return iclass;
				}
			}
			iclass.addStereotype("Java Class");
		}
		return iclass;
	}

	//set ioperation's Language, Now Jude support C# and JAVA
	private static IOperation setLanguage(IOperation oper) throws InvalidEditingException {
		if (LanguageManager.isCSHARP()) {
			String[] steretypes = oper.getStereotypes();
			for (int i = 0; i < steretypes.length; i++) {
				if (steretypes[i].equals("C# Method")) {
					return oper;
				}
			}
			oper.addStereotype("C# Method");
		} else if (LanguageManager.isJAVA()) {
			String[] steretypes = oper.getStereotypes();
			for (int i = 0; i < steretypes.length; i++) {
				if (steretypes[i].equals("Java Method")) {
					return oper;
				}
			}
			oper.addStereotype("Java Method");
		}
		return oper;
	}
	
}