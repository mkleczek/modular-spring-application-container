package net.codespaces.serviceloader.container;

import org.springframework.boot.SpringApplication;
import org.springframework.context.support.GenericApplicationContext;

public abstract class AbstractSpringModule extends ServiceLoaderRegistrar<GenericApplicationContext>
{
    
    protected static void run(Class<? extends AbstractSpringModule> source) {
        SpringApplication.run(source);
    }
}
