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

import android.content.pm.PackageManager.NameNotFoundException;


public class AboutDialog extends BaseDialog {

	public static AboutDialog newInstance() {
		AboutDialog dialog = new AboutDialog();
		return dialog;
	}

	public AboutDialog() {}
	
	@Override
	public final void onBuildDialog(Builder builder) {
		builder.setTitle(R.string.dialog_about);
		builder.setView(R.layout.dialog_about);
		builder.setPositiveButton(R.string.generic_ok);
	}
	
	@Override
	public void onRefreshDialog() {
		String version;
		try {
			version = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			version = "???";
		}
		
		setViewTextById(
				R.id.dialog_about_title,
				R.string.dialog_about_title,
				getResources().getString(R.string.app_name),
				version);
	}

	@Override
	public int getHelpTextResId() {
		return R.string.help_credits;
	}
	
}
