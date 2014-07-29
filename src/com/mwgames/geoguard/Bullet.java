package com.mwgames.geoguard;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Bullet extends Sprite{
	// ===========================================================
	// Constants
	// ===========================================================
	
	// ===========================================================
	// Fields
	// ===========================================================
		
	// ===========================================================
	// Constructors
	// ===========================================================
	
	public Bullet(float centerX, float centerY, ITextureRegion textureRegion, VertexBufferObjectManager vertexBufferObjectManager) {
		super(centerX, centerY, textureRegion, vertexBufferObjectManager);
	}
	public Bullet(float centerX, float centerY, ITextureRegion textureRegion, VertexBufferObjectManager vertexBufferObjectManager, int startX, int startY, int startAngle, int startV) {
		super(centerX, centerY, textureRegion, vertexBufferObjectManager);
	}
	
	// ===========================================================
	// Getters & Setters
	// ===========================================================
	
	// ===========================================================
	// Methods
	// ===========================================================
	
}
