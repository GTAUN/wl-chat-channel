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

import net.gtaun.util.event.Event;
import net.gtaun.wl.chat.ChatChannel;

/**
 * 聊天频道事件抽象类。
 * 
 * @author MK124
 */
public abstract class ChatChannelEvent extends Event
{
	protected final ChatChannel channel;
	
	
	protected ChatChannelEvent(ChatChannel channel)
	{
		this.channel = channel;
	}
	
	public ChatChannel getChannel()
	{
		return channel;
	}
}
