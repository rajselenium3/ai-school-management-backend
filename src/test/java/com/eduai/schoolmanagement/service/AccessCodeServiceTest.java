package com.eduai.schoolmanagement.service;

import com.eduai.schoolmanagement.entity.AccessCode;
import com.eduai.schoolmanagement.entity.Student;
// ...existing code...
import com.eduai.schoolmanagement.repository.AccessCodeRepository;
import com.eduai.schoolmanagement.repository.StudentRepository;
import com.eduai.schoolmanagement.repository.ParentRepository;
// ...existing code...
import org.junit.jupiter.api.DisplayName;
// ...existing code...
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
// ...existing code...
import org.mockito.InjectMocks;
import org.mockito.Mock;
// ...existing code...
import org.mockito.junit.jupiter.MockitoExtension;

// ...existing code...
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccessCodeServiceTest {
    @Mock
    private AccessCodeRepository accessCodeRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private ParentRepository parentRepository;
    @InjectMocks
    private AccessCodeService accessCodeService;

    @Test
    @DisplayName("should return all access codes")
    void getAllAccessCodes_returnsAll() {
        List<AccessCode> codes = List.of(mock(AccessCode.class));
        when(accessCodeRepository.findAll()).thenReturn(codes);
        assertThat(accessCodeService.getAllAccessCodes()).isEqualTo(codes);
    }

    @Test
    @DisplayName("should return access code by code if active")
    void getAccessCodeByCode_active() {
        AccessCode code = mock(AccessCode.class);
        when(accessCodeRepository.findByAccessCodeAndActive("abc", true)).thenReturn(Optional.of(code));
        assertThat(accessCodeService.getAccessCodeByCode("abc")).contains(code);
    }

    @Test
    @DisplayName("should return empty if access code not found")
    void getAccessCodeByCode_notFound() {
        when(accessCodeRepository.findByAccessCodeAndActive("abc", true)).thenReturn(Optional.empty());
        assertThat(accessCodeService.getAccessCodeByCode("abc")).isEmpty();
    }

    @Test
    @DisplayName("should generate student access code if not exists")
    void generateStudentAccessCode_new() {
        Student student = mock(Student.class);
        when(studentRepository.findById("id")).thenReturn(Optional.of(student));
        when(accessCodeRepository.findValidUnusedCodeByStudentId("id")).thenReturn(Optional.empty());
        when(student.getUser()).thenReturn(null);
        AccessCode code = mock(AccessCode.class);
        when(accessCodeRepository.save(any())).thenReturn(code);
        assertThat(accessCodeService.generateStudentAccessCode("id")).isEqualTo(code);
    }

    @Test
    @DisplayName("should return existing student access code if present")
    void generateStudentAccessCode_existing() {
        Student student = mock(Student.class);
        AccessCode code = mock(AccessCode.class);
        when(studentRepository.findById("id")).thenReturn(Optional.of(student));
        when(accessCodeRepository.findValidUnusedCodeByStudentId("id")).thenReturn(Optional.of(code));
        assertThat(accessCodeService.generateStudentAccessCode("id")).isEqualTo(code);
    }

    @Test
    @DisplayName("should throw if student not found")
    void generateStudentAccessCode_studentNotFound() {
        when(studentRepository.findById("id")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> accessCodeService.generateStudentAccessCode("id"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Student not found");
    }

    @Test
    @DisplayName("should validate access code for correct user type and valid code")
    void validateAccessCode_valid() {
        AccessCode code = mock(AccessCode.class);
        when(accessCodeRepository.findByAccessCodeAndActive("abc", true)).thenReturn(Optional.of(code));
        when(code.getUserType()).thenReturn("STUDENT");
        when(code.isValid()).thenReturn(true);
        when(code.canBeUsed()).thenReturn(true);
        assertThat(accessCodeService.validateAccessCode("abc", "STUDENT")).isTrue();
    }

    @Test
    @DisplayName("should not validate access code if not found")
    void validateAccessCode_notFound() {
        when(accessCodeRepository.findByAccessCodeAndActive("abc", true)).thenReturn(Optional.empty());
        assertThat(accessCodeService.validateAccessCode("abc", "STUDENT")).isFalse();
    }

    @Test
    @DisplayName("should not validate access code if user type mismatches")
    void validateAccessCode_userTypeMismatch() {
        AccessCode code = mock(AccessCode.class);
        when(accessCodeRepository.findByAccessCodeAndActive("abc", true)).thenReturn(Optional.of(code));
        when(code.getUserType()).thenReturn("PARENT");
        assertThat(accessCodeService.validateAccessCode("abc", "STUDENT")).isFalse();
    }

    @Test
    @DisplayName("should not validate access code if not valid")
    void validateAccessCode_notValid() {
        AccessCode code = mock(AccessCode.class);
        when(accessCodeRepository.findByAccessCodeAndActive("abc", true)).thenReturn(Optional.of(code));
        when(code.getUserType()).thenReturn("STUDENT");
        when(code.isValid()).thenReturn(false);
        assertThat(accessCodeService.validateAccessCode("abc", "STUDENT")).isFalse();
    }

    @Test
    @DisplayName("should not validate access code if cannot be used")
    void validateAccessCode_cannotBeUsed() {
        AccessCode code = mock(AccessCode.class);
        when(accessCodeRepository.findByAccessCodeAndActive("abc", true)).thenReturn(Optional.of(code));
        when(code.getUserType()).thenReturn("STUDENT");
        when(code.isValid()).thenReturn(true);
        when(code.canBeUsed()).thenReturn(false);
        assertThat(accessCodeService.validateAccessCode("abc", "STUDENT")).isFalse();
    }

    @Test
    @DisplayName("should use access code if valid")
    void useAccessCode_valid() {
        AccessCode code = mock(AccessCode.class);
        when(accessCodeRepository.findByAccessCodeAndActive("abc", true)).thenReturn(Optional.of(code));
        when(code.getUserType()).thenReturn("STUDENT");
        when(code.isValid()).thenReturn(true);
        when(code.canBeUsed()).thenReturn(true);
        Map<String, Object> result = accessCodeService.useAccessCode("abc", "user", "STUDENT");
        assertThat(result.get("success")).isEqualTo(true);
        verify(code).markAsUsed("user");
        verify(accessCodeRepository).save(code);
    }

    @Test
    @DisplayName("should not use access code if invalid")
    void useAccessCode_invalid() {
        when(accessCodeRepository.findByAccessCodeAndActive("abc", true)).thenReturn(Optional.empty());
        Map<String, Object> result = accessCodeService.useAccessCode("abc", "user", "STUDENT");
        assertThat(result.get("success")).isEqualTo(false);
    }

    // Add more tests for edge cases and other public methods as needed
}
