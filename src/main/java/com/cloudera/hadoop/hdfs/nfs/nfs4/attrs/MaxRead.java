package com.cloudera.hadoop.hdfs.nfs.nfs4.attrs;

import static com.cloudera.hadoop.hdfs.nfs.nfs4.Constants.*;

import com.cloudera.hadoop.hdfs.nfs.rpc.RPCBuffer;
public class MaxRead extends Attribute {
  public MaxRead() {
    super();
  }
  protected long mSize;
  @Override
  public void read(RPCBuffer buffer) {
    mSize = buffer.readUint64();
  }

  @Override
  public void write(RPCBuffer buffer) {
    buffer.writeUint64(mSize);
  }

  @Override
  public int getID() {
    return NFS4_FATTR4_MAXREAD;
  }

  public long getSize() {
    return mSize;
  }

  public void setSize(long size) {
    this.mSize = size;
  }
  
}