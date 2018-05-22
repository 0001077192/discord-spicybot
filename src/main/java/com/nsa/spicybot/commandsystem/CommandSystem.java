package com.nsa.spicybot.commandsystem;

import java.util.ArrayList;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandSystem
{
	private static ArrayList<ICommand> commands = new ArrayList<ICommand>();
	private static ICommand defaultCommand = null;
	private static ICommand currentCommand = null;
	private static char prefix = '/';
	
	public static void register( ICommand cmd )
	{
		commands.add( cmd );
	}
	
	public static void registerDefault( ICommand cmd )
	{
		defaultCommand = cmd;
	}
	
	public static void setPrefix( char c )
	{
		prefix = c;
	}
	
	public static char getPrefix()
	{
		return prefix;
	}
	
	public static ICommand[] getRegisteredCommands()
	{
		ICommand[] dummy = new ICommand[commands.size()];
		return commands.toArray( dummy );
	}
	
	public static CommandResult updateIfNeeded( MessageReceivedEvent evt, String str )
	{
		str = str.trim();
		if( currentCommand != null && currentCommand instanceof IUpdateableCommand &&
			( ( IUpdateableCommand ) currentCommand ).waitingForUpdate() )
		{
			System.out.println( "Attempting update: " + str );
			return ( ( IUpdateableCommand ) currentCommand ).updateCommand( evt, str );
		} else
			return null;
	}
	
	public static CommandResult attemptExecute( MessageReceivedEvent evt, String cmd )
	{
		System.out.println( "Attempting execute: " + cmd );
		cmd = cmd.trim();
		if( !cmd.toLowerCase().startsWith( "" + prefix ) )
			return new CommandResult( null, "A command starts with a(n) \'" + prefix + "\'!" );
		String base = cmd.substring( 1, cmd.indexOf( " " ) == -1 ? cmd.length() : cmd.indexOf( " " ) );
		base = base.toLowerCase();
		CommandArguments args = new CommandArguments( cmd.indexOf( " " ) == -1 ? "" : cmd.substring( base.length() + 2 ) );
		for( ICommand command: commands )
			if( command.getCommandName().equalsIgnoreCase( base ) )
				return ( currentCommand = command.getCommandInstance() ).executeCommand( evt, args );
		if( defaultCommand != null )
			return defaultCommand.getCommandInstance().executeCommand( evt, args );
		else
			return new CommandResult( null, "Unknown command \"" + prefix + base + "\"!" );
	}
}
