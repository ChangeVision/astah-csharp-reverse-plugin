package com.change_vision.astah.extension.plugin.csharpreverse.reverser;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.change_vision.jude.api.inf.editor.BasicModelEditor;
import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IOperation;

public class MemberCSharp extends Member {
    private static final Logger logger = LoggerFactory.getLogger(MemberCSharp.class);

	public static final String KEYWORD_CONST = "const";
	
	public static final String KEYWORD_OVERRIDE = "override";
	
	public static final String KEYWORD_READ_ONLY = "readonly";
	
	public static final String KEYWORD_DELEGATE = "delegate";
	
	public static final String KEYWORD_SEALED = "sealed";
	
	public static final String KEYWORD_INTERNAL = "internal";
	
	public static final String KEYWORD_UNSAFE = "unsafe";
	
	public static final String KEYWORD_VIRTUAL = "virtual";
	
	public static final String KEYWORD_ABSTRACT = "abstract";
	
	public static final String KEYWORD_VOLATILE = "volatile";
	
	public static final String KEYWORD_EVENT = "event";
	
	public static final String KEYWORD_PARTIAL = "partial";
	
	public static final String KEYWORD_ARRAY = "array";
	
	public static final String KEYWORD_STATIC = "static";
	
	public static final String KEYWORD_NEW = "new";
	
	public static final String KEYWORD_IMPLICIT = "implicit";

	@Override
	void dealOperationKeyword(BasicModelEditor basicModelEditor, Set<String> keywords,
			IOperation fun) throws InvalidEditingException {
		if (keywords.contains(KEYWORD_CONST)) {
			basicModelEditor.createTaggedValue(fun, "jude.c_sharp.const", "true");
		}
		if (keywords.contains(KEYWORD_OVERRIDE)) {
			basicModelEditor.createTaggedValue(fun, "jude.c_sharp.override", "true");
		}
		if (!"no".equals(staticBoolean)) {
			fun.setStatic(true);
		}
		if (keywords.contains(KEYWORD_DELEGATE)) {
			fun.getOwner().addStereotype("delegate");
		}
		if (keywords.contains(KEYWORD_INTERNAL)) {
			basicModelEditor.createTaggedValue(fun, "jude.c_sharp.internal", "true");
		}
		if (keywords.contains(KEYWORD_SEALED)) {
			basicModelEditor.createTaggedValue(fun, "jude.c_sharp.sealed", "true");
		}
		if (keywords.contains(KEYWORD_UNSAFE)) {
			basicModelEditor.createTaggedValue(fun, "jude.c_sharp.unsafe", "true");
		}		
		if(KEYWORD_VIRTUAL.equals(this.getVirt())){
			basicModelEditor.createTaggedValue(fun, "jude.c_sharp.virtual", "true");
		}
		if (KEYWORD_EVENT.equals(this.getKind())) {
			fun.addStereotype("event");
		}
		if (keywords.contains(KEYWORD_ABSTRACT)) {
			fun.setAbstract(true);
		}
		if (keywords.contains(AND)) {
			fun.setTypeModifier(AND);
		} else if (keywords.contains(STAR + STAR)) {
			fun.setTypeModifier(STAR + STAR);
		} else if (keywords.contains(STAR)) {
			fun.setTypeModifier(STAR);
		}
		
	}

	@Override
	void dealAttributeKeywords(BasicModelEditor basicModelEditor, Set<String> keywords,
			IAttribute attr) throws InvalidEditingException {
		try {
            if (keywords.contains(KEYWORD_CONST)) {
            	basicModelEditor.createTaggedValue(attr, "jude.c_sharp.const", "true");
            }
            if (keywords.contains(KEYWORD_READ_ONLY)) {
            	attr.setChangeable(false);
            }
            if (keywords.contains(KEYWORD_OVERRIDE)) {
            	basicModelEditor.createTaggedValue(attr, "jude.c_sharp.override", "true");
            }
            if (!"no".equals(staticBoolean)) {
            	attr.setStatic(true);
            }
            if (keywords.contains(KEYWORD_DELEGATE)) {
            	attr.getOwner().addStereotype("delegate");
            }
            if (keywords.contains(KEYWORD_INTERNAL)) {
            	basicModelEditor.createTaggedValue(attr, "jude.c_sharp.internal", "true");
            }
            if (keywords.contains(KEYWORD_SEALED)) {
            	basicModelEditor.createTaggedValue(attr, "jude.c_sharp.sealed", "true");
            }
            if (keywords.contains(KEYWORD_VOLATILE)) {
            	basicModelEditor.createTaggedValue(attr, "jude.c_sharp.volatile", "true");
            }
            if (keywords.contains(AND)) {
            	attr.setTypeModifier(AND);
            } else if (keywords.contains(STAR + STAR)) {
            	attr.setTypeModifier(STAR + STAR);
            } else if (keywords.contains(STAR)) {
            	attr.setTypeModifier(STAR);
            }
        } catch (InvalidEditingException e) {
            logger.debug(String.format("%s", e.getMessage()));
        }
	}

