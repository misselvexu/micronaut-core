/*
 * Copyright 2017-2020 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.discovery.cloud.digitalocean;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.core.annotation.Experimental;
import io.micronaut.discovery.cloud.ComputeInstanceMetadata;
import io.micronaut.discovery.cloud.ComputeInstanceMetadataResolver;
import io.micronaut.discovery.cloud.NetworkInterface;
import io.micronaut.jackson.core.tree.JsonNodeTreeCodec;
import io.micronaut.jackson.databind.JacksonDatabindMapper;
import io.micronaut.json.JsonMapper;
import io.micronaut.json.JsonStreamConfig;
import io.micronaut.json.tree.JsonNode;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static io.micronaut.discovery.cloud.ComputeInstanceMetadataResolverUtils.populateMetadata;
import static io.micronaut.discovery.cloud.ComputeInstanceMetadataResolverUtils.readMetadataUrl;
import static io.micronaut.discovery.cloud.digitalocean.DigitalOceanMetadataKeys.CIDR;
import static io.micronaut.discovery.cloud.digitalocean.DigitalOceanMetadataKeys.DROPLET_ID;
import static io.micronaut.discovery.cloud.digitalocean.DigitalOceanMetadataKeys.GATEWAY;
import static io.micronaut.discovery.cloud.digitalocean.DigitalOceanMetadataKeys.HOSTNAME;
import static io.micronaut.discovery.cloud.digitalocean.DigitalOceanMetadataKeys.INTERFACES;
import static io.micronaut.discovery.cloud.digitalocean.DigitalOceanMetadataKeys.IPV4;
import static io.micronaut.discovery.cloud.digitalocean.DigitalOceanMetadataKeys.IPV6;
import static io.micronaut.discovery.cloud.digitalocean.DigitalOceanMetadataKeys.IP_ADDRESS;
import static io.micronaut.discovery.cloud.digitalocean.DigitalOceanMetadataKeys.MAC;
import static io.micronaut.discovery.cloud.digitalocean.DigitalOceanMetadataKeys.NETMASK;
import static io.micronaut.discovery.cloud.digitalocean.DigitalOceanMetadataKeys.PRIVATE_INTERFACES;
import static io.micronaut.discovery.cloud.digitalocean.DigitalOceanMetadataKeys.PUBLIC_INTERFACES;
import static io.micronaut.discovery.cloud.digitalocean.DigitalOceanMetadataKeys.REGION;
import static io.micronaut.discovery.cloud.digitalocean.DigitalOceanMetadataKeys.USER_DATA;
import static io.micronaut.discovery.cloud.digitalocean.DigitalOceanMetadataKeys.VENDOR_DATA;

/**
 * Resolves {@link ComputeInstanceMetadata} for Digital Ocean.
 *
 * @author Alvaro Sanchez-Mariscal
 * @since 1.1
 */
@Singleton
@Requires(env = Environment.DIGITAL_OCEAN)
public class DigitalOceanMetadataResolver implements ComputeInstanceMetadataResolver {

    private static final Logger LOG = LoggerFactory.getLogger(DigitalOceanMetadataResolver.class);
    private static final int READ_TIMEOUT_IN_MILLS = 5000;
    private static final int CONNECTION_TIMEOUT_IN_MILLS = 5000;

    private final DigitalOceanMetadataConfiguration configuration;
    private final JsonFactory jsonFactory;
    private final JsonStreamConfig jsonStreamConfig;

    private DigitalOceanInstanceMetadata cachedMetadata;

    /**
     * @param configuration Digital Ocean Metadata configuration
     * @param jsonFactory Factory to use for json parsing
     * @param mapper Mapper to use for deserialization
     */
    @Inject
    @Experimental
    public DigitalOceanMetadataResolver(
        DigitalOceanMetadataConfiguration configuration,
        JsonFactory jsonFactory,
        JsonMapper mapper
    ) {
        this.configuration = configuration;
        this.jsonFactory = jsonFactory;
        this.jsonStreamConfig = mapper.getStreamConfig();
    }

    /**
     * Construct with default settings.
     */
    public DigitalOceanMetadataResolver() {
        configuration = new DigitalOceanMetadataConfiguration();
        jsonFactory = new JsonFactory();
        jsonStreamConfig = JsonStreamConfig.DEFAULT;
    }

    /**
     * @param objectMapper To read and write JSON
     * @param configuration Digital Ocean Metadata configuration
     */
    public DigitalOceanMetadataResolver(ObjectMapper objectMapper, DigitalOceanMetadataConfiguration configuration) {
        this(configuration, objectMapper.getFactory(), new JacksonDatabindMapper(objectMapper));
    }

