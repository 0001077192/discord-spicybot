package com.nsa.spicybot.commands;

import com.nsa.spicybot.SpicyBot;
import com.nsa.spicybot.commandsystem.CommandArguments;
import com.nsa.spicybot.commandsystem.CommandResult;
import com.nsa.spicybot.commandsystem.CommandSystem;
import com.nsa.spicybot.commandsystem.ICommand;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;


public class SpicyPointsCommand implements ICommand
{
    @Override
    public String getCommandName()
    {
        return "sp";
    }
    
    @Override
    public ICommand getCommandInstance()
    {
        return new SpicyPointsCommand();
    }
    
    @Override
    public String getUsage()
    {
        return CommandSystem.getPrefix() + "sp <user> [<+|-|=><amount>]";
    }
    
    @Override
    public CommandResult executeCommand( MessageReceivedEvent evt, CommandArguments args )
    {
        List<Member> mentions = evt.getMessage().getMentionedMembers();
        
        if( mentions.size() < 1 )
            return new CommandResult( this, getUsage() );
        
        Member mem  = mentions.get( 0 );
        String key  = "users." + mem.getUser().getId() + ".sp";
        Emote  coin = SpicyBot.discord.getEmotesByName( "sp", true ).get( 0 );
        
        String remoteVar = SpicyBot.getRemoteVar( key );
        int sp = 0;
        try {
            if( remoteVar != null )
                sp = Integer.parseInt( remoteVar );
        } catch( NumberFormatException e ) {
            return new CommandResult( this, mem.getAsMention() + "'s Official Spicy Point Count has been corrupted." );
        }
        
        if( args.length() < 2 )
            return new CommandResult( this, mem.getAsMention() + " has " + sp + coin + "." );
        
        char operation = args.get( 1 ).charAt( 0 );
        if( operation != '+' && operation != '-' && operation != '=' )
            return new CommandResult( this, getUsage() );
        
        int amt = 0;
        try {
            amt = Integer.parseInt( args.get( 1 ).substring( 1 ) );
        } catch( Exception e ) {
            return new CommandResult( this, getUsage() );
        }
        
        boolean successful;
        switch( operation )
        {
            case '+':
                successful = SpicyBot.setRemoteVar( key, "" + ( sp += amt ) );
                break;
                
            case '-':
                successful = SpicyBot.setRemoteVar( key, "" + ( sp -= amt ) );
                break;
                
            case '=':
                successful = SpicyBot.setRemoteVar( key, "" + ( sp = amt ) );
                break;
                
            default:
                successful = false;
        }
        
        return new CommandResult( this, ( successful ? "Spicy Points Updated Successfully!" : "An error occured while attempting to update the Spicy Point count." ) + "\n" + mem.getAsMention() + " has " + sp + coin + ".", successful );
    }
}
