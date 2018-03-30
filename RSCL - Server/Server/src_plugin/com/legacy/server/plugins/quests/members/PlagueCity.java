package com.legacy.server.plugins.quests.members;

import static com.legacy.server.plugins.Functions.addItem;
import static com.legacy.server.plugins.Functions.doGate;
import static com.legacy.server.plugins.Functions.getNearestNpc;
import static com.legacy.server.plugins.Functions.hasItem;
import static com.legacy.server.plugins.Functions.message;
import static com.legacy.server.plugins.Functions.npcTalk;
import static com.legacy.server.plugins.Functions.playerTalk;
import static com.legacy.server.plugins.Functions.removeItem;
import static com.legacy.server.plugins.Functions.showMenu;

import com.legacy.server.Constants;
import com.legacy.server.model.container.Item;
import com.legacy.server.model.entity.GameObject;
import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.model.world.World;
import com.legacy.server.plugins.QuestInterface;
import com.legacy.server.plugins.listeners.action.InvUseOnObjectListener;
import com.legacy.server.plugins.listeners.action.ObjectActionListener;
import com.legacy.server.plugins.listeners.action.TalkToNpcListener;
import com.legacy.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.legacy.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.legacy.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public class PlagueCity implements QuestInterface,TalkToNpcListener,
TalkToNpcExecutiveListener, InvUseOnObjectListener,
InvUseOnObjectExecutiveListener, ObjectActionListener,
ObjectActionExecutiveListener {

	@Override
	public int getQuestId() {
		return Constants.Quests.PLAGUE_CITY;
	}

	@Override
	public String getQuestName() {
		return "Plague City (members)";
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player p) {
		p.message("Well done you have completed the plague city quest");
		p.incQuestExp(14, (75 * p.getSkills().getMaxStat(14)) + 175);
		p.incQuestPoints(1);
		p.message("@gre@You have gained 1 quest point!");

	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		if (n.getID() == 437) {
			return true;
		}
		if (n.getID() == 450) {
			return true;
		}
		if (n.getID() == 443) {
			return true;
		}
		if (n.getID() == 446 || n.getID() == 447) {
			return true;
		}
		if (n.getID() == 449) {
			return true;
		}
		if (n.getID() == 452) {
			return true;
		}
		if (n.getID() == 454) {
			return true;
		}
		if (n.getID() == 465) {
			return true;
		}
		return false;
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == 465) {
			if(p.getQuestStage(this) >= 11 || p.getQuestStage(this) == -1) {
				p.message("You have already rescued Elena");
				return;
			}
			playerTalk(p, n, "Hi, you're free to go",
					"Your kidnappers don't seem to be about right now");
			npcTalk(p, n, "Thank you, Being kidnapped was so inconvenient",
					"I was on my way back to East Ardougne with some samples",
					"I want to see if I can diagnose a cure for this plague");
			playerTalk(p, n,
					"Well you can leave via the manhole cover near the gate");
			npcTalk(p, n, "If you go and see my father",
					"I'll make sure he adequately rewards you");
			p.updateQuestStage(getQuestId(), 11);
		}
		if (n.getID() == 454) {
			switch (p.getQuestStage(this)) {
			case 8:
				npcTalk(p, n, "My head hurts", "I'll speak to you another day");
				int menu = showMenu(p, n, "This is really important though",
						"Ok goodbye");
				if (menu == 0) {
					npcTalk(p,
							n,
							"I can't possibly speak to you with my head spinning like this",
							"I went a bit heavy on the drink again last night",
							"curse my herbalist",
							"she made the best hang over cures",
							"Darn inconvenient of her catching the plague");
					int menu2 = showMenu(p, n, "Ok goodbye",
							"You shouldn't drink so much then",
							"Do you know what is in the cure?");
					if (menu2 == 0) {
						// nothing
					} else if (menu2 == 1) {
						npcTalk(p, n,
								"Well positions of responsibility are hard",
								"I need something to take my mind off things",
								"especially with the problems this place has");
						int menu3 = showMenu(p, n, "Ok goodbye",
								"Do you know what is in the cure?",
								"I don't think drink is the best solution");
						if (menu3 == 0) {
							// nothing
						} else if (menu3 == 1) {
							npcTalk(p, n, "Hmm let me think",
									"ouch - thinking not clever",
									"Ah here, she did scribble it down for me");
							p.message("Bravek hands you a tatty piece of paper");
							addItem(p, 781, 1);
							p.updateQuestStage(getQuestId(), 9);
						} else if (menu3 == 2) {
							npcTalk(p,
									n,
									"uurgh",
									"My head still hurts too much to think straight",
									"Oh for one of Trudi's hangover cures");
						}
					} else if (menu2 == 2) {
						npcTalk(p, n, "Hmm let me think",
								"ouch - thinking not clever",
								"Ah here, she did scribble it down for me");
						p.message("Bravek hands you a tatty piece of paper");
						addItem(p, 781, 1);
						p.updateQuestStage(getQuestId(), 9);
					}
				} else if (menu == 1) {
					// nothing
				}
				break;
			case 9:
				npcTalk(p, n, "uurgh",
						"My head still hurts too much to think straight",
						"Oh for one of Trudi's hangover cures");
				if (hasItem(p, 771)) {
					playerTalk(p, n, "Try this");
					message(p, "You give Bravek the hangover cure",
							"Bravek gulps down the foul looking liquid");
					removeItem(p, 771, 1);
					npcTalk(p,
							n,
							"grruurgh",
							"Ooh that's much better",
							"thanks that's the clearest my head has felt in a month",
							"Ah now what was it you wanted me to do for you?");
					p.updateQuestStage(getQuestId(), 10);
					playerTalk(p, n,
							"I need to rescue a kidnap victim called Elena",
							"She's being held in a plague house I need permission to enter");
					npcTalk(p, n,
							"Well the mourners deal with that sort of thing");
					int finale = showMenu(p, n, "Ok I'll go speak to them",
							"Is that all anyone says around here?",
							"They won't listen to me");
					if (finale == 0) {
						// NOTHING
					} else if (finale == 1) {
						npcTalk(p, n, "Well they know best about plague issues");
						int last2 = showMenu(
								p,
								n,
								"Don't you want to take an interest in it at all?",
								"They won't listen to me");
						if (last2 == 0) {
							npcTalk(p, n,
									"Nope I don't wish to take a deep interest in plagues");
							npcTalk(p, n, "That stuff is too scary for me");
							int last3 = showMenu(
									p,
									n,
									"I see why people say you're a weak leader",
									"Ok I'll talk to the mourners",
									"they won't listen to me");
							if (last3 == 0) {
								npcTalk(p,
										n,
										"bah people always criticise their leaders",
										"But delegating is the only way to lead",
										"I delegate all plague issues to the mourners");
								playerTalk(p, n,
										"this whole city is a plague issue");
							} else if (last3 == 1) {
								// NOTHING
							} else if (last3 == 2) {
								playerTalk(
										p,
										n,
										"They say I'm not properly equipped to go in the house",
										"Though I do have a very effective gas mask");
								npcTalk(p,
										n,
										"hmm well I guess they're not taking the issue of a kidnap seriously enough",
										"They do go a bit far sometimes",
										"I've heard of Elena, she has helped us a lot",
										"Ok I'll give you this warrant to enter the house");
								addItem(p, 775, 1);
							}
						}
					} else if (finale == 2) {
						playerTalk(
								p,
								n,
								"They say I'm not properly equipped to go in the house",
								"Though I do have a very effective gas mask");
						npcTalk(p,
								n,
								"hmm well I guess they're not taking the issue of a kidnap seriously enough",
								"They do go a bit far sometimes",
								"I've heard of Elena, she has helped us a lot",
								"Ok I'll give you this warrant to enter the house");
						addItem(p, 775, 1);
					}
				}
				break;
			case 10:
				npcTalk(p, n, "thanks again for the hangover cure");
				if (hasItem(p, 775) || p.getQuestStage(getQuestId()) == 11
						|| p.getQuestStage(getQuestId()) == -1) {
					playerTalk(p, n, "Not a problem, happy to help out");
					npcTalk(p, n, "I'm just having a little bit of whisky",
							"then I'll feel really good");
				} else {
					npcTalk(p, n,
							"Ah now what was it you wanted me to do for you?");
					playerTalk(p, n, "I need to rescue Elena",
							"She's now a kidnap victim",
							"She's being held in a plague house I need permission to enter");
					npcTalk(p, n,
							"Well the mourners deal with that sort of thing");
					int last = showMenu(p, n, "Ok I'll go speak to them",
							"Is that all anyone says around here?",
							"They won't listen to me");
					if (last == 0) {
						// NOTHING
					} else if (last == 1) {
						npcTalk(p, n, "Well they know best about plague issues");
						int last2 = showMenu(
								p,
								n,
								"Don't you want to take an interest in it at all?",
								"They won't listen to me");
						if (last2 == 0) {
							npcTalk(p, n,
									"Nope I don't wish to take a deep interest in plagues");
							npcTalk(p, n, "That stuff is too scary for me");
							int last3 = showMenu(
									p,
									n,
									"I see why people say you're a weak leader",
									"Ok I'll talk to the mourners",
									"they won't listen to me");
							if (last3 == 0) {
								npcTalk(p,
										n,
										"bah people always criticise their leaders",
										"But delegating is the only way to lead",
										"I delegate all plague issues to the mourners");
								playerTalk(p, n,
										"this whole city is a plague issue");
							} else if (last3 == 1) {
								// NOTHING
							} else if (last3 == 2) {
								playerTalk(
										p,
										n,
										"They say I'm not properly equipped to go in the house",
										"Though I do have a very effective gas mask");
								npcTalk(p,
										n,
										"hmm well I guess they're not taking the issue of a kidnap seriously enough",
										"They do go a bit far sometimes",
										"I've heard of Elena, she has helped us a lot",
										"Ok I'll give you this warrant to enter the house");
								addItem(p, 775, 1);
							}
						} else if (last2 == 1) {
							playerTalk(
									p,
									n,
									"They say I'm not properly equipped to go in the house",
									"Though I do have a very effective gas mask");
							npcTalk(p,
									n,
									"hmm well I guess they're not taking the issue of a kidnap seriously enough",
									"They do go a bit far sometimes",
									"I've heard of Elena, she has helped us a lot",
									"Ok I'll give you this warrant to enter the house");
							addItem(p, 775, 1);
						}
					} else if (last == 2) {
						playerTalk(
								p,
								n,
								"They say I'm not properly equipped to go in the house",
								"Though I do have a very effective gas mask");
						npcTalk(p,
								n,
								"hmm well I guess they're not taking the issue of a kidnap seriously enough",
								"They do go a bit far sometimes",
								"I've heard of Elena, she has helped us a lot",
								"Ok I'll give you this warrant to enter the house");
						addItem(p, 775, 1);
					}
				}
				break;
			}
		}
		if (n.getID() == 452) {
			switch (p.getQuestStage(this)) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 10:
			case 11:
				npcTalk(p, n,
						"Hello welcome to the civic office of west Ardougne",
						"How can I help you?");
				int menuMan = showMenu(p, n, "who is through that door?",
						"I'm just looking thanks");
				if (menuMan == 0) {
					npcTalk(p, n, "The city warder Bravek is in there");
					playerTalk(p, n, "Can i go in?");
					npcTalk(p, n, "He has asked not to be disturbed");
				} else if (menuMan == 1) {
					// nothing
				}
				break;
			case 8:
			case 9:
				npcTalk(p, n,
						"Hello welcome to the civic office of west Ardougne",
						"How can I help you?");
				int first = showMenu(p, n,
						"I need permission to enter a plague house",
						"who is through that door?", "I'm just looking thanks");
				if (first == 0) {
					npcTalk(p, n, "Rather you than me",
							"Well the mourners normally deal with that stuff",
							"You should speak to them",
							"Their headquarters are right near the city gate");
					int menuMenu = showMenu(
							p,
							n,
							"I'll try asking them then",
							"Surely you don't let them run everything for you?",
							"This is urgent though");
					if (menuMenu == 0) {
						// nothing
					} else if (menuMenu == 1) {
						npcTalk(p, n,
								"Well they do know what they're doing there",
								"If they did start doing something badly",
								"Bravek the city warder",
								"would have the power to override",
								"I can't see that happening though");
						playerTalk(p, n, "Can I speak to Bravek anyway?");
						npcTalk(p, n, "He has asked not to be disturbed");
						int third = showMenu(p, n, "This is urgent though",
								"Ok I will leave him alone");
						if (third == 0) {
							playerTalk(p, n, "Someone's been kidnapped",
									"and is being held in a plague house");
							npcTalk(p, n, "I'll see what I can do I suppose",
									"Mr Bravek there's a man here who really needs to speak to you");
							Npc bravek = getNearestNpc(p, 454, 15);
							npcTalk(p, bravek,
									"I suppose they can come in then",
									"If they keep it short");
							p.message("You go into the office");
							p.teleport(647, 585, false);
						} else if (third == 1) {
							// nothing
						}
					} else if (menuMenu == 2) {
						playerTalk(p, n, "Someone's been kidnapped",
								"and is being held in a plague house");
						npcTalk(p, n, "I'll see what I can do I suppose",
								"Mr Bravek there's a man here who really needs to speak to you");
						Npc bravek = getNearestNpc(p, 454, 15);
						npcTalk(p, bravek, "I suppose they can come in then",
								"If they keep it short");
						p.message("You go into the office");
						p.teleport(647, 585, false);
					}

				} else if (first == 1) {
					npcTalk(p, n, "The city warder Bravek is in there");
					playerTalk(p, n, "Can i go in?");
					npcTalk(p, n, "He has asked not to be disturbed");
					int second = showMenu(p, n, "This is urgent though",
							"Ok I will leave him alone");
					if (second == 0) {
						playerTalk(p, n, "Someone's been kidnapped",
								"and is being held in a plague house");
						npcTalk(p, n, "I'll see what I can do I suppose",
								"Mr Bravek there's a man here who really needs to speak to you");
						Npc bravek = getNearestNpc(p, 454, 15);
						npcTalk(p, bravek, "I suppose they can come in then",
								"If they keep it short");
						p.message("You go into the office");
						p.teleport(647, 585, false);
					} else if (second == 1) {
						// nothing
					}
				} else if (first == 2) {
					// nothing
				}
				break;
			}
		}
		if (n.getID() == 449) {
			switch (p.getQuestStage(this)) {
			case 6:
				playerTalk(p, n, "Hello",
						"Your parents say you saw what happened to Elena");
				npcTalk(p, n, "sniff", "Yes I was near the south east corner",
						"When I saw Elena walking by",
						"I was about to run to greet her",
						"when some men jumped out",
						"Shoved a sack over her head",
						"and dragged her into a building");
				playerTalk(p, n, "Which building?");
				npcTalk(p, n, "It was the mossy windowless building",
						"In that south east corner of west Ardougne");
				p.updateQuestStage(getQuestId(), 7);
				break;
			case -1:
				npcTalk(p,n, "Have you found Elena yet?");
				playerTalk(p,n, "Yes she's safe at home");
				npcTalk(p,n, "I hope she comes and visits sometime");
				playerTalk(p,n, "Maybe");
				break;
			}
		}
		if (n.getID() == 446 || n.getID() == 447) {
			switch (p.getQuestStage(this)) {
			case 6:
				playerTalk(p, n,
						"Hi I hear a woman called Elena is staying here");
				npcTalk(p,
						n,
						"Yes she was staying here",
						"but slightly over a week ago she was getting ready to go back",
						"However she never managed to leave",
						"My daughter Milli was playing near the west wall",
						"When she saw some shadowy figures jump out and grab her",
						"Milli is upstairs if you wish to speak to her");
				break;
			case 7:
				npcTalk(p, n, "Any luck with finding Elena yet?");
				playerTalk(p, n, "Not yet");
				npcTalk(p, n, "I wish you luck she did a lot for us");
				break;
			case -1:
				npcTalk(p,n, "Any luck with finding Elena yet?");
				playerTalk(p,n, "Yes she is safe at home now");
				npcTalk(p,n, "That's good to hear she helped us a lot");
				break;
			}
		}
		if (n.getID() == 443) {
			switch (p.getQuestStage(this)) {
			case 5:
				npcTalk(p, n, "Hello I don't recognise you",
						"We don't get many newcomers around here");
				int first = showMenu(p, n,
						"Hi I'm looking for a woman from east Ardougne",
						"So who's in charge here?");
				if (first == 0) {
					npcTalk(p,
							n,
							"East Ardougnian women are easier to find in east Ardougne",
							"Not many would come to west ardougne to find one",
							"Any particular woman you have in mind?");
					playerTalk(p, n, "Yes a lady called Elena");
					npcTalk(p, n, "What does she look like?");
					if (hasItem(p, 767)) {
						p.message("You show the picture to Jethick");
						npcTalk(p,
								n,
								"Ah yes I recognise her",
								"She was over here to help aid plague victims",
								"I think she is staying over with the Rehnison family",
								"They live in the small timbered building at the far north side of town",
								"I've not seen her around here in a while mind you");
						if (!hasItem(p, 768)) {
							npcTalk(p,
									n,
									"I don't suppose you could run me a little errand?",
									"While you are over there",
									"I borrowed this book from them",
									"can you return it?");
							p.message("Jethick gives you a book");
							addItem(p, 768, 1);
						}
					} else {
						playerTalk(p, n, "Um brown hair, in her twenties");
						npcTalk(p,
								n,
								"Hmm that doesn't narrow it down a huge amount",
								"I'll need to know more than that");
					}
				} else if (first == 1) {
					npcTalk(p,
							n,
							"Well King tyras has wandered off in to the west kingdom",
							"He doesn't care about the mess he's left here",
							"The city warder Bravek is in charge at the moment",
							"He's not much better");
				}
				break;
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case -1:
				npcTalk(p, n, "Hello I don't recognise you",
						"We don't get many newcomers around here");
				break;
			}
		}
		if (n.getID() == 450) {
			switch (p.getQuestStage(this)) {
			case 0:
				playerTalk(p, n, "hello madam");
				npcTalk(p, n, "oh hello there");
				playerTalk(p, n, "are you ok?");
				npcTalk(p, n, "not too bad",
						"I've just got some troubles on my mind");
				break;
			case 1:
				playerTalk(p, n,
						"hello, Edmond has asked me to help find your daughter");
				npcTalk(p, n, "yes he told me",
						"I've begun making your special gas mask",
						"but i need some dwellberries to finish it");
				if (hasItem(p, 765)) {
					playerTalk(p, n, "yes I've got some here");
					message(p, "you give the dwellberries to alrena",
							"alrena crushes the berries into a smooth paste",
							"she then smears the paste over a strange mask");
					p.getInventory().remove(765, 1);
					addItem(p, 766, 1);

					npcTalk(p,
							n,
							"there we go all done",
							"while in west ardougne you must wear this at all times",
							"or you'll never make it back");
					p.message("alrena gives you the mask");
					npcTalk(p, n,
							"while you two are digging I'll make a spare mask",
							"I'll hide it in the cupboard incase the mourners come in");
					p.updateQuestStage(getQuestId(), 2);
				} else {
					playerTalk(p, n, "I'll try to get some");
					npcTalk(p, n,
							"the best place to look is in mcgrubor's wood to the north");
				}
				break;
			case 2:
				if (p.getCache().hasKey("soil_soften")) {
					playerTalk(p, n, "hello again alrena");
					npcTalk(p, n, "how's the tunnel going?");
					playerTalk(p, n, "I'm getting there");
					npcTalk(p, n,
							"one of the mourners has been sniffing around",
							"asking questions about you and Edmond",
							"you should keep an eye out for him");
					playerTalk(p, n, "ok, thanks alrena");
					return;
				}
				playerTalk(p, n, "hello alrena");
				npcTalk(p, n, "hello darling",
						"how's that tunnel coming along?");
				playerTalk(p, n, "we're getting there");
				npcTalk(p, n, "well I'm sure you're quicker than Edmond");
				playerTalk(p, n,
						"i just need to soften the soil and then we'll start digging");
				npcTalk(p,
						n,
						"if you lose your protective clothing I've made a spare set",
						"they're hidden in the cupboard incase the mourners come in");
				break;
			case 3:
				playerTalk(p, n, "hello alrena");
				npcTalk(p, n,
						"Hi, have you managed to get through to west ardougne?");
				playerTalk(p, n, "not yet, but i should be going through soon");
				npcTalk(p,
						n,
						"make sure you wear your mask while you are over there",
						"i can't think of a worse way to die");
				break;
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
				playerTalk(p, n, "hello alrena");
				npcTalk(p, n, "hello, any word on elena?");
				playerTalk(p, n, "not yet I'm afraid");
				break;
			case 11:
			case -1:
				npcTalk(p,
						n,
						"Thank you for rescuing my daughter",
						"Elena has told me of your bravery",
						"In entering a house that could have been plague infected",
						"I can't thank you enough");
				break;
			}
		}
		if (n.getID() == 437) {
			switch (p.getQuestStage(this)) {
			case 0:
				playerTalk(p, n, "hello old man");
				p.message("the man looks upset");
				playerTalk(p, n, "what's wrong?");
				npcTalk(p, n, "I've got to find my daughter",
						"i pray that she's still alive");
				int firstMenu = showMenu(p, n, "What's happened to her?",
						"Well, good luck with finding her");
				if (firstMenu == 0) {
					npcTalk(p,
							n,
							"elena's a missionary and a healer",
							"three weeks ago she managed to cross the ardougne wall",
							"no one's allowed to cross the wall in case they spread the plague",
							"but after hearing the screams of suffering she felt she had to help",
							"she said she'd be gone for a few days but we've heard nothing since");
					int secondMenu = showMenu(p, n,
							"Tell me more about the plague",
							"Can i help find her?", "I'm sorry i have to go");
					if (secondMenu == 0) {
						npcTalk(p,
								n,
								"The mourners can tell you more than me",
								"they're the only ones allowed to cross the border",
								"I do know the plague is a horrible way to go",
								"that's why elena felt she had to go help");
						int thirdMenu = showMenu(p, n, "Can I help find her?",
								"I'm sorry i have to go");
						if (thirdMenu == 0) {
							npcTalk(p,
									n,
									"really, would you?",
									"I've been working on a plan to get over the wall",
									"but I'm too old and tired to carry it through",
									"if you're going over the first thing you'll need is protection from the plague",
									"My wife made a special gasmask  for elena",
									"with dwellberries rubbed into it",
									"Dwellberries help repel the virus",
									"We need some more though");
							playerTalk(p, n,
									"Where can I find these Dwellberries?");
							npcTalk(p, n,
									"the only place i know is mcgrubor's wood to the north");
							playerTalk(p, n, "ok I'll go get some");
							p.updateQuestStage(getQuestId(), 1);
						} else if (thirdMenu == 1) {
							npcTalk(p, n, "ok then goodbye");
						}
					} else if (secondMenu == 1) {
						npcTalk(p,
								n,
								"really, would you?",
								"I've been working on a plan to get over the wall",
								"but I'm too old and tired to carry it through",
								"if you're going over the first thing you'll need is protection from the plague",
								"My wife made a special gasmask  for elena",
								"with dwellberries rubbed into it",
								"Dwellberries help repel the virus",
								"We need some more though");
						playerTalk(p, n, "Where can I find these Dwellberries?");
						npcTalk(p, n,
								"the only place i know is mcgrubor's wood to the north");
						playerTalk(p, n, "ok I'll go get some");
						p.updateQuestStage(getQuestId(), 1);
					} else if (secondMenu == 2) {
						npcTalk(p, n, "ok then goodbye");
					}
				} else if (firstMenu == 1) {
					// NOTHING
				}
				break;
			case 1:
				playerTalk(p, n, "hello Edmond");
				npcTalk(p, n, "have you got the dwellberries?");
				if (hasItem(p, 765)) {
					playerTalk(p, n, "yes i have some here");
					npcTalk(p, n, "take them to my wife alrena");
				} else {
					playerTalk(p, n, "sorry I'm afraid not");
					npcTalk(p, n,
							" you'll probably find them in mcgrubor's wood to the north");
				}
				break;
			case 2:
				if (p.getCache().hasKey("soil_soften")) {
					playerTalk(p, n, "I've soaked the soil with water");
					npcTalk(p, n,
							"that's great it should be soft enough to dig through now");
					return;
				}
				playerTalk(p, n, "hi Edmond, I've got the gasmask now");
				npcTalk(p,
						n,
						"good stuff now for the digging",
						"beneath are the ardougne sewers",
						"there you'll find access to west ardougne",
						"the problem is the soil is rock hard",
						"you'll need to pour on some  buckets of water to soften it up",
						"I'll keep an eye out for the mourners");
				break;
			case 3:
				playerTalk(p, n,
						"Edmond, I can't get through to west ardougne",
						"there's an iron grill blocking my way",
						"i can't pull it off alone");
				npcTalk(p, n,
						"if you get some rope you could tie it to the grill",
						"then we could both pull it from here");
				break;
			case 4:
				playerTalk(p, n,
						"I've tied the other end of this rope to the grill");
				message(p, "Edmond gets a good grip on the rope",
						"together you tug the rope",
						"you hear a clunk as you both fly backwards");
				npcTalk(p, n, "that's done the job",
						"Remember always wear the gasmask",
						"otherwise you'll die over there for certain",
						"and please bring my elena back safe and sound");
				p.updateQuestStage(getQuestId(), 5);
				break;
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
				playerTalk(p, n, "hello");
				npcTalk(p, n, "Have you found Elena yet?");
				playerTalk(p, n, "Not yet, it's big city over there");
				npcTalk(p, n, "I hope it's not to late");
				break;
			case 11:
				npcTalk(p, n, "Thank you thank you",
						"Elena beat you back by minutes",
						"now I said I'd give you a reward");
				p.sendQuestComplete(Constants.Quests.PLAGUE_CITY);
				npcTalk(p, n, "What can I give you as a reward I wonder?",
						"Here take this magic scroll",
						"I have little use for it, but it may help you");
				addItem(p, 752, 1);
				p.message("This story is to be continued");
				break;
			case -1:
				npcTalk(p, n, "Ah hello again", "And thank you again");
				playerTalk(p, n, "No problem");
				break;
			}
		}
	}

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item,
			Player player) {
		if (obj.getID() == 447) {
			return true;
		}
		if (obj.getID() == 449) {
			return true;
		}
		if (obj.getID() == 457 && item.getID() == 780) {
			return true;
		}
		return false;
	}

	private int BUCKETS_USED = 0;

	@Override
	public void onInvUseOnObject(GameObject obj, Item item, Player p) {
		if (obj.getID() == 447) {
			if (item.getID() == 50) {
				if(p.getQuestStage(getQuestId()) == 2) {
					if (BUCKETS_USED >= 3) {
						p.message("the soil softens slightly");
						p.message("the soil is soft enough to dig into");
						if(!p.getCache().hasKey("soil_soften")) {
							p.getCache().store("soil_soften", true);
						}
					} else {
						p.message("you poor the water onto the soil");
						p.message("the soil softens slightly");
					}
					p.getInventory().replace(50, 21);
					BUCKETS_USED++;
				} else {
					p.message("You see no reason to do that at the moment");
				}
			}
			if (item.getID() == 211) {
				if (p.getCache().hasKey("soil_soften") || p.getQuestStage(getQuestId()) >= 3
						|| p.getQuestStage(getQuestId()) == -1) {
					message(p, "you dig deep into the soft soil",
							"Suddenly it crumbles away", "you fall through",
							"and land in the sewer");
					p.teleport(621, 3414, false);
					p.message("Edmond follows you down the hole");
					if(p.getCache().hasKey("soil_soften")) {
						p.getCache().remove("soil_soften");
					}
					if(p.getQuestStage(getQuestId()) == 2) {
						p.updateQuestStage(getQuestId(), 3);
					}
				} else {
					message(p, "you dig the soil", "The ground is rather hard");
				}
			}
		}
		if (obj.getID() == 449) {
			if (item.getID() == 237) {
				if (p.getQuestStage(this) >= 4 || p.getQuestStage(getQuestId()) == -1) {
					p.message("nothing interesting happens");
					return;
				}
				p.message("you tie one end of the rope to the sewer pipe's grill");
				p.message("and hold the other end in your hand");
				if(p.getQuestStage(this) == 3) {
					p.updateQuestStage(getQuestId(), 4);
				}
			}
		}
		if (obj.getID() == 457 && item.getID() == 780) {
			p.message("you go through the gate");
			doGate(p, obj, 181);
		}
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command,
			Player player) {
		if (obj.getID() == 448) {
			return true;
		}
		if (obj.getID() == 449) {
			return true;
		}
		if (obj.getID() == 456) {
			return true;
		}
		if (obj.getID() == 457) {
			return true;
		}
		if(obj.getID() == 452) {
			return true;
		}
		return false;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if(obj.getID() == 452) {
			if(p.getQuestStage(this) >= 2 || p.getQuestStage(this) == -1) {
				if(!hasItem(p, 766)) {
					p.message("you find a protective mask");
					addItem(p, 766, 1);
				} else {
					p.message("it's an old dusty cupboard");
				}
			}
		}
		if (obj.getID() == 448) {
			p.message("you climb up the mud pile");
			p.teleport(620, 578, false);
		}
		if (obj.getID() == 449) {
			if(p.getQuestStage(this) == -1 && p.getQuestStage(Constants.Quests.BIOHAZARD) != -1) {
				return;
			}
			if(p.getQuestStage(this) == -1 && p.getQuestStage(Constants.Quests.BIOHAZARD) == -1) {
				p.message("you climb through the sewer pipe");
				p.teleport(632, 589, false);
				return;
			}
			if (p.getQuestStage(getQuestId()) >= 5) {
				if (p.getInventory().wielding(766)) {
					p.message("you climb through the sewer pipe");
					p.teleport(632, 589, false);
				} else {
					p.message("You should wear your gasmask");
					p.message("Before entering west Ardougne");
				}
				return;
			}
			p.message("the grill is too secure");
			p.message("you can't pull it off alone");
		}
		if (obj.getID() == 456) {
			if(p.getQuestStage(this) >= 11 || p.getQuestStage(this) == -1) {
				p.message("the barrel is empty");
				return;
			}
			if (!hasItem(p, 780)) {
				p.message("You find a small key in the barrel");
				addItem(p, 780, 1);
			} else {
				p.message("the barrel is empty");
			}
		}
		if (obj.getID() == 457) {
			if(p.getQuestStage(this) >= 11 || p.getQuestStage(this) == -1) {
				p.message("you go through the gate");
				doGate(p, obj, 181);
				return;
			}
			if (p.getY() >= 3448) {
				World.getWorld().replaceGameObject(obj, 
						new GameObject(obj.getLocation(), 181, obj
								.getDirection(), obj.getType()));
				World.getWorld().delayedSpawnObject(obj.getLoc(), 2000);
				p.message("you go through the gate");
				p.teleport(637, 3447, false);
			} else {
				if (hasItem(p, 780)) {
					p.message("The gate is locked");
					p.message("Why don't you use your key on the gate?");
				} else {
					Npc elena = getNearestNpc(p, 465, 10);
					if(elena != null) {
						npcTalk(p, elena, "Hey get me out of here please");
						playerTalk(p, elena, "I would do but I don't have a key");
						npcTalk(p, elena,
								"I think there may be one around here somewhere",
								"I'm sure I saw them stashing it somewhere");
						int menu = showMenu(p, elena,
								"Have you caught the plague?",
								"Ok I will look for it");
						if (menu == 0) {
							npcTalk(p, elena, "No, I have none of the symptoms");
							playerTalk(p, elena,
									"Strange I was told this house was plague infected");
							npcTalk(p, elena,
									"I suppose that was a cover up by the kidnappers");
						} else if (menu == 1) {
							// Nothing
						}
					} else {
						p.message("Elena is currently busy");
					}
				}
			}
		}
	}

}
