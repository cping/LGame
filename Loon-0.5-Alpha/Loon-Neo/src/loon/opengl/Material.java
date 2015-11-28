package loon.opengl;

import loon.IDGenerator;
import loon.LTexture;
import loon.canvas.LColor;
import loon.utils.NumberUtils;

public class Material {
	private int id;

	private LColor ambient;
	private LColor diffuse;
	private LColor specular;

	private LTexture diffuseMap;
	private LTexture normalMap;
	private LTexture specularMap;

	private float dissolve;
	private float specularPower;
	private float illumination;

	private String name;

	public Material() {
		id = IDGenerator.generate();

		ambient = new LColor(1, 1, 1, 1);
		diffuse = new LColor(1, 1, 1, 1);
		specular = new LColor(1, 1, 1, 1);

		dissolve = 1;
		specularPower = 100;
		illumination = 2;

		this.name = "Default";
	}

	public Material(String name) {
		this();
		setName(name);
	}

	public Material(Material m) {
		this.ambient = m.ambient.cpy();
		this.diffuse = m.diffuse.cpy();
		this.specular = m.specular.cpy();

		this.diffuseMap = m.diffuseMap;
		this.normalMap = m.normalMap;
		this.specularMap = m.specularMap;

		this.dissolve = m.dissolve;
		this.specularPower = m.specularPower;
		this.illumination = m.illumination;
	}

	public LColor getAmbient() {
		return ambient;
	}

	public Material setAmbient(LColor ambient) {
		this.ambient = ambient;
		return this;
	}

	public LColor getDiffuse() {
		return diffuse;
	}

	public Material setDiffuse(LColor diffuse) {
		this.diffuse = diffuse;
		return this;
	}

	public LColor getSpecular() {
		return specular;
	}

	public Material setSpecular(LColor specular) {
		this.specular = specular;
		return this;
	}

	public LTexture getDiffuseMap() {
		return diffuseMap;
	}

	public Material setDiffuseMap(LTexture diffuseMap) {
		this.diffuseMap = diffuseMap;
		return this;
	}

	public LTexture getNormalMap() {
		return normalMap;
	}

	public Material setNormalMap(LTexture normalMap) {
		this.normalMap = normalMap;
		return this;
	}

	public LTexture getSpecularMap() {
		return specularMap;
	}

	public Material setSpecularMap(LTexture specularMap) {
		this.specularMap = specularMap;
		return this;
	}

	public float getDissolve() {
		return dissolve;
	}

	public Material setDissolve(float dissolve) {
		this.dissolve = dissolve;
		return this;
	}

	public float getSpecularPower() {
		return specularPower;
	}

	public Material setSpecularPower(float specularPower) {
		this.specularPower = specularPower;
		return this;
	}

	public float getIllumination() {
		return illumination;
	}

	public Material setIllumination(float illumination) {
		this.illumination = illumination;
		return this;
	}

	public String getName() {
		return name;
	}

	public Material setName(String name) {
		this.name = name;
		return this;
	}

	public int getID() {
		return id;
	}

	@Override
	public int hashCode() {
		int result = getAmbient().hashCode();
		result = 31 * result + getDiffuse().hashCode();
		result = 31 * result + getSpecular().hashCode();
		result = 31 * result + getDiffuseMap().hashCode();
		result = 31 * result + getNormalMap().hashCode();
		result = 31 * result + getSpecularMap().hashCode();
		result = 31
				* result
				+ (getDissolve() != +0.0f ? NumberUtils
						.floatToIntBits(getDissolve()) : 0);
		result = 31
				* result
				+ (getSpecularPower() != +0.0f ? NumberUtils
						.floatToIntBits(getSpecularPower()) : 0);
		result = 31
				* result
				+ (getIllumination() != +0.0f ? NumberUtils
						.floatToIntBits(getIllumination()) : 0);
		result = 31 * result + getName().hashCode();
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o){
			return true;
		}
		if (o == null || getClass() != o.getClass()){
			return false;
		}

		Material material = (Material) o;

		return NumberUtils.compare(material.getDissolve(), getDissolve()) == 0
				&& NumberUtils.compare(material.getSpecularPower(),
						getSpecularPower()) == 0
				&& NumberUtils.compare(material.getIllumination(),
						getIllumination()) == 0
				&& getAmbient().equals(material.getAmbient())
				&& getDiffuse().equals(material.getDiffuse())
				&& getSpecular().equals(material.getSpecular())
				&& getDiffuseMap().equals(material.getDiffuseMap())
				&& getNormalMap().equals(material.getNormalMap())
				&& getSpecularMap().equals(material.getSpecularMap())
				&& getName().equals(material.getName());
	}

}
