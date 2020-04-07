package com.wq.clink.dispather;

import com.wq.clink.dispather.box.abs.Packet;
import com.wq.clink.dispather.box.abs.ReceivePacket;
import com.wq.clink.dispather.box.abs.SendPacket;

import java.io.*;

public class FileReceivePacket extends ReceivePacket<FileOutputStream,File> {
    private final File file;

    public FileReceivePacket(long len, File file) {
        super(len);
        this.file = file;
    }


    @Override
    public byte type() {
        return Packet.TYPE_STREAM_FILE;
    }

    @Override
    protected FileOutputStream createStream() {
        try {
            return new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected File buildEntity(FileOutputStream stream) {
        return file;
    }
}