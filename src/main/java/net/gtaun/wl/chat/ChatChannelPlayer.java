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

package net.gtaun.wl.chat;

import net.gtaun.shoebill.object.Player;

/**
 * 新未来世界聊天频道玩家控制接口。
 * 
 * @author MK124
 */
public interface ChatChannelPlayer
{
	Player getPlayer();
	
	void setCurrentChannel(ChatChannel channel);
	ChatChannel getCurrentChannel();
	
	void chat(String text);
}
