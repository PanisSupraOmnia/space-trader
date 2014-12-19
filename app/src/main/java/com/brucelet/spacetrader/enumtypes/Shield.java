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

import com.brucelet.spacetrader.R;

public enum Shield implements XmlString, XmlString.Plural, Purchasable {

	ENERGY     ( R.string.shield_energy,      100,  5000, 5, 70, R.plurals.shield_energy     ),
	REFLECTIVE ( R.string.shield_reflective,  200, 20000, 6, 30, R.plurals.shield_reflective ),
	// The shields below can't be bought
	LIGHTNING  ( R.string.shield_lightning,   350, 45000, 8,  0, R.plurals.shield_lightning  ),
	;
	
	private static final Shield[] BUYABLE_VALUES = { ENERGY, REFLECTIVE };
	
	public final int resId;
	public final int power;
	public final int price;
	public final TechLevel techLevel;
	public final int chance; // Chance that this is fitted in a slot
	
	public final int pluralId;
	
	private Shield(int resId, int power, int price, int techLevel, int chance, int pluralId) {
		this.resId = resId;
		this.pluralId = pluralId;
		
		this.power = power;
		this.price = price;
		
		this.techLevel = 
				(techLevel >= 0 && techLevel < TechLevel.values().length)? TechLevel.values()[techLevel] : null;
				
		this.chance = chance;
	}
	
	@Override
	public String toXmlString(Resources res) {
		return res.getString(resId);
	}

	@Override
	public String toXmlPluralString(int amount, Resources res) {
		return res.getQuantityString(pluralId, amount, amount);
	}

	@Override
	public int buyPrice(TechLevel systemTechLevel, int traderSkill) {
		return Impl.buyPrice(price, techLevel, systemTechLevel, traderSkill);
	}

	@Override
	public int sellPrice() {
		return Purchasable.Impl.sellPrice(price);
	}

	@Override
	public TechLevel techLevel() {
		return techLevel;
	}
	
	public static Shield[] buyableValues() {
		return BUYABLE_VALUES.clone();
	}
}
