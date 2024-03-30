package aa.timonin.service.impl;

import aa.timonin.entity.AppDocument;
import aa.timonin.entity.BinaryContent;
import aa.timonin.exceptions.UploadFileException;
import aa.timonin.repository.AppDocumentRepository;
import aa.timonin.repository.BinaryContentRepository;
import aa.timonin.service.FileService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Log4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    @Value("${token}")
    private String token;
    @Value("${service.file_info.uri}")
    private String fileInfoUri;
    @Value("${service.file_storage.uri}")
    private String fileStorageUri;
    private final AppDocumentRepository appDocumentRepository;
    private final BinaryContentRepository binaryContentRepository;


    @Override
    public AppDocument processDoc(Message telegramMessage) {
        String fileId = telegramMessage.getDocument().getFileId();
        ResponseEntity<String> response = getFilePath(fileId);
        try{
            if(response.getStatusCode() == HttpStatus.OK){
                JSONObject jsonObject = new JSONObject(response.getBody());
                String filePath = String.valueOf(jsonObject
                        .getJSONObject("result")
                        .getString("file_path"));
                byte[] fileInBytes = downloadFile(filePath);
                BinaryContent transientBinaryContent = BinaryContent.builder()
                        .fileIsArrayOfBytes(fileInBytes)
                        .build();
                BinaryContent persistentBinaryContent = binaryContentRepository.save(transientBinaryContent);
                Document telegramDocument = telegramMessage.getDocument();
                AppDocument transientAppDocument = buildTransientAppDocument(telegramDocument,persistentBinaryContent);
                return appDocumentRepository.save(transientAppDocument);

            }else {
                throw new UploadFileException("проблема при сохранении документов " + response);
            }
        }catch (JSONException e){
            throw new UploadFileException("Проблема при обработке JSONObject " + e.getMessage());       //todo написать обработчик исключений
        }

    }

    private byte[] downloadFile(String filePath) {
        String fullURI = fileStorageUri
                .replace("{token}", token)
                .replace("{file_path}",filePath);
        URL urlObj = null;
        try(InputStream is = urlObj.openStream()) {
            return is.readAllBytes();

        } catch(IOException e){
            throw new UploadFileException("ошибка при загрузке файла "+ fullURI +" \n "+ e.getMessage());
        }

    }

    private ResponseEntity<String> getFilePath(String fileId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(httpHeaders);

        return restTemplate.exchange(
                fileInfoUri,
                HttpMethod.GET,
                request,
                String.class,
                token,
                fileId);
    }

    private AppDocument buildTransientAppDocument(Document telegramDocument, BinaryContent persistentBinaryContent) {
        return AppDocument.builder()
                .telegramFileId(telegramDocument.getFileId())
                .binaryContent(persistentBinaryContent)
                .docName(telegramDocument.getFileName())
                .fileSize(telegramDocument.getFileSize())
                .mimeType(telegramDocument.getMimeType())
                .build();
    }
}
