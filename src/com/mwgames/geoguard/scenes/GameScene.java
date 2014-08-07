package com.mwgames.geoguard.scenes;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import org.andengine.engine.Engine.EngineLock;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.Entity;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.util.GLState;
import org.andengine.util.HorizontalAlign;

import android.util.Log;
import android.widget.Toast;

import com.mwgames.geoguard.base.BaseScene;
import com.mwgames.geoguard.entities.Bullet;
import com.mwgames.geoguard.entities.Ship;
import com.mwgames.geoguard.entities.Target;
import com.mwgames.geoguard.util.ResourceManager;
import com.mwgames.geoguard.util.SceneManager;
import com.mwgames.geoguard.util.SceneManager.SceneType;


public class GameScene extends BaseScene implements IOnSceneTouchListener
{
	private static final int LAYER_COUNT = 2;
	private static final int LAYER_BACKGROUND = 0;
	private static final int LAYER_ACTIVITY = LAYER_BACKGROUND + 1;
	
	private boolean mHolding = true;
	private int startGameCountdown = 5;
	private Text countdownText;
	private String[] countdownTextStrings = {" GO!"," 1 "," 2 "," 3 ","Get Ready ... "};
	
	private HUD gameHUD;
	private Text scoreText;
	private Ship mShip;
	
	/* object lists */
	private LinkedList targetll;
	private LinkedList targetsToBeAdded;
	private LinkedList bulletll;
	private LinkedList bulletsToBeAdded;

	public static int initialTargetDuration = 7;
    private float targetDuration;
    private float bulletDuration;
    
	/* object handlers */
	private float mTargetSpawnDelay;
	private float mBulletSpawnDelay;
	private TimerHandler targetTimerHandler;
	private TimerHandler bulletTimerHandler;
    private boolean canSendBullet;
	
	private boolean isTouchActive;
	private float fingerX;
	private float fingerY;
	private float fingerA;
	private float fingerADeg;

	public static int mTargets = 0;
	public static int mBullets = 0;
	
