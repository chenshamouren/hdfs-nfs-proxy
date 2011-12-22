package com.cloudera.hadoop.hdfs.nfs.nfs4.handlers;

import static com.cloudera.hadoop.hdfs.nfs.nfs4.Constants.*;

import java.io.IOException;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloudera.hadoop.hdfs.nfs.nfs4.ChangeInfo;
import com.cloudera.hadoop.hdfs.nfs.nfs4.NFS4Exception;
import com.cloudera.hadoop.hdfs.nfs.nfs4.NFS4Handler;
import com.cloudera.hadoop.hdfs.nfs.nfs4.Session;
import com.cloudera.hadoop.hdfs.nfs.nfs4.attrs.ChangeID;
import com.cloudera.hadoop.hdfs.nfs.nfs4.requests.REMOVERequest;
import com.cloudera.hadoop.hdfs.nfs.nfs4.responses.REMOVEResponse;

public class REMOVEHandler extends OperationRequestHandler<REMOVERequest, REMOVEResponse> {
  protected static final Logger LOGGER = LoggerFactory.getLogger(REMOVEHandler.class);

  @Override
  protected REMOVEResponse doHandle(NFS4Handler server, Session session,
      REMOVERequest request) throws NFS4Exception, IOException {
    if(session.getCurrentFileHandle() == null) {
      throw new NFS4Exception(NFS4ERR_NOFILEHANDLE);
    }
    if("".equals(request.getName())) {
      throw new NFS4Exception(NFS4ERR_INVAL);
    }
    Path parentPath = server.getPath(session.getCurrentFileHandle());
    Path path = new Path(parentPath, request.getName());
    FileSystem fs = session.getFileSystem();
    if(!fs.exists(path)) {
      throw new NFS4Exception(NFS4ERR_NOENT);
    }
    REMOVEResponse response = createResponse();
    ChangeInfo changeInfo = new ChangeInfo();
    FileStatus parentStatus = fs.getFileStatus(parentPath);
    ChangeID changeIDBefore = new ChangeID();
    changeIDBefore.setChangeID(parentStatus.getModificationTime());
    changeInfo.setChangeIDBefore(changeIDBefore);

    fs.delete(path, false);
    
    parentStatus = fs.getFileStatus(parentPath);
    ChangeID changeIDAfter = new ChangeID();
    changeIDAfter.setChangeID(parentStatus.getModificationTime());
    changeInfo.setChangeIDAfter(changeIDAfter);
    changeInfo.setAtomic(true);
    response.setChangeInfo(changeInfo);
    response.setStatus(NFS4_OK);
    return response;
  }

  @Override
  protected REMOVEResponse createResponse() {
    return new REMOVEResponse();
  }

}