package com.mwgames.geoguard;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

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
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.debug.Debug;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import android.graphics.Point;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.util.Log;
import android.view.Display;

public class GeoGuardGameActivity extends SimpleBaseGameActivity implements IAccelerationListener, IOnSceneTouchListener{
	
	// ===========================================================
	// Constants
	// ===========================================================
	
	/* camera & positioning */
	private static Point windowDimensions = new Point();
	public static int CAMERA_WIDTH = 0;
	public static int CAMERA_HEIGHT = 0;
	public static int CENTER_X = 0;
	public static int CENTER_Y = 0;
	public static float CAMERA_DIAGONAL = (float) Math.sqrt(Math.pow(CAMERA_WIDTH, 2) + Math.pow(CAMERA_HEIGHT, 2));
	
	public static int mTargets = 0;
	
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

	private Text mScoreText;
	private IFont mFont;
	
	protected boolean mGameRunning;
	
	// ===========================================================
	// Constructors
	// ===========================================================
	
	/* game constructor not needed. engine generates needed constructors through overridden methods
		public GeoGuardGameActivity() {
			// TODO Auto-generated constructor stub
		}
	*/
	
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
		mShip = new Ship(CENTER_X, CENTER_Y, this.mShipFaceTextureRegion, this.getVertexBufferObjectManager());
		this.mScene.getChildByIndex(LAYER_ACTIVITY).attachChild(mShip);
		Log.d("ShipDB", "creating ship with :::  Xpos: " + Float.toString(CENTER_X) + "  Ypos: " + Float.toString(CENTER_Y));
		//create empty object lists
		targetll = new LinkedList();
		targetsToBeAdded = new LinkedList();
		bulletll = new LinkedList();
		bulletsToBeAdded = new LinkedList();
		
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
				
		createSpriteSpawnTimeHandler();
		
		bulletPool = new BulletPool(mBulletFaceTextureRegion, this.getVertexBufferObjectManager());
		targetPool = new TargetPool(mTargetFaceTextureRegion, this.getVertexBufferObjectManager());
		
		IUpdateHandler detectTargetOutOfBounds = new IUpdateHandler() {
		    @Override
		    public void reset() {
		    }
		 
		    @Override
		    public void onUpdate(float pSecondsElapsed) {
		 
		        Iterator<Target> targets = targetll.iterator();
		        Target _target;
		        boolean remove = false;
		        
		        while (targets.hasNext()) {
		            _target = targets.next();
		            
		            if(_target.getStartX() > CENTER_X & _target.getX() <= CENTER_X){
			            Log.d("RemoveLogic", "Cond-1 eval = true :::   StartX: " + Float.toString(_target.getStartX()) + "  CurrentX: " + Float.toString(_target.getX()));
			            remove = true;
		            }
		            if(_target.getStartX() < CENTER_X & _target.getX() >= CENTER_X){
			            Log.d("RemoveLogic", "Cond-2 eval = true :::   StartX: " + Float.toString(_target.getStartX()) + "  CurrentX: " + Float.toString(_target.getX()));
			            remove = true;
		            }
		            if(_target.getStartY() > CENTER_Y & _target.getY() <= CENTER_Y){
			            Log.d("RemoveLogic", "Cond-3 eval = true :::   StartY: " + Float.toString(_target.getStartY()) + "  CurrentY: " + Float.toString(_target.getY()));
			            remove = true;
		            }
		            if(_target.getStartY() < CENTER_Y & _target.getY() >= CENTER_Y){
			            Log.d("RemoveLogic", "Cond-4 eval = true :::   StartY: " + Float.toString(_target.getStartY()) + "  CurrentY: " + Float.toString(_target.getY()));
			            remove = true;
		            } 
		            if(remove){
		            	removeTarget(_target, targets);
		            }
		            remove = false;
		        }
		        targetll.addAll(targetsToBeAdded);
		        targetsToBeAdded.clear();
		    }
		};
		mScene.getChildByIndex(LAYER_ACTIVITY).registerUpdateHandler(detectTargetOutOfBounds);
		
