package com.change_vision.astah.extension.plugin.csharpreverse.reverser;
import java.util.HashSet;

import com.change_vision.jude.api.inf.editor.ModelEditorFactory;
import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.model.IElement;
import com.change_vision.jude.api.inf.model.IParameter;


public class ParamCSharp extends Param {
	
	@Override
	void dealKeyword(IElement param, HashSet keywords) throws InvalidEditingException, ClassNotFoundException {
		if (LanguageManager.isCSHARP() && param instanceof IParameter) {
			if (keywords.contains(REF))
				((IParameter) param).setDirection("inout");
			else if (keywords.contains(OUT))
				((IParameter) param).setDirection("out");
			else if (keywords.contains(THIS)) {
				ModelEditorFactory.getBasicModelEditor().createTaggedValue(param.getOwner(), "jude.c_sharp.extension_method", "true");
			}
			if (keywords.contains(AND)) {
				param.setTypeModifier(AND);
			} else if (keywords.contains(STAR + STAR)) {
				param.setTypeModifier(STAR + STAR);
			} else if (keywords.contains(STAR)) {
				param.setTypeModifier(STAR);
			}
		}
	}
}