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
		String dummy = "Command Help\n<Angle Bracketed Arguments> are required\n[Square Bracketed Arguments] are not\n\n";
		for( ICommand cmd: CommandSystem.getRegisteredCommands() )
			dummy += cmd.getUsage();
		return new CommandResult( this, dummy, true );
	}
}