	@Override
	public void createScene()
	{
		createLayers();
    	createBackground();
    	createCountdownText();
    	createHUD();
		createShip();
		createObjects();
		createDurations();
    	
		ResourceManager.getInstance().unloadMenuTextures();
		engine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() 
		{
            public void onTimePassed(final TimerHandler pTimerHandler) 
            {
            	startGameCountdown--;
            	countdownText.setText(countdownTextStrings[startGameCountdown]);
            	if(startGameCountdown <= 0){
            		engine.unregisterUpdateHandler(pTimerHandler);
            		mHolding = false;
            	}
            }
		}));
		while(mHolding);
		
		this.setOnSceneTouchListener(this);
		createCollisionHandler();
		createTargetSpawnTimeHandler();
		createBulletSpawnTimeHandler();
		
	}

	private void createCollisionHandler() {
		IUpdateHandler detectCollisionsAndBounds = new IUpdateHandler() {
		    @Override
		    public void reset() {
		    }
		 
		    @Override
		    public void onUpdate(float pSecondsElapsed) {
		    	if(mShip != null){
            	Log.d("ShipDB", "Ship Health: " + Integer.toString(mShip.getHealth()));
		 
		        Iterator<Target> targets = targetll.iterator();
		        Iterator<Bullet> bullets = bulletll.iterator();
		        Target _target;
		        Bullet _bullet;
		        boolean remove = false;
		        boolean hit = false;
		        
		        while (targets.hasNext()) {
		            _target = targets.next();

		            //remove if outOfBounds
		            if( (_target.getStartX() > camera.getCenterX() & _target.getX() <= camera.getCenterX() + 1) ||
		            	(_target.getStartX() < camera.getCenterX() & _target.getX() >= camera.getCenterX() - 1) ||
		            	(_target.getStartY() > camera.getCenterY() & _target.getY() <= camera.getCenterY() + 1) ||
		            	(_target.getStartY() < camera.getCenterY() & _target.getY() >= camera.getCenterY() - 1) ){
			            removeTarget(_target, targets);
		            	break;
		            }
		            
		            //check ship collision
		            if(_target.collidesWith(mShip)){
		            	mShip.decrementHealth();
			            removeTarget(_target, targets);
		            	if(!mShip.isAlive()){

		            		final EngineLock engineLock = engine.getEngineLock();
		            		engineLock.lock();
		            		
		            		mShip.detachSelf();
		            		mShip.dispose();
		            		mShip = null;
		            		engine.unregisterUpdateHandler(bulletTimerHandler);
		            		engineLock.unlock();
		            		endGame();
		            	}
		            	break;
		            }
		            
		            //check active bullets for collision
		            while(bullets.hasNext()){
		            	_bullet = bullets.next();
		            	
		            	//remove if outOfBounds
			            if( _bullet.getX() >= camera.getWidth()   ||
			            	_bullet.getX() <= 0                   ||
			            	_bullet.getY() >= camera.getHeight()  ||
			            	_bullet.getY() <= 0 	         
			                ){
			            	removeBullet(_bullet, bullets);
			            	continue;
			            }
			            
			            if(_target.collidesWith(_bullet)){
			            	removeBullet(_bullet, bullets);
			            	hit = true;
			            }
		            }
		            if(hit){
		            	removeTarget(_target, targets);
		            	hit = false;
		            }
		        }
		        targetll.addAll(targetsToBeAdded);
		        targetsToBeAdded.clear();

		        bulletll.addAll(bulletsToBeAdded);
		        bulletsToBeAdded.clear();
		    }
		    }
		};
		this.getChildByIndex(LAYER_ACTIVITY).registerUpdateHandler(detectCollisionsAndBounds);
		
	}

	
	@Override
	public void onBackKeyPressed()
	{
		SceneManager.getInstance().loadMenuScene(engine);
	}

	@Override
	public SceneType getSceneType()
	{
		return SceneType.SCENE_GAME;
	}

	@Override
	public void disposeScene()
	{
		for(int i = 0; i < LAYER_COUNT; i++) {
			this.getChildByIndex(i).detachChildren();
			this.detachChild(i);
			
		}
		this.detachSelf();
		this.dispose();
	}
	
	private void createLayers(){
		for(int i = 0; i < LAYER_COUNT; i++) {
			attachChild(new Entity());
		}
	}
	private void createBackground(){
		setBackgroundEnabled(false);
		getChildByIndex(LAYER_BACKGROUND).attachChild(new Sprite(
				(ResourceManager.CAMERA_WIDTH - resourceManager.gameBackgroundRegion.getWidth()) / 2,
				(ResourceManager.CAMERA_HEIGHT - resourceManager.gameBackgroundRegion.getHeight()) / 2, 
				resourceManager.gameBackgroundRegion,
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
	private void createCountdownText()
	{
		countdownText = new Text(20, 420, resourceManager.font, "Get Ready ...  3  2  1  GO!", new TextOptions(HorizontalAlign.CENTER), vbom);
		countdownText.setPosition(camera.getCenterX(), camera.getCenterY() - 100);	
		countdownText.setText(countdownTextStrings[5]);
		this.attachChild(countdownText);	
	}
	private void createHUD()
	{
		gameHUD = new HUD();
		
		scoreText = new Text(20, 420, resourceManager.font, "Score: 0123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
		scoreText.setPosition(0, 0);	
		scoreText.setText("Score: 0");
		gameHUD.attachChild(scoreText);
		
		camera.setHUD(gameHUD);
	}
	private void createShip(){
		mShip = new Ship(ResourceManager.getInstance().CENTER_X, 
						 ResourceManager.getInstance().CENTER_Y, 
						 ResourceManager.getInstance().shipRegion, 
						 vbom, 3, true);
		this.getChildByIndex(LAYER_ACTIVITY).attachChild(mShip);
		mShip.setRotation(0);
	}
	private void createObjects(){
		targetll = new LinkedList();
		targetsToBeAdded = new LinkedList();
		bulletll = new LinkedList();
		bulletsToBeAdded = new LinkedList();
	}
	private void createDurations(){
		mTargetSpawnDelay = 3f;
		mBulletSpawnDelay = 0.5f;
		
		targetDuration = initialTargetDuration - mShip.getEnvLevel();
		bulletDuration = initialTargetDuration - mShip.getEnvLevel();
	}
	//Creates a Timer Handler used to Spawn Targets
	private void createTargetSpawnTimeHandler() {
		targetTimerHandler = new TimerHandler(mTargetSpawnDelay, true,
		new ITimerCallback() {
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				addTarget();
			}
		});
		engine.registerUpdateHandler(targetTimerHandler);
	}
	//Creates a Timer Handler used to throttle Bullet spawns
	private void createBulletSpawnTimeHandler() {
	    bulletTimerHandler = new TimerHandler(mBulletSpawnDelay, true,
	    new ITimerCallback() {

			@Override
	        public void onTimePassed(TimerHandler pTimerHandler) {
				canSendBullet = true;
	            if(isTouchActive) addBullet();
	        }
	    });
	    engine.registerUpdateHandler(bulletTimerHandler);
	}
	
	@SuppressWarnings("unchecked")
	private void addTarget(){
		mTargets++;
		Random rand = new Random();
		ITextureRegion region = rand.nextBoolean() ? ResourceManager.getInstance().targetPentagonRegion : ResourceManager.getInstance().targetTriangleRegion;
		int startX = rand.nextInt((int) (camera.getWidth() - region.getWidth())) + (int) region.getWidth();
		int startY = rand.nextInt((int) (camera.getHeight()- region.getHeight())) + (int) region.getHeight();

		Log.d("TargetDB", "ActiveTargets: " + Integer.toString(mTargets));
		
		switch(rand.nextInt(4)){
			case 0:
				startX = 0;
				break;
			case 1:
				startY = 0;
				break;
			case 2:
				startX = (int) camera.getWidth();
				break;
			case 3:
				startY = (int) camera.getHeight();
				break;
			default:
				break;
		}
		Target target = new Target(startX, startY, region.deepCopy(), vbom);
	
		this.getChildByIndex(LAYER_ACTIVITY).attachChild(target);

		MoveModifier mod = new MoveModifier(targetDuration, target.getX(), camera.getCenterX(), target.getY(), camera.getCenterY());
		target.registerEntityModifier(mod.deepCopy());	
		
		targetsToBeAdded.add(target);
	}
	
	@SuppressWarnings("unchecked")
	private void addBullet(){ 
		mBullets++;
		Log.d("BulletDB", "ActiveBullets: " + Integer.toString(mBullets));
		canSendBullet = false;
		
		//int startX = rand.nextInt((int) (CENTER_X - mTargetFaceTextureRegion.getWidth())) + (int) mTargetFaceTextureRegion.getWidth();
		//int startY = rand.nextInt((int) (CAMERA_HEIGHT - mTargetFaceTextureRegion.getHeight()))
		
		final Bullet bullet = new Bullet(camera.getCenterX(), camera.getCenterY(), ResourceManager.getInstance().bulletRegion, vbom);
		//body = PhysicsFactory.createBoxBody(this.mPhysicsWorld, face, BodyType.DynamicBody, objectFixtureDef);
		
		float destX = (float) (Math.cos(fingerA) * ResourceManager.getInstance().CAMERA_DIAGONAL + camera.getCenterX());
		
		float destY = (float) (Math.sin(fingerA) * ResourceManager.getInstance().CAMERA_DIAGONAL + camera.getCenterY());
	
		this.getChildByIndex(LAYER_ACTIVITY).attachChild(bullet);

		bullet.setRotation(fingerADeg);
		MoveModifier mod = new MoveModifier(bulletDuration, camera.getCenterX(), destX, camera.getCenterY(), destY);
		bullet.registerEntityModifier(mod.deepCopy());	
		
		bulletsToBeAdded.add(bullet);
	}	
	
	@SuppressWarnings("rawtypes")
	public void removeTarget(final Target target, Iterator it) {
	    engine.runOnUpdateThread(new Runnable() {
	 
	        @Override
	        public void run() {
	        	mTargets--;
	        	getChildByIndex(LAYER_ACTIVITY).detachChild(target);
	        }
	    });
	    it.remove();
	}
	
	@SuppressWarnings("rawtypes")
	public void removeBullet(final Bullet bullet, Iterator it) {
	    engine.runOnUpdateThread(new Runnable() {
	 
	        @Override
	        public void run() {
	        	mBullets--;
	            getChildByIndex(LAYER_ACTIVITY).detachChild(bullet);
	        }
	    });
	    it.remove();
	}
	
	private void endGame(){
	    activity.runOnUiThread(new Runnable() {
	        @Override
	        public void run() {
	           Toast.makeText(activity.getApplicationContext(), "You Fail at This Game", Toast.LENGTH_SHORT).show();
	        }
	    });
	}
	
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		if(mShip != null){
			if(pSceneTouchEvent.isActionDown()) {
				fingerX = pSceneTouchEvent.getX();
				fingerY = pSceneTouchEvent.getY();
				fingerA = (float) Math.atan2(fingerY - camera.getCenterY(), fingerX - camera.getCenterX());
				fingerADeg = (float) (fingerA * 180 / Math.PI);
				mShip.setRotation(fingerADeg);
				isTouchActive = true;
				if(canSendBullet){
					addBullet();
					bulletTimerHandler.reset();
				}
				return true;
			}
			if(pSceneTouchEvent.isActionMove()) {
				fingerX = pSceneTouchEvent.getX();
				fingerY = pSceneTouchEvent.getY();
				fingerA = (float) Math.atan2(fingerY - camera.getCenterY(), fingerX - camera.getCenterX());
				fingerADeg = (float) (fingerA * 180 / Math.PI);
				mShip.setRotation(fingerADeg);
				return true;
			}
			if(pSceneTouchEvent.isActionUp()) {
				isTouchActive = false;
				return true;
			}
		}
		return false;
	}	
}