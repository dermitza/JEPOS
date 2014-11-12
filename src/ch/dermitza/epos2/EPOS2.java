/**
 * This file is part of JEPOS. Copyright (C) 2014 K. Dermitzakis
 * <dermitza@gmail.com>
 *
 * JEPOS is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * JEPOS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with JEPOS. If not, see <http://www.gnu.org/licenses/>.
 */
package ch.dermitza.epos2;

import ch.dermitza.jcanopen.canopen.SDOFrame;
import ch.dermitza.jcanopen.canopen.async.AbstractSDOTransceiver;

/**
 * 
 *
 * @author K. Dermitzakis
 * @version 0.04
 * @since   0.04
 */
public class EPOS2 {

    public static final String MOTION_PROFILES[] = {
        "Trapezoidal",
        "Sinusoidal"
    };
    public static final short OBJ_STATUSWORD = 0x6041;
    public static final short OBJ_CONTROLWORD = 0x6040;
    public static final short OBJ_OPERATION_DISPLAY_MODES = 0x6061;
    public static final short OBJ_OPERATION_MODES = 0x6060;
    public static final short OBJ_VELOCITY_MODE_SETTING_VALUE = 0x206B;
    public static final short OBJ_VELOCITY_ACTUAL_VALUE = 0x606C;
    public static final short OBJ_VELOCITY_ACTUAL_AVG_VALUE = 0x2028;
    public static final short OBJ_VELOCITY_SENSOR_ACTUAL_VALUE = 0x6069;
    public static final short OBJ_VELOCITY_DEMAND_VALUE = 0x606B;
    public static final short OBJ_POSITION_ACTUAL_VALUE = 0x6064;
    public static final short OBJ_TARGET_PROFILE_POSITION = 0x607A;
    public static final short OBJ_CURRENT_CONTROL_PARAMS = 0x60F6;
    public static final short OBJ_PROFILE_VELOCITY = 0x6081;
    public static final short OBJ_MOTION_PROFILE_TYPE = 0x6086;
    public static final short OBJ_PROFILE_ACCELERATION = 0x6083;
    public static final short OBJ_PROFILE_DECELERATION = 0x6084;
    public static final byte OBJ_CURRENT_PGAIN_SUBIDX = 0x01;
    public static final byte OBJ_CURRENT_IGAIN_SUBIDX = 0x02;
    public static final int ST_FAULT = 0;
    public static final int ST_START = 1;
    public static final int ST_NOT_READY_TO_SWITCH_ON = 2;
    public static final int ST_SWITCH_ON_DISABLED = 3;
    public static final int ST_READY_TO_SWITCH_ON = 4;
    public static final int ST_SWITCH_ON = 5;
    public static final int ST_REFRESH = 6;
    public static final int ST_MEASURE_INIT = 7;
    public static final int ST_OPERATION_ENABLE = 8;
    public static final int ST_QUICK_STOP = 9;
    public static final int ST_QUICK_STOP_ACTIVE_DISABLE = 10;
    public static final int ST_QUICK_STOP_ACTIVE_ENABLE = 11;
    public static final byte OPMODE_NOOP = 99;
    public static final byte OPMODE_VELOCITY = -2;
    public static final byte OPMODE_POSITION = -1;
    public static final byte OPMODE_PROFILE_VELOCITY = 3;
    public static final byte OPMODE_PROFILE_POSITION = 1;
    public static final byte OPMODE_INTERPOLATED_PROFILE_POSITION = 7;
    public static final byte OPMODE_HOMING = 6;
    public static final short CW_SHUTDOWN = 0x06;
    public static final short CW_SWITCH_ON = 0x07;
    public static final short CW_SWITCH_ON_ENABLE_OP = 0x0F;
    public static final short CW_DISABLE_VOLTAGE = 0x00;
    public static final short CW_QUICKSTOP = 0x02;
    public static final short CW_DISABLE_OPERATION = 0x07;
    public static final short CW_ENABLE_OPERATION = 0x0F;
    public static final short CW_FAULT_RESET = 0x80;
    public static final short CW_PROFILE_POSITION_RELATIVE = 0x7F;
    public static final short CW_PROFILE_POSITION_ABSOLUTE = 0x3F;
    public static final int FLAG_1 = 1;
    public static final int FLAG_2 = 2;
    public static final int FLAG_3 = 4;
    public static final int FLAG_4 = 8;
    public static final int START = 0;
    public static final int NOT_READY = (FLAG_1 << 8);
    public static final int SWITCH_ON_DISABLED = (FLAG_1 << 8) | (FLAG_3 << 4);
    public static final int READY_TO_SWITCH_ON = (FLAG_1 << 8) | (FLAG_2 << 4) | FLAG_1;
    public static final int SWITCHED_ON = (FLAG_1 << 8) | (FLAG_2 << 4) | FLAG_2 | FLAG_1;
    public static final int REFRESH = (FLAG_3 << 12) | (FLAG_1 << 8) | (FLAG_2 << 4) | FLAG_2 | FLAG_1;
    public static final int MEASURE_INIT = (FLAG_3 << 12) | (FLAG_1 << 8) | (FLAG_2 << 4) | (FLAG_1 << 4) | FLAG_2 | FLAG_1;
    public static final int OPERATION_ENABLE = (FLAG_1 << 8) | (FLAG_2 << 4) | (FLAG_1 << 4) | FLAG_3 | FLAG_2 | FLAG_1;
    public static final int QUICKSTOP_ACTIVE = (FLAG_1 << 8) | (FLAG_1 << 4) | FLAG_3 | FLAG_2 | FLAG_1;
    public static final int FAULT_REACTION_ACTIVE_DISABLED = (FLAG_1 << 8) | FLAG_4 | FLAG_3 | FLAG_2 | FLAG_1;
    public static final int FAULT_REACTION_ACTIVE_ENABLED = (FLAG_1 << 8) | (FLAG_1 << 4) | FLAG_4 | FLAG_3 | FLAG_2 | FLAG_1;
    public static final int FAULT = (FLAG_1 << 8);
    private short nodeID;
    private AbstractSDOTransceiver trans;

