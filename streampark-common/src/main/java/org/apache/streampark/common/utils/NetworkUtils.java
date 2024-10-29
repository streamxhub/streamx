/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.streampark.common.utils;

import org.apache.streampark.common.constants.Constants;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.net.InetAddressUtils;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class NetworkUtils {

    private static InetAddress LOCAL_ADDRESS = null;
    private static volatile String HOST_ADDRESS;
    private static String preferredInterface;
    private static String restrictInterface;
    private static String priorityStrategy;

    @Value("${network.preferred-interface:null}")
    public void setPreferredInterface(String PreferredInterface) {
        preferredInterface = PreferredInterface;
    }

    @Value("${network.restrict-interface:docker0}")
    public void setRestrictInterface(String RestrictInterface) {
        restrictInterface = RestrictInterface;
    }

    @Value("${network.priority-strategy:default}")
    public void setPriorityStrategy(String PriorityStrategy) {
        priorityStrategy = PriorityStrategy;
    }

    /**
     * get addr like host:port
     * @return addr
     */
    public static String getAddr(String host, int port) {
        return String.format("%s:%d", host, port);
    }

    /**
     * get addr like host:port
     * @return addr
     */
    public static String getAddr(int port) {
        return getAddr(getHost(), port);
    }

    /**
     * get host
     * @return host
     */
    public static String getHost(InetAddress inetAddress) {
        if (inetAddress != null) {
            return inetAddress.getHostAddress();
        }
        return null;
    }

    public static String getHost() {
        if (HOST_ADDRESS != null) {
            return HOST_ADDRESS;
        }

        InetAddress address = getLocalAddress();
        if (address != null) {
            HOST_ADDRESS = getHost(address);
            return HOST_ADDRESS;
        }
        return Constants.LOCAL_HOST;
    }

    private static InetAddress getLocalAddress() {
        if (null != LOCAL_ADDRESS) {
            return LOCAL_ADDRESS;
        }
        LOCAL_ADDRESS = getLocalAddress0();
        return LOCAL_ADDRESS;
    }

    /**
     * Find first valid IP from local network card
     *
     * @return first valid local IP
     */
    private static synchronized InetAddress getLocalAddress0() {
        List<NetworkInterface> suitableNetworkInterface = findSuitableNetworkInterface();
        List<InetAddress> suitableInetAddress = findSuitableInetAddress(suitableNetworkInterface);
        if (CollectionUtils.isEmpty(suitableInetAddress)) {
            return null;
        }
        return suitableInetAddress.get(0);
    }

    private static InetAddress normalizeV6Address(Inet6Address address) {
        String addr = address.getHostAddress();
        int i = addr.lastIndexOf('%');
        if (i > 0) {
            try {
                return InetAddress.getByName(addr.substring(0, i) + '%' + address.getScopeId());
            } catch (UnknownHostException e) {
                log.debug("Unknown IPV6 address: ", e);
            }
        }
        return address;
    }

    protected static boolean isValidV4Address(InetAddress address) {
        if (!(address instanceof Inet4Address)) {
            return false;
        }
        String name = address.getHostAddress();
        return (name != null
            && InetAddressUtils.isIPv4Address(name)
            && !address.isAnyLocalAddress()
            && !address.isLoopbackAddress());
    }

    protected static boolean isValidV6Address(InetAddress address) {
        if (!(address instanceof Inet6Address)) {
            return false;
        }
        String name = address.getHostAddress();
        return (name != null
            && InetAddressUtils.isIPv6Address(name)
            && !address.isAnyLocalAddress()
            && !address.isLoopbackAddress());
    }

    /**
     * Check if an ipv6 address
     *
     * @return true if it is reachable
     */
    private static boolean isPreferIPV6Address() {
        return Boolean.getBoolean("java.net.preferIPv6Addresses");
    }

    private static boolean isPreferIPV4Address() {
        return Boolean.getBoolean("java.net.preferIPv4Addresses");
    }

    /**
     * Get the suitable {@link NetworkInterface}
     */
    private static List<NetworkInterface> findSuitableNetworkInterface() {

        // Find all network interfaces
        List<NetworkInterface> networkInterfaces = Collections.emptyList();
        try {
            networkInterfaces = getAllNetworkInterfaces();
        } catch (SocketException e) {
            log.warn("ValidNetworkInterfaces exception", e);
        }

        // Filter the loopback/virtual/ network interfaces
        List<NetworkInterface> validNetworkInterfaces = networkInterfaces
            .stream()
            .filter(networkInterface -> {
                try {
                    return !(networkInterface == null
                        || networkInterface.isLoopback()
                        || networkInterface.isVirtual()
                        || !networkInterface.isUp());
                } catch (SocketException e) {
                    log.warn("ValidNetworkInterfaces exception", e);
                    return false;
                }
            })
            .collect(Collectors.toList());

        // Use the specified network interface if set
        String specifiedNetworkInterfaceName = specifyNetworkInterfaceName();
        if (StringUtils.isNotBlank(specifiedNetworkInterfaceName)) {
            validNetworkInterfaces = validNetworkInterfaces.stream()
                .filter(networkInterface -> specifiedNetworkInterfaceName.equals(networkInterface.getDisplayName()))
                .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(validNetworkInterfaces)) {
                throw new IllegalArgumentException(
                    "The specified network interface: " + specifiedNetworkInterfaceName + " is not found");
            }
            log.info("Use the specified network interface: {} -> {}", specifiedNetworkInterfaceName,
                validNetworkInterfaces);
        }

        Set<String> restrictNetworkInterfaceName = restrictNetworkInterfaceName();
        if (CollectionUtils.isNotEmpty(restrictNetworkInterfaceName)) {
            validNetworkInterfaces = validNetworkInterfaces.stream()
                .filter(validNetworkInterface -> !restrictNetworkInterfaceName
                    .contains(validNetworkInterface.getDisplayName()))
                .collect(Collectors.toList());
        }
        return filterByNetworkPriority(validNetworkInterfaces);
    }

    /**
     * Get the suitable {@link InetAddress}
     */
    private static List<InetAddress> findSuitableInetAddress(List<NetworkInterface> networkInterfaces) {
        if (CollectionUtils.isEmpty(networkInterfaces)) {
            return Collections.emptyList();
        }
        List<InetAddress> allInetAddresses = new LinkedList<>();
        for (NetworkInterface networkInterface : networkInterfaces) {
            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                allInetAddresses.add(addresses.nextElement());
            }
        }
        // Get prefer addresses
        List<InetAddress> preferInetAddress = new ArrayList<>();
        if (!isPreferIPV6Address() && !isPreferIPV4Address()) {
            // no prefer, will use all addresses
            preferInetAddress.addAll(getIpv4Addresses(allInetAddresses));
            preferInetAddress.addAll(getIpv6Addresses(allInetAddresses));
        }
        if (isPreferIPV4Address()) {
            preferInetAddress.addAll(getIpv4Addresses(allInetAddresses));
        }
        if (isPreferIPV6Address()) {
            preferInetAddress.addAll(getIpv6Addresses(allInetAddresses));
        }
        // Get reachable addresses
        return preferInetAddress.stream()
            .filter(inetAddress -> {
                try {
                    return inetAddress.isReachable(100);
                } catch (IOException e) {
                    log.warn("InetAddress isReachable exception", e);
                    return false;
                }
            }).collect(Collectors.toList());
    }

    private static List<InetAddress> getIpv4Addresses(List<InetAddress> allInetAddresses) {
        if (CollectionUtils.isEmpty(allInetAddresses)) {
            return Collections.emptyList();
        }
        List<InetAddress> validIpv4Addresses = new ArrayList<>();
        for (InetAddress inetAddress : allInetAddresses) {
            if (isValidV4Address(inetAddress)) {
                validIpv4Addresses.add(inetAddress);
            }
        }
        return validIpv4Addresses;
    }

    private static List<InetAddress> getIpv6Addresses(List<InetAddress> allInetAddresses) {
        if (CollectionUtils.isEmpty(allInetAddresses)) {
            return Collections.emptyList();
        }
        List<InetAddress> validIpv6Addresses = new ArrayList<>();
        for (InetAddress inetAddress : allInetAddresses) {
            if (!isValidV6Address(inetAddress)) {
                continue;
            }
            Inet6Address v6Address = (Inet6Address) inetAddress;
            InetAddress normalizedV6Address = normalizeV6Address(v6Address);
            validIpv6Addresses.add(normalizedV6Address);
        }
        return validIpv6Addresses;
    }

    private static List<NetworkInterface> getAllNetworkInterfaces() throws SocketException {
        List<NetworkInterface> validNetworkInterfaces = new LinkedList<>();
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            log.debug("Found NetworkInterface: {}", networkInterface);
            validNetworkInterfaces.add(networkInterface);
        }
        log.info("Get all NetworkInterfaces: {}", validNetworkInterfaces);
        return validNetworkInterfaces;
    }

    private static String specifyNetworkInterfaceName() {
        return preferredInterface;
    }

    private static Set<String> restrictNetworkInterfaceName() {
        if (StringUtils.isBlank(restrictInterface)) {
            return Sets.newHashSet("docker0");
        } else {
            return Arrays.stream(preferredInterface.split(",")).map(String::trim).collect(Collectors.toSet());
        }
    }

    private static List<NetworkInterface> filterByNetworkPriority(List<NetworkInterface> validNetworkInterfaces) {
        if (CollectionUtils.isEmpty(validNetworkInterfaces)) {
            return Collections.emptyList();
        }

        switch (priorityStrategy) {
            case "default":
                log.debug("Use default NetworkInterface acquisition policy");
                return findAddressByDefaultPolicy(validNetworkInterfaces);
            case "inner":
                log.debug("Use inner NetworkInterface acquisition policy");
                return findInnerAddressNetWorkInterface(validNetworkInterfaces);
            case "outer":
                log.debug("Use outer NetworkInterface acquisition policy");
                return findOuterAddressNetworkInterface(validNetworkInterfaces);
            default:
                log.error("There is no matching network card acquisition policy!");
                return Collections.emptyList();
        }
    }

    private static List<NetworkInterface> findAddressByDefaultPolicy(List<NetworkInterface> validNetworkInterfaces) {
        List<NetworkInterface> allAddress = new ArrayList<>();
        allAddress.addAll(findInnerAddressNetWorkInterface(validNetworkInterfaces));
        allAddress.addAll(findOuterAddressNetworkInterface(validNetworkInterfaces));
        return allAddress;
    }

    /**
     * Get the Intranet IP
     *
     * @return If no {@link NetworkInterface} is available , return <code>null</code>
     */
    private static List<NetworkInterface> findInnerAddressNetWorkInterface(List<NetworkInterface> validNetworkInterfaces) {
        if (CollectionUtils.isEmpty(validNetworkInterfaces)) {
            return Collections.emptyList();
        }

        List<NetworkInterface> innerNetworkInterfaces = new ArrayList<>();
        for (NetworkInterface ni : validNetworkInterfaces) {
            Enumeration<InetAddress> address = ni.getInetAddresses();
            while (address.hasMoreElements()) {
                InetAddress ip = address.nextElement();
                if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress()) {
                    innerNetworkInterfaces.add(ni);
                }
            }
        }
        return innerNetworkInterfaces;
    }

    private static List<NetworkInterface> findOuterAddressNetworkInterface(List<NetworkInterface> validNetworkInterfaces) {
        if (CollectionUtils.isEmpty(validNetworkInterfaces)) {
            return Collections.emptyList();
        }

        List<NetworkInterface> outerNetworkInterfaces = new ArrayList<>();
        for (NetworkInterface ni : validNetworkInterfaces) {
            Enumeration<InetAddress> address = ni.getInetAddresses();
            while (address.hasMoreElements()) {
                InetAddress ip = address.nextElement();
                if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress()) {
                    outerNetworkInterfaces.add(ni);
                }
            }
        }
        return outerNetworkInterfaces;
    }
}
