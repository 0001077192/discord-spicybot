package com.nsa.spicybot.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.nsa.spicybot.SpicyBot;
import com.nsa.spicybot.commandsystem.CommandArguments;
import com.nsa.spicybot.commandsystem.CommandResult;
import com.nsa.spicybot.commandsystem.CommandSystem;
import com.nsa.spicybot.commandsystem.ICommand;
import com.nsa.spicybot.commandsystem.IUpdateableCommand;

import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class PollCommand implements IUpdateableCommand
{
	private static String question = null;
	private static String[] choices = null;
	private static String channel = null;
	private static PollCommand currentPoll = null;
	
	private static int[] poll = null;
	private static HashMap<User, Integer> votes = null;
	
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
		return CommandSystem.getPrefix() + "poll open <question>\n" + CommandSystem.getPrefix() + "poll close";
	}
	
	public CommandResult open( String question, String... choices )
	{
		if( PollCommand.currentPoll != null )
			return new CommandResult( this, "A poll has already been opened!" );
		PollCommand.question = question;
		PollCommand.choices = choices;
		PollCommand.currentPoll = this;
		return new CommandResult( this );
	}
	
	public CommandResult close()
	{
		if( PollCommand.currentPoll == null )
			return new CommandResult( this, "There are no open polls!" );
		String results = Arrays.toString( poll );
		PollCommand.question = null;
		PollCommand.choices = null;
		PollCommand.channel = null;
		PollCommand.currentPoll = null;
		PollCommand.poll = null;
		PollCommand.votes = null;
		return new CommandResult( this, "The current poll has been closed. Results: " + results, true );
	}
	
	@Override
	public CommandResult executeCommand( MessageReceivedEvent evt, CommandArguments args )
	{
		if( args.length() > 0 )
			if( args.get( 0 ).equalsIgnoreCase( "open" ) )
			{
				if( args.length() > 1 )
				{
					PollCommand.currentPoll = this;
					PollCommand.question = args.getRaw( 1 );
					return new CommandResult( this, "Type each of the choices for your poll on its own line and say it to me:", true );
				}
			} else
				if( args.get( 0 ).equalsIgnoreCase( "close" ) )
					return close();
		
		return new CommandResult( this, getUsage() );
	}
	
	@Override
	public boolean waitingForUpdate()
	{
		return currentPoll != null;
	}
	
	@Override
	public CommandResult updateCommand( MessageReceivedEvent evt, String data )
	{
		if( SpicyBot.isFromBotChannel( evt ) )
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
				votes = new HashMap<User, Integer>();
				return new CommandResult( this, "The poll has been created! Use \"" + CommandSystem.getPrefix() + "poll close\" to close the poll!" );
			}
			
			if( data.length() > 6 && data.substring( 6 ).toLowerCase().startsWith( "close" ) )
				return close();
			else
				return new CommandResult( this, "Use \"" + CommandSystem.getPrefix() + "poll close\" to close the current poll!" );
		} else
			if( data.toLowerCase().startsWith( "vote" ) )
				try {
					int vote = Integer.parseInt( data.substring( 5 ) );
					poll[vote]++;
					if( votes.containsKey( evt.getAuthor() ) )
					{
						poll[votes.get( evt.getAuthor() )]--;
						return new CommandResult( this, "Your vote has been changed, " + evt.getAuthor().getAsMention() + "!", true );
					}
					votes.put( evt.getAuthor(), vote );
					return new CommandResult( this, "Your vote has been cast, " + evt.getAuthor().getAsMention() + "!", true );
				} catch( Exception e ) {
					return new CommandResult( this, evt.getAuthor().getAsMention() + ": Valid vote choices are numbers 0-" + ( choices.length - 1 ) + "." );
				}
		return new CommandResult( this );
	}
}
