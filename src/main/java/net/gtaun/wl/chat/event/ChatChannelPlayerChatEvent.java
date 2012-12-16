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

package net.gtaun.wl.chat.event;

import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.Interruptable;
import net.gtaun.wl.chat.ChatChannel;

/**
 * 聊天频道玩家消息事件。
 * 
 * @author MK124
 */
public class ChatChannelPlayerChatEvent extends ChatChannelPlayerEvent implements Interruptable
{
	private String text;
	private boolean isCanceled;
	
	
	public ChatChannelPlayerChatEvent(ChatChannel channel, Player player, String text)
	{
		super(channel, player);
		this.text = text;
	}
	
	@Override
	public void interrupt()
	{
		super.interrupt();
	}
	
	public void cancel()
	{
		isCanceled = true;
		interrupt();
	}
	
	public boolean isCanceled()
	{
		return isCanceled;
	}
	
	public String getText()
	{
		return text;
	}
	
	public void setText(String text)
	{
		this.text = text;
	}
}
