/*
 *     Copyright (C) 2014 Russell Wolf, All Rights Reserved
 *     
 *     Based on code by Pieter Spronck
 * 
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 *     You can contact the author at spacetrader@brucelet.com
 */
//package com.brucelet.spacetrader;
//
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.OutputStreamWriter;
//import java.util.LinkedList;
//import java.util.Map;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.pm.PackageManager;
//import android.content.res.TypedArray;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.Handler;
//import android.support.v4.app.FragmentManager;
//import android.support.v7.app.ActionBar;
//import android.support.v7.app.ActionBarActivity;
//import android.util.TypedValue;
//import android.view.KeyEvent;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.inputmethod.InputMethodManager;
//
//import com.brucelet.spacetrader.datatypes.GameState;
//import com.brucelet.spacetrader.enumtypes.EndStatus;
//import com.brucelet.spacetrader.enumtypes.ThemeType;
//
//public abstract class BaseActivity extends ActionBarActivity {
//	static { android.util.Log.w("SpaceTrader", "Space Trader for Android"); }
//	
//	// Proguard run flag. As written, didntRunProguard will always be true. However proguard settings assume no side
//	// effects from doesntRunInProguard() and so strip the function call out so that didntRunProguard is left as false.
//	// Then DEVELOPER_MODE is set to be true if proguard doesn't run so that it doesn't need to be manually toggled.
//	private static boolean didntRunProguard = false;
//	private static void doesntRunInProguard() {	didntRunProguard = true; }
//	static { doesntRunInProguard(); }
//	public static final boolean DEVELOPER_MODE = didntRunProguard;
//	static { if (DEVELOPER_MODE) android.util.Log.w("SpaceTrader", "Developer Mode is active"); }
//	
//	private GameState mGameState;
//
//	private String mCurrentGame;
//
//	static final String GAME_1 = "Game 1";
//	static final String GAME_2 = "Game 2";
//	static final String SAVEFILE = "SpaceTraderSave";
//
//	// TODO do these need to be volatile?
//	private volatile boolean mClicking;
//	private volatile boolean mShowingDialog;
//	
//	private boolean mWelcomeShown = false;
//	
//	// NB using LinkedList as a Deque, but Deque interface doesn't exist on android until API 9 so we must specify type as LinkedList for addFirst() and removeFirst() methods. Also cannot use other Deque implementations such as ArrayDeque.
//	private final LinkedList<BaseDialog> mDialogQueue = new LinkedList<BaseDialog>();
//
//	public BaseActivity() {
//		super();
//	}
//	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		
//		mCurrentGame = getPreferences(MODE_PRIVATE).getString("currentGame", GAME_1);
//		android.util.Log.d("onCreate()", "Current game is "+mCurrentGame);
//				
//		ThemeType theme;
//		if (getIntent().hasExtra("theme")) {
//			android.util.Log.d("onCreate()", "Setting theme from intent extra");
//			int themeIndex = getIntent().getIntExtra("theme", 0);
//			if (themeIndex < 0 || themeIndex >= ThemeType.values().length) themeIndex = 0;
//			theme = ThemeType.values()[themeIndex];
//			mWelcomeShown = true;
//		} else {
//			android.util.Log.d("onCreate()", "Setting theme from saved preferences");
//			theme = getThemeType();
//		}
//		setTheme(theme.resId);
//		android.util.Log.d("onCreate()", "Setting theme "+getResources().getResourceName(theme.resId));
//		getSharedPreferences(mCurrentGame, MODE_PRIVATE).edit().putInt("theme", theme.ordinal()).commit();
//		
//	}
//	
//	@Override
//	protected void onPause() {
//		saveState(getSharedPreferences(mCurrentGame, MODE_PRIVATE));
//		super.onPause();
//	}
//	
//	@Override
//	protected void onResume() {
//		super.onResume();
//		loadState(getSharedPreferences(mCurrentGame, MODE_PRIVATE));
//		
//		if (mGameState.identifyStartup() && !mWelcomeShown) {
//			// Make sure only one of these ever appears, in case onResume() is called again before the dialog is dismissed.
//			if (getSupportFragmentManager().findFragmentByTag(IdentifyStartupDialog.class.getName()) == null) {
//				showDialogFragment(IdentifyStartupDialog.newInstance());
//			}
//		}
//	}
//	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//
//		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT && !hasWriteExternalPermission()) {
//			menu.removeItem(R.id.menu_savegame);
//		}
//
//		return super.onCreateOptionsMenu(menu);
//		
//	}
//	
//	private boolean hasWriteExternalPermission() {
//		PackageManager pm = getPackageManager();
//		int hasPerm = pm.checkPermission(
//				android.Manifest.permission.WRITE_EXTERNAL_STORAGE, 
//				getPackageName());
//		return hasPerm == PackageManager.PERMISSION_GRANTED;
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//
//		switch (item.getItemId()) {
//		case R.id.menu_keyboard:
//			// XXX for occasional debugging.
//			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//			imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
//
//			return true;
//
//		case R.id.menu_options:
//
//			showDialogFragment(OptionsDialog.newInstance());
//			return true;
//		case R.id.menu_shortcuts:
//
//			showDialogFragment(ShortcutDialog.newInstance());
//
//			return true;
//		case R.id.menu_scores:
//			mGameState.viewHighScores();
//			return true;
//		case R.id.menu_clearscores:
//			showDialogFragment(ConfirmDialog.newInstance(
//					R.string.dialog_clearscores_title, 
//					R.string.dialog_clearscores_message, 
//					R.string.help_cleartable,
//					new OnConfirmListener() {
//						@Override
//						public void onConfirm() {
//							mGameState.initHighScores();
//						}
//					}, null));
//			return true;
//		case R.id.menu_new:
//			showDialogFragment(ConfirmDialog.newInstance(
//					R.string.dialog_newgame_confirm, 
//					R.string.dialog_newgame_confirm_message, 
//					R.string.help_confirmnew,
//					new OnConfirmListener() {
//						@Override
//						public void onConfirm() {
//							launchActivity(TitleActivity.class);
//							((TitleScreen)findScreenById(R.id.screen_title)).onSingleClick(null);
//						}
//					}, null));
//			return true;
//		case R.id.menu_switch:
//			showDialogFragment(ConfirmDialog.newInstance(
//					R.string.dialog_switchgame_title, 
//					R.string.dialog_switchgame_message, 
//					R.string.help_switchgame, 
//					new OnConfirmListener() {
//
//						@Override
//						public void onConfirm() {
//							SharedPreferences currentPrefs = getSharedPreferences(mCurrentGame, MODE_PRIVATE);
//							saveState(currentPrefs);
//
//							String otherGame = mCurrentGame.equals(GAME_1)? GAME_2 : GAME_1;
//							SharedPreferences otherPrefs = getSharedPreferences(otherGame, MODE_PRIVATE);
//
//							if (!otherPrefs.getBoolean("game started", false)) {
//								// start new game
//								launchActivity(TitleActivity.class);
//								showDialogFragment(SimpleDialog.newInstance(
//										R.string.dialog_switchtonew_title, 
//										R.string.dialog_switchtonew_message, 
//										R.string.help_switchtonew,
//										new OnConfirmListener() {
//											@Override
//											public void onConfirm() {
//												mGameState.switchToNew();
//											}
//										}));
//
//							} else {
//								// switch to existing game
//								final ThemeType currentTheme = getThemeType();
//								final ThemeType otherTheme = ThemeType.values()[otherPrefs.getInt("theme", currentTheme.ordinal())];
//
//								if (otherPrefs.getBoolean("sharePreferences", true)) { // TODO change default from true to current value of GameState.sharePreferences
//									// If other game shares preferences, then copy from current game.
//									SharedPreferences.Editor otherEditor = otherPrefs.edit();
//
//									otherEditor.putInt("theme", currentPrefs.getInt("theme", 0));
//									mGameState.copyPrefs(currentPrefs, otherEditor);
//
//									otherEditor.commit();
//								}
//
//								// XXX this looks a bit inelegant to the user
//								if (currentTheme != otherTheme) {
//									setNewTheme(otherTheme);
//								}
//								loadState(otherPrefs);
//
//								showDialogFragment(SimpleDialog.newInstance(
//										R.string.dialog_switched_title, 
//										R.string.dialog_switched_message, 
//										R.string.help_switched,
//										mGameState.nameCommander()));
//							}
//
//							mCurrentGame = otherGame;
//							getPreferences(MODE_PRIVATE).edit().putString("currentGame", mCurrentGame).commit();
//						}
//					}, 
//					null));
//			return true;
//		case R.id.menu_retire:
//			showDialogFragment(ConfirmDialog.newInstance(
//					R.string.dialog_retire_title, 
//					R.string.dialog_retire_message, 
//					R.string.help_retire,
//					new OnConfirmListener() {
//						@Override
//						public void onConfirm() {
//							mGameState.showEndGameScreen(EndStatus.RETIRED);
//						}
//					}, null));
//			return true;
//
//
//		case R.id.menu_savegame:
//			saveSnapshot();
//			return true;
//
//
//		case R.id.menu_help_about:
//			showDialogFragment(AboutDialog.newInstance());
//			return true;
//
//		case R.id.menu_help_acknowledgements:
//			showDialogFragment(HelpDialog.newInstance(R.string.help_acknowledgements));
//			return true;
//
//		case R.id.menu_help_current:
//			showDialogFragment(HelpDialog.newInstance(getHelpTextResId()));
//			return true;
//
//		case R.id.menu_help_firststeps:
//			showDialogFragment(HelpDialog.newInstance(R.string.help_firststeps));
//			return true;
//
//		case R.id.menu_help_howtoplay:
//			showDialogFragment(HelpDialog.newInstance(R.string.help_howtoplay));
//			return true;
//
//		case R.id.menu_help_helponmenu:
//			showDialogFragment(HelpDialog.newInstance(R.string.help_helponmenu));
//			return true;
//
//		case R.id.menu_help_skills:
//			showDialogFragment(HelpDialog.newInstance(R.string.help_skills));
//			return true;
//
//		case R.id.menu_help_shipequipment:
//			showDialogFragment(HelpDialog.newInstance(R.string.help_shipequipment));
//			return true;
//
//		case R.id.menu_help_trading:
//			showDialogFragment(HelpDialog.newInstance(R.string.help_trading));
//			return true;
//
//		case R.id.menu_help_traveling:
//			showDialogFragment(HelpDialog.newInstance(R.string.help_travelling));
//			return true;
//
//		case R.id.menu_help_documentation:
//			// Show the documentation file from the original game, in a webview dialog
//			Intent intent = new Intent(this, DocumentationActivity.class);
//			intent.putExtra("theme", getThemeType().resId);
//			startActivity(intent);
//			return true;
//
//		}
//		return false;
//	}
//	
//	@Override
//	public boolean dispatchKeyEvent(KeyEvent event) {
//		int keyCode = event.getKeyCode();
//		boolean keyDown = event.getMetaState() == 0 && event.getAction() == KeyEvent.ACTION_DOWN;
//		
//		switch (keyCode) {
//		case KeyEvent.KEYCODE_O:
//			
//			if (keyDown) showDialogFragment(OptionsDialog.newInstance());
//			return true;
//			
//		case KeyEvent.KEYCODE_BACK:
//			if (event.isLongPress() && keyDown) {
//				showExitDialog();
//				return true;
//			}
//			// Otherwise, we use default key-handling, and push through to onBackPressed().
//			break;
//		}
//
//		return super.dispatchKeyEvent(event);
//	}
//
//
//	//	@Override
//	public void showDialogFragment(BaseDialog dialog) {
//		//		dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogTheme);
//
//		String tag = dialog.getClass().getName();
//		android.util.Log.d("showDialogFragment()", "Showing fragment "+tag);
//
//
//		if (mShowingDialog) {
//			android.util.Log.d("showDialogFragment()", "Previous dialog display already in progress!");
//			if (!mDialogQueue.contains(dialog)) mDialogQueue.addFirst(dialog);
//			return;
//		}
//
//		mShowingDialog = true;
//
//		FragmentManager fm = getSupportFragmentManager();
////		FragmentTransaction ft = fm.beginTransaction();
////		Fragment prev = fm.findFragmentByTag(tag);
////		if (prev != null) {
////			ft.remove(prev);
////		}
////		ft.addToBackStack(null);
//
//		onShowDialog(dialog);
//
//		// Show the dialog.
//		dialog.show(fm, tag);
//	}
//
//	//	@Override
//	public ThemeType getThemeType() {
//		int themeIndex = getSharedPreferences(mCurrentGame, MODE_PRIVATE).getInt("theme", 0);
//		if (themeIndex < 0 || themeIndex >= ThemeType.values().length) themeIndex = 0;
//		return ThemeType.values()[themeIndex];
//	}
//
//	//	@Override
//	public void setNewTheme(ThemeType theme) {
//
//		android.util.Log.d("setNewTheme()", "Setting new theme "+getResources().getResourceName(theme.resId));
//
//		finish();
//		Intent intent = new Intent(this, getClass());
//		intent.putExtra("theme", theme.ordinal());
//
//		startActivity(intent);
//		//		overridePendingTransition(0, 0);
//	}
//	
//	@Override
//	public void onBackPressed() {
//		if (onBackPressedDoExit()) {
//			showExitDialog();
//		}
//	}
//	
//	private void showExitDialog() {
//		showDialogFragment(ConfirmDialog.newInstance(
//				R.string.dialog_exit_title, 
//				R.string.dialog_exit_message, 
//				R.string.help_exit,
//				new OnConfirmListener() {
//			@Override
//			public void onConfirm() {
//				new Handler().post(new Runnable() {
//					@Override
//					public void run() {
//						BaseActivity.super.onBackPressed();
//					}
//				});
//			}
//		}, null));
//	}
//
////	@Override
//	public GameState getGameState() {
//		return mGameState;
//	}
//	
//	//@Override
//	void startClick() {
//		mClicking = true;
//		//	android.util.Log.v("click","Start click");
//	}
//
//	//@Override
//	void finishClick() {
//		mClicking = false;
//		//	android.util.Log.v("click","Finish click");
//	}
//
//	//@Override
//	boolean isClicking() {
//		return mShowingDialog || mClicking;
//	}
//
//	void reportDialogShown() {
//		mShowingDialog = false;
//	}
//		
//	void showQueuedDialog() {
//		if (mDialogQueue.isEmpty()) return;
//		
//		showDialogFragment(mDialogQueue.removeFirst());
//	}
//	
//	public void launchActivity(Class<?> clazz) {
//		Intent intent = new Intent(getApplicationContext(), clazz);
//		startActivity(intent);
//		finish();
//	}
//	
//	public abstract void onShowDialog(BaseDialog dialog);
//	public abstract boolean onBackPressedDoExit();
//	public abstract int getHelpTextResId();
//	public abstract void onSaveState(SharedPreferences.Editor editor);
//	public abstract void onLoadState(SharedPreferences prefs);
//
//}
