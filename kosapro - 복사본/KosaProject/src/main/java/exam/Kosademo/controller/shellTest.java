package exam.Kosademo.controller;


import exam.Kosademo.service.ScriptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api")
public class shellTest {

    @Autowired
    private ScriptService scriptService;

    // HTML 템플릿 페이지를 반환
    @GetMapping("/shellTest")
    public String showShellTestPage(Model model) {
        model.addAttribute("result", "Click the button to execute the script.");
        return "shellTest"; // resources/templates/shellTest.html
    }

    // 스크립트 실행 및 결과 반환
    @GetMapping("/execute-script")
    public String executeScript(Model model) {
        try {
            scriptService.runScriptAndUploadToS3();
            model.addAttribute("result", "Script executed and file uploaded to S3 successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("result", "An error occurred: " + e.getMessage());
        }
        return "shellTest";
    }
}
