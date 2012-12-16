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

import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.object.Player;
import net.gtaun.wl.chat.ChatChannel;
import net.gtaun.wl.chat.ChatChannelPlayer;

/**
 * 新未来世界聊天频道玩家控制实现。
 * 
 * @author MK124
 */
public class ChatChannelPlayerImpl implements ChatChannelPlayer
{
	private final Player player;
	
	private ChatChannel currentChannel;
	
	
	public ChatChannelPlayerImpl(Player player)
	{
		this.player = player;
	}
	
	@Override
	public Player getPlayer()
	{
		return player;
	}
	
	@Override
	public ChatChannel getCurrentChannel()
	{
		return currentChannel;
	}
	
	@Override
	public void setCurrentChannel(ChatChannel channel)
	{
		this.currentChannel = channel;
	}

	@Override
	public void chat(String text)
	{
		if (currentChannel == null)
		{
			player.sendMessage(Color.RED, "You did not joined any channel, so you can not chat.");
			return;
		}
		
		currentChannel.chat(player, text);
	}
}
