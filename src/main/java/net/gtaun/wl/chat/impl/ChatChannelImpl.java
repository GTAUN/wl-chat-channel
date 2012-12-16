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

package net.gtaun.wl.chat.impl;

import java.util.LinkedList;
import java.util.List;

import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.object.Player;
import net.gtaun.wl.chat.ChatChannel;

/**
 * 聊天频道实现类。
 * 
 * @author MK124
 */
public class ChatChannelImpl implements ChatChannel
{
	private final List<Player> members;
	
	private boolean isDestroyed;
	private String name;
	private String format = "[" + FORMAT_CHANNEL_NAME + "] " + FORMAT_PLAYER_COLOR + FORMAT_PLAYER_NAME+ ": " + FORMAT_CHANNEL_COLOR + FORMAT_PLAYER_TEXT;
	private Color color = Color.WHITE;
	
	
	public ChatChannelImpl(String name)
	{
		members = new LinkedList<>();
		this.name = name;
	}
	
	@Override
	public void destroy()
	{
		isDestroyed = true;
	}
	
	@Override
	public boolean isDestroyed()
	{
		return isDestroyed;
	}
	
	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String getFormat()
	{
		return format;
	}

	@Override
	public void setFormat(String format)
	{
		this.format = format;
	}
	
	@Override
	public void join(Player player)
	{
		if (isDestroyed) return;
		members.add(player);
	}
	
	@Override
	public void leave(Player player)
	{
		if (isDestroyed) return;
		members.remove(player);
	}
	
	@Override
	public void chat(Player player, String text)
	{
		if (isDestroyed) return;
		
		String message = format;
		message = message.replace(FORMAT_CHANNEL_NAME, name);
		message = message.replace(FORMAT_CHANNEL_COLOR, color.toEmbeddingString());
		message = message.replace(FORMAT_PLAYER_NAME, player.getName());
		message = message.replace(FORMAT_PLAYER_TEXT, text);
		message = message.replace(FORMAT_PLAYER_COLOR, player.getColor().toEmbeddingString());
		
		for (Player member : members)
		{
			member.sendMessage(Color.WHITE, message);
		}
	}
}
