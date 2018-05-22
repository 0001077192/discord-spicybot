package com.nsa.spicybot.commands;

import com.nsa.spicybot.commandsystem.CommandArguments;
import com.nsa.spicybot.commandsystem.CommandResult;
import com.nsa.spicybot.commandsystem.CommandSystem;
import com.nsa.spicybot.commandsystem.ICommand;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class HelpCommand implements ICommand
{
	@Override
	public String getCommandName()
	{
		return "help";
	}

	@Override
	public ICommand getCommandInstance()
	{
		return new HelpCommand();
	}

	@Override
	public String getUsage()
	{
		return CommandSystem.getPrefix() + "help";
	}

	@Override
	public CommandResult executeCommand( MessageReceivedEvent evt, CommandArguments args )
	{
		String dummy = "Command Help\nEx: " + CommandSystem.getPrefix() + "cmd <required> [optional]\n";
		for( ICommand cmd: CommandSystem.getRegisteredCommands() )
			dummy += "\n" + cmd.getUsage();
		return new CommandResult( this, dummy, true );
	}
}
