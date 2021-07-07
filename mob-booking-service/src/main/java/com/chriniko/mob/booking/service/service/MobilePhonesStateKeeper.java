package com.chriniko.mob.booking.service.service;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.chriniko.mob.booking.service.dto.PhoneBookedResult;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MobilePhonesStateKeeper extends AbstractBehavior<MobilePhonesStateKeeper.Command> {

    private final Map<String /*deviceId*/, PhoneBookedResult> bookingInfo;

    public MobilePhonesStateKeeper(ActorContext<Command> context) {
        super(context);

        bookingInfo = new LinkedHashMap<>();
    }

    public static Behavior<MobilePhonesStateKeeper.Command> create() {
        return Behaviors.setup(context -> {
            context.setLoggerName(MobilePhonesStateKeeper.class);
            context.getLog().info("Starting mobile phones state keeper....self: " + context.getSelf());
            return new MobilePhonesStateKeeper(context);
        });
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                //
                //
                .onMessage(GetDeviceInfo.class, getDeviceInfo -> {

                    final ActorRef<MobilePhonesWorker.DeviceInfoFetched> from = getDeviceInfo.getFrom();
                    final String deviceId = getDeviceInfo.getDeviceId();

                    final ActorRef<Command> self = getContext().getSelf();

                    final PhoneBookedResult record = bookingInfo.get(deviceId);

                    getContext().getLog().info("get device info command received, getDeviceInfo: {}, from: {}", getDeviceInfo, from);

                    from.tell(new MobilePhonesWorker.DeviceInfoFetched(self, deviceId, record));
                    return this;
                })
                .onMessage(UpdateDeviceInfo.class, updateDeviceInfo -> {

                    final ActorRef<MobilePhonesWorker.DeviceInfoUpdated> from = updateDeviceInfo.getFrom();
                    final String deviceId = updateDeviceInfo.getDeviceId();
                    final PhoneBookedResult phoneBookedResult = updateDeviceInfo.getPhoneBookedResult();

                    final ActorRef<Command> self = getContext().getSelf();

                    if (phoneBookedResult == null) {
                        bookingInfo.remove(deviceId);
                    } else {
                        bookingInfo.put(deviceId, phoneBookedResult);
                    }


                    getContext().getLog().info("update device info command received, updateDeviceInfo: {}, from: {}", updateDeviceInfo, from);

                    from.tell(new MobilePhonesWorker.DeviceInfoUpdated(self, deviceId));
                    return this;
                })
                //
                //
                .onMessage(GetDeviceInfos.class, getDeviceInfos -> {

                    final ActorRef<DeviceInfosResult> from = getDeviceInfos.getFrom();

                    getContext().getLog().info("get device infos command received, getDeviceInfos: {}, from: {}", getDeviceInfos, from);

                    final List<Tuple2<String, PhoneBookedResult>> entries = bookingInfo.entrySet()
                            .stream()
                            .map(entry -> Tuple.of(entry.getKey(), entry.getValue()))
                            .collect(Collectors.toList());

                    final ActorRef<Command> self = getContext().getSelf();
                    from.tell(new DeviceInfosResult(self, entries));

                    return this;
                })
                .build();
    }


    // --- protocol/message definition ---

    interface Command {
    }

    // commands
    @Getter
    @ToString
    @RequiredArgsConstructor
    public static class GetDeviceInfo implements MobilePhonesStateKeeper.Command {
        private final ActorRef<MobilePhonesWorker.DeviceInfoFetched> from;

        private final String deviceId;
    }

    @Getter
    @ToString
    @RequiredArgsConstructor
    public static class UpdateDeviceInfo implements MobilePhonesStateKeeper.Command {
        private final ActorRef<MobilePhonesWorker.DeviceInfoUpdated> from;

        private final String deviceId;
        private final PhoneBookedResult phoneBookedResult;
    }

    @Getter
    @ToString
    @RequiredArgsConstructor
    public static class GetDeviceInfos implements MobilePhonesStateKeeper.Command {
        private final ActorRef<DeviceInfosResult> from;

    }

    @Getter
    @ToString
    @RequiredArgsConstructor
    public static class DeviceInfosResult implements MobilePhonesStateKeeper.Command {
        private final ActorRef<MobilePhonesStateKeeper.Command> from;

        private final List<Tuple2<String, PhoneBookedResult>> entries;
    }

}
