package com.nsa.spicybot.commands;

import java.util.List;

import com.nsa.spicybot.SpicyBot;
import com.nsa.spicybot.commandsystem.CommandArguments;
import com.nsa.spicybot.commandsystem.CommandResult;
import com.nsa.spicybot.commandsystem.CommandSystem;
import com.nsa.spicybot.commandsystem.ICommand;
import com.nsa.spicybot.commandsystem.IUpdateableCommand;

import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class PollCommand implements IUpdateableCommand
{
	private static String question = null;
	private static String[] choices = null;
	private static String channel = null;
	private static PollCommand currentPoll = null;
	
	private static int[] poll = null;
	
	@Override
	public String getCommandName()
	{
		return "poll";
	}
	
	@Override
	public ICommand getCommandInstance()
	{
		if( currentPoll == null )
			return new PollCommand();
		else
			return currentPoll;
	}
	
	public String getUsage()
	{
		return "/poll open <question>\n/poll close";
	}
	
	public CommandResult open( String question, String... choices )
	{
		if( PollCommand.question != null || PollCommand.choices != null )
			return new CommandResult( this, "A poll has already been opened!" );
		PollCommand.question = question;
		PollCommand.choices = choices;
		PollCommand.currentPoll = this;
		return new CommandResult( this );
	}
	
	public CommandResult close()
	{
		if( PollCommand.question == null || PollCommand.choices == null )
			return new CommandResult( this, "There are no open polls!" );
		PollCommand.question = null;
		PollCommand.choices = null;
		PollCommand.currentPoll = null;
		return new CommandResult( this, "The current poll has been closed. You don't get to see the results." );
	}
	
	@Override
	public CommandResult executeCommand( MessageReceivedEvent evt, CommandArguments args )
	{
		if( args.length() < 1 )
			return new CommandResult( this, getUsage() );
		
		if( args.get( 1 ).equalsIgnoreCase( "open" ) )
			if( args.length() > 1 )
			{
				PollCommand.currentPoll = this;
				PollCommand.question = args.getRaw( 2 );
				return new CommandResult( this, "Type each of the choices for your poll on its own line and say it to me:", true );
			} else
				return new CommandResult( this );
		else
			if( args.get( 1 ).equalsIgnoreCase( "close" ) )
				close();
		return new CommandResult( this );
	}
	
	@Override
	public boolean waitingForUpdate()
	{
		return currentPoll != null;
	}
	
	@Override
	public CommandResult updateCommand( MessageReceivedEvent evt, String data )
	{
		if( PollCommand.choices == null )
		{
			PollCommand.choices = data.split( "\n" );
			return new CommandResult( this, "What channel do you want to send the poll in?", true );
		}
		
		if( PollCommand.channel == null )
		{
			List<TextChannel> channels = evt.getJDA().getTextChannelsByName( data, true );
			if( channels.size() < 1 )
				return new CommandResult( this, "What channel do you want to send the poll in?", false );
			PollCommand.channel = data;
			String dummy = "NEW POLL:\n" + PollCommand.question;
			for( int i = 0; i < choices.length; i++ )
				dummy += "\n" + i + ": " + choices[i];
			dummy += "\n\nTo cast your vote, say \"vote <choice>\", where <choice> is the number of your choice!";
			for( TextChannel chan: channels )
				chan.sendMessage( dummy ).queue();
			poll = new int[choices.length];
			return new CommandResult( this, "The poll has been created! Use \"" + CommandSystem.getPrefix() + "poll close\" to close the poll!" );
		}
		
		if( SpicyBot.isFromBotChannel( evt ) )
			return new CommandResult( this, "Use \"" + CommandSystem.getPrefix() + "poll close\" to close the current poll!" );
		else
			if( data.toLowerCase().startsWith( "vote" ) )
				try {
					int vote = Integer.parseInt( data.substring( 5 ) );
					poll[vote]++;
					return new CommandResult( this, "Your cote has been cast, " + evt.getAuthor().getAsMention() + "!", true );
				} catch( Exception e ) {
					return new CommandResult( this, evt.getAuthor().getAsMention() + ": Valid vote choices are numbers 0-" + ( choices.length - 1 ) + "." );
				}
		return new CommandResult( this );
	}
}
