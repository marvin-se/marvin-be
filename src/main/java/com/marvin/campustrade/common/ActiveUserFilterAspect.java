package com.marvin.campustrade.common;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.hibernate.Session;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Aspect
@Component
@Slf4j
public class ActiveUserFilterAspect {

    @PersistenceContext
    private EntityManager entityManager;

    @Around(
            "@annotation(com.marvin.campustrade.common.IncludeInactiveUsers) || " +
                    "@within(com.marvin.campustrade.common.IncludeInactiveUsers)"
    )
    @Transactional
    public Object proceedWithoutActiveFilter(ProceedingJoinPoint joinPoint) throws Throwable {

        // DO NOT enable the filter
        log.debug("Including inactive users for {}", joinPoint.getSignature());

        return joinPoint.proceed();
    }

    @Around(
            "execution(* com.marvin.campustrade..service..*(..)) && " +
                    "!@annotation(com.marvin.campustrade.common.IncludeInactiveUsers) && " +
                    "!@within(com.marvin.campustrade.common.IncludeInactiveUsers)"
    )
    @Transactional
    public Object proceedWithActiveFilter(ProceedingJoinPoint joinPoint) throws Throwable {

        Session session = entityManager.unwrap(Session.class);
        session.enableFilter("activeUserFilter")
                .setParameter("isActive", true);

        try {
            return joinPoint.proceed();
        } finally {
            session.disableFilter("activeUserFilter");
        }
    }
}
