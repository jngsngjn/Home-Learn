package project.homelearn.service.manager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import project.homelearn.config.storage.StorageConstants;
import project.homelearn.dto.common.FileDto;
import project.homelearn.dto.manager.board.BoardCreateDto;
import project.homelearn.dto.manager.board.BoardReadDto;
import project.homelearn.dto.manager.board.BoardUpdateDto;
import project.homelearn.dto.teacher.dashboard.ManagerBoardDto;
import project.homelearn.entity.manager.ManagerBoard;
import project.homelearn.repository.board.ManagerBoardRepository;
import project.homelearn.service.common.StorageService;

import java.util.List;

/**
 * Author : 동재완
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ManagerBoardService {

    private final StorageService storageService;
    private final ManagerBoardRepository managerBoardRepository;

    // 생성 서비스
    public boolean createManagerBoard(BoardCreateDto managerBoardWriteDto) {
        try {
            ManagerBoard board = new ManagerBoard();
            board.setTitle(managerBoardWriteDto.getTitle());
            board.setContent(managerBoardWriteDto.getContent());
            board.setEmergency(managerBoardWriteDto.getEmergency());

            MultipartFile file = managerBoardWriteDto.getFile();
            if (file != null && !file.isEmpty()) {
                FileDto fileDto = storageService.uploadFile(file, StorageConstants.ANNOUNCEMENT_STORAGE);

                board.setUploadFileName(fileDto.getOriginalFileName());
                board.setStoreFileName(fileDto.getUploadFileName());
                board.setFilePath(fileDto.getFilePath());
            }

            managerBoardRepository.save(board);
            return true;
        } catch (Exception e) {
            log.error("Error creating manager board", e);
            return false;
        }
    }

    // 변환된 DTO, 조회 서비스
    public Page<BoardReadDto> getManagerBoards(Pageable pageable) {
        Page<ManagerBoard> managerBoards = managerBoardRepository.findManagerBoardsBy(pageable);
        return managerBoards.map(this::convertToListWithFileDto);
    }

    // 조회를 위한 DTO변환
    public BoardReadDto convertToListWithFileDto (ManagerBoard managerBoard) {
        return new BoardReadDto(
                managerBoard.getId(),
                managerBoard.getTitle(),
                managerBoard.getContent(),
                managerBoard.getCreatedDate(),
                managerBoard.isEmergency(),
                managerBoard.getFilePath(),
                managerBoard.getUploadFileName()
        );
    }

    // 수정 서비스
    public boolean updateManagerBoard(Long id, BoardUpdateDto boardUpdateDto) {
        try {
            ManagerBoard board = managerBoardRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Error getting manager board with id" + id));
            board.setTitle(boardUpdateDto.getTitle());
            board.setContent(boardUpdateDto.getContent());
            board.setEmergency(boardUpdateDto.getEmergency());

            MultipartFile file = boardUpdateDto.getFile();
            String previousFilePath = board.getFilePath();

            //파일 삭제 조건 (먼저 파일을 삭제한 후 새 파일을 업로드하는 순서로 처리)
            if (boardUpdateDto.isUseDefaultFile() || (file != null && !file.isEmpty())) {
                if (previousFilePath != null) {
                   storageService.deleteFile(previousFilePath);
                   board.setUploadFileName(null);
                   board.setStoreFileName(null);
                   board.setFilePath(null);
                }
            }

            //새 파일 업로드 조건
            if (file != null && !file.isEmpty()) {
                FileDto fileDto = storageService.uploadFile(file, StorageConstants.ANNOUNCEMENT_STORAGE);
                board.setUploadFileName(fileDto.getOriginalFileName());
                board.setStoreFileName(fileDto.getUploadFileName());
                board.setFilePath(fileDto.getFilePath());
            }

            managerBoardRepository.save(board);
            return true;
        } catch (Exception e) {
            log.error("Error updating manager board", e);
            return false;
        }
    }

    // 삭제 서비스
    public boolean deleteManagerBoards(List<Long> ids) {
        try {
            List<ManagerBoard> boards = managerBoardRepository.findAllById(ids);
            for (ManagerBoard board : boards) {
                String file = board.getFilePath();
                if (file != null) {
                    storageService.deleteFile(file);
                }
            }
            managerBoardRepository.deleteAllById(ids);
            return true;
        } catch (Exception e) {
            log.error("Error deleting manager board", e);
            return false;
        }
    }

    public List<ManagerBoardDto> viewManagerBoardRecent() {
        return managerBoardRepository.findManagerBoardRecent4();
    }
}