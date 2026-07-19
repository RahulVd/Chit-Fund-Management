package com.rahul.chitfund_backend.controller;

import com.rahul.chitfund_backend.dto.MeetingRequest;
import com.rahul.chitfund_backend.entity.Meeting;
import com.rahul.chitfund_backend.service.MeetingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/meetings")
public class MeetingController {

    @Autowired
    private MeetingService meetingService;

    @PostMapping
    public ResponseEntity<Meeting> createMeeting(@Valid @RequestBody MeetingRequest request) {
        Meeting meeting = meetingService.createMeeting(
                request.getChitGroupId(),
                request.getMonthNumber(),
                request.getMeetLink(),
                request.getNotes()
        );
        return ResponseEntity.ok(meeting);
    }

    @GetMapping("/group/{chitGroupId}")
    public ResponseEntity<List<Meeting>> getMeetings(@PathVariable Long chitGroupId) {
        return ResponseEntity.ok(meetingService.getMeetingsByGroup(chitGroupId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Meeting> updateMeeting(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Meeting meeting = meetingService.updateMeeting(
                id,
                body.get("meetLink"),
                body.get("notes")
        );
        return ResponseEntity.ok(meeting);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeeting(@PathVariable Long id) {
        meetingService.deleteMeeting(id);
        return ResponseEntity.ok().build();
    }

}
