package com.nebula.controlplane;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main application class for Nebula Control Plane.
 * This is the master agent that orchestrates the entire Nebula platform.
 */
@SpringBootApplication
@EnableAsync
public class NebulaControlPlaneApplication {

    public static void main(String[] args) {
        SpringApplication.run(NebulaControlPlaneApplication.class, args);
    }
}