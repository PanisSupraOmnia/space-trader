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
package com.brucelet.spacetrader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.Map;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.PopupMenuCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.brucelet.spacetrader.datatypes.GameState;
import com.brucelet.spacetrader.enumtypes.EndStatus;
import com.brucelet.spacetrader.enumtypes.ScreenType;
import com.brucelet.spacetrader.enumtypes.ThemeType;

/*
 * Some random notes:
 * 
 * List of modifications made to original code/behavior (All references to the original palm version refer to the final 1.2.2 release):
 *  - Quest systems can now be optionally randomized.
 *  - When selling cargo in orbit, the other trader will no longer buy more than he has room for.
 *  - During encounters, the opponent ship is now rotated to face the player.
 *  - Encounters can optionally display shield/hull percentages (as in original textual encounters mode) in addition to images.
 *  - Added a checkbox for the upgraded hull to the Cheat dialog.
 *  - Cheat codes now disable scoring as with loading a snapshot in the original. A confirmation window explaining this appears the first time a cheat is used.
 *  - Added a check in the New Commander screen to prevent the player from leaving the name blank.
 *  - It's now possible to restart the disease quest if your ship has been destroyed and the antidote lost. This makes the gameplay behavior consistent with in-game text which tells you to go get more.
 *  - Tribble positions are fully randomized over the entire screen. Clicking on one now causes it to jump to a new position so that buttons and text don't get blocked.
 *  - The customs police encounter is disabled if you are arrested or your ship is destroyed, so the player doesn't get in trouble on the next flight long after the cargo was lost.
 *  - Light, Dark, and Classic Palm OS themes.
 *  - [DISABLED] The Customs Police will no longer penalize you if you've discarded or lost the contraband they're looking for, as long as your record is clean.
 *  - Android button behavior:
 *    - Back button optionally returns to previous screen while docked.
 *    - Volume keys optionally scroll through systems on the galactic chart, target system, and average prices screens, behaving like the palm scroll buttons.
 *  - Some bugfixes, including:
 *    - Fixed issue where moon and tribble buyer news stories would only appear if the system had a status other than Uneventful.
 *    - Fixed issue where Bank screen would only print 'maximum' next to noclaim value on the first day it hits 90%.
 *    - Fix so that newspaper displays headlines about player with police record above hero score and not just equal to it. (This was listed in changelog for v1.2.2 of the original but not actually fixed)
 *    - The option to continuously attack fleeing ships which had no effect in the palm version now behaves as apparently intended by interrupting a continuous attack if the opponent starts fleeing and the option is unchecked.
 *    - Fixed a subtle bug where the wrong system's size was used to determine the amount of trade goods available.
 *    - The easter bunny message no longer displays unless the trade is successful.
 *    - Zeethibal now correctly has no pay.
 *    - The personnel roster now displays correctly if both Jarek and Wild are on board at the same time.
 *    - The Buy Ship screen now disallows buying a ship with only two crew quarters if both Jarek and Wild are aboard.
 *    - Fixed bug that could cause player to get negative money when transferring unique equipment to a new ship.
 *    - Fixed a bug where the wrong system index was used to seed the newspaper, so that changing warpsystem could cause the newspaper masthead to change.
 *    - Added a check so that criminal players are not paid bounties. In game text suggested this already was happening but the bounties were still paid.
 *    - Fixed a bug where stats which were greater than 10 could never see a decrease.
 *    - Fixed a minor bug in computing the value of a ship when the fuel compactor gadget is present.
 *    - News stories no longer display for Dragonfly or Scarab quests if the player doesn't meet the conditions for receiving those quests.
 *  
 * TODO list:
 *  - Bug: Overflowing text on small devices.
 *  - Clean up laggy features:
 *    - Warp subscreen viewpagers
 *  - Write: Tweak help texts for android
 *  - Code: Make quest updates optional? (add another checkbox to NewGameDialog)
 *    - Disease updates
 *    - Customs Police Fix
 *    - Trade in orbit capacity
 *  - Code: Instance field visibility in datatypes package.
 *  - Code: Make sure that save/load functions correctly handle all fields.
 *    - Also consider transitioning to use of saveInstanceState Bundles to cut down lag (if it looks like it will help) 
 *  - Bug: There are instances where dialogs become blank when leaving and returning to app after long time. Look into fragment lifecycle to handle saving state better.
 *    - Use arguments in place of all instance fields? But how to handle listeners and arbitrarily-typed args? This may require a large change to remove anonymous inner class listeners
 *    - This looks ugly but is usually just a minor problem because user can close dialog and repeat whatever action originally spawned it.
 *  - Xml/Code: Layout updates to make app organization more modernized?
 *    - Do something with bottom half of screen?
 *    - ViewPager for moving between docked screens
 *    - Separate activities for Title, End, Encounter, Docked
 * 
 * 
 * 
 */
public class MainActivity extends ActionBarActivity implements /*OnNavigationListener,*/ OnMenuItemClickListener//, OnPageChangeListener //, GameManager 
{
	static { android.util.Log.w("SpaceTrader", "Space Trader for Android"); }
	
	// Proguard run flag. As written, didntRunProguard will always be true. However proguard settings assume no side
	// effects from doesntRunInProguard() and so strip the function call out so that didntRunProguard is left as false.
	// Then DEVELOPER_MODE is set to be true if proguard doesn't run so that it doesn't need to be manually toggled.
	private static boolean didntRunProguard = false;
	private static void doesntRunInProguard() {	didntRunProguard = true; }
	static { doesntRunInProguard(); }
	public static final boolean DEVELOPER_MODE = didntRunProguard;
	static { if (DEVELOPER_MODE) android.util.Log.w("SpaceTrader", "Developer Mode is active"); }
		
	private static final int[] SHORTCUT_IDS = {
		R.id.menu_shortcut1,
		R.id.menu_shortcut2,
		R.id.menu_shortcut3,
		R.id.menu_shortcut4,
	};
	private char[] mShortcutKeys;
//	private final int[] mMenuShortcuts = {
//			0,				// default shortcut 1 = Buy Cargo
//			1,				// default shortcut 2 = Sell Cargo
//			2,				// default shortcut 3 = Shipyard
//			10,				// default shortcut 4 = Short Range Warp
//			};
	
	
//	// NB this must stay in sync with the string array resources @array/screen_list and @array/shortcut_list
//	private final Class<?>[] mScreenList = {
//			BuyScreen.class,
//			SellScreen.class,
//			YardScreen.class,
//			BuyEqScreen.class,
//			SellEqScreen.class,
//			PersonnelScreen.class,
//			BankScreen.class,
//			InfoScreen.class,
//			StatusScreen.class,
//			ChartScreen.class,
//			WarpScreen.class,
//	};
	
	// NB using LinkedList as a Deque, but Deque interface doesn't exist on android until API 9 so we must specify type as LinkedList for addFirst() and removeFirst() methods.
//	private final LinkedList<Integer> mBackStack = new LinkedList<Integer>();
	private final LinkedList<ScreenType> mBackStack = new LinkedList<>();
	private final LinkedList<BaseDialog> mDialogQueue = new LinkedList<>();
	
//	private TextView mActionBarSpinner;
	private View mTitleView;
	private TextView mTitleText;
	private ImageView mTitleIcon;
	
//	private ScreenPagerAdapter mScreenPagerAdapter;
//	private ViewPager mViewPager;
	
