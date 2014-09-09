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

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brucelet.spacetrader.datatypes.GameState;
import com.brucelet.spacetrader.enumtypes.XmlString;

public abstract class BaseDialog extends DialogFragment implements ConvenienceMethods, OnClickListener {
//	private Button mNegative;
//	private Button mNeutral;
//	private Button mPositive;
//	
//	protected final Button getButton(int which) {
//		Button b;
//		switch (which) {
//		case AlertDialog.BUTTON_POSITIVE:
//			b = mPositive;
//			break;
//		case AlertDialog.BUTTON_NEUTRAL:
//			b = mNeutral;
//			break;
//		case AlertDialog.BUTTON_NEGATIVE:
//			b = mNegative;
//			break;
//		default:
//			throw new IllegalArgumentException("Illegal button requested");
//		}
//		if (b == null) throw new NullPointerException("Null button requested.");
//		
//		return b;
//	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set STYLE_NO_FRAME so that we can manually add consistent holo dialog backgrounds
		setStyle(STYLE_NO_FRAME, 0);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {		
		View root = inflater.inflate(R.layout.dialog_layout, container, false);
		
		
		TypedValue tv = new TypedValue();
		TypedArray ta;

		View topPanel = root.findViewById(R.id.topPanel);
		View contentPanel = root.findViewById(R.id.contentPanel);
		View buttonPanel = root.findViewById(R.id.buttonPanel);

		// Manually setting some things  from resources since we're not using real AlertDialogs
		getActivity().getTheme().resolveAttribute(R.attr.alertDialogStyle, tv, true);
		ta = getActivity().getTheme().obtainStyledAttributes(tv.resourceId, new int[] {android.R.attr.topBright, android.R.attr.centerBright, android.R.attr.bottomBright, android.R.attr.divider});
		topPanel.setBackgroundResource(ta.getResourceId(0, 0));
		contentPanel.setBackgroundResource(ta.getResourceId(1, 0));
		buttonPanel.setBackgroundResource(ta.getResourceId(2, 0));

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			((LinearLayout)buttonPanel).setDividerDrawable(getResources().getDrawable(ta.getResourceId(3,0)));
			((LinearLayout)buttonPanel.findViewById(R.id.buttons)).setDividerDrawable(getResources().getDrawable(ta.getResourceId(3,0)));
		}
		
		ViewGroup content = (ViewGroup) contentPanel.findViewById(R.id.content);
		View message = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_message, content, false);
		
		Builder builder = new Builder(getActivity());
		builder.setView(message);
		
		onBuildDialog(builder);
		((TextView)message.findViewById(R.id.dialog_message)).setText(builder.mMessage);
		
		// Title panel.
		((TextView)root.findViewById(R.id.alertTitle)).setText(builder.mTitle);
		
		// Content panel. This might be message or custom view.
		((FrameLayout)root.findViewById(R.id.content)).addView(builder.mView);

		// Button panel
		Button pos = (Button) root.findViewById(R.id.button1);
		Button neg = (Button) root.findViewById(R.id.button2);
		Button neu = (Button) root.findViewById(R.id.button3);
		if (builder.mPositiveButton != null && builder.mPositiveButton.length() > 0) {
			pos.setVisibility(View.VISIBLE);
			pos.setText(builder.mPositiveButton);
		}
		if (builder.mNegativeButton != null && builder.mNegativeButton.length() > 0) {
			neg.setVisibility(View.VISIBLE);
			neg.setText(builder.mNegativeButton);
		}
		if (builder.mNeutralButton != null && builder.mNeutralButton.length() > 0) {
			neu.setVisibility(View.VISIBLE);
			neu.setText(builder.mNeutralButton);
		}
