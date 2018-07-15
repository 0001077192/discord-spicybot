package com.nsa.spicybot;

import java.util.concurrent.TimeUnit;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;

public class Launcher
{
    //Legacy args: { TOKEN, GUILD, CHANNEL }
	public static void main( String[] args )
	{
		System.out.println( "COMMAND-LINE ARGS: " + java.util.Arrays.toString( args ) );
		System.out.println( "Launcher has started!" );
		String token = SpicyBot.getRemoteVar( "bot.token" ), guild = SpicyBot.getRemoteVar( "bot.guild" ), channel = SpicyBot.getRemoteVar( "bot.channel" );
		args = new String[] { token, guild, channel };
		JDABuilder builder = new JDABuilder( AccountType.BOT );
    	builder.setToken( args[0] );
    	SpicyBot.init( args[0], args[1], args[2] );
    	builder.addEventListener( new SpicyBot() );
    	try {
    		Runtime.getRuntime().addShutdownHook( new Thread( () -> {
    			System.out.println( "Shutting down JDA..." );
    			if( SpicyBot.discord != null )
    				SpicyBot.discord.shutdown();
    			else
    				System.err.println( "Error shutting down JDA: JDA has not been initialized!" );
    		} ) );
    		SpicyBot.discord = builder.buildBlocking();
    		System.out.println( "Connected!" );
    		SpicyBot.discord.getGuildById( args[1] ).getTextChannelById( args[2] ).sendMessage( "SpicyBot has been enabled." ).queueAfter( 1, TimeUnit.SECONDS );
		} catch( LoginException e )
    	{
			e.printStackTrace();
        } catch( InterruptedException e )
    	{
			e.printStackTrace();
		}
	}
}