		return this.mScene;
	}
	
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		if(this.mPhysicsWorld != null) {
			if(pSceneTouchEvent.isActionDown()) {
				final float touchX = pSceneTouchEvent.getX();
				final float touchY = pSceneTouchEvent.getY();
				final float angle = (float) Math.atan2(touchY - CENTER_Y, touchX - CENTER_X);
				mShip.setRotation(angle);
				//addBullet(touchX, touchY, angle);
				/*Bullet bullet = new Bullet(CENTER_X, CENTER_Y, this.mBulletFaceTextureRegion, this.getVertexBufferObjectManager());
				Body body = PhysicsFactory.createBoxBody(this.mPhysicsWorld, bullet, BodyType.DynamicBody, BULLET_FIXTURE_DEF);
				this.mScene.getChildByIndex(LAYER_ACTIVITY).attachChild(bullet);
				this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(bullet, body, true, true));
				body.setBullet(true);
				body.setLinearVelocity((float)Math.cos(pSceneTouchEvent.getX())*5, (float)Math.sin(pSceneTouchEvent.getY()) * 5);*/
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
		//final Vector2 gravity = Vector2Pool.obtain(pAccelerationData.getX(), pAccelerationData.getY());
		//this.mPhysicsWorld.setGravity(gravity);
		//Vector2Pool.recycle(gravity);	
	}
	
	@Override
	public void onResumeGame() {
		super.onResumeGame();

		//this.enableAccelerationSensor(this);
	}

	@Override
	public void onPauseGame() {
		super.onPauseGame();

		//this.disableAccelerationSensor();
	}
	
	// ===========================================================
	// Methods
	// ===========================================================
	/*
	private void addBullet(float pX, float pY, float angle) {
		final Bullet bullet = new Bullet(CENTER_X, CENTER_Y, this.mBulletFaceTextureRegion, this.getVertexBufferObjectManager());
		//body = PhysicsFactory.createBoxBody(this.mPhysicsWorld, face, BodyType.DynamicBody, objectFixtureDef);
		
		
		float destX = (pX - CENTER_X) / distanceFromCenter;
		float destY = (pY - CENTER_Y) / distanceFromCenter;
		
		
		int destX = CENTER_X CAMERA_WIDTH;
		int destY;
		bullet.setRotation(angle);
		MoveModifier mod = new MoveModifier();
		this.mScene.attachChild(bullet);
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(face, body, true, true));
	}
	*/
	/**
	 * Creates a Timer Handler used to Spawn Sprites
	 */
	private void createSpriteSpawnTimeHandler() {
	    TimerHandler spriteTimerHandler;
	    float mEffectSpawnDelay = 1f;
	 
	    spriteTimerHandler = new TimerHandler(mEffectSpawnDelay, true,
	    new ITimerCallback() {
	 
	        @Override
	        public void onTimePassed(TimerHandler pTimerHandler) {
	            addTarget();
	        }
	    });
	 
	    getEngine().registerUpdateHandler(spriteTimerHandler);
	}
	
	@SuppressWarnings("unchecked")
	private void addTarget(){
		mTargets++;
		Random rand = new Random();
		int startX = rand.nextInt((int) (GeoGuardGameActivity.CAMERA_WIDTH - mTargetFaceTextureRegion.getWidth())) + (int) mTargetFaceTextureRegion.getWidth();
		int startY = rand.nextInt((int) (GeoGuardGameActivity.CAMERA_HEIGHT - mTargetFaceTextureRegion.getHeight())) + (int) mTargetFaceTextureRegion.getHeight();

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
		Log.d("TargetDB", "creating target with :::   StartX: " + Float.toString(startX) + "  StartY: " + Float.toString(startY));
		Target target = new Target(startX,startY,mTargetFaceTextureRegion.deepCopy(),this.getVertexBufferObjectManager());
	
		mScene.getChildByIndex(LAYER_ACTIVITY).attachChild(target);

		float duration = initialTargetDuration - mShip.getLevelModifier();
		
		MoveModifier mod = new MoveModifier(duration, target.getX(), CENTER_X, target.getY(), CENTER_Y);
		target.registerEntityModifier(mod.deepCopy());	
		
		targetsToBeAdded.add(target);
	}
	
	public void removeTarget(final Target target, Iterator it) {
	    runOnUpdateThread(new Runnable() {
	 
	        @Override
	        public void run() {
	        	mTargets--;
	        	Log.d("TargetDB", "removing target with :::   StartX: " + Float.toString(target.getX()) + "  StartY: " + Float.toString(target.getY()));
	            mScene.getChildByIndex(LAYER_ACTIVITY).detachChild(target);
	        }
	    });
	    it.remove();
	}
	
	public void removeBullet(final Bullet bullet, Iterator it) {
	    runOnUpdateThread(new Runnable() {
	 
	        @Override
	        public void run() {
	            mScene.getChildByIndex(LAYER_ACTIVITY).detachChild(bullet);
	        }
	    });
	    it.remove();
	}
}