	private final GameState mGameState = new GameState(this);
	
	private volatile boolean mClicking;
	private volatile boolean mShowingDialog;

	public static final String GAME_1 = "Game 1";
	public static final String GAME_2 = "Game 2";
	static final String SAVEFILE = "SpaceTraderSave";
	
	private String mCurrentGame;
	
	private boolean mWelcomeShown = false;
	
	private ActionMode mActionMode;
	
//	private ViewFlipper mViewFlipper;
//	private final SparseArray<BaseScreen> mScreenCache = new SparseArray<>();
	
//	private FrameLayout mFragmentContainer;
	private ScreenType mCurrentScreen;
	
	
//	// TODO think about whether something like this might speed up some view generation
//	private static final SparseArray<View> mViewCache = new SparseArray<View>();
//	
//	public View findCachedViewById(int id) {
//		return findCachedViewById(id, null);
//	}
//	public View findCachedViewById(int id, View root) {
//		View view = mViewCache.get(id);
//		
//		if (view == null || !view.isShown()) {
//			view = (root==null? findViewById(id) : root.findViewById(id));
//			mViewCache.put(id, view);
//			android.util.Log.d("ViewCache", "Adding view "+getResources().getResourceEntryName(id)+" to cache");
//		}
//		return view;
//	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mCurrentGame = getPreferences(MODE_PRIVATE).getString("currentGame", GAME_1);
		android.util.Log.d("onCreate()", "Current game is "+mCurrentGame);
				
		ThemeType theme;
		if (getIntent().hasExtra("theme")) {
			android.util.Log.d("onCreate()", "Setting theme from intent extra");
			int themeIndex = getIntent().getIntExtra("theme", 0);
			if (themeIndex < 0 || themeIndex >= ThemeType.values().length) themeIndex = 0;
			theme = ThemeType.values()[themeIndex];
			mWelcomeShown = true;
		} else {
			android.util.Log.d("onCreate()", "Setting theme from saved preferences");
			theme = getThemeType();
		}
		setTheme(theme.resId);
		android.util.Log.d("onCreate()", "Setting theme "+getResources().getResourceName(theme.resId));
		getSharedPreferences(mCurrentGame, MODE_PRIVATE).edit().putInt("theme", theme.ordinal()).commit();

		android.util.Log.d("onCreate()", "setContentView() called");
//		setContentView(R.layout.activity_main);
		setContentView(R.layout.activity_main2);
		
		final ScreenType[] screens = ScreenType.dropdownValues();
		mShortcutKeys = new char[screens.length];
		for (int i = 0; i < mShortcutKeys.length; i++) {
			mShortcutKeys[i] = Character.toLowerCase(getResources().getString(screens[i].shortcutId).charAt(0));
		}

//		SpinnerAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, new String[screens.length]) {
//			
//			@Override
//			public View getDropDownView(int position, View row, ViewGroup parent)
//			{   
//				if(row == null)
//				{
//					//inflate your customlayout for the textview
//					LayoutInflater inflater = getLayoutInflater();
//					row = inflater.inflate(R.layout.spinner_dropdown_item_actionbar, parent, false);
//				}
//
//				//put the data in it
//				TextView text1 = (TextView) row.findViewById(R.id.spinner_shortcut);
//				text1.setText(screens[position].shortcutId);
//				TextView text2 = (TextView) row.findViewById(R.id.spinner_text);
//				text2.setText(screens[position].titleId);
//
//				return row;
//			}
//
//			@Override
//			public View getView(int position, View row, ViewGroup parent)
//			{   
//				if (mActionBarSpinner == null) {
//					mActionBarSpinner = (TextView) getLayoutInflater().inflate(R.layout.spinner_dropdown_title_actionbar, parent, false);
//					
//					TypedValue tv = new TypedValue();
//					getTheme().resolveAttribute(R.attr.actionBarSpinnerTextStyle, tv, true);
//					mActionBarSpinner.setTextAppearance(MainActivity.this, tv.data);
//					
//				}
//				return mActionBarSpinner;
//			}
//		};
//		getSupportActionBar().setListNavigationCallbacks(adapter, this);
		
		ActionBar ab = getSupportActionBar();
		mTitleView = LayoutInflater.from(getSupportActionBar().getThemedContext()).inflate(R.layout.ab_title, null);
		mTitleText = (TextView) mTitleView.findViewById(R.id.title);
		mTitleIcon = (ImageView) mTitleView.findViewById(R.id.icon);
		TypedValue tv = new TypedValue();
		getTheme().resolveAttribute(R.attr.actionBarSpinnerTextStyle, tv, true);
		mTitleText.setTextAppearance(MainActivity.this, tv.data);	// TODO get this working in xml instead of here
		mTitleView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onOptionsItemSelectedWithId(v.getId());
			}
		});
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		ab.setDisplayShowHomeEnabled(false);
		ab.setDisplayShowTitleEnabled(false);
		ab.setDisplayShowCustomEnabled(true);
		ab.setCustomView(mTitleView);
		
//		// Create the adapter that will return a fragment for each of the three
//		// primary sections of the activity.
//		mScreenPagerAdapter = new ScreenPagerAdapter(this);
//
//		// Set up the ViewPager with the sections adapter.
//		mViewPager = (ViewPager) findViewById(R.id.pager);
//		mViewPager.setAdapter(mScreenPagerAdapter);
//		mViewPager.setOffscreenPageLimit(100);
//		mViewPager.setOnPageChangeListener(this);
		
//		setCurrentScreen(R.id.screen_title);

		android.util.Log.d("onCreate()", "Finished");
	}
	
//	@Override
	public ThemeType getThemeType() {
		int themeIndex = getSharedPreferences(mCurrentGame, MODE_PRIVATE).getInt("theme", 0);
		if (themeIndex < 0 || themeIndex >= ThemeType.values().length) themeIndex = 0;
		return ThemeType.values()[themeIndex];
	}

