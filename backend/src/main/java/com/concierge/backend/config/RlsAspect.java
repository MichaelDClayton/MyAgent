package com.concierge.backend.config;

import com.concierge.backend.context.TenantContext;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RlsAspect {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Before("execution(* com.concierge.backend.repository.*.*(..))")
    public void setTenantInDb() {
        String tenantId = TenantContext.getCurrentTenant();
        if (tenantId != null) {
            // Set the session variable for Postgres
            jdbcTemplate.execute("SET app.current_tenant_id = '" + tenantId + "'");
        } else {
            // Unset or set to a dummy UUID to prevent access
            jdbcTemplate.execute("SET app.current_tenant_id = '" + java.util.UUID.randomUUID() + "'");
        }
    }
}
