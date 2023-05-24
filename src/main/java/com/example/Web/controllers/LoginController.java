package com.example.Web.controllers;

import com.example.Web.models.Role;
import com.example.Web.models.User;
import com.example.Web.models.UserRole;
import com.example.Web.repo.UserRepo;
import com.example.Web.repo.UserRoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
public class LoginController {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private UserRoleRepo userRoleRepo;

    @GetMapping("/registration")
    public String registaration(Authentication authentication, Model model) {
        boolean reg = authentication != null && authentication.isAuthenticated();
        model.addAttribute("reg", reg);
        return "registration";
    }

    @PostMapping("/registration")
    public String registration(@ModelAttribute("userForm") User userForm, BindingResult bindingResult, Model model, Map<String, Object> model1) {
        // Проверка, что email не пустой и не зарегистрирован в базе данных
        if (userForm.getEmail() == null || userRepo.findByEmail(userForm.getEmail()) != null) {
            bindingResult.rejectValue("email", "error.userForm", "This email is already registered");
        }

        // Если есть ошибки, вернуть форму регистрации с ошибками
        if (bindingResult.hasErrors()) {
            model.addAttribute("message", "This email is already registered");
            model1.put("message", "This email is already registered");
            return "registration";
        }
        userRepo.save(userForm);
        userRoleRepo.save(new UserRole(userForm, Role.USER));

        // Перенаправление на страницу успешной регистрации
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return "redirect:/login?logout";
    }

}



