/*
 *In Pairs create two Bot subclasses which meet all specifications ___/10
 *Bot is submitted complete and on time to compete
 *Has a firing method which uses appropriate logic to fire at other bots
 *Has a dodge method which uses appropriate logic to dodge oncoming bullets
 *Does not throw exceptions/Has images
 *Has appropriate logic for picking up bullets from dead bodies.
 *One of teams bot's places within the top half of competitors
 ****Additionally, there will be 4 marks allocated for demonstrating collaboration skills to work together in their pair but possibly also with other groups. This can be demonstrated in a number of ways: pair programming in class, sharing code and discussion on Slack and Github. Posting and answering questions on Slack and in class.
 */
package bots;

import java.awt.Graphics;
import java.awt.Image;

import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;




public class Rinnegan extends Bot {

	private enum BotState 
	{
		SCAVENGE, DODGE, SHOOT
	};

	private int constant;//for gif images

	private BotState state = BotState.SHOOT;

	private int move = BattleBotArena.STAY;

	String name; 

	//private int fireOK;

	BotHelper helper = new BotHelper();

	public Rinnegan() {
		// TODO Auto-generated constructor stub

	}
	private boolean dodge = true;//boolean to state whether dodging is required

	private int counter = 50;//counter that is used to seperate bullets from being fired simultaneously

	private int spiralCounter = 0;//counter used to fire four bullets; one in each direction, simultaneously at the start of each round

	private boolean start1;//this is used to make the bot have a boost at the beginning of each round

	private boolean start2;//this is used to set the bot to its regular state after the boosted state

	private double x, y;//stating variables for my bots x and y coordinate

	private double enemyX, enemyY;//variables for enemy x and y

	private int frameCount = 0;//variable to control gif animation speed

	String[] images = new String[36];//String array that holds the strings needed to load images from computer files

	Image[] gif = new Image[36];//array needed to set loaded images in a for loop for animation

	private int a = 0;//variable used to animate the gif in the get move function

	Image up, down, left, right, current;



	@Override
	public void newRound() {
		// TODO Auto-generated method stub
		a=0;
		spiralCounter = 0;
		state = BotState.DODGE;
		start1 = true;
		start2 = false;
		dodge = true;

	}

