package com.nsa.spicybot.files;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class Database
{
	private String name;
	private HashMap<String, String> entries = new HashMap<String, String>();
	
	public static Database load( String filename )
	{
		try {
			Scanner input = new Scanner( new File( filename ) );
			HashMap<String, String> lines = new HashMap<String, String>();
			Database db = new Database();
			String line = null;
			if( input.hasNextLine() )
				db.setName( input.nextLine().trim() );
			else
				db.setName( filename );
			while( input.hasNextLine() && ( line = input.nextLine().trim() ) != null && !line.startsWith( "#" ) )
				if( line.indexOf( "=" ) != -1 )
					lines.put( line.substring( 0, line.indexOf( "=" ) ), line.substring( line.indexOf( "=" ) + 1 ) );
				else
					lines.put( "UNKNOWN", line );
			return db;
		} catch( IOException e ) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Database( String name )
	{
		this.name = name;
	}
	
	public Database()
	{
		this( null );
	}
	
	public void setName( String name )
	{
		this.name = name;
	}
	
	public String get( String key )
	{
		if( entries.containsKey( key ) )
			return entries.get( key );
		else
			return null;
	}
	
	public String get( String key, String defaultValue )
	{
		if( entries.containsKey( key ) )
			return entries.get( key );
		else
			return defaultValue;
	}
	
	/**
	 * @return Did the entry already exist?
	 */
	public boolean set( String key, String value )
	{
		if( entries.containsKey( key ) )
		{
			entries.put( key, value );
			return true;
		} else {
			entries.put( key, value );
			return false;
		}
	}
}
