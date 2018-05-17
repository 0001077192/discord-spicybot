package com.nsa.spicybot;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;

public class Launcher
{
	public static void main( String[] args )
	{
		System.out.println( "COMMAND-LINE ARGS: " + java.util.Arrays.toString( args ) );
		System.out.println( "Launcher has started!" );
		JDABuilder builder = new JDABuilder( AccountType.BOT );
    	builder.setToken( args[0] );
    	SpicyBot.init( args[0], args[1], args[2] );
    	builder.addEventListener( new SpicyBot() );
    	try {
    		SpicyBot.discord = builder.buildBlocking();
    		Runtime.getRuntime().addShutdownHook( new Thread( () -> SpicyBot.discord.shutdown() ) );
    		SpicyBot.discord.getGuildById( args[1] ).getTextChannelById( args[2] ).sendMessage( "I'm online! Hello @everyone!" );
		} catch( LoginException e )
    	{
			e.printStackTrace();
        } catch( InterruptedException e )
    	{
			e.printStackTrace();
		}
	}
}
