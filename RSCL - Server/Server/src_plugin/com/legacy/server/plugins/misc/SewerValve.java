package com.legacy.server.plugins.misc;

import static com.legacy.server.plugins.Functions.message;

import com.legacy.server.model.entity.GameObject;
import com.legacy.server.model.entity.player.Player;
import com.legacy.server.plugins.listeners.action.ObjectActionListener;
import com.legacy.server.plugins.listeners.executive.ObjectActionExecutiveListener;

public class SewerValve implements ObjectActionExecutiveListener,
ObjectActionListener {

	public static final int SEWER_VALVE_1 = 412;
	public static final int SEWER_VALVE_2 = 413;
	public static final int SEWER_VALVE_3 = 414;
	public static final int SEWER_VALVE_4 = 415;
	public static final int SEWER_VALVE_5 = 416;
	public static final int LOG_RAFT = 432;
	public static final int LOG_RAFT_BACK = 433;

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if(obj.getID() == SEWER_VALVE_1 || obj.getID() == SEWER_VALVE_2 || obj.getID() == SEWER_VALVE_3 || obj.getID() == SEWER_VALVE_4 || obj.getID() == SEWER_VALVE_5) {
			if(command.equalsIgnoreCase("turn left")) {
				p.message("you turn the large metal");
				p.message("valve to the left");
				p.message("beneath the soil you can");
				p.message("hear the gushing of water");
				if(obj.getID() == SEWER_VALVE_1 && p.getCache().hasKey("VALVE_1_RIGHT")) {
					p.getCache().remove("VALVE_1_RIGHT");
				}

				if(obj.getID() == SEWER_VALVE_2 && !p.getCache().hasKey("VALVE_2_LEFT")) {
					p.getCache().store("VALVE_2_LEFT", true);
				} 

				if(obj.getID() == SEWER_VALVE_3 && p.getCache().hasKey("VALVE_3_RIGHT")) {
					p.getCache().remove("VALVE_3_RIGHT");
				}

				if(obj.getID() == SEWER_VALVE_4 && p.getCache().hasKey("VALVE_4_RIGHT")) {
					p.getCache().remove("VALVE_4_RIGHT");
				}

				if(obj.getID() == SEWER_VALVE_5 && !p.getCache().hasKey("VALVE_5_LEFT")) {
					p.getCache().store("VALVE_5_LEFT", true);
				} 

			} else if(command.equalsIgnoreCase("turn right")) {
				p.message("you turn the large metal");
				p.message("valve to the right");
				p.message("beneath the soil you can");
				p.message("hear the gushing of water");
				if(obj.getID() == SEWER_VALVE_1 && !p.getCache().hasKey("VALVE_1_RIGHT")) {
					p.getCache().store("VALVE_1_RIGHT", true);
				} 
				if(obj.getID() == SEWER_VALVE_2 && p.getCache().hasKey("VALVE_2_LEFT")) {
					p.getCache().remove("VALVE_2_LEFT");
				}
				if(obj.getID() == SEWER_VALVE_3 && !p.getCache().hasKey("VALVE_3_RIGHT")) {
					p.getCache().store("VALVE_3_RIGHT", true);
				}
				if(obj.getID() == SEWER_VALVE_4 && !p.getCache().hasKey("VALVE_4_RIGHT")) {
					p.getCache().store("VALVE_4_RIGHT", true);
				}
				if(obj.getID() == SEWER_VALVE_5 && p.getCache().hasKey("VALVE_5_LEFT")) {
					p.getCache().remove("VALVE_5_LEFT");
				}
			}
		}
		if(obj.getID() == LOG_RAFT) {
			message(p, "you carefully board the small raft");
			if(p.getCache().hasKey("VALVE_1_RIGHT") && p.getCache().hasKey("VALVE_2_LEFT") && p.getCache().hasKey("VALVE_3_RIGHT") && p.getCache().hasKey("VALVE_4_RIGHT") && p.getCache().hasKey("VALVE_5_LEFT")) {
				p.teleport(587,  3411);
				p.message("the raft washes up the sewer, the sewer passages end here");
			} else {
				p.teleport(621, 3465);
				p.message("the raft washes up the sewer, and stops at the first island");
				p.message("You need to find the right combination");
				p.message("of the 5 sewer valves above to get further");
			}
		}
		if(obj.getID() == LOG_RAFT_BACK) {
			p.message("the raft floats down the sewers");
			p.message("to the cave entrance");
			p.teleport(620, 3478);
		}
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		return obj.getID() == SEWER_VALVE_1 || obj.getID() == SEWER_VALVE_2 || obj.getID() == SEWER_VALVE_3 || obj.getID() == SEWER_VALVE_4 || obj.getID() == SEWER_VALVE_5 || obj.getID() == LOG_RAFT || obj.getID() == LOG_RAFT_BACK;
	}

}
