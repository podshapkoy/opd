package com.example.Web.controllers;

import com.example.Web.AdjectiveCount;
import com.example.Web.models.*;
import com.example.Web.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.*;


@Controller
public class ExpertController {

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    UserRepo userRepo;
    @Autowired
    OccupationRepository occupationRepository;
    @Autowired
    AdjectiveRepository adjectiveRepository;
    @Autowired
    ExpertsOpinionRepo expertsOpinionRepo;
    @Autowired
    TestsRepo testsRepo;
    @Autowired
    AllTestsResultRepo allTestsResultRepo;
    @Autowired
    AvailableTestsRepo availableTestsRepo;

    @GetMapping("/expert")
    public String startMode() {
        return "expertMode/expertsMode";
    }

    @GetMapping("/expert/send_test")
    public String sendTest(Model model) {
        String onlyUserS = "select new User(u.id, u.username,u.email,u.gender,u.age) from User u where u.id in (SELECT ur.user.id from UserRole ur where u.id=ur.user.id group by ur.user.id having count(*)=1)";
        List<User> onlyUser = entityManager.createQuery(onlyUserS, User.class)
                .getResultList();
        Iterable<User> allUsers = userRepo.findAll();
        model.addAttribute("allUsers", onlyUser);
        return "expertMode/user_list";
    }

    @GetMapping("/expert/grading_occupation")
    public String show(Model model, Authentication authentication) {
        int userId = userRepo.findByEmail(authentication.getName()).getId();
        String forChecked = "SELECT eo.occupation.id from ExpertsOpinion eo where eo.user.id = :userId";
        Query query = entityManager.createQuery(forChecked, Integer.class);
        query.setParameter("userId", userId);
        List<Integer> ratedOccupations = query.getResultList();
        model.addAttribute("ratedOccupations", ratedOccupations);
        Iterable<Occupation> allOccupations = occupationRepository.findAll();
        model.addAttribute("allOccupations", allOccupations);
        return "expertMode/occupations";
    }

    @GetMapping("/expert/grading_occupation/{id}")
    public String grade(Model model, @PathVariable(value = "id") int id) {
        Occupation occupation = occupationRepository.findById(id).get();
        model.addAttribute("occupation", occupation);
        Iterable<Adjective> adjectiveList = adjectiveRepository.findAll();
        model.addAttribute("adjectiveList", adjectiveList);
        model.addAttribute("result", new Result());
        String forAdjectiveIds = "SELECT new com.example.Web.AdjectiveCount(adj.name, COUNT(eo.adjective.id)) " +
                "FROM ExpertsOpinion eo inner join Adjective adj on eo.adjective.id=adj.id " +
                "WHERE eo.occupation.id = :occupationId " +
                "GROUP BY eo.adjective.id " +
                "ORDER BY COUNT(eo.adjective.id) DESC";
        String forAllAnswers = "SELECT COUNT(*) FROM ExpertsOpinion eo WHERE eo.occupation.id = :occupationId";
        Long allAnswersCount = entityManager.createQuery(forAllAnswers, Long.class)
                .setParameter("occupationId", id)
                .getSingleResult();
        List<AdjectiveCount> adjectiveIds = entityManager.createQuery(forAdjectiveIds, AdjectiveCount.class)
                .setParameter("occupationId", id)
                .setMaxResults(10)
                .getResultList();
        model.addAttribute("adjectiveIds", adjectiveIds);
        model.addAttribute("allAnswersCount", allAnswersCount);
        if (allAnswersCount == 0) {
            model.addAttribute("message", "Данную профессию еще не оценил ни один эксперт. Станьте первым!");
        }
        return "expertMode/grading_occupation";
    }

    @PostMapping("/expert/grading_occupation/{id}")
    public String gradeBD(@PathVariable(value = "id") int id, @ModelAttribute("result") Result result, Authentication authentication) {
        int[] selectedIds = result.getSelectedIds();
        Occupation occupation = occupationRepository.findById(id).get();
        User user = userRepo.findByEmail(authentication.getName());
        if (selectedIds != null || selectedIds.length != 0) {
            for (int adjId : selectedIds) {
                Adjective adjective = adjectiveRepository.findById(adjId).get();
                expertsOpinionRepo.save(new ExpertsOpinion(user, occupation, adjective));

            }
        }
        return "redirect:/expert/grading_occupation";

    }

