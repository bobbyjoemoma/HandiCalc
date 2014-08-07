package com.mwgames.geoguard.entities;

import org.andengine.entity.Entity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.vbo.ISpriteVertexBufferObject;
import org.andengine.opengl.shader.ShaderProgram;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.DrawType;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Ship extends Sprite{
	// ===========================================================
	// Constants
	// ===========================================================
	private static final int SHIP_MAX_HEALTH = 4;
	// ===========================================================
	// Fields
	// ===========================================================
	private int health = 0;
	private boolean isAlive = true;
	private int EnvLevel = 1;
	private int powerLevel = 1;
	private int speedLevel = 1;
	private int accuracyLevel = 1;
	
	// ===========================================================
	// Constructors
	// ===========================================================

	public Ship(float centerX, float centerY, ITextureRegion textureRegion, VertexBufferObjectManager vertexBufferObjectManager, int health, boolean isAlive) {
		super(centerX, centerY, textureRegion, vertexBufferObjectManager);
        this.health = health;
        this.isAlive = isAlive;	
	}

	// ===========================================================
	// Getters & Setters
	// ===========================================================
	
	public int getHealth() {
		return health;
	}
	public void setHealth(int health) {
		this.health = health;
	}
	public boolean isAlive() {
		return isAlive;
	}
	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}
	public int getEnvLevel() {
		return EnvLevel;
	}
	public void setEnvLevel(int envLevel) {
		EnvLevel = envLevel;
	}
	public int getPowerLevel() {
		return powerLevel;
	}
	public void setPowerLevel(int powerLevel) {
		this.powerLevel = powerLevel;
	}
	public int getSpeedLevel() {
		return speedLevel;
	}
	public void setSpeedLevel(int speedLevel) {
		this.speedLevel = speedLevel;
	}
	public int getAccuracyLevel() {
		return accuracyLevel;
	}
	public void setAccuracyLevel(int accuracyLevel) {
		this.accuracyLevel = accuracyLevel;
	}

	public void decrementHealth() {
		this.health--;
		if(this.health <= 0) this.isAlive = false;
	}
	public void incrementHealth() {
		this.health++;
		if(this.health >= 0) this.isAlive = true;
	}
	
}