    public EPOS2(short nodeID) {
        this.nodeID = nodeID;
    }

    public void setTranceiver(AbstractSDOTransceiver trans) {
        this.trans = trans;
    }

    public short getNodeID() {
        return this.nodeID;
    }

    public void setNodeID(short nodeID) {
        this.nodeID = nodeID;
    }

    public int getState() {
        int state = -1;
        String str = "STATE: ";
        SDOFrame f = new SDOFrame(getNodeID(), false, OBJ_STATUSWORD, (byte) 0x00);

        int rsp = trans.transmitSDO(f);

        if ((rsp & (FLAG_3 << 12)) == (FLAG_3 << 12)) {
            // REFRESH or MEASURE_INIT
            if ((rsp & (FLAG_1 << 4)) == (FLAG_1 << 4)) {
                // MEASURE_INIT
                str += "MEASURE_INIT";
                state = ST_MEASURE_INIT;
            } else {
                // REFRESH
                str += "REFRESH";
                state = ST_REFRESH;
            }
        } else {
            // ALL OTHERS
            if ((rsp & (FLAG_1 << 8)) == (FLAG_1 << 8)) {
                // ALL OTHERS
                if ((rsp & (FLAG_3 << 4)) == (FLAG_3 << 4)) {
                    // SWITCH_ON_DISABLED
                    str += "SWITCH_ON_DISABLED";
                    state = ST_SWITCH_ON_DISABLED;
                } else {
                    // ALL OTHERS
                    if ((rsp & (FLAG_2 << 4)) == (FLAG_2 << 4)) {
                        // OPERATION_ENABLE, SWITCHED_ON, READY_TO_SWITCH_ON
                        if ((rsp & (FLAG_1 << 4)) == (FLAG_1 << 4)) {
                            // OPERATION_ENABLE
                            str += "OPERATION_ENABLE";
                            state = ST_OPERATION_ENABLE;
                        } else {
                            if ((rsp & (FLAG_2)) == (FLAG_2)) {
                                // SWITCHED_ON
                                str += "SWITCHED_ON";
                                state = ST_SWITCH_ON;
                            } else {
                                // READY_TO_SWITCH_ON
                                str += "READY_TO_SWITCH_ON";
                                state = ST_READY_TO_SWITCH_ON;
                            }
                        }
                    } else {
                        // QUICKSTOP_ACTIVE, FAULT_REACTION_ACTIVE_DISABLED
                        // FAULT_REACTION_ACTIVE_ENABLED, NOT_READY_TO_SWITCH_ON
                        if ((rsp & (FLAG_1 << 4)) == (FLAG_1 << 4)) {
                            // QUICKSTOP_ACTIVE, FAULT_REACTION_ACTIVE_ENABLED
                            if ((rsp & (FLAG_4)) == (FLAG_4)) {
                                // FAULT_REACTION_ACTIVE_ENABLED
                                str += "FAULT_REACTION_ACTIVE_ENABLED";
                                state = ST_QUICK_STOP_ACTIVE_ENABLE;
                            } else {
                                // QUICKSTOP_ACTIVE
                                str += "QUICKSTOP_ACTIVE";
                                state = ST_QUICK_STOP;
                            }
                        } else {
                            // FAULT_REACTION_ACTIVE_DISABLED, FAULT, NOT_READY_TO_SWITCH_ON
                            if ((rsp & (FLAG_4)) == (FLAG_4)) {
                                // FAULT_REACTION_ACTIVE_DISABLED, FAULT
                                if ((rsp & (FLAG_3)) == (FLAG_3)) {
                                    // FAULT_REACTION_ACTIVE_DISABLED
                                    str += "FAULT_REACTION_ACTIVE_DISABLED";
                                    state = ST_QUICK_STOP_ACTIVE_DISABLE;
                                } else {
                                    // FAULT
                                    str += "FAULT";
                                    state = ST_FAULT;
                                }
                            } else {
                                str += "NOT_READY_TO_SWITCH_ON";
                                state = ST_NOT_READY_TO_SWITCH_ON;
                                // NOT_READY_TO_SWITCH_ON
                            }
                        }
                    }
                }
            } else {
                str += "START";
                state = ST_START;
                // START
            }
        }
        System.out.println(str);
        return state;
        /*
         if((rsp & NOT_READY) == NOT_READY){
         str += "NOT_READY";
         state = ST_NOT_READY_TO_SWITCH_ON;
         }else if((rsp & SWITCH_ON_DISABLED) == SWITCH_ON_DISABLED){
         str += "SWITCH_ON_DISABLED";
         state = ST_SWITCH_ON_DISABLED;
         }else if((rsp & READY_TO_SWITCH_ON) == READY_TO_SWITCH_ON){
         str += "READY_TO_SWITCH_ON";
         state = ST_READY_TO_SWITCH_ON;
         }else if((rsp & SWITCHED_ON) == SWITCHED_ON){
         str += "SWITCHED_ON";
         state = ST_SWITCH_ON;
         }else if((rsp & REFRESH) == REFRESH){
         str += "REFRESH";
         state = ST_REFRESH;
         }else if((rsp & MEASURE_INIT) == MEASURE_INIT){
         str += "MEASURE_INIT";
         state = ST_MEASURE_INIT;
         }else if((rsp & OPERATION_ENABLE) == OPERATION_ENABLE){
         str += "OPERATION_ENABLE";
         state = ST_OPERATION_ENABLE;
         }else if((rsp & QUICKSTOP_ACTIVE) == QUICKSTOP_ACTIVE){
         str += "QUICKSTOP_ACTIVE";
         state = ST_QUICK_STOP;
         }else 
            
         if((rsp & FAULT_REACTION_ACTIVE_ENABLED) == FAULT_REACTION_ACTIVE_ENABLED){
         str += "FAULT_REACTION_ACTIVE_ENABLED";
         state = ST_QUICK_STOP_ACTIVE_ENABLE;
         } else if((rsp & FAULT_REACTION_ACTIVE_DISABLED) == FAULT_REACTION_ACTIVE_DISABLED){
         str += "FAULT_REACTION_ACTIVE_DISABLED";
         state = ST_QUICK_STOP_ACTIVE_DISABLE;
         }
         else if((rsp & FAULT) == FAULT){
         str += "FAULT";
         state = ST_FAULT;
         } else if((rsp & START) == START){
         str += "START";
         state = ST_START;
         }else {
         str += "UNKNOWN";
         }
         System.out.println(str);
         return state;

         if(rsp != -1){
         boolean[] bits = new boolean[16];
         bits[0] = (rsp & 0x0001) == 0x0001;
         bits[1] = (rsp & 0x0002) == 0x0002;
         bits[2] = (rsp & 0x0004) == 0x0004;
         bits[3] = (rsp & 0x0008) == 0x0008;

         bits[4] = (rsp & 0x0010) == 0x0010;
         bits[5] = (rsp & 0x0020) == 0x0020;
         bits[6] = (rsp & 0x0040) == 0x0040;
         bits[7] = (rsp & 0x0080) == 0x0080;

         bits[8] = (rsp & 0x0100) == 0x0100;
         bits[9] = (rsp & 0x0200) == 0x0200;
         bits[10] = (rsp & 0x0400) == 0x0400;
         bits[11] = (rsp & 0x0800) == 0x0800;

         bits[12] = (rsp & 0x1000) == 0x1000;
         bits[13] = (rsp & 0x2000) == 0x2000;
         bits[14] = (rsp & 0x4000) == 0x4000;
         bits[15] = (rsp & 0x8000) == 0x8000;

         if (bits[14]) {
         if (bits[4]) {
         System.out.println("State: Measure Init");
         return ST_MEASURE_INIT;
         } else {
         System.out.println("State: Refresh");
         return ST_REFRESH;
         }
         } else {
         if (!bits[8]) {
         System.out.println("State: Start");
         return ST_START;
         } else {
         if (bits[6]) {
         System.out.println("State: Switch on disabled");
         return ST_SWITCH_ON_DISABLED;
         } else {
         if (bits[5]) {
         if (bits[4]) {
         System.out.println("State: Operation Enable");
         return ST_OPERATION_ENABLE;
         } else {
         if (bits[1]) {
         System.out.println("State: Switched On");
         return ST_SWITCH_ON;
         } else {
         System.out.println("State: Ready to Switch On");
         return ST_READY_TO_SWITCH_ON;
         }
         }
         } else {
         if (!bits[3]) {
         if (bits[2]) {
         System.out.println("State: Quick Stop Active");
         return ST_QUICK_STOP;
         } else {
         System.out.println("State: Not Ready to Switch On");
         return ST_NOT_READY_TO_SWITCH_ON;
         }
         } else {
         if (bits[4]) {
         System.out.println("State: Fault Reaction Active (Enabled)");
         return ST_QUICK_STOP_ACTIVE_ENABLE;
         } else {
         if (bits[2]) {
         System.out.println("State: Fault Reaction Active (Disabled)");
         return ST_QUICK_STOP_ACTIVE_DISABLE;
         } else {
         System.out.println("State: Fault");
         return ST_FAULT;
         }
         }
         }
         }
         }
         }
         }
         }
         */
    }

