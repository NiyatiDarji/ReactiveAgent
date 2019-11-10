//
//	File:			Brain.java
//	Author:		Krzysztof Langner
//	Date:			1997/04/28
//
//    Modified by:	Paul Marlow

//    Modified by:      Edgar Acosta
//    Date:             March 4, 2008

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.Math;
import java.util.Scanner;
import java.util.regex.*;

class Brain extends Thread implements SensorInput
{
    //---------------------------------------------------------------------------
    // This constructor:
    // - stores connection to krislet
    // - starts thread for this object
    public Brain(SendCommand krislet, 
		 String team, 
		 char side, 
		 int number, 
		 String playMode)
    {
	m_timeOver = false;
	m_krislet = krislet;
	m_memory = new Memory();
	//m_team = team;
	m_side = side;
	// m_number = number;
	m_playMode = playMode;
	start();
    }

    public void run()
    {
		ObjectInfo ball;
		ObjectInfo goalpost;
	
		//first place all the players at random positions
		if(Pattern.matches("^before_kick_off.*",m_playMode))
			m_krislet.move( -Math.random()*52.5 , 34 - Math.random()*68.0 );
		
		while( !m_timeOver )
	    {
			
		String combination;
		String action;
		String B;
		String D;
		String d;
		String G;	
		
		ball =  m_memory.getObject("ball");			//get ball object
		if( m_side == 'l' )							//get respective goalpost object
			goalpost = m_memory.getObject("goal r");
		else
			goalpost = m_memory.getObject("goal l");
		
		//setting bits according to the environment perceived
		if( ball == null ) {
			B = "0"; D = "0"; d = "0"; G = "0";
		}
		else{
			B = "1";
			if(ball.m_distance > 1.0) 
				D = "1";
			else 
				D = "0";
			if(ball.m_direction != 0)
				d = "1";
			else
				d = "0";
			if(goalpost == null) 
				G = "0";
			else
				G = "1";
		}
		
		combination = B+D+d+G;
		action = findRuleGetAction(combination); //find action corresponding the given combination from the text file
		
		//perform action
	
		if(Pattern.matches("^waitturn.*",action)) {
		  	String raw_arg = action.substring(action.indexOf("(")+1,action.length()-1);
		   	Double arg = Double.valueOf(raw_arg);
		   	m_krislet.turn(arg);
			m_memory.waitForNewInfo();
		}
		if(Pattern.matches("^kick.*",action)) {
		   	String raw_arg = action.substring(action.indexOf("(")+1,action.length()-1);
		   	String temp[] = raw_arg.split(",");						
		   	Double arg1 = Double.valueOf(temp[0]);								
		   	Double arg2 = null ;
		   	if (temp[1].equals("direction"))
		   		arg2 = (double) goalpost.m_direction;
		   	else
		   		arg2 = Double.valueOf(temp[1]);
		   	m_krislet.kick(arg1, arg2);
		}
		if(Pattern.matches("^turn.*",action)) {
		   	String raw_arg = action.substring(action.indexOf("(")+1,action.length()-1);
		   	Double arg = null;
		   	if (raw_arg.equals("direction"))
		 		arg = (double) ball.m_direction;
		   	else
		   		arg = Double.valueOf(raw_arg);
		   	m_krislet.turn(arg);
		}
		if(Pattern.matches("^dash.*",action)) {
		   	String raw_arg = action.substring(action.indexOf("(")+1,action.length()-1);
		 	Double arg = null ;
		   	if (raw_arg.equals("distance"))
		 		arg = (double) ball.m_distance;
		   	else
		   		arg = Double.valueOf(raw_arg);
		   	m_krislet.dash(10*arg);	    		
		}
		if(action  == null) 
		   	m_krislet.dash(10*ball.m_distance); 
		
		// sleep one step to ensure that we will not send
		// two commands in one cycle.
		try{
			    Thread.sleep(2*SoccerParams.simulator_step);
			}catch(Exception e){
				System.out.println(e);}
		}
		m_krislet.bye();
    }
    
    //FIND RULES FROM TXT FILE
    
    public String findRuleGetAction(String combination) {
    	File file = new File("question1.txt");
    	String action = null;
    	String line;
		try {
			Scanner input = new Scanner(file);			
			while (input.hasNextLine()) {
				line = input.nextLine().toLowerCase();		//made the strings in lower case for case insensitive comparison
			    if(line.contains(combination.toLowerCase())) {
					String parts[] = line.split(":");
			    	action = parts[1];
			    	break;
			    	}		    
			}
		} catch (FileNotFoundException e) {e.printStackTrace();}	
		return action;
    }


    //===========================================================================
    // Here are suporting functions for implement logic


    //===========================================================================
    // Implementation of SensorInput Interface

    //---------------------------------------------------------------------------
    // This function sends see information
    public void see(VisualInfo info)
    {
	m_memory.store(info);
    }


    //---------------------------------------------------------------------------
    // This function receives hear information from player
    public void hear(int time, int direction, String message)
    {
    }

    //---------------------------------------------------------------------------
    // This function receives hear information from referee
    public void hear(int time, String message)
    {						 
	if(message.compareTo("time_over") == 0)
	    m_timeOver = true;

    }


    //===========================================================================
    // Private members
    private SendCommand	                m_krislet;			// robot which is controled by this brain
    private Memory			m_memory;				// place where all information is stored
    private char			m_side;
    volatile private boolean		m_timeOver;
    private String                      m_playMode;
    
}
