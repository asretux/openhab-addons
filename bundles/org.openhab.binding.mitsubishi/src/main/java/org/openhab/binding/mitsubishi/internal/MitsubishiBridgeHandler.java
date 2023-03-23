import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.binding.BridgeHandler;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.thing.binding.ThingHandlerCallback;
import org.openhab.core.thing.binding.builder.BridgeBuilder;
import org.openhab.core.thing.binding.builder.ThingBuilder;
import org.openhab.core.thing.binding.firmware.FirmwareVersion;
import org.openhab.core.thing.binding.firmware.FirmwareVersionRange;
import org.openhab.core.thing.binding.firmware.Version;
import org.openhab.core.thing.binding.firmware.VersionRange;
import org.openhab.core.thing.binding.firmware.VersionUtils;
import org.openhab.core.thing.common.ThingUID;
import org.openhab.core.types.State;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.binding.BaseBridgeHandler;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.net.http.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.HashMap;

@NonNullByDefault
public class MitsubishiBridgeHandler extends BaseBridgeHandler {

    private static final Logger logger = LoggerFactory.getLogger(MitsubishiAirConditionerBridgeHandler.class);

    public static final ThingTypeUID THING_TYPE_UID = new ThingTypeUID("mitsubishi:airconditioner:bridge");

    private final Map<ThingUID, MitsubishiHandler> handlers = new HashMap<>();

    public MitsubishiAirConditionerBridgeHandler(Thing bridge, BridgeHandlerCallback callback) {
        super(bridge, callback);
    }

    @Override
    public void initialize() {
        super.initialize();
        updateStatus(ThingStatus.ONLINE);
    }

    @Override
    public void dispose() {
        super.dispose();
        for (MitsubishiHandler handler : handlers.values()) {
            handler.dispose();
        }
        handlers.clear();
        updateStatus(ThingStatus.OFFLINE);
    }

    @Override
    public void handleChild(Thing child, ThingHandler handler) {
        super.handleChild(child, handler);
        if (handler instanceof MitsubishiHandler) {
            handlers.put(child.getUID(), (MitsubishiHandler) handler);
        }
    }

    @Override
    public void handleChildRemoved(Thing child) {
        super.handleChildRemoved(child);
        handlers.remove(child.getUID());
    }

    public void addAirConditioner(String ip, int port, String deviceId) {
        ThingUID uid = new ThingUID(THING_TYPE_UID, deviceId);
        ThingBuilder builder = ThingBuilder.create(uid, THING_TYPE_UID, deviceId)
                .withLabel("Mitsubishi Air Conditioner")
                .withBridge(getThing().getUID());
        builder.withConfig(MitsubishiHandler.CONFIG_IP, ip);
        builder.withConfig(MitsubishiHandler.CONFIG_PORT, port);
        builder.withConfig(MitsubishiHandler.CONFIG_DEVICE_ID, deviceId);
        Thing thing = builder.build();
        getThingHandlerCallback().onThingAdded(thing);
    }

    public void removeAirConditioner(String deviceId) {
        ThingUID uid = new ThingUID(THING_TYPE_UID, deviceId);
        Thing thing = getThing().
        getThingHandlerCallback().onThingRemoved(uid);
    }

    public void updateAirConditioner(String deviceId, String mode, int temperature, boolean power) {
        ThingUID uid = new ThingUID(THING_TYPE_UID, deviceId);
        MitsubishiHandler handler = handlers.get(uid);
        if (handler != null) {
            State state = null;
            if (mode != null) {
                switch (mode) {
                    case MitsubishiHandler.MODE_COOL:
                        state = OnOffType.ON;
                        handler.setCoolMode();
                        break;
                    case MitsubishiHandler.MODE_HEAT:
                        state = OnOffType.ON;
                        handler.setHeatMode();
                        break;
                    case MitsubishiHandler.MODE_DRY:
                        state = OnOffType.ON;
                        handler.setDryMode();
                        break;
                    case MitsubishiHandler.MODE_FAN:
                        state = OnOffType.ON;
                        handler.setFanMode();
                        break;
                    case MitsubishiHandler.MODE_AUTO:
                        state = OnOffType.ON;
                        handler.setAutoMode();
                        break;
                    default:
                        logger.warn("Unknown mode: {}", mode);
                        break;
                }
            }
            if (temperature >= MitsubishiHandler.TEMP_MIN && temperature <= MitsubishiHandler.TEMP_MAX) {
                state = new DecimalType(temperature);
                handler.setTemperature(temperature);
            }
            if (power) {
                state = OnOffType.ON;
                handler.setPowerOn();
            } else {
                handler.setPowerOff();
            }
            if (state != null) {
                updateState(uid, state);
            }
        }
    }

    public static BridgeBuilder createBridgeBuilder(ThingUID uid) {
        return BridgeBuilder.create(THING_TYPE_UID, uid).withTypeUID(THING_TYPE_UID)
                .withProperties(new FirmwareVersionRange(new FirmwareVersion(new Version(1, 0, 0)),
                        new VersionRange(VersionUtils.DEFAULT_LATEST_VERSION, true, VersionUtils.DEFAULT_LATEST_VERSION, true))));
    }

    public static ThingBuilder createAirConditionerThingBuilder(ThingUID uid, String ip, int port, String deviceId) {
        return ThingBuilder.create(uid, MitsubishiHandler.THING_TYPE_UID, deviceId)
                .withLabel("Mitsubishi Air Conditioner")
                .withBridge(MitsubishiAirConditionerBridgeHandler.THING_TYPE_UID, deviceId)
                .withConfig(MitsubishiHandler.CONFIG_IP, ip)
                .withConfig(MitsubishiHandler.CONFIG_PORT, port)
                .withConfig(MitsubishiHandler.CONFIG_DEVICE_ID, deviceId)
                .withProperties(new FirmwareVersionRange(new FirmwareVersion(new Version(1, 0, 0)),
                        new VersionRange(VersionUtils.DEFAULT_LATEST_VERSION, true, VersionUtils.DEFAULT_LATEST_VERSION, true))));
    }

}
