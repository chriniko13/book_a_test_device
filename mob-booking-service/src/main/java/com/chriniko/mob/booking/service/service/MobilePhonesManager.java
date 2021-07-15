package com.chriniko.mob.booking.service.service;

import akka.actor.typed.*;
import akka.actor.typed.javadsl.*;
import com.chriniko.mob.booking.service.domain.DeviceShortInfo;
import com.chriniko.mob.booking.service.dto.PhoneBookedResult;
import io.vavr.Tuple;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class MobilePhonesManager extends AbstractBehavior<MobilePhonesManager.MobilePhonesManagerMessage> {

    // Note: in a real world system, this can be fetched from a database.
    private final Map<String /*deviceId*/, DeviceShortInfo> deviceShortInfoById;

    // Note: routers
    private final List<String> supportedBrands = Arrays.asList("samsung", "motorola", "lg", "huawei", "apple", "nokia");
    private final Map<String, ActorRef<MobilePhonesWorker.Command>> routersByBrand;


    private final ActorRef<MobilePhonesStateKeeper.Command> mobilePhonesStateKeeperActorRef;


    public MobilePhonesManager(ActorContext<MobilePhonesManagerMessage> context) {
        super(context);

        deviceShortInfoById = setupDeviceIds();
        mobilePhonesStateKeeperActorRef = context.spawn(MobilePhonesStateKeeper.create(), "mob-phones-state-keeper");

        final int poolSize = 2;

        routersByBrand = supportedBrands.stream()
                .map(brand -> {

                    String routerName = brand + "-worker-pool";

                    PoolRouter<MobilePhonesWorker.Command> samsungWorkerPool =
                            Routers.pool(
                                    poolSize,
                                    // make sure the workers are restarted if they fail
                                    Behaviors.supervise(MobilePhonesWorker.create(mobilePhonesStateKeeperActorRef)).onFailure(SupervisorStrategy.restart())
                            ).withRouteeProps(DispatcherSelector.blocking()).withRoundRobinRouting();


                    ActorRef<MobilePhonesWorker.Command> router = context.spawn(samsungWorkerPool, routerName, DispatcherSelector.sameAsParent());

                    return Tuple.of(brand, router);

                })
                .collect(Collectors.toMap(r -> r._1, r -> r._2));
    }


    public static Behavior<MobilePhonesManagerMessage> create() {
        return Behaviors.setup(context -> {
            context.setLoggerName(MobilePhonesManager.class);
            context.getLog().info("Starting up mobile phones manager...self: " + context.getSelf());
            return new MobilePhonesManager(context);
        });
    }

    @Override
    public Receive<MobilePhonesManagerMessage> createReceive() {
        return newReceiveBuilder()
                //
                //
                .onMessage(BookPhone.class, bookPhone -> {

                    final ActorRef<PhoneBooked> from = bookPhone.from;
                    final ActorRef<MobilePhonesManagerMessage> self = getContext().getSelf();
                    final String deviceId = bookPhone.deviceId;


                    final DeviceShortInfo deviceShortInfo = deviceShortInfoById.get(deviceId);
                    if (deviceShortInfo == null) {
                        from.tell(new PhoneBooked(null, false, null, "provided mobile device id is not registered in the system"));
                        return this;
                    }


                    final String brand = deviceShortInfo.getBrand().toLowerCase();
                    getContext().getLog().info("book phone command received, bookPhone: {}, from: {}, brand: {}", bookPhone, from, brand);


                    final MobilePhonesWorker.DoBookMobile doBookMobile = new MobilePhonesWorker.DoBookMobile(from, self, bookPhone.username, deviceId);

                    final ActorRef<MobilePhonesWorker.Command> router = routersByBrand.get(brand);
                    if (router == null) {
                        from.tell(new PhoneBooked(null, false, null, "modelId of device is not registered, brand: " + brand));
                    } else {
                        router.tell(doBookMobile);
                    }

                    return this;
                })
                .onMessage(PhoneBooked.class, phoneBooked -> {
                    getContext().getLog().info("phone booked event received, phoneBooked: {}", phoneBooked);

                    final ActorRef<PhoneBooked> from = phoneBooked.getFrom();
                    from.tell(phoneBooked); // Note: just pass-through/delegate
                    return this;
                })
                //
                //
                .onMessage(ReturnPhone.class, returnPhone -> {


                    final ActorRef<PhoneReturned> from = returnPhone.from;
                    final ActorRef<MobilePhonesManagerMessage> self = getContext().getSelf();
                    final String deviceId = returnPhone.deviceId;


                    final DeviceShortInfo deviceShortInfo = deviceShortInfoById.get(deviceId);
                    if (deviceShortInfo == null) {
                        from.tell(new PhoneReturned(null, false, null, "provided mobile device id is not registered in the system"));
                        return this;
                    }

                    final String brand = deviceShortInfo.getBrand().toLowerCase();
                    getContext().getLog().info("book return command received, returnPhone: {}, from: {}, brand: {}", returnPhone, from, brand);


                    final MobilePhonesWorker.DoReturnMobile doReturnMobile = new MobilePhonesWorker.DoReturnMobile(from, self, deviceId);
                    final ActorRef<MobilePhonesWorker.Command> router = routersByBrand.get(brand);

                    if (router == null) {
                        from.tell(new PhoneReturned(null, false, null, "modelId of device is not registered, brand: " + brand));
                    } else {
                        router.tell(doReturnMobile);
                    }

                    return this;
                })
                .onMessage(PhoneReturned.class, phoneReturned -> {
                    getContext().getLog().info("phone returned event received, phoneReturned: {}", phoneReturned);

                    final ActorRef<PhoneReturned> from = phoneReturned.getFrom();
                    from.tell(phoneReturned); // Note: just pass-through/delegate
                    return this;
                })
                //
                //
                .onMessage(GetAllBookedInfo.class, getAllBookedInfo -> {
                    getContext().getLog().info("get all booked info command received, getAllBookedInfo: {}", getAllBookedInfo);

                    final ActorRef<MobilePhonesStateKeeper.DeviceInfosResult> from = getAllBookedInfo.getFrom();

                    mobilePhonesStateKeeperActorRef.tell(new MobilePhonesStateKeeper.GetDeviceInfos(from));

                    return this;
                })
                //
                //
                .build();
    }


    // --- infra ---
    private Map<String, DeviceShortInfo> setupDeviceIds() {
        final Map<String, DeviceShortInfo> deviceShortInfoById;
        deviceShortInfoById = new LinkedHashMap<>();
        deviceShortInfoById.put("7fe1c6e5-7bda-4297-8a70-b9b5d4a87f09", new DeviceShortInfo("Samsung Galaxy S9", "samsung_galaxy_s9", "samsung"));
        deviceShortInfoById.put("76aa4f02-0956-4bc8-b092-723fe2f4944a", new DeviceShortInfo("Samsung Galaxy S8", "samsung_galaxy_s8", "samsung"));
        deviceShortInfoById.put("7e8fe38d-1b85-4907-8409-13ddab799757", new DeviceShortInfo("Samsung Galaxy S7", "samsung_galaxy_s7", "samsung"));
        deviceShortInfoById.put("0712d2b8-864d-41ba-ab7f-15e049aac03c", new DeviceShortInfo("Motorola Nexus 6", "motorola_nexus_6", "motorola"));
        deviceShortInfoById.put("91b287f4-4d9e-4e6a-8327-31c80c4973fb", new DeviceShortInfo("LG Nexus 5X", "lg_nexus_5x", "lg"));
        deviceShortInfoById.put("68ed4db2-e030-455f-9073-d84578b8059c", new DeviceShortInfo("Huawei Honor 7X", "huawei_honor_7x", "huawei"));
        deviceShortInfoById.put("a383db47-b1fa-48f5-826d-b755f644d6a6", new DeviceShortInfo("Apple iPhone X", "apple_iphone_x", "apple"));
        deviceShortInfoById.put("aeca925b-8103-4f14-8aea-ae7c9478b3b3", new DeviceShortInfo("Apple iPhone 8", "apple_iphone_8", "apple"));
        deviceShortInfoById.put("cbc0e576-6836-4c0f-8215-188b77502e35", new DeviceShortInfo("Apple iPhone 4s", "apple_iphone_4s", "apple"));
        deviceShortInfoById.put("8170aaa5-b29c-4c72-af68-40e51514ac7a", new DeviceShortInfo("Nokia 3310", "nokia_3310", "nokia"));
        return deviceShortInfoById;
    }


    // --- protocol/message definition ---

    public interface MobilePhonesManagerMessage {
    }


    // commands
    @Getter
    @ToString
    @RequiredArgsConstructor
    public static class BookPhone implements MobilePhonesManagerMessage {
        private final ActorRef<PhoneBooked> from;
        private final String username;
        private final String deviceId;
    }

    @Getter
    @ToString
    @RequiredArgsConstructor
    public static class ReturnPhone implements MobilePhonesManagerMessage {
        private final ActorRef<PhoneReturned> from;
        private final String deviceId;
    }

    @Getter
    @ToString
    @RequiredArgsConstructor
    public static class GetAllBookedInfo implements MobilePhonesManagerMessage {
        private final ActorRef<MobilePhonesStateKeeper.DeviceInfosResult> from;
    }


    // events
    @Getter
    @ToString
    @RequiredArgsConstructor
    public static class PhoneBooked implements MobilePhonesManagerMessage {
        private final ActorRef<PhoneBooked> from;

        private final boolean outcome;
        private final PhoneBookedResult phoneBookedResult; // null when outcome --> false
        private final String reasonOfNotBooked; // null when outcome --> true
    }

    @Getter
    @ToString
    @RequiredArgsConstructor
    public static class PhoneReturned implements MobilePhonesManagerMessage {
        private final ActorRef<PhoneReturned> from;

        private final boolean outcome;
        private final PhoneBookedResult phoneBookedResult; // null when outcome --> false
        private final String reasonOfNotBooked; // null when outcome --> true
    }


}
