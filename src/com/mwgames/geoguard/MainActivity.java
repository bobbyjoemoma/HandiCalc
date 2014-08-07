package com.mwgames.geoguard;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
import com.google.android.gms.games.snapshot.Snapshots;
import com.google.android.gms.plus.Plus;
import com.mwgames.geoguard.R.string;
import com.mwgames.geoguard.google.service.GBaseGameActivity;
import com.mwgames.geoguard.google.service.GGameHelper;
import com.mwgames.geoguard.util.ResourceManager;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements GGameHelper.GameHelperListener{

	// The game helper object. This class is mainly a wrapper around this object.
    public static GGameHelper mHelper;

    public static final int CLIENT_GAMES = GGameHelper.CLIENT_GAMES;
    public static final int CLIENT_APPSTATE = GGameHelper.CLIENT_APPSTATE;
    public static final int CLIENT_PLUS = GGameHelper.CLIENT_PLUS;
    public static final int CLIENT_SNAPSHOT = GGameHelper.CLIENT_SNAPSHOT;
    public static final int CLIENT_ALL = GGameHelper.CLIENT_ALL;
    
    // Requested clients for Google API
    protected int mRequestedClients = CLIENT_GAMES | CLIENT_PLUS | CLIENT_SNAPSHOT;;

    private final static String TAG = "MAIN";
    protected boolean mDebugLog = true;
    ResourceManager mResourceManager;
    
    public MainActivity() {
        super();
    }

    public MainActivity(int requestedClients) {
        super();
        setRequestedClients(requestedClients);
    }
    
    protected void setRequestedClients(int requestedClients) {
        mRequestedClients = requestedClients;
    }   
    public GGameHelper getGameHelper() {
        if (mHelper == null) {
            mHelper = new GGameHelper(this, mRequestedClients);
            mHelper.enableDebugLog(mDebugLog);
        }
        return mHelper;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	mResourceManager = new ResourceManager();
    	setContentView(R.layout.dialog_loading);
    	
        if (mHelper == null) {
            getGameHelper();
        }
        mHelper.setup(this);
    }
    @Override
    protected void onStart() {
        super.onStart();
        mHelper.onStart(this);
    }
    @Override
    protected void onStop() {
        super.onStop();
        mHelper.onStop();
    }
    @Override
    protected void onActivityResult(int request, int response, Intent data) {
        super.onActivityResult(request, response, data);
        mHelper.onActivityResult(request, response, data);
    }

    protected GoogleApiClient getApiClient() {
        return mHelper.getApiClient();
    }
    protected boolean isSignedIn() {
        return mHelper.isSignedIn();
    }
    protected void beginUserInitiatedSignIn() {
        mHelper.beginUserInitiatedSignIn();
    }
    protected void signOut() {
        mHelper.signOut();
    }
    protected void showAlert(String message) {
        mHelper.makeSimpleDialog(message).show();
    }
    protected void showAlert(String title, String message) {
        mHelper.makeSimpleDialog(title, message).show();
    }
    protected void enableDebugLog(boolean enabled) {
        mDebugLog = true;
        if (mHelper != null) {
            mHelper.enableDebugLog(enabled);
        }
    }
    protected String getInvitationId() {
        return mHelper.getInvitationId();
    }
    protected void reconnectClient() {
        mHelper.reconnectClient();
    }
    protected boolean hasSignInError() {
        return mHelper.hasSignInError();
    }
    protected GGameHelper.SignInFailureReason getSignInError() {
        return mHelper.getSignInError();
    }
    
    public void googleSignInButton(View view){
    	/*
    	try 
    	{
    		//runs on UI thread
    		runOnUiThread(new Runnable() 
    		{
    			@Override
    			public void run() 
    			{
    	            //checks if user is signed in
    	            //you must call an instance of your game activity here to get
    	            //to Game Helper
    				if(isSignedIn())
    				{
    					//signOut();
    				}
    				else
    				{
    					//SplashActivity.beginUserInitiatedSignIn();
    				}
    			}
    		});
    	} 
    	catch (Exception e) 
    	{
    		e.printStackTrace();
    	}
    	*/
    }

    /** Overriden GoogleAPIClient Helper methods */
	@Override
	public void onSignInFailed() {
		Log.d("APIclient","CONNECTION FAILED");
		Toast.makeText(getApplicationContext(), "Connection failed", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onSignInSucceeded() {
		Log.d("APIclient","CONNECTED");
		StartGameActivity.mPlayer = Games.Players.getCurrentPlayer(mHelper.getApiClient());
		final String displayText = "Welcome to GeoGuard " + StartGameActivity.mPlayer.getDisplayName();
	    Toast.makeText(getApplicationContext(), displayText, Toast.LENGTH_SHORT).show();
	    
		setContentView(R.layout.activity_main);
    	updateMainResourceId();
    	updateUIsignIn(mHelper.isSignedIn());
    	
    	loadFromSnapshot(ResourceManager.snapshotName);
    	saveSnapshot(ResourceManager.snapshotName);
    	/*
        if(!ResourceManager.newPlayer){
        	//(GoogleApiClient apiClient, String fileName, boolean createIfNotFound)
        }
        else{
        	 AsyncTask<Void, Void, Integer> task = new AsyncTask<Void, Void, Integer>() {

     			@Override
     	        protected Integer doInBackground(Void... params) {
     				Snapshots.OpenSnapshotResult result = Games.Snapshots.open(getApiClient(),ResourceManager.snapshotName, true).await();
     				
                	return 1;
     	        }

     	        @Override
     	        protected void onPostExecute(Integer status){
     	        }
     	    };
     	    
        }*/
	}	
	
	void loadFromSnapshot(final String name) {
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.dialog_loading);
		dialog.show();

	    AsyncTask<Void, Void, Integer> task = new AsyncTask<Void, Void, Integer>() {

			@Override
	        protected Integer doInBackground(Void... params) {
	            // Open the saved game using its name.
	            Snapshots.OpenSnapshotResult result = Games.Snapshots.open(getApiClient(),name, true).await();
	            int status = result.getStatus().getStatusCode();
	            ResourceManager.mSnapshot = processSnapshotOpenResult(result,0);
	            if (ResourceManager.mSnapshot != null){
	                ResourceManager.snapshotBytesIn = ResourceManager.mSnapshot.readFully();
	                ResourceManager.snapshotREADflag = true;
	                updateUIloaded(true);
	                Log.e(TAG, "Error while loading: " + status);
	            }else{
	                Log.e(TAG, "Error while loading: " + status);
	                updateUIloaded(false);
	                ResourceManager.snapshotREADflag = true;
	            }

	            return status;
	        }

	        @Override
	        protected void onPostExecute(Integer status){
	            dialog.dismiss();
	        }

			
	    };

	    task.execute();
	}
	Snapshot processSnapshotOpenResult(Snapshots.OpenSnapshotResult result,
	        int retryCount){
	    Snapshot mResolvedSnapshot = null;
	    retryCount++;
	    int status = result.getStatus().getStatusCode();

	    Log.i(TAG, "Save Result status: " + status);

	    if (status == GamesStatusCodes.STATUS_OK) {
	        return result.getSnapshot();
	    } else if (status == GamesStatusCodes.STATUS_SNAPSHOT_CONTENTS_UNAVAILABLE) {
	        return result.getSnapshot();
	    } else if (status == GamesStatusCodes.STATUS_SNAPSHOT_CONFLICT){
	        Snapshot snapshot = result.getSnapshot();
	        Snapshot conflictSnapshot = result.getConflictingSnapshot();

	        // Resolve between conflicts by selecting the newest of the conflicting snapshots.
	        mResolvedSnapshot = snapshot;

	        if (snapshot.getMetadata().getLastModifiedTimestamp() <
	                conflictSnapshot.getMetadata().getLastModifiedTimestamp()){
	            mResolvedSnapshot = conflictSnapshot;
	        }

	        Snapshots.OpenSnapshotResult resolveResult = Games.Snapshots.resolveConflict(
	                getApiClient(), result.getConflictId(), mResolvedSnapshot)
	                .await();

	        if (retryCount < ResourceManager.MAX_SNAPSHOT_RESOLVE_RETRIES){
	            return processSnapshotOpenResult(resolveResult, retryCount);
	        }else{
	            String message = "Could not resolve snapshot conflicts";
	            Log.e(TAG, message);
	            Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
	        }

	    }
	    // Fail, return null.
	    return null;
	}
	void saveSnapshot(final String name) {
	    AsyncTask<Void, Void, Snapshots.OpenSnapshotResult> task =
	            new AsyncTask<Void, Void, Snapshots.OpenSnapshotResult>() {
	                @Override
	                protected Snapshots.OpenSnapshotResult doInBackground(Void... params) {
	                    Snapshots.OpenSnapshotResult result = Games.Snapshots.open(getApiClient(), name, true).await();
	                    return result;
	                }

	                @Override
	                protected void onPostExecute(Snapshots.OpenSnapshotResult result) {
	                    Snapshot toWrite = processSnapshotOpenResult(result, 0);

	                    Log.i(TAG, writeSnapshot(toWrite));
	                }
	            };

	    task.execute();
	}
	public static String writeSnapshot(Snapshot snapshot){
    	snapshot.writeBytes(ResourceManager.gameState.getBytes());
    	String description = "Player Rank: " + ResourceManager.playerRank + " Coins: " + ResourceManager.currency;
	    // Save the snapshot.
	    SnapshotMetadataChange metadataChange
	            = new SnapshotMetadataChange.Builder()
	            .setDescription(description)
	            .build();
	    Games.Snapshots.commitAndClose(mHelper.getApiClient(), snapshot, metadataChange);
	    return snapshot.toString();
	}
	public void updateMainResourceId() {
		ResourceManager.mSignInButtonMain = (SignInButton) findViewById(R.id.signInButton_main);
		ResourceManager.mSignOutButtonMain = (Button) findViewById(R.id.signOutButton_main);
		ResourceManager.mSettingsButtonMain = (Button) findViewById(R.id.settingsButton_main);
		ResourceManager.mPlayGameButtonMain = (Button) findViewById(R.id.playGameButton_main);
	    //mResourceManager.mUpgradesButtonMain = (Button) findViewById(R.id.upgradesButton_main);
		ResourceManager.mLoginTextMain = (TextView) findViewById(R.id.loginText_main);
		
	}
	public void updateUIsignIn(boolean signedIn) {
		if(signedIn){
			ResourceManager.mSignInButtonMain.setVisibility(0);
			ResourceManager.mSignOutButtonMain.setVisibility(1);
			ResourceManager.mLoginTextMain.setText(R.string.signedInText);
		} else {
			ResourceManager.mSignInButtonMain.setVisibility(1);
			ResourceManager.mSignOutButtonMain.setVisibility(0);
			ResourceManager.mLoginTextMain.setText(R.string.signedOutText);			
		}
		
	}
	public void updateUIloaded(boolean loaded) {
		/*
		if(loaded){
			ResourceManager.mSignInButtonMain.setVisibility(0);
			ResourceManager.mSignOutButtonMain.setVisibility(1);
			ResourceManager.mLoginTextMain.setText(R.string.signedInText);
		} else {
			ResourceManager.mSignInButtonMain.setVisibility(1);
			ResourceManager.mSignOutButtonMain.setVisibility(0);
			ResourceManager.mLoginTextMain.setText(R.string.signedOutText);			
		}
		*/
	}
}
