package com.mwgames.geoguard.util;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.Texture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.snapshot.Snapshot;
import com.mwgames.geoguard.StartGameActivity;
import com.mwgames.geoguard.google.service.GGameHelper;
import com.mwgames.geoguard.google.service.GGameHelper.SignInFailureReason;

import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.widget.Button;
import android.widget.TextView;

public class ResourceManager {
	
	/** private control variables */
	private static final ResourceManager INSTANCE = new ResourceManager();
	
	/** global engine variables */
	public Engine engine;
	public StartGameActivity activity;
	public Camera camera;
	public VertexBufferObjectManager vbom;
    public GGameHelper mHelper;
	
	/** device file variables */
	public static boolean newPlayer = true;
	
	/** ==== gameState === */
	public static String gameState = 
			 "1" + //playerRank
			"-0" + //hardUnlocked
		    "-0" + //insaneUnlocked
			"-0" + //experiencePoints
			"-0" + //currency
			"-0" + //shipLevel
			"-0" + //shieldLevel
			"-0" + //turretLevel
			"-0" + //ultimate1Level
			"-0" + //ultimate2Level
			"-0" + //ultimate3Level
			"-0" + //bulletPower
			"-0" + //bulletSpeed
			"-0" + //bulletAccuracy
			"-0" + //bulletType
			"-0" + //shieldBooster
			"-0" + //doubleDamageBooster
			"-0" + //timeBooster
			"-0" 
			;
	
	public static int playerRank;
	public static int hardUnlocked;
	public static int insaneUnlocked;
	public static int experiencePoints;
	public static int currency;
	public static int shipLevel;
	public static int shieldLevel;
	public static int turretLevel;
	public static int ultimate1Level;
	public static int ultimate2Level;
	public static int ultimate3Level;
	public static int bulletPower;
	public static int bulletSpeed;
	public static int bulletAccuracy;
	public static int bulletType;
	public static int shieldBooster;
	public static int doubleDamageBooster;
	public static int timeBooster;
	
	/** Android resource variables */
	public static float CAMERA_WIDTH = 0;
	public static float CAMERA_HEIGHT = 0;
	public static float CENTER_X = 0;
	public static float CENTER_Y = 0;
	public static float CAMERA_DIAGONAL = 0;
	public Camera mCamera = null;

    /** UI Variables */
	public static int BUTTON_STATIC = 0;
	public static int BUTTON_PRESSED = 1;
	public static int BUTTON_DISABLED = 2;
	
    public static SignInButton mSignInButtonMain;
    public static Button mSignOutButtonMain;
    public static Button mSettingsButtonMain;
    public static Button mPlayGameButtonMain;
    public static Button mUpgradesButtonMain;
    public static TextView mLoginTextMain;
	
    /** Google API client object we manage */
    public final static int MAX_SNAPSHOT_RESOLVE_RETRIES = 3;
    public static Snapshot mSnapshot;
    public static byte[] snapshotBytesIn;
    public static boolean snapshotREADflag = false;
    public static final String snapshotName = "gameProgress";
    public static Handler mHandler;
    
	/** splash constants */
	public final float SPLASH_DURATION = 5.0f;
	public final float SPLASH_START_SCALE = 0;
	
	/** Texture variables */
	private BuildableBitmapTextureAtlas menuAtlas;
	private BitmapTextureAtlas loadingAtlas;
	private BitmapTextureAtlas splashAtlas;
	private BitmapTextureAtlas gameBackgroundAtlas;
	private BitmapTextureAtlas gameAtlas;
	
		public ITextureRegion splashRegion;
		public ITextureRegion loadingRegion;
		
		public ITextureRegion menuBackgroundRegion;
		public ITiledTextureRegion playButtonRegion;
		public ITiledTextureRegion upgradesButtonRegion;
		public ITiledTextureRegion storeButtonRegion;
		public ITiledTextureRegion leaderboardButtonRegion;
		public ITiledTextureRegion achievementsButtonRegion;
		public ITiledTextureRegion settingsButtonRegion;
		public ITiledTextureRegion soundButtonRegion;
		
