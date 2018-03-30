package com.legacy.server.plugins.quests.members.watchtower;

import static com.legacy.server.plugins.Functions.*;

import com.legacy.server.Constants;
import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.plugins.QuestInterface;
import com.legacy.server.plugins.listeners.action.TalkToNpcListener;
import com.legacy.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.legacy.server.util.rsc.DataConversions;
/**
 * 
 * @author Imposter/Fate
 *
 */
public class WatchTowerDialogues implements QuestInterface, TalkToNpcListener, TalkToNpcExecutiveListener {

	/** 
	 * REMEMBER: 
	 * Ogre Og = Want coins.
	 * Ogre Grew = Want Tooth.
	 * **/


	/** NPC IDs **/
	public static int WATCHTOWER_WIZARD = 672;
	public static int GREW = 681;
	public static int OG = 680;
	public static int TOBAN = 682;
	public static int OGRE_CITIZEN = 704;
	public static int ATTACKABLE_GEM_TRADER = 689;
	public static int OGRE_ENCLAVE_GUARDS = 684;
	public static int OGRE_TRADER_ROCK_CAKE = 688;
	public static int CITY_GUARD = 691;

	//LAST SKAVID FOR LEARNED LANGUAGE
	public static int SKAVID_679 = 679;

	// SKAVID FOR STARTING LANGUAGE
	public static int SKAVID_678 = 678;

	public static int SKAVID_696 = 696;
	public static int SKAVID_707 = 707;
	public static int SKAVID_708 = 708;
	public static int SKAVID_709 = 709;

	/** ITEM IDs **/
	public static int KEY = 1047;
	public static int DRAGON_BONE = 814;
	public static int OGRE_RELIC_PART = 1050;
	public static int OGRE_RELIC_PART_2 = 1049;
	public static int OGRE_RELIC_PART_3 = 1048;
	public static int OGRE_RELIC_COMPLETE = 1044;
	public static int OGRE_TOOTH = 1043;
	public static int POWERING_CRYSTAL1 = 1037;
	public static int POWERING_CRYSTAL2 = 1152;
	public static int STOLEN_GOLD = 1040;
	public static int SKAVID_MAP = 1045;


	class WatchTowerWizard {
		public static final int SEARCHINGTHECAVES = 0;
	}

	@Override
	public int getQuestId() {
		return Constants.Quests.WATCHTOWER;
	}

