package com.mwgames.geoguard.entities;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.pool.GenericPool;

import com.mwgames.geoguard.StartGameActivity;

public class TargetPool extends GenericPool<Target> {
    private ITextureRegion mTextureRegion;
    private VertexBufferObjectManager mVertexBufferObjectManager;
 
    public TargetPool(ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager) {
        mTextureRegion = pTextureRegion;
        mVertexBufferObjectManager = pVertexBufferObjectManager;
    }
 
    /** Called when a projectile is required but there isn't one in the pool */
    @Override
    protected Target onAllocatePoolItem() {
        int startX = generateRandomTargetControlStart(StartGameActivity.CAMERA_WIDTH);
		int startY = generateRandomTargetControlStart(StartGameActivity.CAMERA_HEIGHT);
		int startV = generateRandomTargetControlVelocity();
		switch((int)(Math.random() * 100) % 4){
			case 0:
				startX = 0;
				break;
			case 1:
				startY = 0;
				break;
			case 2:
				startX = StartGameActivity.CAMERA_WIDTH;
				break;
			case 3:
				startY = StartGameActivity.CAMERA_HEIGHT;
				break;
			default:
				break;
		}
		return new Target(startX, startY, mTextureRegion, mVertexBufferObjectManager, startV);
    }
 
    private int generateRandomTargetControlVelocity() {
		return 0;
	}

	/** Called when a projectile is sent to the pool */
    protected void onHandleRecycleItem(final Sprite target) {
        target.clearEntityModifiers();
        target.clearUpdateHandlers();
        target.setVisible(false);
        target.detachSelf();
        target.reset();
    }
    public int generateRandomTargetControlStart(int mod) {
		return ((int)(Math.random() * 10000)) % mod;
	}
}