    @GetMapping("/expert/send_test/user_{id}")
    public String userDetails(@PathVariable(value = "id") int id, Model model) {
        User user = userRepo.findById(id).get();
        model.addAttribute("user", user);
        String availableTestsS = "select new Tests(at.tests.id,t.name,t.description) from AvailableTests at inner join Tests t on at.tests.id=t.id where at.user.id= :id";
        List<Tests> availableTests = entityManager.createQuery(availableTestsS, Tests.class)
                .setParameter("id", id)
                .getResultList();
        String notAvailableTestsS = "select new Tests(t.id,t.name,t.description) from Tests t where t.id not in (select at.tests.id from AvailableTests at where at.user.id= :id)";
        List<Tests> notAvailableTests = entityManager.createQuery(notAvailableTestsS, Tests.class)
                .setParameter("id", id)
                .getResultList();
        model.addAttribute("availableTests", availableTests);
        model.addAttribute("notAvailableTests", notAvailableTests);
        System.out.println(availableTests);
        System.out.println(notAvailableTestsS);
        model.addAttribute("result", new Result());
        return "expertMode/tests_appointment";

    }

    @PostMapping("/expert/send_test/user_{id}")
    public String sentTest(@PathVariable(value = "id") int id, @ModelAttribute("result") Result result, Authentication authentication) {
        int[] selectedIds = result.getSelectedIds();
        for (int testId : selectedIds) {
            availableTestsRepo.save(new AvailableTests(userRepo.findById(id).get(), testsRepo.findById(testId).get()));
            System.out.println(new AvailableTests(userRepo.findById(id).get(), testsRepo.findById(testId).get()));
        }
        return "redirect:/expert/send_test";
    }

