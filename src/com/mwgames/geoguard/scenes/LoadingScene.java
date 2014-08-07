package com.mwgames.geoguard.scenes;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.util.GLState;

import com.mwgames.geoguard.base.BaseScene;
import com.mwgames.geoguard.util.ResourceManager;
import com.mwgames.geoguard.util.SceneManager.SceneType;


public class LoadingScene extends BaseScene
{
	private Sprite loadingBG;
	
	@Override
	public void createScene()
	{
		loadingBG = new Sprite((ResourceManager.CAMERA_WIDTH - resourceManager.loadingRegion.getWidth()) / 2,
						(ResourceManager.CAMERA_HEIGHT - resourceManager.loadingRegion.getHeight()) / 2, 
						resourceManager.loadingRegion,
						vbom)
    	{
    		@Override
            protected void preDraw(GLState pGLState, Camera pCamera) 
    		{
                super.preDraw(pGLState, pCamera);
                pGLState.enableDither();
            }
    	};
    	
    	//loadingBG.setScale(1.5f);
    	//splash.setPosition(400, 240);
    	attachChild(loadingBG);
	}

	@Override
	public void onBackKeyPressed()
	{
		return;
	}

	@Override
	public SceneType getSceneType()
	{
		return SceneType.SCENE_LOADING;
	}

	@Override
	public void disposeScene()
	{
		loadingBG.detachSelf();
		loadingBG.dispose();
		this.detachSelf();
		this.dispose();
	}
}