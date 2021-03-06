package com.wq.clink.core.frames;

import com.wq.clink.core.IoArgs;

/**
 * Author: weiqiang
 * Time: 2020/4/7 上午10:53
 */
public class ReceiveHeaderFrame extends AbsReceiveFrame {
    private final byte[] body;
    public String fileName;

    ReceiveHeaderFrame(byte[] header) {
        super(header);
        body = new byte[bodyRemaining];
    }

    @Override
    protected int consumBody(IoArgs args) {
        int offset = body.length - bodyRemaining;
        int i = args.writeTo(body, offset);
        byte[] names = new byte[body[6]];
        args.writeTo(names, 0);
        fileName = new String(names);
        return i;
    }

    public long getPacketLength() {
        return ((((long) body[0]) & 0xFFL) << 32)
                | ((((long) body[1]) & 0xFFL) << 24)
                | ((((long) body[2]) & 0xFFL) << 16)
                | ((((long) body[3]) & 0xFFL) << 8)
                | (((long) body[4]) & 0xFFL);
    }

    public byte getPacketType() {
        return body[5];
    }

    public byte[] getPacketHeaderInfo() {
        if (body.length > SendHeaderFrame.PACKET_HEADER_FRAME_MIN_LENGTH) {
            byte[] headerInfo = new byte[body.length - SendHeaderFrame.PACKET_HEADER_FRAME_MIN_LENGTH];
            System.arraycopy(body, SendHeaderFrame.PACKET_HEADER_FRAME_MIN_LENGTH,
                    headerInfo, 0, headerInfo.length);
            return headerInfo;
        }
        return null;
    }

}
