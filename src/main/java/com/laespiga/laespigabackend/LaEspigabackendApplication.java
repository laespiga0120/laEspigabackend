package com.laespiga.laespigabackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling; // Importar

@SpringBootApplication
@EnableScheduling // Habilitar tareas programadas
public class LaEspigabackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(LaEspigabackendApplication.class, args);
    }
}
