package com.mwgames.geoguard;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import org.andengine.engine.Engine.EngineLock;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.Entity;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.IFont;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.debug.Debug;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.google.android.gms.games.Player;

import android.graphics.Point;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

public class GeoGuardGameActivity extends SimpleBaseGameActivity implements IAccelerationListener, IOnSceneTouchListener{
	
	// ===========================================================
	// Constants
	// ===========================================================
	
	/* service constants */
	public static boolean mSignedIn = false;
	public static String mUserName = "";
	
	public static Player mPlayer;
	
	/* camera & positioning */
	private static Point windowDimensions = new Point();
	public static int CAMERA_WIDTH = 0;
	public static int CAMERA_HEIGHT = 0;
	public static int CENTER_X = 0;
	public static int CENTER_Y = 0;
	public static float CAMERA_DIAGONAL = 0;
	
	public static int mTargets = 0;
	public static int mBullets = 0;
	
	public static int initialTargetDuration = 8;
	
	/* scene layer handling */
	private static final int LAYER_COUNT = 3;
	private static final int LAYER_BACKGROUND = 0;
	private static final int LAYER_ACTIVITY = LAYER_BACKGROUND + 1;
	private static final int LAYER_OVERLAY = LAYER_ACTIVITY + 1;
	
	/* entity Categories */
	public static final short CATEGORYBIT_WALL     = 1 << 0;
	public static final short CATEGORYBIT_SHIP     = 1 << 1;
	public static final short CATEGORYBIT_SHIELD   = 1 << 2;
	public static final short CATEGORYBIT_BULLET   = 1 << 3;
	public static final short CATEGORYBIT_TARGET   = 1 << 4;
	public static final short CATEGORYBIT_POWERUP  = 1 << 5;

	/* collision masking */
	public static final short MASKBITS_WALL     = CATEGORYBIT_WALL;
	public static final short MASKBITS_SHIP     = CATEGORYBIT_TARGET + CATEGORYBIT_POWERUP; 
	public static final short MASKBITS_SHIELD   = CATEGORYBIT_TARGET + CATEGORYBIT_POWERUP; 
	public static final short MASKBITS_BULLET   = CATEGORYBIT_WALL + CATEGORYBIT_TARGET + CATEGORYBIT_POWERUP;
	public static final short MASKBITS_TARGET   = CATEGORYBIT_SHIP + CATEGORYBIT_SHIELD;
	public static final short MASKBITS_POWERUP  = CATEGORYBIT_SHIP + CATEGORYBIT_SHIELD;

