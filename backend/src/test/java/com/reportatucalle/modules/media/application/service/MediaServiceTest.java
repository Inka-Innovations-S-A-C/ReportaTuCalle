package com.reportatucalle.modules.media.application.service;

import com.reportatucalle.modules.media.domain.portsout.StoragePort;
import com.reportatucalle.shared.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class MediaServiceTest {

    private final StoragePort storagePort = mock(StoragePort.class);
    private final MediaService mediaService = new MediaService(storagePort);

    @Test
    void uploadImage_withValidFile_generatesUniqueNameAndUploads() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "bache.png", "image/png", "contenido".getBytes());
        when(storagePort.uploadFile(anyString(), any(byte[].class))).thenReturn("http://localhost/uploads/foto.png");

        String url = mediaService.uploadImage(file);

        assertEquals("http://localhost/uploads/foto.png", url);
        verify(storagePort).uploadFile(argThat(name -> name.endsWith(".png")), any(byte[].class));
    }

    @Test
    void uploadImage_withoutExtension_usesJpg() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "foto", "image/jpeg", "abc".getBytes());
        when(storagePort.uploadFile(anyString(), any(byte[].class))).thenReturn("ok");

        assertEquals("ok", mediaService.uploadImage(file));
        verify(storagePort).uploadFile(argThat(name -> name.endsWith(".jpg")), any(byte[].class));
    }

    @Test
    void uploadImage_rejectsEmptyFile() {
        MultipartFile file = new MockMultipartFile("file", "vacio.png", "image/png", new byte[0]);

        BusinessException ex = assertThrows(BusinessException.class, () -> mediaService.uploadImage(file));

        assertEquals("FILE_EMPTY", ex.getErrorCode());
    }

    @Test
    void uploadImage_rejectsInvalidContentType() {
        MultipartFile file = new MockMultipartFile("file", "script.js", "application/javascript", "x".getBytes());

        BusinessException ex = assertThrows(BusinessException.class, () -> mediaService.uploadImage(file));

        assertEquals("INVALID_FILE_TYPE", ex.getErrorCode());
    }

    @Test
    void uploadImage_rejectsTooLargeFile() {
        byte[] data = new byte[(5 * 1024 * 1024) + 1];
        MultipartFile file = new MockMultipartFile("file", "grande.png", "image/png", data);

        BusinessException ex = assertThrows(BusinessException.class, () -> mediaService.uploadImage(file));

        assertEquals("FILE_TOO_LARGE", ex.getErrorCode());
    }

    @Test
    void uploadImage_whenMultipartFails_wrapsBusinessException() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(10L);
        when(file.getContentType()).thenReturn("image/webp");
        when(file.getOriginalFilename()).thenReturn("foto.webp");
        when(file.getBytes()).thenThrow(new IOException("fallo"));

        BusinessException ex = assertThrows(BusinessException.class, () -> mediaService.uploadImage(file));

        assertEquals("FILE_PROCESSING_ERROR", ex.getErrorCode());
    }
}