//		builder.create(root);
		
		View infoButton = root.findViewById(R.id.alertHelp);
		if (getHelpTextResId() > 0) {
			infoButton.setVisibility(View.VISIBLE);
			infoButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (getGameManager().isClicking()) return;
					
					getGameManager().startClick();
					onClickHelpButton();
					getGameManager().finishClick();
				}
			});
		} else {
			infoButton.setVisibility(View.GONE);
		}
		
		pos.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (getGameManager().isClicking()) return;
				
				getGameManager().startClick();
				onClickPositiveButton();
				getGameManager().finishClick();
			}
		});

		neu.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (getGameManager().isClicking()) return;
				
				getGameManager().startClick();
				onClickNeutralButton();
				getGameManager().finishClick();
			}
		});

		neg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (getGameManager().isClicking()) return;
				
				getGameManager().startClick();
				onClickNegativeButton();
				getGameManager().finishClick();
			}
		});

		return root;
	}

	@Override
	public void onAttach(Activity activity) {
		if (!(activity instanceof MainActivity)) {
			throw new IllegalArgumentException(BaseDialog.class.getSimpleName()+" Fragment must attach to spacetrader "+MainActivity.class.getSimpleName()+" class");
		}
		super.onAttach(activity);
	}
	
	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);			// Must call super here for proper backstack management. This was a really annoying bug.
		
		if (getGameManager() != null) getGameManager().showQueuedDialog();
	}	
	
	@Override
	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
		onClickNegativeButton(); // NB This will protect against lockups due to dialog dismissal in asynctasks as long as dialogs all use negative button for dismissal/unlock
	}
	
	@Override
	public void onResume() {
		super.onResume();
		onRefreshDialog();
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.setOnShowListener(new DialogInterface.OnShowListener() { // NB OnShowListener requires API 8
			
			@Override
			public void onShow(DialogInterface dialog) {
				android.util.Log.d("onShow()", "Dialog "+this+" is showing.");
				getGameManager().reportDialogShown();
			}
		});
		
		// STYLE_NO_FRAME removes animation style so add it back in
		Window window = dialog.getWindow();
		window.getAttributes().windowAnimations = android.R.style.Animation_Dialog;
		
		// Also missing background dimming since not using standard AlertDialog so throw that up too
		window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		window.getAttributes().dimAmount = 0.6f; // NB Just hardcoding this right now although it might be technically more correct to grab it from style.
		
		return dialog;
	}
	