//	@Override
	public void setNewTheme(ThemeType theme) {

		android.util.Log.d("setNewTheme()", "Setting new theme "+getResources().getResourceName(theme.resId));
		
		finish();
		Intent intent = new Intent(this, getClass());
		intent.putExtra("theme", theme.ordinal());
		
		startActivity(intent);
//		overridePendingTransition(0, 0);
	}
	
	@Override
	protected void onPause() {
		autosave();
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		loadState(getSharedPreferences(mCurrentGame, MODE_PRIVATE));
		
		if (mGameState.identifyStartup() && !mWelcomeShown) {
			// Make sure only one of these ever appears, in case onResume() is called again before the dialog is dismissed.
			if (getSupportFragmentManager().findFragmentByTag(IdentifyStartupDialog.class.getName()) == null) {
				showDialogFragment(IdentifyStartupDialog.newInstance());
			}
		}
	}
	
	public void autosave() {
		saveState(getSharedPreferences(mCurrentGame, MODE_PRIVATE));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		ActionBar ab = getSupportActionBar();
		TypedValue tv = new TypedValue();
		
		
//		int index = getViewFlipper().getDisplayedChild();
////		int index = mViewPager.getCurrentItem();
		
//		BaseScreen screen = findScreenById(getCurrentScreenId());
		
		ScreenType screenType = mCurrentScreen;
		
//		if (screen == null) {
		if (screenType == null) {
			// We're creating menu before the layouts are all ready, so stop and try again later
			return false;
		}

//		getMenuInflater().inflate(R.menu.main, menu);
//		if (!getGameState().developerMode()) menu.removeGroup(R.id.menu_group_extra);
		

//		if (screen.getTag() != null) {
		if (screenType.docked) {
//		if (ScreenPagerAdapter.DOCKED_SCREEN_LIST.contains(screen.getType())) {
			getMenuInflater().inflate(R.menu.shortcuts, menu);
			
//			ab.setDisplayShowTitleEnabled(false);
//			ab.setDisplayShowHomeEnabled(false);
//			ab.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
//			ab.setSelectedNavigationItem(index);

//			ab.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
//			ab.setDisplayShowHomeEnabled(false);
//			ab.setDisplayShowTitleEnabled(true);
//			ab.setTitle(screen.getType().titleId);
//			ab.setDisplayHomeAsUpEnabled(true);
//			ab.setDisplayHomeAsUpEnabled(false);
//			ab.setHomeButtonEnabled(false);
			
//			TextView mTitleView = (TextView) LayoutInflater.inflate(getSupportActionBarContext(), R.layout.ab_title);
//			mTitleView.setText(screen.getType().titleId);
//			getTheme().resolveAttribute(R.attr.actionBarSpinnerTextStyle, tv, true);
//			mTitleView.setTextAppearance(MainActivity.this, tv.data);
//			mTitleView.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					onOptionsItemSelectedWithId(v.getId());
//				}
//			});
//			ab.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
//			ab.setDisplayShowHomeEnabled(false);
//			ab.setDisplayShowTitleEnabled(false);
//			ab.setDisplayShowCustomEnabled(true);
//			ab.setCustomView(mTitleView);
			
			
			if (getTheme().resolveAttribute(R.attr.actionBarBackgroundDropdown, tv, true)) {
				TypedArray ta = obtainStyledAttributes(tv.resourceId, new int[] {android.R.attr.background});
				ta.getValue(0, tv);
				ab.setBackgroundDrawable(getResources().getDrawable(tv.resourceId));
				ta.recycle();
			}
			

			ScreenType[] screens = ScreenType.dropdownValues();
			for (int i = 0; i < SHORTCUT_IDS.length; i++) {
				MenuItem item = menu.findItem(SHORTCUT_IDS[i]);
				int screenIndex = getGameState().getShortcut(i+1);
				String title = getResources().getString(screens[screenIndex].titleId);
				String shortcut = getResources().getString(screens[screenIndex].shortcutId);
				item.setTitle(title);
				item.setTitleCondensed(shortcut);
				item.setAlphabeticShortcut(shortcut.charAt(0));
			}

//			if (mActionBarSpinner != null) {
//				mActionBarSpinner.setText(screen.getTag());
//			}
			mTitleText.setText(screenType.titleId);
			mTitleIcon.setVisibility(View.GONE);
			
			getTheme().resolveAttribute(R.attr.actionBarTitleBackground, tv, true);
			mTitleView.setBackgroundResource(tv.resourceId);
			
		} else {
			
//			ab.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
//			ab.setDisplayShowTitleEnabled(true);
//			ab.setDisplayHomeAsUpEnabled(false);
//			ab.setHomeButtonEnabled(false);
			
			if (getTheme().resolveAttribute(R.attr.actionBarBackgroundTitle, tv, true)) {
				TypedArray ta = obtainStyledAttributes(tv.resourceId, new int[] {android.R.attr.background});
				ta.getValue(0, tv);
				ab.setBackgroundDrawable(getResources().getDrawable(tv.resourceId));
				ta.recycle();
			}

//			if (getCurrentScreenId() == R.id.screen_encounter) {
			if (getCurrentScreenType() == ScreenType.ENCOUNTER) {
				mTitleText.setText(R.string.screen_encounter);
				mTitleIcon.setVisibility(View.GONE);
//				ab.setTitle(R.string.screen_encounter);
//				ab.setDisplayShowHomeEnabled(false);
				
//				getMenuInflater().inflate(R.menu.encounter, menu);
			} else {
				mTitleText.setText(R.string.app_name);
				mTitleIcon.setVisibility(View.VISIBLE);
//				ab.setTitle(R.string.app_name);
//				ab.setDisplayShowHomeEnabled(true);
				
				// No retiring from title or endgame screens
				menu.removeItem(R.id.menu_retire);
//				menu.removeGroup(R.id.menu_group_game);
			}

			getTheme().resolveAttribute(R.attr.selectableItemBackground, tv, true);
			mTitleView.setBackgroundResource(tv.resourceId);
		}
		

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT && !hasWriteExternalPermission()) {
			menu.removeItem(R.id.menu_savegame);
		}
		
