package com.mwgames.geoguard.util;

import org.andengine.engine.Engine;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.ui.IGameInterface.OnCreateSceneCallback;

import com.mwgames.geoguard.base.BaseScene;
import com.mwgames.geoguard.scenes.GameScene;
import com.mwgames.geoguard.scenes.LoadingScene;
import com.mwgames.geoguard.scenes.MainMenuScene;

/**
 * @author Mateusz Mysliwiec
 * @author www.matim-dev.com
 * @version 1.0
 */
public class SceneManager
{
	//---------------------------------------------
	// SCENES
	//---------------------------------------------
	
	private BaseScene loadingScene;
	private BaseScene menuScene;
	private BaseScene gameScene;
	private BaseScene upgradesScene;
	private BaseScene storeScene;
	private BaseScene settingsScene;
	private BaseScene endedScene;
	
	//---------------------------------------------
	// VARIABLES
	//---------------------------------------------
	
	private static final SceneManager INSTANCE = new SceneManager();
	
	private SceneType currentSceneType = SceneType.SCENE_LOADING;
	
	private BaseScene currentScene;
	
	private Engine engine = ResourceManager.getInstance().engine;
	
	public enum SceneType
	{
		SCENE_LOADING,
		SCENE_MENU,
		SCENE_GAME,
		SCENE_UPGRADES,
		SCENE_STORE,
		SCENE_SETTINGS,
		SCENE_ENDED
	}
	
	//---------------------------------------------
	// CLASS LOGIC
	//---------------------------------------------
	
	public void setScene(BaseScene scene)
	{
		engine.setScene(scene);
		currentScene = scene;
		currentSceneType = scene.getSceneType();
	}
	
	public void setScene(SceneType sceneType)
	{
		switch (sceneType)
		{
			case SCENE_LOADING:
				setScene(loadingScene);
				break;
			case SCENE_MENU:
				setScene(menuScene);
				break;
			case SCENE_GAME:
				setScene(gameScene);
				break;
			case SCENE_UPGRADES:
				setScene(upgradesScene);
				break;
			case SCENE_STORE:
				setScene(storeScene);
				break;
			case SCENE_SETTINGS:
				setScene(settingsScene);
				break;
			case SCENE_ENDED:
				setScene(endedScene);
				break;
			default:
				break;
		}
	}
	
	public void createLoadingScene(OnCreateSceneCallback pOnCreateSceneCallback)
	{
		ResourceManager.getInstance().loadLoadingScreen();
		loadingScene = new LoadingScene();
		currentScene = loadingScene;
		pOnCreateSceneCallback.onCreateSceneFinished(loadingScene);
	}
	
	public void createMenuScene()
	{
		
		ResourceManager.getInstance().loadMenuResources();
		menuScene = new MainMenuScene();
        SceneManager.getInstance().setScene(menuScene);
        disposeLoadingScene();
	    
	}
	
	
	
	private void disposeLoadingScene()
	{
		ResourceManager.getInstance().unloadLoadingScreen();
		loadingScene.disposeScene();
		loadingScene = null;
	}
	
	public void loadGameScene(final Engine mEngine)
	{
		gameScene = new GameScene();
		ResourceManager.getInstance().unloadMenuTextures();
		ResourceManager.getInstance().loadGameResources();
		setScene(gameScene);		
	}
	
	public void loadMenuScene(final Engine mEngine)
	{
		setScene(menuScene);
		gameScene.disposeScene();
		//ResourceManager.getInstance().unloadGameTextures();
		/*
		mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() 
		{
            public void onTimePassed(final TimerHandler pTimerHandler) 
            {
            	mEngine.unregisterUpdateHandler(pTimerHandler);
            	ResourcesManager.getInstance().loadMenuTextures();
        		setScene(menuScene);
            }
		}));
		*/
	}
	
	//---------------------------------------------
	// GETTERS AND SETTERS
	//---------------------------------------------
	
	public static SceneManager getInstance()
	{
		return INSTANCE;
	}
	
	public SceneType getCurrentSceneType()
	{
		return currentSceneType;
	}
	
	public BaseScene getCurrentScene()
	{
		return currentScene;
	}
}