package com.centralconsig.core.domain.repository;

import com.centralconsig.core.domain.entity.SystemConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemConfigurationRepository extends JpaRepository<SystemConfiguration, Long> {
}
