package com.nsa.spicybot.commands;

import com.nsa.spicybot.commandsystem.CommandArguments;
import com.nsa.spicybot.commandsystem.CommandResult;
import com.nsa.spicybot.commandsystem.CommandSystem;
import com.nsa.spicybot.commandsystem.ICommand;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class TellCommand implements ICommand
{
    @Override
    public String getCommandName()
    {
        return "tell";
    }
    
    @Override
    public ICommand getCommandInstance()
    {
        return new SayCommand();
    }
    
    @Override
    public String getUsage()
    {
        return CommandSystem.getPrefix() + "tell <user> <message>";
    }
    
    @Override
    public CommandResult executeCommand( MessageReceivedEvent evt, CommandArguments args )
    {
        if( args.length() < 2 )
            return new CommandResult( this, getUsage() );
        List<User> users = evt.getJDA().getUsersByName( args.get( 0 ), true );
        if( users.size() != 1 )
            return new CommandResult( this, "That user cannot be found!" );
        users.get( 0 ).openPrivateChannel().queue( channel -> channel.sendMessage( args.getRaw( 1 ) ).queueAfter( 1, TimeUnit.SECONDS ) );
        return new CommandResult( this, "Sending message...", true );
    }
}
