package org.it4y.jni;

import org.it4y.jni.linux.socket;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteOrder;

/**
 * Created by luc on 1/9/14.
 */
public class libcTest {
    Logger log= LoggerFactory.getLogger(libcTest.class);

    @Test
    public void testNtol() throws Exception {
     log.info("System endian: {}", ByteOrder.nativeOrder());
     /* convert 32 bits from network to local format */
     int x=0x44332211;
     Assert.assertEquals(0x11223344,libc.ntol(x));
     x=0xffeeddcc;
     Assert.assertEquals(0xccddeeff,libc.ntol(x));

    }

    @Test
    public void testToInetAddress() throws Exception {
        InetAddress ip=libc.toInetAddress(0x01020304);
        Assert.assertNotNull(ip);
        Assert.assertEquals("/1.2.3.4", ip.toString());

        ip=libc.toInetAddress(0xfffffffe);
        Assert.assertNotNull(ip);
        Assert.assertEquals("/255.255.255.254", ip.toString());

    }

    @Test
    public void testin_addr() throws Exception {
        libc.in_addr a;
        a=new libc.in_addr();
        Assert.assertEquals(0, a.address);

        a=new libc.in_addr(0x01020304);
        Assert.assertEquals(0x01020304, a.address);
        InetAddress ax=a.toInetAddress();
        Assert.assertNotNull(ax);
        Assert.assertEquals("/1.2.3.4", ax.toString());
        Assert.assertEquals("0x01020304",a.toString());

        a=new libc.in_addr(0xfffffffe);
        Assert.assertEquals(0xfffffffe, a.address);
        ax=a.toInetAddress();
        Assert.assertNotNull(ax);
        Assert.assertEquals("/255.255.255.254", ax.toString());
        Assert.assertEquals("0xfffffffe",a.toString());

    }

    @Test
    public void testSockaddr_in() throws Exception {
        libc.sockaddr_in sa;

        sa=new libc.sockaddr_in(0,0, socket.AF_INET);
        Assert.assertNotNull(sa);
        Assert.assertEquals(0, sa.address);
        Assert.assertEquals(0, sa.port);
        Assert.assertEquals(socket.AF_INET,sa.family);

        sa=new libc.sockaddr_in(0x01020304,0x1234,socket.AF_INET);
        Assert.assertEquals(0x01020304, sa.address);
        Assert.assertEquals(0x1234,sa.port);
        Assert.assertEquals(10,socket.AF_INET6);

        sa=new libc.sockaddr_in(0x7f000001,0xffff,socket.AF_INET);
        Assert.assertEquals(0x7f000001, sa.address);
        Assert.assertEquals(0xffff,sa.port);
        Assert.assertEquals(socket.AF_INET,sa.family);

        InetSocketAddress isa=sa.toInetSocketAddress();
        Assert.assertNotNull(isa);
        Assert.assertEquals("/127.0.0.1:65535",isa.toString());
        Assert.assertEquals("0x7f000001:65535",sa.toString());


        sa=new libc.sockaddr_in(0xAABBCCDD,0xffff,socket.AF_INET);
        Assert.assertEquals(0xAABBCCDD, sa.address);
        Assert.assertEquals(0xffff,sa.port);
        Assert.assertEquals(socket.AF_INET,sa.family);

        isa=sa.toInetSocketAddress();
        Assert.assertNotNull(isa);
        Assert.assertEquals("/170.187.204.221:65535",isa.toString());
        Assert.assertEquals("0xaabbccdd:65535",sa.toString());

        sa=new libc.sockaddr_in(0xfffffffe,0xffff,socket.AF_INET);
        Assert.assertEquals(0xfffffffe, sa.address);
        Assert.assertEquals(0xffff,sa.port);
        Assert.assertEquals(socket.AF_INET,sa.family);

        isa=sa.toInetSocketAddress();
        Assert.assertNotNull(isa);
        Assert.assertEquals("/255.255.255.254:65535",isa.toString());
        Assert.assertEquals("0xfffffffe:65535",sa.toString());

        InetAddress ia=sa.toInetAddress();
        Assert.assertNotNull(ia);
        Assert.assertEquals("/255.255.255.254",ia.toString());

    }