////		if (getViewFlipper().getVisibility() != View.VISIBLE) {
////			android.util.Log.d("onCreateOptionsMenu()", "Closing loading screen");
////			findViewById(R.id.loading).setVisibility(View.GONE);
////			getViewFlipper().setVisibility(View.VISIBLE);
////		}
//		if (getFragmentContainer().getVisibility() != View.VISIBLE) {
//			android.util.Log.d("onCreateOptionsMenu()", "Closing loading screen");
//			findViewById(R.id.loading).setVisibility(View.GONE);
//			getFragmentContainer().setVisibility(View.VISIBLE);
//		}
				
		boolean out = super.onCreateOptionsMenu(menu);
		android.util.Log.d("onCreateOptionsMenu()","Options menu (re-)created!");
		return out;
	}
	
	private boolean hasWriteExternalPermission() {
		PackageManager pm = getPackageManager();
		int hasPerm = pm.checkPermission(
				android.Manifest.permission.WRITE_EXTERNAL_STORAGE, 
				getPackageName());
		return hasPerm == PackageManager.PERMISSION_GRANTED;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if (item.getGroupId() == R.id.menu_group_shortcuts) {
			for (int i = 0; i < mShortcutKeys.length; i++) {
				if (item.getAlphabeticShortcut() == mShortcutKeys[i]) {
//					setCurrentScreen(getViewFlipper().getChildAt(i).getId());
					setCurrentScreenType(ScreenType.values()[i]);
//					setCurrentScreen((int)mScreenPagerAdapter.getItemId(i));
					return true;
				}

			}
			return false;
		}
		return onOptionsItemSelectedWithId(item.getItemId()) || super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onMenuItemClick(MenuItem item) {
//		if (item.getGroupId() == R.id.menu_group_command) {
//			setCurrentScreen(item.getItemId());
//			return true;
//		}
//	
//		return onOptionsItemSelectedWithId(item.getItemId());
		
		if (item.getGroupId() == R.id.menu_group_command) {
			for (int i = 0; i < mShortcutKeys.length; i++) {
				if (item.getAlphabeticShortcut() == mShortcutKeys[i]) {
//					setCurrentScreen(getViewFlipper().getChildAt(i).getId());
					setCurrentScreenType(ScreenType.values()[i]);
//					setCurrentScreen((int)mScreenPagerAdapter.getItemId(i));
					return true;
				}

			}
			return true;
		}
	
		return onOptionsItemSelectedWithId(item.getItemId());
	}
	
	public boolean onOptionsItemSelectedWithId(int id) {
		finishMenuActionMode();
		
		android.util.Log.d("","Selecting menu item with id "+getResources().getResourceEntryName(id));
		
		switch (id) {
		case R.id.menu_keyboard:
			// XXX for occasional debugging.
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

			return true;

		case R.id.menu_options:

			showDialogFragment(OptionsDialog.newInstance());
			return true;
		case R.id.menu_shortcuts:

			showDialogFragment(ShortcutDialog.newInstance());

			return true;
		case R.id.menu_scores:
			getGameState().viewHighScores();
			return true;
		case R.id.menu_clearscores:
			showDialogFragment(ConfirmDialog.newInstance(
					R.string.dialog_clearscores_title, 
					R.string.dialog_clearscores_message, 
					R.string.help_cleartable,
					new OnConfirmListener() {
						@Override
						public void onConfirm() {
							mGameState.initHighScores();
						}
					}, null));
			return true;
		case R.id.menu_new:
			showDialogFragment(ConfirmDialog.newInstance(
					R.string.dialog_newgame_confirm, 
					R.string.dialog_newgame_confirm_message, 
					R.string.help_confirmnew,
					new OnConfirmListener() {
						@Override
						public void onConfirm() {
//							setCurrentScreen(R.id.screen_title);
//							((TitleScreen)findScreenById(R.id.screen_title)).onSingleClick(null);
							setCurrentScreenType(ScreenType.TITLE);
							showDialogFragment(NewGameDialog.newInstance());
						}
					}, null));
			return true;
		case R.id.menu_switch:
			showDialogFragment(ConfirmDialog.newInstance(
					R.string.dialog_switchgame_title, 
					R.string.dialog_switchgame_message, 
					R.string.help_switchgame, 
					new OnConfirmListener() {

						@Override
						public void onConfirm() {
							SharedPreferences currentPrefs = getSharedPreferences(mCurrentGame, MODE_PRIVATE);
							saveState(currentPrefs);

							final String otherGame = mCurrentGame.equals(GAME_1)? GAME_2 : GAME_1;
							SharedPreferences otherPrefs = getSharedPreferences(otherGame, MODE_PRIVATE);

							if (!otherPrefs.getBoolean("game started", false)) {
								// start new game
//								setCurrentScreen(R.id.screen_title);
								setCurrentScreenType(ScreenType.TITLE);
								showDialogFragment(SimpleDialog.newInstance(
										R.string.dialog_switchtonew_title, 
										R.string.dialog_switchtonew_message, 
										R.string.help_switchtonew,
										new OnConfirmListener() {
											@Override
											public void onConfirm() {
												String defaultName = getString(otherGame.equals(GAME_1)? R.string.name_commander : R.string.name_commander2);
												mGameState.switchToNew(defaultName);
											}
										}));

							} else {
								// switch to existing game

								SharedPreferences.Editor otherEditor = otherPrefs.edit();
								if (otherPrefs.getBoolean("sharePreferences", true)) { // TODO change default from true to current value of GameState.sharePreferences
									// If other game shares preferences, then copy from current game.

									mGameState.copyPrefs(currentPrefs, otherEditor);
								}
								otherEditor.putInt("theme", getThemeType().ordinal()); // XXX For now, theme is always the same in both games so we don't need to reset the activity here.
								otherEditor.commit();
								
								loadState(otherPrefs);

								showDialogFragment(SimpleDialog.newInstance(
										R.string.dialog_switched_title, 
										R.string.dialog_switched_message, 
										R.string.help_switched,
										getGameState().nameCommander()));
							}

							mCurrentGame = otherGame;
							getPreferences(MODE_PRIVATE).edit().putString("currentGame", mCurrentGame).commit();
						}
					}, 
					null));
			return true;
		case R.id.menu_retire:
			showDialogFragment(ConfirmDialog.newInstance(
					R.string.dialog_retire_title, 
					R.string.dialog_retire_message, 
					R.string.help_retire,
					new OnConfirmListener() {
						@Override
						public void onConfirm() {
							mGameState.showEndGameScreen(EndStatus.RETIRED);
						}
					}, null));
			return true;


		case R.id.menu_savegame:
			saveSnapshot();
			return true;


		case R.id.menu_help_about:
			showDialogFragment(AboutDialog.newInstance());
			return true;

		case R.id.menu_help_acknowledgements:
			showDialogFragment(HelpDialog.newInstance(R.string.help_acknowledgements));
			return true;

		case R.id.menu_help_current:
//			showDialogFragment(HelpDialog.newInstance(findScreenById(getCurrentScreenId()).getHelpTextResId()));
			showDialogFragment(HelpDialog.newInstance(getCurrentScreen().getHelpTextResId()));
			return true;

		case R.id.menu_help_firststeps:
			showDialogFragment(HelpDialog.newInstance(R.string.help_firststeps));
			return true;

		case R.id.menu_help_howtoplay:
			showDialogFragment(HelpDialog.newInstance(R.string.help_howtoplay));
			return true;

		case R.id.menu_help_helponmenu:
			showDialogFragment(HelpDialog.newInstance(R.string.help_helponmenu));
			return true;

		case R.id.menu_help_skills:
			showDialogFragment(HelpDialog.newInstance(R.string.help_skills));
			return true;

		case R.id.menu_help_shipequipment:
			showDialogFragment(HelpDialog.newInstance(R.string.help_shipequipment));
			return true;

		case R.id.menu_help_trading:
			showDialogFragment(HelpDialog.newInstance(R.string.help_trading));
			return true;

		case R.id.menu_help_traveling:
			showDialogFragment(HelpDialog.newInstance(R.string.help_travelling));
			return true;

		case R.id.menu_help_documentation:
			// Show the documentation html file from the original game, in a webview
			Intent intent = new Intent(this, DocumentationActivity.class);
			intent.putExtra("theme", getThemeType().resId);
			startActivity(intent);
			return true;

		case R.id.home:
		case android.R.id.home:
			startMenuActionMode();
			return true;
		}
		return false;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mActionMode != null) {
			switch (event.getActionMasked()) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_POINTER_DOWN:
					finishMenuActionMode();
					return true;
			}
		}
		
		if (super.onTouchEvent(event)) return true;
		

		return false;
	}


	@Override
	public void onBackPressed() {
//		if (getSupportFragmentManager().getBackStackEntryCount() > 0) super.onBackPressed();
		
		if (mGameState.recallScreens() && mBackStack.size() > 0) {
//			setCurrentScreen(mBackStack.removeFirst());
			setCurrentScreenType(mBackStack.removeFirst());
			android.util.Log.d("onBackPressed()", "Popping back stack. Remaining states: "+mBackStack.size());
			mBackStack.removeFirst(); // setCurrentScreen will add a new layer, so pop it off.
		} 
		else {
			showExitDialog();
		}
	}
	
	private void showExitDialog() {
		BaseDialog dialog = ConfirmDialog.newInstance(
				R.string.dialog_exit_title, 
				R.string.dialog_exit_message, 
				R.string.help_exit,
				new OnConfirmListener() {
			@Override
			public void onConfirm() {
				new Handler().post(new Runnable() {
					@Override
					public void run() {
						MainActivity.super.onBackPressed();
					}
				});
			}
		}, null);
		showDialogFragment(dialog);
	}

