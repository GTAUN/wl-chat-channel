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
