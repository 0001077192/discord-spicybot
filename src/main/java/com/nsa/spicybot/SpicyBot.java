package com.nsa.spicybot;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import com.nsa.spicybot.commands.*;
import com.nsa.spicybot.commandsystem.CommandArguments;
import com.nsa.spicybot.commandsystem.CommandResult;
import com.nsa.spicybot.commandsystem.CommandSystem;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;

public class SpicyBot extends ListenerAdapter
{
	public static JDA discord = null;
	public static SpicyBot bot = null;
	private static String /*token, guild,*/ channel;
	private static boolean init = true;
	private static ArrayList<String> badWords;
    
    public static void main( String[] args )
    {
        //String bad = "?badword!";
        //System.out.println( bad.toLowerCase().replace( "badword", getMild( 3 ) ) );
    }
    
	public static void init( String token, String guild, String channel )
	{
		if( !init )
			throw new IllegalStateException( "Init may only be called once!" );
		//SpicyBot.token = token;
		//SpicyBot.guild = guild;
		SpicyBot.channel = channel;
		init = false;
		CommandSystem.register( new HelpCommand() );
		CommandSystem.register( new RestartCommand() );
		CommandSystem.register( new PollCommand() );
        CommandSystem.register( new SayCommand() );
		CommandSystem.register( new TellCommand() );
  
		System.out.println( "Bot vars initialized:\nTOKEN: " + token + "\nGUILD: " + guild + "\nCHANNEL: " + channel );
		
		//Bad words from https://www.freewebheaders.com/full-list-of-bad-words-banned-by-google/
		Scanner input = new Scanner( SpicyBot.class.getResourceAsStream( "/badwords.txt" ) );
        badWords = new ArrayList<String>();
        while( input.hasNextLine() )
            badWords.add( input.nextLine() );
        System.out.println( "Loaded " + badWords.size() + " bad words." );
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
    		System.out.println( "Message Received: " + msg );
    		if( isFromBotChannel( evt ) )
    		{
    			if( msg.startsWith( "" + CommandSystem.getPrefix() ) )
    			{
    				CommandResult result = CommandSystem.attemptExecute( evt, msg );
    				System.out.println( "Command Result: " + result );
    				if( result.getMessage() != null )
    					evt.getChannel().sendMessage( result.getMessage() ).queueAfter( 1, TimeUnit.SECONDS );
    				return;
    			}
    		} else {
    		    String[] words = msg.split( " " );
    		    String whatTheyMeantToSay = "";
    		    boolean first = true, isBad = false;
    		    for( String word: words )
                {
                    String w = word.toLowerCase();
                    while( w.length() > 0 && !Character.isLetterOrDigit( w.charAt( 0 ) ) )
                        w = w.substring( 1 );
                    while( w.length() > 0 && !Character.isLetterOrDigit( w.charAt( w.length() - 1 ) ) )
                        w = w.substring( 0, w.length() - 1 );
                    boolean isWordBad = false;
                    for( String bad: badWords )
                        if( bad.equalsIgnoreCase( w ) )
                            isWordBad = true;
                    if( !isWordBad )
                        if( first )
                        {
                            whatTheyMeantToSay += word;
                            first = false;
                        } else
                            whatTheyMeantToSay += " " + word;
                    else {
                        isBad = true;
                        if( first )
                        {
                            whatTheyMeantToSay += word.toLowerCase().replace( w, getMild( w.length() ) );
                            first = false;
                        } else
                            whatTheyMeantToSay += " " + word.toLowerCase().replace( w, getMild( w.length() ) );
                    }
                }
                
                if( isBad )
                {
                    try { evt.getMessage().delete().queue(); } catch( IllegalStateException e ) {}
                    evt.getChannel().sendMessage( "I think " + evt.getAuthor().getAsMention() + " meant to say:\n\n" + whatTheyMeantToSay ).queueAfter( 1, TimeUnit.SECONDS );
                }
            }
            
			CommandResult result = CommandSystem.updateIfNeeded( evt, msg );
			System.out.println( "Command Result: " + result );
			if( result != null )
				evt.getChannel().sendMessage( result.getMessage() ).queueAfter( 1, TimeUnit.SECONDS );
			//else
			//	evt.getChannel().sendMessage( "_spicy_" ).queueAfter( 1, TimeUnit.SECONDS );
    	}
    }
	
    private static String getMild( int num )
    {
        Emote  mild  = discord.getEmotesByName( "mild", true ).get( 0 );
        String dummy = "";
        for( int i = 0; i < num; i++ )
            dummy += mild.getAsMention();
        return dummy;
    }
    
	public static boolean isFromBotChannel( MessageReceivedEvent evt )
	{
		return evt.getChannel().getId().equals( channel );
	}
	
	public static String getRemoteVar( String name )
    {
        try {
            URL server = new URL( "https://nsaweb.wixsite.com/spicybot/_functions/var/" + name );
            HttpURLConnection connection = ( HttpURLConnection ) server.openConnection();
            connection.setRequestMethod( "GET" );
            connection.setConnectTimeout( 60000 );
            connection.setReadTimeout( 60000 );
            connection.connect();
            int code = connection.getResponseCode();
            if( code / 100 != 2 )
            {
                System.err.println( "Attempt to GET remote var \"" + name + "\" returned response code " + code + " (" + response( code ) + ")" );
                try {
                    BufferedReader in      = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );
                    String         inputLine;
                    StringBuilder  content = new StringBuilder();
                    while( ( inputLine = in.readLine() ) != null )
                        content.append( inputLine );
                    in.close();
                    connection.disconnect();
                    System.err.println( content.toString() );
                } catch( IOException e ) {
                    System.err.println( "An error occured while attempting to read the error." );
                    //e.printStackTrace();
                }
                return null;
            }
            BufferedReader in      = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );
            String         inputLine;
            StringBuilder  content = new StringBuilder();
            while( ( inputLine = in.readLine() ) != null )
                content.append( inputLine );
            in.close();
            connection.disconnect();
            return content.toString();
        } catch( IOException e )
        {
            System.err.println( "Attempt to GET remote var \"" + name + "\" failed!" );
            e.printStackTrace();
            return null;
        }
    }
    
    public static boolean setRemoteVar( String name, String value )
    {
        try {
            URL server = new URL( "https://nsaweb.wixsite.com/spicybot/_functions/var/" + name + "/" + value );
            HttpURLConnection connection = ( HttpURLConnection ) server.openConnection();
            connection.setRequestMethod( "POST" );
            connection.setConnectTimeout( 5000 );
            connection.setReadTimeout( 5000 );
            connection.connect();
            int code = connection.getResponseCode();
            if( code / 100 != 2 )
            {
                System.err.println( "Attempt to SET remote var \"" + name + "\" returned response code " + code + " (" + response( code ) + ")" );
                try {
                    BufferedReader in      = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );
                    String         inputLine;
                    StringBuilder  content = new StringBuilder();
                    while( ( inputLine = in.readLine() ) != null )
                        content.append( inputLine );
                    in.close();
                    connection.disconnect();
                    System.err.println( content.toString() );
                } catch( IOException e ) {
                    System.err.println( "An error occured while attempting to read the error." );
                    //e.printStackTrace();
                }
                return false;
            } else
                return true;
        } catch( IOException e )
        {
            System.err.println( "Attempt to SET remote var \"" + name + "\" failed!" );
            e.printStackTrace();
            return false;
        }
    }
    
    //Codes: https://www.restapitutorial.com/httpstatuscodes.html
    public static String response( int code )
    {
        switch( code )
        {
            //Informational
            case 100: return "CONTINUE";
            case 101: return "SWITCHING PROTOCOLS";
            case 102: return "PROCESSING";
            
            //Success
            case 200: return "OK";
            case 201: return "CREATED";
            case 202: return "ACCEPTED";
            case 203: return "NON-AUTHORITIVE INFORMATION";
            case 204: return "NO CONTENT";
            case 205: return "RESET CONTENT";
            case 206: return "PARTIAL CONTENT";
            case 207: return "MULTI-STATUS";
            case 208: return "ALREADY REPORTED";
            case 226: return "IM USED";
            
            //Redirection
            case 300: return "MULTIPLE CHOICES";
            case 301: return "MOVED PERMANENTLY";
            case 302: return "FOUND";
            case 303: return "SEE OTHER";
            case 304: return "NOT MODIFIED";
            case 305: return "USE PROXY";
            case 306: return "(UNUSED??)";
            case 307: return "TEMPORARY REDIRECT";
            case 308: return "TEMPORARY REDIRECT (EXPERIMENTAL)";
            
            //Client Error
            case 400: return "BAD REQUEST";
            case 401: return "UNAUTHORIZED";
            case 402: return "PAYMENT REQUIRED";
            case 403: return "FORBIDDEN";
            case 404: return "NOT FOUND";
            case 405: return "METHOD NOT ALLOWED";
            case 406: return "NOT ACCEPTABLE";
            case 407: return "PROXY AUTHENTICATION REQUIRED";
            case 408: return "REQUEST TIMEOUT";
            case 409: return "CONFLICT";
            case 410: return "GONE";
            case 411: return "LENGTH REQUIRED";
            case 412: return "PRECONDITION FAILED";
            case 413: return "REQUEST ENTITY TOO LARGE";
            case 414: return "REQUEST URI TOO LONG";
            case 415: return "UNSUPPORTED MEDIA TYPE";
            case 416: return "REQUESTED RANGE NOT SATISFIABLE";
            case 417: return "EXPECTATION FAILED";
            case 418: return "I'M A TEAPOT";
            case 420: return "ENHANCE YOUR CALM";
            case 422: return "UNPROCESSABLE ENTITY";
            case 423: return "LOCKED";
            case 424: return "FAILED DEPENDENCY";
            case 425: return "(RESERVED CODE??)";
            case 426: return "UPGRADE REQUIRED";
            case 428: return "PRECONDITION REQUIRED";
            case 429: return "TOO MANY REQUESTS";
            case 431: return "REQUEST HEADER FIELDS TOO LARGE";
            case 444: return "NO RESPONSE";
            case 449: return "RETRY WITH";
            case 450: return "BLOCKED BY WINDOWS PARENTAL CONTROLS";
            case 451: return "UNAVAILABLE FOR LEGAL REASONS";
            case 499: return "CLIENT CLOSED REQUEST";
            
            //Server Error
            case 500: return "INTERNAL SERVER ERROR";
            case 501: return "NOT IMPLEMENTED";
            case 502: return "BAD GATEWAY";
            case 503: return "SERVICE UNAVAILABLE";
            case 504: return "GATEWAY TIMEOUT";
            case 505: return "HTTP VERSION NOT SUPPORTED";
            case 506: return "VARIANT ALSO NEGOTIATES";
            case 507: return "INSUFFICIENT STORAGE";
            case 508: return "LOOP DETECTED";
            case 509: return "BANDWIDTH LIMIT EXCEEDED";
            case 510: return "NOT EXTENDED";
            case 511: return "NETWORK AUTHENTICATION REQUIRED";
            case 598: return "NETWORK READ TIMEOUT ERROR";
            case 599: return "NETWORK CONNECT TIMEOUT ERROR";
            
            default:
                switch( code / 100 )
                {
                    case 1:   return "UNKNOWN INFO";
                    case 2:   return "UNKNOWN SUCCESS";
                    case 3:   return "UNKNOWN REDIRECTION";
                    case 4:   return "UNKNOWN CLIENT ERROR";
                    case 5:   return "UNKNOWN SERVER ERROR";
                    default:  return "UNKNOWN ERROR";
                }
        }
    }
}
