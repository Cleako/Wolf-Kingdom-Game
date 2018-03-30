package com.legacy.server.plugins.quests.members;

import static com.legacy.server.plugins.Functions.addItem;
import static com.legacy.server.plugins.Functions.hasItem;
import static com.legacy.server.plugins.Functions.message;
import static com.legacy.server.plugins.Functions.npcTalk;
import static com.legacy.server.plugins.Functions.playerTalk;
import static com.legacy.server.plugins.Functions.removeItem;
import static com.legacy.server.plugins.Functions.showMenu;
import static com.legacy.server.plugins.Functions.sleep;

import com.legacy.server.Constants;
import com.legacy.server.model.entity.GameObject;
import com.legacy.server.model.entity.GroundItem;
import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.model.world.World;
import com.legacy.server.plugins.QuestInterface;
import com.legacy.server.plugins.listeners.action.ObjectActionListener;
import com.legacy.server.plugins.listeners.action.PickupListener;
import com.legacy.server.plugins.listeners.action.TalkToNpcListener;
import com.legacy.server.plugins.listeners.action.WallObjectActionListener;
import com.legacy.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.legacy.server.plugins.listeners.executive.PickupExecutiveListener;
import com.legacy.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.legacy.server.plugins.listeners.executive.WallObjectActionExecutiveListener;

