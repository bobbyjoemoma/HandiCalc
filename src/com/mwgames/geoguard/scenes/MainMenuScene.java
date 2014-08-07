package com.mwgames.geoguard.scenes;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.AnimatedSpriteMenuItem;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.util.GLState;

import com.mwgames.geoguard.base.BaseScene;
import com.mwgames.geoguard.util.ResourceManager;
import com.mwgames.geoguard.util.SceneManager;
import com.mwgames.geoguard.util.SceneManager.SceneType;


public class MainMenuScene extends BaseScene implements IOnMenuItemClickListener
{
	//---------------------------------------------
	// VARIABLES
	//---------------------------------------------
	
	private MenuScene menuChildScene;
	
	private final int MENU_PLAY = 0;
	private final int MENU_UPGRADES = 1;
	private final int MENU_STORE = 2;
	private final int MENU_LEADERBOARD = 3;
	private final int MENU_ACHIEVEMENTS = 4;
	private final int MENU_SETTINGS = 5;
	private final int MENU_SOUND = 6;
	
	//---------------------------------------------
	// METHODS FROM SUPERCLASS
	//---------------------------------------------

	@Override
	public void createScene()
	{
		ResourceManager.getInstance().unloadSplashScreen();
		createBackground();
		createMenuChildScene();
	}

	@Override
	public void onBackKeyPressed()
	{
		System.exit(0);
	}

	@Override
	public SceneType getSceneType()
	{
		return SceneType.SCENE_MENU;
	}
	

	@Override
	public void disposeScene()
	{
		// TODO Auto-generated method stub
	}
	
	/*
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY)
	{
		switch(pMenuItem.getID())
		{
			case MENU_PLAY:
				//Load Game Scene!
				SceneManager.getInstance().loadGameScene(engine);
				return true;
			case MENU_OPTIONS:
				return true;
			default:
				return false;
		}
	}
	*/
	
	//---------------------------------------------
	// CLASS LOGIC
	//---------------------------------------------
	
	private void createBackground()
	{
		attachChild(new Sprite((ResourceManager.CAMERA_WIDTH - resourceManager.menuBackgroundRegion.getWidth()) / 2,
							(ResourceManager.CAMERA_HEIGHT - resourceManager.menuBackgroundRegion.getHeight()) / 2, 
							resourceManager.menuBackgroundRegion,
							vbom)
		{
    		@Override
            protected void preDraw(GLState pGLState, Camera pCamera) 
    		{
                super.preDraw(pGLState, pCamera);
                pGLState.enableDither();
            }
		});
	}
	
