package com.cloudera.hadoop.hdfs.nfs.nfs4.attrs;


import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;

import com.cloudera.hadoop.hdfs.nfs.nfs4.NFS4Exception;
import com.cloudera.hadoop.hdfs.nfs.nfs4.NFS4Handler;
import com.cloudera.hadoop.hdfs.nfs.nfs4.Session;

public class SymLinkSupportHandler extends AttributeHandler<SymLinkSupport> {

  @Override
  public SymLinkSupport get(NFS4Handler server, Session session,
      FileSystem fs, FileStatus fileStatus) throws NFS4Exception {
    SymLinkSupport support = new SymLinkSupport();
    support.set(false);
    return support;
  }

}