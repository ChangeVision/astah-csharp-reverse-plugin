package com.change_vision.astah.extension.plugin.csharpreverse.reverser;

import com.change_vision.jude.api.inf.editor.BasicModelEditor;
import com.change_vision.jude.api.inf.editor.ModelEditorFactory;
import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IElement;

/**
 * this class extend Param the sub-tag is <defval>.it is named "defval"
 */
public class TemplateParam extends Param {
    private String defval;

    public String getDefval() {
        return defval;
    }

    public void setDefval(String defval) {
        this.defval = defval;
    }

    @Override
    protected IElement createParameter(IElement parent, String name, String paramArray,
            String paramName) throws InvalidEditingException, ProjectNotFoundException,
            ClassNotFoundException {
        BasicModelEditor basicModelEditor = ModelEditorFactory.getBasicModelEditor();
        return basicModelEditor.createTemplateParameter(((IClass) parent), this.getType(),
                (IClass) null, defval);
    }
}
