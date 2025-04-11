package com.thaihoc.miniinsta.service.chat;

import com.thaihoc.miniinsta.dto.chat.CreateParticipantRequest;
import com.thaihoc.miniinsta.dto.chat.UpdateParticipantRequest;
import com.thaihoc.miniinsta.exception.AlreadyExistsException;
import com.thaihoc.miniinsta.exception.IdInvalidException;
import com.thaihoc.miniinsta.model.Participant;

public interface ParticipantService {

    Participant getParticipantById(long participantId) throws IdInvalidException;

    Participant createParticipant(CreateParticipantRequest request) throws IdInvalidException, AlreadyExistsException;

    Participant updateParticipant(UpdateParticipantRequest request) throws IdInvalidException;

    void deleteParticipant(long participantId) throws IdInvalidException;

}
