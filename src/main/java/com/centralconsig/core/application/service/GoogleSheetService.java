package com.centralconsig.core.application.service;

import com.centralconsig.core.domain.entity.GoogleSheet;
import com.centralconsig.core.domain.repository.GoogleSheetRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoogleSheetService {

    private final GoogleSheetRepository googleSheetRepository;

    public GoogleSheetService(GoogleSheetRepository googleSheetRepository) {
        this.googleSheetRepository = googleSheetRepository;
    }

    public List<GoogleSheet> getAllSheets() {
        return googleSheetRepository.findAll();
    }
}
