package com.nsa.spicybot.commands;

import com.nsa.spicybot.SpicyBot;
import com.nsa.spicybot.commandsystem.CommandArguments;
import com.nsa.spicybot.commandsystem.CommandResult;
import com.nsa.spicybot.commandsystem.CommandSystem;
import com.nsa.spicybot.commandsystem.ICommand;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
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
        
        if( mentions.size() < 1 && args.length() > 0 )
            return new CommandResult( this, getUsage() );
        
        Member mem    = args.length() == 0 ? evt.getMember() : mentions.get( 0 );
        String key    = "users." + mem.getUser().getId() + ".sp";
        Emote  coin   = SpicyBot.discord.getEmotesByName( "sp", true ).get( 0 );
        int    offset = SpicyBot.countSpaces( mem.getEffectiveName() ) + 1;
    
        boolean valid;
        String remoteVar = SpicyBot.getRemoteVar( key );
        int sp = 0;
        try {
            if( remoteVar != null )
                sp = Integer.parseInt( remoteVar );
            valid = true;
        } catch( NumberFormatException e ) {
            valid = false;
        }
        
        if( args.length() < offset + 1 )
            if( valid )
                return new CommandResult( this, mem.getAsMention() + " has " + sp + coin.getAsMention() + "." );
            else
                return new CommandResult( this, mem.getAsMention() + "'s official " + coin.getAsMention() + " count has been corrupted." );
        
        char operation = args.get( offset ).charAt( 0 );
        if( operation != '+' && operation != '-' && operation != '=' )
            return new CommandResult( this, getUsage() );
        
        int amt = 0;
        try {
            amt = Integer.parseInt( args.get( offset ).substring( 1 ) );
        } catch( Exception e ) {
            return new CommandResult( this, getUsage() );
        }
        
        boolean successful;
        switch( operation )
        {
            case '+':
                if( valid )
                    successful = SpicyBot.setRemoteVar( key, "" + ( sp += amt ) );
                else
                    return new CommandResult( this, mem.getAsMention() + "'s official " + coin.getAsMention() + " count has been corrupted." );
                break;
                
            case '-':
                if( valid )
                    successful = SpicyBot.setRemoteVar( key, "" + ( sp -= amt ) );
                else
                    return new CommandResult( this, mem.getAsMention() + "'s official " + coin.getAsMention() + " count has been corrupted." );
                break;
                
            case '=':
                successful = SpicyBot.setRemoteVar( key, "" + ( sp = amt ) );
                break;
                
            default:
                successful = false;
        }
        
        return new CommandResult( this, ( successful ? "Spicy Points Updated Successfully!" : "An error occured while attempting to update the Spicy Point count." ) + "\n" + mem.getAsMention() + " has " + sp + coin.getAsMention() + ".", successful );
    }
}
