package com.rahul.chitfund_backend.service;

import com.rahul.chitfund_backend.entity.ChitGroup;
import com.rahul.chitfund_backend.entity.Meeting;
import com.rahul.chitfund_backend.exception.CustomException;
import com.rahul.chitfund_backend.repository.ChitGroupRepository;
import com.rahul.chitfund_backend.repository.MeetingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MeetingService {

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private ChitGroupRepository chitGroupRepository;

    public Meeting createMeeting(Long chitGroupId, Integer monthNumber, String meetLink, String notes) {
        ChitGroup group = chitGroupRepository.findById(chitGroupId)
                .orElseThrow(() -> new CustomException("Chit group not found"));

        // Check if a meeting already exists for this month
        if (meetingRepository.findByChitGroupIdAndMonthNumber(chitGroupId, monthNumber).isPresent()) {
            throw new CustomException("A meeting link already exists for month " + monthNumber + ". Use update instead.");
        }

        Meeting meeting = new Meeting();
        meeting.setChitGroup(group);
        meeting.setMonthNumber(monthNumber);
        meeting.setMeetLink(meetLink);
        meeting.setNotes(notes);
        meeting.setCreatedDate(LocalDate.now());

        return meetingRepository.save(meeting);
    }

    public List<Meeting> getMeetingsByGroup(Long chitGroupId) {
        chitGroupRepository.findById(chitGroupId)
                .orElseThrow(() -> new CustomException("Chit group not found"));
        return meetingRepository.findByChitGroupId(chitGroupId);
    }

    public Meeting updateMeeting(Long id, String meetLink, String notes) {
        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new CustomException("Meeting not found"));

        if (meetLink != null && !meetLink.isBlank()) {
            meeting.setMeetLink(meetLink);
        }
        meeting.setNotes(notes);

        return meetingRepository.save(meeting);
    }

    public void deleteMeeting(Long id) {
        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new CustomException("Meeting not found"));
        meetingRepository.delete(meeting);
    }

}
