package com.nsa.spicybot;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

public class SpicyBot extends ListenerAdapter
{
	public static JDA discord;
	private static String token, guild, channel;
	
    public static void main( String[] args )
    {
    	token   = args[0];
    	guild   = args[1];
    	channel = args[2];
    	
    	JDABuilder builder = new JDABuilder( AccountType.BOT );
    	builder.setToken( token );
    	builder.addEventListener( new SpicyBot() );
    	try {
			discord = builder.buildBlocking();
	    	discord.getGuildById( guild ).getTextChannelById( channel ).sendMessage( "I'm online! Hello @everyone!" );
		} catch( LoginException e )
    	{
			e.printStackTrace();
        } catch( InterruptedException e )
    	{
			e.printStackTrace();
		}
    }
    
    @SubscribeEvent
    public void onMessageReceived( MessageReceivedEvent evt )
    {
    	if( evt.getChannel().getId().equals( channel ) )
    		evt.getChannel().sendMessage( "_spicy_" );
    }
}
