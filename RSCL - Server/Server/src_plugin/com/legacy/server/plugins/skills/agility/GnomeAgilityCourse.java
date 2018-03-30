package com.legacy.server.plugins.skills.agility;

import static com.legacy.server.plugins.Functions.AGILITY;
import static com.legacy.server.plugins.Functions.getNearestNpc;
import static com.legacy.server.plugins.Functions.inArray;
import static com.legacy.server.plugins.Functions.message;
import static com.legacy.server.plugins.Functions.movePlayer;
import static com.legacy.server.plugins.Functions.npcYell;
import static com.legacy.server.plugins.Functions.playerTalk;
import static com.legacy.server.plugins.Functions.sleep;

import com.legacy.server.model.entity.GameObject;
import com.legacy.server.model.entity.npc.Npc;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.plugins.listeners.action.ObjectActionListener;
import com.legacy.server.plugins.listeners.executive.ObjectActionExecutiveListener;

public class GnomeAgilityCourse implements ObjectActionListener, ObjectActionExecutiveListener {
	
	private static final int BALANCE_LOG = 655;
	private static final int NET = 647;
	private static final int WATCH_TOWER = 648;
	private static final int ROPE_SWING = 650;
	private static final int LANDING = 649;
	private static final int SECOND_NET = 653;
	private static final int PIPE = 654;
	
	public static int[] obstacleOrder = {BALANCE_LOG, NET, WATCH_TOWER, ROPE_SWING, LANDING, SECOND_NET, PIPE};
	
	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		return inArray(obj.getID(), BALANCE_LOG, NET, WATCH_TOWER, ROPE_SWING, LANDING, SECOND_NET, PIPE);
	}
	
	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		Npc gnomeTrainer = getNearestNpc(p, 578, 10);
		p.setBusy(true);
		switch(obj.getID()) {
			case BALANCE_LOG:
				p.message("You stand on slippery log");
				for(int y = 494; y < 500;y++) {
					movePlayer(p, 692, y);
					sleep(650);
				}
				p.message("and walk across");
			break;
			case NET:
				if(gnomeTrainer != null) {
					npcYell(p, gnomeTrainer, "Move it, move it, move it");
				}
				p.message("You climb the net");
				sleep(2000);
				movePlayer(p, 692, 1448);
				p.message("and pull yourself onto the platform");
				break;
				
			case WATCH_TOWER:
				if(gnomeTrainer != null) {
					npcYell(p, gnomeTrainer, "that's it, straight up, no messing around");
				}
				p.message("You pull yourself up the tree");
				sleep(1000);
				movePlayer(p, 693, 2394);
				p.message("to the platform above");
				break;
				
			case ROPE_SWING:
				p.message("You reach out and grab the rope swing");
				sleep(1000);
				p.message("You hold on tight");
				sleep(2000);
				movePlayer(p, 685, 2396);
				p.message("and swing to the opposite platform");
				break;
			case LANDING:
				p.message("You hang down from the tower");
				sleep(1000);
				movePlayer(p, 683, 506);
				p.message("and drop to the floor");
				playerTalk(p, null, "ooof");
				break;
			case SECOND_NET:
				if(gnomeTrainer != null) {
					npcYell(p, gnomeTrainer, "my granny can move faster than you");
				}
				p.message("You take a few steps back");
				int initialY = p.getY();
				movePlayer(p, p.getX(), initialY + 2);
				sleep(650);
				p.message("and run towards the net");
				movePlayer(p, p.getX(), initialY - 2);
				sleep(650);
				movePlayer(p, p.getX(), initialY - 2);
				break;
			case PIPE:
				message(p, "You squeeze through the pipe", "and shuffle down into it");
				movePlayer(p, 683, 494);
				if(gnomeTrainer != null) {
					npcYell(p, gnomeTrainer, "that's the way, well done");
				}
				break;
		}
		p.incExp(AGILITY, 7.5, true);
		AgilityUtils.setNextObstacle(p, obj.getID(), obstacleOrder, 75.0);
		p.setBusy(false);
	}
}
