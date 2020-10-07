package bots;

import java.awt.Graphics;
import java.awt.Image;
import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;
import bots.SwatchBot.botState;

public class SwatchBot extends Bot {
	enum botState {
		RANDOM, DODGING, SCAVENGING, ATTACKING
		// Do not need a dodging state as the bot will always be dodging
		// Priority is to stay alive
	}

	private int constant;// for gif images

	private int frameCount = 0;// variable to control gif animation speed

	private int counter = 50;// counter that is used to seperate bullets from being fired simultaneously

	private int spiralCounter = 0;// counter used to fire four bullets; one in each direction, simultaneously at
									// the start of each round

	String[] images = new String[6];// String array that holds the strings needed to load images from computer files
	
	Image[] gif = new Image[6];// array needed to set loaded images in a for loop for animation

	private int a = 0;// variable used to animate the gif in the get move function

	BotHelper helper = new BotHelper();

	Image up, down, left, right, current;

	private int move;

	protected botState state;

	private int moveCounts = 99;

	private double x, y;

	private boolean start1;// this is used to make the bot have a boost at the beginning of each round

	private boolean start2;// this is used to set the bot to its regular state after the boosted state

	public SwatchBot() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void newRound() {
		// TODO Auto-generated method stub
		a = 0;
		spiralCounter = 0;
		start1 = true;
		start2 = false;
		int i = (int) (Math.random() * 4);
		if (i == 0) {
			move = BattleBotArena.UP;
		} else if (i == 1) {
			move = BattleBotArena.DOWN;
		} else if (i == 2) {
			move = BattleBotArena.LEFT;
		} else {
			move = BattleBotArena.RIGHT;
		}
	}

	@Override
	public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
		
		
		// find the closest livebot, bullet and deadbot
		BotInfo closestEnemy = null;
		Bullet closestDanger = null;
		BotInfo closestDead = null;

		counter--; // decreasing counter to be used to prevent bullets from being fired at the same
					// time; this counter is divided by modulus command
		frameCount++; // controlling gif animation speed
		spiralCounter++; // controls the duration of the boost at the start of each round

		if (liveBots.length > 0) {
			closestEnemy = helper.findClosest(me, liveBots);
		}
		if (bullets.length > 0) {
			closestDanger = helper.findClosest(me, bullets);
		}
		if (deadBots.length > 0) {
			closestDead = helper.findClosest(me, deadBots);
		}

	

		if (start1 == true) {
			/*
			 * //start1 is true at the beginning of each round. when this is true, if the
			 * bot spawns in the upper half of the screen, it will move down and fire right,
			 * increasing probability to kill enemies. If the bot spawns in the lower half
			 * of the screen, it will move up and fire left, for 100 frames.
			 */
			if (spiralCounter < 24) {// if 100 frames have not been reached yet at the start of each round
				if (me.getBotNumber() < BattleBotArena.NUM_BOTS / 2) {// if my bot spawns in the upper half of the
																		// screen
					// if bot beside my bot is a teammate, don't shoot powerup.

					move = BattleBotArena.DOWN;// move down
					if (counter % 6 == 0 && shotOK) {// if the shot is ready
						return BattleBotArena.FIRERIGHT;// fire right

					}

				} else if (me.getBotNumber() > BattleBotArena.NUM_BOTS / 2) {
					move = BattleBotArena.UP;
					if (counter % 6 == 0 && shotOK) {
						return BattleBotArena.FIRELEFT;
					}
				}
			} else {// when 100 frames have passed since the beginning of the new round
				start1 = false;// set boost state to false
				start2 = true;// return to regular functionality of the bot (logical dodging, shooting,
								// scavenging
			}
//			
		}

