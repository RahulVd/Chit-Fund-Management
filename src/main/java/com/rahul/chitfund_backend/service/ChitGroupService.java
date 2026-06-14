package com.rahul.chitfund_backend.service;

import com.rahul.chitfund_backend.entity.ChitGroup;
import com.rahul.chitfund_backend.repository.ChitGroupRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChitGroupService {

    private final ChitGroupRepository repository;

    public ChitGroupService(ChitGroupRepository repository) {
        this.repository = repository;
    }

    public ChitGroup createChitGroup(ChitGroup chitGroup) {
        return repository.save(chitGroup);
    }

    public List<ChitGroup> getAllChitGroups() {
        return repository.findAll();
    }
}