//	@Override
	public GameState getGameState() {
		return mGameState;
	}

//	@Override
	public void clearBackStack() {
		mBackStack.clear();
	}

//	@Override
	public void showDialogFragment(BaseDialog dialog) {
//		dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogTheme);
		
		String tag = dialog.getClass().getName();
		android.util.Log.d("showDialogFragment()", "Showing fragment "+tag);
		
		
		if (mShowingDialog) {
			android.util.Log.d("showDialogFragment()", "Previous dialog display already in progress!");
			if (!mDialogQueue.contains(dialog)) mDialogQueue.addFirst(dialog);
			return;
		}
		
		mShowingDialog = true;
		
		FragmentManager fm = getSupportFragmentManager();
//		FragmentTransaction ft = fm.beginTransaction();
//		Fragment prev = fm.findFragmentByTag(tag);
//		if (prev != null) {
//			ft.remove(prev);
//		}
//		ft.addToBackStack(null);

//		if (getCurrentScreenId() == R.id.screen_encounter) getGameState().clearButtonAction();
		if (getCurrentScreenType() == ScreenType.ENCOUNTER) getGameState().clearButtonAction();
		
		// Show the dialog.
		dialog.show(fm, tag);
	}
	
	private void startMenuActionMode() {

		if (getCurrentScreenType() == ScreenType.ENCOUNTER) getGameState().clearButtonAction();
		
//		final boolean showCommand = findScreenById(getCurrentScreenId()).getType().docked;
		final boolean showCommand = getCurrentScreenType().docked;
//		final boolean showCommand = ScreenPagerAdapter.DOCKED_SCREEN_LIST.contains(findScreenById(getCurrentScreenId()).getType());
		
		
		mActionMode = startSupportActionMode(new ActionMode.Callback() {
//			private ListPopupWindow commandDropdown;
			
			@TargetApi(Build.VERSION_CODES.HONEYCOMB)
			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				android.util.Log.d("Menu Item Click","Preparing MenuSpinnerActionProvider");
				// Do nothing
				return false;
				
//				if (commandDropdown != null) commandDropdown.show();
//				return true;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {
				android.util.Log.d("Menu Item Click","Destroying MenuSpinnerActionProvider");
				// Do nothing
			}

			@TargetApi(Build.VERSION_CODES.HONEYCOMB)
			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				android.util.Log.d("Menu Item Click","Creating MenuSpinnerActionProvider");
				
				final View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.menu_action_mode_spinners, null);
				final Button command = (Button) view.findViewById(R.id.menu_command);
				final Button game = (Button) view.findViewById(R.id.menu_game);
				final Button help = (Button) view.findViewById(R.id.menu_help);

//				TypedValue tv = new TypedValue();
//				TypedArray ta;
								
				// TODO: Convert these PopupMenus to ListPopupWindows
				if (showCommand) {			
//					// This big commented block is a first pass at using ListPopupWindow instead of PopupMenu. It's difficult to get the dropdown to behave correctly,
//					// specifically to set the correct width (wrap_content wraps to the anchor view) and to appear/disappear at the proper location when displayed immediately 
//					final ScreenType[] screens = ScreenType.dropdownValues();
//					ListAdapter adapter = new BaseAdapter() {
//						
//						@Override
//						public View getView(int position, View convertView, ViewGroup parent) {
//							View listItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_dropdown_item_actionbar, parent, false);
//							TextView text1 = (TextView) listItem.findViewById(R.id.spinner_shortcut);
//							text1.setText(screens[position].shortcutId);
//							text1.setTypeface(Typeface.MONOSPACE);
//							TextView text2 = (TextView) listItem.findViewById(R.id.spinner_text);
//							text2.setText(screens[position].titleId);
//							return listItem;
//						}
//						
//						@Override
//						public long getItemId(int position) {
//							return screens[position].fragmentId;
//						}
//						
//						@Override
//						public Object getItem(int position) {
//							return screens[position];
//						}
//						
//						@Override
//						public int getCount() {
//							return screens.length;
//						}
//
//					};
//					final ListPopupWindow commandDropdown = new ListPopupWindow(MainActivity.this);
////					commandDropdown = new ListPopupWindow(MainActivity.this);
//					commandDropdown.setAdapter(adapter);
//					commandDropdown.setAnchorView(command);
//					
//					
////					getTheme().resolveAttribute(R.attr.panelMenuListWidth, tv, true);
////					commandDropdown.setContentWidth((int) (tv.getDimension(getResources().getDisplayMetrics()) + 0.5));
////					commandDropdown.setContentWidth(ListPopupWindow.WRAP_CONTENT);
////					commandDropdown.setWidth(ListPopupWindow.WRAP_CONTENT);
//					commandDropdown.setOnItemClickListener(new ListView.OnItemClickListener() {
//
//						@Override
//						public void onItemClick(
//								android.widget.AdapterView<?> parent,
//								View view, int position, long id) {
//							setCurrentScreen(screens[position].fragmentId);
//							commandDropdown.dismiss();
//						}
//					});
//
//					command.setOnClickListener(new OnClickListener() {
//						
//						@Override
//						public void onClick(View v) {
//							if (commandDropdown.isShowing()) {
//								commandDropdown.dismiss();
//							} else {
//								commandDropdown.show();
//							}
//						}
//					});
//					command.setOnTouchListener(ListPopupWindowCompat.createDragToOpenListener(commandDropdown, command));
//					commandDropdown.show();
					
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						initializeDropdownHoneycomb(command, R.menu.command).show();
					} else {
						initializeDropdownCompat(command, R.menu.command).show();
					}
				} else {
					command.setVisibility(View.GONE);
				}
				
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					android.widget.PopupMenu gameDropdown = initializeDropdownHoneycomb(game, R.menu.game);
					if (!getGameState().developerMode()) gameDropdown.getMenu().removeGroup(R.id.menu_group_extra);	// Dev options eg call keyboard for testing.
					if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT && !hasWriteExternalPermission()) {
						gameDropdown.getMenu().removeItem(R.id.menu_savegame);
					}
					if (!getCurrentScreenType().docked) {
//						if (!ScreenPagerAdapter.DOCKED_SCREEN_LIST.contains(findScreenById(getCurrentScreenId()).getType())) {
						gameDropdown.getMenu().removeItem(R.id.menu_retire);
					}
