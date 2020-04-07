package com.wq.clink.dispather.box;

import com.wq.clink.core.Frame;
import com.wq.clink.dispather.box.abs.Packet;
import com.wq.clink.dispather.box.abs.SendPacket;

import java.io.*;

public class FileSendPacket extends SendPacket<FileInputStream> {
    private final File file;

    public FileSendPacket(File file) {
        this.file = file;
            this.length = file.length();
    }


    @Override
    public byte type() {
        return TYPE_STREAM_FILE;
    }

    @Override
    protected FileInputStream createStream() {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}