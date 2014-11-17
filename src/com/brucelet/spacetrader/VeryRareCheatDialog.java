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

import android.view.LayoutInflater;
import android.view.ViewGroup;

public class VeryRareCheatDialog extends BaseDialog {

	public static VeryRareCheatDialog newInstance() {
		return new VeryRareCheatDialog();
	}
	
	public VeryRareCheatDialog() {}
	
	@Override
	public final void onBuildDialog(Builder builder, LayoutInflater inflater, ViewGroup parent) {
		builder.setTitle(R.string.dialog_veryrarecheat_title).setView(R.layout.dialog_veryrarecheat);
		builder.setPositiveButton(R.string.generic_done);
	}
	
	@Override
	public void onClickPositiveButton() {
		getGameState().veryRareCheatHandleEvent();
		dismiss();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		getGameState().showVeryRareCheat();
	}

	@Override
	public int getHelpTextResId() {
		return -1;
	}

}
