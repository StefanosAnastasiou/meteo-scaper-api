package com.emperium.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateAnnotationUtil {

    private static SessionFactory sessionFactory;

    private static SessionFactory buildSessionFactory() {
        Configuration cfg = new Configuration();
        cfg.setProperty("hibernate.connection.username", System.getenv("DATABASE_USERNAME"));
        cfg.setProperty("hibernate.connection.password", System.getenv("DATABASE_PASSWORD"));


        return cfg.configure().buildSessionFactory();
    }

    public static SessionFactory getSessionFactory() {
        if(sessionFactory == null) sessionFactory = buildSessionFactory();
        return sessionFactory;
    }

    public static void shutdown() {
        getSessionFactory().close();
    }
}