//	@Override
//	public final Dialog onCreateDialog(Bundle savedInstanceState) {
//		android.util.Log.d("BaseDialog", "Creating dialog "+this.getClass().getName());
//
////		Builder builder = new Builder(new ContextThemeWrapper(getActivity(), R.style.DialogTheme));
//		Builder builder = new Builder(getActivity());
//		
//		View message = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_message, null);
//		builder.setView(message);
//		
//		onBuildDialog(builder);
//
//		View title = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_title, null);
//		((TextView)title.findViewById(R.id.alertTitle)).setText(builder.mTitle);
//		View infoButton = title.findViewById(R.id.alertHelp);
//		if (getHelpTextResId() > 0) {
//			infoButton.setVisibility(View.VISIBLE);
//			infoButton.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					onClickHelpButton();
//				}
//			});
//		} else {
//			infoButton.setVisibility(View.GONE);
//		}
//		builder.setCustomTitle(title);
//		
//		((TextView)message.findViewById(R.id.dialog_message)).setText(builder.mMessage);
//		
//		final AlertDialog d = builder.create();
//		
//		// Set new onClickListeners for dialog buttons so they don't auto-dismiss
//		d.setOnShowListener(new DialogInterface.OnShowListener() {	// NB OnShowListener requires API 8
//
//			@Override
//			public void onShow(DialogInterface dialog) {
//				android.util.Log.d("onShow()", "Dialog "+this+" is showing.");
//				getGameManager().reportDialogShown();
//				
//				Button mPositive = d.getButton(AlertDialog.BUTTON_POSITIVE);
//				mPositive.setOnClickListener(new View.OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						if (getGameManager().isClicking()) return;
//						
//						getGameManager().startClick();
//						onClickPositiveButton();
//						getGameManager().finishClick();
//					}
//				});
//
//				Button mNeutral = d.getButton(AlertDialog.BUTTON_NEUTRAL);
//				mNeutral.setOnClickListener(new View.OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						if (getGameManager().isClicking()) return;
//						
//						getGameManager().startClick();
//						onClickNeutralButton();
//						getGameManager().finishClick();
//					}
//				});
//
//				Button mNegative = d.getButton(AlertDialog.BUTTON_NEGATIVE);
//				mNegative.setOnClickListener(new View.OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						if (getGameManager().isClicking()) return;
//						
//						getGameManager().startClick();
//						onClickNegativeButton();
//						getGameManager().finishClick();
//					}
//				});
//
//			}
//		});
//		d.setCancelable(true);
//		return d;
//	}

	@Override
	public MainActivity getGameManager() {
		return ((MainActivity) getActivity());
	}

	@Override
	public GameState getGameState() {
		return ((MainActivity) getActivity()).getGameState();
	}


	@Override
	public void setViewTextById(int viewId, int textId) {
		((TextView) getDialog().findViewById(viewId)).setText(textId);
	}


	@Override
	public void setViewTextById(int viewId, int textId, Object... args) {
		
		// A bit of processing so we can drop XmlString enums as format arguments directly without needing to call toXmlString()
		for (int i = 0; i < args.length; i++) {
			if (args[i] instanceof XmlString) {
				args[i] = ((XmlString) args[i]).toXmlString(getResources());
			}
		}
		
		((TextView) getDialog().findViewById(viewId)).setText(getResources().getString(textId, args));
	}
	

	@Override
	public void setViewTextById(int viewId, CharSequence text) {
		((TextView) getDialog().findViewById(viewId)).setText(text);
	}
	

	@Override
	public void setViewTextById(int viewId, XmlString item) {
		((TextView) getDialog().findViewById(viewId)).setText(item.toXmlString(getResources()));		
	}
	

	@Override
	public void setViewVisibilityById(int viewId, boolean visOrInvis, boolean invisOrGone) {
		getDialog().findViewById(viewId).setVisibility(visOrInvis? View.VISIBLE : invisOrGone? View.INVISIBLE : View.GONE);
	}	
	

	@Override
	public void setViewVisibilityById(int viewId, boolean visOrInvis) {
		setViewVisibilityById(viewId, visOrInvis, true);
	}
	
	
	@Override
	public final void onClick(View view) {
		if (!(this instanceof OnSingleClickListener)) return;
		if (getGameManager().isClicking()) return;
		
		getGameManager().startClick();
		((OnSingleClickListener)this).onSingleClick(view);
		getGameManager().finishClick();
	}
	
	public final void onClickHelpButton() {
		getGameManager().showDialogFragment(HelpDialog.newInstance(getHelpTextResId()));
	}

	// Hooks to be used by derived classes
	public void onRefreshDialog() {}
	public void onBuildDialog(Builder builder) {}
	public void onClickPositiveButton() { dismiss(); }
	public void onClickNeutralButton() { dismiss(); }
	public void onClickNegativeButton() { dismiss(); }

	public abstract int getHelpTextResId();

