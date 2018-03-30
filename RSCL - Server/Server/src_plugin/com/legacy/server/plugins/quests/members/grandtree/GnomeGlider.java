package com.legacy.server.plugins.quests.members.grandtree;

import static com.legacy.server.plugins.Functions.getNearestNpc;
import static com.legacy.server.plugins.Functions.message;
import static com.legacy.server.plugins.Functions.npcTalk;
import static com.legacy.server.plugins.Functions.playerTalk;
import static com.legacy.server.plugins.Functions.showMenu;

import com.legacy.server.Constants;
import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.plugins.listeners.action.TalkToNpcListener;
import com.legacy.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public class GnomeGlider implements TalkToNpcListener, TalkToNpcExecutiveListener { 
	
	public static final int GNOME_PILOT = 552;
	
	public static final int GNOME_PILOT_TWO = 556;
	
	public static final int GNOME_PILOT_KARAMJA = 569;
	public static final int GNOME_PILOT_VARROCK = 570;
	public static final int GNOME_PILOT_ALKHARID = 571;
	public static final int GNOME_PILOT_WHITEMOUNTAIN = 572;


	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		if(n.getID() == GNOME_PILOT || n.getID() == GNOME_PILOT_KARAMJA || n.getID() == GNOME_PILOT_ALKHARID || n.getID() == GNOME_PILOT_WHITEMOUNTAIN || n.getID() == GNOME_PILOT_VARROCK) 
			return true;
		return false;
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if(n.getID() == GNOME_PILOT_VARROCK) {
			playerTalk(p,n, "hello again");
			npcTalk(p,n, "well hello adventurer",
					"as you can see we crashed on impact",
					"i don't think it'll fly again",
					"sorry but you'll have to walk");
		}
		if(n.getID() == GNOME_PILOT_KARAMJA || n.getID() == GNOME_PILOT_ALKHARID || n.getID() == GNOME_PILOT_WHITEMOUNTAIN) {
			if(p.getQuestStage(Constants.Quests.GRAND_TREE) == -1) {
				playerTalk(p,n, "hello again");
				npcTalk(p,n, "well hello adventurer");
				npcTalk(p,n, "would you like to go to the tree gnome stronghold?");
				int travelBackMenu = showMenu(p,n,
				"ok then",
				"no thanks");
				if(travelBackMenu == 0) {
					npcTalk(p,n, " ok, hold on tight");
					message(p, "you both hold onto the wooden beam",
							"you take a few steps backand rush forwards",
							"the glider just lifts of the ground");
					p.teleport(221, 3567);
					playerTalk(p,n, "whhaaaaaaaaaagghhh");
					p.teleport(414, 2995);
				}
				return;
			}
			playerTalk(p, n, "hello");
			npcTalk(p, n, "hello traveller");
		}
		if(n.getID() == GNOME_PILOT) {
			playerTalk(p, n, "hello");
			if(p.getQuestStage(Constants.Quests.GRAND_TREE) == -1) {
				npcTalk(p,n, "well hello again traveller");
				npcTalk(p,n, "can i take you somewhere?");
				npcTalk(p,n, "i can fly like the birds");
				int menu = showMenu(p,n,
				"karamja",
				"varrock",
				"Al kharid",
				"white wolf mountain",
				"I'll stay here thanks");
				if(menu == 0) {
					npcTalk(p,n, "ok, your the boss, jump on",
							"hold on tight, it'll be a rough ride");
					message(p, "you hold on tight to the glider's wooden beam",
							"the pilot leans back and then pushes the glider forward",
							"you float softly off the grand tree");
					p.teleport(221, 3567);
					playerTalk(p,n, "whhaaaaaaaaaagghhh");
					p.teleport(389, 753);
					playerTalk(p,n, "ouch");
				} else if(menu == 1) {
					npcTalk(p,n, "ok, your the boss, jump on",
							"hold on tight, it'll be a rough ride");
					message(p, "you hold on tight to the glider's wooden beam",
							"the pilot leans back and then pushes the glider forward",
							"you float softly off the grand tree");
					p.teleport(221, 3567);
					playerTalk(p,n, "whhaaaaaaaaaagghhh");
					p.teleport(58, 504);
					playerTalk(p,n, "ouch");
				} else if(menu == 2) {
					npcTalk(p,n, "ok, your the boss, jump on",
							"hold on tight, it'll be a rough ride");
					message(p, "you hold on tight to the glider's wooden beam",
							"the pilot leans back and then pushes the glider forward",
							"you float softly off the grand tree");
					p.teleport(221, 3567);
					playerTalk(p,n, "whhaaaaaaaaaagghhh");
					p.teleport(88, 664);
					playerTalk(p,n, "ouch");
				} else if(menu == 3) {
					npcTalk(p,n, "ok, your the boss, jump on",
							"hold on tight, it'll be a rough ride");
					message(p, "you hold on tight to the glider's wooden beam",
							"the pilot leans back and then pushes the glider forward",
							"you float softly off the grand tree");
					p.teleport(221, 3567);
					playerTalk(p,n, "whhaaaaaaaaaagghhh");
					p.teleport(400, 461);
					playerTalk(p,n, "ouch");
				} else if(menu == 4) {
					npcTalk(p,n, "no worries, let me know if you change your mind");
				}
				return;
			}
			else if(p.getQuestStage(Constants.Quests.GRAND_TREE) >= 8 && p.getQuestStage(Constants.Quests.GRAND_TREE) <= 9) {
				npcTalk(p,n, "hi, the king said that you need to leave");
				playerTalk(p,n, "yes, apparently humans are invading");
				npcTalk(p,n, "i find that hard to believe",
						"i have lots of human friends");
				playerTalk(p,n, "it seems a bit strange to me");
				npcTalk(p,n, "well, would you like me to take you somewhere?");
				int menu = showMenu(p,n,
						"actually yes, take me to karamja",
						"no thanks i'm going to hang around");
				if(menu == 0) {
					npcTalk(p,n, "ok, your the boss, jump on",
							"hold on tight, it'll be a rough ride");
					message(p, "you hold on tight to the glider's wooden beam",
							"the pilot leans back and then pushes the glider forward",
							"you float softly off the grand tree");
					p.teleport(221, 3567);
					playerTalk(p,n, "whhaaaaaaaaaagghhh");
					p.teleport(425, 764);
					playerTalk(p,n, "ouch");
					Npc GNOME_PILOT = getNearestNpc(p, GNOME_PILOT_TWO, 5);
					npcTalk(p,GNOME_PILOT, "ouch");
					p.message("you crash in south karamja");
					npcTalk(p,GNOME_PILOT, "sorry about that, are you ok");
					playerTalk(p,GNOME_PILOT, "i seem to be fine, can't say the same for your glider");
					npcTalk(p,GNOME_PILOT, "i don't think i can fix this",
							"looks like we'll be heading back by foot",
							"i hope you find what you came for adventurer");
					playerTalk(p,GNOME_PILOT, "me too, take care little man");
					npcTalk(p,GNOME_PILOT, "traveller watch out");
					Npc JOGRE = getNearestNpc(p, 523, 15);
					if(JOGRE != null) {
						npcTalk(p,JOGRE, " grrrrr");
						JOGRE.setChasing(p);
					}
				} else if(menu == 1) {
					npcTalk(p,n, "ok, i'll be here if you need me");
				}
				return;
			}
			npcTalk(p, n, "hello traveller");
		}
	}
}