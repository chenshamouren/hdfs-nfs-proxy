package com.cloudera.hadoop.hdfs.nfs.nfs4;



import java.util.BitSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloudera.hadoop.hdfs.nfs.rpc.RPCBuffer;

/*
 * TODO fix this. I know this can be done cleaner.
 */

public class Bitmap implements MessageBase {
  protected static final Logger LOGGER = LoggerFactory.getLogger(Bitmap.class);

  protected static final int DEFAULT_NUM_BITS = 64;
  protected BitSet mMask = new BitSet(DEFAULT_NUM_BITS);
  @Override
  public void read(RPCBuffer buffer) {
    mMask = new BitSet(DEFAULT_NUM_BITS);
    int size = buffer.readUint32();
    for (int i = 0; i < size; i++) {
      int bitIndex = i * 32;
      int target = buffer.readInt();
      while(target != 0) {
        if((target & 0x01) != 0) {
          mMask.set(bitIndex);
        }
        target = target >>> 1;
        bitIndex++;
      }
    }
  }

  @Override
  public void write(RPCBuffer buffer) {
    if(mMask == null) {
      mMask = new BitSet(DEFAULT_NUM_BITS);
    }
    int bits = mMask.size();
    // all the written to an array of 32 bit integers
    int size = bits % 32 == 0 ? bits / 32 : (bits / 32) + 1;
    buffer.writeUint32(size);
    for (int i = 0; i < size; i++) {
      int target = 0;
      int startOffset = i * 32, endOffset = (i+1) * 32;
      for (int bitOffset = endOffset; bitOffset >= startOffset; bitOffset--) {
        if(mMask.get(bitOffset)) {
          target |= 0x01;
        }
        if(bitOffset != startOffset) {
          target = target << 1;
        }
      }
      buffer.writeInt(target);
    }
  }
  
  public boolean isEmpty() {
    return mMask.isEmpty();
  }
  public int size() {
    return mMask.size();
  }
  public void set(int bitIndex) {
    mMask.set(bitIndex);
  }
  
  public boolean isSet(int bitIndex) {
    return mMask.get(bitIndex);
  }
  
  public BitSet getMask() {
    return mMask;
  }

  public String toString() {
    return String.valueOf(mMask);
  }
  public static void main(String[] args) {
    RPCBuffer buffer = new RPCBuffer();
    buffer.writeUint32(2);
    buffer.writeUint32(0);
    buffer.writeUint32(2);
    buffer.flip();
    Bitmap map = new Bitmap();
    map.read(buffer);
    System.out.println(map);
  }
}