    public long faultReset() {
        //System.out.println("Fault reset");
        SDOFrame f = new SDOFrame(getNodeID(), true, OBJ_CONTROLWORD, (byte) 0x00, CW_FAULT_RESET);
        return trans.transmitSDO(f);
    }

    public long shutdown() {
        //System.out.println("Shutdown");
        SDOFrame f = new SDOFrame(getNodeID(), true, OBJ_CONTROLWORD, (byte) 0x00, CW_SHUTDOWN);
        return trans.transmitSDO(f);
    }

    public long switchOn() {
        //System.out.println("Switch on");
        SDOFrame f = new SDOFrame(getNodeID(), true, OBJ_CONTROLWORD, (byte) 0x00, CW_SWITCH_ON);
        return trans.transmitSDO(f);
    }

    public long disableOperation() {
        //System.out.println("Disable operation");
        SDOFrame f = new SDOFrame(getNodeID(), true, OBJ_CONTROLWORD, (byte) 0x00, CW_DISABLE_OPERATION);
        return trans.transmitSDO(f);
    }

    public long disableVoltage() {
        //System.out.println("Disable voltage");
        SDOFrame f = new SDOFrame(getNodeID(), true, OBJ_CONTROLWORD, (byte) 0x00, CW_DISABLE_VOLTAGE);
        return trans.transmitSDO(f);
    }

