package com.bistro.shared.events;

import lombok.RequiredArgsConstructor;
import org.springframework.modulith.events.IncompleteEventPublications;
import org.springframework.modulith.events.ResubmissionOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FailedEventResubmitter {

    private final IncompleteEventPublications incompleteEventPublications;

    @Scheduled(fixedDelay = 1000)
    public void resubmitFailed(){
        incompleteEventPublications.resubmitIncompletePublications(ResubmissionOptions.defaults());
    }

}
