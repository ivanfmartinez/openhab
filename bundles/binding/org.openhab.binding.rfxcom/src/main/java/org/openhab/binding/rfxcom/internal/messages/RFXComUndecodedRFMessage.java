/**
 * Copyright (c) 2010-2016, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.rfxcom.internal.messages;

import java.util.Arrays;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.openhab.binding.rfxcom.RFXComValueSelector;
import org.openhab.binding.rfxcom.internal.RFXComException;
import org.openhab.core.library.items.NumberItem;
import org.openhab.core.library.items.StringItem;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.types.State;
import org.openhab.core.types.Type;
import org.openhab.core.types.UnDefType;

/**
 * RFXCOM undecoded message
 *
 * @author Ivan Martinez
 * @since 1.9.0
 */
public class RFXComUndecodedRFMessage extends RFXComBaseMessage {

    public enum SubType {
        AC(0),
        ARC(1),
        ATI(2),
        HIDEKI_UPM(3),
        LACROSSE_VIKING(4),
        AD(5),
        MERTIK(6),
        OREGON1(7),
        OREGON2(8),
        OREGON3(9),
        PROGUARD(10),
        VISONIC(11),
        NEC(12),
        FS20(13),
        RESERVED_0E(14),
        BLINDS(15),
        RUBICSON(16),
        AE(17),
        FINE_OFFSET(18),
        RGB(19),
        RTS(20),
        SELECT_PLUS(21),
        HOME_CONFORT(22),
        
        UNKNOWN(255);

        private final int subType;

        SubType(int subType) {
            this.subType = subType;
        }

        SubType(byte subType) {
            this.subType = subType;
        }

        public byte toByte() {
            return (byte) subType;
        }
    }

    private final static List<RFXComValueSelector> supportedValueSelectors = Arrays.asList(RFXComValueSelector.RAW_DATA);

    public SubType subType = SubType.UNKNOWN;

    public RFXComUndecodedRFMessage() {
        packetType = PacketType.UNDECODED_RF_MESSAGE;
    }

    public RFXComUndecodedRFMessage(byte[] data) {
        encodeMessage(data);
    }

    @Override
    public String toString() {
        String str = "";

        str += super.toString();
        str += "\n - Sub type = " + subType;
        str += "\n - Id = " + generateDeviceId();
        str += "\n - Data = " + DatatypeConverter.printHexBinary(getRawData());

        return str;
    }
    
    private byte[] getRawData() {
        return Arrays.copyOfRange(rawMessage, 4, rawMessage.length);
    }

    @Override
    public void encodeMessage(byte[] data) {

        super.encodeMessage(data);

        try {
            subType = SubType.values()[super.subType];
        } catch (Exception e) {
            subType = SubType.UNKNOWN;
        }
    }

    @Override
    public byte[] decodeMessage() {
        byte[] data = new byte[10];

        data[0] = 0x0B;
        data[1] = RFXComBaseMessage.PacketType.UNDECODED_RF_MESSAGE.toByte();
        data[2] = subType.toByte();
        data[3] = seqNbr;

        return data;
    }

    @Override
    public String generateDeviceId() {
        return "UNDECODED";
    }

    @Override
    public State convertToState(RFXComValueSelector valueSelector) throws RFXComException {

        org.openhab.core.types.State state = UnDefType.UNDEF;

        if (valueSelector.getItemClass() == StringItem.class) {

            if (valueSelector == RFXComValueSelector.RAW_DATA) {

                state = new StringType(DatatypeConverter.printHexBinary(rawMessage));

            } else {
                throw new RFXComException("Can't convert " + valueSelector + " to StringItem");
            }
        } else {

            throw new RFXComException("Can't convert " + valueSelector + " to " + valueSelector.getItemClass());

        }

        return state;
    }

    @Override
    public void convertFromState(RFXComValueSelector valueSelector, String id, Object subType, Type type,
            byte seqNumber) throws RFXComException {

        throw new RFXComException("Not supported");
    }

    @Override
    public Object convertSubType(String subType) throws RFXComException {

        for (SubType s : SubType.values()) {
            if (s.toString().equals(subType)) {
                return s;
            }
        }

        throw new RFXComException("Unknown sub type " + subType);
    }

    @Override
    public List<RFXComValueSelector> getSupportedValueSelectors() throws RFXComException {
        return supportedValueSelectors;
    }

}