//					if (!showCommand) gameDropdown.show();	// TODO is this desired behavior?
					
					initializeDropdownHoneycomb(help, R.menu.help);
				} else {
					PopupMenu gameDropdown = initializeDropdownCompat(game, R.menu.game);
					if (!getGameState().developerMode()) gameDropdown.getMenu().removeGroup(R.id.menu_group_extra);	// Dev options eg call keyboard for testing.
					if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT && !hasWriteExternalPermission()) {
						gameDropdown.getMenu().removeItem(R.id.menu_savegame);
					}
					if (!getCurrentScreenType().docked) {
//						if (!ScreenPagerAdapter.DOCKED_SCREEN_LIST.contains(findScreenById(getCurrentScreenId()).getType())) {
						gameDropdown.getMenu().removeItem(R.id.menu_retire);
					}
					
					initializeDropdownCompat(help, R.menu.help);
				}
				mode.setCustomView(view);

				return true;
			}

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				android.util.Log.d("Menu Item Click","Clicking MenuSpinnerActionProvider");
				// Do nothing
				return false;
			}
		});
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private android.widget.PopupMenu initializeDropdownHoneycomb(View anchor, int menuRes) {
		final android.widget.PopupMenu dropdown = new android.widget.PopupMenu(MainActivity.this, anchor);
//		dropdown.inflate(menuRes); // API 14
		dropdown.getMenuInflater().inflate(menuRes, dropdown.getMenu());

		// Anon inner class instead of making Activity implement both OnMenuItemClickListener interfaces so that Activity doesn't need to be tagged as API 11+.
		dropdown.setOnMenuItemClickListener(new android.widget.PopupMenu.OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				return MainActivity.this.onMenuItemClick(item);
			}
		});
		anchor.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dropdown.show();
			}
		});
		OnTouchListener listener = PopupMenuCompat.getDragToOpenListener(dropdown);
		if (listener != null) anchor.setOnTouchListener(listener);
		
		return dropdown;
	}

	private PopupMenu initializeDropdownCompat(View anchor, int menuRes) {
		final PopupMenu dropdown = new PopupMenu(MainActivity.this, anchor);
		dropdown.inflate(menuRes);
		dropdown.setOnMenuItemClickListener(MainActivity.this);
		anchor.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dropdown.show();
			}
		});
		
		return dropdown;
	}
	
	boolean finishMenuActionMode() {
		if (mActionMode != null) {
			mActionMode.finish();
			mActionMode = null;
			return true;
		}
		return false;
	}
	
	public BaseScreen getCurrentScreen() {
		return (BaseScreen) getSupportFragmentManager().findFragmentByTag(getResources().getString(mCurrentScreen.titleId));
	}

////	@Override
//	public BaseScreen findScreenById(int id) {
//		if (id <= 0) return null;
//		BaseScreen screen = mScreenCache.get(id);
//		if (screen == null) {
//			screen = ((BaseScreen) getSupportFragmentManager().findFragmentById(id));
////			screen = (BaseScreen) mScreenPagerAdapter.getItem(mScreenPagerAdapter.getItemPosition(id));
//			mScreenCache.append(id, screen);
//		}
//		return screen;
//		
////		return ((BaseScreen) getSupportFragmentManager().findFragmentById(id));
//////		return (BaseScreen) mScreenPagerAdapter.getItem(mScreenPagerAdapter.getItemPosition(id));
//	}

////	@Override
//	public int getCurrentScreenId() {
//		ViewFlipper vf = getViewFlipper();
//		if (vf == null) return -1;
//		View view = vf.getCurrentView();
//		if (view == null) return -1;
//		return getViewFlipper().getCurrentView().getId();
//
////		int id = (int) mScreenPagerAdapter.getItemId(mViewPager.getCurrentItem());
////		android.util.Log.d("getCurrentScreenId()", "Current screen id is "+getResources().getResourceName(id));
////		return id;
//	}
	public ScreenType getCurrentScreenType() {
		return mCurrentScreen;
	}
	
	void showQueuedDialog() {
		if (mDialogQueue.isEmpty()) return;
		
		showDialogFragment(mDialogQueue.removeFirst());
	}

////	@Override
//	public BaseScreen setCurrentScreen(int id) {
//		android.util.Log.d("setCurrentScreen()","Setting screen "+getResources().getResourceEntryName(id));
//		
////		Debug.startMethodTracing("spacetrader");
//		
//		int prevId = getCurrentScreenId(); // TODO should I return here on prevId == id?
//		if (/*prevId != id && */mActionMode != null) {
//			mActionMode.finish();
//			mActionMode = null;
//		}
//				
//		ViewFlipper vf = getViewFlipper();
//		
//		int index = vf.indexOfChild(findViewById(id));
//		if (index < 0) {
//			throw new IllegalArgumentException("View id "+getResources().getResourceName(id)+" is not a valid Screen");
//		}
//		
////		int index = mScreenPagerAdapter.getItemPosition(id);
////		
////		if (index == FragmentPagerAdapter.POSITION_NONE) {
////			for (ScreenType type : ScreenType.values()) {
////				if (id == type.fragmentId) {
////					index = 0;
////					mScreenPagerAdapter.setUnscrollable(type);
////					break;
////				}
////			}
////		}
//		
//		BaseScreen prev = findScreenById(prevId);
//		BaseScreen next = findScreenById(id);
//		
//		if (next.getTag() != null && prev.getTag() != null && !next.equals(prev)) {
//			mBackStack.addFirst(prev.getId());
//			android.util.Log.d("setCurrentScreen()", "Adding "+prev.getTag()+" to back stack. Total size is "+mBackStack.size());
//		} else if (!next.equals(prev)) {
//			mBackStack.clear();
//			android.util.Log.d("setCurrentScreen()", "Clearing back stack.");
//		}
//		
//		next.onShowScreen();
//		next.onRefreshScreen();
//		
//		vf.setDisplayedChild(index);
//		
////		mViewPager.setCurrentItem(index);
//
////		supportInvalidateOptionsMenu();
//		if ((next.getTag() == null) || prev.getTag() == null) supportInvalidateOptionsMenu();
//		else mTitleText.setText(next.getTag());	// If we don't recreate menu than we must update title here instead.
//		
////		Debug.stopMethodTracing();
//		
//		return next;
//
//	}

	public BaseScreen setCurrentScreenType(ScreenType type) {
		android.util.Log.d("setCurrentScreen()","Setting screen "+type);
		
		ScreenType prevType = getCurrentScreenType();
		if (/*prevType != prevType || */true) {
			finishMenuActionMode();
		}

		BaseScreen next;
		try {
			next = type.screenClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.container, next, getResources().getString(type.titleId));
		ft.commit();

		if (type != null && prevType != null && type.docked && prevType.docked && type != prevType) {
			mBackStack.addFirst(prevType);
			android.util.Log.d("setCurrentScreen()", "Adding "+prevType+" to back stack. Total size is "+mBackStack.size());
		} else if (type != prevType) {
			mBackStack.clear();
			android.util.Log.d("setCurrentScreen()", "Clearing back stack.");
		}
		
//		next.onShowScreen();
//		next.onRefreshScreen();
		mCurrentScreen = type;
		
		supportInvalidateOptionsMenu();
//		if (prevType == null || !type.docked || !prevType.docked) supportInvalidateOptionsMenu();
//		else mTitleText.setText(type.titleId);	// If we don't recreate menu than we must update title here instead.
		
		return next;
	}
	
	// The following 3 methods are hacks so I don't have to update other classes yet
	public BaseScreen findScreenById(int id) {
		if (getCurrentScreenType().fragmentId == id) return getCurrentScreen();
		return null;
	}
	public int getCurrentScreenId() {
		return getCurrentScreenType().fragmentId;
	}
	public BaseScreen setCurrentScreen(int id) {
		for (ScreenType type : ScreenType.values()) {
			if (type.fragmentId == id) return setCurrentScreenType(type);
		}
		return null;
	}