    public void enableMotor(byte opMode) {
        //System.out.println("Enable motor");
        int state = getState();
        if (state == ST_SWITCH_ON) {
            enableOperation();
        }

        if (opMode != OPMODE_NOOP) {
            int currMode = getOperationMode();
            //if ((currMode & 0xFF) != (opMode & 0xFF)) {
                setOperationMode(opMode);
            //} else {
                //System.err.println("SAME_MODE");
            //}
        } else {
            //System.err.println("NO_OPERATION");
        }
    }

    public int getOperationMode() {
        //System.out.println("Get operation mode");
        SDOFrame f = new SDOFrame(getNodeID(), false, OBJ_OPERATION_DISPLAY_MODES, (byte) 0x00);
        return trans.transmitSDO(f);
    }

    public long setOperationMode(byte opMode) {
        //System.out.println("Set operation mode");
        SDOFrame f = new SDOFrame(getNodeID(), true, OBJ_OPERATION_MODES, (byte) 0x00, opMode);
        return trans.transmitSDO(f);
    }
//    public long setTargetVelocity(long velocity) {
//        //System.out.println("Set target velocity");
//        SDOFrame f = new SDOFrame(nodeID, true, OBJ_VELOCITY_MODE_SETTING_VALUE, (byte) 0x00, (int) velocity);
//        return trans.transmitSDO(f);
//    }
     
