package com.nsa.spicybot;

import com.nsa.spicybot.commandsystem.CommandSystem;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

public class SpicyBot extends ListenerAdapter
{
	public static JDA discord = null;
	public static SpicyBot bot = null;
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
		
		System.out.println( "Bot vars initialized:\nTOKEN: " + token + "\nGUILD: " + guild + "\nCHANNEL: " + channel );
	}
    
	public SpicyBot()
	{
		if( init )
			throw new IllegalStateException( "Init must be called before creating a bot!" );
		if( bot != null )
			throw new IllegalStateException( "Only one bot may exist!" );
		else
			bot = this;
	}
	
	@Override
    @SubscribeEvent
    public void onMessageReceived( MessageReceivedEvent evt )
    {
    	if( !evt.getAuthor().isBot() )
    	{
    		String msg = evt.getMessage().getContentStripped();
    		if( isFromBotChannel( evt ) )
    			if( msg.startsWith( "" + CommandSystem.getPrefix() ) )
    				CommandSystem.attemptExecute( evt, msg );
    			else
    				if( CommandSystem.updateIfNeeded( evt, msg ) == null )
    					evt.getChannel().sendMessage( "_spicy_" ).queue();
    	}
    }
	
	public static boolean isFromBotChannel( MessageReceivedEvent evt )
	{
		return evt.getChannel().getId().equals( channel );
	}
}
