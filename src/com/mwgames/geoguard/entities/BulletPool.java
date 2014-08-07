package com.mwgames.geoguard.entities;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.pool.GenericPool;

import com.mwgames.geoguard.StartGameActivity;

public class BulletPool extends GenericPool<Bullet> {
    private ITextureRegion mTextureRegion;
    private VertexBufferObjectManager mVertexBufferObjectManager;
    
    public BulletPool(ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager) {
        mTextureRegion = pTextureRegion;
        mVertexBufferObjectManager = pVertexBufferObjectManager;
    }
 
    /** Called when a projectile is required but there isn't one in the pool */
    @Override
    protected Bullet onAllocatePoolItem() {
        float centerX = StartGameActivity.CAMERA_WIDTH / 2;
		float centerY = StartGameActivity.CAMERA_HEIGHT / 2;
		return new Bullet(centerX, centerY, mTextureRegion, mVertexBufferObjectManager);
    }
 
    /** Called when a projectile is sent to the pool */
    protected void onHandleRecycleItem(final Sprite bullet) {
        bullet.clearEntityModifiers();
        bullet.clearUpdateHandlers();
        bullet.setVisible(false);
        bullet.detachSelf();
        bullet.reset();
    }
}