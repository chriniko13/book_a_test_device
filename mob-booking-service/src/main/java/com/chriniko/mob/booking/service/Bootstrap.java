package com.chriniko.mob.booking.service;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import com.chriniko.mob.booking.service.service.MobilePhonesManager;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.CompletionStage;

public class Bootstrap {

    private static final int PORT = 8080;

    public static void main(String[] args) throws Exception {

        System.out.println("WILL START THE SERVER NOW...");

        // boot up server using the route as defined below
        final Behavior<MobilePhonesManager.MobilePhonesManagerMessage> mobilePhonesManagerMessageBehavior = MobilePhonesManager.create();
        final ActorSystem<MobilePhonesManager.MobilePhonesManagerMessage> system = ActorSystem.create(mobilePhonesManagerMessageBehavior, "routes");

        final Http http = Http.get(system);

        //In order to access all directives we need an instance where the routes are define.
        final ObjectMapper objectMapper = new ObjectMapper();
        final MoBookingService moBookingService = new MoBookingService(objectMapper, system);


        final CompletionStage<ServerBinding> binding =
                http.newServerAt("localhost", PORT)
                        .bind(moBookingService.createRoute());


        System.out.println("Server online at http://localhost:8080/\nPress RETURN to stop...");

        String tree = system.printTree();
        System.out.println(tree);

        System.in.read(); // let it run until user presses return

        binding
                .thenCompose(ServerBinding::unbind) // trigger unbinding from the port
                .thenAccept(unbound -> system.terminate()); // and shutdown when done
    }


}
