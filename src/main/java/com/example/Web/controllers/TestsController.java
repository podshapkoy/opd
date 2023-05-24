package com.example.Web.controllers;

import com.example.Web.models.*;
import com.example.Web.repo.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Controller
public class TestsController {
    @Autowired
    TestsRepo testsRepo;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    FinishedSessionUserTestRepo finishedSessionUserTestRepo;
    @Autowired
    UserRepo userRepo;
    @Autowired
    AllTestsResultRepo allTestsResultRepo;

    @GetMapping("/tests")
    public String tests(Model model, Authentication authentication) {
        int userId = userRepo.findByEmail(authentication.getName()).getId();
        String availableTestsS = "select new Tests(at.tests.id,t.name,t.description) from AvailableTests at inner join Tests t on at.tests.id=t.id where at.user.id= :id order by at.tests.id";
        List<Tests> availableTests = entityManager.createQuery(availableTestsS, Tests.class)
                .setParameter("id", userId)
                .getResultList();
        model.addAttribute("availableTests", availableTests);
        boolean test = authentication != null && authentication.isAuthenticated();
        model.addAttribute("test", test);
        return "Tests";
    }

    @GetMapping("/tests/test_{text}")
    public String test(@PathVariable(value = "text") String text) {
        return ("lab_2/test_" + text);
    }

    @GetMapping("/tests/test_{text}/test_result")
    public String res(@PathVariable(value = "text") int text,Model model) {
        model.addAttribute("testId", text);
        return "test_result";
    }

    @PostMapping("/tests/test_{text}/test_result")
    public String handleTestResult(@RequestParam("answersList") String answersSt, @RequestParam("labelsList") String labelsSt, @PathVariable(value = "text") String text, Authentication authentication) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        List<Double> answers = mapper.readValue(answersSt, new TypeReference<List<Double>>() {
        });
        List<String> labels = mapper.readValue(labelsSt, new TypeReference<List<String>>() {
        });
        FinishedSessionUserTest finishedSessionUserTest = new FinishedSessionUserTest(userRepo.findByEmail(authentication.getName()), testsRepo.findById(Integer.parseInt(text)).get());
        System.out.println(finishedSessionUserTest);
        finishedSessionUserTestRepo.save(finishedSessionUserTest);
        for (int i = 0; i<answers.size();i++) {
            allTestsResultRepo.save(new AllTestsResult(finishedSessionUserTest, answers.get(i), labels.get(i)));
        }
        return "redirect:/tests";
    }

}
