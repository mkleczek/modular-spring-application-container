package net.codespaces.serviceloader.container;

import java.util.concurrent.Future;

import org.springframework.beans.factory.FactoryBean;

public record FutureBeanFactory<T>(Class<T> objectType, Future<? extends T> future) implements FactoryBean<T>
{

    @Override
    public T getObject() throws Exception
    {
        return future().get();
    }

    @Override
    public Class<?> getObjectType()
    {
        return objectType();
    }

}
