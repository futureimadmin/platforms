package com.nebula.dataplane.controller;

import com.nebula.dataplane.service.DataPlaneService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
@RequestMapping("/api/v1/platform")
@RequiredArgsConstructor
public class DataPlaneController {

    @Autowired
    private final DataPlaneService dataPlaneService;

    @PostMapping("/create/{planId}")
    public String executeFlow(@PathVariable("planId") String planId) {
        log.info("Received request to execute flow for plan: {}", planId);
        try {
            return dataPlaneService.generateExecutionFlowJobSources(planId);
        } catch (ExecutionException | InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
