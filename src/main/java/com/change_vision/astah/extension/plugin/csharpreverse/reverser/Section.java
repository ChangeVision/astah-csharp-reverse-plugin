package com.change_vision.astah.extension.plugin.csharpreverse.reverser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.xml.sax.SAXException;

import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IElement;
import com.change_vision.jude.api.inf.model.IPackage;

/**
 * 
 * this class is the tag of **.xml the tag is <sectiondef>. the class is named Section the tag
 * attribute is <sectiondef kind>.it is named "kind" members is list of the member'class.tag is
 * <memberdef> parent is the Compounddef relation
 */
public class Section implements IConvertToJude {
    private String kind;
    private List<Member> members;
    private CompoundDef parent;

    public CompoundDef getParent() {
        return parent;
    }

    public void setParent(CompoundDef parent) {
        this.parent = parent;
    }

    public Section() {
        members = new ArrayList<Member>();
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

    public void addMember(Member member) {
        members.add(member);
        member.setParent(this);
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public void convertToJudeModel(IElement parent, File[] files) throws InvalidEditingException,
            ClassNotFoundException, ProjectNotFoundException, IOException, SAXException {
        for (Member member : members) {
            if (parent instanceof IPackage) {
                continue;
            }
            member.convertToJudeModel(parent, files);
        }
    }

    public void convertToJudeModelExceptEnum(IPackage parent, File[] files)
            throws InvalidEditingException, ClassNotFoundException, ProjectNotFoundException,
            IOException, SAXException {
        for (Member member : members) {
            if (Member.KIND_ENUM.equals(member.getKind())) {
                continue;
            }
            if (member.getType() != null && member.getType().indexOf("delegate ") != -1) {
                IClass parentClass = Tool.getClass(parent, member.getName(), null);
                member.convertToJudeModel(parentClass, files);
            }
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}