package com.wolfeiii.agoniaguilds.service;

import com.wolfeiii.agoniaguilds.AgoniaGuilds;
import com.wolfeiii.agoniaguilds.configuration.bossbar.BossBarsService;
import com.wolfeiii.agoniaguilds.configuration.message.Message;
import com.wolfeiii.agoniaguilds.configuration.message.MessageService;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;

public class ServicesHandler {

    private final AgoniaGuilds plugin;

    private BossBarsService bossBarsService;
    private MessageService messageService;

    public ServicesHandler(AgoniaGuilds plugin) {
        this.plugin = plugin;
    }

    public void registerBossBarsService(BossBarsService bossBarsService) {
        this.bossBarsService = bossBarsService;
        Bukkit.getServicesManager().register(BossBarsService.class, bossBarsService, plugin, ServicePriority.Normal);
    }

    public void registerMessagesService(MessageService messageService) {
        this.messageService = messageService;
        Bukkit.getServicesManager().register(MessageService.class, messageService, plugin, ServicePriority.Normal);
    }

    public BossBarsService getBossBarsService() {
        return bossBarsService;
    }

    public MessageService getMessageService() {
        return messageService;
    }
}
