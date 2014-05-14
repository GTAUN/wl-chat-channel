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

package net.gtaun.wl.chat;

import java.util.Collection;

import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.service.Service;

/**
 * 新未来世界聊天频道服务接口。
 * 
 * @author MK124
 */
public interface ChatChannelService extends Service
{
	ChatChannelPlayer getPlayer(Player player);
	
	ChatChannel createChannel(String name);
	void destroyChannel(ChatChannel channel);
	
	boolean isChannelExists(String name);
	ChatChannel getChannel(String name);
	Collection<ChatChannel> getChannels();
	
	ChatChannel getDefaultChannel();
	void setDefaultChannel(ChatChannel channel);
	
	/**
	 * 是否开启频道系统的命令。
	 * 
	 * @param enable 是否开启
	 */
	void setCommandEnabled(boolean enable);
	
	/**
	 * 设置频道系列命令的命令头。
	 * 
	 * @param op 命令头，默认为 'ch'
	 */
	void setCommandOperation(String op);
}