	@Override
	public String getQuestName() {
		return "Watchtower (members)";
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player player) {
		//NOT USED NO NEED.
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		if(n.getID() == WATCHTOWER_WIZARD) {
			return true;
		}
		if(n.getID() == GREW || n.getID() == OG || n.getID() == TOBAN) {
			return true;
		}
		if(n.getID() == OGRE_CITIZEN || n.getID() == ATTACKABLE_GEM_TRADER || n.getID() == OGRE_ENCLAVE_GUARDS || n.getID() == OGRE_TRADER_ROCK_CAKE || n.getID() == CITY_GUARD) {
			return true;
		}
		if(n.getID() == SKAVID_679 || n.getID() == SKAVID_696 || n.getID() == SKAVID_707 || n.getID() == SKAVID_708 || n.getID() == SKAVID_709 || n.getID() == SKAVID_678) {
			return true;
		}
		return false;
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if(n.getID() == SKAVID_679) {
			if(p.getCache().hasKey("skavid_completed_language") || p.getQuestStage(Constants.Quests.WATCHTOWER) == -1) {
				npcTalk(p, n, "What, you gots the crystal...");
				int lastMenu = showMenu(p, n, "But I've lost it!", "Oh okay then");
				if(lastMenu == 0) {
					if(hasItem(p, POWERING_CRYSTAL2) || p.getQuestStage(Constants.Quests.WATCHTOWER) == -1) {
						npcTalk(p, n, "I have no more for you!");
					} else {
						npcTalk(p, n, "All right, take this one then...");
						p.message("The skavid gives you a crystal");
						addItem(p, POWERING_CRYSTAL2, 1);
					}
				} else if(lastMenu == 1) {
					npcTalk(p, n, "I'll be on my way then");
				}
			}
			else if(p.getCache().hasKey("language_cur") 
					&& p.getCache().hasKey("language_ar") 
					&& p.getCache().hasKey("language_ig") 
					&& p.getCache().hasKey("language_nod")) {
				String[] sayChat = { "Cur tanath...", "Ar cur...", "Bidith Ig...", "Gor nod..." };
				int randomizeChat = DataConversions.random(0, sayChat.length - 1);
				npcTalk(p, n, sayChat[randomizeChat]);
				int menu = showMenu(p, n, "Cur", "Ar", "Bidith", "Tanath", "Gor");
				boolean correctAnswer = false;
				if(menu == 0) {
					if(randomizeChat == 2) 
						correctAnswer = true;
				} else if(menu == 2) {
					if(randomizeChat == 0) 
						correctAnswer = true;
				} else if(menu == 3) {
					if(randomizeChat == 3) 
						correctAnswer = true;
				} else if(menu == 4) {
					if(randomizeChat == 1) 
						correctAnswer = true;
				}
				if(menu != -1) {
					if(correctAnswer) {
						npcTalk(p, n, "Heh-heh! So you speak a little skavid eh?",
								"I'm impressed, here take this prize...");
						p.message("The skavid gives you a large crystal");
						addItem(p, POWERING_CRYSTAL2, 1);
						if(p.getCache().hasKey("language_cur") 
								&& p.getCache().hasKey("language_ar") 
								&& p.getCache().hasKey("language_ig") 
								&& p.getCache().hasKey("language_nod")) {
							p.getCache().remove("language_cur");
							p.getCache().remove("language_ar");
							p.getCache().remove("language_ig");
							p.getCache().remove("language_nod");
							p.getCache().remove("skavid_started_language");
							p.getCache().store("skavid_completed_language", true);
						}
					} else {
						npcTalk(p, n, "???");
						p.message("The response was wrong");
					}
				}
			} else {
				npcTalk(p, n, "Tanath Gor Ar Bidith ?");
				playerTalk(p, n, "???");
				p.message("You cannot communicate with the skavid");
				p.message("It seems you haven't learned enough of thier language yet...");
			}
		}
		if(inArray(n.getID(), SKAVID_696, SKAVID_707, SKAVID_708, SKAVID_709)) {
			if(n.getID() == SKAVID_696) {
				if(p.getCache().hasKey("language_ig") || p.getCache().hasKey("skavid_completed_language") || p.getQuestStage(Constants.Quests.WATCHTOWER) == -1) {
					npcTalk(p, n, "Bidith Ig...");
					p.message("You have already talked to this skavid");
					return;
				}
				npcTalk(p, n, "Cur bidith...");
			} else if(n.getID() == SKAVID_707) {
				if(p.getCache().hasKey("language_ar") || p.getCache().hasKey("skavid_completed_language") || p.getQuestStage(Constants.Quests.WATCHTOWER) == -1) {
					npcTalk(p, n, "Ar cur...");
					p.message("You have already talked to this skavid");
					return;
				}
				npcTalk(p, n, "Gor cur...");
			} else if(n.getID() == SKAVID_708) {
				if(p.getCache().hasKey("language_cur") || p.getCache().hasKey("skavid_completed_language") || p.getQuestStage(Constants.Quests.WATCHTOWER) == -1) {
					npcTalk(p, n, "Cur tanath...");
					p.message("You have already talked to this skavid");
					return;
				}
				npcTalk(p, n, "Bidith tanath...");
			} else if(n.getID() == SKAVID_709) {
				if(p.getCache().hasKey("language_nod") || p.getCache().hasKey("skavid_completed_language") || p.getQuestStage(Constants.Quests.WATCHTOWER) == -1) {
					npcTalk(p, n, "Gor nod...");
					p.message("You have already talked to this skavid");
					return;
				}
				npcTalk(p, n, "Tanath gor...");
			}
			if(p.getCache().hasKey("skavid_started_language")) {
				p.message("The skavid is trying to communicate...");
				boolean correctWord = false;
				int learnMenu = showMenu(p, n, "Cur", "Ar", "Ig", "Nod", "Gor");
				if(learnMenu == 0) {
					if(n.getID() == SKAVID_708) {
						npcTalk(p, n, "Cur",
								"Cur tanath");
						p.getCache().store("language_cur", true);
						correctWord = true;
					} 
				}
				else if(learnMenu == 1) {
					if(n.getID() == SKAVID_707) {
						npcTalk(p, n, "Ar",
								"Ar cur");
						p.getCache().store("language_ar", true);
						correctWord = true;
					} 
				}
				else if(learnMenu == 2) {
					if(n.getID() == SKAVID_696) {
						npcTalk(p, n, "Ig",
								"Bidith Ig");
						p.getCache().store("language_ig", true);
						correctWord = true;
					} 
				} 
				else if(learnMenu == 3) {
					if(n.getID() == SKAVID_709) {
						npcTalk(p, n, "Nod",
								"Gor nod");
						p.getCache().store("language_nod", true);
						correctWord = true;
					} 
				}

				if(learnMenu != -1) {
					if(correctWord) {
						p.message("It seems the skavid understood you");
					} else {
						npcTalk(p, n, "???");
						message(p, "It seems that was the wrong reply");
					}
				}
			} else {
				playerTalk(p, n, "???");
				p.message("The skavid is trying to communicate...");
				p.message("You don't know any skavid words yet!");
			}
		}

		if(n.getID() == SKAVID_678) {
			if(p.getQuestStage(Constants.Quests.WATCHTOWER) == -1) {
				npcTalk(p, n, "Ah master...",
						"You did well to master our language...");
				return;
			}
			if((p.getCache().hasKey("language_cur") 
					&& p.getCache().hasKey("language_ar") 
					&& p.getCache().hasKey("language_ig") 
					&& p.getCache().hasKey("language_nod")) || p.getCache().hasKey("skavid_completed_language")) {
				npcTalk(p, n, "Master, my kinsmen tell me you have learned skavid",
						"You should speak to the mad ones in their cave...");
				return;
			}
			else if(p.getCache().hasKey("skavid_started_language")) {
				npcTalk(p, n, "Master, how are you doing learning our language ?");
				playerTalk(p, n, "I am studying the speech of your kind...");
			} else {
				npcTalk(p, n, "Tanath cur, tanath cur");
				playerTalk(p, n, "???");
				npcTalk(p, n, "Don't hurt me, don't hurt me!");
				playerTalk(p, n, "Stop moaning creature",
						"I know about you skavids",
						"You serve those monsters the ogres");
				npcTalk(p, n, "Please dont touch me!");
				playerTalk(p, n, "You have something that belongs to me...");
				npcTalk(p, n, "I don't have anything, please believe me!");
				playerTalk(p, n, "Somehow I find your words hard to believe");
				npcTalk(p, n, "I'm begging your kindness, I don't have it!");
				int menu = showMenu(p, n,
						"I don't believe you hand it over!",
						"Okay okay i'm not going to hurt you");
				if(menu == 0) {
					npcTalk(p, n, "Ahhhhh, help!");
					p.message("The skavid runs away...");
					temporaryRemoveNpc(n);
					playerTalk(p, n, "Oh great...I've scared it off!");
				} else if(menu == 1) {
					npcTalk(p, n, "Thank you kind " + (p.isMale() ? "sir" : "madam"),
							"I'll tells you where that things you wants is...",
							"The mad skavids have it in their cave in the city",
							"You will have to learn skavid",
							"Otherwise they will not talks to you",
							"Make sure you remembers all that you hear",
							"Let me tells you the most common skavid words...",
							"Ar",
							"Nod",
							"Gor",
							"Ig",
							"Cur",
							"That will gets you started...");
					p.getCache().store("skavid_started_language", true);
					p.updateQuestStage(Constants.Quests.WATCHTOWER, 5);
				}
			}
		}

		if(n.getID() == WATCHTOWER_WIZARD) {
			watchtowerWizardDialogue(p, n, -1);
		}
		if(n.getID() == OGRE_CITIZEN) {
			npcTalk(p,n, "Uh ? what are you doing here ?");
		}
		if(n.getID() == ATTACKABLE_GEM_TRADER) {
			npcTalk(p,n, "Grrr, little animal.. I shall destroy you!");
			n.startCombat(p);
		}
		if(n.getID() == OGRE_ENCLAVE_GUARDS) {
			if(p.getQuestStage(Constants.Quests.WATCHTOWER) == -1) {
				npcTalk(p, n, "What do you want ?");
				int menu = showMenu(p, n,
						"I want to go in there",
						"I want to rid the world of ogres");
				if(menu == 0) {
					npcTalk(p, n, "Oh you do, do you ?",
							"How about no ?");
					n.startCombat(p);
				} else if(menu == 1) {
					npcTalk(p, n, "You dare mock me creature!!!");
					n.startCombat(p);
				}
			} else {
				npcTalk(p,n, "Stop bothering me minion!");
			}
		}
		if(n.getID() == OGRE_TRADER_ROCK_CAKE) {
			npcTalk(p,n, "Arr, small thing wants my food does it ?",
					"I'll teach you to deal with ogres!");
			n.startCombat(p);
		}
		if(n.getID() == CITY_GUARD) {
			if(p.getCache().hasKey("city_guard_riddle")) {
				npcTalk(p,n, "What is it ?");
				int menu = showMenu(p,n,
						"Do you have any other riddles for me ?",
						"I have lost the map you gave me");
				if(menu == 0) {
					npcTalk(p,n, "Yes, what looks good on a plate with salad ?");
					int subMenu = showMenu(p,n,
							"I don't know...",
							"A nice pizza ?");
					if(subMenu == 0) {
						npcTalk(p,n, "You!!!",
								"Now go and bother me no more...");
					} else if(subMenu == 1) {
						npcTalk(p,n, "Grr.. think you are a comedian eh ?",
								"Get lost!");
					}
				} else if(menu == 1) {
					if(hasItem(p, SKAVID_MAP)) {
						npcTalk(p,n, "Are you blind ? what is that you are carrying ?");
						playerTalk(p,n, "Oh, that map....");
					} else {
						npcTalk(p,n, "What's the point ? take this copy and bother me no more!");
						addItem(p, SKAVID_MAP, 1);
					}
				}
			} else {
				npcTalk(p,n, "Grrrr, what business have you here ?");
				playerTalk(p,n, "I am on an errand...");
				npcTalk(p,n, "So what do you want with me ?");
				int menu = showMenu(p, n, "I am an ogre killer come to destroy you!",
						"I seek passage into the skavid caves");
				if(menu == 0) {
					npcTalk(p,n, "Grrr, little animal.. I shall destroy you!");
					n.startCombat(p);
				} else if(menu == 1) {
					npcTalk(p,n, "Is that so...",
							"You humour me small thing, answer this riddle and I will help you...",
							"I want you to bring me an item",
							"I will give you all the letters of this item, you work out what it is...",
							"My first is in days, but not in years",
							"My second is in evil, and also in tears",
							"My third is in all, but not in none",
							"My fourth is in hot, but not in sun",
							"My fifth is in heaven, and also in hate",
							"My sixth is in fearing, but not in fate",
							"My seventh is in plush, but not in place",
							"My eighth is in nine, but not in eight",
							"My last is in earth, and also in in great",
							"My whole is an object, that magic will make",
							"It brings wrack and ruin to all in it's wake...",
							"Now how long I wonder, will this riddle take ?");
				}
			}
		}
		if(n.getID() == GREW) {
			switch(p.getQuestStage(this)) {
			case -1:
				p.message("The ogre is not interested in you anymore");
				break;
			case 0:
			case 1:
				p.message("The ogre has nothing to say at the moment...");
				break;
			case 2:
			case 3:
			case 4:
				if(p.getCache().hasKey("ogre_grew_p1")) {
					npcTalk(p,n, "What are you doing here morsel ?");
					int menu = showMenu(p,n,
							"Can I do anything else for you ?",
							"I've lost the relic part you gave me",
							"I've lost the crystal you gave me");
					if(menu == 0) {
						npcTalk(p,n, "I have nothing left for you but the cooking pot!");
					} else if(menu == 1) {
						if(!hasItem(p, OGRE_RELIC_PART_2)) {
							npcTalk(p,n, "Stupid morsel, I have another",
									"Take it and go now before I lose my temper");
							addItem(p, OGRE_RELIC_PART_2, 1);
						} else {
							npcTalk(p,n, "You lie to me morsel!");
						}
					} else if(menu == 2) {
						if(!hasItem(p, POWERING_CRYSTAL1)) {
							npcTalk(p,n, "I suppose you want another ?",
									"I suppose just this once I could give you my copy...");
							addItem(p, POWERING_CRYSTAL1, 1);
						} else {
							npcTalk(p,n, "How dare you lie to me Morsel!",
									"I will finish you now!");
						}
					}
				} else {
					if(p.getCache().hasKey("ogre_grew")) {
						npcTalk(p,n, "The morsel is back",
								"Does it have our tooth for us ?");
						if(hasItem(p, OGRE_TOOTH)) {
							playerTalk(p,n, "I have it");
							npcTalk(p,n, "It's got it, good good",
									"That should annoy gorad wonderfully",
									"Heheheheh!");
							removeItem(p, OGRE_TOOTH, 1);
							npcTalk(p,n, "Heres a token of my gratitude");
							addItem(p, OGRE_RELIC_PART_2, 1);
							npcTalk(p,n, "Some old gem I stole from Gorad...",
									"And an old part of a statue",
									"Heheheheh!");
							p.message("The ogre hands you a large crystal");
							p.message("The ogre gives you part of a statue");
							addItem(p, POWERING_CRYSTAL1, 1);
							p.getCache().remove("ogre_grew");
							p.getCache().store("ogre_grew_p1", true);
						} else {
							playerTalk(p,n, "Err, I don't have it");
							npcTalk(p,n, "Morsel, you dare to return without the tooth!",
									"Either you are a fool, or want to be eaten!");
						}
					} else {
						npcTalk(p,n, "What do you want tiny morsel ?",
								"You would look good on my plate");
						playerTalk(p,n, "I want to enter the city of ogres");
						npcTalk(p,n, "Perhaps I should eat you instead ?");
						int menu = showMenu(p,n,
								"Don't eat me, I can help you",
								"You will have to kill me first");
						if(menu == 0) {
							npcTalk(p,n, "What can a morsel like you do for me ?");
							playerTalk(p,n, "I am a mighty adventurer",
									"Slayer of monsters and user of magic powers");
							npcTalk(p,n, "Well well, perhaps the morsel can help after all...",
									"If you think you're tough",
									"Find Gorad my enemy in the south east settlement",
									"And knock one of his teeth out!",
									"Heheheheh!");
							p.getCache().store("ogre_grew", true);
						} else if(menu == 1) {
							npcTalk(p,n, "That can be arranged - guards!!");
							ogreSpawnAndAttack(p, n);
						}
					}
				}
				break;
			}
		}
		if(n.getID() == OG) {
			switch(p.getQuestStage(this)) {
			case -1:
				p.message("The ogre is not interested in you anymore");
				break;
			case 0:
			case 1:
				p.message("He's busy, try him later");
				break;
			case 2:
			case 3:
			case 4:
				if(p.getCache().hasKey("ogre_relic_part_3")) {
					npcTalk(p,n, "It's the little rat again");
					int menu = showMenu(p,n,
							"Do you have any other tasks for me ?",
							"I have lost the relic part you gave me");
					if(menu == 0) {
						npcTalk(p,n, "No, I have no more tasks for you, now go away");
					} else if(menu == 1) {
						if(!hasItem(p, OGRE_RELIC_PART_3)) {
							npcTalk(p,n, "Grrr, why do I bother ?",
									"It's a good job I have another part!");
							addItem(p, OGRE_RELIC_PART_3, 1);
						} else {
							npcTalk(p,n, "Are you blind! I can see you have it even from here!");
						}
					}
				} else {
					if(p.getCache().hasKey("ogre_og")) {
						npcTalk(p,n, "Where is my gold from that traitor toban?");
						int subMenu = showMenu(p,n,
								"I have your gold",
								"I haven't got it yet",
								"I have lost the key!");
						if(subMenu == 0) {
							if(hasItem(p, STOLEN_GOLD, 1)) {
								npcTalk(p,n, "Well well, the little rat has got it!",
										"take this to show the little rat is a friend to the ogres",
										"Hahahahaha!");
								removeItem(p, STOLEN_GOLD, 1);
								p.message("The ogre gives you part of a horrible statue");
								addItem(p, OGRE_RELIC_PART_3, 1);
								p.getCache().remove("ogre_og");
								/** Very strange setup of quest tbh, but that's what it is **/
								p.getCache().store("ogre_relic_part_3", true);
							} else {
								npcTalk(p,n, "That is not what I want rat!",
										"If you want to impress me",
										"Then get the gold I asked for!");
							}
						} else if(subMenu == 1) {
							npcTalk(p,n, "Don't come back until you have it",
									"Unless you want to be on tonight's menu!");
						} else if(subMenu == 2) {
							if(hasItem(p, KEY)) {
								npcTalk(p,n, "Oh yeah! what's that then ?");
								p.message("It seems you still have the key...");
							} else {
								npcTalk(p,n, "Idiot! take another and don't lose it!");
								addItem(p, KEY, 1);
							}
						}
					} else {
						npcTalk(p,n, "Why are you here little rat ?");
						int menu = showMenu(p,n,
								"I seek entrance to the city of ogres",
								"I have come to kill you");
						if(menu == 0) {
							npcTalk(p,n, "You have no business there!",
									"Just a minute...maybe if you did something for me I might help you get in...");
							playerTalk(p,n, "What can I do to help an ogre ?");
							npcTalk(p,n, "South East of here there is another settlement",
									"The name of the chieftan is Toban",
									"He stole some gold from me",
									"And I want it back!",
									"Here is a key to the chest it's in",
									"If you bring it here",
									"I may reward you...");
							addItem(p, KEY, 1);
							p.getCache().store("ogre_og", true);
						} else if(menu == 1) {
							npcTalk(p,n, "Kill me eh ?",
									"you shall be crushed like the vermin you are!",
									"Guards!!");
							ogreSpawnAndAttack(p, n);
						}
					}
				}
				break;
			}
		}
		if(n.getID() == TOBAN) {
			if(p.getQuestStage(Constants.Quests.WATCHTOWER) == -1) {
				p.message("The ogre is not interested in you anymore");
				return;
			}
			if(p.getCache().hasKey("ogre_relic_part_1")) {
				npcTalk(p,n, "The small thing returns, what do you want now ?");
				int subMenu = showMenu(p,n,
						"I seek another task",
						"I can't find the relic part you gave me");
				if(subMenu == 0) {
					npcTalk(p,n, "Have you arrived for dinner ?",
							"Ha ha ha! begone small thing!");
				} else if(subMenu == 1) {
					if(!hasItem(p, OGRE_RELIC_PART)) {
						npcTalk(p,n, "Small thing, how could you be so careless ?",
								"Here, take this one");
						addItem(p, OGRE_RELIC_PART, 1);
					} else {
						npcTalk(p,n, "Small thing, you lie to me!",
								"I always says that small things are big trouble...");
					}
				}
			} else {
				if(p.getCache().hasKey("ogre_toban")) {
					npcTalk(p,n, "Ha ha ha! small thing returns",
							"Did you bring the dragon bone ?");
					if(hasItem(p, DRAGON_BONE)) {
						playerTalk(p,n, "When I say I will get something I get it!");
						removeItem(p, DRAGON_BONE, 1);
						npcTalk(p,n, "Ha ha ha! small thing has done it",
								"Toban is glad, take this...");
						p.message("The ogre gives you part of a statue");
						addItem(p, OGRE_RELIC_PART, 1);
						p.getCache().remove("ogre_toban");
						p.getCache().store("ogre_relic_part_1", true);
					} else {
						playerTalk(p,n, "I have nothing for you");
						npcTalk(p,n, "Then you shall get nothing from me!");
					}
				} else {
					if(p.getCache().hasKey("ogre_og")) {
						npcTalk(p,n, "What do you want small thing ?");
						int menu = showMenu(p,n,
								"I seek entrance to the city of ogres",
								"Die creature");
						if(menu == 0) {
							npcTalk(p,n, "Ha ha ha! you'll never get in there");
							playerTalk(p,n, "I fear not for that city");
							npcTalk(p,n, "Bold words for a thing so small");
							int subMenu = showMenu(p,n,
									"I could do something for you...",
									"Die creature");
							if(subMenu == 0) {
								npcTalk(p,n, "Ha ha ha! this creature thinks it can help me!",
										"I would eat you now, but for your puny size",
										"Prove to me your might",
										"Bring me the bones of a dragon to chew on",
										"And I may spare you from a painful death");
								p.getCache().store("ogre_toban", true);
							} else if(subMenu == 1) {
								npcTalk(p,n, "Ha ha ha! it thinks it's a match for toban does it ?");
								n.startCombat(p);
							}
						} else if(menu == 1) {
							npcTalk(p,n, "Ha ha ha! it thinks it's a match for toban does it ?");
							n.startCombat(p);
						}
					} else {
						p.message("He is busy at the moment...");
					}
				}
			}
		}
	}
	private void watchtowerWizardDialogue(Player p, Npc n, int cID) {
		if(n.getID() == WATCHTOWER_WIZARD) {
			if(cID == -1) {
				switch(p.getQuestStage(this)) {
				case -1:
					// TODO dialogue for when learned the spell.
					npcTalk(p, n, "Hello again adventurer",
							"Thanks again for your help in keeping us safe");
					int finish = showMenu(p, n,
							"I lost the scroll you gave me",
							"That's okay");
					if(finish == 0) {
						if(hasItem(p, 1181)) {
							npcTalk(p, n, "Ho ho ho! a comedian to the finish!",
									"There it is, in your backpack!");
						} else {
							npcTalk(p, n, "Never mind, have another...");
							addItem(p, 1181, 1);
						}
					} else if(finish == 1) {
						npcTalk(p, n, "We are always in your debt...");
					}
					break;
				case 0:
					npcTalk(p,n, "Oh my Oh my!");
					int menu = showMenu(p,n,
							"What's the matter ?",
							"You wizards are always complaining");
					if(menu == 0) {
						npcTalk(p,n, "Oh dear oh dear",
								"Darn and drat",
								"We try hard to keep this town protected",
								"But how can we do that when the watchtower isn't working ?");
						playerTalk(p,n, "What do you mean it isn't working ?");
						npcTalk(p,n, "The watchtower here works by the power of a magical device",
								"An ancient spell designed to ward off ogres",
								"That has been in place here for many moons",
								"The exact knowledge of the spell is lost to us now",
								"But the essence of the spell",
								"Has been infused into 4 powering crystals",
								"To keep the tower protected from the hordes in the mendips...");
						int menu2 = showMenu(p,n,
								"So how come the spell dosen't work ?",
								"I'm not interested in the rantings of an old wizard");
						if(menu2 == 0) {
							npcTalk(p,n, "The crystals! the crystals!",
									"They have been taken!");
							playerTalk(p,n, "Taken...");
							npcTalk(p,n, "Stolen!");
							playerTalk(p,n, "Stolen...");
							npcTalk(p,n, "Yes, yes! do I have to repeat myself ?");
							p.message("The wizard seems very stressed...");
							int menu3 = showMenu(p,n,
									"Can I be of help ?",
									"I'm not sure I can help",
									"I'm not interested");
							if(menu3 == 0) {
								npcTalk(p,n, "Help ?",
										"Oh wonderful dear traveller",
										"Yes I could do with an extra pair of eyes here");
								playerTalk(p,n, "???");
								npcTalk(p,n, "There must be some evidence of what has happened somewhere",
										"Perhaps you could assist me in searching for clues");
								playerTalk(p,n, "I would be happy to");
								npcTalk(p,n, "Try searching the surrounding area");
								/** QUEST START - STAGE 1 **/
								p.updateQuestStage(this, 1);
							} else if(menu3 == 1) {
								npcTalk(p,n, "Oh dear what am I to do ?",
										"The safety of this whole area is in jeopardy!");
							} else if(menu3 == 2) {
								npcTalk(p,n, "That's typical nowadays",
										"Its left to us wizards to do all the work...");
								p.message("The wizard is not impressed");
							}
						} else if(menu2 == 1) {
							p.message("The wizard gives you a suspicious look");
						}
					} else if(menu == 1) {
						npcTalk(p,n, "Complaining ?.... complaining !",
								"What folks these days don't realize",
								"Is that if it wasn't for us wizards",
								"This entire world would be overrun",
								"With every creature that walks this world!");
						p.message("The wizard angrily walks away");
					}
					break;
				case 1:
					npcTalk(p,n, "Hello again",
							"Did you find anything of interest ?");
					if(hasItem(p, WatchTowerObstacles.FINGERNAILS)) {
						playerTalk(p,n, "Have a look at these");
						removeItem(p, WatchTowerObstacles.FINGERNAILS, 1);
						npcTalk(p,n, "Interesting, very interesting",
								"Long nails...grey in colour",
								"Well chewed...",
								"Of course, they belong to a skavid");
						playerTalk(p,n, "A skavid ?");
						npcTalk(p,n, "A servant race to the ogres",
								"Gray depressed looking creatures",
								"Always loosing nails, teeth and hair",
								"They inhabit the caves in the mendip hills",
								"They normally keep to themselves though",
								"It's unusual for them to venture from their caves");
						int m = showMenu(p,n,
								"What do you suggest that I do ?",
								"Shall I search the caves ?");
						if(m == 0) {
							watchtowerWizardDialogue(p, n, WatchTowerWizard.SEARCHINGTHECAVES);
						} else if(m == 1) {
							watchtowerWizardDialogue(p, n, WatchTowerWizard.SEARCHINGTHECAVES);
						}
					} else {
						playerTalk(p,n, "No nothing yet");
						npcTalk(p,n, "Oh dear oh dear",
								"There must be something somewhere");
					}
					break;
				case 2:
					npcTalk(p,n, "How's it going ?");
					int newM = showMenu(p,n,
							"I am having difficulty with the tribes",
							"I have everything under control",
							"I have lost something the ogres gave to me");
					if(newM == 0) {
						npcTalk(p,n, "Talk to them face to face",
								"And don't show any fear",
								"Make sure you are rested and well-fed",
								"And fight the good fight!");
					} else if(newM == 1) {
						npcTalk(p,n, "Good, good! I will expect the crystals back shortly then...");
					} else if(newM == 2) {
						npcTalk(p,n, "Oh deary me!",
								"Well there's nothing I can do about it",
								"You will have to go back to them i'm afraid");
					}
					break;
				case 3:
					npcTalk(p,n, "Ah the warrior returns",
							"Have you found a way into Gu'Tanoth yet ?");
					playerTalk(p,n, "I can't get past the guards");
					npcTalk(p,n, "Well, ogres dislike others apart from their kind",
							"What you need is some form of proof of friendship",
							"Something to trick them into believing you are their friend",
							"...Which shouldn't be too hard considering their intelligence!");
					if(!hasItem(p, OGRE_RELIC_COMPLETE)) {
						int lostRelicMenu = showMenu(p,n,
								"I have lost the relic you gave me",
								"I will find my way in, no problem");
						if(lostRelicMenu == 0) {
							npcTalk(p,n, "What! lost the relic ? How careless!",
									"It's a good job I copied that design then...",
									"You can take this copy instead, its just as good");
							addItem(p, OGRE_RELIC_COMPLETE, 1);
						} else if(lostRelicMenu == 1) {
							npcTalk(p,n, "Yes, I'm sure you will...good luck");
						}
					}
					break;
				case 4:
					npcTalk(p,n, "How is the quest going ?");
					playerTalk(p,n, "I have worked out the guard's puzzle");
					npcTalk(p,n, "My my! a wordsmith as well as a hero!");
					int mymyMenu = showMenu(p,n,
							"I am still trying to navigate the skavid caves",
							"I am trying to get into the shaman's cave",
							"It is going well");
					if(mymyMenu == 0) {
						npcTalk(p,n, "Take some illumination with you or else it will be dark!");
					} else if(mymyMenu == 1) {
						npcTalk(p,n, "Yes it will be well-guarded",
								"Hmmm, let me see...",
								"Ah yes, I gather some ogres are allergic to certain herbs...",
								"Now what was it ?",
								"It had white berries and blue leaves.... I remember that!",
								"You should try looking through some of the caves...");
					} else if(mymyMenu == 2) {
						npcTalk(p,n, "Thats good to hear",
								"We are much closer to fixing the tower now");
					}
					break;
				case 5:
					npcTalk(p, n, "Hello again, how do you fare?");
					int questMenu5 = showMenu(p, n,
							"It goes well, I can now navigate the skavid caves",
							"I had a crystal but I lost it",
							"I am now ready for the shaman");
					if(questMenu5 == 0) {
						npcTalk(p, n, "That is good news",
								"Let me know if you find anything of interest...");
					} else if(questMenu5 == 1) {
						playerTalk(p, n, "I had a crystal, but I lost it");
						npcTalk(p, n, "Oh no, well you had better go back there again then!");
					} else if(questMenu5 == 2) {
						npcTalk(p, n, "Remember all I told you, you must distract the guard somehow",
								"The herbs with blue leaves and berries is what you are looking for",
								"This herb is very poisonous however, handle it carefully",
								"Also, be on your guard in that cave",
								"Who know what monsters may be present in that awful place");

					}
					break;
				case 6:
					playerTalk(p, n, "I have found the cave of ogre shaman",
							"But I cannot touch them!");
					npcTalk(p, n, "That is because of their magical powers",
							"We must fight them with their own methods",
							"Do not speak to them!",
							"I suggest a potion...",
							"Collect some guam leaves",
							"and some jangerberries",
							"And mix in some ground bat bones",
							"It is essential to return it to me before you use it",
							"So I can empower it with my magic",
							"Be very careful how you mix it, its extremely volatile",
							"Mixing ingredients of this type in the wrong order can cause explosions!",
							"I hope you've been brushing up in herblaw and magic ?",
							"I must warn you that only experienced magicians can use this potion",
							"It is too dangerous in the hands of the unskilled...");
					p.updateQuestStage(this, 7);
					break;
				case 7:
					npcTalk(p, n, "Any more news ?");
					if(hasItem(p, 1053)) {
						playerTalk(p, n, "Yes I have made the potion");
						npcTalk(p, n, "That's great news, let me infuse it with magic...");
						p.message("The wizard mutters strange words over the liquid");
						removeItem(p, 1053, 1);
						addItem(p, 1054, 1);
						if(p.getQuestStage(Constants.Quests.WATCHTOWER) == 7) {
							p.updateQuestStage(Constants.Quests.WATCHTOWER, 8);
						}
						npcTalk(p, n, "Here it is, a dangerous substance",
								"I must remind you that this potion can only be used",
								"If your magic ability is high enough");
					} else {
						playerTalk(p, n, "Can you tell me again what I need for the potion ?");
						npcTalk(p, n, "Yes indeed, you need some guam leaves,",
								"Jangerberries and ground bat bones",
								"Then the potion can be powered with magic",
								"And the ogre shaman can be destroyed");
					}
					break;
				case 8:
					npcTalk(p, n, "Hello again",
							"Did the potion work ?");
					playerTalk(p, n, "I am still working to rid us of these shaman...");
					npcTalk(p, n, "May you have sucess in your task");
					int qMenu = showMenu(p, n,
							"I had another crystal but I lost it",
							"I am looking for another crystal",
							"I have found another crystal!");
					if(qMenu == 0) {
						npcTalk(p, n, "Oh really ?",
								"It's probably been dropped in the shaman cave",
								"Go and have a good search that area again");
					} else if(qMenu == 1) {
						npcTalk(p, n, "I am sure the cave holds the final one",
								"Look for the source of the shaman power...");
						playerTalk(p, n, "Okay I will go and have a look");
					} else if(qMenu == 2) {
						npcTalk(p, n, "Good, let's have it here...");
					}
					break;
				case 9:
					npcTalk(p, n, "Hello again",
							"Did the potion work ?");
					playerTalk(p, n, "Indeed it did!",
							"I wiped out those ogre shaman!",
							"I am looking for another crystal");
					npcTalk(p, n, "I am sure the cave holds the final one",
							"Look for the source of the shaman power...");
					playerTalk(p, n, "Okay I will go and have a look");
					break;
				}
			} switch(cID) {
			case WatchTowerWizard.SEARCHINGTHECAVES:
				npcTalk(p,n, "It's no good searching the caves",
						"Well, not yet anyway");
				playerTalk(p,n, "Why not ?");
				npcTalk(p,n, "They are deep and complex",
						"The only way you will navigate the caves is to have a map or something",
						"It may be that the ogres have one");
				playerTalk(p,n, "And how do you know that ?");
				npcTalk(p,n, "Well... I don't");
				int m2 = showMenu(p,n,
						"So what do I do ?",
						"I wont bother then");
				if(m2 == 0) {
					npcTalk(p,n, "You need to be fearless",
							"And gain entrance to Gu'Tanoth the city of ogres",
							"And find out how to navigate the caves");
					playerTalk(p, n, "That sounds scary");
					npcTalk(p, n, "Ogres are nasty creatures yes",
							"Only a strong warrior, and a clever one at that",
							"Can get the better of the ogres...");
					playerTalk(p, n, "What do I need to do to get into the city");
					npcTalk(p, n, "Well the guards need to be dealt with",
							"You could start by checking out the ogre settleements around here",
							"Tribal ogres often hate their neighbours...");
					p.updateQuestStage(this, 2);
				} else if(m2 == 1) {
					npcTalk(p,n, "Won't bother, won't bother ?",
							"...Perhaps this quest is too hard for you");
					p.message("The wizard walks away");
				}
				break;
			}
		}
	}
	private void ogreSpawnAndAttack(Player p, Npc n) {
		spawnNpc(706, p.getX(), p.getY(), 60000 * 3);
		sleep(1600);
		Npc ogre = getNearestNpc(p, 706, 4);
		if(ogre != null) {
			ogre.startCombat(p);
		}
	}
}
