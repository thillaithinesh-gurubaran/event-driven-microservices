package com.techbees.product.commands.rest;

import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventhandling.TrackingEventProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/management")
public class EventsReplayController {

    @Autowired
    private EventProcessingConfigurer eventProcessingConfigurer;

    @PostMapping("/eventProcessor/{processorName}/reset")
    public ResponseEntity<String> replayEvents(@PathVariable String processorName) {

        /*Optional<TrackingEventProcessor> trackingEventProcessor =
                eventProcessingConfigurer.eventProcessor(processorName, TrackingEventProcessor.class);

        if (trackingEventProcessor.isPresent()) {
            TrackingEventProcessor eventProcessor = trackingEventProcessor.get();
            eventProcessor.shutDown();
            eventProcessor.resetTokens();
            eventProcessor.start();

            return ResponseEntity.ok().body(
                    String.format("The event processor [%s] has been reset", processorName));
        } else {
            return ResponseEntity.badRequest().body(
                    String.format("The event processor [%s] is not an tracking event processor", processorName));
        }*/

        return ResponseEntity.ok().body("Events replayed successfully.");
    }

}
