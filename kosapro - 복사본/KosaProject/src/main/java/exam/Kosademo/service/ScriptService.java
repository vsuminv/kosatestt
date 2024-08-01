package exam.Kosademo.service;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;

@Service
public class ScriptService {

    private final S3Client s3Client;
    private final String bucketName;

    public ScriptService(S3Client s3Client, @Value("${aws.s3.bucket.name}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    public void runScriptAndUploadToS3() throws IOException, InterruptedException {
        // 임시 파일을 만들기 위한 파일 경로
        File tempScriptFile = File.createTempFile("CentOS6", ".sh");
        tempScriptFile.deleteOnExit();

        System.out.println("Temporary script file created at: " + tempScriptFile.getAbsolutePath());

        // 클래스패스에서 쉘 스크립트 읽기
        try (InputStream inputStream = new ClassPathResource("CentOS6.sh").getInputStream();
             FileOutputStream outputStream = new FileOutputStream(tempScriptFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        System.out.println("Shell script file read successfully.");

        // 쉘 스크립트에 실행 권한 추가
        tempScriptFile.setExecutable(true);

        // 쉘 스크립트 실행
        ProcessBuilder processBuilder = new ProcessBuilder("sh", tempScriptFile.getAbsolutePath());
       // ProcessBuilder processBuilder = new ProcessBuilder("C:\\Program Files\\Git\\bin\\bash.exe", tempScriptFile.getAbsolutePath());

        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        // 프로세스 출력 로그 읽기
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            System.out.println("Script execution output:");
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }

        int exitCode = process.waitFor();
        System.out.println("Script execution finished with exit code: " + exitCode);

        // JSON 파일 경로
        File jsonFile = new File("output.json");
        if (jsonFile.exists()) {
            System.out.println("JSON file found at: " + jsonFile.getAbsolutePath());

            // S3로 JSON 파일 업로드
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key("output.json")
                            .build(),
                    jsonFile.toPath());

            System.out.println("JSON file uploaded to S3 bucket: " + bucketName);
        } else {
            System.out.println("JSON file not found at: " + jsonFile.getAbsolutePath());
            throw new IOException("JSON file not found.");
        }
    }
}
