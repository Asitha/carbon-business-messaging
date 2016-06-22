/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.andes.core.internal.config;

import org.apache.commons.lang.StringUtils;
import org.wso2.carbon.andes.core.internal.configuration.AndesConfigurationManager;
import org.wso2.carbon.andes.core.internal.configuration.enums.AndesConfiguration;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * The following class contains mutator methods for configuration values the broker.
 */
public class QpidServiceImpl {

    /**
     * The client ID for AMQP URL.
     */
    private static final String CARBON_CLIENT_ID = "carbon";

    /**
     * The Virtual host name for AMQL URL.
     */
    private static final String CARBON_VIRTUAL_HOST_NAME = "carbon";

    /**
     * The default host name for connections.
     */
    private static final String CARBON_DEFAULT_HOSTNAME = "localhost";

    /**
     * Default domain separator.
     */
    private static final String DOMAIN_NAME_SEPARATOR = "@";

    /**
     * Tenant domain separator.
     */
    private static final String DOMAIN_NAME_SEPARATOR_INTERNAL = "!";

    /**
     * The access key for internal VM connection.
     */
    private String accessKey;

    /**
     * The default host name for connection.
     */
    private String hostname = CARBON_DEFAULT_HOSTNAME;

    /**
     * The default AMQP port.
     */
    private Integer amqpPort = 5672;

    /**
     * The default AMQP SSL port.
     */
    private Integer amqpSSLPort = 8672;

    /**
     * The default MQTT port.
     */
    private Integer mqttPort = 1883;

    /**
     * The default MQTT SSL port.
     */
    private Integer mqttSSLPort = 8883;

    /**
     * Creates a new Qpid Service.
     *
     * @param accessKey The access key for in VM connection string.
     */
    public QpidServiceImpl(String accessKey) {
        this.accessKey = accessKey;
    }

    /**
     * Read configuration files and set ports and host names
     */
    public void loadConfigurations() {
        // Get the hostname that Carbon runs on
//        String andesConfigHostAddress =
//                String.valueOf(AndesConfigurationManager.readValue(AndesConfiguration.TRANSPORTS_AMQP_BIND_ADDRESS));
        String andesConfigHostAddress =
                AndesConfigurationManager.readValue(AndesConfiguration.TRANSPORTS_AMQP_BIND_ADDRESS);
        if (StringUtils.isNotBlank(andesConfigHostAddress)) {
            try {
                hostname = InetAddress.getByName(andesConfigHostAddress).getHostAddress();
            } catch (UnknownHostException e) {
                hostname = CARBON_DEFAULT_HOSTNAME;
            }
        }

        // Read Qpid broker amqpPort from configuration file
        amqpPort = readPortFromConfig();

        // Read Qpid broker SSL amqpPort from configuration file
        amqpSSLPort = readSSLPortFromConfig();

        // Read MQTT amqpPort from configuration file
        mqttPort = readMQTTPortFromConfig();

        // Read MQTT ssl port value from configuation file
        mqttSSLPort = readMQTTSSLPortFromConfig();
    }

    /**
     * {@inheritDoc}
     */
    public String getAccessKey() {
        return accessKey;
    }

    /**
     * {@inheritDoc}
     */
    public String getClientID() {
        return CARBON_CLIENT_ID;
    }

    /**
     * {@inheritDoc}
     */
    public String getVirtualHostName() {
        return CARBON_VIRTUAL_HOST_NAME;
    }

    /**
     * {@inheritDoc}
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * {@inheritDoc}
     */
    public Integer getAMQPPort() {
        return amqpPort;
    }

    /**
     * {@inheritDoc}
     */
    public void setAMQPPort(Integer amqpPort) {
        this.amqpPort = amqpPort;
    }

    /**
     * {@inheritDoc}
     */
    public Integer getAMQPSSLPort() {
        return amqpSSLPort;
    }

    /**
     * {@inheritDoc}
     */
    public void setAMQPSSLPort(Integer amqpSSLPort) {
        this.amqpSSLPort = amqpSSLPort;
    }

    /**
     * {@inheritDoc}
     */
    public Integer getMqttPort() {
        return mqttPort;
    }

    /**
     * {@inheritDoc}
     */
    public void setMqttPort(Integer mqttPort) {
        this.mqttPort = mqttPort;
    }


    /**
     * {@inheritDoc}
     */
    public Integer getMqttSSLPort() {
        return mqttSSLPort;
    }

