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
//import android.content.res.Resources;
//import android.content.res.Resources.Theme;
//
//import com.brucelet.spacetrader.datatypes.GameState;
//import com.brucelet.spacetrader.enumtypes.ThemeType;
//
//// TODO make GameState members private and interact through this interface instead. (Major refactor)
//public interface GameManager {
//
////	public BaseScreen findScreenById(int id);
////
////	public int getCurrentScreenId();
////
////	public void setCurrentScreen(int id);
//
////	public BaseScreen findScreenByClass(Class<?> tag);
////	public BaseScreen findScreenByTag(String tag);
//	public BaseScreen findScreenById(int id);
//	
////	public Class<?> getCurrentScreenClass();
//	public int getCurrentScreenId();
//
////	public BaseScreen setCurrentScreenByClass(Class<?> tag);
////	public BaseScreen setCurrentScreenByTag(String tag);
//	public BaseScreen setCurrentScreen(int id);
//	
////	public void refreshCurrentScreen();
//	
//	public void showDialogFragment(BaseDialog newFragment);
//
//	public BaseDialog findDialogByClass(Class<? extends BaseDialog> tag);
//
//	// TODO consider ways to do a getCurrentDialog() method
//
//	public GameState getGameState();
//	
////	public void startNewGame();
//	
//	public void setShortcuts(int[] shortcuts);
//	
//	public Resources getResources();
//	
//	public void clearBackStack();
//
//	public void setNewTheme(ThemeType theme);
//	public ThemeType getThemeType();
//	public Theme getTheme();
//	
//	void startClick();
//	void finishClick();
//	boolean isClicking();
//	
//}