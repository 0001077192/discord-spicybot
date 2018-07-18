package com.nsa.spicybot.commands;

import com.nsa.spicybot.SpicyBot;
import com.nsa.spicybot.commandsystem.CommandArguments;
import com.nsa.spicybot.commandsystem.CommandResult;
import com.nsa.spicybot.commandsystem.CommandSystem;
import com.nsa.spicybot.commandsystem.ICommand;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class SayCommand implements ICommand
{
    @Override
    public String getCommandName()
    {
        return "say";
    }
    
    @Override
    public ICommand getCommandInstance()
    {
        return new SayCommand();
    }
    
    @Override
    public String getUsage()
    {
        return CommandSystem.getPrefix() + "say <channelname> <message>";
    }
    
    @Override
    public CommandResult executeCommand( MessageReceivedEvent evt, CommandArguments args )
    {
        if( args.length() < 2 )
            return new CommandResult( this, getUsage() );
        List<TextChannel> channels = evt.getJDA().getTextChannelsByName( args.get( 0 ), true );
        if( channels.size() != 1 )
            return new CommandResult( this, "That channel cannot be found!" );
        channels.get( 0 ).sendMessage( args.getRaw( 1 ) ).queueAfter( 1, TimeUnit.SECONDS );
        evt.getMessage().delete().queue();
        return new CommandResult( this/*, "Sending message...", true*/ );
    }
}
