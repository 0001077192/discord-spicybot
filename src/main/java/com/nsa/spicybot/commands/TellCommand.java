package com.nsa.spicybot.commands;

import com.nsa.spicybot.SpicyBot;
import com.nsa.spicybot.commandsystem.CommandArguments;
import com.nsa.spicybot.commandsystem.CommandResult;
import com.nsa.spicybot.commandsystem.CommandSystem;
import com.nsa.spicybot.commandsystem.ICommand;
import net.dv8tion.jda.core.entities.Member;
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
        return new TellCommand();
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
        
        List<Member> mentions = evt.getMessage().getMentionedMembers();
        
        if( mentions.size() < 1 )
            return new CommandResult( this, getUsage() );
        
        Member  mem    = mentions.get( 0 );
        int     offset = SpicyBot.countSpaces( mem.getEffectiveName() ) + 1;
        
        evt.getMessage().delete().queue();
        mem.getUser().openPrivateChannel().queue( channel -> channel.sendMessage( args.getRaw( offset ) ).queueAfter( 1, TimeUnit.SECONDS ) );
        return new CommandResult( this/*, "Sending message...", true*/ );
    }
}
