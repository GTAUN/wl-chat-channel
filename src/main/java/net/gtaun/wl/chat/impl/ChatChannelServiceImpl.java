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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import net.gtaun.shoebill.event.PlayerEventHandler;
import net.gtaun.shoebill.event.player.PlayerCommandEvent;
import net.gtaun.shoebill.event.player.PlayerConnectEvent;
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent;
import net.gtaun.shoebill.event.player.PlayerTextEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManager.HandlerPriority;
import net.gtaun.util.event.ManagedEventManager;
import net.gtaun.wl.chat.ChatChannel;
import net.gtaun.wl.chat.ChatChannelPlayer;
import net.gtaun.wl.chat.ChatChannelService;
import net.gtaun.wl.chat.event.ChatChannelCreateEvent;
import net.gtaun.wl.chat.event.ChatChannelDestroyEvent;

/**
 * 新未来世界聊天频道服务实现类。
 * 
 * @author MK124
 */
public class ChatChannelServiceImpl implements ChatChannelService
{
	private final ManagedEventManager eventManager;
	private final Map<String, ChatChannel> channels;
	private final Map<Player, ChatChannelPlayer> players;
	
	private ChatChannel defaultChannel;
	private boolean isCommandEnabled = true;
	private String commandOperation = "/c";
	
	
	public ChatChannelServiceImpl(EventManager rootEventManager)
	{
		eventManager = new ManagedEventManager(rootEventManager);
		channels = new HashMap<>();
		players = new HashMap<>();
		
		initialize();
	}
	
	private void initialize()
	{
		eventManager.registerHandler(PlayerConnectEvent.class, playerEventHandler, HandlerPriority.MONITOR);
		eventManager.registerHandler(PlayerDisconnectEvent.class, playerEventHandler, HandlerPriority.BOTTOM);
		eventManager.registerHandler(PlayerCommandEvent.class, playerEventHandler, HandlerPriority.NORMAL);
		eventManager.registerHandler(PlayerTextEvent.class, playerEventHandler, HandlerPriority.NORMAL);
		
		setDefaultChannel(createChannel("Global"));
	}
	
	public void uninitialize()
	{
		eventManager.cancelAll();
		players.clear();
	}
	
	@Override
	public ChatChannelPlayer getPlayer(Player player)
	{
		return players.get(player);
	}
	
	@Override
	public ChatChannel createChannel(String name)
	{
		ChatChannel channel = getChannel(name);
		if (channel != null) return channel;
		
		channel = new ChatChannelImpl(name, eventManager);
		channels.put(channel.getName(), channel);
		
		ChatChannelCreateEvent event = new ChatChannelCreateEvent(channel);
		eventManager.dispatchEvent(event, this);
		
		return channel;
	}
	
	@Override
	public ChatChannel getChannel(String name)
	{
		return channels.get(name);
	}
	
	@Override
	public Collection<ChatChannel> getChannels()
	{
		return Collections.unmodifiableCollection(channels.values());
	}
	
	@Override
	public void destroyChannel(ChatChannel channel)
	{
		if (channel.isDestroyed()) return;
		
		ChatChannelDestroyEvent event = new ChatChannelDestroyEvent(channel);
		eventManager.dispatchEvent(event, this, channel);
		
		channels.remove(channel.getName());
		channel.destroy();
	}

	@Override
	public ChatChannel getDefaultChannel()
	{
		return defaultChannel;
	}
	
	@Override
	public void setDefaultChannel(ChatChannel channel)
	{
		this.defaultChannel = channel;
	}
	
	@Override
	public void setCommandEnabled(boolean enable)
	{
		isCommandEnabled = enable;
	}
	
	@Override
	public void setCommandOperation(String op)
	{
		commandOperation = op;
	}
	
	private boolean processPlayerCommand(Player player, String op, Queue<String> args)
	{
		return false;
	}
	
	private PlayerEventHandler playerEventHandler = new PlayerEventHandler()
	{
		public void onPlayerConnect(PlayerConnectEvent event)
		{
			Player player = event.getPlayer();
			ChatChannelPlayer chatChannelPlayer = new ChatChannelPlayerImpl(player);
			defaultChannel.join(player);
			chatChannelPlayer.setCurrentChannel(defaultChannel);
			players.put(player, chatChannelPlayer);
		}
		
		public void onPlayerDisconnect(PlayerDisconnectEvent event)
		{
			Player player = event.getPlayer();
			players.remove(player);
		}
		
		public void onPlayerCommand(PlayerCommandEvent event)
		{
			if (isCommandEnabled == false) return;
			
			Player player = event.getPlayer();
			
			String command = event.getCommand();
			String[] splits = command.split(" ", 2);
			
			String operation = splits[0].toLowerCase();
			Queue<String> args = new LinkedList<>();
			
			if (splits.length > 1)
			{
				String[] argsArray = splits[1].split(" ");
				args.addAll(Arrays.asList(argsArray));
			}
			
			if (operation.equals(commandOperation))
			{
				String op = args.poll();
				boolean ret = processPlayerCommand(player, op, args);
				if (ret) event.setProcessed();
				return;
			}
		}
		
		public void onPlayerText(PlayerTextEvent event)
		{
			Player player = event.getPlayer();
			String text = event.getText();
			ChatChannelPlayer chatChannelPlayer = getPlayer(player);
			chatChannelPlayer.chat(text);
			event.disallow();
		}
	};
}
