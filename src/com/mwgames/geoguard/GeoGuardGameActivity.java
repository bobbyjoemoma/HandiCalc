package com.mwgames.geoguard;
//TEST CHANGE COMMIT 2
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import android.graphics.Point;
import android.view.Display;

public class GeoGuardGameActivity extends SimpleBaseGameActivity{
	
	// ===========================================================
	// Constants
	// ===========================================================
	
	private static Point windowDimensions = new Point();
	private static int CAMERA_WIDTH = 0;
	private static int CAMERA_HEIGHT = 0;
	
	// ===========================================================
	// Fields
	// ===========================================================

	private Camera mCamera;
	
	private BitmapTextureAtlas mBitmapTextureAtlas;

	private TiledTextureRegion mShipFaceTextureRegion;
	private TiledTextureRegion mBulletFaceTextureRegion;
	private TiledTextureRegion mTargetFaceTextureRegion;

	private Scene mScene;

	private PhysicsWorld mPhysicsWorld;
	
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
		
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		
		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera);

	}

	@Override
	protected void onCreateResources() {
		
	//	BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		//this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 64, 64, TextureOptions.BILINEAR);
//		this.mShipFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "face_box_tiled.png", 0, 0, 2, 1); // 64x32
	//	this.mBulletFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "face_circle_tiled.png", 0, 32, 2, 1); // 64x32
		//this.mTargetFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "face_circle_tiled.png", 0, 32, 2, 1); // 64x32
	//	this.mBitmapTextureAtlas.load();
		
	}

	@Override
	protected Scene onCreateScene() {
		// TODO Auto-generated method stub
		return null;
	}

	// ===========================================================
	// Methods
	// ===========================================================

}
