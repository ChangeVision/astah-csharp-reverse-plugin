package com.change_vision.astah.extension.plugin.csharpreverse.reverser;

import java.io.File;
import java.io.IOException;

import org.xml.sax.SAXException;

import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IElement;

/**
 * 
 * this class is the tag of **.xml<br />
 * the tag is {@literal <enumvalue>}. the class is named EnumValue<br />
 * the tag is {@literal <enumvalue id>}.it is named "id"<br />
 * the tag is {@literal <enumvalue prot>}.it is named "prot"<br />
 * the sub-tag is {@literal <name>}.it is named "name"<br />
 * the sub-tag is {@literal <initializer>}.it is named "initializer"<br />
 * the sub-tag is {@literal <briefdescription>}.it is named "briefdescription"<br />
 * the sub-tag is {@literal <detaileddescription>}.it is named
 * "detaileddescription"<br />
 */
public class EnumValue implements IConvertToJude {

	/**
	 * enumvalue id
	 */
	String id;

	/**
	 * 可視性
	 */
	String prot;

	/**
	 * 変数の名前
	 */
	String name;

	/**
	 * 初期値
	 */
	String initializer;

	/**
	 * 説明
	 */
	String briefdescription;

	/**
	 * 概要
	 */
	String detaileddescription;

	/**
	 * enumvalue id を取得します。
	 * 
	 * @return enumvalue id
	 */
	public String getId() {
		return id;
	}

	/**
	 * enumvalue id を設定します。
	 * 
	 * @param id
	 *            enumvalue id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 可視性を取得します。
	 * 
	 * @return 可視性
	 */
	public String getProt() {
		return prot;
	}

	/**
	 * 可視性を設定します。
	 * 
	 * @param prot
	 *            可視性
	 */
	public void setProt(String prot) {
		this.prot = prot;
	}

	/**
	 * 名前を取得します。
	 * 
	 * @return 名前
	 */
	public String getName() {
		return name;
	}

	/**
	 * 名前を設定します。
	 * 
	 * @param name
	 *            名前
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 説明を取得します。
	 * 
	 * @return 説明
	 */
	public String getBriefdescription() {
		return briefdescription;
	}

	/**
	 * 説明を設定します。
	 * 
	 * @param briefdescription
	 *            説明
	 */
	public void setBriefdescription(String briefdescription) {
		this.briefdescription = briefdescription;
	}

	/**
	 * 概要を取得します。
	 * 
	 * @return 概要
	 */
	public String getDetaileddescription() {
		return detaileddescription;
	}

	/**
	 * 概要を設定します。
	 * 
	 * @param detaileddescription
	 *            概要
	 */
	public void setDetaileddescription(String detaileddescription) {
		this.detaileddescription = detaileddescription;
	}

	/**
	 * 初期値を取得します。
	 * 
	 * @return 初期値
	 */
	public String getInitializer() {
		return initializer;
	}

	/**
	 * 初期値を設定します。
	 * 
	 * @param initializer
	 *            初期値
	 */
	public void setInitializer(String initializer) {
		initializer = initializer.trim();
		if (initializer.startsWith("=")) {
			initializer = initializer.replaceFirst("=", "").trim();
		}
		this.initializer = initializer;
	}

	@Override
	public void convertToJudeModel(IElement parent, File[] files)
			throws InvalidEditingException, ClassNotFoundException,
			ProjectNotFoundException, IOException, SAXException {
		IAttribute iattr = Tool.getAttribute((IClass) parent, getName(), "int");
		if (null == iattr) {
			return;
		}
		if (getInitializer() != null)
			iattr.setInitialValue(this.getInitializer());
		iattr.addStereotype("enum constant");
	}
}