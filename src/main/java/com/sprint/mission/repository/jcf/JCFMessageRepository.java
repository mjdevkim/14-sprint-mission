package com.sprint.mission.repository.jcf;

import com.sprint.mission.domain.Message;
import com.sprint.mission.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFMessageRepository implements MessageRepository {

    private final Map<UUID, Message> messageMap = new HashMap<>();


    @Override
    public Message save(Message message) {
        messageMap.put(message.getId(), message);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID messageId) {
        return Optional.ofNullable(messageMap.get(messageId));
    }


    @Override
    public Optional<Message> findMostRecentByChannelId(UUID channelId) {
        Message mostRecentMessage = null;

        for (Message currentMessage : messageMap.values()) {
            if (!currentMessage.getChannelId().equals(channelId)) {
                continue;
            }

            if (Objects.isNull(mostRecentMessage) ||
                    currentMessage.getCreatedAt()
                        .isAfter(mostRecentMessage.getCreatedAt())
            ) {
                mostRecentMessage = currentMessage;
            }
        }

        return Optional.ofNullable(mostRecentMessage);
    }


    @Override
    public List<Message> findAll() {
        return messageMap.values().stream().toList();
    }

    @Override
    public void delete(UUID messageId) {
        messageMap.remove(messageId);
    }


    @Override
    public void deleteAllByChannelId(UUID channelId) {
        // 한 채널 안에 있는 모든 메시지
        List<UUID> messageIdsToDelete = new ArrayList<>();

        for (Message message : messageMap.values()) {
            if (Objects.equals(message.getChannelId(), channelId)) {
                messageIdsToDelete.add(message.getId());
            }
        }

        for (UUID messageId : messageIdsToDelete) {
            messageMap.remove(messageId);
        }
    }
}