	@Override
	FilterKeyword filterKeyword(String type) {
		Set<String> keywords = new HashSet<String>();
		if (type.indexOf(KEYWORD_CONST) != -1) {
			if (KEYWORD_CONST.equals(type.trim())) {
				type = "";
			} else {
				type = type.replaceFirst(KEYWORD_CONST + " ", "");
			}
			keywords.add(KEYWORD_CONST);
		}
		if (type.indexOf(KEYWORD_READ_ONLY) != -1) {
			if (KEYWORD_READ_ONLY.equals(type.trim())) {
				type = "";
			} else {
				type = type.replaceFirst(KEYWORD_READ_ONLY + " ", "");
			}
			keywords.add(KEYWORD_READ_ONLY);
		}
		if (type.indexOf(KEYWORD_OVERRIDE) != -1) {
			if (KEYWORD_OVERRIDE.equals(type.trim())) {
				type = "";
			} else {
				type = type.replaceFirst(KEYWORD_OVERRIDE + " ", "");
			}
			keywords.add(KEYWORD_OVERRIDE);
		}
		if (type.indexOf(KEYWORD_DELEGATE) != -1) {
			if (KEYWORD_DELEGATE.equals(type.trim())) {
				type = "";
			} else {
				type = type.replaceFirst(KEYWORD_DELEGATE + " ", "");
			}
			keywords.add(KEYWORD_DELEGATE);
		}
		if (type.indexOf(KEYWORD_INTERNAL) != -1) {
			if (KEYWORD_INTERNAL.equals(type.trim())) {
				type = "";
			} else {
				type = type.replaceFirst(KEYWORD_INTERNAL + " ", "");
			}
			keywords.add(KEYWORD_INTERNAL);
		}
		if (type.indexOf(KEYWORD_SEALED) != -1) {
			if (KEYWORD_SEALED.equals(type.trim())) {
				type = "";
			} else {
				type = type.replaceFirst(KEYWORD_SEALED + " ", "");
			}
			keywords.add(KEYWORD_SEALED);
		}
		if (type.indexOf(KEYWORD_UNSAFE) != -1) {
			if (KEYWORD_UNSAFE.equals(type.trim())) {
				type = "";
			} else {
				type = type.replaceFirst(KEYWORD_UNSAFE + " ", "");
			}
			keywords.add(KEYWORD_UNSAFE);
		}
		if (type.indexOf(KEYWORD_ABSTRACT) != -1) {
			if (KEYWORD_ABSTRACT.equals(type.trim())) {
				type = "";
			} else {
				type = type.replaceFirst(KEYWORD_ABSTRACT + " ", "");
			}
			keywords.add(KEYWORD_ABSTRACT);
		}
		if (type.indexOf(KEYWORD_VOLATILE) != -1) {
			if (KEYWORD_VOLATILE.equals(type.trim())) {
				type = "";
			} else {
				type = type.replaceFirst(KEYWORD_VOLATILE + " ", "");
			}
			keywords.add(KEYWORD_VOLATILE);
		}		
		if (type.indexOf(KEYWORD_VIRTUAL) != -1) {
			if (KEYWORD_VIRTUAL.equals(type.trim())) {
				type = "";
			} else {
				type = type.replaceFirst(KEYWORD_VIRTUAL + " ", "");
			}
			keywords.add(KEYWORD_VIRTUAL);
		}
		if (type.indexOf(KEYWORD_PARTIAL) != -1) {
			if (KEYWORD_PARTIAL.equals(type.trim())) {
				type = "";
			} else {
				type = type.replaceFirst(KEYWORD_PARTIAL + " ", "");
			}
			keywords.add(KEYWORD_PARTIAL);
		}
		if (type.indexOf("[") != -1) {
			type = type.substring(0, type.indexOf("["));
			keywords.add(KEYWORD_ARRAY);
		}
		if (type.trim().endsWith(AND)) {
			type = type.replaceFirst(AND, "").trim();
			keywords.add(AND);
		}
		if (type.trim().endsWith(STAR + STAR)) {
			type = type.replaceFirst("\\*" + "\\*", "").trim();
			keywords.add(STAR + STAR);
		}
		if (type.trim().endsWith(STAR)) {
			type = type.replaceFirst("\\*", "").trim();
			keywords.add(STAR);
		}
		if (KEYWORD_STATIC.equals(type.trim())) {
			type = "";
		}
		if (type.indexOf(KEYWORD_NEW) != -1) {
			if (KEYWORD_NEW.equals(type.trim())) {
				type = "";
			} else {
				type = type.replaceFirst(KEYWORD_NEW + " ", "");
			}
			keywords.add(KEYWORD_NEW);
		}
		if (type.indexOf(KEYWORD_IMPLICIT) != -1) {
			if (KEYWORD_IMPLICIT.equals(type.trim())) {
				type = "";
			} else {
				type = type.replaceFirst(KEYWORD_IMPLICIT + " ", "");
			}
			keywords.add(KEYWORD_IMPLICIT);
		}
        return new FilterKeyword(keywords, type);
	}
}