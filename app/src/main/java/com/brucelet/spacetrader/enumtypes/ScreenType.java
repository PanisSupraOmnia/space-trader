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

import android.content.res.Resources;

import com.brucelet.spacetrader.BankScreen;
import com.brucelet.spacetrader.BaseScreen;
import com.brucelet.spacetrader.BuyEqScreen;
import com.brucelet.spacetrader.BuyScreen;
import com.brucelet.spacetrader.BuyShipScreen;
import com.brucelet.spacetrader.ChartScreen;
import com.brucelet.spacetrader.EncounterScreen;
import com.brucelet.spacetrader.EndOfGameScreen;
import com.brucelet.spacetrader.InfoScreen;
import com.brucelet.spacetrader.PersonnelScreen;
import com.brucelet.spacetrader.R;
import com.brucelet.spacetrader.SellEqScreen;
import com.brucelet.spacetrader.SellScreen;
import com.brucelet.spacetrader.StatusCargoScreen;
import com.brucelet.spacetrader.StatusQuestsScreen;
import com.brucelet.spacetrader.StatusScreen;
import com.brucelet.spacetrader.StatusShipScreen;
import com.brucelet.spacetrader.TitleScreen;
import com.brucelet.spacetrader.WarpPricesScreen;
import com.brucelet.spacetrader.WarpScreen;
import com.brucelet.spacetrader.WarpTargetScreen;
import com.brucelet.spacetrader.YardScreen;

public enum ScreenType implements XmlString {
	BUY       ( R.id.screen_buy,            R.string.screen_buy,            R.string.shortcut_spinner_buy,       R.string.shortcut_buy,       BuyScreen.class,          true,  true  ),
	SELL      ( R.id.screen_sell,           R.string.screen_sell,           R.string.shortcut_spinner_sell,      R.string.shortcut_sell,      SellScreen.class,         true,  true  ),
	YARD      ( R.id.screen_yard,           R.string.screen_yard,           R.string.shortcut_spinner_yard,      R.string.shortcut_yard,      YardScreen.class,         true,  true  ),
	BUYEQ     ( R.id.screen_buyeq,          R.string.screen_buyeq,          R.string.shortcut_spinner_buyeq,     R.string.shortcut_buyeq,     BuyEqScreen.class,        true,  true  ),
	SELLEQ    ( R.id.screen_selleq,         R.string.screen_selleq,         R.string.shortcut_spinner_selleq,    R.string.shortcut_selleq,    SellEqScreen.class,       true,  true  ),
	PERSONNEL ( R.id.screen_personnel,      R.string.screen_personnel,      R.string.shortcut_spinner_personnel, R.string.shortcut_personnel, PersonnelScreen.class,    true,  true  ),
	BANK      ( R.id.screen_bank,           R.string.screen_bank,           R.string.shortcut_spinner_bank,      R.string.shortcut_bank,      BankScreen.class,         true,  true  ),
	INFO      ( R.id.screen_info,           R.string.screen_info,           R.string.shortcut_spinner_info,      R.string.shortcut_info,      InfoScreen.class,         true,  true  ),
	STATUS    ( R.id.screen_status,         R.string.screen_status,         R.string.shortcut_spinner_status,    R.string.shortcut_status,    StatusScreen.class,       true,  true  ),
	CHART     ( R.id.screen_chart,          R.string.screen_chart,          R.string.shortcut_spinner_chart,     R.string.shortcut_chart,     ChartScreen.class,        true,  true  ),
	WARP      ( R.id.screen_warp,           R.string.screen_warp,           R.string.shortcut_spinner_warp,      R.string.shortcut_warp,      WarpScreen.class,         true,  true  ),
	// The screens below do not appear in the dropdown menu.
	BUYSHIP   ( R.id.screen_yard_buyship,   R.string.screen_yard_buyship,   -1,                                  -1,                          BuyShipScreen.class,      false, true  ),
	QUESTS    ( R.id.screen_status_quests,  R.string.screen_status_quests,  -1,                                  -1,                          StatusQuestsScreen.class, false, true  ),
	SHIP      ( R.id.screen_status_ship,    R.string.screen_status_ship,    -1,                                  -1,                          StatusShipScreen.class,   false, true  ),
	CARGO     ( R.id.screen_status_cargo,   R.string.screen_status_cargo,   -1,                                  -1,                          StatusCargoScreen.class,  false, true  ),
	TARGET    ( R.id.screen_warp_target,    R.string.screen_warp_target,    -1,                                  -1,                          WarpTargetScreen.class,   false, true  ),
	AVGPRICES ( R.id.screen_warp_avgprices, R.string.screen_warp_avgprices, -1,                                  -1,                          WarpPricesScreen.class,   false, true  ),
	// TODO? The screens below will be removed if their corresponding ui elements are spun off into their own activities.
	TITLE     (R.id.screen_title,           R.string.screen_title,          -2,                                  -2,                          TitleScreen.class,        false, false ),
	ENDGAME   (R.id.screen_endofgame,       R.string.screen_endofgame,      -2,                                  -2,                          EndOfGameScreen.class,    false, false ),
	ENCOUNTER (R.id.screen_encounter,       R.string.screen_encounter,      -2,                                  -2,                          EncounterScreen.class,    false, false ),
	;

	public static ScreenType[] dropdownValues() {
		return new ScreenType[] { BUY, SELL, YARD, BUYEQ, SELLEQ, PERSONNEL, BANK, INFO, STATUS, CHART, WARP, };
	}
	public static ScreenType[] dockedValues() {
		return new ScreenType[] { BUY, SELL, YARD, BUYEQ, SELLEQ, PERSONNEL, BANK, INFO, STATUS, CHART, WARP, BUYSHIP, QUESTS, SHIP, CARGO, TARGET, AVGPRICES, };
	}
	
	public final int fragmentId;
	public final int titleId;
	public final Class<? extends BaseScreen> screenClass;
	public final int shortcutId;
	public final int spinnerId;
	public final boolean dropdown;
	public final boolean docked;
	ScreenType(int fragmentId, int titleId, int spinnerId, int shortcutId, Class<? extends BaseScreen> screenClass, boolean dropdown, boolean docked) {
		this.fragmentId = fragmentId;
		this.titleId = titleId;
		this.spinnerId = spinnerId;
		this.shortcutId = shortcutId;
		this.screenClass = screenClass;
		this.dropdown = dropdown;
		this.docked = docked;
	}
	
	@Override
	public String toXmlString(Resources res) {
		return res.getString(titleId);
	}
	
}
