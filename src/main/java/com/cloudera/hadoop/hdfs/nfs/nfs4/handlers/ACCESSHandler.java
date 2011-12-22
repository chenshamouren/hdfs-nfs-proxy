package com.cloudera.hadoop.hdfs.nfs.nfs4.handlers;

import static com.cloudera.hadoop.hdfs.nfs.nfs4.Constants.*;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloudera.hadoop.hdfs.nfs.nfs4.NFS4Exception;
import com.cloudera.hadoop.hdfs.nfs.nfs4.NFS4Handler;
import com.cloudera.hadoop.hdfs.nfs.nfs4.Session;
import com.cloudera.hadoop.hdfs.nfs.nfs4.UserIDMapper;
import com.cloudera.hadoop.hdfs.nfs.nfs4.requests.ACCESSRequest;
import com.cloudera.hadoop.hdfs.nfs.nfs4.requests.CompoundRequest;
import com.cloudera.hadoop.hdfs.nfs.nfs4.responses.ACCESSResponse;
import com.cloudera.hadoop.hdfs.nfs.security.AuthenticatedCredentials;

public class ACCESSHandler extends OperationRequestHandler<ACCESSRequest, ACCESSResponse> {
  protected static final Logger LOGGER = LoggerFactory.getLogger(ACCESSHandler.class);
  public static final int ACCESS_READ    = 0x04;
  public static final int ACCESS_WRITE   = 0x02;
  public static final int ACCESS_EXECUTE = 0x01;

  @Override
  protected ACCESSResponse doHandle(NFS4Handler server, Session session,
      ACCESSRequest request) throws NFS4Exception {
    if(session.getCurrentFileHandle() == null) {
      throw new NFS4Exception(NFS4ERR_NOFILEHANDLE);
    }
    CompoundRequest compoundRequest = session.getCompoundRequest();
    AuthenticatedCredentials creds = compoundRequest.getCredentials();
    Path path = server.getPath(session.getCurrentFileHandle());
    try {

      UserIDMapper mapper = UserIDMapper.get(session.getConfiguration());
      String user = mapper.getUserForUID(creds.getUID(), null);
      if(user == null) {
        throw new Exception("Could not map " + creds.getUID() + " to user");
      }
      String group = mapper.getGroupForGID(creds.getGID(), null);
      if(group == null) {
        throw new Exception("Could not map " + creds.getGID() + " to group");
      }
          
      FileSystem fs = session.getFileSystem();
      FileStatus fileStatus = fs.getFileStatus(path);
      FsPermission perms = fileStatus.getPermission();
      //FsAction action = perms.getUserAction(); // always comes back ALL??

      int permissions = perms.toShort();
      int saved = permissions;
      int rtn = setPerms(permissions, false);
      permissions = permissions >> 3;
      if(group.equals(fileStatus.getGroup())) {
        rtn = setPerms(permissions, true);
      }
      permissions = permissions >> 3;
      if(user.equals(fileStatus.getOwner())) {
        rtn = setPerms(permissions, true);
      }    
      if(LOGGER.isDebugEnabled()) {
        LOGGER.debug("Checking access for '" + user + "' and path " + path + 
            " owned by '" + fileStatus.getOwner() + 
            "' permissions " + Integer.toOctalString(saved) +
            ", Returning " + Integer.toHexString(rtn));
      }
      int access = rtn & request.getAccess();

      ACCESSResponse response = createResponse();
      response.setStatus(NFS4_OK);
      response.setAccess(access);
      response.setSupported(access);
      return response;
    } catch (Exception e) {
      throw new NFS4Exception(NFS4ERR_SERVERFAULT, e);
    }
  }
  protected int setPerms(int permissions, boolean isOwner) {
    int rtn = 0;
    if(isSet(permissions, ACCESS_READ)) {
      rtn |= NFS_ACCESS_READ;
      rtn |= NFS_ACCESS_LOOKUP;
    }
    if(isSet(permissions, ACCESS_WRITE)) {
      rtn |= NFS_ACCESS_MODIFY;
      rtn |= NFS_ACCESS_EXTEND;
      if(isOwner) {
        rtn |= NFS_ACCESS_DELETE;
      }
    }
    if(isSet(permissions, ACCESS_EXECUTE)) {
      rtn |= NFS_ACCESS_EXECUTE;
    }
    return rtn;
  }
  protected boolean isSet(int access, int mode) {
    return (access & mode) == mode;
  }


  @Override
  protected ACCESSResponse createResponse() {
    return new ACCESSResponse();
  }

}