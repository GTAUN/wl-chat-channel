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

package net.gtaun.wl.chat.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.player.PlayerUtils;
import net.gtaun.shoebill.data.Color;
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
import net.gtaun.wl.chat.event.ChatChannelDestroyEvent;
import net.gtaun.wl.chat.event.ChatChannelEventHandler;

/**
 * 新未来世界聊天频道服务实现类。
 * 
 * @author MK124
 */
public class ChatChannelServiceImpl implements ChatChannelService
{
	private final Shoebill shoebill;
	
	private final ManagedEventManager eventManager;
	private final Map<String, ChatChannel> channels;
	private final Map<Player, ChatChannelPlayer> players;
	
	private ChatChannel defaultChannel;
	private boolean isCommandEnabled = true;
	private String commandOperation = "/ch";
	
	
	public ChatChannelServiceImpl(Shoebill shoebill, EventManager rootEventManager)
	{
		this.shoebill = shoebill;
		
		eventManager = new ManagedEventManager(rootEventManager);
		channels = new HashMap<>();
		players = new HashMap<>();
		
		initialize();
	}
	
	private void initialize()
	{
		for (Player player : shoebill.getSampObjectStore().getPlayers()) createChatChannelPlayer(player);
		
		eventManager.registerHandler(PlayerConnectEvent.class, playerEventHandler, HandlerPriority.MONITOR);
		eventManager.registerHandler(PlayerDisconnectEvent.class, playerEventHandler, HandlerPriority.BOTTOM);
		eventManager.registerHandler(PlayerCommandEvent.class, playerEventHandler, HandlerPriority.NORMAL);
		eventManager.registerHandler(PlayerTextEvent.class, playerEventHandler, HandlerPriority.NORMAL);
		
		eventManager.registerHandler(ChatChannelDestroyEvent.class, this, channelEventHandler, HandlerPriority.BOTTOM);
	}
	
	public void uninitialize()
	{
		eventManager.cancelAll();
		
		for (ChatChannel channel : channels.values()) channel.destroy();
		channels.clear();
		
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
		
		channel = new ChatChannelImpl(name, this, eventManager);
		channels.put(channel.getName(), channel);
		
		return channel;
	}
	
	@Override
	public void destroyChannel(ChatChannel channel)
	{
		channel.destroy();
	}
	
	@Override
	public boolean isChannelExists(String name)
	{
		return channels.containsKey(name);
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
	public ChatChannel getDefaultChannel()
	{
		return defaultChannel;
	}
	
	@Override
	public void setDefaultChannel(ChatChannel channel)
	{
		this.defaultChannel = channel;
		
		if (defaultChannel != null)
		{
			for (ChatChannelPlayer channelPlayer : players.values())
			{
				if (channelPlayer.getCurrentChannel() == null)
				{
					defaultChannel.join(channelPlayer.getPlayer());
					channelPlayer.setCurrentChannel(defaultChannel);
				}
			}
		}
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
		if (op.equals("create"))
		{
			if (args.size() < 1)
			{
				player.sendMessage(Color.YELLOW, "Usage: /ch create [name]");
				return true;
			}
			
			String name = args.poll();
			if (isChannelExists(name))
			{
				player.sendMessage(Color.YELLOW, "Channel already exists.");
				return true;
			}
			
			ChatChannel channel = createChannel(name);
			channel.join(player);
			
			player.sendMessage(Color.WHITE, "Channel \"" + name + "\" has been successfully created.");
			return true;
		}
		else if (op.equals("join"))
		{
			if (args.size() < 1)
			{
				player.sendMessage(Color.YELLOW, "Usage: /ch join [name]");
				return true;
			}
			
			String name = args.poll();
			ChatChannel channel = getChannel(name);
			if (channel == null)
			{
				player.sendMessage(Color.YELLOW, "The specified channel does not exist.");
				return true;
			}
			
			channel.join(player);
			
			player.sendMessage(Color.WHITE, "You have joined the \"" + name + "\" channel.");
			return true;
		}
		
		return false;
	}
	
	private void createChatChannelPlayer(Player player)
	{
		ChatChannelPlayer chatChannelPlayer = new ChatChannelPlayerImpl(player);
		chatChannelPlayer.setCurrentChannel(defaultChannel);
		players.put(player, chatChannelPlayer);
	}
	
	private PlayerEventHandler playerEventHandler = new PlayerEventHandler()
	{
		public void onPlayerConnect(PlayerConnectEvent event)
		{
			Player player = event.getPlayer();
			createChatChannelPlayer(player);
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
			event.disallow();
			
			Player player = event.getPlayer();
			String text = event.getText();
			ChatChannelPlayer chatChannelPlayer = getPlayer(player);
			
			char op = text.charAt(0);
			if (op == '@')
			{
				String[] splits = text.substring(1).split(" ", 2);
				if (splits.length != 2)
				{
					player.sendMessage(Color.YELLOW, "Usage: @[id/name] [message]");
					return;
				}
				
				String name = splits[0], message = splits[1];
				Player target;
				
				target = PlayerUtils.getPlayerByNameOrId(name);
				if (target == null)
				{
					player.sendMessage(Color.YELLOW, "Did not find the specified player.");
					return;
				}
				
				player.sendChat(target, message);
			}
			else if (op == '#')
			{
				String[] splits = text.substring(1).split(" ", 2);
				if (splits.length != 2)
				{
					player.sendMessage(Color.YELLOW, "Usage: #[channel] [message]");
					return;
				}
				
				String name = splits[0], message = splits[1];
				ChatChannel channel = getChannel(name);
				if (channel == null)
				{
					player.sendMessage(Color.YELLOW, "Did not find the specified channel.");
					return;
				}
				
				if (channel.isMember(player) == false)
				{
					player.sendMessage(Color.YELLOW, "You did not join this channel.");
					return;
				}
				
				chatChannelPlayer.chat(channel, message);
			}
			
			chatChannelPlayer.chat(text);
		}
	};
	
	private ChatChannelEventHandler channelEventHandler = new ChatChannelEventHandler()
	{
		protected void onChannelDestroy(ChatChannelDestroyEvent event)
		{
			ChatChannel channel = event.getChannel();
			channels.remove(channel.getName());
		}
	};
}
