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

import net.gtaun.shoebill.common.AbstractShoebillContext;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.event.player.PlayerCommandEvent;
import net.gtaun.shoebill.event.player.PlayerConnectEvent;
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent;
import net.gtaun.shoebill.event.player.PlayerTextEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.Attentions;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.HandlerPriority;
import net.gtaun.wl.chat.ChatChannel;
import net.gtaun.wl.chat.ChatChannelPlayer;
import net.gtaun.wl.chat.ChatChannelService;
import net.gtaun.wl.chat.event.ChatChannelDestroyEvent;

/**
 * 
 * @author MK124
 */
public class ChatChannelServiceImpl extends AbstractShoebillContext implements ChatChannelService
{
	private final Map<String, ChatChannel> channels;
	private final Map<Player, ChatChannelPlayer> players;
	
	private ChatChannel defaultChannel;
	private boolean isCommandEnabled = true;
	private String commandOperation = "/ch";
	
	
	public ChatChannelServiceImpl(EventManager rootEventManager)
	{
		super(rootEventManager);
		
		channels = new HashMap<>();
		players = new HashMap<>();
		
		init();
	}
	
	@Override
	protected void onInit()
	{
		for (Player player : Player.get()) createChatChannelPlayer(player);
		
		eventManagerNode.registerHandler(PlayerConnectEvent.class, HandlerPriority.MONITOR, (e) ->
		{
			Player player = e.getPlayer();
			createChatChannelPlayer(player);
		});
		
		eventManagerNode.registerHandler(PlayerDisconnectEvent.class, HandlerPriority.BOTTOM, (e) ->
		{
			Player player = e.getPlayer();
			players.remove(player);
		});
		
		eventManagerNode.registerHandler(PlayerCommandEvent.class, (e) ->
		{
			if (isCommandEnabled == false) return;
			
			Player player = e.getPlayer();
			
			String command = e.getCommand();
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
				if (ret) e.setProcessed();
				return;
			}
		});
		
		eventManagerNode.registerHandler(PlayerTextEvent.class, (e) ->
		{
			e.disallow();
			
			Player player = e.getPlayer();
			String text = e.getText();
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
				
				target = Player.getByNameOrId(name);
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
		});
		
		eventManagerNode.registerHandler(ChatChannelDestroyEvent.class, HandlerPriority.BOTTOM, Attentions.create().object(this), (e) ->
		{
			ChatChannel channel = e.getChannel();
			channels.remove(channel.getName());
		});
	}
	
	@Override
	protected void onDestroy()
	{
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
		
		channel = new ChatChannelImpl(name, this, eventManagerNode);
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
}
