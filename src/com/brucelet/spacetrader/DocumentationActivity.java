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

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.webkit.WebView;

import com.brucelet.spacetrader.enumtypes.ThemeType;

public class DocumentationActivity extends ActionBarActivity implements View.OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ThemeType themeType = (ThemeType) getIntent().getSerializableExtra("theme");
		setTheme(themeType.resId);
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_documentation);
		
		Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(tb);

		ActionBar ab = getSupportActionBar();
		TypedValue tv = new TypedValue();
		if (getTheme().resolveAttribute(R.attr.actionBarBackgroundDefault, tv, true)) {
			ab.setBackgroundDrawable(getResources().getDrawable(tv.resourceId));
		}
		if (themeType.isMaterialTheme && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			// In Lollipop, use the platform up button so the ripple is masked correctly.
			ab.setDisplayShowTitleEnabled(true);
			ab.setDisplayHomeAsUpEnabled(true);
			if (getTheme().resolveAttribute(R.attr.homeAsUpIndicator, tv, true)) {
				ab.setHomeAsUpIndicator(tv.resourceId);
			}
		} else {
			// Otherwise, use custom layout so that the selector color is correct.
			ab.setCustomView(R.layout.ab_title_documentation);
			View view = ab.getCustomView();
			view.findViewById(R.id.up).setOnClickListener(this);
			ab.setDisplayShowTitleEnabled(false);
			ab.setDisplayShowCustomEnabled(true);
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

	@Override
	public void onClick(View v) {
		onSupportNavigateUp();
	}
}
