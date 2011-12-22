package com.cloudera.hadoop.hdfs.nfs.nfs4.attrs;

import static com.cloudera.hadoop.hdfs.nfs.nfs4.Constants.*;
public class LinkSupport extends BooleanAttribute {
  public LinkSupport() {
    super();
  }
  @Override
  public int getID() {
    return NFS4_FATTR4_LINK_SUPPORT;
  }
  
}