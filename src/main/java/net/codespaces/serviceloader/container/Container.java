package net.codespaces.serviceloader.container;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Container
{
    
    public static void main(String[] args)
    {
        final var app = new SpringApplication(Container.class);
        app.addInitializers(new ServiceLoaderInitializer() {});
        app.run(args);
    }
    
}
