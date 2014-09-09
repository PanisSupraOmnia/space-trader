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

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.webkit.WebView;

public class DocumentationActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setTheme(getIntent().getIntExtra("theme", R.style.ActivityTheme_Light));
		
		setContentView(R.layout.activity_documentation);
		
		ActionBar ab = getSupportActionBar();
		ab.setDisplayShowHomeEnabled(true);
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setHomeButtonEnabled(true);
		ab.setDisplayShowTitleEnabled(true);
		
		TypedValue tv = new TypedValue();
		boolean changeBg = getTheme().resolveAttribute(R.attr.actionBarBackgroundTitle, tv, true);
		if (changeBg) {
			TypedArray ta = obtainStyledAttributes(tv.resourceId, new int[] {android.R.attr.background});
			ta.getValue(0, tv);
			ab.setBackgroundDrawable(getResources().getDrawable(tv.resourceId));
			ta.recycle();
		}
		
		getWebView().loadUrl("file:///android_asset/spacetrader.html");
	}
	
	private WebView getWebView() {
		return (WebView) findViewById(R.id.documentation_webview);
	}
	
	@Override
	public void onBackPressed() {
		WebView webView = getWebView();
		if (webView.canGoBack()) {
			webView.goBack();
		} else {
			super.onBackPressed();
		}
	}
}
