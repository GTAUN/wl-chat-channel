/**
 * WL Chat Channel Plugin
 * Copyright (C) 2013 MK124
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.gtaun.wl.chat;

import java.util.List;

import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.shoebill.object.Player;

/**
 * 
 * @author MK124
 */
public interface ChatChannel extends Destroyable
{
	public static final String FORMAT_CHANNEL_NAME = "%CHANNEL_NAME%";
	public static final String FORMAT_CHANNEL_COLOR = "%CHANNEL_COLOR%";
	public static final String FORMAT_PLAYER_NAME = "%PLAYER_NAME%";
	public static final String FORMAT_PLAYER_TEXT = "%PLAYER_TEXT%";
	public static final String FORMAT_PLAYER_COLOR = "%PLAYER_COLOR%";
	
	
	String getName();

	String getPrefixFormat();
	void setPrefixFormat(String prefix);
	
	String getPlayerMessageFormat();
	void setPlayerMessageFormat(String format);
	
	boolean isMember(Player player);
	List<Player> getMembers();
	
	boolean join(Player player);
	boolean leave(Player player);

	void chat(Player player, String text);
	void message(String text);
}
