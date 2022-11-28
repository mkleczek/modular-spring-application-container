package net.codespaces.serviceloader.container;

import org.springframework.context.ApplicationContext;

public class ApplicationEventPublisherProvider
{
    
    public static ApplicationContext provider() {
        return ContainerModule.applicationContext;
    }

}
