package com.mwgames.geoguard;

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
	private int level = 1;
	private int bullet = 1;
	
	// ===========================================================
	// Constructors
	// ===========================================================
	public Ship(float centerX, float centerY, ITextureRegion textureRegion, VertexBufferObjectManager vertexBufferObjectManager) {
		super(centerX, centerY, textureRegion, vertexBufferObjectManager);
        this.health = SHIP_MAX_HEALTH;
        this.isAlive = this.health > 0 ? true : false;	
	}
	public Ship(float centerX, float centerY, ITextureRegion textureRegion, VertexBufferObjectManager vertexBufferObjectManager, int health) {
		super(centerX, centerY, textureRegion, vertexBufferObjectManager);
        this.health = health;
        this.isAlive = this.health > 0 ? true : false;	
	}
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
	public void killShip() {
		this.isAlive = false;
	}
	public float getLevelModifier() {
		return (level * 0.1f);
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public float getBulletModifier() {
		return (bullet * 0.1f);
	}
	public void setBullet(int bullet) {
		this.bullet = bullet;
	}
}
