package com.mwgames.geoguard;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.plus.Plus;
import com.mwgames.geoguard.util.ResourceManager;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.EntityModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.modifier.ColorBackgroundModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.ui.activity.BaseSplashActivity;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.IModifier.IModifierListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class SplashActivity extends SimpleBaseGameActivity {

	private Scene mScene;
   
	private static Point windowDimensions = new Point();

    @Override
    public EngineOptions onCreateEngineOptions() {
    	
		//create window dimensions via window manager
		Display display = getWindowManager().getDefaultDisplay();
		display.getSize(windowDimensions);	
		ResourceManager.CAMERA_WIDTH = windowDimensions.x;
		ResourceManager.CAMERA_HEIGHT = windowDimensions.y;
		ResourceManager.CENTER_X = ResourceManager.CAMERA_WIDTH / 2;
		ResourceManager.CENTER_Y = ResourceManager.CAMERA_HEIGHT / 2;
		ResourceManager.CAMERA_DIAGONAL = (float) Math.sqrt(Math.pow(ResourceManager.CAMERA_WIDTH, 2) + Math.pow(ResourceManager.CAMERA_HEIGHT, 2)) / 2;
Log.d("SPLASH","ENGINE SPECS LOADED");

		return new EngineOptions(true,
								 ScreenOrientation.LANDSCAPE_SENSOR, 
								 new RatioResolutionPolicy(ResourceManager.CAMERA_WIDTH, ResourceManager.CAMERA_HEIGHT), 
								 new Camera(0, 0, ResourceManager.CAMERA_WIDTH, ResourceManager.CAMERA_HEIGHT));
		
    }
    
    @Override
    public void onCreateResources() {
    	ResourceManager.getInstance().loadSplashResources(this.getEngine(), getApplicationContext());
    }
    
	@Override
    public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());
    	
		this.mScene = new Scene();
		this.mScene.setBackground(new Background(0.0f, 0.0f, 0.0f));
		//this.mScene.setBackgroundEnabled(false);
		//ResourceManager.CAMERA_WIDTH - mResourceManager.getSplashRegion().getWidth()) / 2
        final Sprite splashImage = new Sprite((ResourceManager.CAMERA_WIDTH - ResourceManager.getInstance().splashRegion.getWidth()) / 2,
        									  (ResourceManager.CAMERA_HEIGHT - ResourceManager.getInstance().splashRegion.getHeight()) / 2, 
        									  ResourceManager.getInstance().splashRegion,
        									  this.getVertexBufferObjectManager()
        									  );   
        splashImage.setAlpha(0f);  

        float splash_dur = ResourceManager.getInstance().SPLASH_DURATION;
        SequenceEntityModifier animation = new SequenceEntityModifier(
    		new DelayModifier(0.5f),
    		new AlphaModifier(3.0f, 0f, 1f),
    		new DelayModifier(2.0f),
    		new AlphaModifier(3.0f, 1f, 0f),
    		new DelayModifier(0.5f)      		
            //new ParallelEntityModifier(
                //new ScaleModifier(SPLASH_DURATION, SPLASH_START_SCALE,1),
                //new AlphaModifier(SPLASH_DURATION, 0,1)
            //)
        );
        animation.addModifierListener(new IModifierListener<IEntity>() {
            @Override
            public void onModifierStarted(final IModifier<IEntity> pModifier, final IEntity pItem) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Loading GameResources", Toast.LENGTH_SHORT).show();
                    	//mResourceManager.loadGameResources(getEngine(), getApplicationContext());
                        	
                    }                    
                });
            }

            @Override
            public void onModifierFinished(final IModifier<IEntity> pEntityModifier, final IEntity pEntity) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    	getEngine().stop();
                    	startActivity(new Intent(SplashActivity.this, StartGameActivity.class));
                    	Toast.makeText(getApplicationContext(), "Advancing to MAIN", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });            
        
        splashImage.registerEntityModifier(animation);  
        mScene.attachChild(splashImage);  
           
        return mScene;
    }
}
