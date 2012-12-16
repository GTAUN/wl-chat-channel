/**
 * Copyright (C) 2012 MK124
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package net.gtaun.wl.chat;

import java.util.Collection;

import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.shoebill.object.Player;

/**
 * 聊天频道接口。
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

	Collection<Player> getMembers();
	
	boolean join(Player player);
	boolean leave(Player player);

	void chat(Player player, String text);
	void message(String text);
}