	/* fixture definitions for bodies */
	public static final FixtureDef WALL_FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f, false, CATEGORYBIT_WALL, MASKBITS_WALL, (short)0);
	public static final FixtureDef SHIP_FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f, false, CATEGORYBIT_SHIP, MASKBITS_SHIP, (short)0);
	public static final FixtureDef SHIELD_FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f, false, CATEGORYBIT_SHIELD, MASKBITS_SHIELD, (short)0);
	public static final FixtureDef BULLET_FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f, false, CATEGORYBIT_BULLET, MASKBITS_BULLET, (short)0);
	public static final FixtureDef TARGET_FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f, false, CATEGORYBIT_TARGET, MASKBITS_TARGET, (short)0);
	public static final FixtureDef POWERUP_FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f, false, CATEGORYBIT_POWERUP, MASKBITS_POWERUP, (short)0);
	
	// ===========================================================
	// Fields
	// ===========================================================

	/* camera */
	private Camera mCamera;
	
	/* textures */
	private BitmapTextureAtlas mBitmapTextureAtlas;
	private ITextureRegion mShipFaceTextureRegion;
	private ITextureRegion mBulletFaceTextureRegion;
	private ITextureRegion mTargetFaceTextureRegion;

	/* ship instance */
	private Ship mShip;
 
	/* object lists */
	private LinkedList targetll;
	private LinkedList targetsToBeAdded;
	private LinkedList bulletll;
	private LinkedList bulletsToBeAdded;
	
	private BulletPool bulletPool;
	private TargetPool targetPool;
	
	private PhysicsWorld mPhysicsWorld;
	
	private Scene mScene;
	
	private float mTargetSpawnDelay;
	private float mBulletSpawnDelay;
	private TimerHandler targetTimerHandler;
	private TimerHandler bulletTimerHandler;
    private boolean canSendBullet;
	
    private float targetDuration;
    private float bulletDuration;
    
	private boolean isTouchActive;
	private float fingerX;
	private float fingerY;
	private float fingerA;
	private float fingerADeg;
	
	private Text mScoreText;
	private IFont mFont;
	
	protected boolean mGameRunning;
	
	// ===========================================================
	// Constructors
	// ===========================================================
	
	// ===========================================================
	// Getters & Setters
	// ===========================================================
	public static int getCameraWidth() {
		return CAMERA_WIDTH;
	}
	static void setCameraWidth(int cameraWidth) {
		GeoGuardGameActivity.CAMERA_WIDTH = cameraWidth;
	}
	public static int getCameraHeight() {
		return CAMERA_HEIGHT;
	}
	static void setCameraHeight(int cameraHeight) {
		GeoGuardGameActivity.CAMERA_HEIGHT = cameraHeight;
	}
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public EngineOptions onCreateEngineOptions() {
		//create window dimensions via window manager
		Display display = getWindowManager().getDefaultDisplay();
		display.getSize(windowDimensions);
		setCameraWidth(windowDimensions.x);
		setCameraHeight(windowDimensions.y);
		CENTER_X = CAMERA_WIDTH / 2;
		CENTER_Y = CAMERA_HEIGHT / 2;
		CAMERA_DIAGONAL = (float) Math.sqrt(Math.pow(CAMERA_WIDTH, 2) + Math.pow(CAMERA_HEIGHT, 2)) / 2;
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera);
		
	}

	@Override
	protected void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		
		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mShipFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "SHIP.png", 0, 0); 
		this.mBulletFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "bullet.png", 0, 32);
		this.mTargetFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "PoopRock.png", 32, 0); 
		this.mBitmapTextureAtlas.load();
		
	}

	@Override
	protected Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());
		
		this.mScene = new Scene();
		for(int i = 0; i < LAYER_COUNT; i++) {
			this.mScene.attachChild(new Entity());
		}
		
		this.mScene.setBackground(new Background(255.0f, 255.0f, 255.0f));
		/* uncomment to create background image sprite
		this.mscene.setBackgroundEnabled(false);
		this.mScene.getChildByIndex(LAYER_BACKGROUND).attachChild(new Sprite(0, 0, this.mBackgroundTextureRegion, this.getVertexBufferObjectManager()));
		*/
		this.mScene.setOnSceneTouchListener(this);
		
		/* uncomment to implement text score on overlay layer
		this.mScoreText = new Text(5, 5, this.mFont, "Score: 0", "Score: XXXXXXX".length(), this.getVertexBufferObjectManager());
		this.mScoreText.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		this.mScoreText.setAlpha(0.5f);
		this.mScene.getChildByIndex(LAYER_OVERLAY).attachChild(this.mScoreText);
		*/
		
		//attach ship to activity layer
		mShip = new Ship(CENTER_X, CENTER_Y, this.mShipFaceTextureRegion, this.getVertexBufferObjectManager(), 3, true);
		this.mScene.getChildByIndex(LAYER_ACTIVITY).attachChild(mShip);
		Log.d("ShipDB", "creating ship with :::  Xpos: " + Float.toString(CENTER_X) + "  Ypos: " + Float.toString(CENTER_Y));
		Log.d("ShipDB", "ScreenDiagonal :::  " + Float.toString(CAMERA_DIAGONAL));
		
		mShip.setRotation(0);
		
		mTargetSpawnDelay = 3f;
		mBulletSpawnDelay = 0.5f;
		
		targetDuration = initialTargetDuration - mShip.getEnvLevel();
		bulletDuration = initialTargetDuration - mShip.getEnvLevel();
		
		//create empty object lists
		targetll = new LinkedList();
		targetsToBeAdded = new LinkedList();
		bulletll = new LinkedList();
		bulletsToBeAdded = new LinkedList();
		
		/*
		//attach physics world to activity layer
		this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false, 3, 2);

		final Rectangle ground = new Rectangle(0, CAMERA_HEIGHT - 2, CAMERA_WIDTH, 2, this.getVertexBufferObjectManager());
		final Rectangle roof = new Rectangle(0, 0, CAMERA_WIDTH, 2, this.getVertexBufferObjectManager());
		final Rectangle left = new Rectangle(0, 0, 2, CAMERA_HEIGHT, this.getVertexBufferObjectManager());
		final Rectangle right = new Rectangle(CAMERA_WIDTH - 2, 0, 2, CAMERA_HEIGHT, this.getVertexBufferObjectManager());

		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, ground, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, roof, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyType.StaticBody, wallFixtureDef);

		this.mScene.getChildByIndex(LAYER_ACTIVITY).attachChild(ground);
		this.mScene.getChildByIndex(LAYER_ACTIVITY).attachChild(roof);
		this.mScene.getChildByIndex(LAYER_ACTIVITY).attachChild(left);
		this.mScene.getChildByIndex(LAYER_ACTIVITY).attachChild(right);
		
		this.mScene.getChildByIndex(LAYER_ACTIVITY).registerUpdateHandler(this.mPhysicsWorld);
		*/
		
		createTargetSpawnTimeHandler();
		createBulletSpawnTimeHandler();
		
		//bulletPool = new BulletPool(mBulletFaceTextureRegion, this.getVertexBufferObjectManager());
		//targetPool = new TargetPool(mTargetFaceTextureRegion, this.getVertexBufferObjectManager());
		
		//particle destructors on outOfBounds and collisions
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
		            if( (_target.getStartX() > CENTER_X & _target.getX() <= CENTER_X + 1) ||
		            	(_target.getStartX() < CENTER_X & _target.getX() >= CENTER_X - 1) ||
		            	(_target.getStartY() > CENTER_Y & _target.getY() <= CENTER_Y + 1) ||
		            	(_target.getStartY() < CENTER_Y & _target.getY() >= CENTER_Y - 1) ){
			            removeTarget(_target, targets);
		            	break;
		            }
		            
		            //check ship collision
		            if(_target.collidesWith(mShip)){
		            	mShip.decrementHealth();
			            removeTarget(_target, targets);
		            	if(!mShip.isAlive()){

		            		final EngineLock engineLock = getEngine().getEngineLock();
		            		engineLock.lock();
		            		
		            		mShip.detachSelf();
		            		mShip.dispose();
		            		mShip = null;
		            		getEngine().unregisterUpdateHandler(bulletTimerHandler);
		            		engineLock.unlock();
		            		endGame();
		            	}
		            	break;
		            }
		            
		            //check active bullets for collision
		            while(bullets.hasNext()){
		            	_bullet = bullets.next();
		            	
		            	//remove if outOfBounds
			            if( _bullet.getX() >= CAMERA_WIDTH   ||
			            	_bullet.getX() <= 0              ||
			            	_bullet.getY() >= CAMERA_HEIGHT  ||
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
		mScene.getChildByIndex(LAYER_ACTIVITY).registerUpdateHandler(detectCollisionsAndBounds);
		/*
		IUpdateHandler detectBulletOutOfBounds = new IUpdateHandler() {
		    @Override
		    public void reset() {
		    }
		 
		    @Override
		    public void onUpdate(float pSecondsElapsed) {
		 
		        Iterator<Bullet> bullets = bulletll.iterator();
		        Bullet _bullet;
		        boolean remove = false;
		        
		        while (bullets.hasNext()) {
		            _bullet = bullets.next();
		            
		            if(_bullet.getX() >= CAMERA_WIDTH || _bullet.getX() <= 0){
			            remove = true;
		            }
		            if(_bullet.getY() >= CAMERA_HEIGHT || _bullet.getY() <= 0){
			            remove = true;
		            }
		            if(remove){
		            	removeBullet(_bullet, bullets);
		            }
		            remove = false;
		        }
		        targetll.addAll(targetsToBeAdded);
		        targetsToBeAdded.clear();
		    }
		};
		mScene.getChildByIndex(LAYER_ACTIVITY).registerUpdateHandler(detectBulletOutOfBounds);
		*/
		Log.d("SceneDB", "::: Built Scene :::");
		return this.mScene;
	}
	
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		if(mShip != null){
			if(pSceneTouchEvent.isActionDown()) {
				fingerX = pSceneTouchEvent.getX();
				fingerY = pSceneTouchEvent.getY();
				fingerA = (float) Math.atan2(fingerY - CENTER_Y, fingerX - CENTER_X);
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
				fingerA = (float) Math.atan2(fingerY - CENTER_Y, fingerX - CENTER_X);
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
	
	@Override
	public void onAccelerationAccuracyChanged(AccelerationData pAccelerationData) {
	}	
	@Override
	public void onAccelerationChanged(AccelerationData pAccelerationData) {
	}
	
	@Override
	public void onResumeGame() {
		super.onResumeGame();
	}
	@Override
	public void onPauseGame() {
		super.onPauseGame();
	}
	
	// ===========================================================
	// Methods
	// ===========================================================
	

	 
	//Creates a Timer Handler used to Spawn Targets
	private void createTargetSpawnTimeHandler() {
	    targetTimerHandler = new TimerHandler(mTargetSpawnDelay, true,
	    new ITimerCallback() {
	        @Override
	        public void onTimePassed(TimerHandler pTimerHandler) {
	            addTarget();
	        }
	    });
	    getEngine().registerUpdateHandler(targetTimerHandler);
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
	    getEngine().registerUpdateHandler(bulletTimerHandler);
	}
	
	@SuppressWarnings("unchecked")
	private void addTarget(){
		mTargets++;
		Random rand = new Random();
		int startX = rand.nextInt((int) (CAMERA_WIDTH - mTargetFaceTextureRegion.getWidth())) + (int) mTargetFaceTextureRegion.getWidth();
		int startY = rand.nextInt((int) (CAMERA_HEIGHT - mTargetFaceTextureRegion.getHeight())) + (int) mTargetFaceTextureRegion.getHeight();

		Log.d("TargetDB", "ActiveTargets: " + Integer.toString(mTargets));
		
		switch(rand.nextInt(4)){
			case 0:
				startX = 0;
				break;
			case 1:
				startY = 0;
				break;
			case 2:
				startX = CAMERA_WIDTH;
				break;
			case 3:
				startY = CAMERA_HEIGHT;
				break;
			default:
				break;
		}
		Target target = new Target(startX,startY,mTargetFaceTextureRegion.deepCopy(),this.getVertexBufferObjectManager());
	
		mScene.getChildByIndex(LAYER_ACTIVITY).attachChild(target);

		MoveModifier mod = new MoveModifier(targetDuration, target.getX(), CENTER_X, target.getY(), CENTER_Y);
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
		
		final Bullet bullet = new Bullet(CENTER_X, CENTER_Y, this.mBulletFaceTextureRegion, this.getVertexBufferObjectManager());
		//body = PhysicsFactory.createBoxBody(this.mPhysicsWorld, face, BodyType.DynamicBody, objectFixtureDef);
		
		float destX = (float) (Math.cos(fingerA) * CAMERA_DIAGONAL + CENTER_X);
		
		float destY = (float) (Math.sin(fingerA) * CAMERA_DIAGONAL + CENTER_Y);
	
		mScene.getChildByIndex(LAYER_ACTIVITY).attachChild(bullet);

		bullet.setRotation(fingerADeg);
		MoveModifier mod = new MoveModifier(bulletDuration, CENTER_X, destX, CENTER_Y, destY);
		bullet.registerEntityModifier(mod.deepCopy());	
		
		bulletsToBeAdded.add(bullet);
	}	
	
	@SuppressWarnings("rawtypes")
	public void removeTarget(final Target target, Iterator it) {
	    runOnUpdateThread(new Runnable() {
	 
	        @Override
	        public void run() {
	        	mTargets--;
	        	mScene.getChildByIndex(LAYER_ACTIVITY).detachChild(target);
	        }
	    });
	    it.remove();
	}
	
	@SuppressWarnings("rawtypes")
	public void removeBullet(final Bullet bullet, Iterator it) {
	    runOnUpdateThread(new Runnable() {
	 
	        @Override
	        public void run() {
	        	mBullets--;
	            mScene.getChildByIndex(LAYER_ACTIVITY).detachChild(bullet);
	        }
	    });
	    it.remove();
	}

	public void endGame(){
	    this.runOnUiThread(new Runnable() {
	        @Override
	        public void run() {
	           Toast.makeText(getApplicationContext(), "You Fail at This Game", Toast.LENGTH_SHORT).show();
	        }
	    });
	}
}
