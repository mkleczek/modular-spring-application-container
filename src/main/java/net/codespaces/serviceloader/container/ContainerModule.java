package net.codespaces.serviceloader.container;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
class ContainerModule
{
    
    static ConfigurableApplicationContext applicationContext = SpringApplication.run(ContainerModule.class);

}
