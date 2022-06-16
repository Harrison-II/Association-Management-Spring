package com.spring.ams.controllers;


import com.spring.ams.entity.User;
import com.spring.ams.service.EventService;
import com.spring.ams.service.UserService;
import com.spring.ams.service.impl.ExportUserList;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
public class UserController {

    private final UserService userService;
    private final EventService eventService;

    private PasswordEncoder encoder;

    public UserController(UserService userService, EventService eventService){
        this.userService = userService;
        this.eventService = eventService;
    }

    @GetMapping("/index")
    public String home(HttpSession session, Model model){

        session.setAttribute("userCount", userService.getAllUsers().size());
        session.setAttribute("eventCount", eventService.getAllEvents().size());

        model.addAttribute("users",userService.getAllUsers());
        return "index";
    }

    @GetMapping("/members")
    public String listUsers(Model model){
        model.addAttribute("users", userService.getAllUsers());
        return "members";
    }

    @GetMapping("/members/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model){
        model.addAttribute("user", userService.getUserById(id));
        return "edit_user";
    }

    @PostMapping("/members/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute("user") User user, Model model){
        //get user from db by id
        User existingUser = userService.getUserById(id);

        existingUser.setId(id);
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setYearOfStudy(user.getYearOfStudy());
        existingUser.setComputerNumber(user.getComputerNumber());
        existingUser.setEmail(user.getEmail());
        existingUser.setPassword(user.getPassword());
        existingUser.setEnabled(user.getEnabled());

        userService.updateUser(existingUser);
        return "redirect:/members";
    }

    @GetMapping("/members/{id}")
    public String deleteUser(@PathVariable Long id){
        userService.deleteUserById(id);
        return "redirect:/members";
    }

    @GetMapping("/members/new")
    public String createUserForm(Model model){
        User user = new User();
        userService.setDefaultRole(user);
        model.addAttribute("user", user);

        return "create_user";
    }

    @PostMapping("/members")
    public String saveUser(@ModelAttribute("user") User user) {
//        userService.setDefaultRole(user);
        userService.saveUser(user);
        return "redirect:/members";
    }

    @GetMapping("/members/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=users_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        List<User> exportUsers = userService.listAll();

        ExportUserList excelExporter = new ExportUserList(exportUsers);

        excelExporter.export(response);
    }

    @GetMapping("/certificate/print")
    public void generateCertificate(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy:hh:mm:ss");
        String currentDateTime = dateFormat.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=certificate_" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);

        this.userService.printCertificate(response);
    }

    @GetMapping("/settings")
    public String settingsPage(){
        return "settings";
    }

    @GetMapping("/account")
    public String account(){
        return "account";
    }
}
