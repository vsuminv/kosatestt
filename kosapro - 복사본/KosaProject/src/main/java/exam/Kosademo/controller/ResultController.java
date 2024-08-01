package exam.Kosademo.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api")
public class ResultController {

    private final ClassPathResource resource = new ClassPathResource("result2.json");


    // serverInfo 여러개 있을 때
//    @GetMapping("/result1")
//    public String getResultDetail(Model model) {
//        try {
//            // JSON 파일을 읽어와서 Map으로 변환
//            ObjectMapper objectMapper = new ObjectMapper();
//            Map<String, Object> jsonMap = objectMapper.readValue(
//                    resource.getInputStream(),
//                    new TypeReference<Map<String, Object>>() {});
//
//            // JSON 객체에서 'servers' 배열을 추출
//            List<Map<String, Object>> serverInfoList = (List<Map<String, Object>>) jsonMap.get("Server_Info");
//
//            // 모델에 serverInfoList 추가
//            model.addAttribute("serverInfoList", serverInfoList);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return "resultTarget";
//    }

    @GetMapping("/result1")
    public String getResultTarget(Model model) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> jsonMap = objectMapper.readValue(resource.getInputStream(), new TypeReference<Map<String, Object>>() {});

            Map<String, Object> serverInfoMap = (Map<String, Object>) jsonMap.get("Server_Info");
            model.addAttribute("serverInfo", serverInfoMap);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "resultTarget";
    }

    @GetMapping("/result5")
    public String getResultDetail(Model model) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> jsonMap = objectMapper.readValue(resource.getInputStream(), new TypeReference<Map<String, Object>>() {});

            Map<String, Object> serverInfoMap = (Map<String, Object>) jsonMap.get("Server_Info");
            model.addAttribute("serverInfo", serverInfoMap);

            List<Map<String, Object>> checkResults = (List<Map<String, Object>>) jsonMap.get("Check_Results");
            for (Map<String, Object> item : checkResults) {
                item.put("Server_Info", serverInfoMap);
            }
            model.addAttribute("checkResults", checkResults);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "resultDetail";
    }
}