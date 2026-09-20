package com.sprint.mission.repository.file;

import com.sprint.mission.exception.DiscodeitException;
import com.sprint.mission.exception.DiscodeitExceptionType;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
public abstract class AbstractFileRepository<T extends Serializable> {

    private final String domainName;
    private final Path filePath;

    protected AbstractFileRepository(String domainName, String fileDirectory, String fileName) {
        this.domainName = domainName;
        // 2. 전달받은 파일명과 폴더명을 합쳐서 최종 경로를 정한다
        this.filePath = Path.of(fileDirectory, fileName).toAbsolutePath().normalize();
    }

    @SuppressWarnings("unchecked")
    protected final Map<UUID, T> loadFile() {
        if (Files.exists(filePath)) {
            saveFile(new HashMap<>());
            log.info(
                    "{} 기존 파일 초기화 완료: file={}",
                    domainName,
                    filePath
            );
        }

        // try: 파일 로드
        try (ObjectInputStream inputStream =
                     new ObjectInputStream(new FileInputStream(filePath.toFile()))) {
            Map<UUID, T> loadedData = (Map<UUID, T>) inputStream.readObject();
            log.info(
                    "{} 파일 로드 완료: file={}, count={}",
                    domainName,
                    filePath,
                    loadedData.size()
            );
            return loadedData;
        }
        // 에러 케이스 1: 파일이 존재하지 않음
        catch (FileNotFoundException exception) {
            log.info(
                    "{} 파일이 없어 빈 저장소로 시작합니다: file={}",
                    domainName,
                    filePath
            );
            return new HashMap<>();
        }
        // 에러 케이스 2: 다음 세 Exception 중 하나가 발생함
        catch (IOException | ClassNotFoundException | ClassCastException exception) {
            // IOException: 디스크/ 파일시스템 문제
            // ClassNotFoundException: 클래스가 현재 프로젝트에 없을때
            // ClassCastException: 불러온 object를 주어진 type에 casting하지 못할 떄
            log.error(
                    "{} 파일 로드 실패: file={}",
                    domainName,
                    filePath,
                    exception
            );
            throw new DiscodeitException(
                    DiscodeitExceptionType.FILE_LOAD_FAILED,
                    exception,
                    filePath
            );
        }
    }

//    protected final void deleteFile()

    protected final void saveFile(Map<UUID, T> data) {
        // try: 파일에 저장
        try {
            // 3. 저장할 때 폴더가 없으면 자동으로 생성
            Path parentDirectory = filePath.getParent();

            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }

            try (ObjectOutputStream outputStream =
                         new ObjectOutputStream(
                                 new FileOutputStream(filePath.toFile())
                         )) {

                outputStream.writeObject(data);
            }

            log.debug(
                    "{} 파일 저장 완료: file={}, count={}",
                    domainName,
                    filePath,
                    data.size()
            );
        }
        // 예외 케이스: IOException 발생함 (파일/ 디스크 문제)
        catch (IOException exception) {
            log.error(
                    "{} 파일 저장 실패: file={}",
                    domainName,
                    filePath,
                    exception
            );

            throw new DiscodeitException(
                    DiscodeitExceptionType.FILE_SAVE_FAILED,
                    exception,
                    filePath
            );
        }
    }

}
