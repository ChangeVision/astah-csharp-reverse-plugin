package com.change_vision.astah.extension.plugin.csharpreverse.reverser;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.xml.sax.SAXException;

import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IElement;
import com.change_vision.jude.api.inf.model.IPackage;

public class CompoundDefCSharp extends CompoundDef {
	
	public static final String KIND_STRUCT = "struct";
	
	@Override
	public void convertToJudeModel(IElement parent, File[] files) throws InvalidEditingException,
			ClassNotFoundException, ProjectNotFoundException, IOException, SAXException {
		if (compounddef.get(this.getCompounddefId()) != null) {
			return;
		}
		if (KIND_STRUCT.equals(this.getCompounddefKind())) {			
			IClass iclass = this.convertClass(parent, files, false);
			iclass.addStereotype("struct");
		} else {
			super.convertToJudeModel(parent, files);
		}
	}
	
	@Override
	protected void dealWithGlobalElements(IPackage pkg, File[] files)
	throws ProjectNotFoundException, ClassNotFoundException,
	InvalidEditingException, IOException, SAXException {
		for (Iterator iterator = this.getSections().iterator(); iterator.hasNext();) {
			Section next = (Section) iterator.next();
			//convert enum
			for (Iterator iter = next.getMembers().iterator(); iter.hasNext();) {			
				Member member = (Member) iter.next();
				if (Member.KIND_ENUM.equals(member.getKind())) {
					IClass enumClass = Tool.getClass(pkg, member.getName(), null);
					enumClass.addStereotype("enum");
					for (Iterator vars = member.getEnumValues().iterator(); vars.hasNext();) {
						EnumValue enumValue = (EnumValue) vars.next();
						enumValue.convertToJudeModel(enumClass, files);
					}					
					CompoundDef.compounddef.put(member.getId(), enumClass);
				}	
			}
		
			next.convertToJudeModelExceptEnum(pkg, files);
		}
	}
}