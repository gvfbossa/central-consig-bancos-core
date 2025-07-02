package com.centralconsig.core.domain.repository;

import com.centralconsig.core.domain.entity.GoogleSheet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoogleSheetRepository extends JpaRepository<GoogleSheet, Long> {
    GoogleSheet findByFileName(String nome);
}