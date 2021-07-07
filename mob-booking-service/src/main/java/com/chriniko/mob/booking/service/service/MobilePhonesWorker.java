package com.chriniko.mob.booking.service.service;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.http.javadsl.Http;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import com.chriniko.mob.booking.service.dto.PhoneBookedResult;
import com.chriniko.mob.booking.service.dto.internal.MobileInfoResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletionStage;

import static akka.actor.typed.javadsl.AskPattern.ask;


/*
    Important Note:


    In a more real world project, the communication between the worker and the state keeper should be happened
    with stash and change of behaviour, due to time limited i choose to use the ask pattern for communication.


    Moreover we could use a cache here (just a simple map) in order to eliminate http call (io)
    to mob-info-service.

 */
public class MobilePhonesWorker extends AbstractBehavior<MobilePhonesWorker.Command> {

    private final ActorRef<MobilePhonesStateKeeper.Command> mobilePhonesStateKeeper;

    private final Http http;

    public MobilePhonesWorker(ActorRef<MobilePhonesStateKeeper.Command> mobilePhonesStateKeeper, ActorContext<Command> context) {
        super(context);

        this.mobilePhonesStateKeeper = mobilePhonesStateKeeper;
        this.http = Http.get(context.getSystem());
    }

    public static Behavior<Command> create(ActorRef<MobilePhonesStateKeeper.Command> mobilePhonesStateKeeperActorRef) {
        return Behaviors.setup(context -> {
            context.setLoggerName(MobilePhonesWorker.class);
            context.getLog().info("Starting mobile phones worker....self: " + context.getSelf());
            return new MobilePhonesWorker(mobilePhonesStateKeeperActorRef, context);
        });
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                //
                //
                .onMessage(DoBookMobile.class, doBookMobile -> {

                    final ActorRef<MobilePhonesManager.MobilePhonesManagerMessage> parent = doBookMobile.getParent();
                    final ActorRef<MobilePhonesManager.PhoneBooked> from = doBookMobile.from;
                    final String deviceId = doBookMobile.deviceId;
                    final String username = doBookMobile.username;


                    final CompletionStage<DeviceInfoFetched> deviceInfoFetchedResult
                            = ask(mobilePhonesStateKeeper, param -> new MobilePhonesStateKeeper.GetDeviceInfo(param, deviceId), Duration.ofSeconds(2), getContext().getSystem().scheduler());

                    deviceInfoFetchedResult.handle((deviceInfoFetched, error) -> {
                        if (error != null) {
                            throw asRuntimeException(error); // Note: fail hard
                        } else {

                            final PhoneBookedResult record = deviceInfoFetched.getPhoneBookedResult();

                            if (record != null) {
                                final MobilePhonesManager.PhoneBooked phoneBooked = new MobilePhonesManager.PhoneBooked(from, false, null, "device is already booked, try again later");
                                parent.tell(phoneBooked);

                            } else {
                                // Note: do http call to mob info service and book the device
                                final CompletionStage<HttpResponse> httpRequestToMobInfoService = http.singleRequest(HttpRequest.create("http://127.0.0.1:8099/phones/" + deviceId));
                                httpRequestToMobInfoService.handleAsync((httpResponse, throwable) -> {

                                    if (throwable != null) {
                                        final MobilePhonesManager.PhoneBooked phoneBooked = new MobilePhonesManager.PhoneBooked(from, false, null,
                                                "communication error with mob info service, error: " + throwable.getMessage());
                                        parent.tell(phoneBooked);

                                    } else {

                                        final int status = httpResponse.status().intValue();
                                        if (status != 200) {
                                            final MobilePhonesManager.PhoneBooked phoneBooked = new MobilePhonesManager.PhoneBooked(from, false, null,
                                                    "response status error with mob info service (NOT OKAY)");
                                            parent.tell(phoneBooked);

                                        } else {
                                            Jackson.unmarshaller(MobileInfoResult.class)
                                                    .unmarshal(httpResponse.entity(), getContext().getSystem())
                                                    .thenAccept(mobileInfoResult -> {
                                                        final PhoneBookedResult phoneBookedResult = new PhoneBookedResult(
                                                                false,
                                                                Instant.now(),
                                                                username,
                                                                mobileInfoResult.getTechnology(),
                                                                mobileInfoResult.getBandsByTech()
                                                        );


                                                        final CompletionStage<DeviceInfoUpdated> deviceInfoUpdatedResult
                                                                = ask(mobilePhonesStateKeeper, param -> new MobilePhonesStateKeeper.UpdateDeviceInfo(param, deviceId, phoneBookedResult), Duration.ofSeconds(2), getContext().getSystem().scheduler());
                                                        deviceInfoUpdatedResult.handle((deviceInfoUpdated, errorOnUpdate) -> {

                                                            if (errorOnUpdate != null) {
                                                                throw asRuntimeException(errorOnUpdate); // Note: fail fast.
                                                            } else {
                                                                final MobilePhonesManager.PhoneBooked phoneBooked = new MobilePhonesManager.PhoneBooked(from, true, phoneBookedResult, null);
                                                                parent.tell(phoneBooked);
                                                            }
                                                            return null;
                                                        });
                                                    });
                                        }
                                    }
                                    return null;
                                });
                            }
                        }
                        return null;
                    });
                    return this;
                })
                //
                //
                .onMessage(DoReturnMobile.class, doReturnMobile -> {

                    final ActorRef<MobilePhonesManager.MobilePhonesManagerMessage> parent = doReturnMobile.getParent();
                    final ActorRef<MobilePhonesManager.PhoneReturned> from = doReturnMobile.from;
                    final String deviceId = doReturnMobile.deviceId;


                    final CompletionStage<DeviceInfoFetched> deviceInfoFetchedResult
                            = ask(mobilePhonesStateKeeper, param -> new MobilePhonesStateKeeper.GetDeviceInfo(param, deviceId), Duration.ofSeconds(2), getContext().getSystem().scheduler());

                    deviceInfoFetchedResult.handle((deviceInfoFetched, error) -> {
                        if (error != null) {
                            throw asRuntimeException(error); // Note: fail hard
                        } else {

                            final PhoneBookedResult record = deviceInfoFetched.getPhoneBookedResult();

                            if (record == null) {
                                final MobilePhonesManager.PhoneReturned phoneReturned = new MobilePhonesManager.PhoneReturned(from, false,
                                        null, "device is not booked");
                                parent.tell(phoneReturned);

                            } else {

                                final CompletionStage<DeviceInfoUpdated> deviceInfoUpdatedResult
                                        = ask(mobilePhonesStateKeeper, param -> new MobilePhonesStateKeeper.UpdateDeviceInfo(param, deviceId, null), Duration.ofSeconds(2), getContext().getSystem().scheduler());
                                deviceInfoUpdatedResult.handle((deviceInfoUpdated, errorOnUpdate) -> {

                                    if (errorOnUpdate != null) {
                                        throw asRuntimeException(errorOnUpdate); // Note: fail fast.
                                    } else {

                                        record.setBookedBy(null);
                                        record.setBookedAt(null);
                                        record.setAvailability(true);

                                        final MobilePhonesManager.PhoneReturned phoneReturned
                                                = new MobilePhonesManager.PhoneReturned(from, true, record, null);

                                        parent.tell(phoneReturned);
                                    }
                                    return null;
                                });
                            }
                        }

                        return null;
                    });

                    return this;
                })
                .build();
    }


