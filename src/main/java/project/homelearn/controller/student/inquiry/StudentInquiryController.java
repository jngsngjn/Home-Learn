package project.homelearn.controller.student.inquiry;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.homelearn.dto.student.inquiry.StudentToMangerInquiryDto;
import project.homelearn.service.student.StudentInquiryService;

import java.security.Principal;

/**
 * Author : 정성진
 */
@Slf4j
@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentInquiryController {

    private final StudentInquiryService inquiryService;

    // 문의 등록 : 학생 -> 매니저
    @PostMapping("/inquiries/manager")
    public ResponseEntity<?> inquiryToManager(Principal principal,
                                              @Valid @RequestBody StudentToMangerInquiryDto inquiryDto) {
        if (principal == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String username = principal.getName();

        inquiryService.inquiryToManger(username, inquiryDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 문의 수정 : 학생 -> 매니저
    @PatchMapping("/inquiries/manager/{inquiryId}")
    public ResponseEntity<?> modifyManagerInquiry(@PathVariable("inquiryId") Long inquiryId, Principal principal,
                                                  @Valid @RequestBody StudentToMangerInquiryDto inquiryDto) {
        if (principal == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String username = principal.getName();
        boolean result = inquiryService.modifyManagerInquiry(inquiryId, username, inquiryDto);

        if (result) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}