		public ITextureRegion gameBackgroundRegion;
		public ITextureRegion shipRegion;
		public ITextureRegion bulletRegion;
		public ITextureRegion targetPentagonRegion;
		public ITextureRegion targetTriangleRegion;

		
	public Font font;



	public synchronized void loadSplashResources(Engine pEngine, Context pContext){
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/splash/");
		this.splashAtlas = new BitmapTextureAtlas(pEngine.getTextureManager(), 2048, 2048, TextureOptions.BILINEAR);
		this.splashRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.splashAtlas, pContext, "MWGlogo.jpg", 0, 0); 
		this.splashAtlas.load();
	}
	public void loadMenuResources()
	{
		loadMenuGraphics();
		loadMenuAudio();
		loadMenuFonts();
	}
	
	public void loadGameResources()
	{
		loadGameGraphics();
		loadGameFonts();
		loadGameAudio();
	}
	private void loadMenuGraphics()
	{		
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
        menuAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 2048, 2048, TextureOptions.BILINEAR);
        menuBackgroundRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuAtlas, activity, "NV.jpg");
        playButtonRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(menuAtlas, activity, "playButton.jpg", 1, 2);
        upgradesButtonRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(menuAtlas, activity, "upgradesButton.jpg", 1, 2);
        storeButtonRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(menuAtlas, activity, "storeButton.jpg", 1, 2);
        leaderboardButtonRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(menuAtlas, activity, "soundButton.jpg", 1, 2);
        achievementsButtonRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(menuAtlas, activity, "settingsButton.jpg", 1, 2);
        settingsButtonRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(menuAtlas, activity, "settingsButton.jpg", 1, 2);
        soundButtonRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(menuAtlas, activity, "soundButton.jpg", 1, 2);
        
        //play_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "play.png");
        //options_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "options.png");
       
    	try 
    	{
			menuAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			menuAtlas.load();
		} 
    	catch (final TextureAtlasBuilderException e)
    	{
			Debug.e(e);
		}		
	}
	
	private void loadMenuAudio(){		
	}
	
	private void loadMenuFonts(){
		
		FontFactory.setAssetBasePath("font/");
		final ITexture mainFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		font = FontFactory.createStrokeFromAsset(activity.getFontManager(), mainFontTexture, activity.getAssets(), "Roboto-Regular.ttf", 50, true, Color.WHITE_ARGB_PACKED_INT, 2, Color.BLACK_ARGB_PACKED_INT);
	}
	private void loadGameGraphics()
	{
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
		this.gameBackgroundAtlas = new BitmapTextureAtlas(engine.getTextureManager(), 2048, 2048, TextureOptions.BILINEAR);
		this.gameBackgroundRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.gameBackgroundAtlas, activity, "NVsplash.jpg", 0, 0); 
		this.gameBackgroundAtlas.load();
		
		this.gameAtlas = new BitmapTextureAtlas(engine.getTextureManager(), 128, 128, TextureOptions.BILINEAR);
		this.shipRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.gameAtlas, activity, "SHIP.png", 0, 0); 
		this.bulletRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.gameAtlas, activity, "LAZERBULLET.png", 64, 64);
		this.targetPentagonRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.gameAtlas, activity, "pentagon.png", 64, 0); 
		this.targetTriangleRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.gameAtlas, activity, "triangle.png", 0, 64); 
		this.gameAtlas.load();
		Log.d("STATUS","RES LOADED");
		/*
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
        menuTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
        menu_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "menu_background.png");
        play_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "play.png");
        options_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "options.png");
       
    	try 
    	{
			this.menuTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.menuTextureAtlas.load();
		} 
    	catch (final TextureAtlasBuilderException e)
    	{
			Debug.e(e);
		}
		*/
	}
	
	private void loadGameAudio(){		
	}
	
	private void loadGameFonts(){
		/*
		FontFactory.setAssetBasePath("font/");
		final ITexture mainFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

		font = FontFactory.createStrokeFromAsset(activity.getFontManager(), mainFontTexture, activity.getAssets(), "font.ttf", 50, true, Color.WHITE, 2, Color.BLACK);
		font.load();
		*/
	}
	
	public static void updateGameState(){
		gameState =   		Integer.toString(playerRank)
					+ "-" + Integer.toString(hardUnlocked)
					+ "-" + Integer.toString(insaneUnlocked)
					+ "-" + Integer.toString(experiencePoints)
					+ "-" + Integer.toString(currency)
					+ "-" + Integer.toString(shipLevel)
					+ "-" + Integer.toString(shieldLevel)
					+ "-" + Integer.toString(turretLevel)
					+ "-" + Integer.toString(ultimate1Level)
					+ "-" + Integer.toString(ultimate2Level)
					+ "-" + Integer.toString(ultimate3Level)
					+ "-" + Integer.toString(bulletPower)
					+ "-" + Integer.toString(bulletSpeed)
					+ "-" + Integer.toString(bulletAccuracy)
					+ "-" + Integer.toString(bulletType)
					+ "-" + Integer.toString(shieldBooster)
					+ "-" + Integer.toString(doubleDamageBooster)
					+ "-" + Integer.toString(timeBooster)
					;
	}
	
	public static void parseGameState(String gameState){
		String[] tokens =  gameState.split("-");
		for(int c = 0 ; c < tokens.length; c++){
			Log.d("PARSER","token[" + c + "]: " + tokens[c]);	
		}
		playerRank          	= Integer.parseInt(tokens[0]);
		hardUnlocked			= Integer.parseInt(tokens[1]);
		insaneUnlocked			= Integer.parseInt(tokens[2]);
		experiencePoints  	 	= Integer.parseInt(tokens[3]);
		currency				= Integer.parseInt(tokens[4]);
		shipLevel				= Integer.parseInt(tokens[5]);
		shieldLevel				= Integer.parseInt(tokens[6]);
		turretLevel				= Integer.parseInt(tokens[7]);
		ultimate1Level			= Integer.parseInt(tokens[8]);
		ultimate2Level			= Integer.parseInt(tokens[9]);
		ultimate3Level			= Integer.parseInt(tokens[10]);
		bulletPower				= Integer.parseInt(tokens[11]);
		bulletSpeed				= Integer.parseInt(tokens[12]);
		bulletAccuracy			= Integer.parseInt(tokens[13]);
		bulletType				= Integer.parseInt(tokens[14]);
		shieldBooster			= Integer.parseInt(tokens[15]);
		doubleDamageBooster		= Integer.parseInt(tokens[16]);
		timeBooster				= Integer.parseInt(tokens[17]);
	}

	public static void prepareManager(Engine engine, 
									  StartGameActivity activity, 
									  Camera camera, 
									  VertexBufferObjectManager vbom,
									  GGameHelper helper) {

		getInstance().engine = engine;
		getInstance().activity = activity;
		getInstance().camera = camera;
		getInstance().vbom = vbom;	
		getInstance().mHelper = helper;
	}	
	public static ResourceManager getInstance(){
		return INSTANCE;
	}
	
	public void loadLoadingScreen() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		loadingAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 2048, 2048, TextureOptions.BILINEAR);
		loadingRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(loadingAtlas, activity, "NVsplash.png", 0, 0); 
		loadingAtlas.load();	
	}
	public void unloadLoadingScreen() {
		loadingAtlas.unload();
		loadingRegion = null;		
	}
	public void unloadSplashScreen()
	{
		splashAtlas.unload();
		splashRegion = null;
	}
	public void unloadMenuTextures() {
		menuAtlas.unload();
	}
	public void loadMenuTextures() {
		menuAtlas.load();
	}
	
}
