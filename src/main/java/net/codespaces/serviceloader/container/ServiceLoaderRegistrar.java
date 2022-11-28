package net.codespaces.serviceloader.container;

import java.util.ServiceLoader;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

abstract class ServiceLoaderRegistrar<C extends ConfigurableApplicationContext & BeanDefinitionRegistry> implements ApplicationContextInitializer<C>, BeanDefinitionRegistryPostProcessor
{
    private static Logger LOG = LoggerFactory.getLogger(ServiceLoaderRegistrar.class);
    private static final Module SELF_MODULE = ServiceLoaderRegistrar.class.getModule();
    
    private final Module module;
    private final ExecutorService loadingExecutor;
    
    protected ServiceLoaderRegistrar() {
        this.loadingExecutor = ServiceLoader
                .load(ExecutorService.class)
                .stream()
                .filter(provider -> provider.type().getModule() == SELF_MODULE)
                .map(Supplier::get).findFirst().orElseThrow(AssertionError::new);
        this.module = getClass().getModule();
    }
    
    @Override
    public final void initialize(C applicationContext)
    {
        initializeContext(applicationContext);
    }
    
    @Override
    public final void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public final void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException
    {
        initializeContext(registry);
    }

    private <T> void initializeContext(BeanDefinitionRegistry applicationContext)
    {
        for (final var provides : module.getDescriptor().provides()) {
            try {
                @SuppressWarnings("unchecked")
                final Class<T> serviceClass = (Class<T>) module.getClassLoader().loadClass(provides.service());
                SELF_MODULE.addReads(serviceClass.getModule());
                SELF_MODULE.addUses(serviceClass);
                ServiceLoader.load(serviceClass).stream().filter(provider -> provider.type().getModule() != module).forEach(provider -> {
                    final var beanDef = beanDefinition(provider.type(), provider);
                    final var name = provider.type().getName();
                    applicationContext.registerBeanDefinition(name, beanDef);
                    LOG.debug("Registered bean definition {} of type {}", name, provider.type());
                });
            }
            catch (ClassNotFoundException e) {
                LOG.warn("Service class not found. Corrupt module-info?", e);
            }
        }        
    }
    
    private <S> BeanDefinition beanDefinition(Class<? extends S> implClass, Supplier<S> supplier) {
        final var serviceFuture = loadingExecutor.submit(supplier::get);
        final var constructorArgumentValues = new ConstructorArgumentValues();
        constructorArgumentValues.addIndexedArgumentValue(0, implClass);
        constructorArgumentValues.addIndexedArgumentValue(1, serviceFuture);
        final var beanDefinition = new RootBeanDefinition(FutureBeanFactory.class, constructorArgumentValues, new MutablePropertyValues());
        beanDefinition.setSynthetic(true);
        return beanDefinition;
    }
    
}