    @GetMapping("/expert/send_test/user_{user_id}/test_{test_id}_result")
    public String userResult(@PathVariable(value = "user_id") int user_id, Model model, @PathVariable(value = "test_id") int test_id) {
        List<Result> testResult = new ArrayList<>();
        String us = "select fin.id from FinishedSessionUserTest fin where fin.user.id=:id and fin.tests.id=:test_id ORDER BY fin.id DESC";
        List<Integer> finishedUserTest = entityManager.createQuery(us, Integer.class)
                .setParameter("id", user_id)
                .setParameter("test_id", test_id)
                .getResultList();

        for (int i : finishedUserTest) {
            String oneTestResultS = "select tes.result from AllTestsResult tes where tes.finishedSessionUserTest.id=:session_id";
            List<Double> oneTestResult = entityManager.createQuery(oneTestResultS, Double.class)
                    .setParameter("session_id", i)
                    .getResultList();
            String oneTestResultLabelS = "select tes.label from AllTestsResult tes where tes.finishedSessionUserTest.id=:session_id";
            List<String> oneTestResultLabel = entityManager.createQuery(oneTestResultLabelS, String.class)
                    .setParameter("session_id", i)
                    .getResultList();
            for(int j = 0; j<oneTestResult.size();j++){
                testResult.add(new Result(oneTestResult, oneTestResultLabel));
            }
        }
        Collections.reverse(testResult);
        model.addAttribute("oneTestResult", testResult);
        return "expertMode/user_result";
    }

//    @GetMapping("/expert/see_statistic")
//    public String statistic(Model model) {
//        List<String> finishedUserTest_10f = new ArrayList<>();
//        List<Integer> finishedUserTest_20f = new ArrayList<>();
//        List<Integer> finishedUserTest_30f = new ArrayList<>();
//        List<Integer> finishedUserTest_40f = new ArrayList<>();
//        List<Integer> finishedUserTest_50f = new ArrayList<>();
//        List<Integer> finishedUserTest_overf = new ArrayList<>();
//        String age_10f = "select allT.result_ms AS first_number from AllTestsResult allT inner join FinishedSessionUserTest fin on fin.id=allT.finishedSessionUserTest.id inner join User u on u.id=fin.user.id where (0<=u.age and u.age<=10 and u.gender='F')";
//        String age_20f = "select SUBSTRING_INDEX(allT.result_ms, '-', 1) AS first_number from AllTestsResult allT inner join FinishedSessionUserTest fin on fin.id=allT.finishedSessionUserTest.id inner join User u on u.id=fin.user.id where (11<=u.age and u.age<=20 and u.gender='F') GROUP BY allT.finishedSessionUserTest.id";
//        String age_30f = "select SUBSTRING_INDEX(allT.result_ms, '-', 1) AS first_number from AllTestsResult allT inner join FinishedSessionUserTest fin on fin.id=allT.finishedSessionUserTest.id inner join User u on u.id=fin.user.id where (21<=u.age and u.age<=30 and u.gender='F') GROUP BY allT.finishedSessionUserTest.id";
//        String age_40f = "select SUBSTRING_INDEX(allT.result_ms, '-', 1) AS first_number from AllTestsResult allT inner join FinishedSessionUserTest fin on fin.id=allT.finishedSessionUserTest.id inner join User u on u.id=fin.user.id where (31<=u.age and u.age<=40 and u.gender='F') GROUP BY allT.finishedSessionUserTest.id";
//        String age_50f = "select SUBSTRING_INDEX(allT.result_ms, '-', 1) AS first_number from AllTestsResult allT inner join FinishedSessionUserTest fin on fin.id=allT.finishedSessionUserTest.id inner join User u on u.id=fin.user.id where (41<=u.age and u.age<=50 and u.gender='F') GROUP BY allT.finishedSessionUserTest.id";
//        String age_overf = "select SUBSTRING_INDEX(allT.result_ms, '-', 1) AS first_number from AllTestsResult allT inner join FinishedSessionUserTest fin on fin.id=allT.finishedSessionUserTest.id inner join User u on u.id=fin.user.id where (u.age>50 and u.gender='F') GROUP BY allT.finishedSessionUserTest.id";
//        System.out.println(entityManager.createQuery(age_10f, List<>.class));
//        finishedUserTest_10f = entityManager.createQuery(age_10f, List.class)
//                .getResultList();
//        System.out.println(finishedUserTest_10f);
//        finishedUserTest_20f = entityManager.createQuery(age_20f, Integer.class)
//                .getResultList();
//        finishedUserTest_30f = entityManager.createQuery(age_30f, Integer.class)
//                .getResultList();
//        finishedUserTest_40f = entityManager.createQuery(age_40f, Integer.class)
//                .getResultList();
//        finishedUserTest_50f = entityManager.createQuery(age_50f, Integer.class)
//                .getResultList();
//        finishedUserTest_overf = entityManager.createQuery(age_overf, Integer.class)
//                .getResultList();
////        int sum_age_10f = Math.floorDiv(finishedUserTest_10f.stream().mapToInt(Integer::intValue).sum(),Math.max(1, finishedUserTest_10f.size()));
//        int sum_age_20f = Math.floorDiv(finishedUserTest_20f.stream().mapToInt(Integer::intValue).sum(),Math.max(1, finishedUserTest_20f.size()));
//        int sum_age_30f = Math.floorDiv(finishedUserTest_30f.stream().mapToInt(Integer::intValue).sum(),Math.max(1, finishedUserTest_30f.size()));
//        int sum_age_40f = Math.floorDiv(finishedUserTest_40f.stream().mapToInt(Integer::intValue).sum(),Math.max(1, finishedUserTest_40f.size()));
//        int sum_age_50f = Math.floorDiv(finishedUserTest_50f.stream().mapToInt(Integer::intValue).sum(),Math.max(1, finishedUserTest_50f.size()));
//        int sum_age_overf = Math.floorDiv(finishedUserTest_overf.stream().mapToInt(Integer::intValue).sum(),Math.max(1, finishedUserTest_overf.size()));
////        List<Integer> femaleResult = Arrays.asList(sum_age_10f, sum_age_20f, sum_age_30f, sum_age_40f, sum_age_50f, sum_age_overf);
////        model.addAttribute("femaleResult",femaleResult);
//
////        System.out.println(femaleResult);
//        List<Integer> finishedUserTest_10m= new ArrayList<>();
//        List<Integer> finishedUserTest_20m = new ArrayList<>();
//        List<Integer> finishedUserTest_30m = new ArrayList<>();
//        List<Integer> finishedUserTest_40m = new ArrayList<>();
//        List<Integer> finishedUserTest_50m = new ArrayList<>();
//        List<Integer> finishedUserTest_overm = new ArrayList<>();
//        String age_10m = "select SUBSTRING_INDEX(allT.result_ms, '-', 1) AS first_number from AllTestsResult allT inner join FinishedSessionUserTest fin on fin.id=allT.finishedSessionUserTest.id inner join User u on u.id=fin.user.id where (0<=u.age and u.age<=10 and u.gender='M') GROUP BY allT.finishedSessionUserTest.id";
//        String age_20m = "select SUBSTRING_INDEX(allT.result_ms, '-', 1) AS first_number from AllTestsResult allT inner join FinishedSessionUserTest fin on fin.id=allT.finishedSessionUserTest.id inner join User u on u.id=fin.user.id where (11<=u.age and u.age<=20 and u.gender='M') GROUP BY allT.finishedSessionUserTest.id";
//        String age_30m = "select SUBSTRING_INDEX(allT.result_ms, '-', 1) AS first_number from AllTestsResult allT inner join FinishedSessionUserTest fin on fin.id=allT.finishedSessionUserTest.id inner join User u on u.id=fin.user.id where (21<=u.age and u.age<=30 and u.gender='M') GROUP BY allT.finishedSessionUserTest.id";
//        String age_40m = "select SUBSTRING_INDEX(allT.result_ms, '-', 1) AS first_number from AllTestsResult allT inner join FinishedSessionUserTest fin on fin.id=allT.finishedSessionUserTest.id inner join User u on u.id=fin.user.id where (31<=u.age and u.age<=40 and u.gender='M') GROUP BY allT.finishedSessionUserTest.id";
//        String age_50m = "select SUBSTRING_INDEX(allT.result_ms, '-', 1) AS first_number from AllTestsResult allT inner join FinishedSessionUserTest fin on fin.id=allT.finishedSessionUserTest.id inner join User u on u.id=fin.user.id where (41<=u.age and u.age<=50 and u.gender='M') GROUP BY allT.finishedSessionUserTest.id";
//        String age_overm = "select SUBSTRING_INDEX(allT.result_ms, '-', 1) AS first_number from AllTestsResult allT inner join FinishedSessionUserTest fin on fin.id=allT.finishedSessionUserTest.id inner join User u on u.id=fin.user.id where (u.age>50 and u.gender='M') GROUP BY allT.finishedSessionUserTest.id";
//        finishedUserTest_10m = entityManager.createQuery(age_10m,  Integer.class)
//                .getResultList();
//        finishedUserTest_20m = entityManager.createQuery(age_20m, Integer.class)
//                .getResultList();
//        finishedUserTest_30m = entityManager.createQuery(age_30m, Integer.class)
//                .getResultList();
//        finishedUserTest_40m = entityManager.createQuery(age_40m, Integer.class)
//                .getResultList();
//        finishedUserTest_50m = entityManager.createQuery(age_50m, Integer.class)
//                .getResultList();
//        finishedUserTest_overm = entityManager.createQuery(age_overm, Integer.class)
//                .getResultList();
//        int sum_age_10m = Math.floorDiv(finishedUserTest_10m.stream().mapToInt(Integer::intValue).sum(),Math.max(1, finishedUserTest_10m.size()));
//        int sum_age_20m = Math.floorDiv(finishedUserTest_20m.stream().mapToInt(Integer::intValue).sum(),Math.max(1, finishedUserTest_20m.size()));
//        int sum_age_30m = Math.floorDiv(finishedUserTest_30m.stream().mapToInt(Integer::intValue).sum(),Math.max(1, finishedUserTest_30m.size()));
//        int sum_age_40m = Math.floorDiv(finishedUserTest_40m.stream().mapToInt(Integer::intValue).sum(),Math.max(1, finishedUserTest_40m.size()));
//        int sum_age_50m = Math.floorDiv(finishedUserTest_50m.stream().mapToInt(Integer::intValue).sum(),Math.max(1, finishedUserTest_50m.size()));
//        int sum_age_overm = Math.floorDiv(finishedUserTest_overm.stream().mapToInt(Integer::intValue).sum(),Math.max(1, finishedUserTest_overm.size()));
//        List<Integer> maleResult = Arrays.asList(sum_age_10m, sum_age_20m, sum_age_30m, sum_age_40m, sum_age_50f, sum_age_overf);
//        model.addAttribute("maleResult",maleResult);
//        System.out.println(maleResult);
//        return "expertMode/tests_statistics";
//    }
}
