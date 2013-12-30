package org.it4y.net.tproxy;

import org.it4y.jni.JNILoader;
import org.it4y.jni.libc;
import org.it4y.jni.linuxutils;
import org.it4y.jni.tproxy;
import org.it4y.net.SocketUtils;
import org.it4y.net.linux.*;
import org.it4y.net.linux.SocketOptions;
import org.it4y.net.tproxy.TProxyClientSocket;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.net.*;


/**
 * Created by luc on 12/27/13.
 * You need todo this on linux to run it as normal user
 * setcap "cap_net_raw=+eip cap_net_admin=+eip" /usr/lib/jvm/java-1.7.0/bin/java
 * replace java executable path with your java and use that java
 * Note: after upgrade/changing this file you need to repeat this
 */
public class TProxyServerSocket extends ServerSocket {

    /*
     * This fields are manipulated by native c code so don't change it !!!
     */
    public TProxyServerSocket() throws IOException {
        super();
    }

    public SocketImpl getImplementation() {
        return SocketUtils.getImplementation(this);
    }

    public FileDescriptor getFileDescriptor() {
        return SocketUtils.getFileDescriptor(this);
    }

    public int getFd() {
        return SocketUtils.getFd(this);
    }

    public void setIPTransparentOption() throws libc.ErrnoException {
        linuxutils.setbooleanSockOption(this, SocketOptions.SOL_IP,SocketOptions.IP_TRANSPARENT,true);
    }


    public void initTProxy(InetAddress address,int port) throws Exception {
        setIPTransparentOption();
        setReuseAddress(true);
        //bind to localhost interface
        InetSocketAddress local=new InetSocketAddress(address,port);
        bind(local,500);
    }

    public TProxyClientSocket accepProxy() throws IOException {
        tproxy proxy=new tproxy();
        Socket c=accept();
        //get original destination address stored in client socket structure
        try {
          libc.sockaddr_in remote=linuxutils.getsockname(c);
          return new TProxyClientSocket(c,remote);
        } catch (libc.ErrnoException errno) {
            throw new IOException("libc error",errno);
        }
    }

}
