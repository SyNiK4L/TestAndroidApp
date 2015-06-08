package info.coreyjones.witcher3map;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.NotificationCompat.WearableExtender;


public class Witcher3Map extends ActionBarActivity {
    protected boolean fullScreen = false;
    protected final String siteURL = "http://witcher3map.com";
    protected WebView theMapView;
    protected boolean actionBarState = true;

    //Overrides for activity creation and event handlers
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_witcher3_map);
        theMapView = setupWebView(siteURL, R.id.mapView, savedInstanceState);
        sendWearableNoti();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_witcher3_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handling action bar item clicks here.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            initSettingsMenu();
        } else if (id == R.id.action_immersive) {
            toggleHideyBar();
            if (fullScreen) {
                toastMessage(getString(R.string.fullScreenOff));
                showActionBar();
                fullScreen = !fullScreen;
            } else {
                toastMessage(getString(R.string.fullScreenOn));
                hideActionBar();
                fullScreen = !fullScreen;
            }
        } else if (id == R.id.refresh_window) {
            refreshPage(R.id.mapView);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the state of the WebView
        theMapView.saveState(outState);

        /*
        Not used now but if I need to save the state of multiple webviews this is the code. Save the state into a new bundle
        Then use putBundle to save the new bundle into the outState bundle. Then use .getBundle to retrieve them by key.

        http://stackoverflow.com/questions/4172800/android-muliple-webview-save-instance
        http://stackoverflow.com/questions/18479519/how-to-save-restore-webview-state

        Bundle webViewBundle = new Bundle();
        theMapView.saveState(webViewBundle);
        webViewBundle.putBundle("TheMapView1",outState);
        */
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Restore the state of the WebView
        theMapView.restoreState(savedInstanceState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        WebView myWebView = (WebView) findViewById(R.id.mapView);
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
            myWebView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

    //Custom Functions


    protected void sendWearableNoti(){
        int notificationId = 001;
// Build intent for notification content
        Intent viewIntent = new Intent(this, SettingsMenu.class);
        String EXTRA_EVENT_ID = "1";
        String eventId = "1";
        viewIntent.putExtra(EXTRA_EVENT_ID, eventId);
        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(this, 0, viewIntent, 0);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_event_black_12dp)
                        .setContentTitle("This is a test")
                        .setContentText("hiiiiiii")
                        .setContentIntent(viewPendingIntent);

// Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);

// Build the notification and issues it with notification manager.
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    //WebView Creator
    protected WebView setupWebView(String theURL, int TheViewID, Bundle savedInstanceState) {
        WebView theWebView = (WebView) findViewById(TheViewID);
        theWebView.setWebViewClient(new WebViewClient());

        WebSettings webSettings = theWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setSupportZoom(true);
        if (savedInstanceState == null) {
            // Load a page
            theWebView.loadUrl(theURL);
        }

        return theWebView;
    }

    //Hide Action Bar
    public void toggleActionBar(View view) {
        if (getSupportActionBar() != null && actionBarState) {
            hideActionBar();
        } else if (getSupportActionBar() != null && actionBarState == false) {
            showActionBar();
        }

    }

    public void hideActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
            actionBarState = !actionBarState;
            Button hideButton = (Button) findViewById(R.id.hideBar);
            hideButton.setText(getString(R.string.showBar));
        }
    }

    public void showActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().show();
            actionBarState = !actionBarState;
            Button hideButton = (Button) findViewById(R.id.hideBar);
            hideButton.setText(getString(R.string.hideBar));
        }
    }

    //Refresh WebView - Pass in R.id.controlID
    protected void refreshPage(int TheID) {
        WebView theWebView = (WebView) findViewById(TheID);
        String currentURL = theWebView.getUrl();
        theWebView.loadUrl(currentURL);
        toastMessage(getString(R.string.refreshString));
    }

    //Settings Menu Initialization
    protected void initSettingsMenu() {
        Intent intent = new Intent(this, SettingsMenu.class);
        startActivity(intent);
    }

    //Toast Message Wrapper for easier use.
    protected void toastMessage(CharSequence text) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    //Immersion Mode Function from Google
    protected void toggleHideyBar() {

        int uiOptions = this.getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;

        boolean isImmersiveModeEnabled =
                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);

        // Navigation bar hiding:  Backwards compatible to ICS.
        if (Build.VERSION.SDK_INT >= 14) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }
        // Status bar hiding: Backwards compatible to Jellybean
        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }
        // Immersive mode: Backward compatible to KitKat.
        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        this.getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
        //END_INCLUDE (set_ui_flags)
    }
}