    public int setTargetVelocity(int velocity) {
        //System.out.println("Set target velocity");
        SDOFrame f = new SDOFrame(nodeID, true, OBJ_VELOCITY_MODE_SETTING_VALUE, (byte) 0x00, (int) velocity);
        return trans.transmitSDO(f);
    }
//    public int setVelocityMode() {
//        //System.out.println("Set target velocity");
//        SDOFrame f = new SDOFrame(nodeID, true, OBJ_VELOCITY_MODE_SETTING_VALUE, (byte) 0x00, (int) velocity);
//        return trans.transmitSDO(f);
//    }
        

    public long getTargetVelocity() {
        //System.out.println("Get target velocity");
        SDOFrame f = new SDOFrame(getNodeID(), false, OBJ_VELOCITY_MODE_SETTING_VALUE, (byte) 0x00);
        return trans.transmitSDO(f);
    }

    // WTF IS THIS?
//    public long startVelocity() {
//        //System.out.println("Start velocity");
//        SDOFrame f = new SDOFrame(getNodeID(), true, OBJ_CONTROLWORD, (byte) 0x00, (short) 0x000f);
//        return trans.transmitSDO(f);
//    }
    
    public long startVelocity() {
        //System.out.println("Start velocity");
        SDOFrame f = new SDOFrame(getNodeID(), true, OBJ_VELOCITY_MODE_SETTING_VALUE, (byte) 0x00, (short) 0x000f);
        return trans.transmitSDO(f);
    }
        
        

    public long stopVelocity() {
        //System.out.println("Stop velocity");
        SDOFrame f = new SDOFrame(getNodeID(), true, OBJ_VELOCITY_MODE_SETTING_VALUE, (byte) 0x00, (int) 0);
        return trans.transmitSDO(f);
    }

    public long readVelocitySensorActual() {
        //System.out.println("Read velocity sensor actual");
        SDOFrame f = new SDOFrame(getNodeID(), false, OBJ_VELOCITY_SENSOR_ACTUAL_VALUE, (byte) 0x00);
        return trans.transmitSDO(f);
    }

    public long readVelocityActualAvg() {
        //System.out.println("Read velocity actual average");
        SDOFrame f = new SDOFrame(getNodeID(), false, OBJ_VELOCITY_ACTUAL_AVG_VALUE, (byte) 0x00);
        return trans.transmitSDO(f);
    }

    public long readVelocityActual() {
        //System.out.println("Read velocity actual");
        SDOFrame f = new SDOFrame(getNodeID(), false, OBJ_VELOCITY_ACTUAL_VALUE, (byte) 0x00);
        return trans.transmitSDO(f);
    }

    public long readVelocityDemand() {
        //System.out.println("Read velocity demand");
        SDOFrame f = new SDOFrame(getNodeID(), false, OBJ_VELOCITY_DEMAND_VALUE, (byte) 0x00);
        return trans.transmitSDO(f);
    }

    // ================================================================ VELOCITY
    public long readStatusWord() {
        //System.out.println("Read status word");
        SDOFrame f = new SDOFrame(getNodeID(), false, OBJ_STATUSWORD, (byte) 0x00);
        return trans.transmitSDO(f);
    }

    // POSITION ================================================================
    public int readPositionActual() {
        //System.out.println("Read position actual");
        SDOFrame f = new SDOFrame(getNodeID(), false, OBJ_POSITION_ACTUAL_VALUE, (byte) 0x00);
        return trans.transmitSDO(f);
    }

    public int readTargetProfilePosition() {
        //System.out.println("Read target profile position");
        SDOFrame f = new SDOFrame(getNodeID(), false, OBJ_TARGET_PROFILE_POSITION, (byte) 0x00);
        return trans.transmitSDO(f);
    }

    public int setTargetProfilePosition(int position) {
        //System.out.println("Set target profile position");
        SDOFrame f = new SDOFrame(getNodeID(), true, OBJ_TARGET_PROFILE_POSITION, (byte) 0x00, position);
        return trans.transmitSDO(f);
    }

