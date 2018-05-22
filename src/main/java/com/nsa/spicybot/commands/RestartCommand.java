package com.nsa.spicybot.commands;

import com.nsa.spicybot.commandsystem.CommandArguments;
import com.nsa.spicybot.commandsystem.CommandResult;
import com.nsa.spicybot.commandsystem.CommandSystem;
import com.nsa.spicybot.commandsystem.ICommand;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class RestartCommand implements ICommand
{
	@Override
	public String getCommandName()
	{
		return "restart";
	}

	@Override
	public ICommand getCommandInstance()
	{
		return new RestartCommand();
	}

	@Override
	public String getUsage()
	{
		return CommandSystem.getPrefix() + "restart";
	}

	@Override
	public CommandResult executeCommand( MessageReceivedEvent evt, CommandArguments args )
	{
		new Thread( () -> { try { Thread.sleep( 3000 ); } catch( InterruptedException e ) {} System.exit( 0 ); } ).start();
		return new CommandResult( this, "Restarting SpicyBot...", true );
	}
	
}
