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

import net.gtaun.util.event.EventManager;
import net.gtaun.wl.chat.impl.ChatChannelServiceImpl;
import net.gtaun.wl.common.WlPlugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 新未来世界聊天频道插件主类。
 * 
 * @author MK124
 */
public class ChatChannelPlugin extends WlPlugin
{
	public static final Logger LOGGER = LoggerFactory.getLogger(ChatChannelPlugin.class);
	
	
	private ChatChannelServiceImpl chatChannelService;
	
	
	public ChatChannelPlugin()
	{
		
	}
	
	@Override
	protected void onEnable() throws Throwable
	{
		EventManager eventManager = getEventManager();
		chatChannelService = new ChatChannelServiceImpl(getShoebill(), eventManager);
		registerService(ChatChannelService.class, chatChannelService);
		
		if (chatChannelService.getDefaultChannel() == null)
		{
			ChatChannel defaultChannel = chatChannelService.createChannel("Default");
			defaultChannel.setPrefixFormat("");
			
			chatChannelService.setDefaultChannel(defaultChannel);
			LOGGER.info("No default channel, create a default channel.");
		}
		
		LOGGER.info(getDescription().getName() + " " + getDescription().getVersion() + " Enabled.");
	}
	
	@Override
	protected void onDisable() throws Throwable
	{
		unregisterService(ChatChannelService.class);
		chatChannelService.uninitialize();
		chatChannelService = null;
		
		LOGGER.info(getDescription().getName() + " " + getDescription().getVersion() + " Disabled.");
	}
}