    public int readProfileVelocity() {
        SDOFrame f = new SDOFrame(getNodeID(), false, OBJ_PROFILE_VELOCITY, (byte) 0x00);
        return trans.transmitSDO(f);
    }

    public int setProfileVelocity(int velocity) {
        SDOFrame f = new SDOFrame(getNodeID(), true, OBJ_PROFILE_VELOCITY, (byte) 0x00, velocity);
        return trans.transmitSDO(f);
    }

    public int readProfileAcceleration() {
        SDOFrame f = new SDOFrame(getNodeID(), false, OBJ_PROFILE_ACCELERATION, (byte) 0x00);
        return trans.transmitSDO(f);
    }

    public int setProfileAcceleration(int acceleration) {
        SDOFrame f = new SDOFrame(getNodeID(), true, OBJ_PROFILE_ACCELERATION, (byte) 0x00, acceleration);
        return trans.transmitSDO(f);
    }

    public int readProfileDeceleration() {
        SDOFrame f = new SDOFrame(getNodeID(), false, OBJ_PROFILE_DECELERATION, (byte) 0x00);
        return trans.transmitSDO(f);
    }

    public int setProfileDeceleration(int deceleration) {
        SDOFrame f = new SDOFrame(getNodeID(), true, OBJ_PROFILE_DECELERATION, (byte) 0x00, deceleration);
        return trans.transmitSDO(f);
    }

    public int readMotionProfileType() {
        SDOFrame f = new SDOFrame(getNodeID(), false, OBJ_MOTION_PROFILE_TYPE, (byte) 0x00);
        return trans.transmitSDO(f);
    }

    public int setMotionProfileType(short type) {
        SDOFrame f = new SDOFrame(getNodeID(), true, OBJ_MOTION_PROFILE_TYPE, (byte) 0x00, type);
        return trans.transmitSDO(f);
    }

    // WTF IS THIS?
    public long startProfilePosition(boolean absolute) {
        //System.out.println("Start profile position");

        /*
         int halt        = mode==HALT ?      0x0100 : 0x0000;
         int rel         = mode==RELATIVE ?  0x0040 : 0x0000;
         int nowait      = !wait ?           0x0020 : 0x0000;
         int newsetpoint = new_point ?       0x0010 : 0x0000;
         */

        short cw = absolute ? CW_PROFILE_POSITION_ABSOLUTE : CW_PROFILE_POSITION_RELATIVE;
        SDOFrame f = new SDOFrame(getNodeID(), true, OBJ_CONTROLWORD, (byte) 0x00, cw);
        return trans.transmitSDO(f);
    }

    // ================================================================ POSITION
    // CURRENT =================================================================
    public int readTargetCurrent() {
        return -1;
    }

    public int setTargetCurrent(int current) {
        return -1;
    }

    // ================================================================= CURRENT
    public boolean isTargetReached() {
        //System.out.println("Is target reached");
        SDOFrame f = new SDOFrame(getNodeID(), false, OBJ_STATUSWORD, (byte) 0x00);
        int ans = trans.transmitSDO(f);
        return ((ans & 0x0400) == 0x0400);
    }

    public long enableOperation() {
        //System.out.println("Enable operation");
        SDOFrame f = new SDOFrame(getNodeID(), true, OBJ_CONTROLWORD, (byte) 0x00, CW_ENABLE_OPERATION);
        return trans.transmitSDO(f);
    }

    public void disableController() {
        //System.out.println("Disable controller");
        disableVoltage();
    }

    public boolean enableController() {
        int retries = 0;
        boolean enabled = false;

        int state = getState();
        while (!enabled && retries < 10) {
            switch (state) {
                case ST_FAULT:
                    faultReset();
                    retries++;
                    break;
                case ST_START:
                    break;
                case ST_NOT_READY_TO_SWITCH_ON:
                    break;
                case ST_SWITCH_ON_DISABLED:
                    retries++;
                    shutdown();
                    break;
                case ST_READY_TO_SWITCH_ON:
                    switchOn();
                    break;
                case ST_SWITCH_ON:
                    enabled = true;
                    break;
                case ST_REFRESH:
                    break;
                case ST_MEASURE_INIT:
                    break;
                case ST_OPERATION_ENABLE:
                    disableOperation();
                    break;
                case ST_QUICK_STOP:
                    disableVoltage();
                case ST_QUICK_STOP_ACTIVE_DISABLE:
                    break;
                case ST_QUICK_STOP_ACTIVE_ENABLE:
                    break;
            }
            state = getState();
        }
        return enabled;
    }
}
