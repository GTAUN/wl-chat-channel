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

import net.gtaun.util.event.AbstractEventHandler;

/**
 * 聊天频道消息事件处理者抽象类。
 * 
 * @author MK124
 */
public abstract class ChatChannelEventHandler extends AbstractEventHandler
{
	public ChatChannelEventHandler()
	{
		super(ChatChannelEventHandler.class);
	}
	
	protected void onChannelCreate(ChatChannelCreateEvent event)
	{
		
	}
	
	protected void onChannelDestroy(ChatChannelDestroyEvent event)
	{
		
	}
	
	protected void onChannelPlayerJoin(ChatChannelPlayerJoinEvent event)
	{
		
	}
	
	protected void onChannelPlayerLeave(ChatChannelPlayerLeaveEvent event)
	{
		
	}
	
	protected void onChannelPlayerChat(ChatChannelPlayerChatEvent event)
	{
		
	}
	
	protected void onChannelMessage(ChatChannelMessageEvent event)
	{
		
	}
}