	@Override
	public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets){


		counter--; //decreasing counter to be used to prevent bullets from being fired at the same time; this counter is divided by modulus command
		frameCount++; //controlling gif animation speed
		spiralCounter++; //controls the duration of the boost at the start of each round
		
		BotInfo closestEnemy = null;
		Bullet closestDanger = null;
		BotInfo closestDead = null;

		//move = BattleBotArena.DOWN;
		boolean shoot = true;
		//these if statements prevent errors
		if (liveBots.length > 0) {
			closestEnemy = helper.findClosest(me, liveBots);
		}

		if (bullets.length > 0) {
			closestDanger = helper.findClosest(me, bullets);
		}

		if (deadBots.length > 0) {
			closestDead = helper.findClosest(me, deadBots);
		}

		//Switch images every 10 frames
		if(frameCount > 5) {
			if(a < gif.length) {
				current = gif[a];
				//go to the next image
				a++;
			}
			else {
				//go back to the first image
				a = 0;
				current = gif[a];
			}
			//reset the frame count
			frameCount = 0;
		}

		if (start1) {
			/*//start1 is true at the beginning of each round. when this is true, 
			 * if the bot spawns in the upper half of the screen, it will move 
			 * down and fire right, increasing probability to kill enemies.
			 * If the bot spawns in the lower half of the screen, it will move up and fire left, for 100 frames.
			 * */
			if (spiralCounter < 100) {//if 100 frames have not been reached yet at the start of each round
				if(me.getBotNumber() < BattleBotArena.NUM_BOTS/2) {//if my bot spawns in the upper half of the screen
					move = BattleBotArena.DOWN;//move down
					if(counter % 25 == 0 &&shotOK) {//if the shot is ready 
					return BattleBotArena.FIRERIGHT;//fire right
					}
				}
				else if(me.getBotNumber() > BattleBotArena.NUM_BOTS/2) {
					move = BattleBotArena.UP;
					if(counter % 25 == 0 &&shotOK) {
						return BattleBotArena.FIRELEFT;
						}
				}
			}
			else {//when 100 frames have passed since the begninning of the new round
				start1 = false;//set boost state to false
				start2 = true;//return to regular functionality of the bot (logical dodging, shooting, scavenging
			}
//			
		}

		if (start2 == true) {
//			
			
			//if state is dodge and there are bullets in range
			if(bullets.length > 0 && state == BotState.DODGE) {
				//if the y difference between bot and bullet is small
				if(Math.abs(me.getY() - closestDanger.getY()) < Bot.RADIUS*5) {
					//and the bot is 50 pixels behind the bullet
					if((me.getX() - closestDanger.getX()) < 50) {
						move = BattleBotArena.UP;
						//if close to top of screen, move down
						if(Math.abs(me.getY() - BattleBotArena.TOP_EDGE) < 100) {
							move = BattleBotArena.DOWN;
						}
					}
					//if bullet is in front of you
					else if((me.getX() - closestDanger.getX()) < -50) {
						move = BattleBotArena.DOWN;
						//if close to bottom of screen, move up
						if(Math.abs(me.getY() - BattleBotArena.BOTTOM_EDGE) < 100) {
							move = BattleBotArena.UP;
						}
					}
				}
				//if x of bullet and bot is within small range
				else if(Math.abs(me.getX() - closestDanger.getX()) < Bot.RADIUS*5) {
					//if bullet is 50 pixels below you
					if((me.getY() - closestDanger.getY()) < 50) {
						move = BattleBotArena.LEFT;
						//if close to left edge
						if(Math.abs(me.getX() - BattleBotArena.LEFT_EDGE) < 100) {
							move = BattleBotArena.RIGHT;
						}
					}
					//if bullet is 50 pixels above you
					else if((me.getY() - closestDanger.getY()) < -50) {
						move = BattleBotArena.RIGHT;
						//if close to right edge
						if(Math.abs(me.getX() - BattleBotArena.RIGHT) < 100) {
							move = BattleBotArena.LEFT;
						}
					}
				}
				//if bot can shoot right now
				else /*if(shotOK)*/ {
					//change state
					state = BotState.SHOOT;
					dodge = false;
				}
			}
			else if(state == BotState.SHOOT) {//if bot is in shooting state
				for (int i = 0; i < liveBots.length; i++) {//scans for enemy bots 
					
					double hDifference = Math.abs(helper.calcDisplacement(me.getX(), liveBots[i].getX()));//variable that holds the value for horizontal difference in my bot and enemy bot

					double vDifference = Math.abs(helper.calcDisplacement(me.getY(), liveBots[i].getY()));//variable that holds the value for vertical difference in my bot and enemy bot

					if(bullets.length > 0) { 
						
						//if the y difference between bot and bullet is small
						if(Math.abs(me.getY() - closestDanger.getY()) < Bot.RADIUS*5) {
							//and the bot is 50 pixels behind the bullet
							if((me.getX() - closestDanger.getX()) < 50) {
								dodge = true;
								state = BotState.DODGE;
							}
							//if bullet is in front of you
							else if((me.getX() - closestDanger.getX()) < -50) {
								dodge = true;
								state = BotState.DODGE;
							}
						}
						//if x of bullet and bot is within small range
						else if(Math.abs(me.getX() - closestDanger.getX()) < Bot.RADIUS*5) {
							//if bullet is 50 pixels below you
							if((me.getY() - closestDanger.getY()) < 50) {
								dodge = true;
								state = BotState.DODGE;
							}
							//if bullet is 50 pixels above you
							else if((me.getY() - closestDanger.getY()) < -50) {
								dodge = true;
								state = BotState.DODGE;
							}
						}

					}
					
					else if (counter % 25 == 0 && shotOK) {//separate bullets (prevent four bullets firing at same time creating beam)
						
						if (hDifference <= vDifference) {
							
							if(me.getX() > liveBots[i].getX() ) {//if my bot is to the right of the enemy bot
								return BattleBotArena.FIRELEFT;
							}
							
							else if(me.getX() < liveBots[i].getX() ) {//if my bot is to the left of the enemy bot
								return BattleBotArena.FIRERIGHT;
							}
						}
						
						if (vDifference <= hDifference) {
							
							if(me.getY() > liveBots[i].getY() ) {//if my bot is above enemy bot
								return BattleBotArena.FIREDOWN;
							}
							
							else if(me.getY() < liveBots[i].getY() ) {//if my bot is below enemy bot
								return BattleBotArena.FIREUP;
							}
						}
					}

					else if(dodge == false && me.getBulletsLeft() == 0) {//if theres no need to dodge, and my bot has no bullets remaining
						state = BotState.SCAVENGE;//change bot state to scavenge for ammo
					}
				}
			}

			else if (state == BotState.SCAVENGE) {
				//gathering ammo
				if(bullets.length > 0) {
					//if the y difference between bot and bullet is small
					if(Math.abs(me.getY() - closestDanger.getY()) < Bot.RADIUS*5) {
						//and the bot is 50 pixels behind the bullet
						if((me.getX() - closestDanger.getX()) < 50) {
							dodge = true;
							state = BotState.DODGE;
						}
						//if bullet is in front of you
						else if((me.getX() - closestDanger.getX()) < -50) {
							dodge = true;
							state = BotState.DODGE;
						}
					}
					//if x of bullet and bot is within small range
					else if(Math.abs(me.getX() - closestDanger.getX()) < Bot.RADIUS*5) {
						//if bullet is 50 pixels below you
						if((me.getY() - closestDanger.getY()) < 50) {
							dodge = true;
							state = BotState.DODGE;
						}
						//if bullet is 50 pixels above you
						else if((me.getY() - closestDanger.getY()) < -50) {
							dodge = true;
							state = BotState.DODGE;
						}
					}

				}
				else if(closestDead.getBulletsLeft() > 0) {
					//if my x location is not equal to the nearest deadbots x location, move to that x location
					if(me.getX() != closestDead.getX()) {
						if(me.getX()-closestDead.getX()>0) {
							move = BattleBotArena.LEFT;
						}
						else if(me.getX()-closestDead.getX()<0) {
							move = BattleBotArena.RIGHT;
						}
					}
					else if (me.getX() == closestDead.getX()) {
						move = BattleBotArena.STAY;
						if (me.getY() != closestDead.getY()) {
							if(me.getY()-closestDead.getY()>0) {
								move = BattleBotArena.UP;
							}
							else if(me.getY()-closestDead.getY()<0) {
								move = BattleBotArena.DOWN;
							}
						}
					}
				}
			}
		}

		//		for(int i = 0; i < liveBots.length; i++) {
		//			
		//		}
		x = me.getX();
		y = me.getY();

		if (liveBots.length > 0) {
			enemyX = closestEnemy.getX();
			enemyY = closestEnemy.getY();
		}
		//System.out.print(me.getBulletsLeft());
		return move;

	}









	@Override
	public void draw(Graphics g, int x, int y) {
		// TODO Auto-generated method stub
		g.drawImage(current, x, y, Bot.RADIUS*2, Bot.RADIUS*2, null);

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "BroProGuo";
	}

	@Override
	public String getTeamName() {
		// TODO Auto-generated method stub
		return "Null Pointer Exception";
	}

	@Override
	public String outgoingMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void incomingMessage(int botNum, String msg) {
		// TODO Auto-generated method stub

	}

	@Override


	/**
	 * Image names
	 */
	public String[] imageNames()//loading images
	{
		//String[] images;
		for(int i = 0; i < images.length; i++) {
			constant = i;
			if (i < 10) {
				images[i] = ("frame_0"+i+"_delay-0.01s.gif");
			}
			else if (i >= 10) {
				images[i] = ("frame_"+i+"_delay-0.01s.gif");			
			}
		}
		return images;
	}

	/**
	 * Store the loaded images
	 */
	public void loadedImages(Image[] images)//setting String array to Image array called gif[]
	{
		if (images != null)
		{
			for(int i = 0; i < gif.length; i++) {
				gif[i] = images[i];
			}
		}
	}
} 

