package com.legacy.server.plugins.quests.members.undergroundpass.obstacles;

import static com.legacy.server.plugins.Functions.AGILITY;
import static com.legacy.server.plugins.Functions.HITS;
import static com.legacy.server.plugins.Functions.getCurrentLevel;
import static com.legacy.server.plugins.Functions.getNearestNpc;
import static com.legacy.server.plugins.Functions.inArray;
import static com.legacy.server.plugins.Functions.message;
import static com.legacy.server.plugins.Functions.npcTalk;
import static com.legacy.server.plugins.Functions.playerTalk;
import static com.legacy.server.plugins.Functions.random;

import com.legacy.server.Constants;
import com.legacy.server.Server;
import com.legacy.server.event.custom.UndergroundPassMessages;
import com.legacy.server.model.entity.GameObject;
import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.plugins.listeners.action.ObjectActionListener;
import com.legacy.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.legacy.server.util.rsc.DataConversions;

public class UndergroundPassAgilityObstacles implements ObjectActionListener, ObjectActionExecutiveListener {

	public static final int[] LEDGES = { 862, 864, 863, 872, 865, 866 };
	public static final int NORTH_STONE_STEP = 889;
	public static final int SOUTH_STONE_STEP = 921;
	public static final int FIRST_REMAINING_BRIDGE = 891;
	public static final int[] STONE_JUMP_BRIDGES = { 898, 892, 896, 910, 906, 908, 902, 904, 900, 894 };
	public static final int[] STONE_REMAINING_BRIDGES = { 893, 907, 905, 909, 903, 901, 895, 899, 897 };
	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player p) {
		if(inArray(obj.getID(), LEDGES)) {
			return true;
		}
		if(inArray(obj.getID(), STONE_JUMP_BRIDGES)) {
			return true;
		}
		if(inArray(obj.getID(), STONE_REMAINING_BRIDGES)) {
			return true;
		}
		if(obj.getID() == FIRST_REMAINING_BRIDGE || obj.getID() == NORTH_STONE_STEP || obj.getID() == SOUTH_STONE_STEP) {
			return true;
		}
		return false;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if(inArray(obj.getID(), LEDGES)) {
			message(p, "you climb the ledge");
			if(succeed(p, 1)) {
				switch(obj.getID()) {
				case 862:
					p.teleport(730, 3494);
					break;
				case 864:
					p.teleport((p.getQuestStage(Constants.Quests.UNDERGROUND_PASS) >= 4) || (p.getQuestStage(Constants.Quests.UNDERGROUND_PASS) == -1)  ? 751 : 734, 3496);
					break;
				case 863:
					p.teleport(763, 3442);
					break;
				case 872:
					p.teleport((p.getQuestStage(Constants.Quests.UNDERGROUND_PASS) >= 4) || (p.getQuestStage(Constants.Quests.UNDERGROUND_PASS) == -1) ? 765 : 748, 3497);
					break;
				case 865:
					p.teleport(728, 3499);
					break;
				case 866:
					p.teleport((p.getQuestStage(Constants.Quests.UNDERGROUND_PASS) >= 4) || (p.getQuestStage(Constants.Quests.UNDERGROUND_PASS) == -1) ? 755 : 738, 3501);
					break;
				}
				p.message("you drop down to the cave floor");
			} else {
				p.message("but you loose your footing");
				p.damage(2);
				playerTalk(p,null, "aargh");
			}
		}
		if(obj.getID() == NORTH_STONE_STEP) {
			if(p.getQuestStage(Constants.Quests.UNDERGROUND_PASS) == 4) {
				failBlackAreaObstacle(p, obj); // fail directly, to get stage 5.
			} else {
				message(p, "you walk down the stone steps");
				p.teleport(766, 585);

			}
		}
		if(obj.getID() == SOUTH_STONE_STEP) {
			message(p, "you walk down the steps",
					"they lead to a ladder, you climb down");
			p.teleport(739, 667);
		}
		if(obj.getID() == FIRST_REMAINING_BRIDGE) {
			message(p,"you attempt to walk over the remaining bridge..");
			if(p.getQuestStage(Constants.Quests.UNDERGROUND_PASS) == 4) {
				failBlackAreaObstacle(p, obj); // fail directly, to get stage 5.
			} else {
				if(succeed(p, 1)) {
					if(obj.getX() == p.getX() + 1) {
						p.teleport(776, obj.getY());
					} else {
						p.teleport(773, obj.getY());
					}
					p.message("..you manage to cross safley");
				} else {
					failBlackAreaObstacle(p, obj);
				}
			}
		}
		if(inArray(obj.getID(), STONE_REMAINING_BRIDGES) || inArray(obj.getID(), STONE_JUMP_BRIDGES)) {
			if(inArray(obj.getID(), STONE_JUMP_BRIDGES)) {
				message(p,"you attempt to jump across the gap..");
			} else {
				message(p,"you attempt to walk over the remaining bridge..");
			}
			if(succeed(p, 1)) {
				if(obj.getX() == p.getX() + 1) {
					p.teleport(obj.getX() + 3, obj.getY());
				} else if(obj.getX() == p.getX() - 3){
					p.teleport(obj.getX() - 1, obj.getY());
				} else if(obj.getY() == p.getY() + 1) {
					p.teleport(obj.getX(), obj.getY() + 3);
				} else if(obj.getY() == p.getY() - 3) {
					p.teleport(obj.getX(), obj.getY() - 1);
				}
				p.message("..you manage to cross safley");
			} else {
				failBlackAreaObstacle(p, obj);
			}
			Server.getServer().getEventHandler()
			.add(new UndergroundPassMessages(p, DataConversions.random(3000, 15000)));
		}
	}

	boolean succeed(Player player, int req) {
		int level_difference = getCurrentLevel(player, AGILITY) - req;
		int percent = random(1, 100);

		if(level_difference < 0)
			return true;
		if(level_difference >= 15)
			level_difference = 70;
		if(level_difference >= 20)
			level_difference = 80;
		else
			level_difference = 40 + level_difference;

		return percent <= level_difference;
	}

	private void failBlackAreaObstacle(Player p, GameObject obj) {
		p.message("..but you slip and tumble into the darkness");
		fallTeleportLocation(p, obj);
		p.damage(((int) getCurrentLevel(p, HITS) / 5) + 5); // 6 lowest, 25 max. 
		playerTalk(p,null, "ouch!");
		if(p.getQuestStage(Constants.Quests.UNDERGROUND_PASS) == 4) {
			p.updateQuestStage(Constants.Quests.UNDERGROUND_PASS, 5);
			Npc koftik = getNearestNpc(p, 650, 10);
			if(koftik != null) {
				npcTalk(p,koftik, "traveller is that you?.. my friend on a mission");
				playerTalk(p,koftik, "koftik, you're still here, you should leave");
				npcTalk(p,koftik, "leave?...leave?..this is my home now",
						"home with my lord, he talks to me, he's my friend");
				p.message("koftik seems to be in a weak state of mind");
				playerTalk(p,koftik, "koftik you really should leave these caverns");
				npcTalk(p,koftik, "not now, we're all the same down here",
						"now there's just you and those dwarfs to be converted");
				playerTalk(p,koftik, "dwarfs?");
				npcTalk(p,koftik, "foolish dwarfs, still believing that they can resist",
						"no one resists iban, go traveller",
						"the dwarfs to the south, they're not safe in the south",
						"we'll show them, go slay them m'lord",
						"he'll be so proud, that's all i want");
				playerTalk(p,koftik, "i'll pray for you");
			}
		}
	}
	private void fallTeleportLocation(Player p, GameObject obj) {
		switch(obj.getID()) {
		case NORTH_STONE_STEP:
		case FIRST_REMAINING_BRIDGE:
			p.teleport(738, 584);
			break;
		case 898:
			p.teleport(756, 591);
			break;
		case 893:
			p.teleport(753, 608);
			break;
		case 892:
			p.teleport(734, 596);
			break;
		case 896:
			p.teleport(734, 610);
			break;
		case 910:
			p.teleport(734, 662);
			break;
		case 907:
			p.teleport(733, 646);
			break;
		case 906:
			p.teleport(731, 639);
			break;
		case 905:
		case 904:
			p.teleport(742, 630);
			break;
		case 908:
			p.teleport(760, 638);
			break;
		case 909:
			p.teleport(745, 656);
			break;
		case 902:
			p.teleport(759, 664);
			break;
		case 903:
			p.teleport(761, 613);
			break;
		case 901:
			p.teleport(727, 617);
			break;
		case 895:
			p.teleport(727, 618);
			break;
		case 899:
			p.teleport(734, 619);
			break;
		case 900:
			p.teleport(734, 666);
			break;
		case 894:
			p.teleport(763, 613);
			break;
		case 897:
			p.teleport(753, 585);
			break;
		}
	}
}
