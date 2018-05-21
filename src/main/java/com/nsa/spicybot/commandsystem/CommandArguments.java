package com.nsa.spicybot.commandsystem;

import java.util.ArrayList;

public class CommandArguments
{
	private String raw;
	private ArrayList<String> args = new ArrayList<String>();
	
	public CommandArguments( String arguments )
	{
		raw = arguments;
		for( String arg: arguments.trim().split( " " ) )
			if( arg.length() > 0 )
				args.add( arg );
	}
	
	public String getRaw()
	{
		return raw;
	}
	
	public String getRaw( int startIndex )
	{
		String dummy = args.get( startIndex );
		for( int i = startIndex + 1; i < args.size(); i++ )
			dummy += " " + args.get( i );
		return dummy;
	}
	
	public String getRaw( int startIndex, int endIndex )
	{
		String dummy = args.get( startIndex );
		for( int i = startIndex + 1; i < endIndex; i++ )
			dummy += " " + args.get( i );
		return dummy;
	}
	
	public String[] toArray()
	{
		String[] dummy = new String[args.size()];
		return args.toArray( dummy );
	}
	
	public String get( int index )
	{
		return args.get( index );
	}
	
	public int length()
	{
		return args.size();
	}
}
