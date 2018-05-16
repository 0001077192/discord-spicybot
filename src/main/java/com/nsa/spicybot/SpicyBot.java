package com.nsa.spicybot;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

public class SpicyBot extends ListenerAdapter
{
	public static JDA discord;
	private static String /*token, guild,*/ channel;
	private static boolean init = true;
	
	public static void init( String token, String guild, String channel )
	{
		if( !init )
			throw new IllegalStateException( "Init may only be called once!" );
		//SpicyBot.token = token;
		//SpicyBot.guild = guild;
		SpicyBot.channel = channel;
		init = false;
	}
    
    @SubscribeEvent
    public void onMessageReceived( MessageReceivedEvent evt )
    {
    	if( evt.getChannel().getId().equals( channel ) )
    		evt.getChannel().sendMessage( "_spicy_" );
    }
}
