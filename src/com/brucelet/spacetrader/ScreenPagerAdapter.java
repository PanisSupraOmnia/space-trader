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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.brucelet.spacetrader.enumtypes.ScreenType;


/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class ScreenPagerAdapter extends FragmentPagerAdapter {
	
	// Subsection of this type will be inserted at this index in the default spinner pages.
	private int mSubsectionIndex;
	private ScreenType mSubsectionType;
	private boolean mShowDockedScreens;
	
	private final MainActivity mActivity;
	
	public static final List<ScreenType> DROPDOWN_SCREEN_LIST = Collections.unmodifiableList(Arrays.asList(ScreenType.dropdownValues()));
	public static final List<ScreenType> DOCKED_SCREEN_LIST = Collections.unmodifiableList(Arrays.asList(ScreenType.dockedValues()));


	public ScreenPagerAdapter(MainActivity activity) {
		super(activity.getSupportFragmentManager());
		
		mActivity = activity;
		if (mActivity == null) throw new NullPointerException();
		
		reset();
	}

	@Override
	public Fragment getItem(int position) {
		
		// getItem is called to instantiate the fragment for the given page.
		ScreenType type = getScreenType(position);
		BaseScreen screen;
		try {
			screen = type.screenClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		screen.setAdapter(this);
		
		return screen;

	}

	@Override
	public int getCount() {
		if (!mShowDockedScreens) return 1;
		return DROPDOWN_SCREEN_LIST.size() + (mSubsectionIndex >= 0? 1 : 0); 
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return mActivity.getString(getScreenType(position).titleId);
	}

	@Override
	public int getItemPosition(Object object) {
		if (object instanceof ScreenType) {
			ScreenType type = (ScreenType) object;
			for (int i = 0, s = getCount(); i < s; i++) {
				if (getScreenType(i) == type) {
					return i;
				}
			}
		}
		
		if (object instanceof Integer) {
			int id = (Integer) object;
			for (int i = 0, s = getCount(); i < s; i++) {
				if (getScreenType(i).fragmentId == id) {
					return i;
				}
			}
		}

		return POSITION_NONE;
	}

	@Override
	public long getItemId(int position) {
		return (long) getScreenType(position).fragmentId;
	}
	
	public ScreenType getScreenType(int position) {
		if (!mShowDockedScreens) {
			return mSubsectionType;
		}
		
		if (mSubsectionIndex >= 0) {
			if (position == mSubsectionIndex) {
				return mSubsectionType;
			} else if (position > mSubsectionIndex) {
				return DROPDOWN_SCREEN_LIST.get(position - 1);
			} else {
				return DROPDOWN_SCREEN_LIST.get(position);
			}
		} else {
			return DROPDOWN_SCREEN_LIST.get(position);
		}
	}
	
	public int getSubsectionPosition() {
		return mSubsectionIndex;
	}

	public void addSubsection(int position, ScreenType type) {
		Log.d("addPage()", "Adding page "+mActivity.getString(type.titleId)+" at position "+position);
		mSubsectionIndex = position;
		mSubsectionType = type;
		mShowDockedScreens = true;

		notifyDataSetChanged();
	}
	
	public void setUnscrollable(ScreenType type) {
		mSubsectionIndex = 0;
		mSubsectionType = type;
		mShowDockedScreens = false;
		
		notifyDataSetChanged();
	}

	public void reset() {
		Log.d("reset()", "Resetting subsection");
		mSubsectionIndex = -1;
		mSubsectionType = null;
		mShowDockedScreens = true;

		notifyDataSetChanged();
	}
	
	public MainActivity getActivity() {
		return mActivity;
	}
}