		if (start2 == true) {

			// if the nearest bullet is within a certain range, go to dodge state
			if (bullets.length > 0) {
				if (Math.abs(me.getX() - closestDanger.getX()) <= 50
						|| Math.abs(me.getY() - closestDanger.getY()) <= 50) {
					state = botState.DODGING;
				}
			}

			if (me.getBulletsLeft() <= 16 && closestDead.getBulletsLeft() > 0) {
				state = botState.SCAVENGING;
			}
			if (me.getBulletsLeft() > 25) {
				if (state != botState.DODGING) {
					state = botState.ATTACKING;
				}
			}

			// Change the direction of the bot when moving or shooting in a ceratin
			// direction and update the image

			// if the bot get stuck, move randomly
			if (me.getX() == x && me.getY() == y) {
				state = botState.RANDOM;
			}
			// update my record of my most recent position
			x = me.getX();
			y = me.getY();

			if (state == botState.RANDOM) {
				moveCounts++;
				if (moveCounts >= 30 + (int) Math.random() * 6) {
					moveCounts = 0;
					int choice = (int) (Math.random() * 4);
					if (choice == 0) {
						move = BattleBotArena.UP;
					} else if (choice == 1) {
						move = BattleBotArena.DOWN;
					} else if (choice == 2) {
						move = BattleBotArena.LEFT;
					} else if (choice == 3) {
						move = BattleBotArena.RIGHT;
					}
				}
			}

			if (state == botState.DODGING) {
//				//taking precautions around the edges of arena
//				if (Math.abs(me.getX() - BattleBotArena.RIGHT_EDGE) <=25) {
//					move = BattleBotArena.LEFT;
//				}
//				if (Math.abs(me.getX() - BattleBotArena.LEFT_EDGE) <=25) {
//					move = BattleBotArena.RIGHT;
//				}
//				if (Math.abs(me.getX() - BattleBotArena.TOP_EDGE) <=25) {
//					move = BattleBotArena.DOWN;
//				}
//				if (Math.abs(me.getX() - BattleBotArena.BOTTOM_EDGE) <=25) {
//					move = BattleBotArena.UP;
//				}

				// stuck state
				// gets off tombstone when stuck
				if (Math.abs(me.getX() - closestDead.getX()) <= Bot.RADIUS * 3
						&& Math.abs(me.getY() - closestDead.getY()) <= Bot.RADIUS * 3) {
					if (Math.abs(me.getX() - closestDead.getX()) <= Bot.RADIUS * 3) {
						if (me.getY() > closestDead.getY()) {
							move = BattleBotArena.DOWN;
						} else if (me.getY() < closestDead.getY()) {
							move = BattleBotArena.UP;
						}
					}
					if (Math.abs(me.getY() - closestDead.getY()) <= Bot.RADIUS * 3) {
						if (me.getX() > closestDead.getX()) {
							move = BattleBotArena.RIGHT;
						} else if (me.getX() < closestDead.getX()) {
							move = BattleBotArena.LEFT;
						}
					}
				}

				// fires at enemy that our bot is stuck on
				if (closestEnemy.getName() != "BroProGuoBot" && closestEnemy.getName() != "CompileBot") {
					if (Math.abs(me.getX() - closestEnemy.getX()) <= Bot.RADIUS * 3
							&& Math.abs(me.getY() - closestEnemy.getY()) <= Bot.RADIUS * 3) {

						if (Math.abs(me.getX() - closestEnemy.getX()) <= Bot.RADIUS * 3) {
							if (me.getY() > closestEnemy.getY()) {
								move = BattleBotArena.DOWN;
								return BattleBotArena.FIREUP;
							} else if (me.getY() < closestEnemy.getY()) {
								move = BattleBotArena.UP;
								return BattleBotArena.FIREDOWN;
							}
						}

						if (Math.abs(me.getY() - closestEnemy.getY()) <= Bot.RADIUS * 3) {
							if (me.getX() > closestEnemy.getX()) {
								move = BattleBotArena.RIGHT;
								return BattleBotArena.FIRELEFT;
							} else if (me.getX() < closestEnemy.getX()) {
								move = BattleBotArena.LEFT;
								return BattleBotArena.FIRERIGHT;
							}
						}
					}
				}

				// add speeds
				// check x and y speed of bullet and add it to its current location
				// if it gets closer, it means it goes into my direction
				if (bullets.length > 0) {
					if (Math.abs(me.getX() - closestDanger.getX()) < Bot.RADIUS * 3
							&& me.getY() - closestDanger.getY() <= 50) {
						if (me.getX() > closestDanger.getX()) {
							move = BattleBotArena.RIGHT;
						} else if (me.getX() < closestDanger.getX()) {
							move = BattleBotArena.LEFT;
						}
						if (Math.abs(me.getX() - BattleBotArena.RIGHT_EDGE) <= 100) {
							move = BattleBotArena.LEFT;
						}
					} else if (Math.abs(me.getX() - closestDanger.getX()) < Bot.RADIUS * 3
							&& me.getY() - closestDanger.getY() >= -50) {
//					move = BattleBotArena.LEFT;
//					current = left;
						if (me.getX() > closestDanger.getX()) {
							move = BattleBotArena.RIGHT;
						} else if (me.getX() < closestDanger.getX()) {
							move = BattleBotArena.LEFT;
						}
						if (Math.abs(me.getX() - BattleBotArena.LEFT_EDGE) <= 100) {
							move = BattleBotArena.RIGHT;
						}
					} else if (Math.abs(me.getY() - closestDanger.getY()) < Bot.RADIUS * 3
							&& me.getX() - closestDanger.getX() <= 50) {// when my bot is right of bullet
						if (me.getX() > closestDanger.getX()) {
							move = BattleBotArena.RIGHT;
						} else if (me.getX() < closestDanger.getX()) {
							move = BattleBotArena.LEFT;
						}

						if (Math.abs(me.getY() - BattleBotArena.TOP_EDGE) <= 100) {
							move = BattleBotArena.DOWN;
						}
					} else if (Math.abs(me.getY() - closestDanger.getY()) < Bot.RADIUS * 3
							&& me.getX() - closestDanger.getX() >= -50) {// when my bot is left of bullet
						if (me.getY() > closestDanger.getY()) {
							move = BattleBotArena.DOWN;
						} else if (me.getY() < closestDanger.getY()) {
							move = BattleBotArena.UP;
						}
						if (Math.abs(me.getY() - BattleBotArena.BOTTOM_EDGE) <= 100) {
							move = BattleBotArena.UP;
						}
					} else {
						state = botState.ATTACKING;
					}
				}
			}

			if (state == botState.SCAVENGING) {
				if (deadBots.length != 0) {
					// finds the closes dead bot
					// if there is only 1 bullet left the bot will stop all other actions and go to
					// the closest dead bot

					// these booleans stop all movement as explained previously
					// the movement code to move to the closest dead bot

					double dX = closestDead.getX() - me.getX() + Bot.RADIUS;
					double dY = closestDead.getY() - me.getY() + Bot.RADIUS;
					double aDX = Math.abs(dX);
					double aDY = Math.abs(dY);

					if (aDX > aDY) {
						if (dX < 0) {
							return BattleBotArena.LEFT;
						} else if (dX > 0) {
							return BattleBotArena.RIGHT;
						}
					} else if (aDY > aDX) {
						if (dY > 0) {
							return BattleBotArena.DOWN;
						} else if (dY < 0) {
							return BattleBotArena.UP;
						}
					} else if (dX > 0) {
						return BattleBotArena.UP;
					} else {
						return BattleBotArena.DOWN;
					}

				}
			}
			if (state == botState.ATTACKING) {
				for (BotInfo b : liveBots) {

					if (b.getName() != "BroProGuoBot" && b.getName() != "TalBot") {

						double dX = b.getX() - me.getX() + Bot.RADIUS; // difference in x component between my
																		// bot and the closest enemy
						double dY = b.getY() - me.getY() + Bot.RADIUS; // difference in y component between my
																		// bot and the closest enemy

						double aDX = Math.abs(dX); // absolute value of the difference in x component between my bot and
													// the
													// closest enemy
						double aDY = Math.abs(dY); // absolute value of the difference in y component between my bot and
													// the
													// closest enemy

						int tEnemyX = (int) (aDX / BattleBotArena.BOT_SPEED); // time taken for enemy bot to line up to
																				// our
																				// x component (above or below our bot)
						int tBulletY = (int) (aDY / BattleBotArena.BULLET_SPEED); // time take for the bullet to reach
																					// the
																					// enemy bot's y value (above or
																					// below
																					// our bot)

						// not working as of 8:06 PM, May 6, 2019
						int tEnemyY = (int) (aDY / BattleBotArena.BOT_SPEED); // time taken for enemy bot to line up to
																				// our
																				// y component (right or left of our
																				// bot)
						int tBulletX = (int) (aDX / BattleBotArena.BULLET_SPEED); // time take for the bullet to reach
																					// the
																					// enemy bot's x value (right or
																					// left of
																					// our bot)
						
						
						// makes an array of b, scans all the bots instead of just the closest bot

//			            if (liveBots.length > 0 && closestEnemy.getTeamName() != me.getTeamName()) {
//			                
//			                if (Math.abs(me.getX() - closestEnemy.getX()) < Bot.RADIUS * 2
//			                        && me.getY() - closestEnemy.getY() <= BattleBotArena.RIGHT_EDGE / 2) {
//			                    return BattleBotArena.FIREUP;
//			                }
//			                if (Math.abs(me.getX() - closestEnemy.getX()) < Bot.RADIUS * 2
//			                        && me.getY() - closestEnemy.getY() >= BattleBotArena.BOTTOM_EDGE / 2) {
//			                    return BattleBotArena.FIREDOWN;
//			                }
//			                if (Math.abs(me.getY() - closestEnemy.getY()) < Bot.RADIUS * 2
//			                        && me.getX() - closestEnemy.getX() <= 400) {
//			                    return BattleBotArena.FIRELEFT;
//			                }
//			                if (Math.abs(me.getY() - closestEnemy.getY()) < Bot.RADIUS * 2
//			                        && me.getX() - closestEnemy.getX() >= -400) {
//			                    return BattleBotArena.FIRERIGHT;
//			                }
//			            } else {
//			                state = botState.DODGING;
//			            }

						// printing out variables
						System.out.println("tEnemyX: " + tEnemyX);
						System.out.println("tBulletY: " + tBulletY);
						// System.out.println(counter);

						for (BotInfo c : deadBots) {
							double tombDX = c.getX() - me.getX() + Bot.RADIUS;
							double tombDY = c.getY() - me.getY() + Bot.RADIUS;
							
							
							if (tEnemyX <= tBulletY) {

								if (dY < 0) {// if enemy is above our bot
									if (tombDY < dY && Math.abs(tombDX) > Bot.RADIUS) {
									return BattleBotArena.FIREUP;
									}
								}
								if (dY > 0) {// if enemy is below our bot
									if (tombDY > dY && Math.abs(tombDX) > Bot.RADIUS) {
									return BattleBotArena.FIREDOWN;
									}
								}

							}

							if (tEnemyY <= tBulletX) {
								if (dX < 0) {// if enemy is to the left of our bot
									if (tombDX < dX && Math.abs(tombDY) > Bot.RADIUS) {
									return BattleBotArena.FIRELEFT;
									}
								}
								if (dX > 0) {// if enemy is to the right of our bot
									if (tombDY > dY && Math.abs(tombDX) > Bot.RADIUS) {
									return BattleBotArena.FIRERIGHT;
									}
								}
							}
						}
					}

				}
			}
			if (move == BattleBotArena.UP || move == BattleBotArena.FIREUP)
				current = up;
			else if (move == BattleBotArena.DOWN || move == BattleBotArena.FIREDOWN)
				current = down;
			else if (move == BattleBotArena.LEFT || move == BattleBotArena.FIRELEFT)
				current = left;
			else if (move == BattleBotArena.RIGHT || move == BattleBotArena.FIRERIGHT)
				current = right;
		}
		return move;

	}

	@Override
	public void draw(Graphics g, int x, int y) {
		g.drawImage(current, x, y, Bot.RADIUS*2, Bot.RADIUS*2, null);
	}
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "SwatchBot";
	}

	@Override
	public String getTeamName() {
		// TODO Auto-generated method stub
		return "Unity";
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
	public String[] imageNames()
	{
		String[] images = {"bluejaysup.png","bluejaysdown.png","bluejaysleft.png","bluejaysright.png"};
		return images;
	}

	/**
	 * Store the loaded images
	 */
	public void loadedImages(Image[] images)
	{
		if (images != null)
		{
			current = up = images[0];
			down = images[1];
			left = images[2];
			right = images[3];
		}
	}
}
