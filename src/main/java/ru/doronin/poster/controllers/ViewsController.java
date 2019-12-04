package ru.doronin.poster.controllers;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.doronin.poster.references.SystemReferencesService;
import ru.doronin.poster.user.SystemUser;
import ru.doronin.poster.user.UserService;

@Profile("controllers")
@Controller
@AllArgsConstructor
public class ViewsController {
    private final SystemReferencesService referencesService;
    private final UserService userService;

    @RequestMapping("/")
    public String index(final Model model) {
        model.addAttribute("isDemo", referencesService.isDemo());
        model.addAttribute("authenticated", ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication().isAuthenticated()));
        model.addAttribute("user", userService.getCurrent().map(SystemUser::getId));
        return "index";
    }

    @RequestMapping("/login")
    public String loginPage(final Model model) {
        model.addAttribute("isDemo", referencesService.isDemo());
        model.addAttribute("authenticated", ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication().isAuthenticated()));
        return "login";
    }
}
