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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.gtaun.shoebill.common.AbstractShoebillContext;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Server;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.HandlerPriority;
import net.gtaun.wl.chat.ChatChannel;
import net.gtaun.wl.chat.ChatChannelService;
import net.gtaun.wl.chat.event.ChatChannelCreateEvent;
import net.gtaun.wl.chat.event.ChatChannelDestroyEvent;
import net.gtaun.wl.chat.event.ChatChannelMessageEvent;
import net.gtaun.wl.chat.event.ChatChannelPlayerChatEvent;
import net.gtaun.wl.chat.event.ChatChannelPlayerJoinEvent;
import net.gtaun.wl.chat.event.ChatChannelPlayerLeaveEvent;

/**
 * 
 * @author MK124
 */
public class ChatChannelImpl extends AbstractShoebillContext implements ChatChannel
{
	private final ChatChannelService service;
	private final List<Player> members;
	
	private String name;
	private String prefixFormat = "[" + FORMAT_CHANNEL_NAME + "] ";
	private String playerMessageFormat = FORMAT_PLAYER_COLOR + FORMAT_PLAYER_NAME+ ": " + FORMAT_CHANNEL_COLOR + FORMAT_PLAYER_TEXT;
	private Color color = Color.WHITE;
	
	
	public ChatChannelImpl(String name, ChatChannelService service, EventManager rootEventManager)
	{
		super(rootEventManager);
		this.name = name;
		this.service = service;

		members = new LinkedList<>();
		init();
	}
	
	@Override
	protected void onInit()
	{
		eventManagerNode.registerHandler(PlayerDisconnectEvent.class, HandlerPriority.BOTTOM, (e) ->
		{
			Player player = e.getPlayer();
			if (!isMember(player)) return;
			leave(player);
		});
		
		ChatChannelCreateEvent event = new ChatChannelCreateEvent(this);
		eventManagerNode.dispatchEvent(event, service);
	}
	
	@Override
	public void onDestroy()
	{
		ChatChannelDestroyEvent event = new ChatChannelDestroyEvent(this);
		eventManagerNode.dispatchEvent(event, service, this);
		
		members.clear();
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
	public boolean isMember(Player player)
	{
		return members.contains(player);
	}
	
	@Override
	public List<Player> getMembers()
	{
		return Collections.unmodifiableList(members);
	}
	
	@Override
	public boolean join(Player player)
	{
		if (isDestroyed()) return false;
		if (members.contains(player)) return false;
		
		members.add(player);
		ChatChannelPlayerJoinEvent event = new ChatChannelPlayerJoinEvent(this, player);
		eventManagerNode.dispatchEvent(event, this, player);
		
		return true;
	}
	
	@Override
	public boolean leave(Player player)
	{
		if (isDestroyed()) return false;
		if (!members.remove(player)) return false;
		dispatchLeaveEvent(player);
		return true;
	}
	
	private void dispatchLeaveEvent(Player player)
	{
		ChatChannelPlayerLeaveEvent event = new ChatChannelPlayerLeaveEvent(this, player);
		eventManagerNode.dispatchEvent(event, this, player);
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
		if (isDestroyed()) return;
		
		ChatChannelPlayerChatEvent chatEvent = new ChatChannelPlayerChatEvent(this, player, text);
		eventManagerNode.dispatchEvent(chatEvent, player, this);
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
		eventManagerNode.dispatchEvent(messageEvent, this);
		if (messageEvent.isCanceled()) return;
		
		message = messageEvent.getMessage();
		Server.get().sendMessageToAll(Color.WHITE, message);
		
		for (Player member : members)
		{
			member.sendMessage(Color.WHITE, message);
		}
	}
}
