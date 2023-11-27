/**
 * Copyright (c) 2010-2022 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.mitsubishi.internal;

import static org.openhab.binding.mitsubishi.internal.MitsubishiBindingConstants.*;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link MitsubishiHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Eurotux - Initial contribution
 */
@NonNullByDefault
public class MitsubishiHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(MitsubishiHandler.class);

    private @Nullable MitsubishiConfiguration config;

    public MitsubishiHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (CHANNEL_1.equals(channelUID.getId())) {
            if (command instanceof RefreshType) {
                // TODO: handle data refresh
            }

            // TODO: handle command

            // Note: if communication with thing fails for some reason,
            // indicate that by setting the status with detail information:
            // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
            // "Could not control device at IP address x.x.x.x");
        }
    }

    @Override
    public void initialize() {
        config = getConfigAs(MitsubishiConfiguration.class);

        @Nullable
        String ipAddress = config.ip;

        // Create a new SoapClient to communicate with the AC
        HttpHelper.post("http://" + ipAddress + "/soap", "aaaa");

        // Call the AC's "getTemperature" method to retrieve the current temperature
        // String soapAction = "http://ac_endpoint_url/soap/action/getTemperature";

        // Create a new Channel for the temperature and add it to the Thing
        // Channel temperatureChannel = new ChannelBuilder().thing(getThing().getUID()).id("temperature").type("Number")
        // .label("Temperature").build();
        // updateThing(ThingBuilder.create(getThing()).withChannel(temperatureChannel).build());

        // Call the AC's "getMode" method to retrieve the current mode
        // soapAction = "http://ac_endpoint_url/soap/action/getMode";
        // response = client.callSoapMethod(soapAction, null);

        // Create a new Channel for the mode and add it to the Thing
        // Channel modeChannel = new ChannelBuilder().thing(getThing().getUID()).id("mode").type("String").label("Mode")
        // .build();
        // updateThing(ThingBuilder.create(getThing()).withChannel(modeChannel).build());

        // TODO: Initialize the handler.
        // The framework requires you to return from this method quickly, i.e. any network access must be done in
        // the background initialization below.
        // Also, before leaving this method a thing status from one of ONLINE, OFFLINE or UNKNOWN must be set. This
        // might already be the real thing status in case you can decide it directly.
        // In case you can not decide the thing status directly (e.g. for long running connection handshake using WAN
        // access or similar) you should set status UNKNOWN here and then decide the real status asynchronously in the
        // background.

        // set the thing status to UNKNOWN temporarily and let the background task decide for the real status.
        // the framework is then able to reuse the resources from the thing handler initialization.
        // we set this upfront to reliably check status updates in unit tests.
        updateStatus(ThingStatus.UNKNOWN);

        // Example for background initialization:
        scheduler.execute(() -> {
            boolean thingReachable = true; // <background task with long running initialization here>
            // when done do:
            if (thingReachable) {
                updateStatus(ThingStatus.ONLINE);
            } else {
                updateStatus(ThingStatus.OFFLINE);
            }
        });

        // These logging types should be primarily used by bindings
        // logger.trace("Example trace message");
        // logger.debug("Example debug message");
        // logger.warn("Example warn message");
        //
        // Logging to INFO should be avoided normally.
        // See https://www.openhab.org/docs/developer/guidelines.html#f-logging

        // Note: When initialization can NOT be done set the status with more details for further
        // analysis. See also class ThingStatusDetail for all available status details.
        // Add a description to give user information to understand why thing does not work as expected. E.g.
        // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
        // "Can not access device as username and/or password are invalid");
    }
}
