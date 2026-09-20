package com.sprint.mission.repository.file;

import com.sprint.mission.domain.Message;
import com.sprint.mission.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileMessageRepository
        extends AbstractFileRepository<Message>
        implements MessageRepository {

    private static final String MESSAGE_FILENAME = "messages.ser";

    private final Map<UUID, Message> messageMap;

    public FileMessageRepository(
            @Value("${discodeit.repository.file-directory:.discodeit/objects}")
            String fileDirectory
    ) {
        super("Message", fileDirectory, MESSAGE_FILENAME);
        this.messageMap = loadFile();
    }

    @Override
    public Message save(Message message) {
        messageMap.put(message.getId(), message);
        saveFile(messageMap);
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
            if (currentMessage.getChannelId().equals(channelId)) continue;

            // 가장 최근 보낸 메세지 선정 로직
            if (Objects.isNull(mostRecentMessage)
                    || currentMessage.getCreatedAt()
                        .isAfter(mostRecentMessage.getCreatedAt())) {
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
        saveFile(messageMap);
    }

    @Override
    public void deleteAllByChannelId(UUID channelId) {
        List<Message> messagesToDelete = new ArrayList<>();

        // channelId로 보내진 모든 메세지들을 모은다
        for (Message message : messageMap.values()) {
            if (Objects.equals(message.getChannelId(), channelId)) {
                messagesToDelete.add(message);
            }
        }

        // 그 메세지들을 하나하나 다 지운다
        for (Message message : messagesToDelete) {
            messageMap.remove(message.getId());
        }

        saveFile(messageMap);
    }

}
