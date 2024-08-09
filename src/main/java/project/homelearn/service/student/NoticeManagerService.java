package project.homelearn.service.student;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.homelearn.dto.manager.board.BoardReadDto;
import project.homelearn.entity.manager.ManagerBoard;
import project.homelearn.repository.board.ManagerBoardRepository;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NoticeManagerService {
    private final ManagerBoardRepository managerBoardRepository;

    public Page<BoardReadDto> getManagerNotice(Pageable pageable) {
        Page<ManagerBoard> managerBoards = managerBoardRepository.findManagerBoardsBy(pageable);
        return managerBoards.map(this::convertToListWithFileDto);
    }

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
}