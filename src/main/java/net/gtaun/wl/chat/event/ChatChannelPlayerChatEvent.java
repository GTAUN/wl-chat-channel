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
