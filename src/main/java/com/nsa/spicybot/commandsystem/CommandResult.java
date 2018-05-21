package com.nsa.spicybot.commandsystem;

public class CommandResult
{
	private ICommand source;
	private String message;
	private boolean successful = false;
	
	public CommandResult( ICommand source )
	{
		this( source, null, true );
	}
	
	public CommandResult( ICommand source, String error )
	{
		this( source, error, false );
	}
	
	public CommandResult( ICommand source, String message, boolean successful )
	{
		this.source = source;
		this.message = message;
		this.successful = successful;
	}

	public ICommand getCommand()
	{
		return source;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public boolean wasSuccessful()
	{
		return successful;
	}
	
	public String toString()
	{
		return getCommand() + "; successful: " + wasSuccessful() + "; " + getMessage();
	}
}
