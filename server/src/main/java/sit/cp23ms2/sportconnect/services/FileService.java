package sit.cp23ms2.sportconnect.services;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import sit.cp23ms2.sportconnect.dtos.file.CreateFileDto;
import sit.cp23ms2.sportconnect.dtos.file.FileDto;
import sit.cp23ms2.sportconnect.dtos.file.FilePropertiesDto;
import sit.cp23ms2.sportconnect.dtos.file.PageFileDto;

import sit.cp23ms2.sportconnect.dtos.request.RequestDto;
import sit.cp23ms2.sportconnect.entities.Activity;

import sit.cp23ms2.sportconnect.entities.User;
import sit.cp23ms2.sportconnect.exceptions.type.BadRequestException;
import sit.cp23ms2.sportconnect.exceptions.type.ForbiddenException;
import sit.cp23ms2.sportconnect.repositories.ActivityRepository;
import sit.cp23ms2.sportconnect.repositories.FileRepository;
import sit.cp23ms2.sportconnect.repositories.UserRepository;


import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.UUID;

@Service
public class FileService {

    @Autowired
    FileRepository repository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    ActivityRepository activityRepository;
    @Autowired
    UserRepository userRepository;

    public PageFileDto getFile(int pageNum, int pageSize, Integer activityId) {
        //Sort sort = Sort.by(Sort.Direction.DESC, sortBy);
        Pageable pageRequest = PageRequest.of(pageNum, pageSize);
        Page<sit.cp23ms2.sportconnect.entities.File> listFiles = repository.findAllFiles(pageRequest, activityId);
        PageFileDto pageFileDto = modelMapper.map(listFiles, PageFileDto.class);
        return pageFileDto;
    }

    public sit.cp23ms2.sportconnect.entities.File getById(Integer id) {
        //Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        sit.cp23ms2.sportconnect.entities.File file = repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "File: " + id + " Not Found"));
        return file;
    }

    public sit.cp23ms2.sportconnect.entities.File create(CreateFileDto newFile, HttpServletResponse response) throws IOException, BadRequestException {
        sit.cp23ms2.sportconnect.entities.File fileInfo = new sit.cp23ms2.sportconnect.entities.File();
        if(newFile.getUserId() != null) {
            User user = userRepository.findById(newFile.getUserId()).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "User ID: " + newFile.getUserId() + " Not Found"));
            fileInfo.setUserFile(user);
        }
        if(newFile.getActivityId() != null) {
            Activity activity = activityRepository.findById(newFile.getActivityId()).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Activity ID: " + newFile.getActivityId() + " Not Found"));
            fileInfo.setActivityFile(activity);
        }
        FilePropertiesDto filePropertiesDto = new FilePropertiesDto();
        try {
            filePropertiesDto = this.upload(newFile.getMultipartFile());
        } catch (Exception e) {
            e.getMessage();
        }

        fileInfo.setFileName(filePropertiesDto.getFileName());
        fileInfo.setFileUrl(filePropertiesDto.getFileUrl());
        fileInfo.setFileSize(filePropertiesDto.getFileSize());
        try {
            return repository.saveAndFlush(fileInfo);
        } catch (Exception e) {
            String message = "There is something error when save File to the Database";
//            response.setContentType("application/json");
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            response.getOutputStream().println("{ \"error\": \"" + message + "\" }");
            BlobId blobId = BlobId.of("sportconnect-storage.appspot.com", fileInfo.getFileName());
            this.getStorage().delete(blobId);
            throw new BadRequestException("There is something error when save File to the Database");
        }

        //return repository.saveAndFlush(fileInfo);
    }

    public void delete(Integer id) throws IOException {
        sit.cp23ms2.sportconnect.entities.File file = this.getById(id);
        BlobId blobId = BlobId.of("sportconnect-storage.appspot.com", file.getFileName());
        Storage storage = this.getStorage();
        storage.delete(blobId);
        repository.deleteById(id);
    }

    private File convertToFile(MultipartFile multipartFile, String fileName) throws IOException {
        File tempFile = new File(fileName);
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(multipartFile.getBytes());
            fos.close();
        }
        return tempFile;
    }

    private FilePropertiesDto uploadFile(File file, String fileName) throws IOException {
        BlobId blobId = BlobId.of("sportconnect-storage.appspot.com", fileName);
        System.out.println(blobId);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("media").build();
        System.out.println(blobInfo);
        InputStream inputStream = FileService.class.getClassLoader().getResourceAsStream("firebase-spc.json");
        Credentials credentials = GoogleCredentials.fromStream(inputStream);
        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
        storage.create(blobInfo, Files.readAllBytes(file.toPath()));

        String DOWNLOAD_URL = "https://firebasestorage.googleapis.com/v0/b/sportconnect-storage.appspot.com/o/%s?alt=media";
        FilePropertiesDto filePropertiesDto = new FilePropertiesDto();
        filePropertiesDto.setFileUrl(String.format(DOWNLOAD_URL, URLEncoder.encode(fileName, StandardCharsets.UTF_8)));
        filePropertiesDto.setFileSize(Long.valueOf(storage.get(blobId).getSize()).intValue());
        return filePropertiesDto;
    }

    public FilePropertiesDto upload(MultipartFile multipartFile) throws IOException {

        String fileName = multipartFile.getOriginalFilename();                        // to get original file name
        fileName = UUID.randomUUID().toString().concat(this.getExtension(fileName));  // to generated random string values for file name.

        File file = this.convertToFile(multipartFile, fileName);                      // to convert multipartFile to File
        FilePropertiesDto filePropertiesDto = this.uploadFile(file, fileName);        // to get uploaded file link
        filePropertiesDto.setFileName(fileName);
        file.delete();
        return filePropertiesDto;

    }

    private Storage getStorage() throws IOException {
        InputStream inputStream = FileService.class.getClassLoader().getResourceAsStream("firebase-spc.json");
        Credentials credentials = GoogleCredentials.fromStream(inputStream);
        return StorageOptions.newBuilder().setCredentials(credentials).build().getService();
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
