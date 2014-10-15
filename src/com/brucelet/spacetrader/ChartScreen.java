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

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.brucelet.spacetrader.datatypes.GameState;
import com.brucelet.spacetrader.enumtypes.ScreenType;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Activities that
 * contain this fragment must implement the
 * {@link ChartScreen.OnInteractionListener} interface to handle
 * interaction events. Use the {@link ChartScreen#newInstance} factory method
 * to create an instance of this fragment.
 * 
 */
public class ChartScreen extends BaseScreen {
	
	/**
	 * Use this factory method to create a new instance of this fragment.
	 * 
	 * @return A new instance of fragment ChartFragment.
	 */
	public static ChartScreen newInstance() {
		return new ChartScreen();
	}

	public ChartScreen() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.screen_chart, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		View view = getView();
		view.findViewById(R.id.screen_chart_find).setOnClickListener(this);
		view.findViewById(R.id.screen_chart_jump).setOnClickListener(this);
		ChartView chartView = (ChartView) view.findViewById(R.id.screen_chart_chartview);
		chartView.mFragment = this;
	}
	
	public ChartView getChartView() {
		return (ChartView) getView().findViewById(R.id.screen_chart_chartview);
	}
	
	@Override
	public void onShowScreen() {
		getGameState().showGalaxy();
	}
	
	@Override
	public void onRefreshScreen() {
		getChartView().invalidate();
	}
	
	@Override
	public void onSingleClick(View v) {
		getGameState().galacticChartFormHandleEvent(v.getId());
	}
	
	public static class ChartView extends View {
		private ChartScreen mFragment; // TODO should this be dereferenced at some point to prevent memory leak?
		
		public ChartView(Context context, AttributeSet attrs) {
			super(context, attrs);
		}
		
		@Override
		public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		    int width = MeasureSpec.getSize(widthMeasureSpec);
		    int height = MeasureSpec.getSize(heightMeasureSpec);
		    int size = width > height ? height : width;
		    setMeasuredDimension(size, (int) (size * GameState.GALAXYHEIGHT * 1f / GameState.GALAXYWIDTH) );
		}
		
		@Override
		public void onDraw(Canvas canvas) {
			mFragment.getGameState().drawGalaxy(canvas);
					
		}

		@SuppressLint("NewApi")
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			switch (event.getActionMasked()) {	// NB getActionMasked() required API 8
			case MotionEvent.ACTION_MOVE:
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_UP:
				break;
			default:
				return false;
			}
			
			mFragment.getGameState().galacticChartFormHandleEvent(event.getX(), event.getY(), event.getActionMasked());
			return true;
		}
		
	}
	
	@Override
	public int getHelpTextResId() {
		return R.string.help_galacticchart;
	}

	@Override
	public ScreenType getType() {
		return ScreenType.CHART;
	}
	
}
