package com.mwgames.geoguard.entities;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Target extends Sprite{
	// ===========================================================
	// Constants
	// ===========================================================
	// ===========================================================
	// Fields
	// ===========================================================
	private float startX;
	private float startY;
	private double startAngle;
	private int startV;
	// ===========================================================
	// Constructors
	// ===========================================================
	public Target(float startX, float startY, ITextureRegion textureRegion, VertexBufferObjectManager vertexBufferObjectManager) {
		super(startX, startY, textureRegion, vertexBufferObjectManager);
		this.startX = startX;
		this.startY = startY;
		this.startAngle = 0;
		this.startV = 0;
	}
	public Target(float startX, float startY, ITextureRegion textureRegion, VertexBufferObjectManager vertexBufferObjectManager, int startV) {
		super(startX, startY, textureRegion, vertexBufferObjectManager);
		this.startX = startX;
		this.startY = startY;
		this.startAngle = 0;
		this.startV = startV;
	}

	// ===========================================================
	// Getters & Setters
	// ===========================================================
	public float getStartX() {
		return startX;
	}
	public void setStartX(int startX) {
		this.startX = startX;
	}
	public float getStartY() {
		return startY;
	}
	public void setStartY(int startY) {
		this.startY = startY;
	}
	public double getStartAngle() {
		return startAngle;
	}
	public void setStartAngle(int startAngle) {
		this.startAngle = startAngle;
	}
	public int getStartV() {
		return startV;
	}
	public void setStartV(int startV) {
		this.startV = startV;
	}
}
