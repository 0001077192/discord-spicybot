package com.nsa.spicybot.commandsystem;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public interface ICommand
{
	public String getCommandName();
	public ICommand getCommandInstance();
	public String getUsage();
	public CommandResult executeCommand( MessageReceivedEvent evt, CommandArguments args );
}
