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
package com.brucelet.spacetrader.enumtypes;

import com.brucelet.spacetrader.R;

// XXX Do I want to fill this out and use it?
public class DialogType {

	public enum Simple {
		DERP (-1,-1,-1)
		;

		public final int titleId;
		public final int messageId;
		public final int buttonId;
		public final int helpId;

		Simple(int titleId, int messageId, int helpId) {
			this.titleId = titleId;
			this.messageId = messageId;
			this.buttonId = R.string.generic_ok;
			this.helpId = helpId;
		}

		Simple(int titleId, int messageId, int buttonId, int helpId) {
			this.titleId = titleId;
			this.messageId = messageId;
			this.buttonId = buttonId;
			this.helpId = helpId;		
		}
	}	
	
	public enum Confirm {
		DERP (-1,-1,-1)
		;

		public final int titleId;
		public final int messageId;
		public final int positiveId;
		public final int negativeId;
		public final int helpId;

		Confirm(int titleId, int messageId, int helpId) {
			this.titleId = titleId;
			this.messageId = messageId;
			this.positiveId = R.string.generic_yes;
			this.negativeId = R.string.generic_no;
			this.helpId = helpId;
		}

		Confirm(int titleId, int messageId, int positiveId, int negativeId, int helpId) {
			this.titleId = titleId;
			this.messageId = messageId;
			this.positiveId = positiveId;
			this.negativeId = negativeId;
			this.helpId = helpId;		
		}
	}
	
	public enum Input {
		GETLOAN (
				R.string.screen_bank_loan_get_title,
				R.string.screen_bank_loan_get_message,
				R.string.generic_ok,
				R.string.generic_maximum,
				R.string.generic_nothing,
				R.string.help_getloan
				),

		PAYBACK (
				R.string.screen_bank_loan_get_title,
				R.string.screen_bank_loan_get_message,
				R.string.generic_ok,
				R.string.generic_maximum,
				R.string.generic_nothing,
				R.string.help_payback
				),
		
		;

		public final int titleId;
		public final int messageId;
		public final int positiveId;
		public final int neutralId;
		public final int negativeId;
		public final int helpId;

		Input(int titleId, int messageId, int positiveId, int neutralId, int negativeId, int helpId) {
			this.titleId = titleId;
			this.messageId = messageId;
			this.positiveId = positiveId;
			this.neutralId = neutralId;
			this.negativeId = negativeId;
			this.helpId = helpId;		
		}
	}
}
