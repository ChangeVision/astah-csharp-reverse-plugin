package com.change_vision.astah.extension.plugin.csharpreverse.reverser;
import java.util.HashSet;

import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.model.IElement;
import com.change_vision.jude.api.inf.model.IParameter;


public class TemplateParamCSharp extends TemplateParam {
	
	@Override
	void dealKeyword(IElement param, HashSet keywords) throws InvalidEditingException {
		if (param instanceof IParameter) {
			if (keywords.contains(REF))
				((IParameter) param).setDirection("inout");
			else if (keywords.contains(OUT))
				((IParameter) param).setDirection("out");
		}
	}
}