public class SeaSlug implements QuestInterface,TalkToNpcListener,
		TalkToNpcExecutiveListener, PickupListener, PickupExecutiveListener,
		ObjectActionListener, ObjectActionExecutiveListener,
		WallObjectActionListener, WallObjectActionExecutiveListener {

	@Override
	public int getQuestId() {
		return Constants.Quests.SEA_SLUG;
	}

	@Override
	public String getQuestName() {
		return "Sea Slug (members)";
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player p) {
		p.incQuestPoints(1);
		p.incQuestExp(10, 175 + 200 * p.getSkills().getMaxStat(10));
		p.message("@gre@You have gained 1 quest point!");
		p.message("well done, you have completed the sea slug quest");
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		if (n.getID() == 455) { /* Caroline */
			return true;
		}
		if (n.getID() == 456 || n.getID() == 457 || n.getID() == 458) { /*
																		 * Holgart
																		 * one
																		 * two
																		 * three
																		 */
			return true;
		}
		if (n.getID() == 461) { /* kennith */
			return true;
		}
		if (n.getID() == 459) { /* kent */
			return true;
		}
		if (n.getID() == 462 || n.getID() == 463 || n.getID() == 460) { /*
																		 * Npcs
																		 * on
																		 * fishing
																		 * platform
																		 * and
																		 * Kennith
																		 */
			return true;
		}
		return false;
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == 459) {
			switch (p.getQuestStage(this)) {
			case 4:
				npcTalk(p, n, "oh thank Saradomin",
						"i thought i would be left out here forever");
				playerTalk(p, n,
						"your wife sent me out to find you and your boy",
						"kennith's fine he's on the platform");
				npcTalk(p, n, "i knew the row boat wasn't sea worthy",
						"i couldn't risk bringing him along but you must get him of that platform");
				playerTalk(p, n, "what's going on on there?");
				npcTalk(p,
						n,
						"five days ago we pulled in huge catch",
						"as well as fish we caught small slug like sea creatures, hundreds of them",
						"that's when the fishermen began to act strange",
						"it was the sea slugs, they attach themselves to your body",
						"and somehow take over the mind of the carrier",
						"i told Kennith to hide until i returned but i was washed up here",
						"please go back and get my boy",
						"you can send help for me later", "traveler wait!");
				message(p, "kent reaches behind your neck", "slooop",
						"he pulls a sea slug from under your top");
				npcTalk(p, n,
						"a few more minutes and that thing would have full control you body");
				playerTalk(p, n, "yuck..thanks kent");
				p.updateQuestStage(getQuestId(), 5);
				break;
			case 5:
				playerTalk(p, n, "hello");
				npcTalk(p, n, "oh my", "i must get back to shore");
				break;
			}
		}
		if (n.getID() == 461) {
			switch (p.getQuestStage(this)) {
			case 3:
				playerTalk(p, n, "are you okay young one?");
				npcTalk(p, n, "no i want my daddy");
				playerTalk(p, n, "Where is your father?");
				npcTalk(p,
						n,
						"he went to get help days ago",
						"the nasty fisher men tried to throw me and daddy into the sea",
						"so he told me to hide in here");
				playerTalk(p, n, "that's good advice",
						"you stay here and i'll go try and find your father");
				p.updateQuestStage(getQuestId(), 4);
				break;
			case 4:
				playerTalk(p, n, "are you okay?");
				npcTalk(p, n, "i want to see daddy");
				playerTalk(p, n, "i'm working on it");
				break;
			case 5:
				if (p.getCache().hasKey("loose_panel")) {
					playerTalk(p, n,
							"kennith i've made an opening in the wall",
							"you can come out there");
					npcTalk(p, n, "are their any sea slugs on the other side?");
					playerTalk(p, n, "not one");
					npcTalk(p, n, "how will i get down stairs");
					playerTalk(p, n, "i'll figure that out in a moment");
					npcTalk(p, n, "okay, when you have i'll come out");
					return;
				}
				playerTalk(p, n, "are you okay?");
				npcTalk(p, n, "i want to see daddy");
				playerTalk(p, n, "you'll be able to see him soon",
						"first we need to get you back to land",
						"come with me to the boat");
				npcTalk(p, n, "no");
				playerTalk(p, n, "what, why not?");
				npcTalk(p, n, "i'm scared of those nasty sea slugs",
						"i won't go near them");
				playerTalk(p, n,
						"okay, you wait here and i'll figure another way to get you out");
				break;
			}
		}
		if (n.getID() == 460) {
			if (p.getQuestStage(getQuestId()) == 5) {
				playerTalk(p, n, "hello");
				npcTalk(p, n, "oh thank god it's you",
						"they've all gone mad i tell you",
						"one of the fishermen tried to throw me into the sea");
				playerTalk(p, n,
						"they're all being controlled by the sea slugs");
				npcTalk(p, n, "i figured as much");
				playerTalk(p, n,
						"i need to get kennith of this platform but i can't get past the fishermen");
				npcTalk(p, n, "the sea slugs are scared of heat",
						"i figured that out when i tried to cook them");
				if (!hasItem(p, 773)) {
					npcTalk(p, n, "here");
					message(p, "bailey gives you a torch");
					addItem(p, 773, 1);
					npcTalk(p,
							n,
							"i doubt the fishermen will come near you if you can get this torch to light",
							"the only problem is all the wood and flint is damp",
							"i can't light a thing");
				} else {
					playerTalk(p, n,
							"i better figure a way to light this torch");
				}
				return;
			}
			playerTalk(p, n, "hello");
			npcTalk(p, n, "well hello there", "what are you doing here?");
			playerTalk(p, n,
					"i'm trying to find out what happened to a boy named kennith");
			npcTalk(p, n, "oh, you mean kent's son",
					"he's around somewhere, probably hiding");
		}
		if (n.getID() == 463) {
			playerTalk(p, n, "hello there");
			p.message("his eyes are fixated");
			p.message("starring at the sea");
			npcTalk(p, n, "must find family");
			playerTalk(p, n, "what?");
			npcTalk(p, n, "soon we'll all be together");
			playerTalk(p, n, "are you okay?");
			npcTalk(p, n, "must find family", "they're all under the blue",
					"deep deep under the blue");
			playerTalk(p, n, "ermm..i'll leave you to it then");
		}
		if (n.getID() == 462) {
			playerTalk(p, n, "hello");
			p.message("his eyes are fixated");
			p.message("starring at the sea");
			npcTalk(p, n, "keep away human", "leave or face the deep blue");
			playerTalk(p, n, "pardon?");
			npcTalk(p, n, "you'll all end up in the blue",
					"deep deep under the blue");
		}
		if (n.getID() == 455) { /* Caroline */
			switch (p.getQuestStage(this)) {
			case 0:
				playerTalk(p, n, "hello there");
				npcTalk(p, n, "is there any chance you could help me?");
				playerTalk(p, n, "what's wrong?");
				npcTalk(p,
						n,
						"it's my husband, he works on a fishing platform",
						"once a month he takes our son kennith out with him",
						"they usually write to me regularly but i've heard nothing all week",
						"it's very strange");
				playerTalk(p, n, "maybe the post was lost!");
				npcTalk(p,
						n,
						"maybe, but no one's heard from the other fishermen on the platform",
						"their families are becoming quite concerned",
						"is there any chance you could visit the platform and find out what's going on?");
				int firstMenu = showMenu(p, n,
						"i suppose so, how do i get there?",
						"i'm sorry i'm too busy");
				if (firstMenu == 0) {
					npcTalk(p, n, "that's very good of you traveller",
							"my friend holgart will take you there");
					playerTalk(p, n, "okay i'll go and see if they're ok");
					npcTalk(p, n, "i will reward you for your time",
							"and it'll give me great piece of mind",
							"to know kennith and my husband kent are safe");
					p.updateQuestStage(getQuestId(), 1);
				} else if (firstMenu == 1) {
					npcTalk(p, n, "thats a shame");
					playerTalk(p, n, "bye");
					npcTalk(p, n, "bye");
				}
				break;
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
				playerTalk(p, n, "hello caroline");
				npcTalk(p, n,
						"brave adventurer have you any news about my son and his father?");
				playerTalk(p, n, "i'm working on it now caroline");
				npcTalk(p, n, "please bring them back safe and sound");
				playerTalk(p, n, "i'll do my best");
				break;
			case 6:
				playerTalk(p, n, "hello");
				npcTalk(p,
						n,
						"brave adventurer you've returned",
						"kennith told me about the strange going ons on the platform",
						"i had no idea it was so serious",
						"i could have lost my son and my husband if it wasn't for you");
				playerTalk(p, n, "we found kent stranded on a island");
				npcTalk(p, n,
						"yes, holgart told me and sent a rescue party out",
						"kent's back at home now, resting with kennith",
						"i don't think he'll be doing any fishing for a while",
						"here, take these oyster pearls as a reward",
						"they're worth a fair bit",
						"and can be used to make lethal crossbow bolts");
				p.sendQuestComplete(Constants.Quests.SEA_SLUG);
				playerTalk(p, n, "thanks");
				npcTalk(p, n, "thank you", "take care of yourself adventurer");
				addItem(p, 779, 1);
				break;
			}
		}
		if (n.getID() == 456 || n.getID() == 457 || n.getID() == 458) { /* Holgart */
			switch (p.getQuestStage(this)) {
			case 0:
				playerTalk(p, n, "hello there");
				npcTalk(p, n, "well hello m'laddy", "beautiful day isn't it");
				playerTalk(p, n, "not bad i suppose");
				npcTalk(p, n, "just smell that sea air... beautiful");
				playerTalk(p, n, "hmm...lovely!");
				break;
			case 1:
				playerTalk(p, n, "hello");
				npcTalk(p, n, "hello m'hearty");
				playerTalk(p, n,
						"i would like a ride on your boat to the fishing platform");
				npcTalk(p, n,
						"i'm afraid it isn't sea worthy, it's full of holes",
						"to fill the holes i'll need some swamp paste");
				playerTalk(p, n, "swamp paste?");
				npcTalk(p, n,
						"yes, swamp tar mixed with flour heated over a fire");
				playerTalk(p, n, "where can i find swamp tar?");
				npcTalk(p,
						n,
						"unfortunately the only supply of swamp tar is in the swamps below lumbridge",
						"it's too far for an old man like me to travel",
						"if you can make me some swamp paste i will give you a ride on my boat");
				playerTalk(p, n, "i'll see what i can do");
				p.updateQuestStage(getQuestId(), 2);
				break;
			case 2:
				playerTalk(p, n, "hello holgart");
				npcTalk(p, n, "hello m'hearty",
						"did you manage to make some swamp paste?");
				if (removeItem(p, 785, 1)) {
					playerTalk(p, n, "yes i have some here");
					p.message("you give holgart the swamp paste");
					npcTalk(p, n, "superb, this looks great");
					p.message("holgart smears the paste over the under side of his boat");
					npcTalk(p, n, "that's done the job, now we can go",
							"jump aboard");
					p.updateQuestStage(getQuestId(), 3);
					int boatMenu = showMenu(p, n, "i'll come back later",
							"okay, lets do it");
					if (boatMenu == 0) {
						npcTalk(p, n, "okay then", "i'll wait here for you");
					} else if (boatMenu == 1) {
						npcTalk(p, n, "hold on tight");
						message(p, "you board the small row boat",
								"you arrive at the fishing platform");
						p.teleport(495, 618, false);
					}
				} else {
					playerTalk(p, n, "i'm afraid not");
					npcTalk(p,
							n,
							"to make it you need swamp tar mixed with flour heated over a fire",
							"the only supply of swamp tar is in the swamps below lumbridge",
							"i can't fix the row boat without it");
					playerTalk(p, n, "ok, i'll try to find some");
				}
				break;
			case 3:
				if (p.getLocation().inArdougne()) {
					playerTalk(p, n, "hello holgart");
					npcTalk(p, n, "hello again land lover",
							"there's some strange going's on, on that platform i tell you");
					int goMenu = showMenu(p, n, "will you take me there?",
							"i'm keeping away from there");
					if (goMenu == 0) {
						npcTalk(p, n, "of course m'hearty",
								"if that's what you want");
						message(p, "you board the small row boat",
								"you arrive at the fishing platform");
						p.teleport(495, 618, false);
					} else if (goMenu == 1) {
						npcTalk(p, n, " fair enough m'hearty");
					}
				} else {
					playerTalk(p, n, "hey holgart");
					npcTalk(p, n, "have you had enough of this place yet?",
							"it's scaring me");
					int goBack = showMenu(p, n,
							"no, i'm going to stay a while",
							"okay, lets go back");
					if (goBack == 0) {
						npcTalk(p, n, "okay m'hearty jump on");
						message(p, "you arrive back on shore");
						p.teleport(515, 613, false);
					} else if (goBack == 1) {
						npcTalk(p, n, " okay, you're the boss");
					}
				}
				break;
			case 4:
				playerTalk(p, n, "holgart, something strange is going on here");
				npcTalk(p, n, "you're telling me",
						"none of the sailors seem to remember who i am");
				playerTalk(p, n,
						"apparently kenniths father left for help a couple of days ago");
				npcTalk(p, n,
						"that's a worry, no ones heard from him on shore",
						"come on, we better go look for him");
				message(p, "you board the row boat",
						"you arrive on a small island");
				p.teleport(512, 639, false);
				break;
			case 5:
				if (p.getLocation().inPlatformArea()) {
					playerTalk(p, n, "hey holgart");
					npcTalk(p, n, "have you had enough of this place yet?",
							"it's scaring me");
					int goBack = showMenu(p, n,
							"no, i'm going to stay a while",
							"okay, lets go back");
					if (goBack == 0) {
						npcTalk(p, n, "okay m'hearty jump on");
						message(p, "you arrive back on shore");
						p.teleport(515, 613, false);
					} else if (goBack == 1) {
						npcTalk(p, n, "okay, you're the boss");
					}
				} else {
					playerTalk(p, n, "we had better get back to the platform",
							"and see what's going on");
					npcTalk(p, n, "you're right", "it all sounds pretty creepy");
					message(p, "you arrive back at the fishing platform");
					p.teleport(495, 618, false);
				}
				break;
			case 6:
				playerTalk(p, n, "did you get the kid back to shore?");
				npcTalk(p, n, "yes, he's safe and sound with his parents",
						"your turn to return to land now adventurer");
				playerTalk(p, n, "looking forward to it");
				p.message("you board the small row boat");
				p.message("you arrive back on shore");
				p.teleport(515, 613, false);
				break;
			}
		}
	}

	@Override
	public boolean blockPickup(Player p, GroundItem i) {
		if (i.getID() == 769) {
			return true;
		}
		return false;
	}

	@Override
	public void onPickup(Player p, GroundItem i) {
		if (i.getID() == 769) {
			int damage = p.getRandom().nextInt(8) + 1;
			p.message("you pick up the seaslug");
			p.message("it sinks its teeth deep into you hand");
			p.damage(damage);
			playerTalk(p, null, "ouch");
			p.message("you drop the sea slug");
		}
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player p) {
		if (obj.getID() == 458) {
			return true;
		}
		if (obj.getID() == 453) {
			return true;
		}
		return false;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if (obj.getID() == 458) {
			if (p.getQuestStage(getQuestId()) < 5) {
				message(p, "You climb up the ladder");
				p.teleport(494, 1561, false);
				return;
			}
			if (p.getQuestStage(getQuestId()) >= 5) {
				if (!hasItem(p, 774)) {
					int damage = p.getRandom().nextInt(1) + 7;
					p.message("You attempt to climb up the ladder");
					p.message("the fishermen approach you");
					p.message("and throw you back down the ladder");
					p.damage(damage);
					playerTalk(p, null, "ouch");
				} else {
					message(p, "You climb up the ladder");
					p.teleport(494, 1561, false);
					p.message("the fishermen seem afraid of your torch");
				}
			}
		}
		if (obj.getID() == 453) {
			if (p.getQuestStage(getQuestId()) == 5) {
				message(p, "you rotate the crane around", "to the far platform");
				GameObject firstRotation = new GameObject(obj.getLocation(),
						453, 5, 0);
				World.getWorld().replaceGameObject(obj, firstRotation);
				sleep(500);
				GameObject secondRotation = new GameObject(obj.getLocation(),
						453, 6, 0);
				World.getWorld().replaceGameObject(obj, secondRotation);
				playerTalk(p, null, "jump on kennith!");
				p.message("kennith comes out through the broken panal");
				GameObject thirdRotation = new GameObject(obj.getLocation(),
						453, 5, 0);
				World.getWorld().replaceGameObject(obj, thirdRotation);
				sleep(500);
				GameObject fourthRotation = new GameObject(obj.getLocation(),
						453, 4, 0);
				World.getWorld().replaceGameObject(obj, fourthRotation);
				message(p, "he climbs onto the fishing net",
						"you rotate the crane back around",
						"and lower kennith to the row boat waiting below");
				p.updateQuestStage(getQuestId(), 6);
				p.getCache().remove("loose_panel");
			} else {
				p.message("Nothing interesting happens");
			}
		}
	}

	@Override
	public boolean blockWallObjectAction(GameObject obj, Integer click,
			Player player) {
		if (obj.getID() == 124) {
			return true;
		}
		return false;
	}

	@Override
	public void onWallObjectAction(GameObject obj, Integer click, Player p) {
		if (obj.getID() == 124) {
			if (p.getQuestStage(getQuestId()) == 5) {
				message(p, "you kick the loose panel",
						"the wood is rotten and crumbles away",
						"leaving an opening big enough for kennith to climb through");
				p.getCache().store("loose_panel", true);
			} else {
				p.message("Nothing interesting happens");
			}
		}
	}
}