//	@Override
	public BaseDialog findDialogByClass(Class<? extends BaseDialog> tag) {
		return ((BaseDialog) getSupportFragmentManager().findFragmentByTag(tag.getName()));
	}

//	private ViewFlipper getViewFlipper() {
//		if (mViewFlipper == null) {
//			mViewFlipper = (ViewFlipper)findViewById(R.id.viewflipper);
//		}
//		return mViewFlipper;
////		return (ViewFlipper)findViewById(R.id.viewflipper);
//	}
	
//	private FrameLayout getFragmentContainer() {
//		if (mFragmentContainer == null) {
//			mFragmentContainer = (FrameLayout)findViewById(R.id.container);
//		}
//		return mFragmentContainer;
//	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		int keyCode = event.getKeyCode();
		char shortcut = event.getMatch(mShortcutKeys);
		boolean keyDown = event.getMetaState() == 0 && event.getAction() == KeyEvent.ACTION_DOWN;
		
		if (shortcut > 0) {
			if (keyDown) for (int i = 0; i < mShortcutKeys.length; i++) {
				if (shortcut == mShortcutKeys[i]) {
//					setCurrentScreen(getViewFlipper().getChildAt(i).getId());
					setCurrentScreenType(ScreenType.values()[i]);
//					setCurrentScreen((int)mScreenPagerAdapter.getItemId(i));
					return true;
				}
			}
		}
		switch (keyCode) {
		case KeyEvent.KEYCODE_O:
			
			if (keyDown) showDialogFragment(OptionsDialog.newInstance());
			return true;
			
		case KeyEvent.KEYCODE_H:
//			if (keyDown) showDialogFragment(HelpDialog.newInstance(findScreenById(getCurrentScreenId()).getHelpTextResId()));
			if (keyDown) showDialogFragment(HelpDialog.newInstance(getCurrentScreen().getHelpTextResId()));
			return true;

		case KeyEvent.KEYCODE_VOLUME_UP:
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			if (!getGameState().volumeScroll()) break;
			
//			switch (getCurrentScreenId()) {
//			case R.id.screen_warp_target:
//			case R.id.screen_warp_avgprices:
//			case R.id.screen_chart:
			switch (getCurrentScreenType()) {
			case TARGET:
			case AVGPRICES:
			case CHART:
				if (keyDown) return mGameState.scrollSystem(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN);
				else return true;
			default:
				// Drop to super call for default volume-change behavior
				break;
			}
			break;
			
		case KeyEvent.KEYCODE_BACK:
			if (event.isLongPress() && keyDown) {
				showExitDialog();
				return true;
			}
			// Otherwise, we use default key-handling, and push through to onBackPressed().
			break;
			
		case KeyEvent.KEYCODE_MENU:
//			if (getCurrentScreenId() == R.id.screen_encounter) getGameState().clearButtonAction();
			if (getCurrentScreenType() == ScreenType.ENCOUNTER) getGameState().clearButtonAction();
			if (keyDown) {
				if (!finishMenuActionMode()) {
					startMenuActionMode();
				}
			}
			return true;

		}

		return super.dispatchKeyEvent(event);
	}

//	@Override
//	public boolean onNavigationItemSelected(int position, long itemId) {
//		
//		setCurrentScreen(getViewFlipper().getChildAt(position).getId());
////		setCurrentScreen((int)mScreenPagerAdapter.getItemId(position));
//		return true;
//	}
	
//	@Override
//	public void onPageSelected(int position) {
//		android.util.Log.i("onPageSelected", "Selecting page "+mScreenPagerAdapter.getPageTitle(position));
//		
//		if (mTitleView != null) mTitleView.setText(mScreenPagerAdapter.getPageTitle(position));
////		for (int i = 0, s = ScreenPagerAdapter.DROPDOWN_SCREEN_LIST.size(); i < s; i++) {
////			if (mScreenPagerAdapter.getScreenType(position) == ScreenPagerAdapter.DROPDOWN_SCREEN_LIST.get(i)) {
////				getSupportActionBar().setSelectedNavigationItem(i);
////				break;
////			}
////		}
//		
////		findScreenById(getCurrentScreenId()).onShowScreen();
////		findScreenById(getCurrentScreenId()).onRefreshScreen();
//	}
//	
//	@Override
//	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//		// Do nothing
//	}
//	
//	@Override
//	public void onPageScrollStateChanged(int state) {
//		if (state == ViewPager.SCROLL_STATE_IDLE) {
//			if (mViewPager.getCurrentItem() != mScreenPagerAdapter.getSubsectionPosition()) {
//				mScreenPagerAdapter.reset();
//			}
//			
////			findScreenById(getCurrentScreenId()).onShowScreen();
////			findScreenById(getCurrentScreenId()).onRefreshScreen();
//			
////			supportInvalidateOptionsMenu();
//		}
//	}
	
