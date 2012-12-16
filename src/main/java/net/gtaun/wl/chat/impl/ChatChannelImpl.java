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
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.ManagedEventManager;
import net.gtaun.wl.chat.ChatChannel;
import net.gtaun.wl.chat.event.ChatChannelMessageEvent;
import net.gtaun.wl.chat.event.ChatChannelPlayerChatEvent;

/**
 * 聊天频道实现类。
 * 
 * @author MK124
 */
public class ChatChannelImpl implements ChatChannel
{
	private final ManagedEventManager eventManager;
	private final List<Player> members;
	
	private boolean isDestroyed;
	private String name;
	private String prefixFormat = "[" + FORMAT_CHANNEL_NAME + "] ";
	private String playerMessageFormat = FORMAT_PLAYER_COLOR + FORMAT_PLAYER_NAME+ ": " + FORMAT_CHANNEL_COLOR + FORMAT_PLAYER_TEXT;
	private Color color = Color.WHITE;
	
	
	public ChatChannelImpl(String name, EventManager rootEventManager)
	{
		this.name = name;
		eventManager = new ManagedEventManager(rootEventManager);
		members = new LinkedList<>();
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
	public String getPrefixFormat()
	{
		return prefixFormat;
	}

	@Override
	public void setPrefixFormat(String prefix)
	{
		this.prefixFormat = prefix;
	}
	
	@Override
	public String getPlayerMessageFormat()
	{
		return playerMessageFormat;
	}

	@Override
	public void setPlayerMessageFormat(String format)
	{
		this.playerMessageFormat = format;
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
	
	private String formatChannelMessage(String format)
	{
		String prefix = format;
		prefix = prefix.replace(FORMAT_CHANNEL_NAME, name);
		prefix = prefix.replace(FORMAT_CHANNEL_COLOR, color.toEmbeddingString());
		return prefix;
	}
	
	private String formatPlayerMessage(String format, Player player, String text)
	{
		String message = format;
		message = message.replace(FORMAT_PLAYER_NAME, player.getName());
		message = message.replace(FORMAT_PLAYER_TEXT, text);
		message = message.replace(FORMAT_PLAYER_COLOR, player.getColor().toEmbeddingString());
		return message;
	}
	
	@Override
	public void chat(Player player, String text)
	{
		if (isDestroyed) return;
		
		ChatChannelPlayerChatEvent chatEvent = new ChatChannelPlayerChatEvent(this, player, text);
		eventManager.dispatchEvent(chatEvent, player, this);
		if (chatEvent.isCanceled()) return;
		
		text = chatEvent.getText();
		
		String message = formatPlayerMessage(playerMessageFormat, player, text);
		message(message);
	}
	
	@Override
	public void message(String text)
	{
		String message = formatChannelMessage(prefixFormat + text);
		
		ChatChannelMessageEvent messageEvent = new ChatChannelMessageEvent(this, message);
		eventManager.dispatchEvent(messageEvent, this);
		if (messageEvent.isCanceled()) return;
		
		message = messageEvent.getMessage();
		
		for (Player member : members)
		{
			member.sendMessage(Color.WHITE, message);
		}
	}
}