    @Override
    public Optional<ComputeInstanceMetadata> resolve(Environment environment) {
        if (!configuration.isEnabled()) {
            return Optional.empty();
        }
        if (cachedMetadata != null) {
            cachedMetadata.setCached(true);
            return Optional.of(cachedMetadata);
        }

        DigitalOceanInstanceMetadata instanceMetadata = new DigitalOceanInstanceMetadata();

        try {
            String metadataUrl = configuration.getUrl();
            JsonNode metadataJson = readMetadataUrl(new URL(metadataUrl), CONNECTION_TIMEOUT_IN_MILLS, READ_TIMEOUT_IN_MILLS, JsonNodeTreeCodec.getInstance().withConfig(jsonStreamConfig), jsonFactory, new HashMap<>());
            if (metadataJson != null) {
                instanceMetadata.setInstanceId(textValue(metadataJson, DROPLET_ID));
                instanceMetadata.setName(textValue(metadataJson, HOSTNAME));
                instanceMetadata.setVendorData(textValue(metadataJson, VENDOR_DATA));
                instanceMetadata.setUserData(textValue(metadataJson, USER_DATA));
                instanceMetadata.setRegion(textValue(metadataJson, REGION));

                JsonNode networkInterfaces = metadataJson.get(INTERFACES.getName());
                List<NetworkInterface> privateInterfaces = processJsonInterfaces(networkInterfaces.get(PRIVATE_INTERFACES.getName()), instanceMetadata::setPrivateIpV4, instanceMetadata::setPrivateIpV6);
                List<NetworkInterface> publicInterfaces = processJsonInterfaces(networkInterfaces.get(PUBLIC_INTERFACES.getName()), instanceMetadata::setPublicIpV4, instanceMetadata::setPublicIpV6);
                var allInterfaces = new ArrayList<NetworkInterface>();
                allInterfaces.addAll(publicInterfaces);
                allInterfaces.addAll(privateInterfaces);
                instanceMetadata.setInterfaces(allInterfaces);

                populateMetadata(instanceMetadata, metadataJson);
                cachedMetadata = instanceMetadata;

                return Optional.of(instanceMetadata);
            }
        } catch (MalformedURLException mue) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Digital Ocean metadataUrl value is invalid!: {}", configuration.getUrl(), mue);
            }
        } catch (IOException ioe) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Error connecting to {} reading instance metadata", configuration.getUrl(), ioe);
            }
        }

        return Optional.empty();
    }

    private List<NetworkInterface> processJsonInterfaces(JsonNode interfaces, Consumer<String> ipv4Setter, Consumer<String> ipv6Setter) {
        var networkInterfaces = new ArrayList<NetworkInterface>();

        if (interfaces != null) {
            var networkCounter = new AtomicInteger();
            interfaces.values().forEach(
                jsonNode -> {
                    var networkInterface = new DigitalOceanNetworkInterface();
                    networkInterface.setId(networkCounter.toString());
                    JsonNode ipv4 = jsonNode.get(IPV4.getName());
                    if (ipv4 != null) {
                        networkInterface.setIpv4(textValue(ipv4, IP_ADDRESS));
                        networkInterface.setNetmask(textValue(ipv4, NETMASK));
                        networkInterface.setGateway(textValue(ipv4, GATEWAY));
                    }
                    JsonNode ipv6 = jsonNode.get(IPV6.getName());
                    if (ipv6 != null) {
                        networkInterface.setIpv6(textValue(ipv6, IP_ADDRESS));
                        networkInterface.setIpv6Gateway(textValue(ipv6, GATEWAY));
                        networkInterface.setCidr(ipv6.get(CIDR.getName()).getIntValue());
                    }
                    networkInterface.setMac(textValue(jsonNode, MAC));

                    networkCounter.getAndIncrement();
                    networkInterfaces.add(networkInterface);
                }
            );

            JsonNode firstIpv4 = interfaces.get(0).get(IPV4.getName());
            ipv4Setter.accept(textValue(firstIpv4, IP_ADDRESS));

            JsonNode firstIpv6 = interfaces.get(0).get(IPV6.getName());
            if (firstIpv6 != null) {
                ipv6Setter.accept(textValue(firstIpv6, IP_ADDRESS));
            }
        }


        return networkInterfaces;
    }

    private String textValue(JsonNode node, DigitalOceanMetadataKeys key) {
        JsonNode value = node.get(key.getName());
        if (value != null) {
            return value.coerceStringValue();
        } else {
            return null;
        }
    }

}