//	/**
//	 * Builder class used in {@link BaseDialog#onBuildDialog(Builder)}.
//	 * This mostly wraps around {@link AlertDialog.Builder}, but it strips
//	 * the {@link DialogInterface.OnClickListener} handling because we set
//	 * our click listeners separately in this class. This is to counter the
//	 * fact that by default, AlertDialogs always dismiss themselves when a
//	 * button is pressed, which we may not want to happen.
//	 */
//	public static class Builder {
//		private final AlertDialog.Builder mBuilder;
//		private final Context mContext;	// XXX should dereference at some point? Or is it ok because Builder is short-lived?
//		private CharSequence mMessage;
//		private CharSequence mTitle;
//		
//		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
//		private Builder(Context context) {
//			mContext = context;
//			
//			if (mContext instanceof MainActivity) {
//				MainActivity activity = (MainActivity) mContext;
//
//				// We should be able to read the AppCompat attribute R.attr.isLightTheme here, but for some reason this doesn't always work, so it's now a member variable of ThemeType.
//				ThemeType theme = activity.getThemeType();
//				
//				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//					mBuilder = new AlertDialog.Builder(activity);
//				} else {
//					mBuilder = new AlertDialog.Builder(activity);
//					
//					// Pre-holo dialogs need to explicity set light/dark backgrounds depending on theme.
//					mBuilder.setInverseBackgroundForced(theme.isLightTheme);
//				}
//			} else {
//				mBuilder = new AlertDialog.Builder(mContext);
//			}
//		}
//		
//		public Builder setPositiveButton(int textId) {
//			mBuilder.setPositiveButton(textId, null);
//			return this;
//		}
//		
//		public Builder setPositiveButton(CharSequence text) {
//			mBuilder.setPositiveButton(text, null);
//			return this;
//		}
//		
//		public Builder setNeutralButton(int textId) {
//			mBuilder.setNeutralButton(textId, null);
//			return this;
//		}
//		
//		public Builder setNeutralButton(CharSequence text) {
//			mBuilder.setNeutralButton(text, null);
//			return this;
//		}
//		
//		public Builder setNegativeButton(int textId) {
//			mBuilder.setNegativeButton(textId, null);
//			return this;
//		}
//		
//		public Builder setNegativeButton(CharSequence text) {
//			mBuilder.setNegativeButton(text, null);
//			return this;
//		}
//
//		public Builder setTitle(int titleId) {
////			mBuilder.setTitle(titleId);
//			mTitle = mContext.getResources().getString(titleId);
//			return this;
//		}
//
//		public Builder setTitle(int titleId, Object... args) {
//
//			// A bit of processing so we can drop XmlString enums as format arguments directly without needing to call toXmlString()
//			for (int i = 0; i < args.length; i++) {
//				if (args[i] instanceof XmlString) {
//					args[i] = ((XmlString) args[i]).toXmlString(mContext.getResources());
//				}
//			}
////			mBuilder.setTitle(mContext.getResources().getString(titleId, args));
//			mTitle = mContext.getResources().getString(titleId, args);
//			return this;
//		}
//		
//		public Builder setTitle(CharSequence title) {
////			mBuilder.setTitle(title);
//			mTitle = title;
//			return this;
//		}
//		
//		public Builder setView(View view) {
//			mBuilder.setView(view);
//			return this;
//		}
//		
//		public Builder setView(int viewId) {
//			View view = LayoutInflater.from(mContext).inflate(viewId, null);
//			mBuilder.setView(view);
//			return this;
//		}
//		
//		public Builder setMessage(int messageId) {
////			mBuilder.setMessage(messageId);
//			mMessage = mContext.getResources().getString(messageId);
//			return this;
//		}
//		
//		public Builder setMessage(int messageId, Object... args) {
//			
//			// A bit of processing so we can drop XmlString enums as format arguments directly without needing to call toXmlString()
//			for (int i = 0; i < args.length; i++) {
//				if (args[i] instanceof XmlString) {
//					args[i] = ((XmlString) args[i]).toXmlString(mContext.getResources());
//				}
//			}
////			mBuilder.setMessage(mContext.getResources().getString(messageId, args));
//			mMessage = mContext.getResources().getString(messageId, args);
//			return this;
//		}
//		
//		public Builder setMessage(CharSequence message) {
////			mBuilder.setMessage(message);
//			mMessage = message;
//			return this;
//		}
//		
//		private AlertDialog create() {
////			mBuilder.setMessage(mMessage);
//			return mBuilder.create();
//		}
//		
//		private Builder setCustomTitle(View customTitleView) {
//			mBuilder.setCustomTitle(customTitleView);
//			return this;
//		}
//		
//	}
	
	public static class Builder {
		private final Context mContext;	// XXX should dereference at some point? Or is it ok because Builder is short-lived?
		private CharSequence mMessage;
		private CharSequence mTitle;
		private CharSequence mPositiveButton;
		private CharSequence mNeutralButton;
		private CharSequence mNegativeButton;
		private View mView;
		
		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		private Builder(Context context) {
			mContext = context;
			
		}
		
		public Builder setPositiveButton(int textId) {
			mPositiveButton = mContext.getString(textId);
			return this;
		}
		
		public Builder setPositiveButton(CharSequence text) {
			mPositiveButton = text;
			return this;
		}
		
		public Builder setNeutralButton(int textId) {
			mNeutralButton = mContext.getString(textId);
			return this;
		}
		
		public Builder setNeutralButton(CharSequence text) {
			mNeutralButton = text;
			return this;
		}
		
		public Builder setNegativeButton(int textId) {
			mNegativeButton = mContext.getString(textId);
			return this;
		}
		
		public Builder setNegativeButton(CharSequence text) {
			mNegativeButton = text;
			return this;
		}

		public Builder setTitle(int titleId) {
			mTitle = mContext.getResources().getString(titleId);
			return this;
		}

		public Builder setTitle(int titleId, Object... args) {

			// A bit of processing so we can drop XmlString enums as format arguments directly without needing to call toXmlString()
			for (int i = 0; i < args.length; i++) {
				if (args[i] instanceof XmlString) {
					args[i] = ((XmlString) args[i]).toXmlString(mContext.getResources());
				}
			}
			mTitle = mContext.getResources().getString(titleId, args);
			return this;
		}
		
		public Builder setTitle(CharSequence title) {
			mTitle = title;
			return this;
		}
		
		public Builder setView(View view) {
			mView = view;
			return this;
		}
		
		public Builder setView(int viewId) {
			View view = LayoutInflater.from(mContext).inflate(viewId, null);
			mView = view;
			return this;
		}
		
		public Builder setMessage(int messageId) {
			mMessage = mContext.getResources().getString(messageId);
			return this;
		}
		
		public Builder setMessage(int messageId, Object... args) {
			
			// A bit of processing so we can drop XmlString enums as format arguments directly without needing to call toXmlString()
			for (int i = 0; i < args.length; i++) {
				if (args[i] instanceof XmlString) {
					args[i] = ((XmlString) args[i]).toXmlString(mContext.getResources());
				}
			}
			mMessage = mContext.getResources().getString(messageId, args);
			return this;
		}
		
		public Builder setMessage(CharSequence message) {
			mMessage = message;
			return this;
		}
		
//		private void create(View dialog) {
//			// Title panel.
//			((TextView)dialog.findViewById(R.id.alertTitle)).setText(mTitle);
//			
//			// Content panel. This might be message or custom view.
//			((FrameLayout)dialog.findViewById(R.id.content)).addView(mView);
//
//			// Button panel
//			if (mPositiveButton != null && mPositiveButton.length() > 0) {
//				Button pos = (Button) dialog.findViewById(R.id.button1);
//				pos.setVisibility(View.VISIBLE);
//				pos.setText(mPositiveButton);
//			}
//			if (mNegativeButton != null && mNegativeButton.length() > 0) {
//				Button neg = (Button) dialog.findViewById(R.id.button2);
//				neg.setVisibility(View.VISIBLE);
//				neg.setText(mNegativeButton);
//			}
//			if (mNeutralButton != null && mNeutralButton.length() > 0) {
//				Button neu = (Button) dialog.findViewById(R.id.button3);
//				neu.setVisibility(View.VISIBLE);
//				neu.setText(mNeutralButton);
//			}
//		}
		
	}

}
