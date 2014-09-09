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

import android.support.v4.view.ViewPager;
import android.util.Log;

import com.brucelet.spacetrader.datatypes.GameState;


public abstract class WarpSubScreen extends BaseScreen implements ViewPager.OnPageChangeListener {
//	protected abstract void setPageListeners(View view);	// These aren't getting called for no reason I can figure out. Currently implemented through xml instead.
	public abstract ViewPager getPager();
	protected abstract int getSpacerHeight();
	protected abstract WarpSystemPagerAdapter createPagerAdapter();
	protected abstract void onRefreshWarpSubScreen();
	
	@Override
	public void onPageSelected(int position) {
		// Do nothing
		Log.d("WarpSubScreen.onPageSelected()","position="+position);
		
	}
	
	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		// Do nothing
//		Log.v("WarpSubScreen.onPageScrolled()","position="+position+", "+"positionOffset="+positionOffset+", "+"positionOffsetPixels="+positionOffsetPixels);
	}
	
	@Override
	public void onPageScrollStateChanged(int state) {
		if (state == ViewPager.SCROLL_STATE_IDLE) {
			// Update the adapter so that we can continue scrolling.
			
			Log.d("WarpSubScreen.onPageScrollStateChanged()","State idle");

			ViewPager pager = getPager();
			int currentItem = pager.getCurrentItem();
			boolean back;
			if (currentItem == WarpSystemPagerAdapter.POSITION_PREV) {
				back = true;
			} else if (currentItem == WarpSystemPagerAdapter.POSITION_NEXT) {
				back = false;
			} else {
				return;
			}

			GameState gameState = getGameState();
			
			gameState.scrollWarpSystem(back);
			onRefreshScreen();
//			gameState.setAdapterSystems(getPagerAdapter());
		}
	}

	@Override
	public final void onRefreshScreen() {
		onRefreshWarpSubScreen();

		ViewPager pager = getPager();
		WarpSystemPagerAdapter adapter = getPagerAdapter();
		
		if (adapter == null) {
			
			adapter = createPagerAdapter();
			
			pager.setOnPageChangeListener(this);
			pager.setOffscreenPageLimit(2);
			
			adapter.instantiateItem(pager, 0);
			adapter.instantiateItem(pager, 1);
			adapter.instantiateItem(pager, 2);
		}
		pager.setAdapter(adapter); // Logically this doesn't seem necessary on each refresh but it gets rid of an apparent flicker which is otherwise present when the adapter items change.
		pager.setCurrentItem(1, false);
		getGameState().setAdapterSystems(adapter);
//		setPageListeners(pager.getChildAt(0));
//		setPageListeners(pager.getChildAt(1));
//		setPageListeners(pager.getChildAt(2));

		
		Log.d("WarpSubScreen.onRefreshScreen()", "current item is "+pager.getCurrentItem());
	}
	
	public WarpSystemPagerAdapter getPagerAdapter() {
		return (WarpSystemPagerAdapter) getPager().getAdapter();
	}
	
	public void notifyAdapterChanged() {
		getPagerAdapter().notifyDataSetChanged();
	}
}