//	public ViewPager getViewPager() {
//		return mViewPager;
//	}
	
	private void saveState(SharedPreferences prefs) {
		SharedPreferences.Editor editor = prefs.edit();
		
		editor.clear();	// We're going to rewrite everything, so make sure we're starting from a blank slate.

		editor.putInt("theme", getThemeType().ordinal());

//		editor.putInt("screen id", getCurrentScreenId());
		editor.putInt("screen type", getCurrentScreenType().ordinal());

//		editor.putBoolean("game started", !(getCurrentScreenId() == R.id.screen_title || getCurrentScreenId() == R.id.screen_endofgame));
		editor.putBoolean("game started", !(getCurrentScreenType() == ScreenType.TITLE || getCurrentScreenType() == ScreenType.ENDGAME));
		
		int s = mBackStack.size();
		editor.putInt("backstack size", s);
		for (int i = 0; i < s; i++) {
//			editor.putInt("backstack_"+i, mBackStack.removeFirst());
			editor.putInt("backstack_"+i, mBackStack.removeFirst().ordinal());
		}
		
		mGameState.saveState(editor);
		
		editor.commit();
	}
	
	private void loadState(SharedPreferences prefs) {
		android.util.Log.d("loadState()","Begin loading state");
		mGameState.loadState(prefs);
		if (DEVELOPER_MODE) {

			// XXX Give user choice to avoid losing savegame in the event of an error. In final game will just error normally without try/catch.
			try {
//				setCurrentScreen(prefs.getInt("screen id", R.id.screen_title));
				setCurrentScreenType(ScreenType.values()[prefs.getInt("screen type", ScreenType.TITLE.ordinal())]);
			} catch (final Exception e) {
				e.printStackTrace();

				showDialogFragment(ConfirmDialog.newInstance(
						"Load Game Failed!", 
						"An error occured loading savegame. Would you like to scrap it and start over?",
						-1,
						new OnConfirmListener() {

							@Override
							public void onConfirm() {
//								setCurrentScreen(R.id.screen_title);
								setCurrentScreenType(ScreenType.TITLE);
							}
						},
						new OnCancelListener() {

							@Override
							public void onCancel() {
								throw new RuntimeException(e);
							}
						}));

			}
		
		} else {
//			setCurrentScreen(prefs.getInt("screen id", R.id.screen_title));
			setCurrentScreenType(ScreenType.values()[prefs.getInt("screen type", ScreenType.TITLE.ordinal())]);
		}

//		setCurrentScreen(prefs.getInt("screen id", R.id.screen_title));
//		setCurrentScreen(R.id.screen_title);
		
		mBackStack.clear();
		for (int i = 0, s = prefs.getInt("backstack size", 0); i < s; i++) {
			mBackStack.addFirst(ScreenType.values()[prefs.getInt("backstack_"+i, 0)]);
		}

		supportInvalidateOptionsMenu();
		android.util.Log.d("loadState()","Finished loading state");
	}

//	@Override
	void startClick() {
		mClicking = true;
//		android.util.Log.v("click","Start click");
	}

//	@Override
	void finishClick() {
		mClicking = false;
//		android.util.Log.v("click","Finish click");
	}
	
//	@Override
	boolean isClicking() {
		return mShowingDialog || mClicking;
	}
	
	void reportDialogShown() {
		mShowingDialog = false;
	}
	
//	private void saveStateToPreferences(Bundle state, SharedPreferences prefs) {
//		SharedPreferences.Editor editor = prefs.edit();
//		
//		for (String key : state.keySet()) {
//			Object obj = state.get(key);
//			
//			if (obj instanceof Integer) {
//				editor.putInt(key, (Integer) obj);
//			} else if (obj instanceof Boolean) {
//				editor.putBoolean(key, (Boolean) obj);
//			} else if (obj instanceof String) {
//				editor.putString(key, (String) obj);
//			} else {
//				throw new IllegalArgumentException();
//			}
//		}
//		
//		editor.commit();
//	}
//	
//	private void loadStateFromPreferences(Bundle state, SharedPreferences prefs) {
//		Map<String, ?> map = prefs.getAll();
//		
//		for (String key : map.keySet()) {
//			Object obj = map.get(key);
//			
//			if (obj instanceof Integer) {
//				state.putInt(key, (Integer) obj);
//			} else if (obj instanceof Boolean) {
//				state.putBoolean(key, (Boolean) obj);
//			} else if (obj instanceof String) {
//				state.putString(key, (String) obj);
//			} else {
//				throw new IllegalArgumentException();
//			}
//		}
//	}
	
	private void saveSnapshot() {
		if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			showDialogFragment(SimpleDialog.newInstance(R.string.dialog_cannotsave_title, R.string.dialog_cannotsave_message, R.string.help_cannotsave));
			return;
		}
		
		SharedPreferences prefs = getSharedPreferences(mCurrentGame, MODE_PRIVATE);
		saveState(prefs);
		Map<String, ?> map = prefs.getAll();
		
		File file = new File(getExternalFilesDir(null), SAVEFILE);
		try {
			FileOutputStream fos = new FileOutputStream(file);
			OutputStreamWriter osw = new OutputStreamWriter(fos);
			BufferedWriter bw = new BufferedWriter(osw);
			for (String key : map.keySet()) {
				bw.write(map.get(key).getClass().getSimpleName()+","+key+","+map.get(key)+"\n");
			}
			bw.close();
			osw.close();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			showDialogFragment(SimpleDialog.newInstance(R.string.dialog_cannotsave_title, R.string.dialog_cannotsave_message, R.string.help_cannotsave));
			return;
		} catch (IOException e) {
			e.printStackTrace();
			showDialogFragment(SimpleDialog.newInstance(R.string.dialog_cannotsave_title, R.string.dialog_cannotsave_message, R.string.help_cannotsave));
			return;
		}
		
		showDialogFragment(SimpleDialog.newInstance(R.string.dialog_gamesaved_title, R.string.dialog_gamesaved_message, R.string.help_gamesaved));
	}
	
	public boolean loadSnapshot() {
		if (!(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState()))) {
			showDialogFragment(SimpleDialog.newInstance(R.string.dialog_cannotload_title, R.string.dialog_cannotload_message, R.string.help_cannotload));
			return false;
		}
		
		SharedPreferences prefs = getSharedPreferences(mCurrentGame, MODE_PRIVATE);
		File file = new File(getExternalFilesDir(null), SAVEFILE);
		
		try {
			SharedPreferences.Editor editor = prefs.edit();
			
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);
			String line = br.readLine();
			while (line != null) {
				String[] tokens = line.split(",",3);
				
				String type = tokens[0];
				String key = tokens[1];
				String value = tokens.length == 3? tokens[2] : "";
				
				if (Integer.class.getSimpleName().equals(type)) {
					editor.putInt(key, Integer.valueOf(value));
				} else if (Boolean.class.getSimpleName().equals(type)) {
					editor.putBoolean(key, Boolean.valueOf(value));
				} else if (String.class.getSimpleName().equals(type)) {
					editor.putString(key, value);
				} else {
					android.util.Log.e("loadSnapshot()", "tokens[0] has invalid value "+tokens[0]);
					showDialogFragment(SimpleDialog.newInstance(R.string.dialog_cannotload_title, R.string.dialog_cannotload_message, R.string.help_cannotload));
					br.close();
					isr.close();
					fis.close();
					return false;
				}
				
				line = br.readLine();
			}
			br.close();
			isr.close();
			fis.close();
			editor.commit();
			
			loadState(prefs);
			
		} catch (FileNotFoundException e) {
			showDialogFragment(SimpleDialog.newInstance(R.string.dialog_cannotload_title, R.string.dialog_cannotload_message, R.string.help_cannotload));
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			showDialogFragment(SimpleDialog.newInstance(R.string.dialog_cannotload_title, R.string.dialog_cannotload_message, R.string.help_cannotload));
			e.printStackTrace();
			return false;
		}
		
		return false;
	}
	
	public void pagerClick(View view) {
		int id = view.getId();
		if (id == R.id.screen_warp_target_cost_specific) {
			mGameState.executeWarpFormHandleEvent(id);
			return;
		}
		if (WarpPricesScreen.LABEL_IDS.containsValue(id) || WarpPricesScreen.PRICE_IDS.containsValue(id)) {
			mGameState.averagePricesFormHandleEvent(id);
			return;
		}
		
	}
}
