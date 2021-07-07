package com.chriniko.mob.booking.service;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.PathMatchers;
import akka.http.javadsl.server.Route;
import com.chriniko.mob.booking.service.dto.BookMobileInfo;
import com.chriniko.mob.booking.service.dto.PhoneBookedResult;
import com.chriniko.mob.booking.service.service.MobilePhonesManager;
import com.chriniko.mob.booking.service.service.MobilePhonesStateKeeper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.vavr.Tuple2;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletionStage;

import static akka.actor.typed.javadsl.AskPattern.ask;
import static akka.http.javadsl.server.PathMatchers.segment;

public class MoBookingService extends AllDirectives {

    private final ObjectMapper objectMapper;

    private final ActorSystem<MobilePhonesManager.MobilePhonesManagerMessage> system;
    private final ActorRef<MobilePhonesManager.MobilePhonesManagerMessage> mobPhoneManager;

    public MoBookingService(ObjectMapper objectMapper, ActorSystem<MobilePhonesManager.MobilePhonesManagerMessage> system) {
        this.objectMapper = objectMapper;
        this.system = system;
        this.mobPhoneManager = system;
    }

    public Route createRoute() {

        final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();

        return concat(

                // Note: view state endpoints
                get(() -> path("mobphones", () -> {

                    final CompletionStage<MobilePhonesStateKeeper.DeviceInfosResult> deviceInfosResult = ask(mobPhoneManager,
                            param -> new MobilePhonesManager.GetAllBookedInfo(param), Duration.ofSeconds(3), system.scheduler()
                    );

                    return onSuccess(() -> deviceInfosResult, result -> {

                        final List<Tuple2<String /*deviceId*/, PhoneBookedResult>> entries = result.getEntries();

                        final ArrayNode array = objectMapper.createArrayNode();

                        entries.forEach(entry -> {

                            final String deviceId = entry._1;
                            final PhoneBookedResult phoneBookedResult = entry._2;

                            final ObjectNode objectJson = objectMapper.createObjectNode()
                                    .put("deviceId", deviceId)
                                    .putPOJO("bookInfo", phoneBookedResult);

                            array.add(objectJson);
                        });


                        return completeOK(array, Jackson.marshaller());
                    });
                })),


                // Note: operational endpoints
                put(() -> path(PathMatchers.segment("mobphones").slash("book").slash(segment()), (String deviceId) ->

                        entity(Jackson.unmarshaller(BookMobileInfo.class), bookMobileInfo -> {

                            final Set<ConstraintViolation<BookMobileInfo>> violations = validator.validate(bookMobileInfo);
                            if (!violations.isEmpty()) {
                                final ArrayNode errors = objectMapper.createArrayNode();
                                violations.forEach(violation -> errors.add(violation.getMessage()));

                                final ObjectNode json = objectMapper.createObjectNode()
                                        .set("errors", errors);
                                return complete(StatusCodes.BAD_REQUEST, json, Jackson.marshaller());
                            }

                            final CompletionStage<MobilePhonesManager.PhoneBooked> phoneBookedResult = ask(mobPhoneManager,
                                    param -> new MobilePhonesManager.BookPhone(param, bookMobileInfo.getUser(), deviceId), Duration.ofSeconds(3), system.scheduler()
                            );

                            return onSuccess(() -> phoneBookedResult, phoneBooked -> {

                                if (phoneBooked.isOutcome()) {

                                    final PhoneBookedResult res = phoneBooked.getPhoneBookedResult();
                                    return completeOK(res, Jackson.marshaller());

                                } else {
                                    final ObjectNode json = objectMapper.createObjectNode()
                                            .put("outcome", false)
                                            .put("error", phoneBooked.getReasonOfNotBooked());

                                    return complete(StatusCodes.BAD_REQUEST, json, Jackson.marshaller());
                                }
                            });
                        }))
                ),

                put(() -> path(PathMatchers.segment("mobphones").slash("return").slash(segment()),
                        (String deviceId) -> {

                            final CompletionStage<MobilePhonesManager.PhoneReturned> phoneReturnedResult = ask(mobPhoneManager,
                                    param -> new MobilePhonesManager.ReturnPhone(param, deviceId), Duration.ofSeconds(3), system.scheduler()
                            );

                            return onSuccess(() -> phoneReturnedResult, phoneReturned -> {
                                if (phoneReturned.isOutcome()) {

                                    final PhoneBookedResult res = phoneReturned.getPhoneBookedResult();
                                    return completeOK(res, Jackson.marshaller());

                                } else {
                                    final ObjectNode json = objectMapper.createObjectNode()
                                            .put("outcome", false)
                                            .put("error", phoneReturned.getReasonOfNotBooked());

                                    return complete(StatusCodes.BAD_REQUEST, json, Jackson.marshaller());
                                }
                            });
                        })
                )


        );

    }


}