    @Test
    public void testTCPInfo() {
        libc.tcp_info tcp_info = new libc.tcp_info();

        Assert.assertNotNull(tcp_info);
        StringBuilder s=new StringBuilder();
        s.append("state:0 ca_state:0").append("\n ");
        s.append("retransmits:0 probes:0 backoff:0 options:0").append("\n ");
        s.append("snd_wscale:0 rcv_wscale:0 rto:0 ato:0 snd_mss:0 rcv_mss:0").append("\n ");
        s.append("unacked:0 sacked:0 lost:0 retrans:0 fackets:0").append("\n ");
        s.append("last_data_sent:0 last_ack_sent:0").append("\n ");
        s.append("last_data_recv:0 last_ack_recv:0").append("\n ");
        s.append("pmtu:0 rcv_ssthresh:0 rtt:0 rttvar:0 snd_ssthresh:0 snd_cwnd:0 advmss:0").append("\n ");
        s.append("reordering:0 rcv_rtt:0 rcv_space:0").append("\n ");
        s.append("total_retrans:0");
        Assert.assertEquals(s.toString(),tcp_info.toString());

        //set some dummy data. make sure we do not have uint -> int conversion issues
        tcp_info.setdata(
           (byte)0xff, // tcpi_state,
           (byte)0xff, // tcpi_ca_state,
           (byte)0xff, // tcpi_retransmits,
           (byte)0xff, // tcpi_probes,
           (byte)0xff, // tcpi_backoff,
           (byte)0xff, // tcpi_options,
           (byte)0xff, // tcpi_snd_wscale,
           (byte)0xff, // tcpi_rcv_wscale,
           (int)0xffff,// tcpi_rto,
           (int)0xffff,// tcpi_ato,
           (int)0xffff,// tcpi_snd_mss,
           (int)0xffff,// tcpi_rcv_mss,
           (int)0xffff,// tcpi_unacked,
           (int)0xffff,// tcpi_sacked,
           (int)0xffff,// tcpi_lost,
           (int)0xffff,// tcpi_retrans,
           (int)0xffff,// tcpi_fackets,
           (int)0xffff,// tcpi_last_data_sent,
           (int)0xffff,// tcpi_last_ack_sent,
           (int)0xffff,// tcpi_last_data_recv,
           (int)0xffff,// tcpi_last_ack_recv,
           (int)0xffff,// tcpi_pmtu,
           (int)0xffff,// tcpi_rcv_ssthresh,
           (int)0xffff,// tcpi_rtt,
           (int)0xffff,// tcpi_rttvar,
           (int)0xffff,// tcpi_snd_ssthresh,
           (int)0xffff,// tcpi_snd_cwnd,
           (int)0xffff,// tcpi_advmss,
           (int)0xffff,// tcpi_reordering,
           (int)0xffff,// tcpi_rcv_rtt,
           (int)0xffff,// tcpi_rcv_space,
           (int)0xffff// tcpi_total_retrans
                );

        s=new StringBuilder();
        s.append("state:255 ca_state:255").append("\n ");
        s.append("retransmits:255 probes:255 backoff:255 options:255").append("\n ");
        s.append("snd_wscale:255 rcv_wscale:255 rto:65535 ato:65535 snd_mss:65535 rcv_mss:65535").append("\n ");
        s.append("unacked:65535 sacked:65535 lost:65535 retrans:65535 fackets:65535").append("\n ");
        s.append("last_data_sent:65535 last_ack_sent:65535").append("\n ");
        s.append("last_data_recv:65535 last_ack_recv:65535").append("\n ");
        s.append("pmtu:65535 rcv_ssthresh:65535 rtt:65535 rttvar:65535 snd_ssthresh:65535 snd_cwnd:65535 advmss:65535").append("\n ");
        s.append("reordering:65535 rcv_rtt:65535 rcv_space:65535").append("\n ");
        s.append("total_retrans:65535");
        Assert.assertEquals(s.toString(),tcp_info.toString());

    }
 }
