package com.nsa.spicybot.commandsystem;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public interface IUpdateableCommand extends ICommand
{
	public boolean waitingForUpdate();
	public CommandResult updateCommand( MessageReceivedEvent evt, String data );
}
