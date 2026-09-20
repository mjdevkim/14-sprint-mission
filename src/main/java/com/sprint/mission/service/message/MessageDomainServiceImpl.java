package com.sprint.mission.service.message;

import com.sprint.mission.domain.Message;
import com.sprint.mission.exception.DiscodeitException;
import com.sprint.mission.exception.DiscodeitExceptionType;
import com.sprint.mission.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Qualifier("messageService")
public class MessageDomainServiceImpl implements MessageDomainService {

    private final MessageRepository messageRepository;

    public MessageDomainServiceImpl(
            MessageRepository messageRepository
    ) {
        this.messageRepository = messageRepository;
    }

    @Override
    public Message create(Message message) {
        messageRepository.save(message);
        return message;
    }

    @Override
    public Message findById(UUID messageId) {
        if (Objects.isNull(messageId)) {
            throw new DiscodeitException(DiscodeitExceptionType.MESSAGE_ID_IS_NULL);
        }

        return messageRepository.findById(messageId)
                .orElseThrow(() ->
                        new DiscodeitException(
                                DiscodeitExceptionType.MESSAGE_NOT_FOUND,
                                messageId
                        )
                );
    }

    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {   // Optional<List<Message>>로 바꾸기
        return messageRepository.findAll().stream()
                .filter(message ->
                        Objects.equals(message.getChannelId(), channelId))
                .toList();
        //      .orElseThrow(); <- 나중에 추가하기
    }

    @Override
    public Message findMostRecentByChannelId(UUID channelId) {
        return messageRepository.findMostRecentByChannelId(channelId)
                .orElse(null);
    }

    @Override
    public Message update(Message updatingMessage) {
        findById(updatingMessage.getId());
        return messageRepository.save(updatingMessage);
    }

    @Override
    public void delete(UUID messageId) {
        findById(messageId);
        messageRepository.delete(messageId);
    }

    @Override
    public void deleteAllByChannelId(UUID channelId) {
        List<Message> messages = findAllByChannelId(channelId);

        for (Message message : messages) {
            messageRepository.delete(message.getId());
        }
    }
}
