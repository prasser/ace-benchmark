package org.trustdeck.benchmark.psneval;

import org.trustdeck.benchmark.psnservice.PSNService;

public interface Work {
    void perform(PSNService service, String token);
}
