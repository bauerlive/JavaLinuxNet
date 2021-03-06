#!/bin/bash
#
# Note we abuse 8.8.4.4 as test IP
TESTIP=8.8.4.4

ip tuntap del dev luc mode tun 
ip tuntap add dev luc mode tun user luc group users
ip addr add 127.0.0.2/32 dev luc
ip link set dev luc up
ip route add $TESTIP dev luc

#route all redirected traffic to lo
ip rule del priority 2000
ip rule add priority 2000 fwmark 1 lookup 100
ip route flush table 100
ip -f inet route add local 0.0.0.0/0 dev lo table 100

iptables -F
iptables -t mangle -F
iptables -t mangle -N DIVERT
iptables -t mangle -A DIVERT -j MARK --set-mark 1
iptables -t mangle -A DIVERT -j ACCEPT

#for forwarding traffic
iptables -t mangle -A PREROUTING -p tcp -m socket -j DIVERT
iptables -t mangle -A PREROUTING -p tcp -m tcp --destination $TESTIP --dport 80 -j TPROXY --on-port 1800 --on-ip 0.0.0.0 --tproxy-mark 0x01/0x01

#for local traffic
iptables -t mangle -A OUTPUT -p tcp -m tcp --destination $TESTIP --dport 80 -j MARK --set-mark 0x01