	private void createMenuChildScene()
	{
		menuChildScene = new MenuScene(camera);
		menuChildScene.setPosition(ResourceManager.CENTER_X,ResourceManager.CENTER_Y);
		menuChildScene.setTouchAreaBindingOnActionDownEnabled(true);
	
		final AnimatedSpriteMenuItem playMenuItem = new AnimatedSpriteMenuItem(MENU_PLAY, resourceManager.playButtonRegion, vbom)
		{
			@Override
		    public boolean onAreaTouched(final TouchEvent pAreaTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY){
				switch(pAreaTouchEvent.getAction()){
				case TouchEvent.ACTION_DOWN:
		     		if(this.getCurrentTileIndex() == ResourceManager.BUTTON_STATIC)
		     			this.setCurrentTileIndex(ResourceManager.BUTTON_PRESSED);
		         	break;
		     	case TouchEvent.ACTION_UP:
		     		if(this.getCurrentTileIndex() == ResourceManager.BUTTON_PRESSED)
		     			this.setCurrentTileIndex(ResourceManager.BUTTON_STATIC);
		     		break;
		     	}
				return super.onAreaTouched(pAreaTouchEvent, pTouchAreaLocalX,pTouchAreaLocalY);
		    }
		};
		final AnimatedSpriteMenuItem upgradesMenuItem = new AnimatedSpriteMenuItem(MENU_UPGRADES, resourceManager.upgradesButtonRegion, vbom)
		{
			@Override
		     public boolean onAreaTouched(final TouchEvent pAreaTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY){
		     	switch(pAreaTouchEvent.getAction()){
		     	case TouchEvent.ACTION_DOWN:
		     		if(this.getCurrentTileIndex() == ResourceManager.BUTTON_STATIC)
		     			this.setCurrentTileIndex(ResourceManager.BUTTON_PRESSED);
		         	break;
		     	case TouchEvent.ACTION_UP:
		     		if(this.getCurrentTileIndex() == ResourceManager.BUTTON_PRESSED)
		     			this.setCurrentTileIndex(ResourceManager.BUTTON_STATIC);
		     		break;
		     	}
		     	return super.onAreaTouched(pAreaTouchEvent, pTouchAreaLocalX,pTouchAreaLocalY);
			}
		};
		final AnimatedSpriteMenuItem storeMenuItem = new AnimatedSpriteMenuItem(MENU_STORE, resourceManager.storeButtonRegion, vbom)
		{
			@Override
		     public boolean onAreaTouched(final TouchEvent pAreaTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY){
		     	switch(pAreaTouchEvent.getAction()){
		     	case TouchEvent.ACTION_DOWN:
		     		if(this.getCurrentTileIndex() == ResourceManager.BUTTON_STATIC)
		     			this.setCurrentTileIndex(ResourceManager.BUTTON_PRESSED);
		         	break;
		     	case TouchEvent.ACTION_UP:
		     		if(this.getCurrentTileIndex() == ResourceManager.BUTTON_PRESSED)
		     			this.setCurrentTileIndex(ResourceManager.BUTTON_STATIC);
		     		break;
		     	}
		     	return super.onAreaTouched(pAreaTouchEvent, pTouchAreaLocalX,pTouchAreaLocalY);
			}
		};	
		final AnimatedSpriteMenuItem leaderboardMenuItem = new AnimatedSpriteMenuItem(MENU_LEADERBOARD, resourceManager.leaderboardButtonRegion, vbom)
		{
			@Override
		     public boolean onAreaTouched(final TouchEvent pAreaTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY){
		     	switch(pAreaTouchEvent.getAction()){
		     	case TouchEvent.ACTION_DOWN:
		     		if(this.getCurrentTileIndex() == ResourceManager.BUTTON_STATIC)
		     			this.setCurrentTileIndex(ResourceManager.BUTTON_PRESSED);
		         	break;
		     	case TouchEvent.ACTION_UP:
		     		if(this.getCurrentTileIndex() == ResourceManager.BUTTON_PRESSED)
		     			this.setCurrentTileIndex(ResourceManager.BUTTON_STATIC);
		     		break;
		     	}
		     	return super.onAreaTouched(pAreaTouchEvent, pTouchAreaLocalX,pTouchAreaLocalY);
			}
		};
		final AnimatedSpriteMenuItem achievementsMenuItem = new AnimatedSpriteMenuItem(MENU_ACHIEVEMENTS, resourceManager.achievementsButtonRegion, vbom)
		{
			@Override
		     public boolean onAreaTouched(final TouchEvent pAreaTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY){
		     	switch(pAreaTouchEvent.getAction()){
		     	case TouchEvent.ACTION_DOWN:
		     		if(this.getCurrentTileIndex() == ResourceManager.BUTTON_STATIC)
		     			this.setCurrentTileIndex(ResourceManager.BUTTON_PRESSED);
		         	break;
		     	case TouchEvent.ACTION_UP:
		     		if(this.getCurrentTileIndex() == ResourceManager.BUTTON_PRESSED)
		     			this.setCurrentTileIndex(ResourceManager.BUTTON_STATIC);
		     		break;
		     	}
		     	return super.onAreaTouched(pAreaTouchEvent, pTouchAreaLocalX,pTouchAreaLocalY);
			}
		};
		final AnimatedSpriteMenuItem settingsMenuItem = new AnimatedSpriteMenuItem(MENU_SETTINGS, resourceManager.settingsButtonRegion, vbom)
		{
			@Override
		     public boolean onAreaTouched(final TouchEvent pAreaTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY){
		     	switch(pAreaTouchEvent.getAction()){
		     	case TouchEvent.ACTION_DOWN:
		     		if(this.getCurrentTileIndex() == ResourceManager.BUTTON_STATIC)
		     			this.setCurrentTileIndex(ResourceManager.BUTTON_PRESSED);
		         	break;
		     	case TouchEvent.ACTION_UP:
		     		if(this.getCurrentTileIndex() == ResourceManager.BUTTON_PRESSED)
		     			this.setCurrentTileIndex(ResourceManager.BUTTON_STATIC);
		     		break;
		     	}
		     	return super.onAreaTouched(pAreaTouchEvent, pTouchAreaLocalX,pTouchAreaLocalY);
			}
		};
		final AnimatedSpriteMenuItem soundMenuItem = new AnimatedSpriteMenuItem(MENU_SOUND, resourceManager.soundButtonRegion, vbom)
		{
			@Override
		     public boolean onAreaTouched(final TouchEvent pAreaTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY){
		     	switch(pAreaTouchEvent.getAction()){
		     	case TouchEvent.ACTION_DOWN:
		     		if(this.getCurrentTileIndex() == ResourceManager.BUTTON_STATIC)
		     			this.setCurrentTileIndex(ResourceManager.BUTTON_PRESSED);
		         	break;
		     	case TouchEvent.ACTION_UP:
		     		if(this.getCurrentTileIndex() == ResourceManager.BUTTON_PRESSED)
		     			this.setCurrentTileIndex(ResourceManager.BUTTON_STATIC);
		     		break;
		     	}
		     	return super.onAreaTouched(pAreaTouchEvent, pTouchAreaLocalX,pTouchAreaLocalY);
			}
		};
		
		
		
		menuChildScene.addMenuItem(playMenuItem);
		menuChildScene.addMenuItem(upgradesMenuItem);
		menuChildScene.addMenuItem(storeMenuItem);
		menuChildScene.addMenuItem(leaderboardMenuItem);
		menuChildScene.addMenuItem(achievementsMenuItem);
		menuChildScene.addMenuItem(settingsMenuItem);
		menuChildScene.addMenuItem(soundMenuItem);
		
		menuChildScene.setBackgroundEnabled(false);
		
		playMenuItem.setPosition(playMenuItem.getX() - playMenuItem.getWidth()/2, playMenuItem.getY() - 250);
		upgradesMenuItem.setPosition(upgradesMenuItem.getX() - upgradesMenuItem.getWidth()/2, upgradesMenuItem.getY() - 100);
		storeMenuItem.setPosition(storeMenuItem.getX() - storeMenuItem.getWidth()/2, storeMenuItem.getY() + 20);
		leaderboardMenuItem.setPosition(leaderboardMenuItem.getX() - leaderboardMenuItem.getWidth() - 20, leaderboardMenuItem.getY() + 140);
		achievementsMenuItem.setPosition(achievementsMenuItem.getX() + 20, achievementsMenuItem.getY() + 140);
		settingsMenuItem.setPosition(upgradesMenuItem.getWidth() - settingsMenuItem.getWidth() - 50, upgradesMenuItem.getHeight() - settingsMenuItem.getHeight() - 50);
		soundMenuItem.setPosition( 50, upgradesMenuItem.getHeight() - soundMenuItem.getHeight() - 50);
		
		menuChildScene.setOnMenuItemClickListener(this);
		
		setChildScene(menuChildScene);
	}
	

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, 
			float pMenuItemLocalX, float pMenuItemLocalY) {
		switch(pMenuItem.getID())
		{
			case MENU_PLAY:
				SceneManager.getInstance().loadGameScene(engine);
				return true;
			case MENU_UPGRADES:
				return true;
			case MENU_STORE:
				return true;
			case MENU_LEADERBOARD:
				return true;
			case MENU_ACHIEVEMENTS:
				return true;
			case MENU_SETTINGS:
				return true;
			case MENU_SOUND:
				return true;
			default:
				return false;
		}
	}
}