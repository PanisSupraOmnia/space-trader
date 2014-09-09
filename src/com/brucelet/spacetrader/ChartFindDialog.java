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





import android.app.AlertDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

public class ChartFindDialog extends BaseDialog {

	public static ChartFindDialog newInstance() {
		return new ChartFindDialog();
	}
	
	public ChartFindDialog() {}
	
	@Override
	public void onBuildDialog(Builder builder) {
		builder.setTitle(R.string.screen_chart_find_title);
		builder.setPositiveButton(R.string.generic_ok).setNegativeButton(R.string.generic_cancel);
		builder.setView(R.layout.screen_chart_find);
	}
		
	@Override
	public void onClickPositiveButton() {
		getGameState().findDialogHandleEvent(AlertDialog.BUTTON_POSITIVE);
	}
	
	@Override
	public void onClickNegativeButton() {
		dismiss();
	}
	
	public static class ChartFindView extends AutoCompleteTextView {

		public ChartFindView(Context context, AttributeSet attrs) {
			super(context, attrs);

			String[] names = getResources().getStringArray(R.array.solar_system_name);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
					context, 
					android.R.layout.simple_list_item_1, 
					names);
			setAdapter(adapter);
		}
	}

	@Override
	public int getHelpTextResId() {
		return R.string.help_findsystem;
	}

}