    /**
     * {@inheritDoc}
     */
    public void setMqttSSLPort(Integer mqttSSLPort) {
        this.mqttSSLPort = mqttSSLPort;
    }

    /**
     * {@inheritDoc}
     */
    public String getInVMConnectionURL(String username) {
        username = getInternalTenantUsername(username);

        // amqp://{username}:{accessKey}@carbon/carbon?brokerlist='vm://:1'
        return "amqp://" + username + ":" + accessKey + "@" + CARBON_CLIENT_ID + "/" +
                CARBON_VIRTUAL_HOST_NAME + "?brokerlist='vm://:1'";
    }

    /**
     * {@inheritDoc}
     */
    public String getTCPConnectionURL(String username, String password) {
        // amqp://{username}:{password}@carbon/carbon?brokerlist='tcp://{hostname}:{amqpPort}'
        return "amqp://" + username + ":" + password + "@" + CARBON_CLIENT_ID + "/" +
                CARBON_VIRTUAL_HOST_NAME + "?brokerlist='tcp://" + hostname + ":" + amqpPort + "'";
    }

    /**
     * {@inheritDoc}
     */
    public String getTCPConnectionURL(String username, String password, String clientID) {
        // amqp://{username}:{password}@{cliendID}/carbon?brokerlist='tcp://{hostname}:{amqpPort}'
        return "amqp://" + username + ":" + password + "@" + clientID + "/" +
                CARBON_VIRTUAL_HOST_NAME + "?brokerlist='tcp://" + hostname + ":" + amqpPort + "'";
    }

    /**
     * {@inheritDoc}
     */
    public String getInternalTCPConnectionURL(String username, String password) {
        username = getInternalTenantUsername(username);

        // amqp://{username}:{password}@carbon/carbon?brokerlist='tcp://{hostname}:{amqpPort}'
        return "amqp://" + username + ":" + password + "@" + CARBON_CLIENT_ID + "/" +
                CARBON_VIRTUAL_HOST_NAME + "?brokerlist='tcp://" + hostname + ":" + amqpPort + "'";
    }

    /**
     * {@inheritDoc}
     */
    public String getInternalTCPConnectionURL(String username, String password, String clientID) {
        username = getInternalTenantUsername(username);

        // amqp://{username}:{password}@{cliendID}/carbon?brokerlist='tcp://{hostname}:{amqpPort}'
        return "amqp://" + username + ":" + password + "@" + clientID + "/" +
                CARBON_VIRTUAL_HOST_NAME + "?brokerlist='tcp://" + hostname + ":" + amqpPort + "'";
    }

    /**
     * Reads the AMQP amqpPort from configuration and calculates the offset amqpPort that should be used in the pack.
     *
     * @return Port used for AMQP transports with offset if specified.
     */
    private Integer readPortFromConfig() {
        return AndesConfigurationManager.readValue(AndesConfiguration.TRANSPORTS_AMQP_DEFAULT_CONNECTION_PORT);
    }

    /**
     * Reads the AMQP SSL port value from configuration.
     *
     * @return The port value
     */
    private Integer readSSLPortFromConfig() {
        return AndesConfigurationManager.readValue(AndesConfiguration.TRANSPORTS_AMQP_SSL_CONNECTION_PORT);
    }

    /**
     * Reads the MQTT port value from configuration.
     *
     * @return The port value
     */
    private Integer readMQTTPortFromConfig() {
        return AndesConfigurationManager.readValue(AndesConfiguration.TRANSPORTS_MQTT_DEFAULT_CONNECTION_PORT);

    }

    /**
     * Reads the MQTT SSL port value from configuration.
     *
     * @return The port value
     */
    private Integer readMQTTSSLPortFromConfig() {
        return AndesConfigurationManager.readValue(AndesConfiguration.TRANSPORTS_MQTT_SSL_CONNECTION_PORT);
    }

    /**
     * Converts domain username to tenant format username by replacing "@" by "!".
     *
     * @param username The username.
     * @return The tenant username.
     */
    private String getInternalTenantUsername(String username) {
        // Replace @ with ! in tenant username as Qpid does not support @ in username
        // E.g. foo@bar.com -> foo!bar.com
        // Note : The Qpid authorization handler uses ! to extract domain name from username when authorizing
        return username.replace(DOMAIN_NAME_SEPARATOR, DOMAIN_NAME_SEPARATOR_INTERNAL);
    }

}