    // --- infra ---
    private static RuntimeException asRuntimeException(Throwable t) {
        // can't throw Throwable in lambdas
        if (t instanceof RuntimeException) {
            return (RuntimeException) t;
        } else {
            return new RuntimeException(t);
        }
    }


    // --- protocol/message definition ---


    public interface Command {
    }

    @Getter
    @ToString
    @RequiredArgsConstructor
    public static class DoBookMobile implements Command {

        private final ActorRef<MobilePhonesManager.PhoneBooked> from;
        private final ActorRef<MobilePhonesManager.MobilePhonesManagerMessage> parent;

        private final String username;
        private final String deviceId;
    }

    @Getter
    @ToString
    @RequiredArgsConstructor
    public static class DoReturnMobile implements Command {

        private final ActorRef<MobilePhonesManager.PhoneReturned> from;
        private final ActorRef<MobilePhonesManager.MobilePhonesManagerMessage> parent;

        private final String deviceId;
    }


    // events
    @Getter
    @ToString
    @RequiredArgsConstructor
    public static class DeviceInfoFetched implements Command {
        private final ActorRef<MobilePhonesStateKeeper.Command> from;

        private final String deviceId;
        private final PhoneBookedResult phoneBookedResult;
    }

    @Getter
    @ToString
    @RequiredArgsConstructor
    public static class DeviceInfoUpdated implements Command {
        private final ActorRef<MobilePhonesStateKeeper.Command> from;

        private final String deviceId;
    }
}
