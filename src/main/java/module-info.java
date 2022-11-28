import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;

import net.codespaces.serviceloader.container.ApplicationEventPublisherProvider;

module net.codespaces.serviceloader.container
{
    requires org.slf4j;
    requires spring.core;
    requires spring.beans;
    requires spring.context;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    
    uses ApplicationListener;
    
    provides ApplicationEventPublisher with ApplicationEventPublisherProvider;
}