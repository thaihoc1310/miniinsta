package com.thaihoc.miniinsta.controller.chat;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thaihoc.miniinsta.dto.chat.CreateParticipantRequest;
import com.thaihoc.miniinsta.dto.chat.UpdateParticipantRequest;
import com.thaihoc.miniinsta.exception.AlreadyExistsException;
import com.thaihoc.miniinsta.exception.IdInvalidException;
import com.thaihoc.miniinsta.model.Participant;
import com.thaihoc.miniinsta.service.chat.ParticipantService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/participants")
public class ParticipantController {

    private final ParticipantService participantService;

    public ParticipantController(ParticipantService participantService) {
        this.participantService = participantService;
    }

    @GetMapping("/{participantId}")
    public ResponseEntity<Participant> getParticipantById(@PathVariable long participantId) throws IdInvalidException {
        return ResponseEntity.ok(participantService.getParticipantById(participantId));
    }

    @PostMapping
    public ResponseEntity<Participant> createParticipant(@Valid @RequestBody CreateParticipantRequest request)
            throws IdInvalidException, AlreadyExistsException {
        return ResponseEntity.status(HttpStatus.CREATED).body(participantService.createParticipant(request));
    }

    @PatchMapping
    public ResponseEntity<Participant> updateParticipant(@Valid @RequestBody UpdateParticipantRequest request)
            throws IdInvalidException {
        return ResponseEntity.ok(participantService.updateParticipant(request));
    }

    @DeleteMapping("/{participantId}")
    public ResponseEntity<Void> deleteParticipant(@PathVariable long participantId) throws IdInvalidException {
        participantService.deleteParticipant(participantId);
        return ResponseEntity.noContent().build();
    }

}
