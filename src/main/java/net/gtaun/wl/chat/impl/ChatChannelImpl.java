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

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.event.PlayerEventHandler;
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.ManagedEventManager;
import net.gtaun.util.event.EventManager.HandlerPriority;
import net.gtaun.wl.chat.ChatChannel;
import net.gtaun.wl.chat.ChatChannelService;
import net.gtaun.wl.chat.event.ChatChannelCreateEvent;
import net.gtaun.wl.chat.event.ChatChannelDestroyEvent;
import net.gtaun.wl.chat.event.ChatChannelPlayerLeaveEvent;
import net.gtaun.wl.chat.event.ChatChannelPlayerJoinEvent;
import net.gtaun.wl.chat.event.ChatChannelMessageEvent;
import net.gtaun.wl.chat.event.ChatChannelPlayerChatEvent;

/**
 * 聊天频道实现类。
 * 
 * @author MK124
 */
public class ChatChannelImpl implements ChatChannel
{
	private final ChatChannelService service;
	private final ManagedEventManager eventManager;
	private final List<Player> members;
	
	private boolean isDestroyed;
	private String name;
	private String prefixFormat = "[" + FORMAT_CHANNEL_NAME + "] ";
	private String playerMessageFormat = FORMAT_PLAYER_COLOR + FORMAT_PLAYER_NAME+ ": " + FORMAT_CHANNEL_COLOR + FORMAT_PLAYER_TEXT;
	private Color color = Color.WHITE;
	
	
	public ChatChannelImpl(String name, ChatChannelService service, EventManager rootEventManager)
	{
		this.name = name;
		this.service = service;
		
		eventManager = new ManagedEventManager(rootEventManager);
		members = new LinkedList<>();
		
		eventManager.registerHandler(PlayerDisconnectEvent.class, playerEventHandler, HandlerPriority.BOTTOM);
		
		ChatChannelCreateEvent event = new ChatChannelCreateEvent(this);
		eventManager.dispatchEvent(event, service);
	}
	
	@Override
	public void destroy()
	{
		ChatChannelDestroyEvent event = new ChatChannelDestroyEvent(this);
		eventManager.dispatchEvent(event, service, this);
		
		eventManager.cancelAll();
		members.clear();
		
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
	public Collection<Player> getMembers()
	{
		return new AbstractCollection<Player>()
		{
			@Override
			public boolean add(Player e)
			{
				return join(e);
			}
			
			@Override
			public Iterator<Player> iterator()
			{
				return new Iterator<Player>()
				{
					private Iterator<Player> iterator = members.iterator();
					private Player current;
					
					@Override
					public boolean hasNext()
					{
						return iterator.hasNext();
					}

					@Override
					public Player next()
					{
						current = iterator.next();
						return current;
					}

					@Override
					public void remove()
					{
						iterator.remove();
						
						if (current != null)
						{
							dispatchLeaveEvent(current);
							current = null;
						}
					}
				};
			}

			@Override
			public int size()
			{
				return members.size();
			}
		};
	}
	
	@Override
	public boolean join(Player player)
	{
		if (isDestroyed) return false;
		if (members.contains(player)) return false;
		
		members.add(player);
		ChatChannelPlayerJoinEvent event = new ChatChannelPlayerJoinEvent(this, player);
		eventManager.dispatchEvent(event, this, player);
		
		return true;
	}
	
	@Override
	public boolean leave(Player player)
	{
		if (isDestroyed) return false;
		if (members.remove(player) == false) return false;
		dispatchLeaveEvent(player);
		return true;
	}
	
	private void dispatchLeaveEvent(Player player)
	{
		ChatChannelPlayerLeaveEvent event = new ChatChannelPlayerLeaveEvent(this, player);
		eventManager.dispatchEvent(event, this, player);
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
	
	private PlayerEventHandler playerEventHandler = new PlayerEventHandler()
	{
		public void onPlayerDisconnect(PlayerDisconnectEvent event)
		{
			
		}
	};
}
