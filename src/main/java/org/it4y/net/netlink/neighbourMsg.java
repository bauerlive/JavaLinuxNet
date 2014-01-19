/*
 *
 *  * Copyright (C) 2014  Luc.Willems @ T.M.M.
 *  *
 *  * This file is released under the LGPL.
 *  * see license.txt for terms and conditions
 *
 */

package org.it4y.net.netlink;

import org.it4y.jni.linux.if_neighbour;

import java.nio.ByteBuffer;

/**
 * Created by luc on 1/2/14.
 */
public class neighbourMsg extends NlMessage {
    byte ndm_family;
    byte ndm_pad1;
    byte ndm_pad2;
    byte ndm_ifindex;
    byte ndm_state;
    byte ndm_flags;
    byte ndm_type;

    public neighbourMsg(final ByteBuffer msg) {
        super(msg);
        ndm_family = msg.get();
        ndm_pad1 = msg.get();
        ndm_pad2 = msg.get();
        ndm_ifindex = msg.get();
        ndm_state = msg.get();
        ndm_flags = msg.get();
        ndm_type = msg.get();
        msg.get();
        msg.getInt();
        parseRTAMessages(msg);
    }

    @Override
    public int getRTAIndex(final String name) {
        for (final int i  : if_neighbour.NDA_NAMES.keySet()) {
            if (name.equals(if_neighbour.NDA_NAMES.get(i))) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public RTAMessage createRTAMessage(final int position, final ByteBuffer msg) {
        return new neighbourRTAMessages(position, msg);
    }

    public String toString() {
        final StringBuilder s = new StringBuilder(128);
        s.append(super.toString());
        s.append("fam: ").append(ndm_family);
        s.append(" ifindex:").append(ndm_ifindex);
        s.append(" state:0x").append(Integer.toHexString((int) ndm_state & 0xff));
        s.append(" flags:0x").append(Integer.toHexString((int) ndm_state & 0xff));
        s.append(" type:0x").append(Integer.toHexString((int) ndm_type & 0xff));
        s.append('\n');
        //dump rta messages
        for (final RTAMessage r : rtaMessages.values()) {
            if (r.getType() == if_neighbour.NDA_DST) {
                s.append(' ').append(r.toString()).append(' ').append(r.getInetAddress()).append('\n');
            } else {
                s.append(' ').append(r.toString()).append('\n');
            }
        }
        return s.toString();
    }
}
