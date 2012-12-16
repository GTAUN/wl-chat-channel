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

import net.gtaun.util.event.Interruptable;
import net.gtaun.wl.chat.ChatChannel;

/**
 * 聊天频道消息事件。
 * 
 * @author MK124
 */
public class ChatChannelMessageEvent extends ChatChannelEvent implements Interruptable
{
	private String message;
	private boolean isCanceled;
	
	
	public ChatChannelMessageEvent(ChatChannel channel, String message)
	{
		super(channel);
		this.message = message;
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
	
	public String getMessage()
	{
		return message;
	}
	
	public void setMessage(String message)
	{
		this.message = message;
	}
}
